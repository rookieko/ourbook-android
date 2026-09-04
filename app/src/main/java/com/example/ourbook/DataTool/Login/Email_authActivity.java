package com.example.ourbook.DataTool.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.SignUpService;
import com.example.ourbook.DataTool.UserData;
import com.example.ourbook.databinding.ActivityEmailAuthBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Email_authActivity extends AppCompatActivity {
    private ActivityEmailAuthBinding binding;
    private RetrofitGenerator retrofitGenerator; //
    private SignUpService signUpService;
    private UserData userData;
    public String authCodeString;
    public int authCode;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        retrofitGenerator = new RetrofitGenerator();

        Intent intent = getIntent(); //전달할 데이터를 받을 Intent
        //text 키값으로 데이터를 받는다. String을 받아야 하므로 getStringExtra()를 사용함
        email = intent.getStringExtra("email");

        binding.AuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCodeString = String.valueOf(binding.AuthCodeInput.getText());
                authCode = Integer.parseInt(authCodeString);
                signUpService = retrofitGenerator.init_user_retrofit(SignUpService.class);
                Call<UserData> call = signUpService.AuthCodeCheck(authCode,email);
                call.enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(Call<UserData> call, Response<UserData> response) {
                        UserData responseUserData = response.body();
                        if (responseUserData == null ){
                            return;
                        }
                        if (responseUserData.AuthCheck.isSuccess()){
                            Toast.makeText(getApplicationContext(),"이메일 인증 성공.",Toast.LENGTH_SHORT).show();
                            Intent intentOk = new Intent(Email_authActivity.this , login_Activity.class);
                            startActivity(intentOk);
//                            finish();
                        }else if (responseUserData.AuthCheck.getStatus() ==400){
                            Toast.makeText(getApplicationContext(),"이메일 인증 번호를 잘못 입력하였습니다.",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),"유효 하지 않습니다.",Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<UserData> call, Throwable t) {

                    }
                });

            }
        });
    }
}