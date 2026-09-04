package com.example.ourbook.DataTool.Response;

import com.squareup.moshi.Json;

import java.util.List;

public class writerResponse {
    // Retrofit 응답 받는 DTO , WebNovel List

        private int status; // 200 성공 ,400 <= 실패
        private boolean success; // true 성공 , false 실패
        private String message; // 메세지 = 성공, 실패에 관한 상세 메세지
        private BooksData data; // 응답 , getData() 로 RecyclerView Adapter 생성자로 DataSet 전달

        // Constructor, Getters, and Setters


    // 생성자, 사용은 안됨
    public writerResponse(int status, boolean success, String message, BooksData data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
    /*for put R.V. Adapter constructor */
    public BooksData getData() {
        return data;
    }
}
