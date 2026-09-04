package com.example.ourbook.exploreActivity;

import static com.example.ourbook.Constants.RESPONSE_DATA_FirstChapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.BaseUrl;
import com.example.ourbook.DataTool.Recycle.Adapter.CommentShowAdapter;
import com.example.ourbook.DataTool.Response.CommentDTO;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.Service.ReviewService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.ReadNovelActivity;
import com.example.ourbook.databinding.ActivityBookInfoBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookInfoActivity extends AppCompatActivity {
    private final static String TAG = " BookInfoActivity " + Constants.AddTAG;
    private ActivityBookInfoBinding binding;
    private int wid;
    private String jwt;
    private String bookName;
    private String coverImagePath;
    private UserSingletone userSingletone;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private ReviewService reviewService;
    private RecyclerView recyclerView;
    private CommentShowAdapter commentShowAdapter;
    private Map<String, Object> requestUserDataMap = new HashMap<>();
    private String summary;
    private String title;
    private String category;
    private String writer_name;
    private String createDate;
    private int chapter; // 총 회차 수 정보
    private int chapter_id; // 이어보기 , 회차 정보
    private int first_chapter_id; // 처음보기 ,
    //    private int chapter_num;
    private String score; // 점수  리뷰에 나타낼 정보
    private int my_comment_id = -1; // 내가 쓴 리뷰 점수
    private int likes; // 관심 목록 or 알림 설정에 관한 옵션  1 = 관심 , 2 = 알림
    // 응답
    private NormalResponseDTO userResponseDto;
    private NormalResponseDTO novelResponseDto;
    private String review_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        bookName = intent.getStringExtra(Constants.INTENT_BOOK_NAME);
        wid = intent.getIntExtra(Constants.INTENT_WID, -1);
        coverImagePath = intent.getStringExtra(Constants.INTENT_BOOK_COVER);
        if (wid == -1) {
            Log.d(TAG, "onCreate: 에러 wid 값 -1  intent 오류 ");
            finish();
        } else {
            Log.d(TAG, "intent 값 확인 onCreate: wid =  " + wid + " bookName = " + bookName + " coverImagePath = " + coverImagePath);
        }
        // recyclerView 초기화
        recyclerView = binding.commentRecyclerView;
        /* summary cardView setting 인도 영상 https://www.youtube.com/watch?v=qIJ_U51s4ls */
        binding.layoutSummary.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        /* summary cardView end */
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);
        reviewService = retrofitGenerator.init_user_retrofit(ReviewService.class);
        userSingletone = UserSingletone.getMyUser();
        jwt = userSingletone.getUserJWT();
        binding.textViewBookTitle.setText(bookName);
        binding.mToolBarBookInfo.setTitle("웹소설 정보");
        binding.mToolBarBookInfo.setNavigationOnClickListener(v -> {
            finish();
        });

        // 웹소설 커버 이미지가 존재 하는 경우
        if (coverImagePath != null && coverImagePath.length() > 5) {
            Glide.with(this).load(BaseUrl.BookCoverImage_URL + coverImagePath).into(binding.imageViewBookCover);
        } else {
            // 웹소설 커버 이미지가 존재 하지 않는 경우
//            Glide.with(this).load(BaseUrl.BookCoverImage_URL+ coverImagePath).into(binding.imageViewBookCover);
        }
        /* TODO not important ) DB  조회 1. 회차 기록 , 2. 회차 등록에 따라
            이어보기, 처음부터 보기를 활성화 ui 변경하는 작업이 필요 할수 있음 */
        // 회차 , 처음 부터 보기
        binding.buttonReadFirst.setOnClickListener(v -> {
            Intent readFirstIntent = new Intent(this, ReadNovelActivity.class);
            // 처음 읽는 경우
            readFirstIntent.putExtra(Constants.INTENT_TYPE, Constants.Read.TYPE_FIRST_READ);
            readFirstIntent.putExtra(Constants.INTENT_CHAPTER_ID, first_chapter_id);
            readFirstIntent.putExtra(Constants.INTENT_WID, wid);
            readFirstIntent.putExtra(Constants.INTENT_CHAPTER_NUM, 1); // 사용하지 말자
            startActivity(readFirstIntent);
        });
        // 회차 , 이어 보기
        binding.buttonReadContinue.setOnClickListener(v -> {
            Intent readContinueIntent = new Intent(this, ReadNovelActivity.class);
            // 이어서 읽는 경우
            readContinueIntent.putExtra(Constants.INTENT_TYPE, Constants.Read.TYPE_CONTINUE_READ);
            readContinueIntent.putExtra(Constants.INTENT_CHAPTER_ID, chapter_id);
            readContinueIntent.putExtra(Constants.INTENT_WID, wid);
            // TODO history 로 부터 chapter num 정보도 가져오기
            startActivity(readContinueIntent);
        });
        // 회차 정보 페이지로 이동
        binding.buttonMoreChapterItem.setOnClickListener(v -> {
            Intent chapterIntent = new Intent(this, ChapterShowActivity.class);
            chapterIntent.putExtra(Constants.INTENT_WID, wid);
            chapterIntent.putExtra(Constants.INTENT_JWT, userSingletone.getUserJWT());
            startActivity(chapterIntent);
        });

        // 리뷰(comment == review )  정보 페이지로 이동
        binding.buttonMoreCommentItem.setOnClickListener(v -> {
            Intent commentIntent = new Intent(this, CommentShowActivity.class);
            commentIntent.putExtra(Constants.INTENT_WID, wid);
            commentIntent.putExtra(Constants.INTENT_JWT, userSingletone.getUserJWT());
            commentIntent.putExtra(Constants.INTENT_REVIEW_ID, my_comment_id); // 수정 용  fab button 선택시 이유 , 리뷰 작성 page 입장이 2곳에서 가능해진다  book info , comment show
            startActivity(commentIntent);
        });
        // 리뷰 등록 페이지로 이동 , 조건 추가 , 웹소설을 실제로 보았는지에 따라서 활성화 , 비활성화
        binding.buttonRegisComment.setOnClickListener(v -> {
            if((!binding.buttonReadContinue.isEnabled())&&(my_comment_id == -1)){
                // 이어보기 버튼이 활성화 되지 않았다 == 보지 않았다 , 기존에 review 가 등록된 경우는 접근 가능하게 하여 예외 처리함
                Toast.makeText(this,"리뷰 작성전에 웹소설을 읽어주세요!",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent regisReviewIntent = new Intent(this, ReviewInputActivity.class);
            regisReviewIntent.putExtra(Constants.INTENT_WID, wid);
            regisReviewIntent.putExtra(Constants.INTENT_JWT, userSingletone.getUserJWT());
            regisReviewIntent.putExtra(Constants.INTENT_REVIEW_ID, my_comment_id); // 수정 작성

            startActivity(regisReviewIntent);
        });

    }

    private int count = 0; //retrofit 요청 횟수 카운트

    @Override
    protected void onResume() {
        super.onResume();

        Call<NormalResponseDTO> bookDataCall = registerBookService.getWebNovelData(userSingletone.getUserJWT(), wid);

        bookDataCall.enqueue(new Callback<NormalResponseDTO>() {

            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                NormalResponseDTO novelResponseDto = response.body();
                if (novelResponseDto == null) {
                    return;
                }
                if (novelResponseDto.isSuccess()) {
                    Map<String, Object> data = novelResponseDto.getData();
                    category = /*" : " + */ String.valueOf(data.get("category"));
                    title = (String) data.get("title");// 제목
                    summary = String.valueOf(data.get("summary")); // 줄거리
                    writer_name = /*" : " + */String.valueOf(data.get("writer_name")); // 작가명
                    //웹소설 등록 날짜 10~> 시간
                    createDate = " 등록일 : " + String.valueOf(data.get("createTime")).substring(0, 10);
                    //회차
                    chapter = Integer.parseInt(Objects.requireNonNullElse(String.valueOf(data.get("total_num")), "0"));
                    first_chapter_id = Integer.parseInt(Objects.requireNonNullElse(data.get(RESPONSE_DATA_FirstChapter), 0).toString());

                    binding.textViewChapterInfo.setText(String.valueOf(chapter));
                    if (!summary.contentEquals("null")) {
                        binding.textViewBookSummary.setText(summary);
                        binding.textViewBookSummaryShort.setText(summary);
                        binding.cardViewSummary.setVisibility(View.VISIBLE);
                    } else {
                        binding.cardViewSummary.setVisibility(View.GONE);
                    }
                    // 작가명
                    binding.textViewWriterName.setText(writer_name);
                    // 작성 날짜
                    binding.textViewCreateDate.setText(createDate);
                    // 카테고리
                    binding.textViewBookCategory.setText(category);
                    // TODO 조회수 , 점수


                    /* 리뷰 ui 정보 update */

                    Log.d(TAG, "onResponse: count " + novelResponseDto.getData().get(Constants.RESPONSE_DATA_REVIEW_Count));

                    if (novelResponseDto.getData().get(Constants.RESPONSE_DATA_REVIEW_Count) != null) {
                        // 리뷰 총 갯수
                        review_count = String.valueOf(novelResponseDto.getData().get(Constants.RESPONSE_DATA_REVIEW_Count));
                        binding.textViewReviewInfo.setText("총 0개의 리뷰가 등록되어 있습니다.");

                    } else {
                        // 리뷰 갯수가 없는 경우
                        review_count = "0";
                    }
                    String count_string = " 총 " + review_count + "개의 리뷰가 등록되어 있습니다.";
                    binding.textViewReviewInfo.setText(count_string);
                    /* 별점 ui 정보 update */
                    String total_score = String.valueOf(novelResponseDto.getData().get("total_score"));
                    if(total_score.contentEquals("null")){
                        binding.textViewRating.setText("");
                    }else {
                        binding.textViewRating.setText(total_score.substring(0,3));
                    }


                    // 성공후 유저 정보도 호출
                    getUserBookData();
                    // 리뷰도 호출
                    getBestReview();
                } else {
                    Log.e(TAG, "onResponse: fail Message " + novelResponseDto.getMessage());
                }


            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage(), t);

                count++;
                // 이렇게 해도 될까
                // 실패 요청을 다시 시도하고 3번 실패하는 경우 액티비티와 함께 요청이 종료되게 설정
                if (count >= 3) {
                    Toast.makeText(getApplicationContext(), "정보를 가져오지 못하였습니다 다음에 다시 시도하세요.", Toast.LENGTH_SHORT).show();
                    finish();
                    call.cancel();
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "정보를 가져오지 못하여 다시 불러옵니다.", Toast.LENGTH_SHORT).show();
                    call.clone().enqueue(this);
                }
            }
        });
    }//Resume

    /* 줄거리 버튼 확정 ,축소 animation 을 위해 구현 */
    public void expand(View view) {
        int v = (binding.textViewBookSummary.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        int mv = (v == View.GONE) ? View.VISIBLE : View.GONE;
        int d = (v == View.GONE) ? R.drawable.caret_down_solid : R.drawable.caret_up_solid;
//        Glide.with(this).load(d).into(binding.imageViewSummary);
        binding.imageViewSummary.setImageResource(d);
        TransitionManager.beginDelayedTransition(binding.layoutSummary, new AutoTransition());
        binding.textViewBookSummary.setVisibility(v);
        binding.textViewBookSummaryShort.setVisibility(mv);
    }

    private void getUserBookData() { // 해당 웹소설에 사용자의 연관된 사용자의 정보를 가져온다
        requestUserDataMap.put(Constants.REQUEST_WID, wid);
        Call<NormalResponseDTO> getUserBookDataCall = registerBookService.getUserBookData(jwt, requestUserDataMap);
        getUserBookDataCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                userResponseDto = response.body();
                if (userResponseDto == null) {
                    return;
                }
                if (userResponseDto.isSuccess()) {
                    Log.d(TAG, "onResponse: getUserBookData() score " + userResponseDto.getData().get(Constants.RESPONSE_DATA_UserScore));
                    Log.d(TAG, "onResponse: getUserBookData() RecentChapter " + userResponseDto.getData().get(Constants.RESPONSE_DATA_RecentChapter));
                    Log.d(TAG, "onResponse: getUserBookData() like option " + userResponseDto.getData().get(Constants.RESPONSE_DATA_LikeOption));
                    Log.d(TAG, "onResponse: getUserBookData() locate " + userResponseDto.getData().get(Constants.RESPONSE_DATA_RecentChapter_locate));
                    Log.d(TAG, "onResponse: getUserBookData() count " + userResponseDto.getData().get(Constants.RESPONSE_DATA_REVIEW_Count));
                    Log.d(TAG, "onResponse: ");
                    Toast.makeText(getApplicationContext(), "유저 정보 호출 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onResponse: user 정보 없음 " + userResponseDto.getMessage());
                    Toast.makeText(getApplicationContext(), "유저 정보가 없음 ", Toast.LENGTH_SHORT).show();
                }
                setUserDataToUI();

            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.e(TAG, "onFailure: getUserBookData() " + t.getMessage());
            }
        });
    }

    /* 가져온 유저 데이터에 따른 ui 변화 */
    private void setUserDataToUI() {
//        String score;
//        int likes;
//        int chapter_id;
        /* 점수 리뷰 존재 하는 경우 */
        String id;
        if (!(score = String.valueOf(userResponseDto.getData().get(Constants.RESPONSE_DATA_UserScore))).equals("null")) {
            // 사용자가 작성한 리뷰가 존재하는 경우 comment_id 로 받는다.
            if (!(id = String.valueOf(userResponseDto.getData().get(Constants.RESPONSE_DATA_UserReviewID))).equals("null")) {
                my_comment_id = Integer.parseInt(id); // null 이 아닌 경우 int 로 파싱 이후 review , comment 수정에서 사용
                Log.d(TAG, "setUserDataToUI: comment_id 값 확인 ");
            }
            //점수 관련 on , exist
            binding.textViewReviewScore.setText(score.substring(0, 3));
            binding.textViewReviewIndicator.setText(R.string.review_is_rating_indicator);
//            binding.imageViewStarIndicator.setImageResource(R.drawable.star_regular_on_icon);
            Glide.with(this).load(R.drawable.star_regular_on_icon).into(binding.imageViewStarIndicator);
            // TODO binding.buttonRegisComment set onclick Listener 수정
        } else {
            // 점수 관리 off , null
            binding.textViewReviewScore.setText("");
            binding.textViewReviewIndicator.setText(R.string.review_not_rating_indicator);
//            binding.imageViewStarIndicator.setImageResource(R.drawable.star_regular_icon);
            Glide.with(this).load(R.drawable.star_regular_icon).into(binding.imageViewStarIndicator);
        }

        /* TODO 관심 목록 or 알림 설정이 존재 하는 경우 */
        if (userResponseDto.getData().get(Constants.RESPONSE_DATA_LikeOption) != null) {
            // 관심 목록에 등록 했거나 , 알림 등록을 한경우 1 = 관심 , 2 = 알림
            likes = Integer.parseInt(String.valueOf(userResponseDto.getData().get(Constants.RESPONSE_DATA_LikeOption)));
            binding.buttonAddLikes.setText(R.string.set_unlike_string);

            if (likes == 2) {
            }
        } else {

        }




        /* 이어보기 , 회차 열람 정보가 존재하는 경우 */
        if (userResponseDto.getData().get(Constants.RESPONSE_DATA_RecentChapter) != null) {
            chapter_id = Integer.parseInt(String.valueOf(userResponseDto.getData().get(
                    Constants.RESPONSE_DATA_RecentChapter)));

//            binding.buttonReadContinue.setActivated(true);
            //TODO INTENT READCHAPTER
        } else {
//            binding.buttonReadContinue.setActivated(false);

//            return;
        }
        setReadButtonUI();
    }// setUserDataUi

    /* 리뷰 베스트 5 정보 호출*/
    private void getBestReview(){
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put(Constants.REQUEST_WID,wid);
        Call<List<CommentDTO>> getCommentItem = reviewService.bestReviewGetList(jwt,requestMap);
        getCommentItem.enqueue(new Callback<List<CommentDTO>>() {
            @Override
            public void onResponse(Call<List<CommentDTO>> call, Response<List<CommentDTO>> response) {
                List<CommentDTO> commentDTOList = response.body();
                if(commentDTOList ==  null){return;}
                generateDataList(commentDTOList);
            }

            @Override
            public void onFailure(Call<List<CommentDTO>> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage() );
            }
        });
    }
    /* 리사이클러뷰 */
    private void generateDataList(List<CommentDTO> resultComment) {
        commentShowAdapter = new CommentShowAdapter(resultComment, null ,BookInfoActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BookInfoActivity.this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentShowAdapter);
//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(CommentShowActivity.this,VERTICAL);
//        comment_recyclerView.addItemDecoration(decoration);

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

    ;
    /* 이어보기 , 처음보기 버튼 활성 변경 */
    public void setReadButtonUI() {
        Log.d(TAG, "setReadButtonUI: first_chapter_id" + first_chapter_id + "chapter_id" + chapter_id);
        if (first_chapter_id > 1 && chapter_id > 1) { // 최초보기 , 이어보기 둘 다 존재하는 경우
            binding.buttonReadFirst.setEnabled(true);
            binding.buttonReadContinue.setEnabled(true);
        } else if (first_chapter_id < 1) { // 최초 보기 회차 존재하지 않는 경우
            binding.buttonReadFirst.setEnabled(false);
            binding.buttonReadContinue.setEnabled(false);
        } else if (chapter_id < 1) { // 이어보기 회차 존재하지 않는 경우
            binding.buttonReadFirst.setEnabled(true);
            binding.buttonReadContinue.setEnabled(false);
        }
    }


}