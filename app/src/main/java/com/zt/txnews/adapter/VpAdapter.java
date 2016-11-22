package com.zt.txnews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zt.txnews.fragment.Fragment1;
import com.zt.txnews.fragment.Fragment10;
import com.zt.txnews.fragment.Fragment2;
import com.zt.txnews.fragment.Fragment3;
import com.zt.txnews.fragment.Fragment4;
import com.zt.txnews.fragment.Fragment5;
import com.zt.txnews.fragment.Fragment6;
import com.zt.txnews.fragment.Fragment7;
import com.zt.txnews.fragment.Fragment8;
import com.zt.txnews.fragment.Fragment9;

import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 */
public class VpAdapter extends FragmentPagerAdapter {
    private List<String> list;

    public VpAdapter(FragmentManager fm,List<String> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return Fragment1.genInstance();
            case 1:
                return Fragment2.genInstance();
            case 2:
                return Fragment3.genInstance();
            case 3:
                return Fragment4.genInstance();
            case 4:
                return Fragment5.genInstance();
            case 5:
                return Fragment6.genInstance();
            case 6:
                return Fragment7.genInstance();
            case 7:
                return Fragment8.genInstance();
            case 8:
                return Fragment9.genInstance();
            case 9:
                return Fragment10.genInstance();
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
