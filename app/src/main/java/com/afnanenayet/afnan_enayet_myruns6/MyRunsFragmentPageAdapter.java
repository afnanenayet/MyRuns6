package com.afnanenayet.afnan_enayet_myruns6;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by aenayet on 1/13/17.
 */

/**
 * A an adapter which manages the tabs in the main UI
 */
class MyRunsFragmentPageAdapter extends FragmentPagerAdapter {
    static final String[] fragmentTitles = {
            "Start",
            "History",
            "Settings"
    };
    private ArrayList<Fragment> fragments;

    public MyRunsFragmentPageAdapter(FragmentManager fragmentManager,
                                     ArrayList<Fragment> fragmentList) {
        super(fragmentManager);
        this.fragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles[position];
    }
}
