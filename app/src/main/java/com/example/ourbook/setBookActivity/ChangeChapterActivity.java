package com.example.ourbook.setBookActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityChangeChapterBinding;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeChapterActivity extends AppCompatActivity {
    /*WriteChapterInputActivity 와 거의 비슷한 형태 */
    private ActivityChangeChapterBinding binding;
    private  String title;
    private  String content;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private UserSingletone userSingletone;
    private static final String TAG = "ChangeChapterActivity"+ Constants.AddTAG;

    private  int wid;
    private int chapter_id;
    private int chapter_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeChapterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        chapter_id = intent.getIntExtra("cid",-1);
        chapter_num = intent.getIntExtra("num",-1);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        binding.chapterTitleInput.setText(title);
        binding.chapterContentInput.setText(content);
        if(chapter_id!= -1 && chapter_num!= -1){
            String chapter_num_string = String.valueOf(chapter_num)+ "화";
            binding.chapterShowText.setText(chapter_num_string);
        }else {
            Toast.makeText(this,"오류가 발생했습니다 다시 시도해주세요 ",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCreate: 에러 cid, num -1 ");
            finish();
        }


        userSingletone = UserSingletone.getMyUser();

        wid = intent.getIntExtra("wid",-1);
        retrofitGenerator= new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);


        binding.applyChapterButton.setOnClickListener(v -> {
            String title_input = String.valueOf(binding.chapterTitleInput.getText());
            String content_input = String.valueOf(binding.chapterContentInput.getText());

            Map<String,String> fieldMap = new HashMap<>();
            fieldMap.put("title",title_input);
            fieldMap.put("content",content_input);
            fieldMap.put("chapter_id", String.valueOf(chapter_id));
            fieldMap.put("change","change");
            Log.d(TAG, "onCreate:  값 확인 title : "+ title_input + " content :" +content_input);

            Call<NormalResponseDTO> chapterChangeCall = registerBookService.ChangeChapter(userSingletone.getUserJWT(),wid,fieldMap);
            chapterChangeCall.enqueue(new Callback<NormalResponseDTO>() {
                @Override
                public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                    NormalResponseDTO normalResponseDTO = response.body();

                    if(normalResponseDTO == null){return;}
                    if(normalResponseDTO.isSuccess()){
                        Toast.makeText(ChangeChapterActivity.this,"성공 ",Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(ChangeChapterActivity.this,"실패 다시 시도하세요",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<NormalResponseDTO> call, Throwable t) {

                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chapter_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {// "저장" 메뉴 항목 클릭 시 수행할 동작
            return true;
        } else if (itemId == R.id.action_delete) {// "삭제" 메뉴 항목 클릭 시 수행할 동작
            Map<String,String> fieldMap = new HashMap<>();
            fieldMap.put("change","delete");
            fieldMap.put("chapter_id", String.valueOf(chapter_id));
            Call<NormalResponseDTO> update_response = registerBookService.ChangeChapter(userSingletone.getUserJWT(),userSingletone.getWid(),fieldMap);
            update_response.enqueue(new Callback<NormalResponseDTO>() {
                @Override
                public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                    NormalResponseDTO normalResponseDTO = response.body();
                    if(normalResponseDTO == null){return;}
                    if(normalResponseDTO.isSuccess()){
                        Toast.makeText(getApplicationContext(),"회차가 삭제 되었습니다.",Toast.LENGTH_SHORT).show();

                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"삭제 실패",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<NormalResponseDTO> call, Throwable t) {

                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        /* 싱글톤  초기화 */
//        userSingletone.setLastNum(0);
//        userSingletone.setWid(-1);
    }
}