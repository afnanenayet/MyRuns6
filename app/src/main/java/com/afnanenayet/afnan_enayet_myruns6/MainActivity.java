package com.afnanenayet.afnan_enayet_myruns6;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * Initialized class from Android Studio - some boilerplate code provided by the Studio,
     * rest modified/added by
     *
     * @Author Afnan Enayet
     */

    private MyRunsFragmentPageAdapter pagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing list of fragments
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new StartFragment());
        fragmentList.add(new HistoryFragment());
        fragmentList.add(new SettingsFragment());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = new MyRunsFragmentPageAdapter(getFragmentManager(),
                fragmentList);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
