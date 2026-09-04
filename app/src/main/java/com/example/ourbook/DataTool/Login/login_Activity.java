package com.example.ourbook.DataTool.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.SignUpService;
import com.example.ourbook.DataTool.SingletoneTest;
import com.example.ourbook.DataTool.UserData;
import com.example.ourbook.DataTool.UserSharedHelper;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.MainLoginActivity;
import com.example.ourbook.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login_Activity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private RetrofitGenerator retrofitGenerator;
    private SignUpService signUpService;
    private UserData userdata;
    private String emailInput;
    private String password;

    //test
    SingletoneTest singletoneTest;
    UserSingletone userSingletone;
    SharedPreferences jwtTokenSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        retrofitGenerator = new RetrofitGenerator();
        // 초기화
        jwtTokenSharedPref = getSharedPreferences("TokenShared",MODE_PRIVATE); // shared 초기화
        userSingletone = UserSingletone.getMyUser();
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpService = retrofitGenerator.init_user_retrofit(SignUpService.class);

                emailInput= String.valueOf(binding.emailInput.getText());
                password = String.valueOf(binding.passwordInput.getText());
                Call< UserData > call = signUpService.LoginCheck(emailInput,password);

                call.enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(Call<UserData> call, Response<UserData> response) {


                       UserData responseLogin = response.body();
                        // if 조건문에 앞이 false 이면 뒤에 있는 조건 문은 실행이 안되는 점을 사용하여 NullPointerException 을 피해 보았음
                       if (responseLogin != null && responseLogin.LoginCheck == 200) {
                           // 로그인 성공
                           Toast.makeText(getApplicationContext(),"로그인 성공",Toast.LENGTH_SHORT).show();

                           // 토큰 저장
                           if (responseLogin.getOBJWToken() == null){
                               Log.d("로그인 액티비티 ", "onResponse: 토큰 null ");
                               return;
                           };
                           //토큰 저장
//                           SharedPreferences.Editor editor =  jwtTokenSharedPref.edit();
//                           editor.putString("jwt",responseLogin.getOBJWToken());
//                           editor.apply();
                           //토큰 저장

                           //토큰 저장 v2

                           UserSharedHelper.write(UserSharedHelper.KEY_LOGIN_TOKEN,responseLogin.getOBJWToken());

                           //토큰 저장 v2

                           Log.d("로그인 액티비티 ", "onResponse: token"+  responseLogin.getOBJWToken());
                           // 싱글톤 초기화
                           // 이후 로그인 응답 수정시 수정 할 것
                           userSingletone.setUserJWT(responseLogin.getOBJWToken());
                           userSingletone.setUserName(responseLogin.userName);
                           userSingletone.setUserEmail(responseLogin.email);
                           userSingletone.setUid(responseLogin.uid);

                           Intent intent = new Intent(login_Activity.this, MainLoginActivity.class);
                           intent.putExtra("email",responseLogin.email);
                           intent.putExtra("userName",responseLogin.userName);
                           intent.putExtra("uid",responseLogin.uid);


                           //TODO 액티비티 스택 관리..  FLAG 검색

//                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그인 성공 , 그 전 액티비티 task 비우기

                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);// 로그인 성공 , 그 전 액티비티 task 비우기
                           startActivity(intent);
                           finish();
                       }else {
                           Toast.makeText(getApplicationContext(),"이메일과 비밀번호를 다시 확인 하세요",Toast.LENGTH_SHORT).show();

                       };
                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {

                    }
                });

            }
        });

        binding.passwordForgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login_Activity.this, password_emailAuthActivity1.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        singletoneTest = SingletoneTest.getInstance();
        singletoneTest.name = singletoneTest.name + " 로그인 액티비티 에서 값을 입력";

        Log.d("싱글톤 테스트 ", " 로그인 액티비티 onResume: singeltone "+ singletoneTest.name);


    }
}