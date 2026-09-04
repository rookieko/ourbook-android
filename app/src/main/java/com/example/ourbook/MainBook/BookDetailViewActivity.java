package com.example.ourbook.MainBook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityBookDetailViewBinding;

public class BookDetailViewActivity extends AppCompatActivity {
    private ActivityBookDetailViewBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityBookDetailViewBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


    }
}