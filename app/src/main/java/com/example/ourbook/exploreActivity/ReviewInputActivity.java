package com.example.ourbook.exploreActivity;

import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_CHARACTER;
import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_STORY;
import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_TOTAL;
import static com.example.ourbook.Constants.RESPONSE_DATA_RATING_WORLD;
import static com.example.ourbook.Constants.RESPONSE_DATA_REVIEW_Content;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.Service.ReviewService;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ActivityReviewInputBinding;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewInputActivity extends AppCompatActivity {
    //TODO review 전송 버튼 이후  서버 사이드 처리 ,  DB 상 comment 에 type 을 만들어 3가지 타입에 대해 각각 처리

    private static final String TAG = "ReviewInputActivity 리뷰 등록 페이지  " + Constants.AddTAG;
    private ActivityReviewInputBinding binding;
    private ColorStateList origin;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private ReviewService reviewService;
    private Map<String, Object> requestMap = new HashMap<>();
    private String jwt;
    private int wid;
    private int comment_id; // 수정 , 처음 입력을 확인하기 위해 사용 기본 값 -1 , -1 인 경우 처음 등록으로 처리
    private float storyScore; // 스토리 스코어
    private float charScore; // 캐릭터 스코어
    private float worldScore; // 세계관 스코어
    private float qualityScore; // 문장 퀄리티 ? 스코어
    private float updateScore; // 연재 성실도 ? 스코어
    private float savedOverallScore; // 종합 스코어 불러올 때만 사용
    private int scoreCount = 0; // 별점 갯수 1. 평균 값 계산 , 2. fab 리뷰 등록 버튼 활성화 조건문에 활용
    private boolean textInputOk;
    private String review_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewInputBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent mintent = getIntent();
        wid = mintent.getIntExtra(Constants.INTENT_WID, -1);
        jwt = mintent.getStringExtra(Constants.INTENT_JWT);
        comment_id = mintent.getIntExtra(Constants.INTENT_REVIEW_ID, -1);
        // 레트로핏, http 통신 서비스 초기화
        retrofitGenerator = new RetrofitGenerator();
//        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        reviewService = retrofitGenerator.init_user_retrofit(ReviewService.class);
        //TODO floating button clickable, non clickable image change
        binding.fabReviewRegis.setClickable(false);
        // 원 color stateList 따로 설정 안해서.. get 으로 사용
        origin = binding.fabReviewRegis.getBackgroundTintList();
        // 초기 값 gray
        binding.fabReviewRegis.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        if (wid == -1) {
            Log.e(TAG, "onCreate: get intent 로 전달 받은 wid 값이 -1 => 전달 받지 못하였음 ");
            finish();
        }
        /*toolbar*/
        binding.mToolBarReviewInput.setNavigationOnClickListener(v -> {
            finish();
        });
        /* rating bar  스토리*/
//        binding.ratingReviewNovelStory.setIsIndicator(false); // clickable 과 비슷하게 동작
        binding.ratingReviewNovelStory.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                binding.ratingReviewNovelStory.setRating(rating);
                ratingBar.setRating(rating);
                storyScore = rating;
                Log.d(TAG, "onRatingChanged: rating bar change " + " rating = " + rating + " fromUser = " + fromUser);
                Log.d(TAG, "onRatingChanged: score 확인 " + "updateScore :" + updateScore + "qualityScore :" + qualityScore+"storyScore : " + storyScore + "charScore : " + charScore + "worldScore :" + worldScore);
                binding.textViewTotalScore.setText(String.valueOf(getOverallScore(storyScore, charScore, worldScore , qualityScore , updateScore)).substring(0,3));
                fabButtonChange();
                if (rating > 0) {


                    // fab button ui 변경

                }
            }
        });
        /*rating bar 캐릭터*/
        binding.ratingReviewNovelCharacter.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                charScore = rating;
                binding.textViewTotalScore.setText(String.valueOf(getOverallScore(storyScore, charScore, worldScore , qualityScore , updateScore)).substring(0,3));
                fabButtonChange();
                if (rating > 0) {

                    // fab button ui 변경
//                    fabButtonChange();

//                    if(textInputOk && scoreCount == 3 ) {
//                        binding.fabReviewRegis.setClickable(true);
//                        binding.fabReviewRegis.setBackgroundTintList(origin);
//                    }
                }
            }
        });
        /*rating bar 세계관*/
        binding.ratingReviewNovelWorld.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                worldScore = rating;
                binding.textViewTotalScore.setText(String.valueOf(getOverallScore(storyScore, charScore, worldScore , qualityScore , updateScore)).substring(0,3));
                fabButtonChange();
                if (rating > 0) {

                    // fab button ui 변경

//                    if(textInputOk && scoreCount == 3 ) {
//                        binding.fabReviewRegis.setClickable(true);
//                        binding.fabReviewRegis.setBackgroundTintList(origin);
//                    }
                }
            }
        });
        /* rating bar 퀄리티 */
        binding.ratingReviewNovelQuality.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                qualityScore = rating;
                binding.textViewTotalScore.setText(String.valueOf(getOverallScore(storyScore, charScore, worldScore , qualityScore , updateScore)).substring(0,3));
                fabButtonChange();

            }
        });

        /* rating bar 성실도 */
        binding.ratingReviewNovelUpdate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                updateScore = rating;
                binding.textViewTotalScore.setText(String.valueOf(getOverallScore(storyScore, charScore, worldScore , qualityScore , updateScore)).substring(0,3));
                fabButtonChange();
            }
        });
        // 리뷰 작성이 신규 작성이 아니라 수정인 경우
        if(comment_id != -1 ){
            loadComment();
        }
        /*리뷰 edit text 등록 , 리스너 */
        binding.textViewReviewInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: 입력 문자열 확인 " + "s =" + s + "start =" + start + "before = " + before + "count = " + count);
