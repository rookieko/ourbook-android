package com.example.ourbook.DataTool;

public class SingletoneTest {
    // 실제로 사용 하지 않음 . 테스트 용으로 생성한 클래스

    public String name;
    private static SingletoneTest instance ;

    private SingletoneTest(){

    };

    public static SingletoneTest getInstance(){
      if ( instance == null){
          instance = new SingletoneTest();
      };

      return instance;
    };

}
