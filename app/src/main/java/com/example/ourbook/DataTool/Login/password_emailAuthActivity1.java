package com.example.ourbook.DataTool.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ourbook.databinding.ActivityPasswordEmailAuth1Binding;

public class password_emailAuthActivity1 extends AppCompatActivity {
    private ActivityPasswordEmailAuth1Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordEmailAuth1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}