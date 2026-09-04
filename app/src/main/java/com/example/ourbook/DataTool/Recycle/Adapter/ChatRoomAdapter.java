package com.example.ourbook.DataTool.Recycle.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ourbook.Chat.ChatRoomActivity;
import com.example.ourbook.Chat.ChatRoomProfileActivity;
import com.example.ourbook.Chat.DTO.ChatRoomDTO;
import com.example.ourbook.Chat.DTO.ChatRoomDetailDTO;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.BaseUrl;
import com.example.ourbook.R;
import com.example.ourbook.databinding.ItemMainChatRoomBinding;
import com.example.ourbook.databinding.ItemSearchChatRoomBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SEARCH_TYPE = 2;
    public static final int MAIN_TYPE = 1;
    private int nowViewType ;
    private static final String TAG = "ChatRoomAdapter" + Constants.AddTAG;
    private List<ChatRoomDTO> roomDataList = new ArrayList<>() ;


    private Context context;

    public ChatRoomAdapter(List<ChatRoomDTO> roomDataList, Context context , int viewType ) {
        this.roomDataList = roomDataList;
        this.context = context;
        this.nowViewType = viewType;
    };

    @Override
    public int getItemViewType(int position) {
        // 기존 한 item 의 종류를 view Type 을 나누기 위해 사용하는 방식이 아닌 그냥 Adapter 재사용 검색 , Main , 나누기 위해서 사용하였음
        return nowViewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MAIN_TYPE ) {
            @NonNull ItemMainChatRoomBinding binding = ItemMainChatRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ChatRoomViewHolder(binding);
        }else if (viewType == SEARCH_TYPE){
            @NonNull ItemSearchChatRoomBinding binding = ItemSearchChatRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SearchChatRoomViewHolder(binding);
        };
        Log.d(TAG, "onCreateViewHolder: null 발생  onCreateViewHolder");
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ChatRoomViewHolder){

            ((ChatRoomViewHolder) holder).onBindMain(roomDataList.get(position));
            holder.itemView.setOnClickListener(v -> {
                Log.d(TAG, "onBindViewHolder: on main ");
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra(Constants.INTENT_CHATROOM_ID,roomDataList.get(position).getChat_room_id());
                intent.putExtra(Constants.INTENT_CHATROOM_USER_ID,roomDataList.get(position).getChatRoomUserID()); // 이미 참가

                context.startActivity(intent);
            });

        } else if (holder instanceof SearchChatRoomViewHolder) {

            ((SearchChatRoomViewHolder) holder).onBindSearch(roomDataList.get(position));
            holder.itemView.setOnClickListener(v -> {
                Log.d(TAG, "onBindViewHolder: click " + position);
                Intent intent = new Intent(context, ChatRoomProfileActivity.class);
                intent.putExtra(Constants.INTENT_CHATROOM_ID,roomDataList.get(position).getChat_room_id());
                context.startActivity(intent);
            });

        }

    }
    @SuppressLint("NotifyDataSetChanged")
    public void setRoomDataList(List<ChatRoomDTO> mchatDTOList ){
        if( mchatDTOList == null ||(mchatDTOList.isEmpty()) ){
            Log.d(TAG, "setRoomDataList:  null or empty in adapter ");
            return;
        }else {
            roomDataList = mchatDTOList;
            notifyDataSetChanged();
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setRoomDataDetail(long chatRoomId , ChatRoomDetailDTO chatRoomDetailDTO){
//        chatRoomDetailDTO.getChatRoomUserNumber();
        for (ChatRoomDTO chatRoomDto :
                roomDataList) {
            if( chatRoomDto.getChat_room_id() == (int) chatRoomId){
                chatRoomDto.lastContent = chatRoomDetailDTO.getLastChatContent();
                chatRoomDto.notReadChatNumbers = chatRoomDetailDTO.getChatNumber();

                chatRoomDto.setTotal_user_number(chatRoomDetailDTO.getChatRoomUserNumber());
                notifyDataSetChanged();
            };
        }
    }

    ;

//    @Override
//    public void onBindViewHolder(@NonNull ChatRoomAdapter.ChatRoomViewHolder holder, int position) {
//
//    }

    @Override
    public int getItemCount() {
        return roomDataList.size();
    }

    public class ChatRoomViewHolder extends RecyclerView.ViewHolder  {
        ItemMainChatRoomBinding binding;
        String webNovelNamePrefix = "웹소설 : ";
        public ChatRoomViewHolder(@NonNull ItemMainChatRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
        public void onBindMain(ChatRoomDTO mdata){

            // 웹소설 : 웹소설 제목
            String webNovelNameFull = webNovelNamePrefix + mdata.getNovel_title();
            binding.itemMainChatRoomName.setText(mdata.getChat_room_name());
            binding.itemMainChatRoomNovelName.setText(webNovelNameFull);
            // book cover image
            if(mdata.getCoverImage()!= null && !mdata.getCoverImage().contentEquals("null")){
                Glide.with(context).load(BaseUrl.BookCoverImage_URL+mdata.getCoverImage()).into(binding.itemMainChatRoomImageCover);
            }else {
                Glide.with(context).load(R.drawable.sample_bookcover).into(binding.itemMainChatRoomImageCover);
            }
            // 마지막 메세지 내용
            if(mdata.lastContent != null && !mdata.lastContent.trim().isEmpty()){
                binding.itemMainChatRoomLastMessageLayout.setVisibility(View.VISIBLE);
                binding.itemMainChatRoomLastMessage.setText(mdata.lastContent);
            }else {
                binding.itemMainChatRoomLastMessageLayout.setVisibility(View.INVISIBLE);
            }
            // 안 읽은 메세지 수
            if( mdata.notReadChatNumbers > 0 ){
                String chatNumber = String.valueOf(mdata.notReadChatNumbers);
               binding.itemMainChatRoomNumberNewMessage.setVisibility(View.VISIBLE);
               binding.itemMainChatRoomNumberNewMessage.setText(chatNumber);
            }else {
                binding.itemMainChatRoomNumberNewMessage.setVisibility(View.INVISIBLE);
            }
            // 채팅방 사용자의 수
            if(mdata.getTotal_user_number() >=0){
                String userNumber = String.valueOf(mdata.getTotal_user_number());
                binding.itemMainChatRoomNumberPeople.setVisibility(View.VISIBLE);
                binding.itemMainChatRoomNumberPeople.setText(userNumber);

            }else {
                binding.itemMainChatRoomNumberPeople.setVisibility(View.INVISIBLE);

            }
        }
    }
    public class SearchChatRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ItemSearchChatRoomBinding binding;
        String webNovelNamePrefix = "웹소설 : ";
        public SearchChatRoomViewHolder(@NonNull ItemSearchChatRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void onBindSearch(ChatRoomDTO mdata){
            // 웹소설 : 웹소설 제목
            String webNovelNameFull = webNovelNamePrefix + mdata.getNovel_title();
            binding.itemSearchChatRoomName.setText(mdata.getChat_room_name());
            binding.itemSearchChatRoomNovelName.setText(webNovelNameFull);
            // room
            /*방 생성 날짜 */
            String tempDate = mdata.getRoom_create_date().trim().substring(0,10);
            binding.itemSearchChatRoomDate.setText(tempDate);
            /*방 최대 인원수*/
            String memberNumber = String.valueOf(mdata.getTotal_user_number())  + " / " + "30";
            binding.itemSearchChatRoomNumberPeople.setText(memberNumber);

            // room introduce
            if(!mdata.getChat_room_intro().contentEquals("null")){
                binding.itemSearchChatRoomIntroduceLayout.setVisibility(View.VISIBLE);
                binding.itemSearchChatRoomIntroduce.setText(mdata.getChat_room_intro());
            }else {
                binding.itemSearchChatRoomIntroduceLayout.setVisibility(View.INVISIBLE);
            }
            // room is lock ?
            if(mdata.getRoom_lock() > 0){
               binding.itemMainChatRoomTypeImageView.setVisibility(View.VISIBLE);
            }else {
                binding.itemMainChatRoomTypeImageView.setVisibility(View.INVISIBLE);
            }
            // book cover image
            if(!mdata.getCoverImage().contentEquals("null")){
                Glide.with(context).load(BaseUrl.BookCoverImage_URL+mdata.getCoverImage()).into(binding.itemSearchChatRoomImageCover);
            }else {
                Glide.with(context).load(R.drawable.sample_bookcover).into(binding.itemSearchChatRoomImageCover);
            }
        }

        @Override
        public void onClick(View v) {
            // 동작 안함
            Log.d(TAG, "onClick: in view holder on click ");
        }
    }
}
