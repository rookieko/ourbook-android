package com.example.ourbook.Chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ourbook.Chat.DTO.ChatMessageDTO;
import com.example.ourbook.Chat.DTO.ChatRoomDTO;
import com.example.ourbook.Chat.DTO.ChatRoomDetailDTO;
import com.example.ourbook.Chat.DTO.DefaultDTO;
import com.example.ourbook.Chat.DTO.JsonConverter;
import com.example.ourbook.Chat.Socket.MySocket;
import com.example.ourbook.Chat.Socket.MySocketManager;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Recycle.Adapter.ChatRoomAdapter;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ChatService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.FragmentMainChatBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
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


public class MainChatFragment extends Fragment implements MySocketManager.MessageListener , MySocketManager.ChatRoomMessageListener {
    private FragmentMainChatBinding binding;
    private MaterialToolbar materialToolbar;
    private RetrofitGenerator retrofitGenerator;
    private ChatService chatService;
    private List<ChatRoomDTO> userChatRoomList = new ArrayList<>();
    DividerItemDecoration dividerItemDecoration ;
    private MySocketManager mySocketManager;
    private RecyclerView recyclerView;

    //TODO recycler View adapter 의 초기화 , viewType에 따라 달라지는지 확인

    private UserSingletone userSingletone = UserSingletone.getMyUser();
    private final JsonConverter jsonConverter = new JsonConverter();
    private ChatRoomAdapter chatRoomAdapter;
    private static final String TAG = "MainChatFragment " + Constants.AddTAG;
    private static final String UID_PARAM = "uid";
    private static final String JWT_PARAM = "jwt";
    private Map<String,Object> requestMap = new HashMap();



    private String uid_bundle;
    private String jwt_bundle;

    public MainChatFragment() {
        // Required empty public constructor
    }

    public static MainChatFragment newInstance(String muid, String mjwt) {
        MainChatFragment fragment = new MainChatFragment();
        Bundle args = new Bundle();
        args.putString(UID_PARAM, muid);
        args.putString(JWT_PARAM, mjwt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            uid_bundle = getArguments().getString(UID_PARAM);
            jwt_bundle = getArguments().getString(JWT_PARAM);
        }
        retrofitGenerator = new RetrofitGenerator();
        chatService = retrofitGenerator.init_user_retrofit(ChatService.class);
//        initSocket(); // 수정
        Log.d(TAG, "onCreate: fcm token in main "+ MyFirebaseMessagingService.getFcmToken());

    }

