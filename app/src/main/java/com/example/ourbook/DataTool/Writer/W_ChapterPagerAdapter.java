package com.example.ourbook.DataTool.Writer;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ourbook.writeActivity.WriteChapterListFragment;
import com.example.ourbook.writeActivity.WriteChapterSettingFragment;
import com.example.ourbook.writeActivity.WriteChapterTempSaveFragment;

import java.util.ArrayList;

//  chapter 입력 화면에 보여지는 회차 관리 view pager
public class W_ChapterPagerAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    // 웹소설 등록된 회차 list 를 보여주는 fragment
    private WriteChapterListFragment writeChapterListFragment;
    //  웹소설 설정 화면을 보여주는 fragment
    private WriteChapterSettingFragment writeChapterSettingFragment;
    // 임시 저장 list 를 보여주는 fragment
    private WriteChapterTempSaveFragment writeChapterTempSaveFragment;

    public W_ChapterPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // fragment adapter init
        switch (position){
            case 0:
                if(writeChapterListFragment == null) {
                    writeChapterListFragment = new WriteChapterListFragment();
                    addFragment(writeChapterListFragment);
                }
                break;
            case 1:
                if ( writeChapterTempSaveFragment  == null) {
                    writeChapterTempSaveFragment = new WriteChapterTempSaveFragment();
                    addFragment(writeChapterTempSaveFragment);
                }
                break;
            case 2:
                if (writeChapterSettingFragment == null) {
                    writeChapterSettingFragment = new WriteChapterSettingFragment();
                    addFragment(writeChapterSettingFragment);
                }
                break;
            default:
                return null;
        }

        return mFragmentList.get(position);

    }

    public void addFragment(Fragment fragment){

        mFragmentList.add(fragment);

    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }
}
