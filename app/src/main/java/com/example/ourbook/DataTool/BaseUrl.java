package com.example.ourbook.DataTool;

import com.example.ourbook.BuildConfig;

public  class BaseUrl {
    // local.properties 의 ourbook.host 가 build.gradle.kts 를 거쳐 들어온다
    public static final String BASE_URL = BuildConfig.SERVER_HOST;



    public static final String ProfileImage_URL = BASE_URL+"OurBook/upload/profileImage/";
    public static final String BookCoverImage_URL = BASE_URL+"OurBook/upload/bookCover/";
    public static final String BookCoverImage_Sample = BookCoverImage_URL+ "book.svg";
    private static final String SIGN_IN_URL = "url";
    public static final String SIGN_IN_URL2  = "285";

    public static String getSIGN_IN_URL() {
        return SIGN_IN_URL;
    }

    public static String getSignInUrl2() {
        return SIGN_IN_URL2;
    }
}
