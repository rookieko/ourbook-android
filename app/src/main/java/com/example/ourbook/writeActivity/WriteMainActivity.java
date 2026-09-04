package com.example.ourbook.writeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Book;
import com.example.ourbook.DataTool.Response.writerResponse;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSharedHelper;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.DataTool.Writer.W_BookAdapter;
import com.example.ourbook.databinding.ActivityWriteMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 글 쓰기 버튼 선택이후 자신이 작성하고 있는 책의 정보를 보여주는 액티비티
// RecyclerView에서 책의 간략한 정보를 보여주고 , 책 추가 버튼이 보인다 , 수정 기능 추가 예정
public class WriteMainActivity extends AppCompatActivity {

    private static final String TAG = "WriteMainActivity"+ Constants.AddTAG;
    private ActivityWriteMainBinding binding;
    private RecyclerView recyclerView;
    private W_BookAdapter wBookAdapter;
    private UserSingletone userSingletone ;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private String jwtToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userSingletone = UserSingletone.getMyUser();
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        jwtToken = userSingletone.getUserJWT();

         recyclerView = binding.bookListView;
         // 리사이클러뷰 아이템 divider 설정
//        MaterialDividerItemDecoration divider = new MaterialDividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.addItemDecoration(divider);
        // 데이터를 가져오고 RecyclerView를 업데이트하는 메소드 호출
//        fetchNovelDataAndSetupRecyclerView();
        binding.createBookButton.setOnClickListener(view -> {
            Intent intent = new Intent(WriteMainActivity.this, BookTitleInputActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });


//        wBookAdapter = new W_BookAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 싱글톤 데이터 존재 확인 , 재할당
        checkRepairJWT(userSingletone);
        // null 예외 처리
        if(userSingletone.getUserJWT() == null){return;}
        // 서버 요청 데이터 가져오기
        Call<writerResponse> writerResponseCall = registerBookService.getWriterBookList(userSingletone.getUserJWT());
        writerResponseCall.enqueue(new Callback<com.example.ourbook.DataTool.Response.writerResponse>() {
            @Override
            public void onResponse(Call<com.example.ourbook.DataTool.Response.writerResponse> call, Response<com.example.ourbook.DataTool.Response.writerResponse> response) {
                writerResponse wResponse = response.body();
                if (wResponse == null) { return;}
                wBookAdapter = new W_BookAdapter(WriteMainActivity.this,wResponse.getData().getBookList(), new W_BookAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(Book book) {
//                        Log.d(TAG, "onItemClick: click listener");
//                        Toast.makeText(getApplicationContext(),book.getTitle() + book.getId(),Toast.LENGTH_SHORT).show(); // 확인 ok
                        Intent intent = new Intent(WriteMainActivity.this,WriteChapterMainActivity.class);


                        intent.putExtra("wid",book.getId());
                        intent.putExtra("title",book.getTitle());

                        startActivity(intent);
                        // TODo
                    }
                });
                recyclerView.setAdapter(wBookAdapter);

            }

            @Override
            public void onFailure(Call<com.example.ourbook.DataTool.Response.writerResponse> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // writerChapterMain 종료로 userSingletone . webNovel id  초기화
        userSingletone.setWid(-1);
    }

    /*jwt null 오류에 대한 대첵 , runtime 오류 등 객체 값이 날라 갔을 때 다시 초기화 */
    private void checkRepairJWT(UserSingletone userSingletone){
        if( userSingletone.getUserJWT() == null){
            Log.e("checkRepairJWT", "onResume: mainLogin token null 로 shared 다시 초기화 " );
            UserSharedHelper.init(getApplicationContext());
            String Token = UserSharedHelper.read(UserSharedHelper.KEY_LOGIN_TOKEN,null);
            if(Token == null){ // JWT 토큰이 존재 하지 않을 때 -> MainActivity 로 이동
                return;
            }else if (Token.length() > 10){ // JWT Token 이 존재 할 때 ,  유효성 검증 필요성
                Log.e("checkRepairJWT", "checkRepairJWT: 토큰 입력 성공" + Token);
                userSingletone.setUserJWT(Token);
            }else {
                Log.e("checkRepairJWT", "checkRepairJWT: JWT TOKENT SHARED 에 저장 되지 않았 습니다." );
            }
        }
    }}