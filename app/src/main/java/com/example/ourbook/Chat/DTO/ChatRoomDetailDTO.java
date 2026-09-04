package com.example.ourbook.Chat.DTO;

import com.squareup.moshi.Json;

public class ChatRoomDetailDTO extends ChatRoomDTO{
    // 원래 ChatRoomDTO 와 별도로 처리하려고 계획했는데 ChatRoomAdapter 에 ChatRoomDTO 와 너무 연결되어 있는 나머지
    // 시간상 대체 할 수 없었음 둘 다 사용함..
    @Json(name = "unread_messages_count")
    private int chatNumber;

    @Json(name = "last_chat_content")
    private String lastChatContent;

//    @Json(name = "user_last_chat_id")
//    private long userLastChatId;

    @Json(name = "chat_room_user_number")
    private int chatRoomUserNumber;

    public void setChatRoomUserNumber(int chatRoomUserNumber) {
        this.chatRoomUserNumber = chatRoomUserNumber;
    }

    public int getChatNumber() {
        return chatNumber;
    }

    public String getLastChatContent() {
        return lastChatContent;
    }

//    public long getLastChatId() {
//        return userLastChatId;
//    }

    public int getChatRoomUserNumber() {
        return chatRoomUserNumber;
    }
}
