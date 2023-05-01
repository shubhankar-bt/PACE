package com.newproject.pace.Adapter;


import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.newproject.pace.ui.home.DateTaskFragment;
import com.newproject.pace.ui.home.NameTaskFragment;


public class TaskViewPagerAdapter extends FragmentPagerAdapter {

    private final int totalTabs;


    public TaskViewPagerAdapter(@NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 1) {
            return new DateTaskFragment();
        }
        return new NameTaskFragment();

    }



    @Override
    public int getCount() {
        return totalTabs;
    }
}
