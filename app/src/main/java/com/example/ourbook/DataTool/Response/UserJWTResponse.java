package com.example.ourbook.DataTool.Response;
import com.example.ourbook.DataTool.User;
import com.squareup.moshi.Json;
public class UserJWTResponse {

    // jwtResult jwt 응답 Data status 200 성공 , 301 , 302 기한 오류 , 404 ~ 토큰 오류
    @Json(name = "jwtResult")
    public NormalResponseDTO OJWT_Check = new NormalResponseDTO();
    // token 확인 , 검증된 payload 를 받을 User 객체
    @Json(name = "user-info")
    private User user_info = new User();

    public User getUser_info() {
        return user_info;
    }

    public void setUser_info(User user_info) {
        this.user_info = user_info;
    }

    public String getOBJWToken() {
        return OBJWToken;
    }

    public void setOBJWToken(String OBJWToken) {
        this.OBJWToken = OBJWToken;
    }

    // token
    @Json(name = "Ojwt-Token")
    private String OBJWToken = null;

}
