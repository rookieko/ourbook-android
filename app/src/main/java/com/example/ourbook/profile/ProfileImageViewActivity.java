package com.example.ourbook.profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityProfileImageViewBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//TODO 이미지 선택 버튼을 통해 이미지 추가 기능 ( 엘범 , 사진 찍기 snackBar 로 선택 , 우선 엘범에서 선택으로 구현하자 )
public class ProfileImageViewActivity extends AppCompatActivity {
    private ActivityProfileImageViewBinding binding;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 227;
    private String imagePath;
    private UserSingletone userSingletone;
    private ActivityResultLauncher<String> getImagePick;
    private ActivityResultLauncher<Uri> getTakePicture;
    private ActivityResultLauncher<String[]> requestPermissionLauncher; // 권한
    private Uri PickImageUri ;

    // 이미지 저장 되는 파일


    private final  String TAG = "ProfileImageViewActivity 프로필 이미지";
    
    //예제
    // 이미지를 저장할 파일 , 카메라 , 앨범 모두 사용
    private File imageFile;

    private ActivityResultLauncher<Intent> launcher_capture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG + "launcher_capture Callback", "image capturing is succeed");
                        Intent intent = new Intent(ProfileImageViewActivity.this, ProfileImageInputActivity.class);
                        Log.d(TAG, "onActivityResult: imagePath : " + imageFile.getPath());

                        intent.putExtra("imagePath", imageFile.getPath());
                        intent.putExtra("setImagePath", imageFile.getPath());
                        startActivity(intent);


                        /*------------------- 수정 ----------------------*/
