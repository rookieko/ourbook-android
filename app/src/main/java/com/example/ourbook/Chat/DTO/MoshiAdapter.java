package com.example.ourbook.Chat.DTO;

import com.example.ourbook.Chat.DTO.Test.ChatMessageTest;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class MoshiAdapter {
    Moshi moshi = new Moshi.Builder().build();
//    String jsonString;
    JsonAdapter<ChatMessageTest> jsonAdapter = moshi.adapter(ChatMessageTest.class);
    public String jsonToString(ChatMessageTest chatMessageTest){
        // 객체를 JSON 문자열로 변환
        String jsonString = jsonAdapter.toJson(chatMessageTest);
        return jsonString;
    }
    public ChatMessageTest StringToChatMessage(String jsonString) throws IOException {

    ChatMessageTest chatMessageTest = jsonAdapter.fromJson(jsonString);
    // JSON 문자열로부터 객체를 파싱
        return chatMessageTest;
    }


}
