package com.example.ourbook.Chat;

import static com.example.ourbook.DataTool.BaseUrl.BookCoverImage_URL;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ChatService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.SearchNovelActivity;
import com.example.ourbook.databinding.ActivityChatRoomCreateBinding;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomCreateActivity extends AppCompatActivity {
    /* 채팅방 생성 액티비티 */
    private ActivityChatRoomCreateBinding binding;
    private final String TAG = " ChatRoomCreateActivity "+ Constants.AddTAG;
    private ChatService chatService;
    private UserSingletone userSingletone;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private RetrofitGenerator retrofitGenerator;
    private Map<String,Object> requestMap = new HashMap<>();
    private int wid = -1; // 기본 값 -1 인 경우 웹소설이 선택이 되지 않았음을 나태는 지표
    private String writerName;
    private String bookName;
    private String coverImagePath;
    private String name;
    private String introduce;
    private String password;
    private String jwt;
    /* 1. 채팅방 이름 중복 ... */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        retrofitGenerator = new RetrofitGenerator();
        chatService = retrofitGenerator.init_user_retrofit(ChatService.class);
        userSingletone = UserSingletone.getMyUser();
        jwt = userSingletone.getUserJWT();
        /* 채팅방 비밀번호 설정 클릭 */
        binding.passwordSettingBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ){
                    binding.chatRoomPasswordInput.setVisibility(View.VISIBLE);
                }else {
                    binding.chatRoomPasswordInput.setVisibility(View.GONE);
                }
            }
        });
        /* 채팅방 생성 버튼 클릭*/
        binding.ChatRoomCreateBtn.setOnClickListener(v -> {
            if(CheckInputCondition()) {
                regisChatRoom();
            };
        });
        // 웹소설 태그 정보를 가져오기 위한 ui + intent Result
        setWidFromSearch();




    }
    private void setWidFromSearch(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if( o.getResultCode() == Activity.RESULT_OK){
                            Intent intent = o.getData();
                            if( intent == null ){ return;}
                            wid = intent.getIntExtra(Constants.INTENT_WID,-1);
                            Log.d(TAG, "onActivityResult: 선택한 웹소설 id " + wid);
                            if (wid == -1 ) {
                                Toast.makeText(ChatRoomCreateActivity.this, "다시 선택해주세요", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            bookName = intent.getStringExtra(Constants.INTENT_BOOK_NAME);
                            writerName = intent.getStringExtra(Constants.INTENT_WRITER_NAME);
                            coverImagePath = intent.getStringExtra(Constants.INTENT_BOOK_COVER);
                            if(coverImagePath == null){
                                Glide.with(ChatRoomCreateActivity.this).load(R.drawable.sample_bookcover).into(binding.bookCoverImageView);
                            }else if( wid != -1 ) {
                                Glide.with(ChatRoomCreateActivity.this).load(BookCoverImage_URL+coverImagePath).into(binding.bookCoverImageView);
                            }
                            String tagString = "제목 : " + bookName + "\n 작가 : " + writerName + "";
                            binding.ChatRoomTag.setText(tagString);
                        }else {
                            if( wid > 0 ){
                                // 이미 선택한 웹소설을 선택하여 wid 가 존재하는 경우
                                return;
                            }
                            Toast.makeText(ChatRoomCreateActivity.this, "채팅방을 만들려면 웹소설을 선택해주세요", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onActivityResult: 실패 result code " +o.getResultCode() );
                        }
                    }
                });
        binding.ChatRoomChooseNovelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ChatRoomCreateActivity.this, SearchNovelActivity.class);
            intent.putExtra("option",1);

            activityResultLauncher.launch(intent);

        });

    }
    private void setRequestMap(){
        name = String.valueOf(binding.chatRoomIntroduceInput.getText()).trim();
        introduce = String.valueOf(binding.chatRoomIntroduceInput.getText()).trim();
        password = String.valueOf(binding.chatRoomPasswordInput.getText()).trim();



        requestMap.put("name",name);
        requestMap.put("introduce",introduce);
        requestMap.put("wid",wid);
        if(binding.passwordSettingBtn.isChecked()) {
            requestMap.put("password_setting",1);
            requestMap.put("password", password);
        }else {
            requestMap.put("password_setting",0);
        }

    }

    /* 채팅방 생성에 필요한 입력 조건을 확인*/
    private boolean CheckInputCondition(){
        // 1. 채팅방 이름
        if(String.valueOf(binding.chatRoomNameInput.getText()).isBlank()){
            binding.chatRoomIndicatorText.setVisibility(View.VISIBLE);
            binding.chatRoomIndicatorText.setText("채팅방의 이름을 설정해주세요.");
            return false;
        }
        // 2. 채팅방 태그  ( 웹소설 선택 )
        else if (wid < 1) {
            binding.chatRoomIndicatorText.setVisibility(View.VISIBLE);
            binding.chatRoomIndicatorText.setText("채팅방의 태그로 사용할 웹소설을 설정해주세요.");
            return false;
            
        }
        // 3. 채팅방 소개 입력
        else if (String.valueOf(binding.chatRoomIntroduceInput.getText()).isBlank()) {
            binding.chatRoomIndicatorText.setVisibility(View.VISIBLE);
            binding.chatRoomIndicatorText.setText("채팅방의 소개를 입력해주세요.");
            return false;

        }
        // 4. 채팅방 비밀번호 선택 => 선택항목
        else if (String.valueOf(binding.chatRoomPasswordInput.getText()).isBlank()) {
            if(binding.passwordSettingBtn.isChecked()) {
                // 선택이 되지 않음
                binding.chatRoomIndicatorText.setVisibility(View.VISIBLE);
                binding.chatRoomIndicatorText.setText("채팅방의 비밀번호를 설정해주세요.");
                return false;
            }
        }
        return true;
    }

    private void regisChatRoom(){
        setRequestMap();
        Call<NormalResponseDTO> regisChatRoomCall = chatService.regisChatRoom(jwt,requestMap);
        regisChatRoomCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                if(!response.isSuccessful()){ return;}
                NormalResponseDTO normalResponseDTO = response.body();
                if( normalResponseDTO == null ) { return; }
                if( normalResponseDTO.isSuccess()){
                    Log.d(TAG, "onResponse: message " + normalResponseDTO.getMessage());
                    Log.d(TAG, "onResponse: data chat_room_id " + normalResponseDTO.getData().get("chat_room_id"));
                    Toast.makeText(getApplicationContext(),"채팅방 생성 성공",Toast.LENGTH_SHORT).show();
                    int cRid = (int) Double.parseDouble(String.valueOf(normalResponseDTO.getData().get("chat_room_id")));
                    Intent intent = new Intent(ChatRoomCreateActivity.this,ChatRoomActivity.class);
                    intent.putExtra(Constants.INTENT_CHATROOM_ID, cRid);
                    startActivity(intent);
                    finish();
                }else {
                    Log.d(TAG, "onResponse: message fail " + normalResponseDTO.getMessage());
                }
            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}