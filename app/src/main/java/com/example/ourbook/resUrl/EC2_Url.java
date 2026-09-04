package com.example.ourbook.resUrl;

public class EC2_Url {
    // ec2 접속 url getEc2_url을 통해서 호출하고 세세한 경로는 해당 클래스에서 추가 호출
    private static final String ec2_url = "https://your-server-host.example.com";

    public static String getEc2_url(){
      return ec2_url;
    };
}
