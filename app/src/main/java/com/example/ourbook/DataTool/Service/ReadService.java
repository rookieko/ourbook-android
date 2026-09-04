package com.example.ourbook.DataTool.Service;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ReadService {

    @FormUrlEncoded
    @POST("OurBook/webnovel/read/first-read.php")
    Call<NormalResponseDTO> readFirstWebNovel(@Header(Constants.HEADER_TOKEN) String jwtToken , @FieldMap Map<String,Object> fieldMap);
    @FormUrlEncoded
    @POST("OurBook/webnovel/read/continue-read.php")
    Call<NormalResponseDTO> readContinueWebNovel(@Header(Constants.HEADER_TOKEN) String jwtToken , @FieldMap Map<String,Object> fieldMap);

    @FormUrlEncoded
    @POST("OurBook/webnovel/read/get-chapter-read.php")
    Call<NormalResponseDTO> getChapterData(@Header(Constants.HEADER_TOKEN) String jwtToken , @FieldMap Map<String,Object> fieldMap);

    @FormUrlEncoded
    @POST("OurBook/webnovel/read/check-read-data.php")
    Call<NormalResponseDTO> getReadData(@Header(Constants.HEADER_TOKEN) String jwtToken , @FieldMap Map<String,Object> fieldMap);

    @FormUrlEncoded
    @POST("OurBook/webnovel/read/upsert-read-data.php")
    Call<NormalResponseDTO> upsertReadData(@Header(Constants.HEADER_TOKEN) String jwtToken , @FieldMap Map<String,Object> fieldMap);

}
