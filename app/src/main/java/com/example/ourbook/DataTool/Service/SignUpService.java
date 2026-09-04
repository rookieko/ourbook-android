package com.example.ourbook.DataTool.Service;

//import static com.example.ourbook.DataTool.BaseUrl;

import com.example.ourbook.DataTool.Response.SignUp_Response;
import com.example.ourbook.DataTool.Response.UserJWTResponse;
import com.example.ourbook.DataTool.UserData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SignUpService {
    final String Email = "email";
    final String userName = "userName";
    final String password = "password";

    final String CheckUrl = "OurBook/member/register/sign-up-duplicateCheck.php";
    final String EmailSendUrl = "OurBook/member/register/emailAuthSend.php";
    final String EmailCheckUrl = "OurBook/member/register/AuthCodeCheck.php";
    final String LoginCheckUrl = "OurBook/member/login.php";
    final String JWTCheckUrl = "OurBook/member/getUserInfo.php";

    @FormUrlEncoded
    @POST(CheckUrl)
    Call<SignUp_Response> UserDataDuplicateCheck(@Field("email") String email , @Field("userName") String userName, @Field("password") String password );
    @FormUrlEncoded
    @POST(CheckUrl)
    Call<SignUp_Response> SignInData(@Field("email") String email , @Field("userName") String userName , @Field("password") String password);
    @GET(CheckUrl)
    Call<ResponseBody> UserDataDuplicateCheck2(@Query("email") String email , @Query("userName") String userName );
    @GET(EmailSendUrl)
    Call<UserData> EmailSend(@Query("email") String email , @Query("userName") String userName );
    @FormUrlEncoded
    @POST(EmailCheckUrl)
    Call<UserData> AuthCodeCheck(@Field("authCode") int authCode , @Field("email") String email);

    @FormUrlEncoded
    @POST(LoginCheckUrl)
    Call<UserData> LoginCheck (@Field("email") String email , @Field("password") String password);


//    @FormUrlEncoded
    @POST(JWTCheckUrl)
    Call<UserJWTResponse> JWTCheck (@Header("Ojwt-Token") String Token  );
}
