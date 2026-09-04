package com.example.ourbook.DataTool.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ourbook.databinding.ActivityPasswordEmailAuth2Binding;

public class password_emailAuthActivity2 extends AppCompatActivity {

    private ActivityPasswordEmailAuth2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordEmailAuth2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}