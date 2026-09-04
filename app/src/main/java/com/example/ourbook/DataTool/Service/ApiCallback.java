package com.example.ourbook.DataTool.Service;

import android.util.Log;

import com.example.ourbook.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public interface ApiCallback extends Callback {
    /*공통적인 레트로핏 응답 처리 부분을 합쳐서 처리 하기 위해 만든 콜백 인터페이스*/
    final String ApiCallback_TAG = "Retrofit 공통 ApiCallback_TAG callback : " + Constants.AddTAG;
    @Override
    default void onResponse(Call call, Response response) {
        if (!response.isSuccessful()){
            Log.d(ApiCallback_TAG, "onResponse 레트로핏 응답 코드 실패 : " + response.code() + "  내용 " + response.body()  );
            return;
        }

    }

    @Override
    default void onFailure(Call call, Throwable t) {
        Log.d(ApiCallback_TAG, "onFailure 레트로핏 연결 실패 : " + t.getMessage());
    }
}
