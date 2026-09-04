package com.example.ourbook.writeActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityBookTitleInputBinding;
import com.google.android.material.appbar.MaterialToolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BookTitleInputActivity extends AppCompatActivity {

    // 책의 제목을 입력하고 중복 유무를 확인 할 수 있는 액티비티
    // 중복이 아닌 경우 다음 상세 정보 입력 페이지로 이동한다.

    private static final String TAG = "BookTitleInputActivity 중복 확인 " + Constants.AddTAG;

    private ActivityBookTitleInputBinding binding;
    private MaterialToolbar materialToolbar;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private String tempWriteBookTitle; /* (임시) 사용하려는 책의 제목
    -> (Think: 서버 DB에 최종적으로 등록 전까지 확인을 해야 하지 않을까..) */

    // 싱글턴, 초기화 for token
    private UserSingletone userSingletone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding   = ActivityBookTitleInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 싱글톤 초기화

        userSingletone = UserSingletone.getMyUser();
        materialToolbar = binding.MToolbar;
        /* toolbar 활성화 */
        setSupportActionBar(materialToolbar);
        /*toolbar 뒤로가기 버튼 (현재 navigation 으로 설정 ) 구현*/
        materialToolbar.setNavigationOnClickListener(view ->{
            finish();
        });
        // 레트로핏 - 인터페이스 초기화
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);


        /* 확인 버튼 기능 구현
            1. text 길이 , 입력 유무 확인
            2. 서버로 부터 중복 확인
         이후 다음 액티비티 이동 */


        binding.bookTitleCheckButton.setOnClickListener(view->{
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
                        // 결과 출력 -> 아래에서 해도 되네 .. startActivity 이후 코드가 일단 끝까지는 진행이 되는 듯  finish 를 사용 하지 않아서 그런가...
//                        Toast.makeText(getApplicationContext(),duplicateCheckResponse.getMessage(),Toast.LENGTH_SHORT).show();
                        // 성공 , intent 이동
                        Intent intent = new Intent(BookTitleInputActivity.this, BookInfoSetActivity.class);
                        // 성공 했던 제목 (= tempWriteBookTitle)
                        intent.putExtra("title",tempWriteBookTitle);
                        startActivity(intent);

                    }

                    // 결과 출력
                    Toast.makeText(getApplicationContext(),duplicateCheckResponse.getMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                    Log.e(TAG, " 레트로핏 onFailure: "+t.getMessage() );
                }
            });


        });
        // 텍스트 입력 확인용   TextWatcher
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
                    binding.bookTitleInputLayout.setError(null);

                }
            }
        });

    }


}