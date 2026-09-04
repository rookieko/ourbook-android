package com.example.ourbook.DataTool;

public  class BaseUrl {
    public static final String BASE_URL= "http://your-server-host.example.com/";



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
