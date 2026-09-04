package com.example.ourbook.Chat.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketTest {
    private static final String SERVER_IP = "203.0.113.10";
    private static final int SERVER_PORT = 6080;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);
        System.out.println("채팅방 서버 연결 ");

        new Thread(new ReadThread(socket)).start(); // 시작
        new Thread(new WriteThread(socket)).start();
    }

    // ReadThread class
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
