package com.zt.txnews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zt.txnews.fragment.FriendsvpFragment;

/**
 * Created by Administrator on 2016/9/19.
 */
public class FriendsVpAdapter extends FragmentPagerAdapter {
    public FriendsVpAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position==0) {
            return new FriendsvpFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
