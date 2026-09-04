package com.example.ourbook.exploreActivity;

import static android.widget.LinearLayout.VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.ourbook.DataTool.Recycle.Adapter.CommentShowAdapter;
import com.example.ourbook.DataTool.Response.CommentDTO;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.Service.ReviewService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityCommentShowBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentShowActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    /**
     * 하나의 웹소설에 등록된 리뷰 data list 를 보여주는 activity
     * review, comment 들을 보여주는 activity 설계 미스로 review ,  comment 를 혼용해서 사용하고 있음
     * 처음에 필요한  data 리뷰 점수 통계 점수
     * 현재 data  : ( wid , uid  )
     * 요청 data  : ( )
     * 응답 data  : ( storyScore , characterScore , worldScore 들의 각각의 평균 점수  )
     */
    private ActivityCommentShowBinding binding;
    private static final String TAG = "CommentShowActivity" + Constants.AddTAG;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private ReviewService reviewService;
    private UserSingletone userSingletone;
    private RecyclerView comment_recyclerView;
    private int wid;
    private String jwt;
    private int option = 1;   // option  0 = 인기 , 1= 최신 , 2= 등록순
    private int page = 0;
    public CommentShowAdapter commentShowAdapter;
    private Map<String, Object> requestMap = new HashMap<>(); // 요청

    //응답
    private List<CommentDTO> resultComment; // 응답
    private String review_count;
    private float story_score;
    private float character_score;
    private float world_score;
    private float quality_score;
    private float update_score;
    private float total_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        wid = intent.getIntExtra(Constants.INTENT_WID, -1);
        jwt = intent.getStringExtra(Constants.INTENT_JWT);
        if (wid == -1) {
            Log.d(TAG, "onCreate: 에러 wid 값 -1  intent 오류 ");
            finish();
        } else {
            Log.d(TAG, "intent 값 확인 onCreate: wid =  " + wid + " jwt = " + jwt);
        }
        requestMap.put("wid", wid);
        userSingletone = UserSingletone.getMyUser();
        jwt = userSingletone.getUserJWT();
        comment_recyclerView = binding.recyclerViewCommentList;
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        reviewService = retrofitGenerator.init_user_retrofit(ReviewService.class);
        binding.mToolBarComment.setNavigationOnClickListener(v -> {
            finish();
        });
        // 페이징  무한스크롤
        binding.commentNestedScrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener)
                        (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                            if (!v.canScrollVertically(1)) {
                                Log.d(TAG, "onCreate scrollViewChapter : 스크롤 불가능 ");
                                page++;
                                changePage(option, page);
                                commentShowAdapter.notifyDataSetChanged();
                            }

                        });
        /* 초기화 */
        changeOption(option, page);
//        getNovelReviewStatisticsData ();
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(CommentShowActivity.this, VERTICAL);
        comment_recyclerView.addItemDecoration(decoration);
        /* 상단 스크롤 refresh */
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
//            Toast.makeText(CommentShowActivity.this, "refresh 성공 " , Toast.LENGTH_SHORT).show();;
            changeOption(option, page);
//            getNovelReviewStatisticsData ();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefreshLayout.setRefreshing(false);

                }
            }, 600);
            //refresh 종료
