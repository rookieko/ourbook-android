package com.example.ourbook.DataTool;

import static com.example.ourbook.DataTool.BaseUrl.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ourbook.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

//레트로핏 생성 class
public class RetrofitGenerator {

    // 재사용을 위해 base url 을 따로 생성자에서 받을까 ?


    // retrofit 회원가입 , 로그인 관련 정보 통신을 위한 retrofit 을 반환 하고 debug 기능 on HttpLoggingInterceptor
    public <T> T init_user_retrofit( Class<T> service){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        // 릴리스 빌드에서 JWT 와 요청 본문이 통째로 logcat 에 남지 않도록 debug 에서만 기록한다
        interceptor.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                //인증 실패시 다시 인증 요청만 시도 하기 위한 목적으로  인터셉터에 authenticator 를 사용하려고 함 401 오류에서 자동으로 실행이 된다 ?
//                .authenticator(new Authenticator() {
//                    @Nullable
//                    @Override
//                    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
//                        return null;
//                    }
//                })
                .connectTimeout(12000, TimeUnit.MILLISECONDS)
                .readTimeout(9000, TimeUnit.MILLISECONDS)
                .build();
        //test 백업
        /*public <T> T init_user_retrofit( Class<T> service){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(12000, TimeUnit.MILLISECONDS)
                    .readTimeout(9000, TimeUnit.MILLISECONDS)
                    .build();*/
        // retrofit 객체 생성...
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl.BASE_URL)
                .client(client) // for debug => logcat 에서 okhttp
                .addConverterFactory(MoshiConverterFactory.create())
                .build();

        return retrofit.create(service);
    };
}
