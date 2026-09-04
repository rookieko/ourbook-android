package com.example.ourbook.Chat.DTO;

import com.squareup.moshi.Json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDTO {

    @Json(name = "method")
    private String method;

    @Json(name = "data")
    private Map<String, Object> data = new HashMap<>();

    @Json(name = "jwt")
    private String jwtToken;
    @Json(name = "fcm_token")
    private String fcmToken;

    @Json(name = "chat_message")
    private ChatMessageDTO chatMessage;

    @Json(name = "chat_room_user")
    private ChatRoomUserDTO chatRoomUserDTO;


    @Json(name = "message")
    public String message;

    @Json(name = "list_chat_room_user")
    private List<ChatRoomUserDTO> listChatRoomUserDTOs;

    public List<ChatRoomUserDTO> getListChatRoomUserDTOs() {
        return listChatRoomUserDTOs;
    }

    public ChatRoomUserDTO getChatRoomUserDTO() {
        return chatRoomUserDTO;
    }

    public void setChatRoomUserDTO(ChatRoomUserDTO chatRoomUserDTO) {
        this.chatRoomUserDTO = chatRoomUserDTO;
    }

    public DefaultDTO(String method) {
        this.method = method;
    }

    public void setChatMessage(ChatMessageDTO chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public ChatMessageDTO getChatMessage() {
        return chatMessage;
    }
}
