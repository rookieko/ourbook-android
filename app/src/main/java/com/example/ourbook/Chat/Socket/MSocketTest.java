package com.example.ourbook.Chat.Socket;

import android.util.Log;

import com.example.ourbook.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MSocketTest {
    public static final String TAG = "MSocketTest" + Constants.AddTAG;
    private PrintWriter writer ;
    private Socket socket;
    private static final String SERVER_IP = "203.0.113.10";
    private static final int SERVER_PORT = 6080;
    public void init() throws IOException {
         socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("채팅방 서버 연결 ");
        writer = new PrintWriter(socket.getOutputStream(), true);
        new Thread(new MSocketTest.ReadThread(socket)).start(); // 시작
        new Thread(new MSocketTest.WriteThread(socket)).start();
    };
    public void disConnect() throws IOException {
        Log.d(TAG, "disConnect: ");
        socket.close();
        Log.d(TAG, "disConnect: result isClosed " + socket.isClosed());
        Log.d(TAG, "disConnect: result isInputShutdown" + socket.isInputShutdown());
        Log.d(TAG, "disConnect: result isOutputShutdown " + socket.isOutputShutdown());

    }
    public void senText(String sendMessage){
            String text;

            while ((text = sendMessage) != null) {
                Log.d(TAG, "senText: LOOP START");
                writer.println(text);
                Log.d(TAG, "senText: LOOP END");

            }
        Log.d(TAG, "senText: LOOP Break ");

    }

    private static class ReadThread implements Runnable {
        private BufferedReader reader;

        public ReadThread(Socket socket) throws IOException {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String response = reader.readLine();
                    System.out.println(response);
//                    System.out.println("입력 받은 타입 " + response.getClass());
                } catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                    break;
                }
            }
        }
    }


    // WriteThread class
    private static class WriteThread implements Runnable {
        private PrintWriter writer;
        private BufferedReader consoleReader;


        public WriteThread(Socket socket) throws IOException {
            writer = new PrintWriter(socket.getOutputStream(), true);
            consoleReader = new BufferedReader(new InputStreamReader(System.in));
        }

        @Override
        public void run() {
            String text;
            try {
                while ((text = consoleReader.readLine()) != null) {
                    writer.println(text);
                }
            } catch (IOException e) {
                System.out.println("Error writing to server: " + e.getMessage());
            }
        }
    }
}
