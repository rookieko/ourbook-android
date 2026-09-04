package com.example.ourbook.writeActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourbook.ChangeCategoryActivity;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.BaseUrl;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.Service.UploadAPI;
import com.example.ourbook.DataTool.UserSharedHelper;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.setBookActivity.BookImageInputActivity;
import com.example.ourbook.setBookActivity.ChangeBookTitleActivity;
import com.example.ourbook.databinding.FragmentWriteChapterSettingBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WriteChapterSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WriteChapterSettingFragment extends Fragment {
    /* My */
    //
    private File imageFile;
    private static final String TAG = "WriteChapterSettingFragment"+ Constants.AddTAG;
    public static final String Message = " ourbook_rookie ";
    //초기화
    private FragmentWriteChapterSettingBinding binding; // view binding
    private UserSingletone userSingletone; // 유저- 싱글톤 , jwt , wid 참조
    private RetrofitGenerator retrofitGenerator;
    private UploadAPI uploadAPI;
    private RegisterBookService registerBookService;
    private PopupMenu popupMenu;
    private String coverImage;
    private String title;
    private String category;
    private int category_id;
    private String summary;
    private int wid;


    /* 엘범 사진 업로드 */
    private ActivityResultLauncher<String> getImagePick;
    private ActivityResultLauncher<Uri> getTakePicture;
    private ActivityResultLauncher<String[]> requestPermissionLauncher; // 권한
    private static final int PICK_IMAGE_REQUEST = 1;  // 한 장

    /* --------------------------------------------------------   */
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WriteChapterSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WriteChapterSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WriteChapterSettingFragment newInstance(String param1, String param2) {
        WriteChapterSettingFragment fragment = new WriteChapterSettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 초기화
        userSingletone = UserSingletone.getMyUser();
        retrofitGenerator  = new RetrofitGenerator();
        // 임시 wid
        wid = userSingletone.getWid();
        // 이미지 - 북커버 업로드 서비스
        uploadAPI = retrofitGenerator.init_user_retrofit(UploadAPI.class);
        // 책 데이터 - 등록 , 수정 서비스
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        //권한 초기화
        //권한 설정 , ActivityResultLauncher 사용
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                result->{
                    Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                    Boolean readExternalStorageGranted = result.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);

                    if (cameraGranted != null && cameraGranted && readExternalStorageGranted != null && readExternalStorageGranted) {
                        // 권한이 모두 승인된 경우
                        Toast.makeText(
                                getActivity()
                                ,"권한 동의 성공"
                                ,Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        // 권한이 거부되었거나 일부만 승인된 경우
                        Log.d("권한", "onRequestPermissionsResult:  실패");
                        Toast.makeText(
                                getActivity()
                                ,"프로필 이미지를 업로드 하려면 권한에 동의 해야 합니다."
                                ,Toast.LENGTH_SHORT
                        ).show();
                        popupMenu.dismiss(); // popupmenu
                        return; // 권한 실패에 대한 설정을 아직 못함

                    }
                });
//        // 권한 사진 , 저장 공간 읽기 권한 요구 실행
//        requestPermissionLauncher.launch(new String[] {
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA
//        });

        // 폐쇄 ActivityResultLauncher<PickVisualMediaRequest> pickMedia  로 대체 원인 찾기
        getImagePick = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            Log.d(TAG, "onActivityResult: 사용 되면 안되는 메서드 확인 ");
                            // Uri 처리
                            String imagePath = getPathFromUri(uri);
                            Log.d(TAG + "이미지 업로드 이전 추가 액티비티", "onActivityResult: " + new File(imagePath).canRead());
                            String showImagePath = uri.toString();

                            Intent intent = new Intent(getActivity(), BookImageInputActivity.class);
                            intent.putExtra("imagePath", imagePath);
                            intent.putExtra("setImagePath", showImagePath);
                            startActivity(intent);
                        }
                    }
                });


    }// onCreate

    // 이미지 선택 수정
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "사진 업로드 register Selected URI: " + uri);
                    // Uri 처리
                    String imagePath = getPathFromUri(uri);
                    //!! 문제 api 버전 탓인지 photoPicker 로 이미지를 가져오지 못하는 경우 contentResolver 을
                    //통해서 inputstream 으로 파일을 읽어서 bitmap 으로  가져오고  bitmap 을 outputstream을
                    //사용하여 저장 한다 , 저장 장소는 따로 디렉토리 권한이 필요한지는 모르겠지만 cache 디렉토리에 저장
                    //하여 사용한다.. 유용한 정보 인거 같음 캐시 디렉토리
                    /*test */


                    File imgfile = saveBitmapToJpeg(getBitmapFromUri(uri),getActivity());
                    Log.d(TAG, "사진 업로드 image: "+imgfile.getPath() );
                    imagePath = imgfile.getPath();
                    /*test */
