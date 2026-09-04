package com.example.ourbook.Chat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.ourbook.Chat.DTO.ChatMessageDTO;
import com.example.ourbook.Chat.DTO.ChatRoomDTO;
import com.example.ourbook.Chat.DTO.DefaultDTO;
import com.example.ourbook.Chat.DTO.JsonConverter;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.UserSharedHelper;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.MainLoginActivity;
import com.example.ourbook.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    private UserSingletone userSingletone ;
    private static String fcmToken;
    private final String TAG = "MyFirebaseMessagingService" + Constants.AddTAG;
    private JsonConverter jsonConverter ;
    @Override
    public void onCreate() {
        super.onCreate();
        UserSharedHelper.init(getApplicationContext());

//        userSingletone = UserSingletone.getMyUser();
    }


//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//        if (remoteMessage.getNotification() != null) {
////            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
//        }
//
//        // 메시지에서 데이터 추출
////        Map<String, String> data = remoteMessage.getData();
////        String title = data.get("title");
////        String body = data.get("body");
////        DefaultDTO defaultDTO;
////        try {
////            jsonConverter = new JsonConverter();
////           defaultDTO = jsonConverter.getDefaultJsonAdapter().fromJson(body);
////        } catch (IOException e) {
////            throw new RuntimeException(e);
////        }
////        if(defaultDTO != null && remoteMessage.getNotification() != null){
////            sendNotification(defaultDTO.getMethod(), defaultDTO.message);
////
////        }
//        // 알림 보내기
////        sendNotification(title, body);
//        Map<String, String> data = remoteMessage.getData();
//        if (data != null && !data.isEmpty()) {
//            String title = data.get("title");
//            String body = data.get("body");
//
//            // Log the body to inspect what we're receiving
//            Log.d(TAG, "Received JSON: " + body);
//
//            if (body != null) {
//                try {
//                    jsonConverter = new JsonConverter();
//                    DefaultDTO defaultDTO = jsonConverter.getDefaultJsonAdapter().fromJson(body);
//
//                    // Check if DTO is parsed correctly
//                    if (defaultDTO != null && remoteMessage.getNotification() != null) {
//                        showNotification(defaultDTO.getChatMessage().userName,defaultDTO.getChatMessage().content);
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "Error parsing JSON", e);
//                }
//            } else {
//                Log.e(TAG, "Body is null, cannot parse JSON");
//            }
//        }
//    }

    // 임시 테스트용 메세지
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + message.getFrom());
//        boolean appOn = UserSharedHelper.read(UserSharedHelper.KEY_APP_ON,false);
//
//        if( appOn ){
//            Log.d(TAG, "onMessageReceived: app On");
//            return;
//        }else {
//            Log.d(TAG, "onMessageReceived: app Off");
//        }

        // Check if message contains a data payload.
        if (message.getData().size() > 1) {
            Log.d(TAG, "Message data payload: " + message.getData().get("method"));
            Log.d(TAG, "Message data payload: " + message.getData());

            if (!message.getData().isEmpty()) {
                if (
                        Objects.requireNonNull(message.getData().get("method")).contentEquals("chat")
                ){
                    jsonConverter = new JsonConverter();
                    DefaultDTO defaultDTO ;

                    try {
                        defaultDTO = jsonConverter.getDefaultJsonAdapter().fromJson(Objects.requireNonNull(message.getData().get("json")));
                        ChatMessageDTO chatMessageDTO = Objects.requireNonNull(defaultDTO).getChatMessage();
//                        showNotification(chatMessageDTO.userName, chatMessageDTO.content);
                        Integer chatRoomUserId = Integer.valueOf(message.getData().get("cruid"));
                        boolean isNotify = UserSharedHelper.getChatRoomNotify(chatMessageDTO.chatRoomId);
                        boolean isChatOn = UserSharedHelper.read("CHATON",false);

                        if(!isChatOn&&isNotify){
                            
                        sendNotification2(chatMessageDTO.userName,chatMessageDTO.content ,  chatMessageDTO , chatRoomUserId);
                        }else {
                            Log.d(TAG, "onMessageReceived: 알림 수신 거부함");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                Log.d(TAG,"size is bigger then 10");
                for (String key :
                        message.getData().keySet()) {
                    Log.d(TAG, "onMessageReceived: " + key +" "+ message.getData().get(key));
                }
                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob();
            } else {
                Log.d(TAG,"size is smaller then 10");

                // Handle message within 10 seconds
//                handleNow();
            }

        }else {
            Log.d(TAG, "onMessageReceived:  data size was 1 ~ 0  ");
        }

        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());

        }else {
            Log.d(TAG, "Message Notification null getMessageType : " + message.getMessageType());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken() : " + token);
//        if( userSingletone!= null){
//            if(!token.isEmpty()) {
//                userSingletone.setFcmToken(token);
//                fcmToken = token;
//            }
//        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("EDMT Channel");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContent(title)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }
    private void sendNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);
//                .setContentIntent(new PendingIntent());  // 알림 클릭 시 수행할 인텐트

        notificationManager.notify(0, notificationBuilder.build());
    }
    private void sendNotification2(String title , String content , ChatMessageDTO chatMessageDTO ,Integer chatRoomUserId){
        Log.d(TAG, "sendNotification2: notification 생성 cruid "+ chatRoomUserId + " content " + content);
        Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
        intent.putExtra(Constants.INTENT_CHATROOM_USER_ID, chatRoomUserId);
        intent.putExtra(Constants.INTENT_CHATROOM_ID,chatMessageDTO.chatRoomId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE); // ?

        String channelId = "Your_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.sample_bookcover)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(new Random().nextInt() /* ID of notification */, notificationBuilder.build());
    }

    public static String getFcmToken() {
        return fcmToken;
    }
}

