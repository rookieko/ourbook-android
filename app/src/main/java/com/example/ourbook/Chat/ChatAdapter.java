package com.example.ourbook.Chat;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.ourbook.Chat.DTO.ChatMessageDTO;
import com.example.ourbook.Chat.DTO.ChatRoomUserDTO;
import com.example.ourbook.Chat.DTO.Test.ChatMessageTest;
import com.example.ourbook.Chat.TimeConverter.DateDetails;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.BaseUrl;
import com.example.ourbook.R;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // https://stickode.tistory.com/173 채팅 recyclerView
    private static final String TAG = "ChatAdapter" + Constants.AddTAG;
    private final int OTHERS = 1;
    private final int MY = 2;
    private final int NOT = 5;
    private List<ChatMessageDTO> chatMessageDTOList;

    public List<ChatRoomUserDTO> getRoomUserList() {
        return roomUserList;
    }


    public void setRoomUserList(List<ChatRoomUserDTO> roomUserList) {
        this.roomUserList.clear();
        this.roomUserList.addAll(roomUserList);
        this.notifyDataSetChanged();
    }

    private List<ChatRoomUserDTO> roomUserList = new ArrayList<>();
    private Integer chatRoomUserID;

    public void ChatAdapterNotUse(List<ChatMessageDTO> chatMessageDTOList) {
        this.chatMessageDTOList = chatMessageDTOList;
    }

    public ChatAdapter(List<ChatMessageDTO> mchatMessageTests, Integer mchatRoomUserID) {
        this.chatMessageDTOList = mchatMessageTests;
        this.chatRoomUserID = mchatRoomUserID;
    }


    // 기존 테스트 파일 error 방지
    @Deprecated
    public static ChatAdapter chatAdaptersNotUse(List<ChatMessageTest> chatMessageTests, String username) {
//        this.chatMessageTests = chatMessageTests;
//        this.username = username;
        Log.e(TAG, "chatAdaptersNotUse: error for not use deprecated");
        return new ChatAdapter(null, null);
    }

    ;


//    @Override
//    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other, parent, false);
//        return new ChatViewHolder(view);
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == OTHERS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other, parent, false);
            return new ChatViewHolder(view);
        } else if (viewType == MY) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me, parent, false);
            return new MyChatViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageDTOList.get(position).chatRoomUserId.equals(chatRoomUserID)) {
            return MY;
        } else if (!chatMessageDTOList.get(position).chatRoomUserId.equals(chatRoomUserID)) {
            return OTHERS; // 이게 문제가 아니였음 else 로 변경 가능
        } else {
            return NOT;
        }
