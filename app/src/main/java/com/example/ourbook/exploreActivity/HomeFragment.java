package com.example.ourbook.exploreActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Book;
import com.example.ourbook.DataTool.Recycle.Adapter.ExploreBookAdapter;
import com.example.ourbook.DataTool.RetrofitGenerator;
import com.example.ourbook.DataTool.Service.RegisterBookService;
import com.example.ourbook.DataTool.UserSingletone;
import com.example.ourbook.R;
import com.example.ourbook.SearchNovelActivity;
import com.example.ourbook.databinding.FragmentHomeBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // 초기화
    private static final String TAG = "HomeFragment"+ Constants.AddTAG;
    private FragmentHomeBinding binding;
    private MaterialToolbar materialToolbar;
    private NavigationView navigationView;
    private RetrofitGenerator retrofitGenerator;
    private RegisterBookService registerBookService;
    private UserSingletone userSingletone;
    private List<Book> resultBookList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "uid";
    private static final String ARG_PARAM2 = "jwt";

    // TODO: Rename and change types of parameters
    private String uid;
    private String jwtKey;

    private ExploreBookAdapter exploreBookAdapter;
    private RecyclerView recyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uid Parameter 1.
     * @param jwt Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String uid, String jwt) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        args.putString(ARG_PARAM2, jwt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_PARAM1);
            jwtKey = getArguments().getString(ARG_PARAM2);
            Log.d(TAG, "onCreate: "+ARG_PARAM1+"mParam1 "+ uid);
            Log.d(TAG, "onCreate:"+ARG_PARAM2+" mParam2 "+ jwtKey);
        }
        retrofitGenerator =new RetrofitGenerator();
        registerBookService = retrofitGenerator.init_user_retrofit(RegisterBookService.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentHomeBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        materialToolbar = binding.mToolBarMain;
        DrawerLayout drawer = binding.mainDrawerLayout;
         navigationView = binding.navViewMain;
         recyclerView = binding.mainRecyclerView;
//         exploreBookAdapter = new ExploreBookAdapter()
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                getActivity(), drawer, materialToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        navigationView.setNavigationItemSelectedListener(item -> {
//            Log.d(TAG, "onCreateView: " + item.getItemId());
//            if(item.isChecked()){
//                drawer.close();
//            }
//            // 메뉴 항목 클릭 시 처리
//            return true;
//        });

//        if (savedInstanceState == null) {
//            navigationView.getMenu().performIdentifierAction(R.menu.menu_main_drawer,0 );
//        }
        // toolbar 설정
        materialToolbar.setNavigationOnClickListener(v -> {
            drawer.open();

        });
        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuId = item.getItemId();
                if( menuId == R.id.item_main_search){
                    Log.d(TAG, "onMenuItemClick: search item click");
                    Intent intent = new Intent(getActivity(), SearchNovelActivity.class);
                    intent.putExtra("option",0); // main 에서의 검색

                    startActivity(intent);
                    return true;
                } else if (menuId == R.id.item_main_likes) {
                    Log.d(TAG, "onMenuItemClick: likes item click");
                    return true;
                }
                return false;
            }

        });


        // navigationView  item click 설정
        navigationView.setNavigationItemSelectedListener(  menuItem ->{
            // Handle menu item selected
            menuItem.setChecked(true);
            drawer.close();
            int itemId = menuItem.getItemId();
            if(itemId == R.id.mit_category){
                explore_category();
            } else if (itemId == R.id.mit_new) {

            }
            return true;
        });
        navigationView.setCheckedItem(R.id.mit_home);

        return binding.getRoot();
    }
    /*explore_category 카테고리 선택 -> 탐색을 위한 액티비티로 이동 , 카테고리 선택  */
    private void explore_category(){
        Intent intent = new Intent(getActivity(),CategorySelectActivity.class);
        intent.putExtra(Constants.INTENT_JWT,jwtKey);
        intent.putExtra(Constants.INTENT_UID,uid);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        //홈화면 돌아왔을때 이동
        navigationView.setCheckedItem(R.id.mit_home);
        Call<List<Book>> mainBookCall = registerBookService.getMainBookItem(jwtKey);
        mainBookCall.enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                resultBookList = response.body();
                generateDataList(resultBookList);
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {

            }
        });

    }

    private void generateDataList(List<Book> resultBooks) {
        exploreBookAdapter = new ExploreBookAdapter( resultBooks,getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(exploreBookAdapter);
    }
}