//                    Log.d(TAG + "이미지 업로드 이전 추가 액티비티", "onActivityResult: " + new File(imagePath).canRead());
                    String showImagePath = uri.toString();

                    Intent intent = new Intent(getActivity(), BookImageInputActivity.class);
                    intent.putExtra("imagePath", imagePath);
                    intent.putExtra("setImagePath", showImagePath);
                    startActivity(intent);

                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWriteChapterSettingBinding.inflate(inflater,container,false);
        //기능 1 )  이미지 업로드 버튼 클릭 TODO
        // 1. 사진 , 2. 앨범 업로드 ~> 사용자 프로필 이미지 업로드 코드 응용
        // popup menu 설정
         popupMenu = new PopupMenu(getActivity(),binding.buttonCoverImageUpload);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int id =item.getItemId();
            if (id == R.id.use_camera){
                captureImage();
                return true;
            }else if (id == R.id.use_gallery) { // 앨범에서 불러오기
                // 기존 프로필 이미지 업로드 방식의 onStartActivityResult 가 아닌 register~ 를 사용 했음
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, PICK_IMAGE_REQUEST);
//                getImagePick.launch("image/*"); //

                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());

                return true;
            }
            return false;
        });

        // 이미지 업로드 -> popupmenu 카메라 , 앨범 업로드 선택
        binding.buttonCoverImageUpload.setOnClickListener(v -> {
            // 권한 사진 , 저장 공간 읽기 권한 요구 실행
            requestPermissionLauncher.launch(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            });
            //TODO 권한 을 다시 불러와서 동의 하지 않은 경우 return 하게 설정

            //
            popupMenu.show();
        });

        /* 제목 변경 */
        binding.changeTitleLayout.setOnClickListener(v -> {
//            Log.d(TAG, "onCreateView: click ");
            Intent intent = new Intent(getActivity(), ChangeBookTitleActivity.class);
            intent.putExtra("title",title);
            startActivity(intent);
        });
        /* 책 삭제 */
        binding.deleteBook.setOnClickListener(v -> {
            BookDataDeleteDialog();
        });
        /* 줄거리 변경*/
        binding.changeSummary.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SummaryInputActivity.class);
            intent.putExtra(Constants.INTENT_SUMMARY,summary);
            intent.putExtra(Constants.INTENT_WID,wid);

            startActivity(intent);
        });
        /* 카테고리 변경*/
        binding.changeCategory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangeCategoryActivity.class);
            intent.putExtra(Constants.INTENT_CID,category_id);
            intent.putExtra(Constants.INTENT_WID,wid);
            intent.putExtra(Constants.INTENT_CATEGORY_NAME,category);
            startActivity(intent);
        });
        /**/
