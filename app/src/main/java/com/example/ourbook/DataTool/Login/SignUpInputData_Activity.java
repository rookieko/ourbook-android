package com.example.ourbook.DataTool.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ourbook.DataTool.Response.SignUp_Response;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.SignUpService;
import com.example.ourbook.DataTool.UserData;

import com.example.ourbook.DataTool.ValidCheckUtil;
import com.example.ourbook.databinding.ActivitySignUpInputDataBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpInputData_Activity extends AppCompatActivity {
    private ActivitySignUpInputDataBinding binding;
    private RetrofitGenerator retrofitGenerator; //
    private SignUpService signUpService;

    private final String TAG = "SignUpInputData_Activity 회원가입 액티비티 ";
    // 변수
    private UserData userData; // 유저 데이터


//    private  ValidCheckUtil validCheckUtil; // 유저 데이터 정규식 확인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpInputDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 변수 초기화
        userData = new UserData();
        retrofitGenerator = new RetrofitGenerator();


        // 이메일 가입 버튼 1. 중복 검사  2. 이메일 전송 확인
        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userData.isValidUserInput()){ // test 1
                    String userName = String.valueOf(binding.usernameInput.getText());
                    String userEmail = String.valueOf(binding.emailInput.getText());
                    String userPassword = String.valueOf(binding.passwordSecondInput.getText());
                    Log.d(TAG, "onClick: 중복 확인 버튼 변수 값 " + userName + userEmail + userPassword);

                    //임시2
                    signUpService = retrofitGenerator.init_user_retrofit(SignUpService.class);
                    Call<SignUp_Response> signUpResponseCall = signUpService.SignInData(userEmail,userName,userPassword);
                    signUpResponseCall.enqueue(new Callback<SignUp_Response>() {
                        @Override
                        public void onResponse(Call<SignUp_Response> call, Response<SignUp_Response> response) {
                            SignUp_Response signUpResponse = response.body();
                            Log.d(TAG, "onResponse: response " + response.toString());
                            Log.d(TAG, "onResponse: response authCodeSaveCheckResponse "+signUpResponse.authCodeSaveCheckResponse.getMessage());
                            Log.d(TAG, "onResponse: response sendAuthEmailResponse "+signUpResponse.sendAuthEmailResponse.getMessage());
                            //중복 응답 처리
                            //1. 이메일 중복
                            if (!signUpResponse.emailDuplicationCheckResponse.isSuccess()){
                                binding.emailInputLayout.setError("이미 사용 중인 이메일 입니다");
                            };
                            if (!signUpResponse.userNameDuplicationCheckResponse.isSuccess()){
                                binding.usernameInputLayout.setError("이미 사용 죽인 닉네임 입니다");
                            };
                            if (!signUpResponse.userNameDuplicationCheckResponse.isSuccess() || !signUpResponse.emailDuplicationCheckResponse.isSuccess() ){
                                return;
                            };
                            //2. username 닉네임 중복


                            //이메일 인증 코드 보내기 ,인증 코드 저장 확인
                            if (signUpResponse.sendAuthEmailResponse.isSuccess()&&signUpResponse.authCodeSaveCheckResponse.isSuccess()){
                                Intent intent = new Intent(SignUpInputData_Activity.this, Email_authActivity.class);
                                intent.putExtra("email", userEmail);
                                startActivity(intent);

                            }else {
                                Toast.makeText(getApplicationContext(),"이메일 인증 코드 전송 실패" , Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onFailure(Call<SignUp_Response> call, Throwable t) {
                            Log.d(TAG, "onFailure: t 메세지 : "+t.getMessage());
                        }
                    });

                    //임시2
//                    Toast.makeText(getApplicationContext(),"모두 입력하였습니다 성공",Toast.LENGTH_SHORT).show();
//                    userData.setUserData(userName,userEmail,userPassword);
                    // 이메일 , 닉네임 중복 검사


                    //  임시 주석 처리
 /*                   signUpService = retrofitGenerator.init_user_retrofit(SignUpService.class);
                    Call<SignUp_Response> userDataCall = signUpService.UserDataDuplicateCheck(userEmail,userName,userPassword);
                    userDataCall.enqueue(new Callback<SignUp_Response>() {
                        @Override
                        public void onResponse(Call<SignUp_Response> call,  Response<SignUp_Response> response) {
                            SignUp_Response resultData = response.body();
                            if (resultData == null){
                                return;
                            }
                            Log.d(TAG, "onResponse: " + response.body());
//                            Log.d(TAG, "onResponse resultData.responceEmail: " + resultData.responceEmail);
//                            Log.d(TAG, "onResponse resultData.responceUserName : " + resultData.responceUserName);
//                            Log.d(TAG, "onResponse resultData.resultEmailSendCodeSaved : " + resultData.resultEmailSendCodeSaved);
//                            Log.d(TAG, "onResponse resultData.resultEmailSendCodeSaved : " + (resultData.resultEmailSendCodeSaved == 200));

                            if (resultData.emailDuplicationCheckResponse.getStatus() == 200){
                                Intent intent = new Intent(SignUpInputData_Activity.this, Email_authActivity.class);
                                intent.putExtra("email", userEmail);
                                startActivity(intent);
                            }else {

                                Toast.makeText(getApplicationContext(),resultData.emailDuplicationCheckResponse.getMessage(),Toast.LENGTH_SHORT).show();

                            };
                        }

                        @Override
                        public void onFailure(@NonNull Call<SignUp_Response> call, Throwable t) {

                            Log.d(TAG, "onFailure: "+ t.getMessage());
                        }
                    });
*/
                }else {   // test 1
                    Toast.makeText(getApplicationContext(),"항목들을 모두 입력하세요",Toast.LENGTH_SHORT).show();

                };    // test 1

            }
        });

        // 유저 정규식 확인  ( 닉네임 )
        binding.usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userNameInput = s.toString();
                if(!ValidCheckUtil.isValidUsername(userNameInput)){
                    binding.usernameInputLayout.setError("유효하지 않는 닉네임 입니다 3자 이상.");
                    userData.userNameCheck = false;

                }else {
                    userData.userNameCheck = true;
                    binding.usernameInputLayout.setError(null);

                };
            }
        });

        // 유저 정규식 확인  ( 비밀번호 )
        binding.passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userPasswordInput = s.toString();
                if(!ValidCheckUtil.isValidPassword(userPasswordInput)){
                    binding.passwordInputLayout.setError("유효하지 않는 비밀번호 입니다.");
                    userData.userPasswordCheck = false;

                }else {
                    userData.userPasswordCheck = true;
                    binding.passwordInputLayout.setError(null);
                };
            }
        });
        // 유저 정규식 확인  ( 재입력 비밀번호 확인 )
        binding.passwordSecondInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userPasswordSecondInput = s.toString();
                // 사용자 비밀번호와 재입력 비밀번호의 값이 같지 않을 때
                if (!userPasswordSecondInput.equals(String.valueOf(binding.passwordInput.getText()))){
                    userData.userPasswordSecondCheck = false;
                    binding.passwordSecondInputLayout.setError("동일한 비밀번호를 입력하세요");
                }else{
                    userData.userPasswordSecondCheck = true;
                    binding.passwordSecondInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 유저 정규식 확인  ( 이메일 )
        binding.emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailInput = s.toString();
                //이메일이 정규식 확인
                if (!ValidCheckUtil.isValidEmail(emailInput)){ // 정규식에 적합 하지 않을 때
                    userData.userEmailCheck = false;
                    binding.emailInputLayout.setError("유효한 이메일을 입력하세요");
                }else {
                    binding.emailInputLayout.setError(null);
                    userData.userEmailCheck = true;
                }
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
};