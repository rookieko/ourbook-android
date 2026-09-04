package com.example.ourbook.Chat.Socket;

import android.util.Log;

import com.example.ourbook.Chat.ChatRoomActivity;
import com.example.ourbook.Chat.DTO.DefaultDTO;
import com.example.ourbook.Chat.DTO.JsonConverter;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.ChatEvent;
import com.example.ourbook.DataTool.UserSingletone;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MySocketManager {
    // socket 을 관리하기위해 만든 클래스 기본적으로 싱글톤 객체로 사용하여 구현하낟.
    private static final String TAG = "MySocketManager" + Constants.AddTAG;
    private static final String SERVER_IP = Constants.SERVER_IP;
    private static final int SERVER_PORT = Constants.SERVER_PORT;
    private Socket mySocket;
    private BufferedReader input;
    private UserSingletone userSingletone;
    private JsonConverter jsonConverter;
    private static MySocketManager instance;
    private static List<ChatRoomMessageListener> listChatMessageListener = new ArrayList<>();

    public synchronized void setMchatRoomMessageListener(ChatRoomMessageListener mchatRoomMessageListener) {
//        this.mchatRoomMessageListener = mchatRoomMessageListener;
        if(!listChatMessageListener.contains(mchatRoomMessageListener)){
            Log.d(TAG, "setMchatRoomMessageListener: register Listener");
        listChatMessageListener.add(mchatRoomMessageListener);
        }else {
            Log.e(TAG, "setMchatRoomMessageListener: 이미 listener 을 입력하였음" );
        }
    }
    public synchronized void removeMchatRoomMessageListener(ChatRoomMessageListener mchatRoomMessageListener){
        if(listChatMessageListener.contains(mchatRoomMessageListener)){
            Log.d(TAG, "removeMchatRoomMessageListener: removeListener");
            listChatMessageListener.remove(mchatRoomMessageListener);
        }else {
            Log.e(TAG, "removeMchatRoomMessageListener: not contain " );
        }
    }

    private static ChatRoomMessageListener mchatRoomMessageListener = null;
    private RefreshChatRoomUser refreshChatRoomUser = null;

    private MySocketManager() {
        try {
            mySocket = new Socket(SERVER_IP, SERVER_PORT);
            input = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            // 여기에 추가 초기화 코드를 넣을 수 있습니다.

            userSingletone = UserSingletone.getMyUser();
            jsonConverter = new JsonConverter();
            // 초기화 여기서 진행하는게 맞는지 확인이 필요함 ;
            initUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized MySocketManager getInstance() {
        if( instance == null){
            instance = new MySocketManager();

        }

        return instance;
    }

    public boolean checkSocket(){
        if(mySocket != null && mySocket.isConnected()){
            Log.d(TAG, "checkSocket: socket is connected");
            return true;
        }else if(mySocket == null){
            // socket 재연결 시도를 여기서 실행
            Log.e(TAG, "checkSocket: socket is null" );
            tryReconnect();
            return false;
        }else {
            Log.e(TAG, "checkSocket: is closed" );
            return false;
        }
    }
    public synchronized void tryReconnect(){
        if(mySocket == null || !mySocket.isConnected()){
            Log.e(TAG, "tryReconnect: reconnect 시도 " );
            instance = new MySocketManager();
        }
    }


    //출력 쓰기
    public void sendMessage(String message ,SendMessageListener sendMessageListener) {
        new Thread(() -> {
            try {
                if(mySocket!= null && mySocket.isConnected()){
                    OutputStream out = mySocket.getOutputStream();
                    PrintWriter writer = new PrintWriter(out, true);
                    writer.println(message);
                    // 메세지를 보내고 나서  ui 처리등을 위해서 listen 를 받는다 .
                    sendMessageListener.sendMessageListen(message);

                }
            } catch (IOException e) {
                Log.d(TAG, "sendMessage: IOException");
                e.printStackTrace();
            }
        }).start();
    }
    // socket 객체생성 성공 이후  서버 DB ->  채팅방 객체화 진행
    public void initUser(){
        DefaultDTO defaultDTO = new DefaultDTO("init");
        defaultDTO.setJwtToken(userSingletone.getUserJWT());
        defaultDTO.setFcmToken(userSingletone.getFcmToken());
        //                    InitUserDTO initUserDTO = new InitUserDTO();
//                    initUserDTO.setUid(userSingletone.getUid());
        String jsonString = jsonConverter.getDefaultJsonAdapter().toJson(defaultDTO);

        sendMessage(jsonString, message -> Log.d(TAG, "sendMessageListen: initUser " + message));
    }


    // 듣기
    public void startListeningForMessages(MessageListener listener) {
        // TODO 임시 MySocketManger 를 액티비티의 Thread 로 처리하는 코드를 ChatListenService 에서 하기위해 리팩토링 하는중
        //
        if(listener == null){
            Log.d(TAG, "startListeningForMessages: null listener");
            return;
        }

        //
        new Thread(() -> {
            while (mySocket != null && !mySocket.isClosed()) {
                try {
                    final String message = input.readLine();

                    //message null check
                    if (message != null) {
                        Log.d(TAG, "startListeningForMessages: 채팅 메세지 not null" + message);
                        listener.listenMessage(message);
                        if(listChatMessageListener != null && !listChatMessageListener.isEmpty()){
                            Log.d(TAG, "startListeningForMessages: 채팅방 메세지 리스너 not null,  message: "  + message);
                            int i = 0;
                            for (ChatRoomMessageListener mchatRoomMessagelistener :
                                    listChatMessageListener) {
                                Log.d(TAG, "startListeningForMessages: loop 실행"+ i);
                                mchatRoomMessagelistener.listenRoomMessage(message);
                                i++;
                            }

                        }else {
                            Log.d(TAG, "startListeningForMessages: null chatRoomListener 듣는 객체가 없은");
                        }

                        // chat id 새로고침을 위해서 사용
                        if(refreshChatRoomUser != null){
                            Log.d(TAG, "startListeningForMessages: refresh chat_id 메세지 리스너 not null,  message: "  + message);
                            refreshChatRoomUser.refreshChatRoomUserData(message);
                        }

                        EventBus.getDefault().post(new ChatEvent(message));
                    } else {
                        // 연결이 끊겼을 때 처리 -> 재연결 시도
                        break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }).start();
    }

    // 채팅방 내에서 듣기
    public interface MessageListener{
        void listenMessage(String message);
    }
    public interface ChatRoomMessageListener{


        void listenRoomMessage(String message);
    }
//    public synchronized void setChatRoomMessageListener(ChatRoomMessageListener chatRoomMessageListener){
//        mchatRoomMessageListener =  chatRoomMessageListener;
//    }
//    public synchronized void setNullChatRoomMessageListener(){
//        mchatRoomMessageListener = null;
//    }

    public interface SendMessageListener{
        void  sendMessageListen(String message);
    }
    public interface RefreshChatRoomUser{
        void refreshChatRoomUserData(String message);
    }


    // 종료 socket - stream

    public void closeConnection(){
        try {
            if(mySocket != null){
                mySocket.close();
            }
            if(input != null){
                input.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
