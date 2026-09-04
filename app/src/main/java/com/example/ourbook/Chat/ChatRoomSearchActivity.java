package com.example.ourbook.Chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.example.ourbook.Chat.DTO.ChatRoomDTO;
import com.example.ourbook.Chat.DTO.SearchChatRoomDTO;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Recycle.Adapter.ChatRoomAdapter;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ChatService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.databinding.ActivityChatRoomSearchBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomSearchActivity extends AppCompatActivity {
    /* 채팅방 검색 액티비티 이후 검색내용 , item 클릭시 채팅방 프로필 페이지로 이동 */
    private final String TAG = " ChatRoomSearchActivity "+ Constants.AddTAG;
    private ActivityChatRoomSearchBinding binding;
    private RecyclerView recyclerView;
    private RecyclerView my_recyclerView;
    private ChatRoomAdapter chatRoomAdapter;
    private ChatRoomAdapter my_chatRoomAdapter;
    private SearchChatRoomDTO resultSearchItemList = new SearchChatRoomDTO();
    private ChatService chatService;
    private RetrofitGenerator retrofitGenerator;
    private UserSingletone userSingletone;
    private Map<String,Object> requestMap = new HashMap<>();
    DividerItemDecoration dividerItemDecoration ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        /* toolbar 뒤로가기 버튼 */
        binding.toolbar.setNavigationOnClickListener(v -> {finish();});
        userSingletone = UserSingletone.getMyUser();
        // retrofit
        retrofitGenerator = new RetrofitGenerator();
        chatService =  retrofitGenerator.init_user_retrofit(ChatService.class);
        // recyclerView  초기화
        recyclerView = binding.searchRecyclerView;
        my_recyclerView = binding.searchMyRecyclerView;
        dividerItemDecoration = new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL);

        chatRoomAdapter = new ChatRoomAdapter(resultSearchItemList.otherChatRoomList , this , ChatRoomAdapter.SEARCH_TYPE);
        my_chatRoomAdapter = new ChatRoomAdapter(resultSearchItemList.myChatRoomList , this , ChatRoomAdapter.SEARCH_TYPE);
        initSearchView();


    }

    @Override
    protected void onResume() {
        super.onResume();
//        initSearchView();
    }

    private void initSearchView(){
        binding.searchView.setSubmitButtonEnabled(true);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: query"+ query);
                binding.searchView.clearFocus();
                searchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: newText" + newText);
                return true;
            }
        });
    }

    private void searchQuery(String query){
        requestMap.put("query",query);
        // SearchChatRoomDTO 안에 두개의 List 가 존재한다
        // 1. 내가 들어가있는 채팅방 리스트 , 2. 내가 들어가 있지 않는 채팅방 리스트
        Call<SearchChatRoomDTO> searchChatRoomCall = chatService.searchChatRoom(userSingletone.getUserJWT(),requestMap);
        searchChatRoomCall.enqueue(new Callback<SearchChatRoomDTO>() {
            @Override
            public void onResponse(Call<SearchChatRoomDTO> call, Response<SearchChatRoomDTO> response) {
                resultSearchItemList = response.body();
                if( response.isSuccessful()){
//                    resultSearchItemList.get(0);
                    if(resultSearchItemList == null ){ return;}
                    // TODO 외부 검색 결과 목록 , 나머지 사용자의 검색 결과도 출력 하게끔 설정
                    Log.d(TAG, "onResponse: message "+ resultSearchItemList.message);
                    if(resultSearchItemList.otherChatRoomList.size() == 0){
                        binding.textViewSearchResult.setText(" 결과물이 없습니다.");
                        generateDataList(resultSearchItemList.otherChatRoomList);
                        my_generateDataList(resultSearchItemList.myChatRoomList);
                        chatRoomAdapter.notifyDataSetChanged();
                        my_chatRoomAdapter.notifyDataSetChanged();
                        return;
                    }else {
                        String search_result = resultSearchItemList.otherChatRoomList.size() + " 개의 검색 결과가 있습니다.";
                        binding.textViewSearchResult.setText(search_result);
                    }
                    Log.d(TAG, "onResponse: resultSearchItemList.get(0); "+ resultSearchItemList.otherChatRoomList.get(0).getChat_room_name());
                    Log.d(TAG, "onResponse: size "+ resultSearchItemList.otherChatRoomList.size());
                    generateDataList(resultSearchItemList.otherChatRoomList);
                    my_generateDataList(resultSearchItemList.myChatRoomList);
                    chatRoomAdapter.notifyDataSetChanged();
                    my_chatRoomAdapter.notifyDataSetChanged();
                }else {
                    Log.d(TAG, "onResponse:  is fail");
                }
            }

            @Override
            public void onFailure(Call<SearchChatRoomDTO> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage() );

            }
        });
    }

    private void generateDataList(List<ChatRoomDTO> resultSearchItemList) {

        chatRoomAdapter = new ChatRoomAdapter(resultSearchItemList,this,ChatRoomAdapter.SEARCH_TYPE );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ChatRoomSearchActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatRoomAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
    private void my_generateDataList(List<ChatRoomDTO> resultSearchItemList) {
        if(!resultSearchItemList.isEmpty()){
            binding.searchMyRecyclerView.setVisibility(View.VISIBLE);
            binding.textViewMySearchResult.setVisibility(View.VISIBLE);
        }else {
            binding.searchMyRecyclerView.setVisibility(View.GONE);
            binding.textViewMySearchResult.setVisibility(View.GONE);
            return;
        }
        my_chatRoomAdapter = new ChatRoomAdapter(resultSearchItemList,this,ChatRoomAdapter.SEARCH_TYPE );
        RecyclerView.LayoutManager my_layoutManager = new LinearLayoutManager(ChatRoomSearchActivity.this);
        my_recyclerView.setLayoutManager(my_layoutManager);
        my_recyclerView.setAdapter(my_chatRoomAdapter);
        my_recyclerView.addItemDecoration(dividerItemDecoration);
    }
}