package com.example.ourbook.DataTool.Response;

import com.squareup.moshi.Json;

import java.util.List;

public class ChapterItem {

    private int status;
    private boolean success;
    private String message;



    @Json(name = "data")
    private List<Chapter> data;

    public int getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Chapter> getData() {
        return data;
    }

    /* 회차 등록 액티비티 chapterInput 마지막 회차를 표시하기 위해 사용
    * 마지막 회차 번호 출력 */
    public int getLastNum(){
        if(data.size()>0) {
            int size = data.size();
            int lastNum1 = data.get(size - 1).num;
            int lastNum2 = data.get(0).num;
            // 야매 .. 최신순 , 등록순 정렬 둘 중 큰 값을 반환
            int lastNum = Integer.max(lastNum1,lastNum2);
            return lastNum;
        }else return 0;
    }
}
