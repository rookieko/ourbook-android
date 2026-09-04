package com.example.ourbook.Chat;

import android.util.Log;
import android.widget.Toast;

import com.example.ourbook.DataTool.UserSingletone;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceService {
    private static String fcmToken;
    private UserSingletone userSingletone ;
    private final String TAG = "MyFirebaseInstanceService" + Constants.TAG;
    public void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    userSingletone = UserSingletone.getMyUser();
                    // Get new FCM registration token
                    String token = task.getResult();
                    fcmToken = token;
                    userSingletone.setFcmToken(token);

                    // Log and toast
                    Log.d(TAG, "FCM Token: " + token);
//                    Toast.makeText(, "FCM Token: " + token, Toast.LENGTH_SHORT).show();
                });
    }
    public void eventTokenRefresh(){
    }

    public void deleteToken(){
//        FirebaseInstanceId.getInstance().deleteInstanceId();
    }

    public static String getFcmToken() {
        return fcmToken;
    }
}