    @Override
    public void onResume() {
        super.onResume();
        if( jwt_bundle == null){
            Log.d(TAG, "onResume: jwt null , jwt : "+ jwt_bundle + " uid "+ uid_bundle);
            return;
        }
//        requestMap.put("tt","tt");
        setChatRoomData();


//        mySocketManager.checkSocket();

//        // socket 연결에 문제가 생겼을 때 재연결 시도
//        if( mySocketManager == null){
//            Log.d(TAG, "onResume: mySocketManager 재연결 시도");
//            initSocket();
//        }else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                mySocketManager.checkSocket();
//
//                }
//            }).start();
//            Log.d(TAG, "onResume: null 이 아님 ");
//        }

    }
    public void initSocket(){
        new Thread(new Runnable() {
            @Override
            public void run() {
//                socket = MySocket.getSocket();
                mySocketManager = MySocketManager.getInstance();
                mySocketManager.startListeningForMessages(MainChatFragment.this);
                mySocketManager.setMchatRoomMessageListener(MainChatFragment.this::listenRoomMessage);
            }
        }).start();
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentMainChatBinding.inflate(inflater, container, false);
        materialToolbar = binding.mtoolBarChatMain;

        materialToolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatTestActivity.class);
            startActivity(intent);
        });

        /*material toolbar 설정*/
        materialToolbar.setOnMenuItemClickListener(item ->
        {
            int itemId = item.getItemId();
            if(itemId == R.id.item_main_chat_search){
                /* 채팅방 검색 */
                Intent intent = new Intent(getActivity(), ChatRoomSearchActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.item_main_chat_create) {
                /* 채팅방 생성 */
                Intent intent = new Intent(getActivity(), ChatRoomCreateActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        recyclerView = binding.recyclerViewMainChat;

        initRecyclerView();
        return binding.getRoot();
    }
    private void initRecyclerView(){
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        chatRoomAdapter = new ChatRoomAdapter(userChatRoomList,getActivity(),ChatRoomAdapter.MAIN_TYPE);
        recyclerView.setAdapter(chatRoomAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    /* 처음 채팅방 정보 조회 */
    private void setChatRoomData(){
        Call<List<ChatRoomDTO>> chatRoomCalls = chatService.getUserChatRoomList(jwt_bundle , requestMap);
        chatRoomCalls.enqueue(new Callback<List<ChatRoomDTO>>() {
            @Override
            public void onResponse(Call<List<ChatRoomDTO>> call, Response<List<ChatRoomDTO>> response) {
                Log.d(TAG, "onResponse: 성공");
                if(!response.isSuccessful()){ return; }
                userChatRoomList = response.body();
                Log.d(TAG, "onResponse: 호출 성공 List parsing size =  "+ userChatRoomList.size());
                chatRoomAdapter.setRoomDataList(userChatRoomList);
                chatRoomAdapter.notifyDataSetChanged();
                // 채팅방 정보 설정

                // 채팅방 정보 로드 이후 socket 으로 chatRoomAdapter 정보( 마지막 채팅 정보 , 채팅방 유저수 , 마지막으로 읽은 채팅방 정보 ) 갱신 준비
                // socket 연결에 문제가 생겼을 때 재연결 시도
                if( mySocketManager == null){
                    Log.d(TAG, "onResume: mySocketManager 재연결 시도");
                    initSocket();

                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mySocketManager.checkSocket();


                        }
                    }).start();
                    Log.d(TAG, "onResume: null 이 아님 ");
                }

                // 채팅방 로드 이후 socket manger 에 listener 를 등록

            }

            @Override
            public void onFailure(Call<List<ChatRoomDTO>> call, Throwable t) {
                Log.d(TAG, "onFailure: error 발생 retrofit ");
            }
        });
    }

    private void setChatRoomDataDetail(long mchatRoomID , int chatRoomUserNumber){
        Log.d(TAG, "setChatRoomDataDetail: chatRoomId 확인 "+ mchatRoomID);
        String jwtKey = userSingletone.getUserJWT();
        Map< String,Object > requestMap = new HashMap<>();
        requestMap.put("chat_room_id", mchatRoomID);
        Call<ChatRoomDetailDTO> chatRoomDataDetail =  chatService.getChatRoomDataDetail(jwtKey,requestMap);
        chatRoomDataDetail.enqueue(new Callback<ChatRoomDetailDTO>() {
            @Override
            public void onResponse(Call<ChatRoomDetailDTO> call, Response<ChatRoomDetailDTO> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "onResponse: error chatRoomDataDetail fail" );
                    return;
                }
                ChatRoomDetailDTO chatRoomDetailDTO = response.body();
                if ( chatRoomDetailDTO == null){
                    Log.d(TAG, "onResponse: null chatRoomDetailDto");
                    return;
                }
                Log.d(TAG, "onResponse: chatRoomDataDetail chatRoomDetail last content " +chatRoomDetailDTO.getLastChatContent());
                Log.d(TAG, "onResponse: chatRoomDataDetail chatRoomDetail chat num" +chatRoomDetailDTO.getChatNumber());

                chatRoomDetailDTO.setChatRoomUserNumber(chatRoomUserNumber);
                chatRoomAdapter.setRoomDataDetail(mchatRoomID,chatRoomDetailDTO);


            }

            @Override
            public void onFailure(Call<ChatRoomDetailDTO> call, Throwable t) {

            }
        });

    }

    @SuppressLint("NewApi")
    @Override
    public void listenMessage(String message) {
        if(message != null){
            
        Log.d(TAG, "listenMessage: 수신한 메세지 " + message);
        }else {
            Log.d(TAG, "listenMessage: messgae is null");
        }
//        Optional<DefaultDTO> optDefaultDTO;
//        DefaultDTO defaultDTO;
//        ChatMessageDTO chatMessageDTO;
//        try {
//            optDefaultDTO = Optional.ofNullable(jsonConverter.getDefaultJsonAdapter().fromJson(message));
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        if(optDefaultDTO.isEmpty()|| optDefaultDTO.get().getMethod() != null){
//            Log.d(TAG, "listenRoomMessage: optDefaultDTO empty , null");
//            return;
//        }
//        defaultDTO = optDefaultDTO.get();
//        if( defaultDTO.getMethod().contentEquals("refresh_chat_id") ){
//                setChatRoomDataDetail((Long) defaultDTO.getData().get("chat_room_id"));
//        }

    }

    /* chatRoomUser id 얻어서 setChatRoomDetail 실행 */
    @SuppressLint("NewApi")
    @Override
    public void listenRoomMessage(String message) {
        Optional<DefaultDTO> optDefaultDTO;
        DefaultDTO defaultDTO;
        ChatMessageDTO chatMessageDTO;
        if(message == null || message.trim().length() < 10){
            Log.d(TAG, "listenRoomMessage: null or length 10 under message message = "+ message);
            return;
        }
//        Log.d(TAG, "listenRoomMessage: message "+ message);
        try {
            optDefaultDTO = Optional.ofNullable(jsonConverter.getDefaultJsonAdapter().fromJson(message));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(optDefaultDTO.isEmpty() || optDefaultDTO.get().getMethod() == null){
            Log.d(TAG, "listenRoomMessage: optDefaultDTO empty , null");
            return;
        }
        defaultDTO = optDefaultDTO.get();
        if( defaultDTO.getMethod().contentEquals("refresh_chat_id") ){
//            Log.d(TAG, "listenRoomMessage: refresh_chat_id method1 " + defaultDTO.getData().get("chat_room_id"));
            if(defaultDTO.getData().containsKey("chat_room_id")){
//                Log.d(TAG, "listenRoomMessage: refresh_chat_id method2 " + defaultDTO.getData().get("chat_room_id"));

            Long chatRoomId = ((Double) Objects.requireNonNull(defaultDTO.getData().get("chat_room_id"))).longValue();
            int size = defaultDTO.getListChatRoomUserDTOs().size(); // 사용자의 수
//            chatRoomAdapter.setRoomDataDetail(chatRoomId.);
            setChatRoomDataDetail(chatRoomId , size );
        }else {
                Log.e(TAG, "listenRoomMessage: message data didn't hava chatRoomId" );
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if( mySocketManager != null){
            mySocketManager.removeMchatRoomMessageListener(MainChatFragment.this);
        }
    }
}