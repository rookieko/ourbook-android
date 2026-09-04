package com.example.ourbook.Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourbook.Chat.DTO.ChatRoomDTO;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.BaseUrl;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ChatService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityChatRoomProfileBinding;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomProfileActivity extends AppCompatActivity {

    /* 채팅방 정보 액티비티 , 채팅방을 검색하고 선택시 보여지는 액티비티
    *  이후 채팅방 입장 버튼 입력시 채팅방 입장 단 비밀번호 설정이 된 경우 입력 확인 */
    private UserSingletone userSingletone;
    private ActivityChatRoomProfileBinding binding;
    private final String TAG = "ChatRoomProfileActivity" + Constants.AddTAG;
    private RetrofitGenerator retrofitGenerator;
    private ChatService chatService;
    private Map<String,Object> requestMap = new HashMap<>();
    private int chatRoomId ;
    String webNovelNamePrefix = "웹소설 : ";
    private ChatRoomDTO chatRoomData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        chatRoomId = intent.getIntExtra(Constants.INTENT_CHATROOM_ID,-1);
        if(chatRoomId == -1){
            Log.d(TAG, "onCreate: error intent error ");
            Toast.makeText(this,"error intent 전달 실패 " ,Toast.LENGTH_SHORT).show();
            finish();
        }else {
            Log.d(TAG, "onCreate: intent 성공 chatRoomId " + chatRoomId);
        }
        retrofitGenerator = new RetrofitGenerator();
        chatService = retrofitGenerator.init_user_retrofit(ChatService.class);

        userSingletone = UserSingletone.getMyUser();
        binding.ChatRoomJoinBtn.setOnClickListener(v -> {
            initJoinBtn();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        initProfile();
    }



    private void initProfile(){
        requestMap.put("chat_room_id",chatRoomId);
        Call<ChatRoomDTO> chatRoomDTOCall = chatService.getChatRoomData(userSingletone.getUserJWT(),requestMap);
        chatRoomDTOCall.enqueue(new Callback<ChatRoomDTO>() {
            @Override
            public void onResponse(Call<ChatRoomDTO> call, Response<ChatRoomDTO> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: error response");
                    return;
                }
                chatRoomData = response.body();
                Log.d(TAG, "onResponse:  null check ");
                if( chatRoomData == null ){
                    Log.d(TAG, "onResponse: error null chatRoomData");
                    return;
                }
                Log.d(TAG, "onResponse:  ui init  start ");
                // 소개 
                String introduce = "  소개 : " + chatRoomData.getChat_room_intro();
                binding.ChatRoomIntroduce.setText(introduce);
                // 이미지
                if(!chatRoomData.getCoverImage().contentEquals("null")){
                    Glide.with(ChatRoomProfileActivity.this).load(BaseUrl.BookCoverImage_URL+chatRoomData.getCoverImage()).into(binding.ChatRoomBookCoverImage);
                }else {
                    Glide.with(ChatRoomProfileActivity.this).load(R.drawable.sample_bookcover).into(binding.ChatRoomBookCoverImage);
                }
                // 태그
                // 웹소설 : 웹소설 제목
                String webNovelNameFull = webNovelNamePrefix + chatRoomData.getNovel_title();
                binding.ChatRoomWebNovelInfo.setText(webNovelNameFull);
                // 채팅방 이름
                String chatRoomName = "채팅방 제목 :" + chatRoomData.getChat_room_name();
                binding.ChatRoomNameInfo.setText(chatRoomName);

                //채팅방 정보
                String ChatRoomDetail = "채팅방 생성 날짜 :"+chatRoomData.getRoom_create_date();
                binding.ChatRoomDetailInfo.setText(ChatRoomDetail);

            }

            @Override
            public void onFailure(Call<ChatRoomDTO> call, Throwable t) {
                Log.d(TAG, "onFailure: fail "+ t.getMessage());
            }
        });
    }

    // 채팅방 입장을 위한 버튼 , 이후로 비밀번호 확인 , 사용자 이미 존재여부 확인 
    private void initJoinBtn(){
        requestMap.put("chat_room_id",chatRoomId);
        Call<NormalResponseDTO> normalResponseDTOCall =  chatService.joinChatRoom(userSingletone.getUserJWT(),requestMap);
        normalResponseDTOCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "onResponse: code error ");
                    return;
                }
                NormalResponseDTO normalResponseDTO = response.body();

                if(normalResponseDTO == null){ return;}
                
                if(normalResponseDTO.isSuccess()){
                    Log.d(TAG, "onResponse:  join success ");
                    //TODO 여기서 php 코드에서 java 로 바꿀지 결정하자.
                    Intent intent = new Intent(ChatRoomProfileActivity.this, ChatRoomActivity.class);
//                    Intent tempintent = new Intent(ChatRoomProfileActivity.this, MainLoginActivity.class);
                    intent.putExtra(Constants.INTENT_CHATROOM_ID,chatRoomId);
                    int tempCRUID ;
                    if(normalResponseDTO.getData() != null && normalResponseDTO.getData().get("chat_room_user_id") != null){
                        Log.d(TAG, "onResponse: not null in chat_room_user_id");
                        tempCRUID = (int) normalResponseDTO.getData().get("chat_room_user_id");
                    }else {
                        Log.e(TAG, "onResponse: null in chat_room_user_id");
                        tempCRUID = -1;
                    }
                    Log.d(TAG, "onResponse: 생성한 chat_room_user_id "+tempCRUID);
                    intent.putExtra(Constants.INTENT_CHATROOM_USER_ID, tempCRUID );
                    startActivity(intent);
                    finish();

                }else {
                    Log.d(TAG, "onResponse: fail join fail ");
                }
            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }
}