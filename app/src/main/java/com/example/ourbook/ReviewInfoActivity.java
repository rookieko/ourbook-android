package com.example.ourbook;

import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_CHARACTER;
import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_QUALITY;
import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_STORY;
import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_TOTAL;
import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_UPDATE;
import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_WORLD;
import static com.example.ourbook.Constants.RESPONSE_DATA_REVIEW_Content;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourbook.DataTool.BaseUrl;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ReviewService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityReviewInfoBinding;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewInfoActivity extends AppCompatActivity {
    // 리뷰 상세 정보 페이지 계획 , 기존 ReviewInput Activity 재활용 한다
    // comment_id 를 사용하여 리뷰 상세 정보를 불러온다
    // 요청 정보
    // RequestData = {comment_id ( intent 를 통해서 가져온다 ), }
    // 응답 정보
    // Response Data = { ! WorldScore , ! StoryScore , ! CharacterScore , ! review_content ( = comment_content) , !  like_score , ! createDate , profileImage , username }
    //
    private static final String TAG = "ReviewInfoActivity " +Constants.AddTAG;
    private RetrofitGenerator retrofitGenerator;
    private ReviewService reviewService;
    private UserSingletone userSingletone;
    private String jwt;
    private ActivityReviewInfoBinding binding;
    private int comment_id;
    private float worldScore;
    private float storyScore;
    private float charScore;
    private float updateScore;
    private float qualityScore;
    private float savedOverallScore;
    private String username;
    private String profileImagePath;
    private String like_score;
    private String createDate;
    private String review_content;
    private int is_like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityReviewInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent  = getIntent();
        comment_id  = intent.getIntExtra(Constants.INTENT_REVIEW_ID,-1);
        if(comment_id == -1 ){
            Toast.makeText(this,"잘못된 접근입니다.",Toast.LENGTH_SHORT).show();
            finish();
        }
        binding.mToolBarReviewInfo.setNavigationOnClickListener(v -> {finish();});
        userSingletone = UserSingletone.getMyUser();
        jwt = userSingletone.getUserJWT();
        retrofitGenerator = new RetrofitGenerator();
        reviewService = retrofitGenerator.init_user_retrofit(ReviewService.class);
        Log.d(TAG, "onCreate: comment_id "+comment_id);
        // data 호출
        loadComment();
        binding.commentImageViewLike.setOnClickListener(v -> {
            likeOnClick();
//            setLikeUi(); // ** 레트로핏 에 따른 ui 변경은 레트로핏에서 하는게 좋다 서버 응답이 쓰레드에서 진행 되기 때문에
        });
        /* 상단 스크롤 refresh */
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
//            Toast.makeText(this, "refresh 성공 " , Toast.LENGTH_SHORT).show();;
            loadComment();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefreshLayout.setRefreshing(false);

                }
            },600);
            //refresh 종료
//            binding.swipeRefreshLayout.setRefreshing(false);
        });

    }
    /* 리뷰 , comment_id 가 존재하는 경우 데이터를 불러온다 */
    private void loadComment() {
        Map<String, Object> loadRequestMap = new HashMap<>();
        loadRequestMap.put(Constants.Review.REQUEST_REVIEW_COMMENT_ID, comment_id);
        Call<NormalResponseDTO> loadCommentCall = reviewService.getReviewOwnData(jwt, loadRequestMap);
        loadCommentCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                NormalResponseDTO normalResponseDTO = response.body();
                if (normalResponseDTO == null) {
                    Log.e(TAG, "onResponse: null comment_id to get comment info");
                    return;
                }
                if (normalResponseDTO.isSuccess()) {
                    worldScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().getOrDefault(RESPONSE_DATA_RATING_WORLD,0.0)));
                    storyScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().getOrDefault(RESPONSE_DATA_RATING_STORY,0.0)));
                    charScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().getOrDefault(RESPONSE_DATA_RATING_CHARACTER,0.0)));
                    qualityScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().getOrDefault(RESPONSE_DATA_RATING_QUALITY,"0.0")));
                    updateScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().getOrDefault(RESPONSE_DATA_RATING_UPDATE,"0.0")));

                    savedOverallScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().getOrDefault(RESPONSE_DATA_RATING_TOTAL,0.0)));
                    review_content = String.valueOf(normalResponseDTO.getData().get(RESPONSE_DATA_REVIEW_Content));

                    /* 추가 기존 own review 와 별개의 코드  imeage , username */
