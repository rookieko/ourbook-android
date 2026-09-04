package com.example.ourbook.DataTool.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.ourbook.DataTool.UserSharedHelper;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.MainLoginActivity;
import com.example.ourbook.R;


public class SplashActivity extends AppCompatActivity {
    // 저장한 Token 값이 존재 하는지
    private boolean isTokenSave = false;
    private final String TAG = "SplashActivity" ;
    private UserSingletone userSingletone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        userSingletone = UserSingletone.getMyUser();
        setContentView(R.layout.activity_splash);

        // SharedPreference 초기화 . LazyViewHolder
        UserSharedHelper.init(getApplicationContext());

        startLoading();
        String Token = UserSharedHelper.read(UserSharedHelper.KEY_LOGIN_TOKEN,null);
        if(Token == null){ // JWT 토큰이 존재 하지 않을 때 -> MainActivity 로 이동
            isTokenSave = false;
        }else if (Token.length() > 10){ // JWT Token 이 존재 할 때 ,  유효성 검증 필요성
            isTokenSave = true;
            userSingletone.setUserJWT(Token);
        }
        Log.d(TAG, " Token Shared 확인  값 : "+ Token);


    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class); // 혹시 몰라서 null 로 변경 가능
                if (isTokenSave){
                     intent = new Intent(SplashActivity.this, MainLoginActivity.class);
                }else {
                     intent = new Intent(SplashActivity.this, MainActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}