//                if(s.length()>20){
//                    binding.fabReviewRegis.setClickable(true);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: s " + s.toString());
                String str = String.valueOf(s);
                int length = str.replace(" ", "").length();
                if (length > 20) { // 길이 조건 충족
                    textInputOk = true;
                    fabButtonChange();
//                    if(textInputOk && scoreCount == 3 ) {
//                        binding.fabReviewRegis.setClickable(true);
////                    binding.fabReviewRegis.setBackgroundTintMode(PorterDuff.Mode.SRC);
////                    ColorStateList origin = binding.fabReviewRegis.getBackgroundTintList();
//                        binding.fabReviewRegis.setBackgroundTintList(origin);
//                    }
//                }else { // 충족 x
//                    textInputOk =false;
//                    binding.fabReviewRegis.setClickable(false);
////                    ColorStateList colorStateList = ColorStateList.valueOf(Color.GRAY);
//                    binding.fabReviewRegis.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                }
            }
        });


        /*리뷰 등록 버튼*/
        binding.fabReviewRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request map 셍성
                review_content = String.valueOf(binding.textViewReviewInput.getText());
                requestMap.put(Constants.REQUEST_WID, wid);
                requestMap.put(Constants.Review.REQUEST_REVIEW_CONTENT, review_content);
                requestMap.put(Constants.Review.REQUEST_REVIEW_SCORE_Character, charScore);
                requestMap.put(Constants.Review.REQUEST_REVIEW_SCORE_Story, storyScore);
                requestMap.put(Constants.Review.REQUEST_REVIEW_SCORE_World, worldScore);
                requestMap.put(Constants.Review.REQUEST_REVIEW_SCORE_Quality, qualityScore);
                requestMap.put(Constants.Review.REQUEST_REVIEW_SCORE_Update, updateScore);
                Call<NormalResponseDTO> regisReviewCall = reviewService.reviewRegis(jwt, requestMap);

                // 서버 요청
                regisReviewCall.enqueue(new Callback<NormalResponseDTO>() {
                    @Override
                    public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                        //TODO ,  서버사이드 코드 작성후 응답 처리
//                        Log.d(TAG, "onResponse: "+response.body());
                        NormalResponseDTO responseDTO = response.body();
                        if (responseDTO.isSuccess()) {
                            Toast.makeText(getApplicationContext(), "리뷰 등록 성공", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // db 등록 실패
                            if (responseDTO.getMessage().contains("rating_unique")) {
                                // 에러 발생 중복 코드
                                Log.d(TAG, "onResponse: 중복 ");
                                Toast.makeText(getApplicationContext(), "이미 등록된 리뷰가 있습니다", Toast.LENGTH_SHORT).show();

                            } else {// 에러 발생 중복 코드 그 외..
                                Toast.makeText(getApplicationContext(), " 에러 " + responseDTO.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                        Log.d(TAG, "onFailure: 레트로핏 message " + t.getMessage());
                    }
                });
            }
        });


    /* 툴팁 풍선 - 리뷰 점수 안내 */
        Context context = this;
        String tooltip_text = Resources.getSystem().getString(R.string.tooltip_review);
        tooltip_text ="<strong>(글쓰기 품질)</strong>: 문장 구성, 전반적인 문체 등 작가의 글쓰기 기술을 평가합니다.<br/>" +
                "<strong>(업데이트 안정성)</strong>: 작가가 얼마나 규칙적으로 새로운 챕터나 섹션을 게시하는지를 평가합니다.<br/>" +
                "<strong>(이야기 전개)</strong>: 플롯의 진행 방식, 이야기의 구조, 서사의 흐름 등 이야기가 얼마나 잘 전개되는지를 나타냅니다.<br/>" +
                "<strong>(캐릭터 디자인)</strong>: 캐릭터의 복잡성, 캐릭터가 얼마나 잘 만들어졌고, 생생하게 묘사되었는지를 의미합니다.<br/>" +
                "<strong>(세계 배경)</strong>: 소설의 설정, 세계관의 구축, 배경의 상세함 및 이 세계가 얼마나 설득력 있게 묘사되었는지를 평가합니다.<br/>";
        Balloon balloon = new Balloon.Builder(context)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setWidth(BalloonSizeSpec.WRAP)
