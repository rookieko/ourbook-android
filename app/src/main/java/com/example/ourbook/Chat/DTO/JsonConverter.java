package com.example.ourbook.Chat.DTO;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.Optional;

public class JsonConverter{
        // TODO JSON Converter 로  Socket 으로 서버에서 오는 메세지 그리고 Client 로 보낼 Json String 을 만들기 위해서 사용한다.

        public JsonConverter() {
            this.moshi = new Moshi.Builder().build();
            this.messageJsonAdapter = moshi.adapter(ChatMessageDTO.class);
            this.initJsonAdapter = moshi.adapter(InitUserDTO.class);
            this.defaultJsonAdapter = moshi.adapter(DefaultDTO.class);
            this.chatRoomUserDTOJsonAdapter = moshi.adapter(ChatRoomUserDTO.class);
        }

        // Moshi 를 사용한 컨버터
        private  Moshi moshi ;
        private JsonAdapter<ChatMessageDTO> messageJsonAdapter;
        private JsonAdapter<InitUserDTO> initJsonAdapter ;
        private  JsonAdapter<DefaultDTO> defaultJsonAdapter;
        private  JsonAdapter<ChatRoomUserDTO> chatRoomUserDTOJsonAdapter;

    public JsonAdapter<ChatMessageDTO> getMessageJsonAdapter() {
        return messageJsonAdapter;
    }

    public JsonAdapter<InitUserDTO> getInitJsonAdapter() {
        return initJsonAdapter;
    }

    public JsonAdapter<DefaultDTO> getDefaultJsonAdapter() {
        return defaultJsonAdapter;
    }

    public JsonAdapter<ChatRoomUserDTO> getChatRoomUserDTOJsonAdapter() {
        return chatRoomUserDTOJsonAdapter;
    }

    // 처음 init , jwtToken 을 확인 하기위해서 사용함
        public Optional<DefaultDTO> fromJsonDefaultDTO(String JsonString) {
            DefaultDTO defaultDTO;

            Optional<DefaultDTO> optional ;
            try {
                defaultDTO = defaultJsonAdapter.fromJson(JsonString);
                optional = Optional.ofNullable(defaultDTO);
                return optional;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("error in parsing fromJsonDefaultDTO");
                e.printStackTrace();
                return Optional.ofNullable(null);
            }

        }

        public Optional<ChatMessageDTO> fromJsonChatMessage(String JsonString){
            ChatMessageDTO chatMessageDTO;

            Optional<ChatMessageDTO> optional ;
            try {
                chatMessageDTO = messageJsonAdapter.fromJson(JsonString);
                optional = Optional.ofNullable(chatMessageDTO);
                return optional;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("error in parsing ChatMessageDTO");
                e.printStackTrace();
                return Optional.ofNullable(null);
            }
        };


class ParsingNullExeption extends Exception {

}

}