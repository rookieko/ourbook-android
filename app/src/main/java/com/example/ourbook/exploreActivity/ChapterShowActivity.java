package com.example.ourbook.exploreActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Book;
import com.example.ourbook.DataTool.Recycle.Adapter.ChapterShowAdapter;
import com.example.ourbook.DataTool.Recycle.Adapter.ExploreBookAdapter;
import com.example.ourbook.DataTool.Response.Chapter;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityChapterShowBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterShowActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    /* 책을 선택 -> 책 정보 화면 -> 회차 <더보기> 선택 -> 현재 화면*/
    private ActivityChapterShowBinding binding;
    private static final String TAG = "ChapterShowActivity"+ Constants.AddTAG;
    private int wid;
    private String jwt;
    private int option = 1;   // option  0 = 인기 , 1= 최신 , 2= 등록순
    private  int page = 0;
    private RegisterBookService registerBookService;
    private RetrofitGenerator retrofitGenerator;
    private Map<String,Object> requestMap = new HashMap<>(); // 요청
    private List<Chapter> resultChapter ; //응답
    private RecyclerView chapter_recyclerView;
    private ChapterShowAdapter chapterAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChapterShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        wid = intent.getIntExtra(Constants.INTENT_WID,-1);
        jwt = intent.getStringExtra(Constants.INTENT_JWT);

        if(wid==-1 ){
            Log.d(TAG, "onCreate: 에러 wid 값 -1  intent 오류 ");
            finish();
        }else {
            Log.d(TAG, "intent 값 확인 onCreate: wid =  "+wid +" jwt = " + jwt);
        }
        requestMap.put("wid",wid);
        chapter_recyclerView = binding.recyclerViewChapterList;
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        binding.mToolBarBookInfo.setNavigationOnClickListener(v -> {
            finish();
        });
        binding.scrollViewChapter.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener)
                        (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if(!v.canScrollVertically(1)){
                        Log.d(TAG, "onCreate scrollViewChapter : 스크롤 불가능 ");
                        page++;
                        changePage(option,page);
                        chapterAdapter.notifyDataSetChanged();
                    }

        });
    }//onCreate()

    @Override
    protected void onResume() {
        super.onResume();
        changeOption(option,page);
    }

    private void generateDataList(List<Chapter> resultChapter) {
        chapterAdapter = new ChapterShowAdapter( resultChapter,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChapterShowActivity.this);
        chapter_recyclerView.setLayoutManager(layoutManager);
        chapter_recyclerView.setAdapter(chapterAdapter);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.old_option_chapter) {
            binding.buttonArrayOption1.setText(R.string.old);
            option = 2; // 등록순
            page= 0;
            changeOption(option,page);

            return true;
        } else if (itemId == R.id.new_option_chapter) {
            binding.buttonArrayOption1.setText(R.string.first);
            option = 1; // 최신순
            page= 0;

            changeOption(option,page);


            return true;
        } else if ( itemId == R.id.popular_option) {
            binding.buttonArrayOption1.setText(R.string.popular);
            option = 0; // 인기순
            page= 0;

            changeOption(option,page);


            return true;
        }
        return false;

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.array_option_chapter_menu, popup.getMenu());
        popup.show();
    }

    /* option 변경에 따른 변화 , book item 을 가져오는 메서드 */
    private void changeOption(int option , int page ){
        requestMap.put("page",page); // 페이지 번호
        requestMap.put("option",option); // option  0 = 인기 , 1= 최신 , 2= 등록순
        Call<List<Chapter>> getChapterItem = registerBookService.getChapterItemList(jwt,requestMap);
        getChapterItem.enqueue(new Callback<List<Chapter>>() {
            @Override
            public void onResponse(Call<List<Chapter>> call, Response<List<Chapter>> response) {
//                Log.d(TAG, "onResponse: "+response);
                if(response.code() == 201){
                    // 결과물이 없을 때
                    Log.d(TAG, "onResponse: 아이템 결과물이 0 , status code "+ response.code());

                }
                else if (response.isSuccessful()){
                    Log.d(TAG, "onResponse changeOption : 성공  , status code "+ response.code());

                    resultChapter = response.body();
                    generateDataList(resultChapter);

                }
            }

            @Override
            public void onFailure(Call<List<Chapter>> call, Throwable t) {
                Log.d(TAG, "onFailure: error "+ t.getMessage());
            }
        });
    }
    private void changePage(int option, int page){
        requestMap.put("page",page); // 페이지 번호
        requestMap.put("option",option); // option  0 = 인기 , 1= 최신 , 2= 등록순
        Call<List<Chapter>> getChapterItem = registerBookService.getChapterItemList(jwt,requestMap);
        getChapterItem.enqueue(new Callback<List<Chapter>>() {
            @Override
            public void onResponse(Call<List<Chapter>> call, Response<List<Chapter>> response) {
//                Log.d(TAG, "onResponse: "+response);
                if(response.code() == 201){
                    // 결과물이 없을 때
                    Log.d(TAG, "onResponse: 아이템 결과물이 0 , status code "+ response.code());

                }
                else if (response.isSuccessful()){
                    Log.d(TAG, "onResponse changeOption : 성공  , status code "+ response.code());

                    List<Chapter> addResult = response.body();
                    if(addResult == null){return;}
//                    generateDataList(resultChapter);
                    chapterAdapter.putData(addResult);

                }
            }

            @Override
            public void onFailure(Call<List<Chapter>> call, Throwable t) {
                Log.d(TAG, "onFailure: error "+ t.getMessage());
            }
        });
    }


}