//            binding.swipeRefreshLayout.setRefreshing(false);
        });

        //        /*nestedScrollView 무한 스크롤*/
        binding.commentNestedScrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener)
                        (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//                Log.d(TAG, "onScrollChange:  스크롤 중 "+ "scrollX : " + scrollX + " scrollY : " + scrollY);
//                Log.d(TAG, "onScrollChange: oldScrollX = "+ oldScrollX+" oldScrollY = "+oldScrollY );

                            if (!v.canScrollVertically(1)) { // 스크롤 불가능 , item end
                                Log.d(TAG, "onScrollChange: canScrollVertically(1) false 스크롤 불가능 ");
                                page++;
                                changePage(option, page);
                            } else {
//                    Log.d(TAG, "onScrollChange: canScrollVertically(1) false ");
                            }

                        });


    }//on create

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        changeOption(option, page);


    }

    private void generateDataList(List<CommentDTO> resultComment) {
        commentShowAdapter = new CommentShowAdapter(resultComment, clickListener, CommentShowActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CommentShowActivity.this);
        comment_recyclerView.setLayoutManager(layoutManager);
        comment_recyclerView.setAdapter(commentShowAdapter);
//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(CommentShowActivity.this,VERTICAL);
//        comment_recyclerView.addItemDecoration(decoration);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.old_option) {
            binding.buttonArrayOption.setText(R.string.old);
            option = Constants.OPTION_Old; // 등록순
            page = 0;
            changeOption(option, page);

            return true;
        } else if (itemId == R.id.new_option) {
            binding.buttonArrayOption.setText(R.string.first);
            option = Constants.OPTION_New; // 최신순
            page = 0;

            changeOption(option, page);


            return true;
        } else if (itemId == R.id.popular_option) {
            binding.buttonArrayOption.setText(R.string.popular);
            option = Constants.OPTION_Popular; // 인기순
            page = 0;

            changeOption(option, page);


            return true;
        }
        return false;
    }

    /* option 변경에 따른 변화 , book item 을 가져오는 메서드 */
    private void changeOption(int option, int page) {
        requestMap.put("page", page); // 페이지 번호
        requestMap.put("option", option); // option  0 = 인기 , 1= 최신 , 2= 등록순
        Call<List<CommentDTO>> getCommentItem = reviewService.reviewGetList(jwt, requestMap);
        getCommentItem.enqueue(new Callback<List<CommentDTO>>() {
            @Override
            public void onResponse(Call<List<CommentDTO>> call, Response<List<CommentDTO>> response) {
                if (response.code() == 201) {
                    // 결과물이 없을 때
                    Log.d(TAG, "onResponse: 아이템 결과물이 0 , status code " + response.code());

                } else if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse changeOption : 성공  , status code " + response.code());
                    resultComment = response.body();
                    generateDataList(resultComment);
                    commentShowAdapter.notifyDataSetChanged();
                    getNovelReviewStatisticsData();

                }
            }

            @Override
            public void onFailure(Call<List<CommentDTO>> call, Throwable t) {
                Log.d(TAG, "onFailure: error changeOption " + t.getMessage());

            }
        });
    }

    /* wid 의 리뷰 통계 관련 data get */
    private void getNovelReviewStatisticsData() {
        // Map 를 전달하는데 어차피 requestMap 에는 wid를 포함하고 있기 때문에 재활용 하자
        Call<NormalResponseDTO> reviewStaticsData = reviewService.getReviewStatisticsData(jwt, requestMap);
        reviewStaticsData.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: getNovelReviewStatisticsData error , response code " + response.body());
                    return;
                }
                NormalResponseDTO statistResponse = response.body();
                if (statistResponse == null) {
                    Log.d(TAG, "onResponse: getNovelReviewStatisticsData error , statistResponse null" + response.body());
                    return;
                }

                if (statistResponse.isSuccess()) {
                    Map<String, Object> data = statistResponse.getData();
                    review_count = String.valueOf(data.getOrDefault("count", "0"));
                    story_score = Float.parseFloat(String.valueOf(data.getOrDefault(Constants.RESPONSE_DATA_RATING_STORY, "0")));
                    world_score = Float.parseFloat(String.valueOf(data.getOrDefault(Constants.RESPONSE_DATA_RATING_WORLD, "0")));
                    character_score = Float.parseFloat(String.valueOf(data.getOrDefault(Constants.RESPONSE_DATA_RATING_CHARACTER, "0")));
                    quality_score = Float.parseFloat(String.valueOf(data.getOrDefault(Constants.RESPONSE_DATA_RATING_QUALITY,0.0)));
                    update_score = Float.parseFloat(String.valueOf(data.getOrDefault(Constants.RESPONSE_DATA_RATING_UPDATE,0.0)));
                    total_score = Float.parseFloat(String.valueOf(data.getOrDefault(Constants.RESPONSE_DATA_RATING_TOTAL, "0")));
                    Log.d(TAG, "onResponse: " + "review_count" + review_count);
                    Log.d(TAG, "onResponse: " + "story_score" + story_score);
                    Log.d(TAG, "onResponse: " + "world_score" + world_score);
                    Log.d(TAG, "onResponse: " + "character_score" + character_score);
                    Log.d(TAG, "onResponse: " + "quality_score" + quality_score);
                    Log.d(TAG, "onResponse: " + "update_score" + update_score);
                    Log.d(TAG, "onResponse: " + "total_score" + total_score);
                    String indicatorString = "등록된 리뷰 " + review_count + "개";
                    binding.textViewReviewIndicator.setText(indicatorString);
                    binding.ratingReviewNovelCharacter.setRating(character_score);
                    binding.ratingReviewNovelStory.setRating(story_score);
                    binding.ratingReviewNovelWorld.setRating(world_score);
                    binding.ratingReviewNovelQuality.setRating(quality_score);
                    binding.ratingReviewNovelUpdate.setRating(update_score);
                    String string_total_score = String.valueOf(total_score).substring(0, 3);
                    binding.textViewTotalScore.setText(string_total_score);
                } else {
                    Log.d(TAG, "onResponse: getNovelReviewStatisticsData statistResponse not success  " + statistResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {

            }
        });
    }

    /* 리사이클러뷰 좋아요 클릭 리스너 */
    private final CommentShowAdapter.OnItemClickListener clickListener = getClickListener();

    private final CommentShowAdapter.OnItemClickListener getClickListener() {
        return new CommentShowAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CommentDTO commentDTO) {
                Map<String, Object> requestReviewLike = new HashMap<>();
                /* comment 에 좋아요를 이미 누른 경우  insert 서버측에서 확인 */
                /* comment 에 좋아요를 누르지 않은 경우  delete 서버측에서 확인  */
                // 현재 좋아요 유무 정보를 보냄
                Log.d(TAG, "onItemClick: commentDTO.is_like " + commentDTO.is_like + "commentDTO.id" + commentDTO.id);
                requestReviewLike.put(Constants.Review.REQUEST_REVIEW_Like_OR_NOT, commentDTO.is_like);
                // comment.id , uid 정보를 전달 uid 는 jwt 에서 불러오기 떄문에 comment id 만 전달
                requestReviewLike.put(Constants.Review.REQUEST_REVIEW_COMMENT_ID, commentDTO.id);
                // comment_id 를  통해 하나의 댓글 정보를 가져온다.
                Call<NormalResponseDTO> setReviewLikeCall = reviewService.setReviewLike(jwt, requestReviewLike);
                setReviewLikeCall.enqueue(new Callback<NormalResponseDTO>() {
                    @Override
                    public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                        NormalResponseDTO normalResponseDTO = response.body();
                        if (normalResponseDTO == null) {
                            Log.d(TAG, "onResponse: NULL response body ");
                            return;
                        }
                        if (normalResponseDTO.isSuccess()) {
                            // 성공
                            /*TODO 이미지 처리 , ui 변경 */
                            if (commentDTO.is_like/*Integer.parseInt(commentDTO.is_like)*/ == 1) {
//                                    commentDTO.is_like = "0";
                                commentDTO.is_like = 0;
                                if (normalResponseDTO.getData().get("like_score") != null) {
                                    commentDTO.like_score = String.valueOf(normalResponseDTO.getData().get("like_score"));
                                }
                                commentShowAdapter.notifyDataSetChanged();

                            } else {
//                                    commentDTO.is_like = "1";
                                commentDTO.is_like = 1;
                                if (normalResponseDTO.getData().get("like_score") != null) {
                                    commentDTO.like_score = String.valueOf(normalResponseDTO.getData().get("like_score"));
                                }
                                commentShowAdapter.notifyDataSetChanged();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                        Log.e(TAG, "onFailure: setReviewLikeCall " + t.getMessage());
                    }
                });

            }
        };
    }

    public void showPopup2(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.array_option_menu, popup.getMenu());
        popup.show();
    }

    /* 페이징을 위해 , 스크롤 , 하단 진행시 추가 데이터를 받아오게 설정했음 */
    private void changePage(int option, int page) {
        requestMap.put("page", page); // 페이지 번호
        requestMap.put("option", option); // option  0 = 인기 , 1= 최신 , 2= 등록순
        Call<List<CommentDTO>> getCommentItem = reviewService.reviewGetList(jwt, requestMap);
        getCommentItem.enqueue(new Callback<List<CommentDTO>>() {
            @Override
            public void onResponse(Call<List<CommentDTO>> call, Response<List<CommentDTO>> response) {
                if (response.code() == 201) {
                    // 결과물이 없을 때
                    Log.d(TAG, "onResponse: 아이템 결과물이 0 , status code " + response.code());

                } else if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse changeOption : 성공  , status code " + response.code());

                    List<CommentDTO> addResult = response.body();
                    if (addResult == null) {
                        return;
                    }
//                    generateDataList(resultChapter);
                    commentShowAdapter.putData(addResult); // notifyDataChange 은 내부에서 진행

                }
            }

            @Override
            public void onFailure(Call<List<CommentDTO>> call, Throwable t) {
                Log.d(TAG, "onFailure: error changePage " + t.getMessage());
            }
        });
    }
}