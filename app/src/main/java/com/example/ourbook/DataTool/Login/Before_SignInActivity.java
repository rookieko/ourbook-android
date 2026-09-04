package com.example.ourbook.DataTool.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ourbook.databinding.ActivityBeforeSignInBinding;

public class Before_SignInActivity extends AppCompatActivity {
    private ActivityBeforeSignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBeforeSignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //회원 정보 등록 페이지로 이동
        binding.emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.ServiceAgreeCheckBox.isChecked()&&binding.personalServiceAgreeCheckBox.isChecked()){
                    Intent intent = new Intent(Before_SignInActivity.this, SignUpInputData_Activity.class);
                    startActivity(intent);

                }else {
                    CharSequence text = "필수 약관에 동의하셔야 합니다.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(Before_SignInActivity.this /* MyActivity */, text, duration);
                    toast.show();
                };

                binding.personalServiceAgreeCheckBox.isChecked();
                binding.marketingEmailAgreeCheckBox.isChecked();

            }
        });


    }
}