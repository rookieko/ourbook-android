package com.example.ourbook;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ourbook.Chat.MainChatFragment;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ManageUserDataService;
import com.example.ourbook.DataTool.UserSharedHelper;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.exploreActivity.HomeFragment;
import com.example.ourbook.profile.ProfileFragment;
import com.example.ourbook.writeActivity.WriteMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainLoginActivity extends AppCompatActivity {
    private final  String TAG = "MainLoginActivity"+ Constants.AddTAG;

    private String jwtToken; // 기존 userSingleTone 으로 불러오는데 에러 발생으로 null 이 되는 문제 발생
    //TODO 메인 액티비티 스택 관리
    com.example.ourbook.databinding.ActivityMainLoginBinding binding;
    private  ProfileFragment profileFragment;
    private MainChatFragment mainChatFragment;
    // Android 13+ 알림 권한 요청 런처. 선언만으로는 채팅 알림이 표시되지 않는다.
    private ActivityResultLauncher<String> notificationPermissionLauncher;
    String email;
    String userName;
    private RetrofitGenerator retrofitGenerator;
    private UserSingletone userSingletone;
    private ManageUserDataService manageUserDataService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.ourbook.databinding.ActivityMainLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 알림 권한 런처 등록 . registerForActivityResult 는 onCreate 안에서 등록해야 한다
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (!isGranted) {
                        Toast.makeText(
                                getApplicationContext()
                                , "알림 권한이 없어 채팅 알림을 받을 수 없습니다"
                                , Toast.LENGTH_SHORT
                        ).show();
                    }
                });
        requestNotificationPermission();

        //초기화
        // 싱글턴 초기화
        userSingletone = UserSingletone.getMyUser();
        jwtToken = userSingletone.getUserJWT();
        profileFragment =  ProfileFragment.newInstance(String.valueOf(userSingletone.getUid()),userSingletone.getUserJWT());

        // 레트로핏 사용을 위한 초기화
        retrofitGenerator = new RetrofitGenerator();

        // 삭제 예정 ,  사용 안하고 있음 삭제 가능 ..
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        userName = intent.getStringExtra("userName");
        // 삭제 예정


        // 로그인 이후 singletone 수정 필수
//        new MyFirebaseInstanceService().getFCMToken();
        getFcmTokens();


//        binding.textViewUserEmail.setText(userSingletone.getUserEmail());
//        binding.textViewUsername.setText(userSingletone.getUserName());

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();



                if (itemId == R.id.tab_home) {

                    loadFragment(HomeFragment.newInstance(String.valueOf(userSingletone.getUid()),userSingletone.getUserJWT()));
//                    selectedFragment = new HomeFragment();
                    return true;

                } else if (itemId == R.id.tab_profile) {
//                    selectedFragment = new ProfileFragment();
                    if(profileFragment == null){
                    profileFragment =  ProfileFragment.newInstance(String.valueOf(userSingletone.getUid()),userSingletone.getUserJWT());
                    };
                    loadFragment(profileFragment);
                    return true;
                } else if (itemId == R.id.tab_chat) {
                    if(mainChatFragment == null){
                        mainChatFragment = MainChatFragment.newInstance(String.valueOf(userSingletone.getUid()),userSingletone.getUserJWT());
                    };
                    loadFragment(mainChatFragment);
                    return true;

                } else if (itemId == R.id.tab_write) {
                    Intent intent1 = new Intent(MainLoginActivity.this, WriteMainActivity.class);
                    startActivity(intent1);
                    return false; // 버튼 활성화를 막음 .. 그전 fragment 유지하고 액티비티 실행만 진행
                }// ... 나머지 탭에 대한 조건들

//                if (selectedFragment != null) {
//                    getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, selectedFragment).commit();
////                    return true;
//                }
                return false;

            }
        });
        String option = intent.getStringExtra("option");
        if(option == null || option.contentEquals("home")) {
        // 초기 화면을  main, home 화면으로 설정
        binding.bottomNavigation.setSelectedItemId(R.id.tab_home);
        } else if (option.contentEquals("chat")) {
            binding.bottomNavigation.setSelectedItemId(R.id.tab_chat);
        }

        //
