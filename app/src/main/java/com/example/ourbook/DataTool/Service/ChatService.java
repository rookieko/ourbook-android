package com.example.ourbook.DataTool.Service;

import com.example.ourbook.Chat.DTO.ChatMessageDTO;
import com.example.ourbook.Chat.DTO.ChatRoomDTO;
import com.example.ourbook.Chat.DTO.ChatRoomDetailDTO;
import com.example.ourbook.Chat.DTO.SearchChatRoomDTO;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ChatService {

    /* 채팅방 생성 필요 데이터
    * 1. 채팅방 이름
    * 2. 채팅방 태그 ( 웹소설 wid )
    * 3. 채팅방 소개 말 ( 비필수 )
    * 4. 채팅방 비밀번호 ( 비필수 )
    * 5.  */
    @FormUrlEncoded
    @POST("OurBook/webnovel/chat/regis-chat-room.php")
    Call<NormalResponseDTO> regisChatRoom (@Header(Constants.HEADER_TOKEN) String Token , @FieldMap Map<String,Object> fieldMap);

    /* 채팅방 참가를 위해서 검색 */
    @FormUrlEncoded
    @POST("OurBook/webnovel/chat/search-chat-room.php")
    Call<SearchChatRoomDTO> searchChatRoom (@Header(Constants.HEADER_TOKEN) String Token , @FieldMap Map<String,Object> fieldMap);
    /* 채팅방 참가 */
    @FormUrlEncoded
    @POST("OurBook/webnovel/chat/join-chat-room.php")
    Call<NormalResponseDTO>  joinChatRoom (@Header(Constants.HEADER_TOKEN) String Token , @FieldMap Map<String,Object> fieldMap);
    /* 채팅방 탈퇴 , 나가기 */
    @FormUrlEncoded
    @POST("")
    Call<NormalResponseDTO> exitChatRoom (@Header(Constants.HEADER_TOKEN) String Token , @FieldMap Map<String,Object> fieldMap);

    /* 사용자의 채팅방 리스트 출력 */
    @FormUrlEncoded
    @POST("OurBook/webnovel/chat/load-chat-room.php")
    Call<List<ChatRoomDTO>> getUserChatRoomList (@Header(Constants.HEADER_TOKEN) String Token , @FieldMap Map<String,Object> fieldMap);


    /*채팅방의 채팅 리스트 출력*/

    @FormUrlEncoded
    @POST("OurBook/webnovel/chat/load-chat.php")
    Call<List<ChatMessageDTO>> getChatDataList (@Header(Constants.HEADER_TOKEN) String Token , @FieldMap Map<String,Object> fieldMap);

    /* 채팅방 정보 출력 */

    @FormUrlEncoded
    @POST("OurBook/webnovel/chat/get-chat-room-info.php")
    Call<ChatRoomDTO> getChatRoomData (@Header(Constants.HEADER_TOKEN) String Token , @FieldMap Map<String,Object> fieldMap);

    @FormUrlEncoded
    @POST("OurBook/webnovel/chat/load-chat-room-detail.php")
    Call<ChatRoomDetailDTO> getChatRoomDataDetail (@Header(Constants.HEADER_TOKEN) String Token , @FieldMap Map<String,Object> fieldMap);
//
//    @FormUrlEncoded
//    @POST("")
//    Call<>
}
