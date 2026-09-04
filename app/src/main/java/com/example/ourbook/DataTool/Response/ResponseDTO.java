package com.example.ourbook.DataTool.Response;

import com.squareup.moshi.Json;

import java.util.Map;


/** 응답 데이터 의 기본 구조 각각의 데이터 클래스는 ResponseDTO 를 상속 , json parsing 과  필요에 따라 메서드 추가*/
public abstract class ResponseDTO {

    @Json(name = "message")
    private String message = "null";

    @Json(name = "status")
    private int status = 0;

    @Json(name =  "success")
    private boolean success = false;


    // user data [id, username, email, password, createDate, status]

    @Json(name = "data")
    private Map<String,Object> data = null;


    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