//                .setHeight(65)
                .setMargin(10)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText(tooltip_text)
                .setTextColor(ContextCompat.getColor(context, R.color.black))
                .setTextIsHtml(true)
//                .setIconDrawable(ContextCompat.getDrawable(context, R.drawable.info_solid))
                .setBackgroundColor(ContextCompat.getColor(context, R.color.ivory))
//                .setOnBalloonClickListener(onBalloonClickListener)
                .setBalloonAnimation(BalloonAnimation.ELASTIC)
//                .setLifecycleOwner(lifecycleOwner)
                .build();
        binding.buttonTooltip.setOnClickListener(v -> {
            balloon.showAlignBottom(binding.buttonTooltip);
        });


    }//on create

    /* 평균 점수 계산 메서드 */
    private float getOverallScore(float one, float two, float three, float four , float five) {
        // 갯수 에 따라서 평균값이 달라지게 설정하고 싶었음
        // 근데 좀 불필요해 보임
        scoreCount = 0;
        scoreCount = !(one > 0) ? scoreCount : scoreCount + 1;
        scoreCount = !(two > 0) ? scoreCount : scoreCount + 1;
        scoreCount = !(three > 0) ? scoreCount : scoreCount + 1;
        scoreCount = !(four > 0) ? scoreCount : scoreCount + 1;
        scoreCount = !(five > 0) ? scoreCount : scoreCount + 1;
        float score = (one + two + three + four + five) / scoreCount;
        float result = (float) Math.round(score * 10) / 10; // 소수점 첫째 자리 까지 출력
        return score;
    }



    /* 리뷰 등록 버튼 ui 변화
    * scoreCount = 입력한 리뷰 점수의 갯수
    * textInputOk = 입력한 리뷰 , content 의 조건 통과 여부*/
    private void fabButtonChange() {
        if (textInputOk && scoreCount == 5) {
            binding.fabReviewRegis.setClickable(true);
            binding.fabReviewRegis.setBackgroundTintList(origin);
        } else {
            binding.fabReviewRegis.setClickable(false);
            binding.fabReviewRegis.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        }
    }

    /* 리뷰 , comment_id 가 존재하는 경우 데이터를 불러온다 */
    private void loadComment() {
        Map<String, Object> loadRequestMap = new HashMap<>();
        loadRequestMap.put(Constants.Review.REQUEST_REVIEW_COMMENT_ID, comment_id);
        loadRequestMap.put(Constants.REQUEST_WID,wid);
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
                    /* TODO null 인 경우 옵션 0 으로 추가해서 다시 진행*/
                    // 각각의 점수 불러오기
                    worldScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().get(RESPONSE_DATA_RATING_WORLD)));
                    storyScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().get(RESPONSE_DATA_RATING_STORY)));
                    charScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().get(RESPONSE_DATA_RATING_CHARACTER)));
                    qualityScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().getOrDefault(Constants.RESPONSE_DATA_RATING_QUALITY,0.0)));
                    updateScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().getOrDefault(Constants.RESPONSE_DATA_RATING_UPDATE,0.0)));
                    savedOverallScore = Float.parseFloat(String.valueOf(normalResponseDTO.getData().get(RESPONSE_DATA_RATING_TOTAL)));
                    review_content = String.valueOf(normalResponseDTO.getData().get(RESPONSE_DATA_REVIEW_Content));
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "worldScore" + worldScore );
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "storyScore" + storyScore );
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "charScore" + charScore );
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "updateScore" + updateScore );
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "qualityScore" + qualityScore );
                    Log.d(TAG, "onResponse:loadComment() parsing 후 "+ "savedOverallScore" + savedOverallScore );
                    // 호출된 data 에 따라 ui 변경
                    binding.ratingReviewNovelCharacter.setRating(charScore);
                    binding.ratingReviewNovelStory.setRating(storyScore);
                    binding.ratingReviewNovelWorld.setRating(worldScore);
                    binding.ratingReviewNovelUpdate.setRating(updateScore);
                    binding.ratingReviewNovelQuality.setRating(qualityScore);
                    binding.textViewReviewInput.setText(review_content);
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

    ;
}