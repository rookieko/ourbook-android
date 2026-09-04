package com.example.ourbook.DataTool.Service;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;

import java.util.Map;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface UploadAPI {

    /* 프로필 이미지 업로드 */
    @Multipart
    @POST("OurBook/member/profileImageEdit.php")
    Call<NormalResponseDTO> uploadImage(@Header (Constants.HEADER_TOKEN) String token , @Part MultipartBody.Part file, @Part("name") String requestBody);


    /*webNovel cover image upload */
    @Multipart
    @POST("OurBook/webnovel/upload-book-cover.php")
    Call<NormalResponseDTO> uploadBookCover(@Header (Constants.HEADER_TOKEN) String token,
                                            @Part MultipartBody.Part imageFile,
                                            @PartMap Map<String,Object> part_map
    );

    @Multipart
    @POST("OurBook/webnovel/upload-book-cover.php")
    Call<String> testUploadBookCover(@Header (Constants.HEADER_TOKEN) String token,
                                                @Part("wid") RequestBody wid_body,
                                                @PartMap Map<String,String> map,
                                                @Part("test") String test
                                                );

    /* 업로드 test */
    @Multipart
    @POST("OurBook/uploadTest.php")
    Call<ResponseBody> uploadImageSample(
            @Part MultipartBody.Part image,
            @Part("desc") RequestBody desc
    );
}
