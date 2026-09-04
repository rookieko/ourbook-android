package com.example.ourbook.exploreActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Recycle.Adapter.CategoryAdapter;
import com.example.ourbook.DataTool.Response.CategoryDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityCategorySelectBinding;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* 카테고리 리스트 view ,  카테고리 선택 하는 액티비티  선택 이후 -> ExploreActivity 로 이동 */
public class CategorySelectActivity extends AppCompatActivity {

    private ActivityCategorySelectBinding binding;
    private String jwt;
    private UserSingletone userSingletone;

    // 서버

    private RetrofitGenerator retrofitGenerator ;
    private RegisterBookService registerBookService;
    private List<CategoryDTO> categoryDTOList;

    // ui
    private RecyclerView recyclerViewCategory;
    private CategoryAdapter categoryAdapter;
    private int uid;
    public static final String TAG = "CategorySelectActivity" + Constants.AddTAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategorySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //
        userSingletone = UserSingletone.getMyUser();
        Intent intent = getIntent();
        uid = Integer.parseInt(Optional.ofNullable(intent.getStringExtra(Constants.INTENT_UID)).orElse( "0"));
        jwt = Optional.ofNullable(intent.getStringExtra(Constants.INTENT_JWT)).orElse("null!");
        Log.d(TAG, "onCreate: 1. jwt , 2. uid = 1. " + jwt +" 2. "+uid);
        Log.d(TAG, "onCreate: "+ userSingletone.toString());
        // 서버
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        // ui
        recyclerViewCategory = binding.recyclerViewCategoryList;
        binding.mToolBarCategory.setNavigationOnClickListener(v -> {
            finish();
        });
        // 카테고리 정보 가져오기
        Call<List<CategoryDTO>> getCategoryInfo = registerBookService.getCategoryInfo();
        getCategoryInfo.enqueue(new Callback<List<CategoryDTO>>() {
            @Override
            public void onResponse(Call<List<CategoryDTO>> call, Response<List<CategoryDTO>> response) {
                if(response.isSuccessful()){
                    categoryDTOList = response.body();
                    if(categoryDTOList!= null) {
                        generateDataList(categoryDTOList);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CategoryDTO>> call, Throwable t) {
                Log.d(TAG, "레트로핏 응답 실패 onFailure: " +t.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // 리사이클러뷰
    private void generateDataList(List<CategoryDTO> categoryDTOS) {
        categoryAdapter = new CategoryAdapter(this, categoryDTOS);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CategorySelectActivity.this);
        recyclerViewCategory.setLayoutManager(layoutManager);
        recyclerViewCategory.setAdapter(categoryAdapter);
    }
}