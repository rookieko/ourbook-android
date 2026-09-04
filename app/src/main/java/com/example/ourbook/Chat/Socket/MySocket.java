package com.example.ourbook.Chat.Socket;

import android.util.Log;

import com.example.ourbook.Constants;

import java.io.IOException;
import java.net.Socket;

public class MySocket {
    private static final String TAG = "MySocket" + Constants.AddTAG;
    private static Socket mySocket;
    private static final String SERVER_IP = "203.0.113.10";
    private static final int SERVER_PORT = 6080;


    public static Socket getSocket(){

        if( mySocket == null){
            try {
                mySocket = new Socket(SERVER_IP, SERVER_PORT);
            } catch (IOException e) {
                Log.d(TAG, "getSocket: IOException ");
                throw new RuntimeException(e);
            }
        }
        return mySocket;
    }


}
