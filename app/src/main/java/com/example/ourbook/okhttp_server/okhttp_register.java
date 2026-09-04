package com.example.ourbook.okhttp_server;

import com.example.ourbook.resUrl.EC2_Url;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class okhttp_register {

    // resource url 모음 클래스 ,
    EC2_Url ec2_url = new EC2_Url();
    // 이메일 보내기 위한 url
    String email_url = ec2_url.getEc2_url()+ "/test/emailTest2_fuctionTest.php";
    private String Email;
    private String Password;
    private String userName;

    public okhttp_register(String email,String password,String userName){
        this.Email =email;
        this.Password = password;
        this.userName =userName;
    };
    public final OkHttpClient client = new OkHttpClient();



    public void run() throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("email",Email)
                .add("username",userName)
                .add("password",Password)
                .build();
        Request request = new Request.Builder()
//                .url("http://your-server-host.example.com/test/emailTest2_fuctionTest.php")
                .url(email_url)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                System.out.println("성공");
                System.out.println(response.body().string());
            }
        };
    }
}
