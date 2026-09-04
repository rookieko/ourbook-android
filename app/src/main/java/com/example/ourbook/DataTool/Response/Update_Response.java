package com.example.ourbook.DataTool.Response;

import com.example.ourbook.DataTool.User;
import com.squareup.moshi.Json;

public class Update_Response {
    public NormalResponseDTO jwtResult = new NormalResponseDTO();


    @Json(name = "updateUserName")
    public NormalResponseDTO updateUserName = new NormalResponseDTO();

    //이름 변경을 위한 응답 data

    @Json(name = "nameDuplicate")
    public NormalResponseDTO nameDuplicate = new NormalResponseDTO();

    public User getUser_info() {
        return user_info;
    }

    public void setUser_info(User user_info) {
        this.user_info = user_info;
    }

    @Json(name = "user-info")
    private User user_info = new User();

    @Json(name = "jwt_new")
    private String jwt_New;

    public String getJwt_New() {
        return jwt_New;
    }

    public void setJwt_New(String jwt_New) {
        this.jwt_New = jwt_New;
    }
}
