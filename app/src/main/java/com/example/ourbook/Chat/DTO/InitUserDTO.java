package com.example.ourbook.Chat.DTO;

import com.squareup.moshi.Json;

public class InitUserDTO {
    @Json(name = "uid")
    private Integer uid ; // 유저 고유 아이디

    @Json(name = "uei")
    private String uei ; // 유저 이메일

    @Json(name = "uni")
    private String uni ; // 유저 닉네임

    @Json(name = "exp")
    private long exp ;	// 기한

    @Json(name = "iat")
    private long iat ;

    private boolean isVerify;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUei() {
        return uei;
    }

    public String getUni() {
        return uni;
    }

    public long getExp() {
        return exp;
    }

    public long getIat() {
        return iat;
    }
}