//        binding.changeType.setVisibility(View.INVISIBLE);

        return binding.getRoot();
    }// onCreateView



    // 1-1) 이미지 촬영 메소드
    private void captureImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Uri 생성 (이 예시에서는 임시 파일 사용)
        imageFile = createImageFile();
        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                "com.example.ourbook.fileprovider",
                imageFile );

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        launcher_capture.launch(intent);

    }
    // 1- 1) 이미지 저장
    String currentPhotoPath;
    private File createImageFile()  {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
    // 1-1) 사진 intent result

    private ActivityResultLauncher<Intent> launcher_capture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG + "launcher_capture Callback", "image capturing is succeed");
                        Intent intent = new Intent(getActivity(), BookImageInputActivity.class);
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
            }
    );
    // 1-2 )  uri 에서 경로를 가져오는 메소드
    private String getPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }


    @Override
    public void onResume() {
        super.onResume();
        Call<NormalResponseDTO> bookDataCall = registerBookService.getWebNovelData(userSingletone.getUserJWT(),userSingletone.getWid());
        bookDataCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                NormalResponseDTO normalResponseDTO = response.body();
                if(normalResponseDTO == null){ return;}
                if (normalResponseDTO.isSuccess()){
                    Map<String,Object> data = normalResponseDTO.getData();
                    coverImage = String.valueOf(data.get("coverImage"));
                    Log.d(TAG, "onResponse: "+coverImage);
                    // 이미지 설정
                    if(coverImage != null && coverImage.length()>5) {
                        Glide.with(WriteChapterSettingFragment.this).load(BaseUrl.BookCoverImage_URL +coverImage).into(binding.imageViewBookCover);
                    }
                    category = (String)data.get("category");
                    category_id = Integer.parseInt((String) data.get("category_id"));
                    title =  (String)data.get("title");
                    summary = (String)data.get("summary");

                    // TODO
                    binding.nowType.setText("소설");
                    //
                    binding.nowCategory.setText((String)data.get("category"));
                    binding.nowTitle.setText((String)data.get("title"));
                    binding.nowSummary.setText(summary);
//                    binding.nowCategory.setText();
                }else {
                    Log.e(TAG, "onResponse: fail "+ normalResponseDTO.getMessage() );
                }


            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {

            }
        });
    }
    /*눼 고겅 갇베 확인 다이얼로그 */
    private void BookDataDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("웹소설 삭제 ");
        builder.setMessage("주의 관련 정보가 삭제됩니다\n" +
                "삭제하시겠습니까?");

        builder.setPositiveButton("삭제하기 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                UserSharedHelper.remove(UserSharedHelper.KEY_LOGIN_TOKEN);
                Map<String,String> fieldMap = new HashMap<>();
                fieldMap.put("change","delete");

                Call<NormalResponseDTO> deleteUserData = registerBookService.UpdateWebNovelData(userSingletone.getUserJWT(),userSingletone.getWid(),fieldMap);
                deleteUserData.enqueue(new Callback<NormalResponseDTO>() {
                    @Override
                    public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {

                        if (response.body() == null){
                            return;
                        }
                        NormalResponseDTO deleteUserResponse = response.body();
                        if (deleteUserResponse.isSuccess()){
                            //
                            getActivity().finish();
                            Toast.makeText(getActivity(),"삭제 되었습니다. " , Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<NormalResponseDTO> call, Throwable t) {

                    }
                });



            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*test*/
    private InputStream getInputStreamFromUri(Uri uri) {
        try {
            // ContentResolver를 통해 Uri에 대한 InputStream을 얻습니다.
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            return inputStream;
        } catch (FileNotFoundException e) {
            // 파일을 찾을 수 없는 경우, 예외 처리
            e.printStackTrace();
            return null;
        }
    }

    // 예를 들어, 이미지 파일을 비트맵으로 로드하는 경우:
    private Bitmap getBitmapFromUri(Uri uri) {
        InputStream inputStream = getInputStreamFromUri(uri);
        if (inputStream != null) {
            // InputStream으로부터 Bitmap을 생성합니다.
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
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
            Log.e("MyTag","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("MyTag","IOException : " + e.getMessage());
        }

        return imgFile;
    }


    /*test*/
}