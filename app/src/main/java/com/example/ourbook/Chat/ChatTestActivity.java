package com.example.ourbook.Chat;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourbook.Chat.DTO.Test.ChatMessageTest;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityChatTestBinding;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatTestActivity extends AppCompatActivity {
    public Moshi moshi = new Moshi.Builder().build();
    private final String TAG = "TEST CHAT " + Constants.AddTAG;

    public JsonAdapter<ChatMessageTest> jsonAdapter = moshi.adapter(ChatMessageTest.class);
    private ActivityChatTestBinding binding;

    String response; //서버 응답

    Handler handler = new Handler(); // 토스트를 띄우기 위한 메인스레드 핸들러 객체 생성
    RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    List<ChatMessageTest> messageList = new ArrayList<>();
    private Socket socketTest;
//    private static final String SERVER_IP = "203.0.113.10";
    private static final String SERVER_IP = "10.0.2.2";
//    private static final int SERVER_PORT = 6080;
    private static final int SERVER_PORT = 1234;
    ReceiveMessageThread receiveMessageThread;
    private UserSingletone userSingletone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityChatTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerView = binding.recyclerViewChatRoom;
        userSingletone = UserSingletone.getMyUser();
        // recyclerView init
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = ChatAdapter.chatAdaptersNotUse(messageList,userSingletone.getUserName());
        recyclerView.setAdapter(chatAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketTest = new Socket(SERVER_IP, SERVER_PORT); // 비동기적으로 소켓 연결
                    receiveMessageThread = new ReceiveMessageThread(socketTest,handler,chatAdapter,messageList);
                    new Thread(receiveMessageThread).start();
                    // 소켓을 사용한 네트워크 작업...
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        binding.buttonChatSend.setOnClickListener(v -> {
            String inputMessage = String.valueOf(binding.chatInput.getText());
//            socketTest.senText(message);
            ChatMessageTest chatMessageTest = new ChatMessageTest();
            chatMessageTest.message = inputMessage;
            chatMessageTest.timestamp = String.valueOf(LocalDateTime.now());
            chatMessageTest.userName = userSingletone.getUserName();
            String jsonString = jsonAdapter.toJson(chatMessageTest);

            new SendMessageThread(socketTest, jsonString,handler).start();
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
//        receiveMessageThread = new ReceiveMessageThread(socketTest,handler,chatAdapter,messageList);

//        new Thread( receiveMessageThread).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            socketTest.disConnect();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
    class SendMessageThread extends Thread {
        private Socket socket;
        private String data;
        private Handler mhandler;

        public SendMessageThread(Socket socket, String data , Handler handler) {
            this.socket = socket;
            this.data = data;
            this.mhandler = handler;
        }

        @Override
        public void run() {
            try {
                OutputStream out = socket.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.newLine(); // JSON 객체 끝에 줄바꿈 문자 추가
                writer.flush();
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        binding.chatInput.clearComposingText();
                        binding.chatInput.setText("");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class ReceiveMessageThread extends Thread {
        private Socket socket;
        private Handler handler; // UI 업데이트를 위한 핸들러
        private ChatAdapter chatAdapter; // 채팅 어댑터
        private List<ChatMessageTest> messageList; // 메시지 목록

        public ReceiveMessageThread(Socket socket, Handler handler, ChatAdapter chatAdapter, List<ChatMessageTest> messageList) {
            this.socket = socket;
            this.handler = handler;
            this.chatAdapter = chatAdapter;
            this.messageList = messageList;
        }

        @Override
        public void run() {
            try {
                if (socket == null) {
                    // 소켓이 null이면 작업을 중단합니다.
                    Log.e(TAG, "run: null 1 " );
                    return;
                }
                if (socket != null && socket.isConnected()) {
                    Log.d(TAG, "run:  not null");
                    InputStream in = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                    String response;
                    while ((response = reader.readLine()) != null) {
                        Log.d(TAG, "run: 수신 ");
                        final String message = response;
                        String finalResponse = response;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "  수신 run: " + finalResponse);
                                // 여기서 message를 ChatMessage 객체로 변환하는 로직이 필요하면 추가
                                ChatMessageTest chatMessageTest = null; // 예시
                                try {
                                    if( finalResponse.length() < 3){
                                        return;
                                    }
                                    chatMessageTest = jsonAdapter.fromJson(finalResponse);
                                    Log.d(TAG, "run:  변환 성공 " + "chatMessage.message" + chatMessageTest.message + "chatMessage.userName "+ chatMessageTest.userName);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
//                                chatMessage.message = message;
                                messageList.add(chatMessageTest);
                                chatAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }else {
                    Log.d(TAG, "run: null 2");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
                }
        }
    class SocketThread2 extends Thread{
        String host; // 서버 IP
        int port; // 전송 데이터

        public SocketThread2(String host, int port){
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            super.run();
            try {
                Socket socket = new Socket(host, port); // 소켓 열어주기

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }



    class SocketThread extends Thread{

        String host; // 서버 IP
        String data; // 전송 데이터

        public SocketThread(String host, String data){
            this.host = host;
            this.data = data;
        }

        @Override
        public void run() {

            try{
                int port = 6080; //포트 번호는 서버측과 똑같이

                Socket socket = new Socket(host, port); // 소켓 열어주기
                DataOutputStream outstream = new DataOutputStream(socket.getOutputStream()); //소켓의 출력 스트림 참조
                outstream.writeUTF(data); // 출력 스트림에 데이터 넣기
                outstream.flush(); // 출력

                DataInputStream inputStream = new DataInputStream(socket.getInputStream()); // 소켓의 입력 스트림 참조
                response = (String) inputStream.readUTF(); // 응답 가져오기
                ChatMessageTest chatMessageTest = jsonAdapter.fromJson(response);
                messageList.add(chatMessageTest);
                Log.d("TAG log chat ", "run: " +response);
                Log.d("TAG log chat ", " message run: " + chatMessageTest.message);
                /* 토스트로 서버측 응답 결과 띄워줄 러너블 객체 생성하여 메인스레드 핸들러로 전달 */
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        chatAdapter.addChat( null/* 임시 chatMessageTest*/);
                        Toast.makeText(ChatTestActivity.this, "서버 응답 : " + response, Toast.LENGTH_LONG).show();

                    }
                });

                socket.close(); // 소켓 해제

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}