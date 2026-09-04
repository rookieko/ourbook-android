package com.example.ourbook.exploreActivity;

//import static com.example.ourbook.Constants.REQUEST_PAGING_OPTION;
import static com.example.ourbook.Constants.Paging.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Book;
import com.example.ourbook.DataTool.Recycle.Adapter.ExploreBookAdapter;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityExploreBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* 카테고리 선택 이후 webNovel List 를 보여주는 액티비티 , 책 선택 이후 bookInfo Activity 로 이동 */
public class ExploreActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private final String TAG = " ExploreActivity " + Constants.AddTAG;
    private ActivityExploreBinding binding;
    private int category_id ;
    private String category_name;
    private RecyclerView book_recyclerView;
    private ExploreBookAdapter bookAdapter;

    private RetrofitGenerator retrofitGenerator;
    private UserSingletone userSingletone;
    private RegisterBookService registerBookService;
    private Map<String,Object> requestMap;
    private List<Book> resultBook = new ArrayList<>();
    private int option = Constants.OPTION_Popular; //  0 = 인기순 , 1  = 최신순 , 2 = 등록순 , 3 = 선호 작품 등록순 ?
    private int page  = 0 ; // 페이지 번호


    // 마지막으로 추가한 숫자를 저장할 변수
    int lastNum;
    Book lastBook;
    private boolean isLoading = false;
    private boolean isPageEnd = false;

    // 연속 클릭 방지 시간 측정용 변수
    private Long mLastClickTime = 0L;


    /* 탐색 페이지 option 과 cid , offset page 정보를 서버로 전달한다 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExploreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        category_id = intent.getIntExtra(Constants.INTENT_CID,-1);
        category_name = intent.getStringExtra(Constants.INTENT_CATEGORY_NAME);
        if(category_id == -1 ){
            Log.d(TAG, "에러 onCreate: 인텐트 값 전달 실패 category id = -1 ");
            finish();
        }else {
            Log.d(TAG, "성공 onCreate:category_id =  "+category_id + " category_name =" +category_name);
        }
        // 변수 초기화
        userSingletone = UserSingletone.getMyUser(); //  jwt
        retrofitGenerator = new RetrofitGenerator(); // 레트로핏 서버
        binding.mToolBarCategoryBook.setTitle(category_name); // title bar 텍스트 설정
        binding.mToolBarCategoryBook.setNavigationOnClickListener(v -> {
            finish();
        });
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);

//        bookAdapter = new ExploreBookAdapter(resultBook, this);
        book_recyclerView = binding.recyclerViewBookList;
        requestMap = new HashMap<>();
        requestMap.put("cid",category_id);
        // 초기 옵션 설정 값
        binding.buttonArrayOption.setText(R.string.first);
        option = 1;

        // 레이아웃 버튼 메뉴


        /*리사이클러뷰 무한 스크롤*/
//        book_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                if(!recyclerView.canScrollVertically(1)){
//                    Log.d(TAG, "onScrolled: 스크롤 끝 도달 ");
//                }
////                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
////                int totalItemCount = layoutManager.getItemCount();
////                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//
//                // 스크롤이 끝에 도달했는지 확인 (마지막으로 보이는 아이템의 위치가 아이템 총 개수 -1 보다 크거나 같을 때)
//
////                    if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 1)) {
////                        // 여기서 데이터 로딩 로직을 실행
////                        Log.d(TAG, "onScrolled: page 증가 " + page);
////                        page++; // 다음 페이지 번호 증가
////                        loadMoreData(); // 데이터 로딩 메서드 호출
////                        isLoading = true; // 로딩 상태 true로 설정
////                    }
//
//            }
//
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                //  최상단 또는 최하단에 있는 상태일 경우 실행
////                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
////                    Log.d(TAG, "onScrollStateChanged: 최 하단 ");
////                    // 최하단일 경우 실행
////                    if (recyclerView.canScrollVertically(1)) {
////                        // 데이터를 추가하는 코드를 실행한지 1초가 지났으면 실행 (Loading Progress Bar를 보여주기 위해 코드 실행을 멈추는 Thread.sleep를 사용해서 임의로 추가)
////                        if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
////                            mLastClickTime = SystemClock.elapsedRealtime(); // 코드 실행시 현재 시간 저장
////
////                        Log.d(TAG, "onScrolled: page 증가 " + page);
////                        page++; // 다음 페이지 번호 증가
////                        loadMoreData(); // 데이터 로딩 메서드 호출
////                        isLoading = true; // 로딩 상태 true로 설정
////
////                        }
////
////                    }
////                }
//            }
//        });
        /*nestedScrollView 새로고침 , swipeRefresh*/
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(ExploreActivity.this, "refresh 성공 " , Toast.LENGTH_SHORT).show();;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefreshLayout.setRefreshing(false);

                }
            },600);
            //refresh 종료
//            binding.swipeRefreshLayout.setRefreshing(false);
        });

