package com.example.ourbook.Chat.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.ourbook.Chat.Service.Event.ToService;
import com.example.ourbook.Chat.Socket.MySocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ChatListenService extends Service {
    private MySocketManager mySocketManager;
    public ChatListenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        if(mySocketManager == null){
            mySocketManager = MySocketManager.getInstance();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getStringExtra("method").contentEquals("init")){
            mySocketManager.startListeningForMessages(null);
            // 여기서 부터 진행 MainChatFragment 에서 실행되는 mySocket 코드를 이동 하는 중
        }
        return START_STICKY;
    }
    // Activity 로 부터의 이벤트 처리 메서드
    // 이벤트 처리 메서드
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToServiceEvent(ToService event) {
        // 여기에서 이벤트에 반응하여 필요한 작업 수행
        Log.d("Service", "Event received: " + event.getMsg());
    }

}