//                    username = String.valueOf(normalResponseDTO.getData().get(Constants.RESPONSE_Data_UserName));
//                    profileImagePath = String.valueOf(normalResponseDTO.getData().get(Constants.RESPONSE_Data_ProfileImage));
                    setReviewOfUserData(normalResponseDTO.getData());

                    /**/
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "worldScore" + worldScore );
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "storyScore" + storyScore );
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "charScore" + charScore );
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "savedOverallScore" + savedOverallScore );
                    String total_score= String.valueOf(savedOverallScore);
                    // 호출된 data 에 따라 ui 변경
                    binding.ratingReviewNovelCharacter.setRating(charScore);
                    binding.ratingReviewNovelStory.setRating(storyScore);
                    binding.ratingReviewNovelWorld.setRating(worldScore);
                    binding.ratingReviewNovelQuality.setRating(qualityScore);
                    binding.ratingReviewNovelUpdate.setRating(updateScore);
                    binding.textViewTotalScore.setText(total_score.substring(0,3));
                    binding.textViewReviewContent.setText(review_content);

                }else {
                    Log.d(TAG, "onResponse: 실패 " + normalResponseDTO.getMessage());
                }
            }


            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.d(TAG, "onFailure: loadComment() " + t.getMessage());
            }
        });
    }

    private void setReviewOfUserData(Map<String,Object> data ) {
        username = String.valueOf(data.get(Constants.RESPONSE_Data_UserName));
        profileImagePath = String.valueOf(data.get(Constants.RESPONSE_Data_ProfileImage));
        like_score = String.valueOf(data.getOrDefault("like_score","0"));
        createDate = String.valueOf(data.get("createDate"));
        is_like = Integer.parseInt(String.valueOf(data.get("is_like")));
        binding.commentItemCommentUsername.setText(username);

        binding.commentDate.setText(createDate.substring(0,16));
        if (profileImagePath.length() > 5) {
            Glide.with(this)
                    .load(BaseUrl.ProfileImage_URL + profileImagePath)
                    .thumbnail(0.1f)
                    .circleCrop()
                    .into(binding.imageViewCommentsProfile);
        }else {
            Glide.with(this)
                    .load(R.drawable.outline_account_circle_24)
                    .into(binding.imageViewCommentsProfile);
        }
        setLikeUi();
    }


    private void setLikeUi(){
        if(is_like == 1 ){
            Glide.with(this)
                    .load(R.drawable.heart_red_icon)
                    .into(binding.commentImageViewLike);
        }else {
            Glide.with(this)
                    .load(R.drawable.heart_empty_icon)
                    .into(binding.commentImageViewLike);
        }
        binding.commentItemTextViewLikeScore.setText(like_score);
    }


    private void likeOnClick(){
        Map<String,Object> requestReviewLike = new HashMap<>();
        /* comment 에 좋아요를 이미 누른 경우  insert 서버측에서 확인 */
        /* comment 에 좋아요를 누르지 않은 경우  delete 서버측에서 확인  */
        // 현재 좋아요 유무 정보를 보냄
        Log.d(TAG, "onItemClick: commentDTO.is_like " + is_like +"commentDTO.id"+ comment_id);
        requestReviewLike.put(Constants.Review.REQUEST_REVIEW_Like_OR_NOT,is_like);
        // comment.id , uid 정보를 전달 uid 는 jwt 에서 불러오기 떄문에 comment id 만 전달
        requestReviewLike.put(Constants.Review.REQUEST_REVIEW_COMMENT_ID,comment_id);
        // comment_id 를  통해 하나의 댓글 정보를 가져온다.
        Call<NormalResponseDTO> setReviewLikeCall= reviewService.setReviewLike(jwt,requestReviewLike);
        setReviewLikeCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                NormalResponseDTO normalResponseDTO = response.body();
                if(normalResponseDTO == null){
                    Log.d(TAG, "onResponse: NULL response body ");
                    return;
                }
                if(normalResponseDTO.isSuccess()) {
                    // 성공
                    /*TODO 이미지 처리 , ui 변경 */
                    if (is_like/*Integer.parseInt(commentDTO.is_like)*/ == 1) {
//                                    commentDTO.is_like = "0";
                        is_like = 0;
                        if(normalResponseDTO.getData().get("like_score") != null){
                            like_score = String.valueOf(normalResponseDTO.getData().get("like_score"));
                        }
//                        commentShowAdapter.notifyDataSetChanged();

                    }else {
//                                    commentDTO.is_like = "1";
                        is_like = 1;
                        if(normalResponseDTO.getData().get("like_score") != null){
                            like_score = String.valueOf(normalResponseDTO.getData().get("like_score"));
                        }
//                        commentShowAdapter.notifyDataSetChanged();

                    }
                    setLikeUi();
                }
            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.e(TAG, "onFailure: setReviewLikeCall "+t.getMessage() );
            }
        });
    }
}