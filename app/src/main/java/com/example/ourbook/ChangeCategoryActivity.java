package com.example.ourbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ourbook.DataTool.Response.CategoryDTO;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityChangeCategoryBinding;
import com.example.ourbook.writeActivity.SummaryInputActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeCategoryActivity extends AppCompatActivity {
    private static final String TAG = "ChangeCategoryActivity 카테고리 수정"+ Constants.AddTAG;
    private ActivityChangeCategoryBinding binding;
    private MaterialAlertDialogBuilder dialogBuilder;
    private UserSingletone userSingletone;
    private String jwt;
    private int wid;
    String[] array2;

    // 서버로 부터 응답으로 가져올 Data
    // 값을 저장 , 불러와서 서버로 보내 카테고리 id 로 값을 저장한다.
    HashMap<String, Integer> CategoryMap = new HashMap<>();


    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;




    private int itemSelected = -1;
    private int tempSelected = -1;
    // 요청으로 보낼 Data
    private  int categoryID = -1; // 카테고리 id
    private int intent_categoryID = -1;
    private String categoryName ;
    private  String Title ; //  책의 제목
    private int type = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        wid = intent.getIntExtra(Constants.INTENT_WID,-1);
        // 카테고리 명
        if( wid == -1 ){
            Toast.makeText(this,"잘못된 접근입니다.",Toast.LENGTH_SHORT).show();
            finish();
        }
        categoryName = intent.getStringExtra(Constants.INTENT_CATEGORY_NAME);
        intent_categoryID = intent.getIntExtra(Constants.INTENT_CID,-1);
        Log.d(TAG, "onCreate: wid " + wid + categoryName + " intent_categoryID" +intent_categoryID);

        // 수정 버튼
        binding.buttonUpdateBookInfo.setOnClickListener(v -> {
            updateBookInfo();
        });


        userSingletone = UserSingletone.getMyUser();
        jwt = userSingletone.getUserJWT();
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

        // intent 로 초기 값 설정
        binding.categorySelectShowView.setText(categoryName);

        // 장르 선택
        binding.categorySelectCardView.setOnClickListener(v -> {
            dialogBuilder.setSingleChoiceItems(array2, itemSelected, (dialog, which) -> {
                Log.d(TAG, "onCreate: which setSingleChoiceItems " + which);
                tempSelected = which;
            }).show();

        });
    }// on create
    private void updateBookInfo(){
        Map<String,String> requsetMap = new HashMap<>();
        requsetMap.put("change","category");
        requsetMap.put("category_id", String.valueOf(categoryID));
        Call<NormalResponseDTO> updateBookInfo  = registerBookService.UpdateWebNovelData(jwt,wid,requsetMap);
        updateBookInfo.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                NormalResponseDTO normalResponseDTO = response.body();
                if(normalResponseDTO == null){
                    Toast.makeText(ChangeCategoryActivity.this,"다시 시도 해주세요",Toast.LENGTH_SHORT).show();
                    return;}
                if (normalResponseDTO.isSuccess()){
                    //
                    Toast.makeText(ChangeCategoryActivity.this,"등록 되었습니다. " , Toast.LENGTH_SHORT).show();
                    finish();

                }else {
                    Toast.makeText(ChangeCategoryActivity.this,"실패 다시 시도해주세요",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }
}