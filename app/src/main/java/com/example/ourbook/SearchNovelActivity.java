package com.example.ourbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.example.ourbook.DataTool.Book;
import com.example.ourbook.DataTool.Recycle.Adapter.ExploreBookAdapter;
import com.example.ourbook.DataTool.Recycle.Adapter.SearchBookAdapter;
import com.example.ourbook.DataTool.Response.DTO.SearchItemDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivitySearchNovelBinding;
import com.example.ourbook.exploreActivity.ExploreActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchNovelActivity extends AppCompatActivity {
    /* 채팅방 생성과 , 웹소설 정보 불러오기를 위한 검색 Activity  Option 을 사용하여 구분 */
    private static final String TAG = "SearchNovelActivity "+ Constants.AddTAG;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private List<SearchItemDTO> resultSearchItemList;
    private SearchBookAdapter searchBookAdapter;
    private RecyclerView recyclerView;
    private String jwt;
    private UserSingletone userSingletone;
    private Intent mIntent;
    private int option; // 0 = main 에서의 검색 , 1 = 채팅방 생성을 위한 검색

    private ActivitySearchNovelBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchNovelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        retrofitGenerator = new RetrofitGenerator();
        // option 불러오기
        mIntent = getIntent();
        Log.d(TAG, "onCreate: intent option 존재 확인  " + mIntent.hasExtra("option"));
        option = mIntent.getIntExtra("option",0);
        Log.d(TAG, "onCreate: 검색 option " + option);
        /* toolbar 뒤로가기 버튼 */
        binding.toolbar.setNavigationOnClickListener(v -> {finish();});
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        userSingletone = UserSingletone.getMyUser();
        jwt = userSingletone.getUserJWT();
        recyclerView = binding.searchRecyclerView;
        initSearchView();
    }
    private void initSearchView(){
        binding.searchView.setSubmitButtonEnabled(true);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: query"+ query);
                binding.searchView.clearFocus();
                searchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: newText" + newText);
                return true;
            }
        });
    }

    private void searchQuery(String query){
        Call<List<SearchItemDTO>> searchItemDTOCall = registerBookService.getSearchItem(jwt,query);
        searchItemDTOCall.enqueue(new Callback<List<SearchItemDTO>>() {
            @Override
            public void onResponse(Call<List<SearchItemDTO>> call, Response<List<SearchItemDTO>> response) {
                resultSearchItemList = response.body();
                if( response.isSuccessful()){
//                    resultSearchItemList.get(0);
                    if(resultSearchItemList == null ){ return;}
                    if(resultSearchItemList.size() == 0){
                        binding.textViewSearchResult.setText(" 결과물이 없습니다.");
                        generateDataList(resultSearchItemList);
                        searchBookAdapter.notifyDataSetChanged();
                        return;
                    }else {
                        String search_result = resultSearchItemList.size() + " 개의 검색 결과가 있습니다.";
                        binding.textViewSearchResult.setText(search_result);
                    }
                    Log.d(TAG, "onResponse: resultSearchItemList.get(0); "+ resultSearchItemList.get(0).getCategory_name());
                    Log.d(TAG, "onResponse: size "+ resultSearchItemList.size());
                    generateDataList(resultSearchItemList);
                    searchBookAdapter.notifyDataSetChanged();
                }else {
                    Log.d(TAG, "onResponse:  is fail");
                }
            }

            @Override
            public void onFailure(Call<List<SearchItemDTO>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage() );
            }
        });
    };
    /* 리사이클러뷰 생성 */
    private void generateDataList(List<SearchItemDTO> resultBooks) {
        searchBookAdapter = new SearchBookAdapter( resultBooks,this , option , this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchNovelActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchBookAdapter);
    }
}