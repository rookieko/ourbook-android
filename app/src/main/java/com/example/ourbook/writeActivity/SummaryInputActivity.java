package com.example.ourbook.writeActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.User;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivitySummaryInputBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SummaryInputActivity extends AppCompatActivity {

    private ActivitySummaryInputBinding binding;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private Map<String,String> requestMap = new HashMap<>();
    private int wid;
    private String jwt;
    private String summary;
    private UserSingletone userSingletone;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySummaryInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        wid = intent.getIntExtra(Constants.INTENT_WID,-1);
        if( wid  < 0 ){
            Toast.makeText(this,"잘못된 접근입니다.",Toast.LENGTH_SHORT).show();
            finish();
        }
        summary = String.valueOf(intent.getStringExtra(Constants.INTENT_SUMMARY));
        if( !summary.contentEquals("null") ){
            binding.textViewSummaryInput.setText(summary);
        }
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        userSingletone = UserSingletone.getMyUser();
        jwt= userSingletone.getUserJWT();
        binding.fabSummaryRegis.setOnClickListener(v -> {
            UpdateSummary();
        });
    }


    private void UpdateSummary(){
//        requestMap.put(Constants.REQUEST_WID, String.valueOf(wid));
        summary = Objects.requireNonNull(binding.textViewSummaryInput.getText()).toString();
        requestMap.put("change","summary");
        requestMap.put(Constants.REQUEST_BOOK_SUMMARY,summary);
        Call<NormalResponseDTO> updateWebNovelCall = registerBookService.UpdateWebNovelData(jwt,wid,requestMap);
        updateWebNovelCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                if (response.body() == null){
                    return;
                }
                NormalResponseDTO normalResponseDTO = response.body();
                if (normalResponseDTO.isSuccess()){
                    //
                    Toast.makeText(SummaryInputActivity.this,"등록 되었습니다. " , Toast.LENGTH_SHORT).show();
                    finish();

                }else {
                    Toast.makeText(SummaryInputActivity.this,"실패 다시 시도해주세요",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {

            }
        });
    }
}