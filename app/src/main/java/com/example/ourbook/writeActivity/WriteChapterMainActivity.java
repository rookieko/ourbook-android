package com.example.ourbook.writeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.Chapter;
import com.example.ourbook.DataTool.Response.ChapterItem;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.DataTool.Writer.W_ChapterAdapter;
import com.example.ourbook.DataTool.Writer.W_ChapterPagerAdapter;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityWriteChapterMainBinding;
import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteChapterMainActivity extends AppCompatActivity {

    //TODO  회차의 수정 삭제

    private ActivityWriteChapterMainBinding binding;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService; // interface for Retrofit
    private UserSingletone userSingletone; // userSingletone , 싱글 톤 주로 jwt 값을 가져 오는데 사용한다.
    private String title; // webnovel 의 타이틀
    private int wid; // wid = webnovel db table 의 id , webnovel 회차 조회 , 수정에 필요

    private RecyclerView recyclerView; // 회차 리스트를 보여주는 웹소설 회차 리사이클러뷰
    private W_ChapterAdapter wChapterAdapter; //리사이클러뷰

    private ViewPager2 viewPager;
    private W_ChapterPagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    private static final String TAG = "WriteChapterMainActivity 챕터 액티비티 "+ Constants.AddTAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteChapterMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userSingletone = UserSingletone.getMyUser();
//        recyclerView = binding.chapterRecyclerView;
        // 리사이클러뷰 아이템 divider 설정
//        MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(this, LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(divider);

        /* view pager , tab layout */
        setViewPager();
        setTabLayout();


        /* view pager , tab layout */
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        wid = intent.getIntExtra("wid",-1);


        //
        if (wid == -1 ){
            Log.d(TAG, "onCreate: wid 값 -1 조건문 탈출 ");
            Toast.makeText(getApplicationContext(),"오류"+ wid, Toast.LENGTH_SHORT).show();
            finish();
        }
        // userSingletone wid 값 설정 .. 각 fragment 에서 웹소설 data 수정 , 요청에 사용
        userSingletone.setWid(wid);
        /* 회차 등록 페이지 */
        binding.addChapterButton.setOnClickListener(v -> {
            Intent intent1= new Intent(WriteChapterMainActivity.this, WriteChapterInputActivity.class);
            intent1.putExtra("wid",wid);
            startActivity(intent1);
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        /* fragment - viewpager - tabLayout 으로 전환 이후 코드 주석 처리  WriteChapterListFragment 로 코드 이동 */
//        Call<ChapterItem> chapterItemCall = registerBookService.getWriteChapterData(userSingletone.getUserJWT(),wid);
//        chapterItemCall.enqueue(new Callback<ChapterItem>() {
//            @Override
//            public void onResponse(Call<ChapterItem> call, Response<ChapterItem> response) {
//                ChapterItem chapterItem = response.body();
//                if (chapterItem == null){
//                    Log.d(TAG, "onResponse: ");
//                    return;}
//                wChapterAdapter = new W_ChapterAdapter( chapterItem.getData(), new W_ChapterAdapter.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(Chapter chapter) {
//
//                    }
//                });
////                recyclerView.setAdapter(wChapterAdapter);
//
//            }
//
//            @Override
//            public void onFailure(Call<ChapterItem> call, Throwable t) {
//
//            }
//        });

    }

    private void setViewPager(){
        viewPager = binding.writerChapterPager;
//        viewPager.setUserInputEnabled(false);
        pagerAdapter = new W_ChapterPagerAdapter(this);
        pagerAdapter.createFragment(0);
        pagerAdapter.createFragment(1);
        pagerAdapter.createFragment(2);
        viewPager.setAdapter(pagerAdapter);
    }

    private void setTabLayout(){

        tabLayout = binding.chapterTabLayout;
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0 ){
                    tab.setText("회차 목록");
                }else if(position ==1 ){
                    tab.setText("임시 저장");
                } else if (position ==2) {
                    tab.setText("설정");
                }
            }
        }
        ).attach();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* 싱글톤  초기화 */
        userSingletone.setLastNum(0);
        userSingletone.setWid(-1);
    }
}