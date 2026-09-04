package com.example.ourbook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ReadService;
import com.example.ourbook.DataTool.TimeAgo;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityReadNovelBinding;
import com.example.ourbook.databinding.ModalReadNovelBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadNovelActivity extends AppCompatActivity {
    private static final String TAG = " ReadNovelActivity "+Constants.AddTAG;
    private ActivityReadNovelBinding binding;
    private ModalReadNovelBinding modalReadNovelBinding;
    private RetrofitGenerator retrofitGenerator;
    private int chapter_id;
    private  Dialog dialog;

    private int type;
    private ReadService readService;
    private Map<String,Object> requestReadMap = new HashMap<>(); // 첫번째 request
    private Map<String,Object> second_requestReadMap = new HashMap<>();
    private String jwt;
    private int wid;

    private UserSingletone userSingletone;
    private NormalResponseDTO rChapterDataDTO;
    private String title; // 회차의 제목
    private String content; // 회차의 내용
    private int history_id; // 기록 정보 id
    private int position = 0; // 현재의 위치
    private float percent = 0; // 현재의 위치 퍼센트
    // 서버에서 받은 마지막 조회 위치. position 은 스크롤에 따라 계속 갱신되므로 별도로 보존한다.
    private int savedPosition = 0;
    private String readDate ;

    public ReadNovelActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadNovelBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Intent intent = getIntent();
//        String content = intent.getStringExtra("content");
        type = intent.getIntExtra(Constants.INTENT_TYPE,-1);
        wid = intent.getIntExtra(Constants.INTENT_WID,-1);
        chapter_id = intent.getIntExtra(Constants.INTENT_CHAPTER_ID,-1);


        if(type == -1 ){
            Toast.makeText(getApplicationContext()," 잘못된 접근입니다. code type null",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(wid == -1){
            Toast.makeText(getApplicationContext()," 잘못된 접근입니다. code wid null",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //        binding.textViewChapterContent.setText(content);
        if(chapter_id == -1 ){
            Toast.makeText(getApplicationContext(),"잘못된 접근입니다. code cid ",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //modal 창 ui 초기화
        initModal();
        // seekBar ui 초기화
        initSeekBar();
        initScroll();
        // 레트로핏 서비스 초기화
        retrofitGenerator = new RetrofitGenerator();
        readService = retrofitGenerator.init_user_retrofit(ReadService.class);
        // 싱글턴 jwt 사용을 위해 초기화 , 이외에는 지양하려고 사용하지 않음
        userSingletone = UserSingletone.getMyUser();
        jwt = userSingletone.getUserJWT();

        // 각 book info activity 에서 1. 처음 읽기 , 2. 이어 읽기 , 3. chapter activity 에서 선택 읽기 에 따라 다르게 처리
        if (type == Constants.Read.TYPE_FIRST_READ) {
            // 처음 읽는 경우
            setReadConfig();

            return;
        } else if (type == Constants.Read.TYPE_SELECT_READ) {
            // 선택해서 처음 읽는 경우
            // 처음 읽거나 이전에 읽었거나 처음 회차 정보 list 에서 처음 부터 data 를 설정하면 좋을듯
//            second_requestReadMap.clear();
//            second_requestReadMap.put(Constants.REQUEST_CHAPTER_ID,chapter_id);
//            Call<NormalResponseDTO> getReadData = readService.getReadData(jwt,second_requestReadMap);
            setReadConfig();

//            Toast.makeText(getApplicationContext()," 잘못된 접근입니다. code type "+ type ,Toast.LENGTH_SHORT).show();
//            finish();
//            return;
        }else if (type == Constants.Read.TYPE_CONTINUE_READ) {
            // 이어서 읽는 경우 , modal 로 해당 위치로 이동 할 것인지 물어보는 ui 추가
            setReadConfig();

        }else {
            Toast.makeText(getApplicationContext()," 잘못된 접근입니다. code type "+ type ,Toast.LENGTH_SHORT).show();
            finish();

        }




    }//onCreate()

    /* 화면을 벗어날 때 마지막으로 읽던 위치를 저장한다.
     * onPause 는 뒤로가기 , 홈 버튼 , 앱 전환을 모두 포괄한다. */
    @Override
    protected void onPause() {
        super.onPause();
        // 잘못된 접근으로 onCreate 가 일찍 끝난 경우 readService 가 초기화되어 있지 않다
        if (readService == null) {
            return;
        }
        // 한 번도 스크롤하지 않았으면 저장할 위치가 없다
        if (position <= 0) {
            return;
        }
        upsertUserReadData();
    }

    private void setFirstRead(){
        //1.  데이터를 가져온다
        //1-1 fieldMap 설정
        requestReadMap.put(Constants.REQUEST_WID , wid);
        //1-2 레트로핏 데이터 요청
        Call<NormalResponseDTO> firstReadDataCall = readService.readFirstWebNovel(jwt,requestReadMap);
        firstReadDataCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                //PHP - getResponseArrayWithData(200,true,"성공",$result)
                rChapterDataDTO = response.body();
                if(rChapterDataDTO== null){return;}
                if(rChapterDataDTO.isSuccess()) {
                    //제목, 내용 정보 초기화
                    title = String.valueOf(rChapterDataDTO.getData().get(Constants.RESPONSE_DATA_ChapterName));
                    content = String.valueOf(rChapterDataDTO.getData().get(Constants.RESPONSE_DATA_ChapterContent));
                    Log.d(TAG, "onResponse: title" + title);
                    binding.materialToolbarRead.setTitle(title);
                    binding.textViewChapterContent.setText(content);

                    //TODO  성공 여부에 따라 history onDuplicate key ~ 사용 해야 겠다 정보 update or insert

                }else {// retrofit 응답은 성공했지만 DB, 서버상의 코드 오류
                    Toast.makeText(getApplicationContext(),"서버 응답 실패"+rChapterDataDTO.getMessage(),Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.e(TAG, "onFailure: "+ t.getMessage() );
            }
        });
    }
    private void setReadConfig( ){
        //1.  데이터를 가져온다
        //1-1 fieldMap 설정
        requestReadMap = new HashMap<>();
        requestReadMap.put(Constants.REQUEST_WID , wid);
        requestReadMap.put(Constants.REQUEST_CHAPTER_ID,chapter_id);
        Call<NormalResponseDTO> getChapterDataCall = readService.getChapterData(jwt,requestReadMap);
        getChapterDataCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                rChapterDataDTO = response.body();
                if(rChapterDataDTO== null){return;}
                if(rChapterDataDTO.isSuccess()) {
                    //제목, 내용 정보 초기화
                    title = String.valueOf(rChapterDataDTO.getData().get(Constants.RESPONSE_DATA_ChapterName));
                    content = String.valueOf(rChapterDataDTO.getData().get(Constants.RESPONSE_DATA_ChapterContent));
                    binding.textViewChapterContent.setText(content);
                    binding.materialToolbarRead.setTitle(title);
                    if(rChapterDataDTO.getData().get("history_id") == null){
                        Log.d(TAG, "setReadConfig onResponse: history_id == null");
                        upsertUserReadData(); // 갱신
                        return;
                    }else {
                        history_id = Integer.parseInt(Objects.requireNonNullElse(rChapterDataDTO.getData().get("history_id"),"0").toString());
                        position = Integer.parseInt(Objects.requireNonNullElse(rChapterDataDTO.getData().get("position"),"0").toString());
                        percent = Float.parseFloat(Objects.requireNonNullElse(rChapterDataDTO.getData().get("percent"),"0.0").toString());
                        savedPosition = position;
                        readDate = String.valueOf(rChapterDataDTO.getData().get("history_date"));
                        if(history_id != 0){
                            String dateText = readDate +"\n 마지막으로 조회했던 기록이 있습니다 이동하시겠습니까?";
                            modalReadNovelBinding.modalTexViewRead.setText(dateText);
                            dialog.show();
                            // 기록이 존재 하는 경우


                        }
                    };


                    //TODO  성공 여부에 따라 history onDuplicate key ~ 사용 해야 겠다 정보 update or insert


//                    secondCall.enqueue(new Callback<NormalResponseDTO>() {
//                        @Override
//                        public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
//                            NormalResponseDTO secondRespondDto = response.body();
//                            if(secondRespondDto == null){return;}
//                            if(secondRespondDto.isSuccess()){
//
//                            }else {
//                                Log.d(TAG, "second onResponse: "+ " 응답 실패 " + secondRespondDto.getMessage());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
//                            Log.e(TAG, " second onFailure: "+t.getMessage() );
//                        }
//                    });

                }else {// retrofit 응답은 성공했지만 DB, 서버상의 코드 오류
                    Toast.makeText(getApplicationContext(),"서버 응답 실패"+rChapterDataDTO.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.e(TAG, " setReadConfig onFailure: "+ t.getMessage() );

            }
        });
    }
    /* 서버로 */
    private void checkUserReadData(){
    }
    private void setContinueRead(){
    }


    /* 사용자의 웹소설 회차의 읽기 정보 데이터를 업데이트 한다. 조회수 , 회차 기록 */
    // 1. 회차 기록 정보 ( 조건  1)  전부 다 보았는지 2) 아니라면 어디까지 보았는지의 정보를 서버로 전달  )
    // 문단 , 글자 시작 숫자 , ( 끝나는 숫자는 북마크 , 형광펜 기능에서 구현 )
    // 2. 조회수
    // ( 조건 회차별 조회수는 최대 1 증가 되고 chapter , webnovel table 에 각각 저장된다
    //  가장 먼저 DB 에서 view table 에 unique constraint 설정이 되어 중복입력이 안되는 것을 활용
    //  IF views 에 data가 정상적으로 입력되는 존재하지 않는 경우 +1 ELSE 그렇지 않는 경우 중단되게 설정 한다 )
    // 3. 북마크 TODO 아직 하지 않음
    //

    private void upsertUserReadData(){
        Log.d(TAG, "upsertUserReadData: 조회 기록 데이터 갱신 ");
        second_requestReadMap.clear();
        second_requestReadMap.put(Constants.REQUEST_CHAPTER_ID,chapter_id);
        second_requestReadMap.put(Constants.REQUEST_WID,wid);
        second_requestReadMap.put(Constants.REQUEST_HISTORY_Percent,percent);
        second_requestReadMap.put(Constants.REQUEST_HISTORY_Position,position);
        Call<NormalResponseDTO> putReadDataCall = readService.upsertReadData(jwt,second_requestReadMap);
        putReadDataCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                NormalResponseDTO normalResponseDTO = response.body();
                if(normalResponseDTO == null){return;}
                if(normalResponseDTO.isSuccess()){
                    Log.d(TAG, "onResponse: 유저 조회 기록 갱신 성공");
                }else {
                    Log.d(TAG, "onResponse: 유저 조회 기록 갱신 실패" + normalResponseDTO.getMessage());

                }

            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Log.d(TAG, "onFailure: "+ t.getMessage());
            }
        });
    }

    /*잡다 UI 초기화*/
    // modal 초기화
    private void initModal(){
        // Modal 창을 띄우는 코드
        dialog = new Dialog(ReadNovelActivity.this);
        dialog.setContentView(R.layout.modal_read_novel);
        modalReadNovelBinding = ModalReadNovelBinding.inflate(dialog.getLayoutInflater());
        dialog.setContentView(modalReadNovelBinding.getRoot());

        Button btnConfirm = modalReadNovelBinding.btnConfirm;
        Button btnCancel = modalReadNovelBinding.btnCancel;

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 확인 버튼 클릭 시 처리 : 마지막으로 읽던 위치로 이동
                // 본문 setText 직후라 아직 레이아웃 측정이 끝나지 않았을 수 있어 post 로 미룬다
                binding.webNovelReadScroll.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.webNovelReadScroll.scrollTo(0, savedPosition);
                    }
                });
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 취소 버튼 클릭 시 처리
                Toast.makeText(ReadNovelActivity.this, "취소 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
                upsertUserReadData();
                dialog.dismiss();
            }
        });


    }
    // scroll 초기화
    private void initScroll(){
        binding.webNovelReadScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int totalHeight = binding.webNovelReadScroll.getChildAt(0).getHeight() - binding.webNovelReadScroll.getHeight();
                int scrollY = binding.webNovelReadScroll.getScrollY();
                // 본문이 화면보다 짧으면 totalHeight 가 0 이하가 되어 0 나누기가 된다
                if (totalHeight <= 0) {
                    return;
                }
                int progress = (int) ((scrollY / (float)totalHeight) * 100);
                binding.seekBarRead.setProgress(progress);
                // 여기서 갱신하지 않으면 upsertUserReadData() 가 항상 초기값을 보낸다
                position = scrollY;
                percent = (scrollY * 100f) / totalHeight;
            }
        });
        // click 리스너
        binding.textViewChapterContent.setOnClickListener(v -> {
            // 현재 가시성 상태에 따라 반대로 설정
            if (binding.materialToolbarRead.getVisibility() == View.VISIBLE) {
                binding.materialToolbarRead.setVisibility(View.GONE);
                binding.seekBarRead.setVisibility(View.GONE);
            } else {
                binding.materialToolbarRead.setVisibility(View.VISIBLE);
                binding.seekBarRead.setVisibility(View.VISIBLE);
            }

        });

    }
    // seek bar 초기화
    private void initSeekBar(){

        binding.seekBarRead.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int totalHeight = binding.webNovelReadScroll.getChildAt(0).getHeight();
                    int y = (int) ((progress / 100.0) * totalHeight);
                    binding.webNovelReadScroll.scrollTo(0, y);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}