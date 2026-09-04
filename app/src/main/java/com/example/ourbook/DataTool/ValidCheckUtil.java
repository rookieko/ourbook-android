package com.example.ourbook.DataTool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidCheckUtil {

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    //가입 닉네임 조건
    // 1. 3자 이상 , 15자 이하
    // 2. 알파벳 숫자 허용
    public static boolean isValidUsername(String username) {

        if(username == null){
            return false;
        }// null 예외 처리
        username = username.trim();
        // 예시: 최소 3자 이상 15자 이하, 알파벳, 숫자, ., _, - 허용 ,
        String usernameRegex = "^[가-힣a-zA-Z0-9]{3,14}$";
        Pattern pattern = Pattern.compile(usernameRegex);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    // 가입 비밀번호 조건
    // 1. 8자 이상
    // 2. 숫자와 알파벳, 특수문자를 모두 포함
    public static boolean isValidPassword(String password) {
        if(password == null){
            return false;
        }// null 예외 처리
        password = password.trim();
        //최소 8자, 숫자, 문자, 특수문자 포함 (?=.*[@#$%^&+=])
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&=])(?!.*[+\\-\"'\\`<> ]).{8,}$";

        Pattern pattern = Pattern.compile(passwordRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


}
