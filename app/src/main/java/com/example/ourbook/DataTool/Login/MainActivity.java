package com.example.ourbook.DataTool.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.SignUpService;
import com.example.ourbook.DataTool.SingletoneTest;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private SingletoneTest singletoneTest;
    private SharedPreferences jwtTokenSharedPref;
    private SignUpService signUpService;
    private RetrofitGenerator retrofitGenerator;
    private UserSingletone userSingletone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 로딩 화면 시작

        //

        singletoneTest = SingletoneTest.getInstance();
        singletoneTest.name = " MainActivity 에서 값을 입력";
        //싱글톤 초기화
        userSingletone = UserSingletone.getMyUser();
        // shared 초기화
//        jwtTokenSharedPref = getSharedPreferences("TokenShared",MODE_PRIVATE);
//        SharedPreferences.Editor editor = jwtTokenSharedPref.edit();
        // shared 에서 token 불러오기
//        String StringToken = jwtTokenSharedPref.getString("jwt",null);
//        Log.d(TAG, "shared test onCreate: "+ StringToken);
//        if (StringToken != null){
//             retrofitGenerator = new RetrofitGenerator();
//            signUpService = retrofitGenerator.init_user_retrofit(SignUpService.class);
//            Call<UserJWTResponse> tokenCall = signUpService.JWTCheck(StringToken);
//            tokenCall.enqueue(new Callback<UserJWTResponse>() {
//                @Override
//                public void onResponse(Call<UserJWTResponse> call, Response<UserJWTResponse> response) {
//                    //값을 가져오고 나서
//                    Log.d(TAG, "UserJWTResponse onResponse: "+ response.body());
//                    UserJWTResponse resultJWTResponse = response.body();
////                    resultJWTResponse.getUser_info().getUserName();
//                    Log.d(TAG, "UserJWTResponse onResponse 이름 : "+resultJWTResponse.getUser_info().getUserName());
//                    Log.d(TAG, "UserJWTResponse onResponse 이메일: "+resultJWTResponse.getUser_info().getEmail());
//                    Log.d(TAG, "UserJWTResponse onResponse JWTKey "+resultJWTResponse.getOBJWToken());
//                    // 응답 성공 , but 시간 갱신 부분도 통과 되게 설정됨 주의
//                    if (resultJWTResponse.OJWT_Check.isSuccess()){
//                        // 토큰 갱신 로직 수정 이후 코드를 수정 해야함.
//                        editor.putString("jwt",resultJWTResponse.getOBJWToken());
//                        editor.apply();
//                        Intent intent = new Intent(MainActivity.this , MainLoginActivity.class);
//                        // 사용자 정보 싱글톤 초기화
//                        userSingletone.setUserEmail(resultJWTResponse.getUser_info().getEmail());
//                        userSingletone.setUserName(resultJWTResponse.getUser_info().getUserName());
//                        userSingletone.setUserJWT(resultJWTResponse.getOBJWToken());
//
//                        startActivity(intent);
//                        finish();
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<UserJWTResponse> call, Throwable t) {
//
//                }
//            });
//
//
//        }



        Log.d("싱글톤 테스트 ", "onCreate: singeltone "+ singletoneTest.name);



        // 로그인 버튼 login activity intent 이동
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, login_Activity.class);
                startActivity(intent);
            }
        });



        //회원 가입 버튼 before_signIn_activity 로이동
        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Before_SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}