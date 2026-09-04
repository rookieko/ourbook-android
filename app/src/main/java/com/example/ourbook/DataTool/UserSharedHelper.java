package com.example.ourbook.DataTool;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ourbook.Constants;

public class UserSharedHelper {
    private static final String TAG = "UserSharedHelper"+ Constants.AddTAG;

    //LazyHolder 를 사용하여 클래스가 로드가 될 때 static 메모리에 올라가게 된다 .
    //
    public static final String KEY_LOGIN_TOKEN = "JWT_TOKEN";
    public static final String KEY_SETTING = "SETTING";
    public static final String PREFERENCE_NAME = "HELPER";
    public static final String KEY_FCM = "NOTIFY";
    public static final String KEY_CHAT_NOTIFY = "ChatNotify";
    public static final String KEY_APP_ON = "APP";
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;

    // SingleTon 으로 생성
    private static class LazyHolder {
        public static final UserSharedHelper uniqueInstance = new UserSharedHelper();
    }

    public static UserSharedHelper getInstance() {
        return LazyHolder.uniqueInstance;
    }

    // init
    public static void init(Context context) {
        prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }
    //custom read - write


    // String read, write
    public static String read(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    public static void write(String key, String value) {
        prefsEditor.putString(key, value).apply();
    }

    // Integer read, write
    public static Integer read(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        prefsEditor.putInt(key, value).apply();
    }

    // boolean read, write
    public static boolean read(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        prefsEditor.putBoolean(key, value).apply();
    }

    public static void remove(String key){
        prefsEditor.remove(key).apply();
    }

    // clear
    public static void destroyPref() {
        prefsEditor.clear().apply();
    }


    public static boolean getChatRoomNotify(int chatRoomId){
        String keyString = KEY_CHAT_NOTIFY + chatRoomId;
        boolean result = read(keyString,true);
        Log.d(TAG, "getChatRoomNotify: "+result);
        return result;
    }
    public static void setChatRoomNotify(int chatRoomId,boolean isListen ){
        String keyString = KEY_CHAT_NOTIFY + chatRoomId;
        write(keyString,isListen);
        Log.d(TAG, "setChatRoomNotify: after write "+ getChatRoomNotify(chatRoomId));
    };


}