//        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageDTO chatMessageTest = chatMessageDTOList.get(position);
        ChatMessageDTO chatMessageTestOld = null;

        if (position > 0) {
            //처음 메세지인 경우
            chatMessageTestOld = chatMessageDTOList.get(position - 1);

        }
        ;
        String date = calculateDate(chatMessageTest, chatMessageTestOld);
        TimeConverter.DateDetails dateDetails = TimeConverter.getNewDate(chatMessageTest.newDate);

        if (holder instanceof ChatViewHolder) {
            // 1. 날짜 설정
            if (date.contentEquals("null")) {
                ((ChatViewHolder) holder).initDate(false);
            } else {
                ((ChatViewHolder) holder).initDate(true);
//                ((ChatViewHolder) holder).setTimestamp(date);
                ((ChatViewHolder) holder).setDate(date);
            }

            //!!! 절차적 당연하게도 알림 , 일반 채팅이더라도 공통으로 가지는 부분 날짜를 보여주는 기능을 맨 앞으로 해야함 return 을 하니까 당연히 코드가 실행이 안되지;;
            // 이외에도 진행해야할 코드로 기존의 채팅방 입장을 php 코드로 작성을 했었는데 이 부분에서 문제가 있었음.
            // 2. 공지 알림 메세지 인지 확인
            if (chatMessageTest == null) {
                return;

            } else if (chatMessageTest.option == 1) {
                // 메세지 타입이 알림일때
                ((ChatViewHolder) holder).initNotification(); // 초기화
                String tempNotify = getUserName(chatMessageTest.chatRoomUserId) + "이 들어왔습니다.";
                ((ChatViewHolder) holder).setNotification(tempNotify);// text 설정
                return;
            } else {
                // 일반 채팅이라서 초기화 진행함 ( 알림 ui GONE // 메시지 ui Visible )
                ((ChatViewHolder) holder).resetChatUi();
            }
//            ((ChatViewHolder) holder).resetChatUi();
            // 수정 하였음 2024.4.12
            // TODO 날짜 수정 long 으로
            // 3. timestamp 설정
//            String tempTimestamp = chatMessageTest.date.substring(10, 16);
            String tempTimestamp = dateDetails.time;
            // timeStamp
            ((ChatViewHolder) holder).setTimestamp(tempTimestamp);
            // 4. 닉네임 설정
            String tempUserName = getUserName(chatMessageTest.chatRoomUserId);
            ((ChatViewHolder) holder).userNameOther.setText(tempUserName);
            // 5. 메세지 설정
            ((ChatViewHolder) holder).messageOther.setText(chatMessageTest.content);
            // 6. 읽음 처리 숫자 설정
            String tempReadNumber = String.valueOf(calculateRead(chatMessageTest.chatId));
            if (tempReadNumber.contentEquals("0")) {
                ((ChatViewHolder) holder).readNumberOther.setVisibility(View.INVISIBLE);
            } else {
                ((ChatViewHolder) holder).readNumberOther.setVisibility(View.VISIBLE);
                ((ChatViewHolder) holder).readNumberOther.setText(tempReadNumber);
            }
            // 7. 프로필 이미지 설정
            String tempProfileImage = getUserProfileImage(chatMessageTest.chatRoomUserId);
            if(tempProfileImage.contentEquals("null")){
                Glide.with(holder.itemView)
                        .load(R.drawable.outline_account_circle_24)
                        .circleCrop()
                        .into(((ChatViewHolder)holder).profileImageOther);
            }else {
                Glide.with(holder.itemView)
                        .load(BaseUrl.ProfileImage_URL+tempProfileImage)
                        .thumbnail(0.1f)
                        .circleCrop()
                        .into(((ChatViewHolder)holder).profileImageOther);
            }

        } else if (holder instanceof MyChatViewHolder) {
            // 1. 날짜 설정
            if (date.contentEquals("null")) {
                ((MyChatViewHolder) holder).initDate(false);
            } else {
                ((MyChatViewHolder) holder).initDate(true);
//                ((ChatViewHolder) holder).setTimestamp(date);
                ((MyChatViewHolder) holder).setDate(date);
            }

            if (chatMessageTest == null) {
                return;
            } else if (chatMessageTest.option.equals(1)) {
                //2.  메세지 타입이 알림일때
                ((MyChatViewHolder) holder).initNotification(); // 초기화
                String tempNotify = getUserName(chatMessageTest.chatRoomUserId) + "이 들어왔습니다.";
                ((MyChatViewHolder) holder).setNotification(tempNotify);// text 설정
                return;
            } else {
                ((MyChatViewHolder) holder).resetChatUi();
//                ((MyChatViewHolder) holder).cardView.setVisibility(View.GONE);
            }
//                ((MyChatViewHolder) holder).notification.setText("");
            //3. timestamp 설정
            String tempDate = dateDetails.time;
//            String tempDate = chatMessageTest.date.substring(10, 16);
//                 String tempDate = chatMessageTest.date;
            ((MyChatViewHolder) holder).setTimestamp(tempDate);
//            ((MyChatViewHolder) holder).userName.setText(chatMessage.userName);
            //4.메세지 설정 ( 닉네임은 안보이니까 )
            ((MyChatViewHolder) holder).messageMy.setText(chatMessageTest.content);
            String tempReadNumber = String.valueOf(calculateRead(chatMessageTest.chatId));

            // 5. 읽음 처리 설정
            if (tempReadNumber.contentEquals("0")) {
                ((MyChatViewHolder) holder).readNumberMy.setVisibility(View.INVISIBLE);
            } else {
                ((MyChatViewHolder) holder).readNumberMy.setVisibility(View.VISIBLE);
                ((MyChatViewHolder) holder).readNumberMy.setText(tempReadNumber);
            }
//            ((MyChatViewHolder) holder).timestamp.setText((int) chatMessageTest.date);
        }
    }

    public String calculateDate(ChatMessageDTO newChatMessage, ChatMessageDTO oldChatMessage) {
        if (oldChatMessage == null) {
            return TimeConverter.getNewDate(newChatMessage.newDate).date;  // 첫 메시지의 경우
        }
        String newDate = TimeConverter.getNewDate(newChatMessage.newDate).date;
        String oldDate = TimeConverter.getNewDate(oldChatMessage.newDate).date;
        if (!newDate.equals(oldDate)) {
            return newDate;  // 날짜가 변경된 경우 새 날짜 반환
        }
        return "null";  // 변경되지 않았다면 "null" 반환
    }

    ;


