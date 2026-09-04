package com.example.ourbook.DataTool.Service;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.Response.Update_Response;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ManageUserDataService {
    final String GetUserUrl = "OurBook/member/getUserInfo.php";
    final String DELETEUserData = "OurBook/member/deleteUser.php";


    @FormUrlEncoded
    @POST("OurBook/member/update/updateUserName.php")
    Call<Update_Response> updateUserName (@Header (Constants.HEADER_TOKEN) String jwt , @Field(Constants.REQUEST_USERNAME) String userName);

    @FormUrlEncoded
    @POST("OurBook/member/update/updateUserPW.php")
    Call<NormalResponseDTO> updateUserPW (@Header (Constants.HEADER_TOKEN) String jwt , @Field(Constants.REQUEST_PASSWORD) String password);

    // 회원 본인의 정보를 가져오기 위한 서비스 data ( map ) 에 회원 정보를 포함
//    @FormUrlEncoded
    @POST(GetUserUrl)
    Call<NormalResponseDTO> getOwnUserData (@Header(Constants.HEADER_TOKEN) String Token  );

    /*회원 정보 삭제 */
//    @FormUrlEncoded
    @POST(DELETEUserData)
    Call<NormalResponseDTO> deleteUser (@Header(Constants.HEADER_TOKEN) String Token);




}
