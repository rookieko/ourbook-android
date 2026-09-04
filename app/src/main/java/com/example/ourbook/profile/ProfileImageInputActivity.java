package com.example.ourbook.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.UploadAPI;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityProfileImageInputBinding;

import java.io.File;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
//import okhttp3.RequestBody.Companion.asRequestBody;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ProfileImageInputActivity extends AppCompatActivity {
    private ActivityProfileImageInputBinding binding;
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
        userSingletone = UserSingletone.getMyUser();
        binding = ActivityProfileImageInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofitGenerator = new RetrofitGenerator();

         uploadAPI = retrofitGenerator.init_user_retrofit(UploadAPI.class);

        Intent intent = getIntent();
        //이미지 경로
        imagePath = intent.getStringExtra("imagePath");
        // 선택한 이미지 경로 uri.toString <-> Uri.parse
        String setImagePath = intent.getStringExtra("setImagePath");
//        binding.selectedImageView.setImageURI(Uri.parse(setImagePath));
        Glide.with(this).load(setImagePath).into(binding.selectedImageView);
//        Log.d(TAG, "onCreate imagePath : " + imagePath);
//        Log.d(TAG, "onCreate setImagePath : " + setImagePath);
//        Log.d(TAG, "onCreate Uri.getPath : " + Uri.parse(setImagePath).getPath());

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//        }


        binding.saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Log.d(TAG, "onClick: 권한 확인 "+ ContextCompat.checkSelfPermission(getApplicationContext(),manifet));

                // 갤러리를 여는 인텐트 생성
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, PICK_IMAGE_REQUEST);


                // Create a file object using the image path
                File file = new File(imagePath);
//                File file = FileUtils.getFile(this, fileUri);
                Log.d(TAG, "onClick: 파일 권한 확인 "+ file.canRead());


                //

                //

                // Create a request body with file and image media type
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
//                RequestBody requestbody2 = file.asRequestBody(MediaType.parse("image/*"),file);
                RequestBody requestbody3 = RequestBody.create(file,MediaType.parse("image/*"));

                // Create MultipartBody.Part using file request-body,file name
                MultipartBody.Part part = MultipartBody.Part.createFormData("imageKey", file.getName(), requestFile);

                // Create description
                RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

                Call<NormalResponseDTO> uploadCall = uploadAPI.uploadImage(userSingletone.getUserJWT(),part,userSingletone.getUserName());
                uploadCall.enqueue(new Callback<NormalResponseDTO>() {
                    @Override
                    public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                        Optional<NormalResponseDTO> optionalNormalResponseDTO = Optional.ofNullable(response.body());

                        NormalResponseDTO UploadResponse = response.body();
                        if (UploadResponse == null){
                            Toast.makeText(getApplicationContext(), "업로드 실패" , Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // null 체크 이후
                        if (UploadResponse.isSuccess()){

                            Toast.makeText(getApplicationContext(), UploadResponse.getMessage() , Toast.LENGTH_SHORT).show();
                            String imagePath = (String) UploadResponse.getData().get("imagePath");
                            userSingletone.setUserImagePath(imagePath);

                            finish();

                        }else {
                            Toast.makeText(getApplicationContext(), UploadResponse.getMessage() , Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                        Log.d(TAG, "업로드 실패 onFailure: " +t.getMessage());
                    }
                });

//                // Execute the request
////                Call<ResponseBody> call = UploadAPI.uploadImage(part, description);
//                Call<ResponseBody> call = uploadAPI.uploadImageSample(part,description);
//                call.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        Log.d(TAG, "onResponse: " + response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        Log.d(TAG, "onFailure: "+ t.getMessage());
//                    }
//                });

            }
        });
        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // 권한이 허용됨
//                } else {
//                    // 권한 거부
//                }
//                return;
//            }
//        }
//    }


    // onActivityResult 메서드에서 URI를 파일 경로로 변환
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//            Uri selectedImageUri = data.getData();
//            imagePath = getPathFromUri(selectedImageUri);
//        }
//    }

    // URI를 실제 파일 경로로 변환하는 메서드
    private String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }


//    // 권한 설정
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static final String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//    private static void getPermission(Activity activity){
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//
//        if (permission2 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if(!Environment.isExternalStorageManager()){
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                activity.startActivity(intent);
//            }
//        }
//    }
}