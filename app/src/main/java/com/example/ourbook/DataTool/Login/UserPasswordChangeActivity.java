package com.example.ourbook.DataTool.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ManageUserDataService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityUserPasswordChangeBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPasswordChangeActivity extends AppCompatActivity {

    ActivityUserPasswordChangeBinding binding;
    private RetrofitGenerator retrofitGenerator; //
    private ManageUserDataService manageUserDataService;
    private UserSingletone userSingletone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPasswordChangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        retrofitGenerator = new RetrofitGenerator();
        userSingletone = UserSingletone.getMyUser();
        manageUserDataService = retrofitGenerator.init_user_retrofit(ManageUserDataService.class);

        binding.editPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 입력한 비밀 번호와 확인 비밀번호가 일치 하는지 확인
                String password1 = String.valueOf(binding.passwordInput.getText());
                String password2 = String.valueOf(binding.passwordSecondInput.getText());
                Log.d("비밀번호 확인 ", "onClick: "+password1 + " and "+ password2);
                if (!password1.equals(password2) ){
                    Toast.makeText(getApplicationContext(),"동일한 비밀 번호를 입력하세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                String Password = String.valueOf(binding.passwordSecondInput.getText());
                Call<NormalResponseDTO> signUpResponseCall = manageUserDataService.updateUserPW(userSingletone.getUserJWT(),Password);
                signUpResponseCall.enqueue(new Callback<NormalResponseDTO>() {
                    @Override
                    public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                        if (response.body() == null){ return;}
                        Log.d("TAG", "onResponse: 비밀 번호 변경 " + response.body());
                        NormalResponseDTO response_PWChange = response.body();
                        if (response_PWChange.isSuccess()){
                            Toast.makeText(getApplicationContext(),"비밀번호 변경 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                        Log.d("TAG", "onFailure: "+ t.getMessage());
                    }
                });
            }
        });


    }
}