package com.example.ourbook.DataTool;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.squareup.moshi.Json;



// 서버 회원가입 응답 DTO UserData 로 받고 회원 관련 data 는 user 를 통해서 받는다.
public class UserData  {
    // 상태 코드  , email , userName 에 관한 중복 코드를 확인하고 message 의 내용을 출력
    @Json(name = "statusCode")
    private String statusCode;
    // 상태 메세지
    @Json(name = "message")
    private String message;
    @Json(name = "AuthCheck")

    public NormalResponseDTO AuthCheck = new NormalResponseDTO();

    // jwtResult jwt 응답 Data status 200 성공 , 301 , 302 기한 오류 , 404 ~ 토큰 오류
    @Json(name = "jwtResult")
    public NormalResponseDTO OJWT_Check = new NormalResponseDTO();
    // token 확인 , 검증된 payload 를 받을 User 객체
    @Json(name = "user-info")
    private User user_info = new User();
    // token
    @Json(name = Constants.RESPONSE_TOKEN)
    private String OBJWToken = null;
    @Json(name = "user")
    private  User user;
    // 회원 가입 확인용 정규식 확인 변수
    public boolean userNameCheck =false;
    public boolean userEmailCheck = false;
    public boolean userPasswordCheck = false;
    public boolean userPasswordSecondCheck = false;
    public boolean userNameDuplicateCheck = false;
    public boolean userEmailDuplicateCheck = false;

    //임시 회원정보 - 로그인 에서 사용
    @Json(name = "userName")
    public String userName = null;
    @Json(name = "email")
    public String email = null;
    @Json(name = "uid")
    public int uid = 0;
    //
    @Json(name = "errorMessage")
    public String errorMessage = "null";
    //

    @Json(name = "responceEmail")
    public String responceEmail ;

    @Json(name = "responceUserName")
    public String responceUserName ;
    public int resultEmailSend;
    public int resultEmailSendCodeSaved;
//    public int AuthCheck;

    @Json(name = "LoginCheck")
    public int LoginCheck = 0;


    public UserData() {
        if (user == null) {
            this.user = new User();
        }
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOBJWToken() {
        return OBJWToken;
    }

    public void setOBJWToken(String OBJWToken) {
        this.OBJWToken = OBJWToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public void setUserData(String userName , String userEmail , String userPassword) {
        this.user.setPassword(userPassword);
        this.user.setUserName(userName);
        this.user.setEmail(userEmail);

    }


    /** 유저 데이터 정보 정규식 확인 통과 유무 출력 boolean
     * 주의 중복 유무가 아님 */
    public boolean isValidUserInput(){
        if ( true&&userEmailCheck&&userPasswordCheck&&userNameCheck&&userPasswordSecondCheck ){

            return true;
        }else {
            return false;
        }

    }
}
;