//    @Override
//    public void onBindViewHolder(ChatViewHolder holder, int position) {
//        ChatMessage chatMessage = chatMessages.get(position);
//        holder.userName.setText(chatMessage.userName);
//        holder.message.setText(chatMessage.message);
//        holder.timestamp.setText(chatMessage.timestamp);
//    }

    @Override
    public int getItemCount() {
        return chatMessageDTOList.size();
    }

    public int  getChatId(int chatId){
        for (ChatMessageDTO chatMessageDTO:
             chatMessageDTOList) {
            if( chatMessageDTO.chatId == chatId){
                int mchatId = chatMessageDTOList.indexOf(chatMessageDTO);
                return mchatId;
            }
        }
        return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addChat(ChatMessageDTO chatMessageTest) {
        if (!CollectionUtils.isEmpty(chatMessageDTOList)) {
            if (chatMessageTest.chatId <= chatMessageDTOList.get(chatMessageDTOList.size() - 1).chatId) {
                Log.e(TAG, "addChat: error for chat message item input timing");
            }
        }
        chatMessageDTOList.add(chatMessageTest);
        this.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setChatMessageDTOList(List<ChatMessageDTO> chatMessageDTOList) {
        this.chatMessageDTOList.clear();
        this.chatMessageDTOList.addAll(chatMessageDTOList);
        this.notifyDataSetChanged();

    }
    public DateDetails getDateTimeUTC(long utcTime){
//        Date date = new Date(utcTime);
        TimeConverter.DateDetails dateDetails = TimeConverter.getNewDate(utcTime);
        return dateDetails;
    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder {

//        ItemChatOtherBinding binding;

        public TextView userNameOther, messageOther, timestampOther, readNumberOther, textViewNotificationOther, textViewDateTimeOther;
        public ImageView profileImageOther;
        public CardView layoutNotificationOther, layoutDateTimeOther;
        public ConstraintLayout layoutChatMessageOther;


        public ChatViewHolder(View itemView) {
            super(itemView);
            userNameOther = itemView.findViewById(R.id.text_chat_user_other);
            messageOther = itemView.findViewById(R.id.text_chat_message_other);
            timestampOther = itemView.findViewById(R.id.text_chat_time_other);
            readNumberOther = itemView.findViewById(R.id.number_peopleLook_other);
            textViewNotificationOther = itemView.findViewById(R.id.text_chat_notification_other);
            textViewDateTimeOther = itemView.findViewById(R.id.text_chat_date_other);
            profileImageOther = itemView.findViewById(R.id.image_chat_profile_other);
            layoutNotificationOther = itemView.findViewById(R.id.layout_chat_notification_other);
            layoutDateTimeOther = itemView.findViewById(R.id.layout_chat_date_other);
            layoutChatMessageOther = itemView.findViewById(R.id.layout_chat_message_other);
        }

        public void setNotification(String notificationString) {

            textViewNotificationOther.setText(notificationString);
//            layoutChatMessageOther.setVisibility(View.GONE);// 1번
//            layoutNotificationOther.setVisibility(View.VISIBLE);// 2번

        }

        // 유저의 입장인 경우 , 공지인 경우 ,
        // 1. chat_message_layout 을 숨긴다.
        // 2. chat_notification_layout 을 보여준다.

        public void initNotification() {
            layoutChatMessageOther.setVisibility(View.GONE);// 1번
            layoutNotificationOther.setVisibility(View.VISIBLE);// 2번
        }

        // 날짜가 바뀌는 경우
        public void initDate(boolean on) {
            if (on) {
                layoutDateTimeOther.setVisibility(View.VISIBLE);
            } else {
                layoutDateTimeOther.setVisibility(View.GONE);
            }
        }

        // 날짜 설정
        public void setDate(String dateString) {
            if (layoutDateTimeOther.getVisibility() == View.GONE) {
                Log.d(TAG, "setDate: GONE TODO Visibility ");
            }
            textViewDateTimeOther.setText(dateString);
        }

        // 초기화 기본값 -> 이게 문제인지 모르겠는데 스크롤시 가려지게 하지 않으면 다른 아이템에서 사용했던 data 가 보여지는
        //  문제가 존재해서 수정을 위해서 코드를 작성함
        // 1. chat_message Ui 보여지게
        // 2. chat_notification Ui 가려지게
        public void resetChatUi() {
            layoutChatMessageOther.setVisibility(View.VISIBLE);
//            itemView.findViewById(R.id.layout_chat_message_other).setVisibility(View.VISIBLE); // 1.번
            layoutNotificationOther.setVisibility(View.GONE);


//            message.setVisibility(View.VISIBLE);
//            timestamp.setVisibility(View.VISIBLE);
//            readNumber.setVisibility(View.VISIBLE);
//            itemView.findViewById(R.id.card_chat_message_other).setVisibility(View.VISIBLE);
//            itemView.findViewById(R.id.text_chat_date_layout2_other).setVisibility(View.GONE);

        }

        // 시 , 분을 표시하기 위한 time_stamp
        public void setTimestamp(String date) {
//            itemView.findViewById(R.id.text_chat_date_layout2_other).setVisibility(View.VISIBLE);
            if ( View.GONE == layoutChatMessageOther.getVisibility() ) {
                Log.d( TAG, "setTimestamp: GONE ? 초기화 되지 않았음 TODO visibility 활성화 " );
            }
            timestampOther.setText(date);
        }

    }

    public static class MyChatViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameMy, messageMy, timestampMy, readNumberMy, textViewNotificationMy, textViewDateTimeMy;
        //        public ImageView profileImage;
        public CardView layoutNotificationMy, layoutDateTimeMy;
        public ConstraintLayout layoutChatMessageMy;

        public MyChatViewHolder(View itemView) {
            super(itemView);
//            userName = itemView.findViewById(R.id.text_chat_user_me);
            messageMy = itemView.findViewById(R.id.text_chat_message_me); // 채팅 메세지
            timestampMy = itemView.findViewById(R.id.text_chat_time_me); // 채팅 옆 시간 , 분
            readNumberMy = itemView.findViewById(R.id.number_peopleLook_me);// 본 사람 숫자
            textViewNotificationMy = itemView.findViewById(R.id.text_chat_notification_me); // 공지 알림
            textViewDateTimeMy = itemView.findViewById(R.id.text_chat_date_me); // 상단 알림 시간 , 날짜
            layoutNotificationMy = itemView.findViewById(R.id.layout_chat_notification_me); // 카드뷰 , 레이아웃 visible, gone 을 사용
            layoutDateTimeMy = itemView.findViewById(R.id.layout_chat_date_me);
            layoutChatMessageMy = itemView.findViewById(R.id.layout_chat_message_me);
//            profileImage = itemView.findViewById(R.id.profile_)
        }

        // 시간에 대한 표시인지 알림인지를 나타내서 보여준다

        // 유저의 입장인 경우 , 공지인 경우 ,
        // 1. chat_message_layout 을 숨긴다.
        // 2. chat_notification_layout 을 보여준다.
        public void initNotification() {
//            message.setVisibility(View.INVISIBLE);
//            timestamp.setVisibility(View.INVISIBLE);
//            readNumber.setVisibility(View.INVISIBLE);
////            notification2.setVisibility(View.GONE);
//            itemView.findViewById(R.id.card_chat_message_me).setVisibility(View.GONE);
            layoutChatMessageMy.setVisibility(View.GONE);
            layoutNotificationMy.setVisibility(View.VISIBLE);
        }

        public void setNotification(String notificationString) {
            textViewNotificationMy.setText(notificationString);
        }

        // 날짜가 바뀌는 경우
        public void initDate(boolean on) {
            if (on) {
                layoutDateTimeMy.setVisibility(View.VISIBLE);
            } else {
                layoutDateTimeMy.setVisibility(View.GONE);
            }
        }

        //날짜 설정
        public void setDate(String dateString) {
            if (View.GONE == layoutDateTimeMy.getVisibility()) {
                Log.d(TAG, "setDate: GONE TODO Visibility");
            }
            textViewDateTimeMy.setText(dateString);
        }

        // 일반 채팅인 경우 공지의 내용이 보여지면 안되기 때문에 따로 Ui 초기화 작업을 진행함
        public void resetChatUi() {

//            message.setVisibility(View.VISIBLE);
//            timestamp.setVisibility(View.VISIBLE);
//            readNumber.setVisibility(View.VISIBLE);
////            notification2.setVisibility(View.GONE);
//            itemView.findViewById(R.id.card_chat_message_me).setVisibility(View.VISIBLE);
//            itemView.findViewById(R.id.text_chat_date_layout2_me).setVisibility(View.GONE);
            layoutChatMessageMy.setVisibility(View.VISIBLE);
            layoutNotificationMy.setVisibility(View.GONE);
        }

        public void setTimestamp(String date) {
            if (layoutChatMessageMy.getVisibility() == View.GONE) {
                Log.d(TAG, "setTimestamp: TODO Visibility error GONE");
            }
            timestampMy.setText(date);

        }

        public void resetTimestamp() {
//            itemView.findViewById(R.id.text_chat_date_layout2_me).setVisibility(View.GONE);

        }

    }

    // chatRoomUserId 를 통해서 유저의 닉네임을 불러온다.
    public String getUserName(Integer chatRoomUserID) {
        if (roomUserList == null) {
            Log.d(TAG, "getUserName: null in List");
            return "null";
        }
        for (ChatRoomUserDTO chatRoomUser :
                roomUserList) {
            if (chatRoomUser.chatRoomUserId.equals(chatRoomUserID)) {
                return chatRoomUser.userName;
            }
        }
        Log.d(TAG, "getUserName: error userId 가 존재하지 않음");
        return "( 나간 사용자 )";
    }
    //chatRoomUserId 를 통해서 유저의 프로필 이미지를 불러온다.
    public String getUserProfileImage(Integer chatRoomUserID){
        if (roomUserList == null) {
            Log.d(TAG, "getUserProfileImage: null in List");
            return "null";
        }
        for (ChatRoomUserDTO chatRoomUser :
                roomUserList) {
            if (chatRoomUser.chatRoomUserId.equals(chatRoomUserID)) {
                return chatRoomUser.profileImage;
            }
        }
        Log.d(TAG, "getUserProfileImage: error userId 가 존재하지 않음");
        return "null";
    }

    // 마지막으로 읽은 chat id 를 불러와서 몇명이 읽었는지를 표시한다.
    // Start,Last Chat Id 의 값들을 읽어서 나타냄
    public Integer calculateRead(Integer chatId) {
        if (roomUserList == null) {
            Log.d(TAG, "calculateRead: null in List");
            return 0;
        }
        int i = roomUserList.size();
        Log.d(TAG, "calculateRead: chaId" + chatId);
        Log.d(TAG, "calculateRead: UserNumber" + i);
        for (ChatRoomUserDTO chatUser :
                roomUserList) {
            if (chatId <= chatUser.lastReadChatId || chatId < chatUser.startReadChatId) {
                Log.d(TAG, "calculateRead: chatUser id :" + chatUser.chatRoomUserId + " start :" + chatUser.startReadChatId + " last : " + chatUser.lastReadChatId);
                Log.d(TAG, "calculateRead: 차감 실행 : " + i);
                i--;
            }
            ;
        }
        Log.d(TAG, "calculateRead: total : " + i);

        return i;

    }

    class CalculatorChat {

    }
}
