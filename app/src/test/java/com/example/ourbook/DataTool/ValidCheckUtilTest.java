package com.example.ourbook.DataTool;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class ValidCheckUtilTest {

    String password;
    boolean result;
    String nickName;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        nickName = null;
        password = null;
        result = false;
    }

    @Test
    @DisplayName("비밀번호가 null")
    public void null_enter_Password() {
        password = null;
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 입력 null",result);
    }

    @Test
    @DisplayName("비밀번호를 입력하지 않음")
    public void not_enter_Password() {
        password = "";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 입력 x",result);
    }

    @Test
    @DisplayName("비밀번호를 8자 이하 입력 숫자와 알파벳 특수문자를 조합하지 않고 입력함")
    public void short_not_confirm_PasswordTest(){
        password = "123456";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( just num ) ",result);

        password = "qwerqw";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( just char ) ",result);

        password = "@#$@##";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( just specail ) ",result);

        password = "12345@";

        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( num + special )",result);

        password = "qwer@#";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( char + special) ",result);

        password = "125qwe";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( char + num) ",result);
    }
    @Test
    @DisplayName("길이가 8자 이하 입력 비밀번호에 숫자와 알파벳 특수문자를 조합하여 입력함")
    public void short_confirm_regex_PasswordTest(){
        password = "1234qw@";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호 길이 fail regex pass",result);
    }



    @Test
    @DisplayName("길이가 8자 이상 비밀번호에 숫자와 알파벳 , 특수문자중 두가지만 조합하여 입력함")
    public void long_not_confirm_regex_PasswordTest(){
        password = "12345678";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( just num ) ",result);

        password = "qwerqwerqwer";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( just char ) ",result);

        password = "@#$@#$@#$@#$";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( just specail ) ",result);

        password = "1234567@";

        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( num + special )",result);

        password = "qwer@#qwe";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( char + special) ",result);
        password = "1235qwerqwe";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( char + num) ",result);

    }

    @Test
    @DisplayName("길이가 8자 이상 비밀번호에 숫자와 알파벳 , 특수문자중 두가지 조합 , 띄어쓰기 포함 하여 입력함")
    public void long_not_confirm_regex_space_PasswordTest(){
        password = "12345 678";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( just num ) , space",result);

        password = "qwerqw erqwer";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( just char ) , space",result);

        password = "@#$@#$ @#$@#$";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( just specail ), space ",result);

        password = "1234567@";

        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( num + special ), space",result);

        password = "qwer@ #qwe";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( char + special), space ",result);
        password = "1235q werqwe";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 pass , regex fail ( char + num) , space",result);

    }
    @Test
    @DisplayName("길이가 8자 이하 비밀번호에 숫자와 알파벳 , 특수문자중 두가지 조합 , 띄어쓰기 포함 하여 입력함")
    public void short_not_confirm_regex_space_PasswordTest(){
        password = "1234 56";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( just num ) , space",result);

        password = "qwer qw";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( just char ) , space",result);

        password = "@#$ @##";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( just specail ) , space",result);

        password = "1234 5@";

        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( num + special ) , space",result);

        password = "qwer@ #qwe";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( char + special) , space",result);
        password = "125 qwe";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호를 길이 fail , regex fail ( char + num) , space",result);

    }
    @Test
    @DisplayName("모든 기준을 충족하는 경우 , 길이가 8자 이상 비밀번호에 숫자와 알파벳 특수문자를 조합하여 입력함 + 빈칸 입력 확인")
    public void long_confirm_space_PasswordTest(){
        password ="1234q w@er";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호 길이 pass , regex pass",result);
    }
    @Test
    @DisplayName("모든 기준을 충족하는 경우 , 길이가 8자 이상 비밀번호에 숫자와 알파벳 특수문자를 조합하여 입력함 + 포함하면 안되는 특수문자")
    public void long_confirm_except_special_PasswordTest(){
        password ="1235q\"w@";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호 길이 pass , regex pass , special fail",result);
        password ="1235qw@'";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호 길이 pass , regex pass , special fail",result);
        password ="1235qw@<";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호 길이 pass , regex pass , special fail",result);
        password ="1235qw@>";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호 길이 pass , regex pass , special fail",result);
        password ="1235qw@-";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호 길이 pass , regex pass , special fail",result);
        password ="1235qw@+";
        result = ValidCheckUtil.isValidPassword(password);
        assertFalse("비밀번호 길이 pass , regex pass , special fail",result);

    }



    @Test
    @DisplayName("모든 기준을 충족하는 경우 , 길이가 8자 이상 비밀번호에 숫자와 알파벳 특수문자를 조합하여 입력함")
    public void long_confirm_PasswordTest(){
        password ="1234qw@er";
        result = ValidCheckUtil.isValidPassword(password);
        assertTrue("비밀번호 길이 pass , regex pass",result);
    }

    @Test
    @DisplayName("닉네임 null 입력 확인")
    public void null_enter_nickName(){
        nickName = null;
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName null 확인",result);
    }
    @Test
    @DisplayName("닉네임 입력 빈 문자열 입력 확인")
    public void empty_enter_nickName(){
        nickName = "";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 빈 문자열 입력 확인",result);
    }
    @Test
    @DisplayName("닉네임 15자 이상 입력 ")
    public void  too_long_not_confirm_nickName(){
        nickName = "123451234512345";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 숫자만 15자 이상 확인",result);

        nickName = "asdfasdfasdfasdfasdf";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 영어만 15자 이상 확인",result);

        nickName = "ㅁㄴㅇㄹㅁㄴㅇㄹㅁㄴㅇㄹㅁㅁ";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 한글만 15자 이상 확인",result);

        nickName = "한글12345678abcde";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 한글만 15자 이상 확인",result);
    }

    @Test
    @DisplayName("닉네임 3자 이하 띄어쓰기 입력 ")
    public void short_space_not_confirm_nickName(){
        nickName = "A C";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 영어만 3자 이하 확인",result);

        nickName = "1 3";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 숫자만 3자 이하 확인",result);

        nickName = "한 글";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 한글만 3자 이하 확인",result);

    }
    @Test
    @DisplayName("닉네임 3자 이상 띄어쓰기 입력 ")
    public void  long_space_not_confirm_nickName(){
        nickName = "123 456";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 숫자만 15자 이상 확인",result);

        nickName = "ABCD EF";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 영어만 15자 이상 확인",result);

        nickName = "한글 사용";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 한글만 15자 이상 확인",result);

        nickName = "ABC123 한글";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 한글만 15자 이상 확인",result);
    }

    @Test
    @DisplayName("닉네임 15자 이상 띄어쓰기 입력 ")
    public void  too_long_space_not_confirm_nickName(){
        nickName = "12345 1234512345";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 숫자만 15자 이상 확인",result);

        nickName = "asdfasdfas dfasdfasdf";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 영어만 15자 이상 확인",result);

        nickName = "ㅁㄴㅇㄹㅁㄴㅇ ㄹㅁㄴㅇㄹㅁㅁ";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 한글만 15자 이상 확인",result);

        nickName = "한글12345678 abcde";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 한글만 15자 이상 확인",result);
    }

    @Test
    @DisplayName("닉네임 3자 이상 15자 이하 입력 ")
    public void  long_confirm_nickName(){
        nickName = "123456";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertTrue("nickName 숫자만 15자 이상 확인",result);

        nickName = "ABCDEF";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertTrue("nickName 영어만 15자 이상 확인",result);

        nickName = "한글사용";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertTrue("nickName 한글만 15자 이상 확인",result);

        nickName = "ABC123한글";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertTrue("nickName 한글만 15자 이상 확인",result);
    }

    @Test
    @DisplayName("닉네임 특수문자 입력 ")
    public void  contain_special_confirm_nickName(){
        nickName = "123456!";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자  확인",result);

        nickName = "ABCDEF@";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자  확인",result);

        nickName = "한글사용#";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자  확인",result);

        nickName = "ABC123한글`";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자  확인",result);

        nickName = "ABC123한글\"";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자 확인",result);


        nickName = "ABC123한글<";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자 확인",result);
        nickName = "ABC123한글>";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자 확인",result);
        nickName = "ABC123한글?";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자 확인",result);
        nickName = "ABC123한글*";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자 확인",result);

        nickName = "ABC123한글&";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자 확인",result);

        nickName = "ABC123한글|";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자 확인",result);
        nickName = "ABC123한글/";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자 확인",result);
        nickName = "ABC123한글^";
        result = ValidCheckUtil.isValidUsername(nickName);
        assertFalse("nickName 특수 문자 확인",result);

    }



}