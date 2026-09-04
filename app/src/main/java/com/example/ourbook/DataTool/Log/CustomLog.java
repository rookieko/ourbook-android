package com.example.ourbook.DataTool.Log;

import android.content.Context;
import android.util.Log;

public class CustomLog {
    private static final String TAG = " ourbook ";
//    private static final boolean DEBUG = BuildConfig.DEBUG;
    private Context context;
//    privateublic CustomLog(Context context) {
//        this.context =
//    }

    public static void d(Context context, String string1 ,String string2){
        Log.d(TAG + context.getClass().getName(), "logN: "+"1."+string1+"2."+ string2);
    }


}
