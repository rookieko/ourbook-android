package com.example.ourbook.DataTool;

import com.squareup.moshi.Json;

//사용자 정보를 담을 DTO

public class User {
    @Json(name = "userName")
    private String userName = null;
    @Json(name = "userId")

    private String userId = null;
    @Json(name = "email")

    private String email = null;
    @Json(name = "password")

    private String password = null;
    private boolean serviceAgree;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
