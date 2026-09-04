package com.example.ourbook.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.ourbook.Chat.DTO.ChatMessageDTO;
import com.example.ourbook.Chat.DTO.ChatRoomUserDTO;
import com.example.ourbook.Chat.DTO.DefaultDTO;
import com.example.ourbook.Chat.DTO.JsonConverter;
import com.example.ourbook.Chat.DTO.Test.ChatMessageTest;
import com.example.ourbook.Chat.Socket.MySocketManager;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.ChatEvent;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ChatService;
import com.example.ourbook.DataTool.UserSharedHelper;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.MainLoginActivity;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityChatRoomBinding;
import com.google.android.gms.common.util.CollectionUtils;
import com.squareup.moshi.Moshi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomActivity extends AppCompatActivity implements MySocketManager.SendMessageListener , MySocketManager.MessageListener , MySocketManager.ChatRoomMessageListener , MySocketManager.RefreshChatRoomUser{

    // https://stickode.tistory.com/173 채팅 recyclerView
    private ActivityChatRoomBinding binding ;
    private RetrofitGenerator retrofitGenerator;
    private ChatService chatService;

    private final String TAG = " ChatRoomActivity "+ Constants.AddTAG;
    private int wid;
    private String name;
    private String introduce;
    private String password;
    public Moshi moshi = new Moshi.Builder().build();
    public Socket socketTest;
    private boolean isNotify = true;

    public MySocketManager mySocketManager;
    public JsonConverter jsonConverter = new JsonConverter();
//    private static final String SERVER_IP = "10.0.2.2";
    //    private static final int SERVER_PORT = 6080;

//    private ReceiveMessageThread receiveMessageThread;
    private Handler handler = new Handler();
    private ChatAdapter chatAdapter ;
    private List<ChatMessageDTO> messageList = new ArrayList<>();
    private List<ChatRoomUserDTO> roomUserList = new ArrayList<>();


    private RecyclerView recyclerView;


    private UserSingletone userSingletone;
    private Integer chatRoomID;
    private Integer chatRoomUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        retrofitGenerator = new RetrofitGenerator();
        chatService = retrofitGenerator.init_user_retrofit(ChatService.class);
        chatRoomID = getIntent().getIntExtra(Constants.INTENT_CHATROOM_ID,-1);

//        String stringExtra = getIntent().getStringExtra(Constants.INTENT_CHATROOM_USER_ID);
//        if (stringExtra == null){
//            finish();
//            throw new java.util.NoSuchElementException("No value present");
//        }
//        String tempCRUI = stringExtra;

        chatRoomUserID = getIntent().getIntExtra(Constants.INTENT_CHATROOM_USER_ID,-1);
        Toast.makeText(getApplicationContext()," chatRoomID " + chatRoomID,Toast.LENGTH_SHORT ).show();

        if(chatRoomID == -1 ){
            Log.d(TAG, "onCreate: error cRid , chat room id -1 ");
            finish();
        }
        Log.d(TAG, "onCreate: char_room_user_id "+ chatRoomUserID);

        if(chatRoomUserID == -1 || chatRoomUserID < 1){
//            Log.d(TAG, "onCreate: char_room_user_id "+ chatRoomUserID);
            Log.d(TAG, "onCreate: intent 전달 오류 ");
            finish();
        }
        // sharedPreference 사용
        UserSharedHelper.init(getApplicationContext());
        // chatRoom Id 알림 설정 Load;
         isNotify =  UserSharedHelper.getChatRoomNotify(chatRoomID);

         // 메모 하기 TODO 테마가 noactionBar 일때 호출이 onCreateOptionsMenu() 안되는데 이 설정으로 작동함
        setSupportActionBar(binding.mtoolBarChatRoom);

        // toolbar 뒤로가기 , main 화면으로 이동
        binding.mtoolBarChatRoom.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ChatRoomActivity.this, MainLoginActivity.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
            // home 화면에서  chat tab을 선택하기 위해서 사용
            intent.putExtra("option","chat");
            startActivity(intent);
            finish();
        });
        // toolbar menu 초기화

