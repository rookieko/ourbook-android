package com.example.ourbook.Chat.DTO.Test;

import com.squareup.moshi.Json;

public class ChatMessageTest {

    @Json(name = "username")
    public String userName;

    @Json(name = "message")
    public String message;

    @Json(name = "timestamp")
    public String timestamp; // 예: "8:00"
}