//        /*nestedScrollView 무한 스크롤*/
        binding.scrollViewExplore.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener)
                        (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//                Log.d(TAG, "onScrollChange:  스크롤 중 "+ "scrollX : " + scrollX + " scrollY : " + scrollY);
//                Log.d(TAG, "onScrollChange: oldScrollX = "+ oldScrollX+" oldScrollY = "+oldScrollY );

            if (!v.canScrollVertically(1)){ // 스크롤 불가능 , item end
                Log.d(TAG, "onScrollChange: canScrollVertically(1) false 스크롤 불가능 " );
                page++;
                changePage(option,page);
            }else {
//                    Log.d(TAG, "onScrollChange: canScrollVertically(1) false ");
            }

        });
    }// onCreate
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.array_option_menu, popup.getMenu());
        popup.show();
    }
    // 데이터 로딩 메서드
    private void loadMoreData() {
        // 로딩바 표시 로직 (예: ProgressBar를 보이게 설정)
        // Retrofit 등을 사용하여 데이터 로딩
        // 데이터 로딩 성공 후:
        isLoading = false; // 로딩 상태 false로 설정
        // 어댑터에 데이터 추가 및 notifyDataSetChanged 호출로 리사이클러뷰 업데이트
        changePage(option,page);

        bookAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 서버 요청 응답  , book data 가져오기
        requestMap.put(REQUEST_PAGING_PAGE,page); // 페이지 번호
        requestMap.put(REQUEST_PAGING_OPTION,option); // option  0 = 인기 , 1= 최신 , 2= 등록순
        Call<List<Book>> getBookItem = registerBookService.getBookItemList(userSingletone.getUserJWT(),requestMap);
        getBookItem.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                Log.d(TAG, "onResponse: "+response);
                if(response.code() == 201){
                    // 결과물이 없을 때 
                    Log.d(TAG, "onResponse: 아이템 결과물이 0 , status code "+ response.code());
                }
                else if (response.isSuccessful()){
                    resultBook = response.body();
//                    bookAdapter.putData(resultBook);
                    generateDataList(resultBook);

                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.old_option) {
            binding.buttonArrayOption.setText(R.string.old);
            option = Constants.OPTION_Old; // 등록순
            page =  0; // 옵션 변경에 따라 page 초기화
            changeOption(option,page);

            return true;
        } else if (itemId == R.id.new_option) {
            binding.buttonArrayOption.setText(R.string.first);
            option = Constants.OPTION_New; // 최신순
            page =  0;// 옵션 변경에 따라 page 초기화

            changeOption(option,page);


            return true;
        } else if ( itemId == R.id.popular_option) {
            binding.buttonArrayOption.setText(R.string.popular);
            option = Constants.OPTION_Popular; // 인기순
            page =  0;// 옵션 변경에 따라 page 초기화

            changeOption(option,page);


            return true;
        }
        return false;
    }

    /* option 변경에 따른 변화 , book item 을 가져오는 메서드 */
    private void changeOption(int option , int page ){

        requestMap.put(REQUEST_PAGING_PAGE,page); // 페이지 번호
        requestMap.put(REQUEST_PAGING_OPTION,option); // option  0 = 인기 , 1= 최신 , 2= 등록순
        Call<List<Book>> getBookItem = registerBookService.getBookItemList(userSingletone.getUserJWT(),requestMap);
        getBookItem.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
//                Log.d(TAG, "onResponse: "+response);
                if(response.code() == 201){
                    // 결과물이 없을 때
                    Log.d(TAG, "onResponse: 아이템 결과물이 0 , status code "+ response.code());

                }
                else if (response.isSuccessful()){
                    Log.d(TAG, "onResponse changeOption : 성공  , status code "+ response.code());

                    resultBook = response.body();
                    generateDataList(resultBook);
                    bookAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.d(TAG, "onFailure: error "+ t.getMessage());
            }
        });
    }

    private void changePage(int option , int page){
        requestMap.put(REQUEST_PAGING_PAGE,page); // 페이지 번호
        requestMap.put(REQUEST_PAGING_OPTION,option); // option  0 = 인기 , 1= 최신 , 2= 등록순
        Call<List<Book>> getBookItem = registerBookService.getBookItemList(userSingletone.getUserJWT(),requestMap);
        getBookItem.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
//                Log.d(TAG, "onResponse: "+response);
                if(response.code() == 201){
                    // 결과물이 없을 때
                    Log.d(TAG, "onResponse: 아이템 결과물이 0 , status code "+ response.code());

                }
                else if (response.isSuccessful()){
                    Log.d(TAG, "onResponse changeOption : 성공  , status code "+ response.code());

                    List<Book> addResult = response.body();
                    if(addResult == null){return;}
//                    generateDataList(resultBook);
//                    resultBook.addAll(addResult);
                    bookAdapter.putData(addResult);
                    isLoading = false;

                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.d(TAG, "onFailure: error "+ t.getMessage());
            }
        });
    }
    /* 리사이클러뷰 생성 */
    private void generateDataList(List<Book> resultBooks) {
        bookAdapter = new ExploreBookAdapter( resultBooks,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ExploreActivity.this);
        book_recyclerView.setLayoutManager(layoutManager);
        book_recyclerView.setAdapter(bookAdapter);


//        book_recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if(v.canScrollVertically(1)){
//                    Log.d(TAG, "onScrollChange: 스크롤 끝 도달 ");
//                }
//
//            }
//        });
        /* test 해보니  recyclerView 밖에 nested ScrollView 를 사용한 것이 문제인거 같음 */
//        book_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                Log.d(TAG, "onScrolled:  스크롤 중 "+ "dx : " + dx + " dy : " + dy);
//                if (recyclerView.canScrollVertically(1)){
//                    Log.d(TAG, "onScrolled: canScrollVertically(1) true " );
//                }else {
//                    Log.d(TAG, "onScrolled: canScrollVertically(1) false ");
//                }
//            }
//        });
    }


}