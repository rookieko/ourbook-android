package com.example.ourbook.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.ManageUserDataService;
import com.example.ourbook.DataTool.UserSharedHelper;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.DataTool.Login.MainActivity;
import com.example.ourbook.DataTool.Login.UserPasswordChangeActivity;
import com.example.ourbook.databinding.FragmentProfileBinding;


import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import retrofit2.Retrofit;

//TODO 현재 여기 까지 진행 , SharedPreference 를  사용하여 로그아웃 기능 , Token  을 사용 하여 유저 정보 조회 , uid
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // 이미지 추가 목적 변수 초기화
    private static final int PICK_IMAGE_REQUEST = 1;


    private Retrofit retrofit;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private UserSingletone userSingletone;

    //
    private FragmentProfileBinding binding;
    private ManageUserDataService manageUserDataService;
    private RetrofitGenerator retrofitGenerator;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "uid";
    private static final String ARG_PARAM2 = "jwt";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        sharedPreferences = this.getActivity().getSharedPreferences("TokenShared",MODE_PRIVATE); // shared 초기화
        editor = sharedPreferences.edit();
        //
         retrofitGenerator = new RetrofitGenerator();
        userSingletone = UserSingletone.getMyUser();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            Log.d(getActivity().getClass().getName(), "onCreate: "+ARG_PARAM1+"mParam1 "+ mParam1);
            Log.d(getActivity().getClass().getName(), "onCreate:"+ARG_PARAM2+" mParam2 "+ mParam2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.textViewUsername.setText(userSingletone.getUserName());
        binding.textViewUserEmail.setText(userSingletone.getUserEmail());
        // 이미지 등록 버튼
        binding.profileImageChangeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ProfileImageViewActivity.class);
                startActivity(intent);
//                openImageChooser();

            }
        });

        // 로그아웃 버튼
        binding.userLogOutTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logOutDialog();
//
//
//                //TODO 수정 필요 remove 인지 clear 인지 확인
//                editor.remove("jwt");
//                editor.apply();
//                UserSharedHelper.remove(UserSharedHelper.KEY_LOGIN_TOKEN);
//                //
//                Intent intent = new Intent(getActivity(),MainActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 기존 에 있던 액티비티를 끌어 올리고 위에 있던 액티비티 걷어냄
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                startActivity(intent);


            }
        });
        //유저 이름 정보 변경
            binding.editNameTextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserNameChangeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            });
        // 유저 비밀 번호 변경
            binding.passwordChangeTextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity() , UserPasswordChangeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            });

        // 유저 회원 정보 삭제 버튼
        binding.userDeleteTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataDeleteDialog();
            }
        });

        // Inflate the layout for this fragment
        return binding.getRoot();



    }

//    private void openImageChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }



    @Override
    public void onResume() {
        Log.d("Fragment 확인 생명주기 ", " Fragment 생명 주기 실행 resume : ");
        Log.d("싱글턴 닉네임 확인 ", "onResume: " + userSingletone.getUserName());
//        binding.textViewUsername.setText(userSingletone.getUserName());
//        binding.textViewUserEmail.setText(userSingletone.getUserEmail());
        super.onResume();
        UserDataUpdate();
        // 프로필 이미지 유효성 확인 , null , 파일명 길이 확인 timstamp 을 사용하기 때문에 10 글자는 넘음
        if (userSingletone.getUserImagePath()!=null && userSingletone.getUserImagePath().length() >2){
            Log.d("프로필 이미지 url 확인 ", "onResume: "+ userSingletone.getUserImagePath());
            Glide.with(ProfileFragment.this).load(userSingletone.getProfileImageUrl()).into(binding.imageViewProfile);
        }
    }


    /* dialog  띄우기 , 로그아웃 확인용 */
    private void logOutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("로그아웃 확인");
        builder.setMessage("로그아웃 하시겠습니까?");

        // 확인 버튼 설정
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 확인 버튼을 눌렀을 때의 동작


                //TODO 수정 필요 remove 인지 clear 인지 확인
                editor.remove("jwt");
                editor.apply();
                UserSharedHelper.remove(UserSharedHelper.KEY_LOGIN_TOKEN);
                //
                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 기존 에 있던 액티비티를 끌어 올리고 위에 있던 액티비티 걷어냄
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Toast.makeText(getActivity(),"로그아웃 하였습니다. " , Toast.LENGTH_SHORT).show();
            }
        });

        // 취소 버튼 설정
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 취소 버튼을 눌렀을 때의 동작
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /*회원 탈퇴 확인 다이얼로그 */
    private void UserDataDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("회원 탈퇴 ");
        builder.setMessage("주의 회원 탈퇴 시 회원 관련 정보가 모두 삭제됩니다\n" +
                "탈퇴하시겠습니까?");

        builder.setPositiveButton("탈퇴 하기 ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                UserSharedHelper.remove(UserSharedHelper.KEY_LOGIN_TOKEN);

                manageUserDataService = retrofitGenerator.init_user_retrofit(ManageUserDataService.class);

                Call<NormalResponseDTO> deleteUserData = manageUserDataService.deleteUser(userSingletone.getUserJWT());
                deleteUserData.enqueue(new Callback<NormalResponseDTO>() {
                    @Override
                    public void onResponse(Call<NormalResponseDTO> call, Response<NormalResponseDTO> response) {

                        if (response.body() == null){
                            return;
                        }
                        NormalResponseDTO deleteUserResponse = response.body();
                        if (deleteUserResponse.isSuccess()){
                            //
                            Intent intent = new Intent(getActivity(),MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 기존 에 있던 액티비티를 끌어 올리고 위에 있던 액티비티 걷어냄
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            UserSharedHelper.remove(UserSharedHelper.KEY_LOGIN_TOKEN);
                            startActivity(intent);
                            Toast.makeText(getActivity(),"회원 탈퇴 하였습니다. " , Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<NormalResponseDTO> call, Throwable t) {

                    }
                });



            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void UserDataUpdate(){
        manageUserDataService =  retrofitGenerator.init_user_retrofit(ManageUserDataService.class);

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
                    binding.textViewUsername.setText(userSingletone.getUserName());
                    binding.textViewUserEmail.setText(userSingletone.getUserEmail());
//                    binding.imageViewProfile.setImageURI(Uri.parse(userSingletone.getUserImagePath()));
//                    Glide.with(getContext()).load(userSingletone.getUserImagePath()).into(binding.imageViewProfile);
                    if( getOwnUserResult.getData().get("id") != null) {
                        userSingletone.setUid(((Double) Objects.requireNonNull(getOwnUserResult.getData().get("id"))).intValue());
//                        Log.d(TAG_GETUSERDATA, " onResume KeySet : "+ getOwnUserResult.getData().keySet() );
//                        for (String key :
//                                getOwnUserResult.getData().keySet()) {
//                            Log.d(TAG_GETUSERDATA, "onResume onResponse: " + key + " : " + getOwnUserResult.getData().get(key));
//                        }


                    }


                }else {
                    Toast.makeText(getContext(),"회원 정보 획득 실패 DB 상의 오류  " ,Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<NormalResponseDTO> call, Throwable t) {
                Toast.makeText(getContext(),"회원 정보 획득 실패 Retrofit 오류 " ,Toast.LENGTH_SHORT).show();

            }
        });
    }
}
