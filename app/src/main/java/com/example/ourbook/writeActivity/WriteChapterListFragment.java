package com.example.ourbook.writeActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.Chapter;
import com.example.ourbook.DataTool.Response.ChapterItem;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.DataTool.Writer.W_ChapterAdapter;
import com.example.ourbook.setBookActivity.ChangeChapterActivity;
import com.example.ourbook.databinding.FragmentWriteChapterListBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WriteChapterListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/* 작성 , 작성하는 웹소설 선택 이후  회차 리스트를 보여주는 fragment  회차 선택 이후 changeChapter activity 로 이동 */
public class WriteChapterListFragment extends Fragment {
    private static final String TAG = "WriteChapterListFragment 작성자 페이지 Fragment "+ Constants.AddTAG;

    private FragmentWriteChapterListBinding binding;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService; // interface for Retrofit
    private RecyclerView recyclerView; // 회차 리스트를 보여주는 웹소설 회차 리사이클러뷰
    private W_ChapterAdapter wChapterAdapter; //리사이클러뷰

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private UserSingletone userSingletone;
    public WriteChapterListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WriteChapterListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WriteChapterListFragment newInstance(String param1, String param2) {
        WriteChapterListFragment fragment = new WriteChapterListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSingletone = UserSingletone.getMyUser();
        retrofitGenerator = new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWriteChapterListBinding.inflate(inflater,container,false);
        recyclerView = binding.chapterRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Call<ChapterItem> chapterItemCall = registerBookService.getWriteChapterData(userSingletone.getUserJWT(),userSingletone.getWid());
        chapterItemCall.enqueue(new Callback<ChapterItem>() {
            @Override
            public void onResponse(Call<ChapterItem> call, Response<ChapterItem> response) {
                ChapterItem chapterItem = response.body();
                if (chapterItem == null){
                    Log.d(TAG, "onResponse: chapterItem null  회차 정보 없음");
                    return;}
                int lastNum = chapterItem.getLastNum();
                //마지막 회차 번호 초기화
                userSingletone.setLastNum(lastNum);
                
                wChapterAdapter = new W_ChapterAdapter( chapterItem.getData(), new W_ChapterAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Chapter chapter) {
                        // 클릭 이벤트 확인
                        Log.d(TAG, "onItemClick: " + chapter.chapter_name);
                        Intent intent = new Intent(getActivity(), ChangeChapterActivity.class);
                        intent.putExtra("title",chapter.chapter_name);
                        intent.putExtra("content",chapter.chapter_content);
                        intent.putExtra("cid",chapter.id);
                        intent.putExtra("num",chapter.num);
                        intent.putExtra("wid",userSingletone.getWid());

                        startActivity(intent);

                    }
                });
                recyclerView.setAdapter(wChapterAdapter);

            }

            @Override
            public void onFailure(Call<ChapterItem> call, Throwable t) {

            }
        });
    }
}