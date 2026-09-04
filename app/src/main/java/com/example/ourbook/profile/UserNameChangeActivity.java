package com.example.ourbook.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ourbook.DataTool.Response.Update_Response;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ManageUserDataService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityUserNameChangeBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNameChangeActivity extends AppCompatActivity {

    ActivityUserNameChangeBinding binding;
    private RetrofitGenerator retrofitGenerator; //
    private ManageUserDataService manageUserDataService;
    private UserSingletone userSingletone;

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserNameChangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("TokenShared",MODE_PRIVATE);

        retrofitGenerator = new RetrofitGenerator();
        userSingletone = UserSingletone.getMyUser();
        manageUserDataService = retrofitGenerator.init_user_retrofit(ManageUserDataService.class);
        binding.newNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = String.valueOf(binding.newNameInput.getText());

                if (newName.length() >= 3) {
                    Call<Update_Response> userNameChangeService = manageUserDataService.updateUserName(userSingletone.getUserJWT(),newName );
                    userNameChangeService.enqueue(new Callback<Update_Response>() {
                        @Override
                        public void onResponse(Call<Update_Response> call, Response<Update_Response> response) {
                            if (response.body() == null){return;}
                            Update_Response updateResponse = response.body();
                            if(updateResponse.nameDuplicate.isSuccess()){
                                if (updateResponse.updateUserName.isSuccess()){
                                    // 닉네임 변경 성공
                                    Toast.makeText(getApplicationContext(),updateResponse.updateUserName.getMessage(),Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    // 닉네임 변경 실패 중복은 아니고 별개의 문제
                                    Toast.makeText(getApplicationContext(),updateResponse.updateUserName.getMessage() + "다시 시도해주세요 ",Toast.LENGTH_SHORT).show();

                                }
                            }else {
                                Toast.makeText(getApplicationContext(),updateResponse.nameDuplicate.getMessage()+ "입니다.",Toast.LENGTH_SHORT).show();
                            }

//                            if (updateResponse.getJwt_New() != null) {
//                                userSingletone.setUserJWT(updateResponse.getJwt_New());
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                userSingletone.setUserName(updateResponse.getUser_info().getUserName());
//                                editor.putString("jwt",userSingletone.getUserJWT());
//                                editor.apply();
//                                Toast.makeText(getApplicationContext(),"변경 성공",Toast.LENGTH_SHORT).show();
//                                finish();
//
//                            }else {
//                                Toast.makeText(getApplicationContext(),"변경 실패",Toast.LENGTH_SHORT).show();
//
//                                return;}

                        }

                        @Override
                        public void onFailure(Call<Update_Response> call, Throwable t) {

                        }
                    });
                }else { // 닉네임 입력 3글자 이하
                    Toast.makeText(UserNameChangeActivity.this,"최소 3글자 이상 입력해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}