//        binding.mtoolBarChatRoom.menu
//        onCreateOptionsMenu();

        // toolbar menu 클릭 설정
        binding.mtoolBarChatRoom.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.item_chatRoom_notify){
//                return true;

                isNotify = !isNotify;
                updateNotifyIcon(item);
                UserSharedHelper.setChatRoomNotify(chatRoomID,isNotify);
                Log.d(TAG, "onCreate: setOnMenuItemClickListener isNotify"+isNotify);
                return true;
            } else if (itemId == R.id.item_chatRoom_exit) {
                return true;
            } else if (itemId == R.id.item_chatRoom_setting) {
                return true;
            }else {
                return false;
            }

        });

        //
        UserSharedHelper.init(getApplicationContext());

        userSingletone = UserSingletone.getMyUser();
        String Token = UserSharedHelper.read(UserSharedHelper.KEY_LOGIN_TOKEN,null);
        userSingletone.setUserJWT(Token);

        recyclerView = binding.recyclerViewChatRoom;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messageList,chatRoomUserID);
        recyclerView.setAdapter(chatAdapter);
        //retrofit 기존 채팅 메세지 정보 load
        LoadChatList();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 채팅방 성공 이후 SocketManager( 소켓 연결 관리 객체 ) 의 전역 인스턴스를 호출
                mySocketManager = MySocketManager.getInstance();
                mySocketManager.checkSocket();
                mySocketManager.setMchatRoomMessageListener(ChatRoomActivity.this);
                updateChatId(0);
                refreshChatId();
            }
        }).start();
        /* test depend*/
        new Thread(new Runnable() {
            @Override
            public void run() {
//                socketTest = MySocket.getSocket();
////                    socketTest = new Socket(SERVER_IP, SERVER_PORT); // 비동기적으로 소켓 연결
//                DefaultDTO defaultDTO = new DefaultDTO("init");
//                defaultDTO.setJwtToken(userSingletone.getUserJWT());
//
//                String jsonString = jsonConverter.getDefaultJsonAdapter().toJson(defaultDTO);
//
//                new SendMessageThread(socketTest, jsonString,handler).start();
//
//                receiveMessageThread = new ReceiveMessageThread(socketTest,handler,chatAdapter,messageList);
//                new Thread(receiveMessageThread).start();
                // 소켓을 사용한 네트워크 작업...
            }
        }).start();

        // 채팅 입력을 할 때 메세지를 입력하지 않은 경우 메세지 전송이 비활성화 되게 설정, trim, isEmpty 로 /n,s 까지 확인
        binding.chatInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.buttonChatSend.setActivated(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()){
                    binding.buttonChatSend.setActivated(true);
                }else {
                    binding.buttonChatSend.setActivated(false);
                };
            }
        });


        binding.buttonChatSend.setOnClickListener(v -> {
            if(!binding.buttonChatSend.isActivated()){
                Toast.makeText(this,"채팅 메세지를 먼저 입력하세요",Toast.LENGTH_SHORT).show();
                return;
            }

            String inputMessage = String.valueOf(binding.chatInput.getText());
//            socketTest.senText(message);
            ChatMessageDTO chatMessage = new ChatMessageDTO();
            /*ChatMessageDTO init */
            chatMessage.content = inputMessage.trim();
            chatMessage.chatRoomId = chatRoomID;
            chatMessage.chatRoomUserId = chatRoomUserID;
            chatMessage.newDate = new java.util.Date().getTime();
            chatMessage.userName = userSingletone.getUserName();
            chatMessage.option = 0;

            /*Default DTO Init*/
            DefaultDTO defaultDTO = new DefaultDTO("send");

            defaultDTO.setChatMessage(chatMessage);
            String jsonStrings = jsonConverter.getDefaultJsonAdapter().toJson(defaultDTO);

            // 수정함  socket 관리 클래스를 따로 만들어서
//            new SendMessageThread(socketTest, jsonStrings, handler).start();
            if(mySocketManager != null){
                mySocketManager.sendMessage(jsonStrings,ChatRoomActivity.this);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateChatId(0);
                        }
                    });
                }
            }).start();
        });


    }
    /*기능 6 ) 채팅메세지 수신 이후 사용자가 마지막으로 읽은 채팅 id 를 갱신하기 위해서 요청 이후 chat start, last id 를 갱신  */
    public void refreshChatId(){
        Log.d(TAG, "refreshChatId: refresh 실행");
        String jsonStrings = "";
        ChatRoomUserDTO chatRoomUserDTO = new ChatRoomUserDTO();
//        Integer chatId = messageList.get(0).chatId;
        // 마지막으로 읽은 채팅 정보 . 시작 채팅은 일단 생략
//        chatRoomUserDTO.lastReadChatId  = chatId;
        chatRoomUserDTO.uid = userSingletone.getUid();
        chatRoomUserDTO.chatRoomUserId = chatRoomUserID;
        chatRoomUserDTO.chatRoomId = chatRoomID;

        DefaultDTO defaultDTO = new DefaultDTO("refresh_chat_id");
        defaultDTO.setJwtToken(userSingletone.getUserJWT()); // 필요 없을 수 있을 것 같음 처음 인증에 사용되고 인증이 안되면 socket 연결 오류 처리하기 때문에

        defaultDTO.setChatRoomUserDTO(chatRoomUserDTO);

        jsonStrings = jsonConverter.getDefaultJsonAdapter().toJson(defaultDTO);
        if(mySocketManager != null){
            mySocketManager.sendMessage(jsonStrings,ChatRoomActivity.this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_in_chatroom, menu);
            Log.d(TAG, "onCreateOptionsMenu: is notify? "+ isNotify);

            // 각 메뉴 아이템에 대한 아이콘을 설정합니다.
            MenuItem itemNotify = menu.findItem(R.id.item_chatRoom_notify);
            itemNotify.setIcon(isNotify ? R.drawable.bell_regular : R.drawable.bell_slash_regular);

            return true;
        }
    public void updateNotifyIcon(MenuItem menu){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "updateNotifyIcon run: update "+ isNotify);
                    menu.setIcon(isNotify ? R.drawable.bell_regular : R.drawable.bell_slash_regular);
                }
            });

    }




    /**
        기능 ) 사용자가 채팅을 수신하고 나서 사용자의 마지막 읽은 채팅 정보를 업데이트 하기위해서 전송 필수
        , chatRoomId , chatRoomUserId , chatId
        mchatId 가 0 인 경우 listChatMessage 의 마지막 chat id 를 불러온다.
    **/
    public void updateChatId(Integer mchatId){
        String jsonStrings = "";
        ChatRoomUserDTO chatRoomUserDTO = new ChatRoomUserDTO();
        if(messageList.isEmpty()){
            Log.d(TAG, "updateChatId: messageList.isEmpty()");
            return;
        }
        Integer chatId;
        if( mchatId == 0) {
             chatId = messageList.get(messageList.size() - 1).chatId;
        }else {
             chatId = mchatId;
        }
        // 마지막으로 읽은 채팅 정보 . 시작 채팅은 일단 생략
        chatRoomUserDTO.lastReadChatId  = chatId;
        chatRoomUserDTO.uid = userSingletone.getUid();
        chatRoomUserDTO.chatRoomUserId = chatRoomUserID;
        chatRoomUserDTO.chatRoomId = chatRoomID;
        DefaultDTO defaultDTO = new DefaultDTO("update_chat_id");
        defaultDTO.setChatRoomUserDTO(chatRoomUserDTO);
        jsonStrings = jsonConverter.getDefaultJsonAdapter().toJson(defaultDTO);
        Log.d(TAG, "updateChatId: update json String "+ jsonStrings);
        if(mySocketManager != null){
            mySocketManager.sendMessage(jsonStrings,ChatRoomActivity.this);
        }

    }

    @Override
    public void sendMessageListen(String message) {
        Log.d(TAG, "sendMessageListen: message : " + message);
    }

    @Override
    public void listenMessage(String message) {
        // not use
        Log.d(TAG, "listenMessage: message" + message);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenChatEvent(ChatEvent chatEvent){
        Log.d(TAG, "listerChatEvent: methodl json " + chatEvent.getMessage());
    };

    @Override
    protected void onStart() {
        super.onStart();
        // 임시
        UserSharedHelper.write("CHATON",true);
        //
        // EventBus 등록
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserSharedHelper.write("CHATON",false);

        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySocketManager.removeMchatRoomMessageListener(this);
        UserSharedHelper.write(UserSharedHelper.KEY_APP_ON,false);
        UserSharedHelper.write("CHATON",false);


    }

    @SuppressLint("NewApi") // ????
    @Override
    public void listenRoomMessage(String message) {
        /* 채팅 메세지의 DATA 확인 */
        Log.d(TAG, "listenRoomMessage: message in activity" + message);
        
        if(message == null || message.isBlank()){
            Log.d(TAG, "listenRoomMessage: message was null or blanked");
            return;
        }
        Optional<DefaultDTO> optDefaultDTO;
        DefaultDTO defaultDTO;
        ChatMessageDTO chatMessageDTO;
        try {UserSharedHelper.write(UserSharedHelper.KEY_APP_ON,false);

            optDefaultDTO = Optional.ofNullable(jsonConverter.getDefaultJsonAdapter().fromJson(message));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(optDefaultDTO.isEmpty()){
            Log.d(TAG, "listenRoomMessage: optDefaultDTO empty , null");
            return;
        }

        
        defaultDTO = optDefaultDTO.get();
        if( defaultDTO.getMethod().contentEquals("send")){
            Log.d(TAG, "listenRoomMessage: method == send ");
            chatMessageDTO = defaultDTO.getChatMessage();

        if(chatMessageDTO != null && chatMessageDTO.content != null){
            // 새로운 메세지인 경우
            // 메세지 수신 이후 읽음 처리를 위해서 update
//            updateChatId();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!Objects.equals(chatMessageDTO.chatRoomId, chatRoomID)){
                        return;
                    }

                    Toast.makeText(ChatRoomActivity.this,chatMessageDTO.content,Toast.LENGTH_SHORT).show();
                    chatAdapter.addChat(chatMessageDTO);
                    recyclerView.scrollToPosition(chatAdapter.getItemCount()-1);


                    // 본인인 입력한 채팅의 경우
                    if(chatMessageDTO.chatRoomUserId.equals(chatRoomUserID)){
                        binding.chatInput.setText("");
                    }

                    //update 읽기 정보
                    updateChatId(chatMessageDTO.chatId);
                    refreshChatId();

                }
            });
        }
        } else if ( defaultDTO.getMethod().contentEquals("refresh_chat_id") ) {
            // 새로운 메세지가 아니라 읽음 처리 확인을 위한 data 의 갱신인 경우
            if( CollectionUtils.isEmpty(defaultDTO.getListChatRoomUserDTOs()) ){
                Log.d(TAG, "listenRoomMessage: refresh_chat_id is empty");
                return;
            }
            Log.d(TAG, "listenRoomMessage: refresh_chat_id 실행");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<ChatRoomUserDTO> chatRoomUserDTOs = defaultDTO.getListChatRoomUserDTOs();
                    Log.d(TAG, "run: refresh_chat_id list 확인 "+ chatRoomUserDTOs.size());
                    if(chatRoomUserDTOs == null || chatRoomUserDTOs.isEmpty()){
                        return;
                    } else if (!chatRoomUserDTOs.get(0).chatRoomId.equals(chatRoomID)) {
                        Log.d(TAG, "run: not same Room ");
                        return;
                    }

                    chatAdapter.setRoomUserList(chatRoomUserDTOs);
//                    chatAdapter.notifyDataSetChanged();

                }
            });

        }

    }

    @Override
    public void refreshChatRoomUserData(String message) {

    }

