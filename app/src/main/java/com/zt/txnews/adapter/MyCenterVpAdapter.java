package com.zt.txnews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zt.txnews.fragment.GuanZhuFridentsFragment;
import com.zt.txnews.fragment.MyInvitationFragment;

import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 */
public class MyCenterVpAdapter extends FragmentPagerAdapter {
    private List<String> list;

    public MyCenterVpAdapter(FragmentManager fm, List<String> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return MyInvitationFragment.getInstance();
            case 1:
                return new GuanZhuFridentsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    //标题方法，要重写
    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).toString();
    }
}
