package com.example.ourbook.DataTool;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ourbook.Constants;

public class UserSingletone {
    private final String TAG = "UserSingletone"+Constants.AddTAG;

    // data in  response [id, username, email, password, createDate, status]
    private String userName;
    private String userEmail;
    private String userJWT;
    private String fcmToken;
    private int uid;
    // write chapter page 에서 사용 , 초기화
    private int lastNum;
//    private int categoryID;

    private int wid; // 웹툰 primary id  임시 사용


    private String userImagePath;

    private static  UserSingletone myUser;

    public static synchronized UserSingletone getMyUser() {
        if (myUser == null){
            myUser = new UserSingletone();
        }
        return myUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFcmToken() {
        if(fcmToken == null){
            Log.e(TAG, "getFcmToken: token" );
            return "null";
        }
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getUserJWT() {
        if (userJWT == null ){
            Log.e("싱글 톤 user ", "getUserJWT:  null " );
            return null;}
        return userJWT;
    }

    public void setUserJWT(String userJWT) {
        this.userJWT = userJWT;
    }

    // 프로필 이미지의 경우.. 이미지 파일명만 저장 하는게 아니라 앞의 경로가 필요 하기 때문에 경로를 만들자..

    public String getUserImagePath() {
        return userImagePath;
    }
    // 실제 프로필 이미지 링크를 사용할때 앞의 URL을 붙여야 하기 때문에 BaseUrl.ProfileImage_URL 를 붙여 준다 (경로)
    public String getProfileImageUrl(){
      return BaseUrl.ProfileImage_URL + userImagePath;
    };



    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    // 초기화 싱글톤 데이터
    public void clearData(){
      myUser = new UserSingletone();
    };

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public int getLastNum() {
        return lastNum;
    }

    public void setLastNum(int lastNum) {
        this.lastNum = lastNum;
    }

    @NonNull
    @Override
    public String toString() {
        String result = " 유저 싱글턴 객체 값  1. wid = "+ wid + " 2. jwt = "+ userJWT + " 3. username = " + userName + " 4. lastNum = " + lastNum;

        return result;
    }
}