////    class SendMessageThread extends Thread {
////        private Socket socket;
////        private String data;
////        private Handler mhandler;
////
////        public SendMessageThread(Socket socket, String data , Handler handler) {
////            this.socket = socket;
////            this.data = data;
////            this.mhandler = handler;
////        }
////        public void initSocket(int uid){
////
////        }
////
////        @Override
////        public void run() {
////            try {
////                OutputStream out = socket.getOutputStream();
////                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
////
////
//////                writer.write(jsons);
//////                writer.newLine(); // JSON 객체 끝에 줄바꿈 문자 추가
////                if(!socket.isConnected()){
////                    Log.d(TAG, "run: disconnected");
////                    Log.d(TAG, "run: message"+ data);
////                    return;
////                }
////                Log.d(TAG, "run: connected");
////                Log.d(TAG, "run: message"+ data);
////
////
////                writer.write(data);
////                writer.newLine(); // JSON 객체 끝에 줄바꿈 문자 추가
////                writer.flush();
////                if(mhandler == null){
////                    return;
////                }
////                mhandler.post(new Runnable() {
////                    @Override
////                    public void run() {
//////                        binding.chatInput.clearComposingText();
////
////                    }
////                });
////            } catch (IOException e) {
////                Log.e(TAG, "run: error out stream "+e.getMessage() );
////                e.printStackTrace();
////            }
////        }
////    }
//    class ReceiveMessageThread extends Thread {
//        private Socket socket;
//        private Handler handler; // UI 업데이트를 위한 핸들러
//        private ChatAdapter chatAdapter; // 채팅 어댑터
//        private List<ChatMessageDTO> messageList; // 메시지 목록
//
//        public ReceiveMessageThread(Socket socket, Handler handler, ChatAdapter chatAdapter, List<ChatMessageDTO> messageList) {
//            this.socket = socket;
//            this.handler = handler;
//            this.chatAdapter = chatAdapter;
//            this.messageList = messageList;
//        }
//
//        @Override
//        public void run() {
//            while (true) {
//            try {
//                    if (socket == null) {
//                        // 소켓이 null이면 작업을 중단합니다.
//                        Log.e(TAG, "run: null 1 ");
//                        return;
//                    }
//                    if (socket != null && socket.isConnected()) {
//                        Log.d(TAG, "run: socket not null");
//                        InputStream in = socket.getInputStream();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
//
////                        BufferedInputStream reader = new BufferedInputStream(in);
////                        DataInputStream dataInputStream = new DataInputStream(reader);
//                        String response ;
//                        while ((response = reader.readLine()) != null) {
////                            String response =  dataInputStream.readUTF();;
////                            String response =  reader.readLine();;
//                            if(response.isEmpty()){
//                                Log.d(TAG, "run: null response , readline");
//                                break;
//                            }
//                            Log.d(TAG, "run: 수신 not null " + response);
//                            final String message = response;
//                            String finalResponse = response;
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Log.d(TAG, "  수신 run handler : " + finalResponse);
//                                    // 여기서 message를 ChatMessage 객체로 변환하는 로직이 필요하면 추가
//                                    ChatMessageDTO chatMessageTest = null; // 예시
//                                    try {
//                                        if (finalResponse.length() < 3) {
//                                            return;
//                                        }
//                                        chatMessageTest = jsonConverter.getMessageJsonAdapter().fromJson(finalResponse);
//                                        Log.d(TAG, "run:  변환 성공 " + "chatMessage.message" + chatMessageTest.content + "chatMessage.userName " + chatMessageTest.userName);
//                                    } catch (IOException e) {
//                                        Log.e(TAG, "run: error parsing"+ e.getMessage() );
//                                        throw new RuntimeException(e);
//                                    }
////                                chatMessage.message = message;
//                                    messageList.add(chatMessageTest);
//                                    chatAdapter.notifyDataSetChanged();
//                                }
//                            });
//                        }
//                    } else {
//                        Log.d(TAG, "run: null read line 2 ");
//                    }
//                } catch(IOException e){
//                    Log.e(TAG, "run: error" + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    class SocketThread2 extends Thread{
//        String host; // 서버 IP
//        int port; // 전송 데이터
//
//        public SocketThread2(String host, int port){
//            this.host = host;
//            this.port = port;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            try {
//                Socket socket = new Socket(host, port); // 소켓 열어주기
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//        }
//    }

    public void LoadChatRoomUserList(){

    }
    public void LoadChatList(){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("chat_room_id",chatRoomID);
//        requestMap.put("","");
        Call<List<ChatMessageDTO>> getChatList = chatService.getChatDataList(userSingletone.getUserJWT(),requestMap);
        getChatList.enqueue(new Callback<List<ChatMessageDTO>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDTO>> call, Response<List<ChatMessageDTO>> response) {
                if(! response.isSuccessful()){
                    Log.e(TAG, "onResponse: is not success" );
                    return;
                }
                if(response.body().isEmpty()){
                    Log.e(TAG, "onResponse:  is empty" );
                    return;
                }
                List<ChatMessageDTO>  chatMessageDTOList = response.body();
                messageList = chatMessageDTOList;
                chatAdapter.setChatMessageDTOList(messageList);
                updateChatId(0);
                recyclerView.scrollToPosition(chatMessageDTOList.size()-1);

            }

            @Override
            public void onFailure(Call<List<ChatMessageDTO>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }
    public void initListen(){
        MySocketManager.ChatRoomMessageListener listener = new MySocketManager.ChatRoomMessageListener() {
            @Override
            public void listenRoomMessage(String message) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserSharedHelper.write(UserSharedHelper.KEY_APP_ON,true);
    }


}