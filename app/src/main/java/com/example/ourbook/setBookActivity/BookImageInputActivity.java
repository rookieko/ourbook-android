package com.example.ourbook.setBookActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.UploadAPI;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityBookImageInputBinding;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BookImageInputActivity extends AppCompatActivity {
    private ActivityBookImageInputBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private RetrofitGenerator retrofitGenerator;

    private Uri selectedImageUri;

    // 임시 .. 이미지 업로드 테스트용
    private Retrofit retrofit;
    private String imagePath;
    private UploadAPI uploadAPI;
    private String TAG = "이미지 업로드";
    private UserSingletone userSingletone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookImageInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        retrofitGenerator = new RetrofitGenerator();
        userSingletone = UserSingletone.getMyUser();

        uploadAPI = retrofitGenerator.init_user_retrofit(UploadAPI.class);

        Intent intent = getIntent();
        //이미지 경로
        imagePath = intent.getStringExtra("imagePath");
        // 선택한 이미지 경로 uri.toString <-> Uri.parse
        String setImagePath = intent.getStringExtra("setImagePath");
//        binding.selectedImageView.setImageURI(Uri.parse(setImagePath));
        Glide.with(this).load(setImagePath).into(binding.selectedImageView);
        Log.d(TAG, "사진 업로드 onCreate: input  "+ setImagePath + imagePath);
        // 등록 버튼
        binding.saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a file object using the image path
//                Log.d(TAG, "onClick: "+ imagePath);
                File file = new File(imagePath);
//                File file = FileUtils.getFile(this, fileUri);
                Log.d(TAG, "onClick: 파일 권한 확인 "+ file.canRead());
                Log.d(TAG, "onClick: wid  확인 "+ userSingletone.getWid());


                //

                // 전송항 part_map 초기화
                Map<String, Object> part_map = new HashMap<>();
                part_map.put("wid",userSingletone.getWid());


                // Create a request body with file and image media type
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
//                RequestBody requestbody2 = file.asRequestBody(MediaType.parse("image/*"),file);
                RequestBody requestbody3 = RequestBody.create(file,MediaType.parse("image/*"));

                // Create MultipartBody.Part using file request-body,file name
                MultipartBody.Part part = MultipartBody.Part.createFormData("imageKey", file.getName(), requestFile);


                Call<NormalResponseDTO> uploadCall = uploadAPI.uploadBookCover(userSingletone.getUserJWT(),part,part_map);
                uploadCall.enqueue(new Callback<NormalResponseDTO>() {
                    @Override
                    public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                        NormalResponseDTO normalResponseDTO  = response.body();
                        if(normalResponseDTO == null){return;}
                        if(normalResponseDTO.isSuccess()){
                            // 성공
                            Toast.makeText(BookImageInputActivity.this,"이미지 변경 성공",Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Log.e(TAG, "onResponse: 이미지 변경 실패 "+ normalResponseDTO.getMessage() );
                        }
                    }

                    @Override
                    public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                    }
                });
            }
        });
        // 취소 버튼
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}