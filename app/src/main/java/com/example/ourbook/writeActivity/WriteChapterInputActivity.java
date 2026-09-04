package com.example.ourbook.writeActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityWriteChapterInputBinding;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteChapterInputActivity extends AppCompatActivity {
    // TODO 회차 정보가 어느 정도 나타내면 좋을 꺼 같음
    private static final String TAG = "WriteChapterInputActivity "+ Constants.AddTAG;

    private ActivityWriteChapterInputBinding binding;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private UserSingletone userSingletone;

    private String chapter_title;
    private String chapter_content;
    private int wid;
    private int chapter_word_length;
    private int chapter_text_length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteChapterInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userSingletone = UserSingletone.getMyUser();
        Intent intent = getIntent();

        wid = intent.getIntExtra("wid",-1);
        retrofitGenerator= new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        // 회차 번호 등록

        String lastnum_string = String.valueOf(userSingletone.getLastNum()+1)+" 화 "; // 꼬임  총 회차 정보
        if(lastnum_string != null) {
            binding.chapterShowText.setText(lastnum_string);
        };
        //

        binding.applyChapterButton.setOnClickListener(v -> {
            chapter_title = String.valueOf(binding.chapterTitleInput.getText());
            chapter_content = String.valueOf(binding.chapterContentInput.getText());
            List<String> words = Arrays.asList(chapter_content.split(" "));
            chapter_word_length = words.size();
            chapter_text_length = chapter_content.length(); // 줄넘기 표시는 안잡아도 되나 .
            Call<NormalResponseDTO> responseDTOCall = registerBookService.ChapterRegister(
                    userSingletone.getUserJWT(),wid,chapter_title,chapter_content,chapter_word_length,chapter_text_length
            );
            Log.d(TAG, "onCreate: 회차 등록전 data 확인 chapter_text_length "
                    +chapter_text_length+" chapter_word_length =" + chapter_word_length
                    +" 첫번째 :"+ words.get(0) +" 마지막 :"+ words.get(words.size()-1));
            responseDTOCall.enqueue(new Callback<NormalResponseDTO>() {
                @Override
                public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                    NormalResponseDTO normalResponseDTO = response.body();
                    if(normalResponseDTO == null){ return;}
                    //
                    if (normalResponseDTO.isSuccess()){
                        Toast.makeText(getApplicationContext(),"등록 성공",Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(WriteChapterInputActivity.this,WriteChapterMainActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent1.putExtra("wid",wid);
                        startActivity(intent1);

                        finish();
                    }else {
                        Toast.makeText(
                                getApplicationContext(),"실패 "+normalResponseDTO.getMessage(),Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage() );
                }
            });
        });

    }
}