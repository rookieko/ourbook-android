package com.example.ourbook.setBookActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityChangeBookTitleBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeBookTitleActivity extends AppCompatActivity {
    private ActivityChangeBookTitleBinding binding;

    private UserSingletone userSingletone;
    private MaterialToolbar materialToolbar;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private String title;
    private String tempWriteBookTitle;
    private static final String TAG= "ChangeBookTitleActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeBookTitleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        title = intent.getStringExtra("title");

        userSingletone = UserSingletone.getMyUser();

        materialToolbar = binding.MToolbar;
        /* toolbar 활성화 */
        setSupportActionBar(materialToolbar);
        /*toolbar 뒤로가기 버튼 (현재 navigation 으로 설정 ) 구현*/
        materialToolbar.setNavigationOnClickListener(view ->{
            finish();
        });

        binding.bookTitleInputText.setText(title);
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);

        binding.bookTitleCheckButton.setOnClickListener(v -> {
            if(binding.bookTitleInputText.getText()== null){ return; }
            // 길이 확인
            tempWriteBookTitle = String.valueOf(binding.bookTitleInputText.getText());
            if (tempWriteBookTitle.length() <  3){
                binding.bookTitleInputLayout.setError("최소 3글자 이상 입력하세요");
            } else if (tempWriteBookTitle.length() > 20) {
                binding.bookTitleInputLayout.setError("최대 20 글자 입력이 가능합니다.");
            }
            // 레트로핏 call 초기화 ,  jwt token , 입력한 title 문자열 전송 , 응답 , isSuccess , status , ( 성공시 title ) 를 받음
            Call<NormalResponseDTO> duplicateCheckCall = registerBookService.TitleDuplicateCheck(userSingletone.getUserJWT(),tempWriteBookTitle);

            duplicateCheckCall.enqueue(new Callback<NormalResponseDTO>() {
                @Override
                public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                    NormalResponseDTO duplicateCheckResponse = response.body();
                    if (duplicateCheckResponse == null){ return; }
                    if(duplicateCheckResponse.isSuccess()){
                        Map<String,String> fieldMap = new HashMap<>();
                        fieldMap.put("change","title");
                        fieldMap.put("title",tempWriteBookTitle);
                        Call<NormalResponseDTO> changeWebNovelCall = registerBookService.UpdateWebNovelData(userSingletone.getUserJWT(),userSingletone.getWid(),fieldMap);
                        /*중복 검사 성공 이후 data 수정 */
                        changeWebNovelCall.enqueue(new Callback<NormalResponseDTO>() {

                            @Override
                            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                                NormalResponseDTO updateResponse = response.body();
                                if (updateResponse == null ){return;}
                                if(updateResponse.isSuccess()){
                                    Toast.makeText(ChangeBookTitleActivity.this,"수정 성공",Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onResponse: "+updateResponse.getMessage());
                                    finish();
                                }else {
                                    Toast.makeText(ChangeBookTitleActivity.this,"수정 실패 다시 시도해주세요",Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                                Log.e(TAG, " 레트로핏 onFailure: "+ t.getMessage() );

                            }
                        });

                    }

                    // 결과 출력
                    Toast.makeText(getApplicationContext(),duplicateCheckResponse.getMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                    Log.e("TAG ", " 레트로핏 onFailure: "+t.getMessage() );
                }
            });

        });

        binding.bookTitleInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 3 ){
                    binding.bookTitleCheckButton.setEnabled(false);
                }else if ( s.length() > 20 ){
                    binding.bookTitleCheckButton.setEnabled(false);
                }else {
                    binding.bookTitleCheckButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 3 ){
                    binding.bookTitleCheckButton.setEnabled(false);
                    binding.bookTitleInputLayout.setError("최소 3글자 이상 입력하세요");
                }else if ( s.length() > 20 ){

                    binding.bookTitleCheckButton.setEnabled(false);
                    binding.bookTitleInputLayout.setError("최대 20 글자 입력이 가능합니다.");
                }else {
                    if (Objects.equals(title, s.toString())){
                        binding.bookTitleCheckButton.setEnabled(false);

                    };
                    binding.bookTitleInputLayout.setError(null);

                }
            }
        });

    }
}