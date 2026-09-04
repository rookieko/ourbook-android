package com.example.ourbook.Chat.DTO;

import com.squareup.moshi.Json;

public class ChatRoomDTO {

    @Json(name = "wid")
    private int wid = -1;
    @Json(name = "novel_title")
    private String novel_title ="null";
    @Json(name = "chat_room_id")
    private int chat_room_id = 0;;
    @Json(name = "user_class")
    private int user_class = -1;
    @Json(name = "user_start_chat_id")
    private int user_start_chat_id = 0;
    @Json(name = "total_users_in_room")
    private int total_user_number = 0;

    @Json(name = "room_create_date")
    private String room_create_date = "null";
    @Json(name = "chat_room_name")
    private String chat_room_name = "null";
    @Json(name = "coverImage")
    private String coverImage = "null";
    @Json(name = "chat_room_intro")
    private String chat_room_intro = "null";
    @Json(name = "room_lock")
    private int room_lock = -1;
    @Json(name = "chat_room_user_id")
    private int chatRoomUserID;

    @Json(name = "last_content")
    public String lastContent;
    @Json(name = "n_read_chat_num")
    public int notReadChatNumbers = 0;

    public int getChatRoomUserID() {
        return chatRoomUserID;
    }

    public int getRoom_lock() {
        return room_lock;
    }

    public void setRoom_lock(int room_lock) {
        this.room_lock = room_lock;
    }

    public String getChat_room_intro() {
        return chat_room_intro;
    }

    public void setChat_room_intro(String chat_room_intro) {
        this.chat_room_intro = chat_room_intro;
    }

//    @Json(name = "last_message")
//    private String lastMessage = "null";

//    public String getLastMessage() {
//        return lastMessage;
//    }
//
//    public void setLastMessage(String lastMessage) {
//        this.lastMessage = lastMessage;
//    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public String getNovel_title() {
        return novel_title;
    }

    public void setNovel_title(String novle_title) {
        this.novel_title = novle_title;
    }

    public int getChat_room_id() {
        return chat_room_id;
    }

    public void setChat_room_id(int chat_room_id) {
        this.chat_room_id = chat_room_id;
    }

    public int getUser_class() {
        return user_class;
    }

    public void setUser_class(int user_class) {
        this.user_class = user_class;
    }

    public int getUser_start_chat_id() {
        return user_start_chat_id;
    }

    public void setUser_start_chat_id(int user_start_chat_id) {
        this.user_start_chat_id = user_start_chat_id;
    }

    public int getTotal_user_number() {
        return total_user_number;
    }

    public void setTotal_user_number(int total_user_number) {
        this.total_user_number = total_user_number;
    }

    public String getRoom_create_date() {
        return room_create_date;
    }

    public void setRoom_create_date(String room_create_date) {
        this.room_create_date = room_create_date;
    }

    public String getChat_room_name() {
        return chat_room_name;
    }

    public void setChat_room_name(String chat_room_name) {
        this.chat_room_name = chat_room_name;
    }
}
