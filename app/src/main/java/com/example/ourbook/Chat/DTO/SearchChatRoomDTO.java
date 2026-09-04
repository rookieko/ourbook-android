package com.example.ourbook.Chat.DTO;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

public class SearchChatRoomDTO extends ChatRoomDTO{

    @Json(name = "message")
    public String message = "null";
    @Json(name ="my")
    public List<ChatRoomDTO> myChatRoomList = new ArrayList<>();

    @Json(name = "others")
    public List<ChatRoomDTO> otherChatRoomList = new ArrayList<>();


}