//        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment selectedFragment = null;
//                int itemId = item.getItemId();
//                if (itemId == R.id.tab_home) {
//                    selectedFragment = new HomeFragment();
//                } else if (itemId == R.id.tab_profile) {
//                    selectedFragment = new ProfileFragment();
//                } else if (itemId == R.id.tab_write) {
//                    Intent intent1 = new Intent(MainLoginActivity.this, WriteMainActivity.class);
//                    startActivity(intent1);
//
//                }  // ... 나머지 탭에 대한 조건들
//
//                if (selectedFragment != null) {
//                    getSupportFragmentManager().beginTransaction().replace(R.id.home_ly, selectedFragment).commit();
//                }
//                return true;
//            }
//
//        });
    }
    /* Android 13(API 33) 부터 알림은 런타임 권한이다.
     * Manifest 선언만 있고 이 요청이 없으면 채팅 알림이 조용히 표시되지 않는다. */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return; // 12 이하는 설치 시점에 부여된다
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return; // 이미 허용된 경우 다시 묻지 않는다
        }
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void loadFragment(Fragment fragment) {
        // 프래그먼트 교체를 위한 트랜잭션
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_ly, fragment);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // writeMain 등에서 runtime exeption 시 객체 data 날라 갔을 때 다시 초기화 한다
        checkRepairJWT(userSingletone);

        // 로그인 성공, 유저의 정보 가져오기.
        Log.d("Fragment 확인 생명주기 ", "액티비티 생명 주기 실행 : ");

        manageUserDataService = retrofitGenerator.init_user_retrofit(ManageUserDataService.class);

        Call<NormalResponseDTO> getOwnUserDataCall =  manageUserDataService.getOwnUserData(userSingletone.getUserJWT());
        getOwnUserDataCall.enqueue(new Callback<NormalResponseDTO>() {
            @Override
            public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {
                 String TAG_GETUSERDATA = "회원 정보 확인 _ MainLoginActivity";
                 NormalResponseDTO getOwnUserResult = response.body();
                if ( getOwnUserResult == null){
                    return;
                }

                if(getOwnUserResult.isSuccess()){
                    userSingletone.setUserName((String) getOwnUserResult.getData().get("username"));
                    userSingletone.setUserEmail((String) getOwnUserResult.getData().get("email"));
                    userSingletone.setUserImagePath((String) getOwnUserResult.getData().get("profileImagePath"));
                    Log.d(TAG_GETUSERDATA, "onResponse: 유저 데이터 갱신 "+ userSingletone.getUserName());
                    if( getOwnUserResult.getData().get("id") != null) {
                        userSingletone.setUid(((Double) Objects.requireNonNull(getOwnUserResult.getData().get("id"))).intValue());
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"회원 정보 획득 실패 DB 상의 오류  " ,Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"회원 정보 획득 실패 Retrofit 오류 " ,Toast.LENGTH_SHORT).show();

            }
        });

    }
    /* Fcm Token 설정 */
    public void getFcmTokens(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt,token);

                        Log.d(TAG,"getFcmTokens() token 값 :"+ token);
                        Toast.makeText(MainLoginActivity.this, token, Toast.LENGTH_SHORT).show();
                        if(token.length() > 10){
                            userSingletone.setFcmToken(token);
                        }
                    }
                });
    }

    /*jwt null 오류에 대한 대첵 , runtime 오류 등 객체 값이 날라 갔을 때 다시 초기화 */
    private void checkRepairJWT(UserSingletone userSingletone){
        if( userSingletone.getUserJWT() == null){
            Log.e("checkRepairJWT", "onResume: mainLogin token null 로 shared 다시 초기화 " );
            UserSharedHelper.init(getApplicationContext());
            String Token = UserSharedHelper.read(UserSharedHelper.KEY_LOGIN_TOKEN,null);
            if(Token == null){ // JWT 토큰이 존재 하지 않을 때 -> MainActivity 로 이동
                return;
            }else if (Token.length() > 10){ // JWT Token 이 존재 할 때 ,  유효성 검증 필요성
                Log.e("checkRepairJWT", "checkRepairJWT: 토큰 입력 성공" + Token);
                userSingletone.setUserJWT(Token);
            }else {
                Log.e("checkRepairJWT", "checkRepairJWT: JWT TOKENT SHARED 에 저장 되지 않았 습니다." );
            }
        }
    }
}