package com.example.ourbook.writeActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.CategoryDTO;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityBookInfoSetBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookInfoSetActivity extends AppCompatActivity {
    /* 책 등록을 하면서 제목 중복 확인 이후 카테고리 설정등 상세 설정을 위한 activity */
    ActivityBookInfoSetBinding binding;
    MaterialAlertDialogBuilder dialogBuilder;
    DialogInterface dialog;
    Resources resources;


    private UserSingletone userSingletone;

    // 책의 상세 설정 입력 페이지 이후 책의 등록이 가능하게 설정
    // 입력할 내용은 장르 , 연재 주기  줄거리

    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;


//    ArrayList<String> category_stringList = new ArrayList<>(Arrays.asList("판타지", "SF", "현대 판타지", "역사", "미스터리", "무협", "로맨스", "라이트 노벨", "다크 판타지"));
//    String[] array = {"판타지", "SF", "현대 판타지"};
//    String[] array = {"판타지", "SF", "현대 판타지", "역사", "미스터리", "무협", "로맨스", "라이트 노벨", "다크 판타지"};
    String[] array2;

    // 서버로 부터 응답으로 가져올 Data
    // 값을 저장 , 불러와서 서버로 보내 카테고리 id 로 값을 저장한다.
    HashMap<String, Integer> CategoryMap = new HashMap<>();
    private static final String TAG = " BookInfoSetActivity 카테고리 선택"+ Constants.AddTAG;


    private int itemSelected = -1;
    private int tempSelected = -1;
    // 요청으로 보낼 Data
    private  int categoryID = -1; // 카테고리 id
    private  String Title ; //  책의 제목
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBookInfoSetBinding binding = ActivityBookInfoSetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        Intent intent = getIntent();
        Title  = getIntent().getStringExtra("title");
        if (Title == null){
            Toast.makeText(getApplicationContext()," 오류! 제목을 다시 한번 입력하세요 ", Toast.LENGTH_SHORT).show();
            finish();
        }

        userSingletone = UserSingletone.getMyUser();
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        // 카테고리 정보 가져오기
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        Call<List<CategoryDTO>> getCategoryInfo = registerBookService.getCategoryInfo();
        getCategoryInfo.enqueue(new Callback<List<CategoryDTO>>() {
            @Override
            public void onResponse(Call<List<CategoryDTO>> call, Response<List<CategoryDTO>> response) {

                List<CategoryDTO> categoryDTOList = response.body();
                int size = categoryDTOList.size();
                array2 = new String[size];
                int i = 0;
                for (CategoryDTO category :
                        categoryDTOList) {
                    Log.d(TAG, "onResponse: 카테고리 값 " + String.valueOf(category.name));
                    CategoryMap.put(category.name, category.id);
                    array2[i] = category.name;
                    i++;
                }
//                dialogBuilder.setSingleChoiceItems(array2, itemSelected, (dialog, which) -> {
//                    tempSelected = which;
//                });


            }

            @Override
            public void onFailure(Call<List<CategoryDTO>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


        dialogBuilder.setTitle(R.string.select_category);
        dialogBuilder.setSingleChoiceItems(array2, itemSelected, (dialog, which) -> {
            Log.d(TAG, "onCreate: which setSingleChoiceItems " + which);
            tempSelected = which;
        }).setNeutralButton(R.string.cancel, (dialog, which) -> {
            Log.d(TAG, "onCreate: which setNeutralButton " + which);
        }).setPositiveButton(R.string.agree, (dialog, which) -> {
            Log.d(TAG, "onCreate: which setPositiveButton " + which);
            itemSelected = tempSelected;
            Log.d(TAG, "onCreate: which setPositiveButton itemSelected" + itemSelected);
            Log.d(TAG, "onCreate: which setPositiveButton tempSelected" + tempSelected);

            binding.categorySelectShowView.setText(array2[itemSelected]);
            if (CategoryMap != null) {
                categoryID = CategoryMap.get(String.valueOf(array2[itemSelected]));
                Log.d(TAG, "onCreate: Real category ID " + categoryID);
            }
        });
        // 장르 선택
        binding.categorySelectCardView.setOnClickListener(v -> {
            dialogBuilder.setSingleChoiceItems(array2, itemSelected, (dialog, which) -> {
                Log.d(TAG, "onCreate: which setSingleChoiceItems " + which);
                tempSelected = which;
            }).show();

        });

//        new MaterialAlertDialogBuilder(this)
//                .setTitle(R.string.make_category)


//        dialogBuilder.setNeutralButton(resources.getString(R.string.cancel) , );


        // 책 등록
        binding.makeBookButton.setOnClickListener(v -> {

            Call<NormalResponseDTO> regisWebNovel = registerBookService.WebNovelRegister(userSingletone.getUserJWT(),Title,categoryID,type);

            regisWebNovel.enqueue(new Callback<NormalResponseDTO>() {
                @Override
                public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                    NormalResponseDTO bookRegisResponse = response.body();
                    if (bookRegisResponse!= null){
                        if (bookRegisResponse.isSuccess()){
                            Toast.makeText(getApplicationContext(),"책 등록 성공 ",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: 책 등록 성공 " + bookRegisResponse.getMessage());
                            Intent intent = new Intent(BookInfoSetActivity.this, WriteMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(),"책 등록 실패 다시 시도해주세요",Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<NormalResponseDTO> call, Throwable t) {

                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}