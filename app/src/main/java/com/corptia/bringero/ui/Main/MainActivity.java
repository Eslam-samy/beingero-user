package com.corptia.bringero.ui.Main;

import android.os.Bundle;
import android.widget.ImageView;

import com.corptia.bringero.Adapter.ViewPagerAdapter;
import com.corptia.bringero.R;
import com.corptia.bringero.utils.lib.CustomViewPager;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.Main.login.LoginFragment;
import com.corptia.bringero.ui.Main.signup.SignupFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends BaseActivity {


    //For Fragment
    TabLayout tabLayout;
    CustomViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    ImageView img_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_logo = findViewById(R.id.img_logo);

        //Views fragments
        viewPager = findViewById(R.id.viewPaper);
        tabLayout = findViewById(R.id.tabLayout);

        //************ For Fragment ************
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new LoginFragment(), "Login");
        viewPagerAdapter.addFragments(new SignupFragment(), "Signup");

        viewPager.setOffscreenPageLimit(0);
        viewPager.setPagingEnabled(false);

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