//                        // 번들을 통해서 촬영 후 저장된 사진의 비트맵을 가져옴
//                        Bundle extra = result.getData().getExtras();
//                        Bitmap bitmap = (Bitmap) extra.get("data");
//
//
//                        // 서버로 전송하기 위해 비트맵을 파일로 변환
//                        imageFile = saveBitmapToJpeg(bitmap, ProfileImageViewActivity.this);
//
//                        Intent intent = new Intent(ProfileImageViewActivity.this, ProfileImageInputActivity.class);
//                        intent.putExtra("imagePath", imageFile.getPath());
//                        intent.putExtra("setImagePath", imageFile.getPath());
//                        startActivity(intent);

                    }else if(result.getResultCode() == Activity.RESULT_CANCELED){
                        Log.d(TAG + "launcher_capture Callback", "image capturing is canceled");
                    }else{
                        Log.e(TAG + "launcher_capture Callback", "image capturing has failed");
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileImageViewBinding.inflate(getLayoutInflater());
        userSingletone = UserSingletone.getMyUser();

        setContentView(binding.getRoot());
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            // 권한이 없을 경우, 사용자에게 권한 요청
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//        }
        // 이미지 추가 의 옵션 선택 팝업 메뉴
        PopupMenu popupMenu = new PopupMenu(this,binding.editImageButton);
        getMenuInflater().inflate(R.menu.profile_menu,popupMenu.getMenu());

        //권한 설정 , ActivityResultLauncher 사용
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                result->{
                    Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                    Boolean readExternalStorageGranted = result.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);

                    if (cameraGranted != null && cameraGranted && readExternalStorageGranted != null && readExternalStorageGranted) {
                        // 권한이 모두 승인된 경우
                        Toast.makeText(
                                getApplicationContext()
                                ,"권한 동의 성공"
                                ,Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        // 권한이 거부되었거나 일부만 승인된 경우
                        Log.d("권한", "onRequestPermissionsResult:  실패");
                        Toast.makeText(
                                getApplicationContext()
                                ,"프로필 이미지를 업로드 하려면 권한에 동의 해야 합니다."
                                ,Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }
                });
        // 권한 사진 , 저장 공간 읽기 권한 요구 실행
        requestPermissionLauncher.launch(new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        });

        // 이미지 추가 의 옵션 선택 팝업 메뉴 클릭 이벤트 정의
        popupMenu.setOnMenuItemClickListener(item -> {
            int id =item.getItemId();
            if (id == R.id.use_camera){ // 사진 찍기
//                PickImageUri = ;
//                getTakePicture.launch(PickImageUri);
                captureImage();
                return true;

            }else if (id == R.id.use_gallery){ // 앨범에서 불러오기
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);



                return true;
            }
            return false;
        });
        binding.editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                // 팝업 메뉴를 표시합니다.
                popupMenu.show();
            }
        });

        getTakePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(),
               isSuccess ->{
                    if(isSuccess){
                        Intent intent = new Intent(ProfileImageViewActivity.this, ProfileImageInputActivity.class);
                        intent.putExtra("imagePath",PickImageUri.getPath());
                        intent.putExtra("setImagePath",PickImageUri.getPath());
                        Log.d(TAG + "카메라 업로드 확인 경로", "onCreate 경로 : "+ PickImageUri.getPath());
                        startActivity(intent);
//                        PickImageUri.getPath();
                    }else Log.d(TAG, "onCreate: 카메라 intent 오류 - 실패 getTakePicture = registerForActivityResult ");
//                    binding.nowProfileImageView.setImageBitmap(bitmap);
//                   File imgFile = saveBitmapToJpeg(bitmap,this);
//                   Intent intent = new Intent(ProfileImageViewActivity.this, ProfileImageInputActivity.class);
//                   intent.putExtra("imagePath",imgFile.getPath());
//                   intent.putExtra("setImagePath",imgFile.getAbsolutePath());
//                   Log.d("카메라 업로드 확인 ", "onCreate:imgFile 권한 읽/쓰 "+imgFile.canRead()+ imgFile.canWrite());
//                   startActivity(intent);
               });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 프로필 이미지 유효성 확인 , null , 파일명 길이 확인 timstamp 을 사용하기 때문에 10 글자는 넘음

        if (userSingletone.getUserImagePath()!=null && userSingletone.getUserImagePath().length() >2){
            Log.d(TAG + "userSingletone.length() ", "onResume: imageView userSingletone.getUserImagePath().length()"+userSingletone.getUserImagePath().length());
            Log.d(TAG + "userSingletone.length() ", "onResume: imageView userSingletone.getUserImagePath() "+userSingletone.getUserImagePath());
            Glide.with(this).load(userSingletone.getProfileImageUrl()).into(binding.nowProfileImageView);
//
        }else {
//            binding.nowProfileImageView.setImageDrawable(DrawableRes.);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Log.d(TAG, "onActivityResult:  사용 되면 안되는 메서드 확인 ");
            Uri selectedImageUri = data.getData();
            Log.d("photo upload ", "사진 업로드 프로필 uri onActivityResult: "+ selectedImageUri);


            //
            imagePath = getPathFromUri(selectedImageUri);
            //  파일 접근이 가능 한지 확인용 ,
            Log.d(TAG + "이미지 업로드 이전 추가 액티비티", "onActivityResult: "+ new File(imagePath).canRead());
            String ShowImagePath = selectedImageUri.toString();

            Intent intent = new Intent(ProfileImageViewActivity.this, ProfileImageInputActivity.class);
            intent.putExtra("imagePath",imagePath);
            intent.putExtra("setImagePath",ShowImagePath);
            startActivity(intent);

        }

    }

    // uri 에서 경로를 가져오는 메소드
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


    // 비트맵을 파일로 변환하는 메소드
    public File saveBitmapToJpeg(Bitmap bitmap, Context context) {

        //내부저장소 캐시 경로를 받아옵니다.
        File storage = context.getCacheDir();

        //저장할 파일 이름
        String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";

        //storage 에 파일 인스턴스를 생성합니다.
        File imgFile = new File(storage, fileName);


        try {

            // 자동으로 빈 파일을 생성합니다.
            imgFile.createNewFile();

            // 파일을 쓸 수 있는 스트림을 준비합니다.
            FileOutputStream out = new FileOutputStream(imgFile);

            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            // 스트림 사용후 닫아줍니다.
            out.close();

            return imgFile;

        } catch (FileNotFoundException e) {
            Log.e(TAG + "MyTag","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG + "MyTag","IOException : " + e.getMessage());
        }

        return imgFile;
    }

//    private static void CheckPermission(){
//        // 권한 확인
//        if (ContextCompat.checkSelfPermission(, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            // 권한이 없을 경우, 사용자에게 권한 요청
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//        }
//
//
//
//    }
    // onRequestPermissionsResult 메서드에서 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용됨
                Log.d(TAG + "권한", "onRequestPermissionsResult:  성공");
            } else {
                // 권한 거부됨, 사용자에게 안내
                Log.d(TAG + "권한", "onRequestPermissionsResult:  실패");
                Toast.makeText(
                        getApplicationContext()
                        ,"프로필 이미지를 업로드 하려면 권한에 동의 해야 합니다."
                        ,Toast.LENGTH_SHORT
                ).show();
                finish();
            }
        }
    }

    // 권한 확인 메소드
    private static int CAMERA_PERMISSION_CODE = 100;
    private void getCameraPermission(){
        // 현재 카메라 권한 여부 확인
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            // 권한이 없을 경우 요청
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }else{
            // 권한이 있을 경우 카메라 촬영 진행
            Log.d(TAG + "Check Camera Permission", "Permission Allowed");
            captureImage();
        }
    }

    // 이미지 촬영 메소드
    private void captureImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Uri 생성 (이 예시에서는 임시 파일 사용)
         imageFile = createImageFile();
        Uri photoURI = FileProvider.getUriForFile(this,
                "com.example.ourbook.fileprovider",
                imageFile );

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        launcher_capture.launch(intent);
    }

    /*카메라로 찍은 이미지 파일을 저장 하기위해 , 비어있는 파일을 생성 , 반환 */

    String currentPhotoPath;

    private File createImageFile()  {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.d(TAG + "error ", "createImageFile:  파일 생성 에러 " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



}