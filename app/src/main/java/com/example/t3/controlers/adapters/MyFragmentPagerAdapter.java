package com.example.t3.controlers.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mList = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //return this.mList == null ? null : this.mList.get(position);
        return  this.mList.get(position);
    }

    @Override
    public int getCount() {
        return this.mList == null ? 0 : this.mList.size();
    }
}
