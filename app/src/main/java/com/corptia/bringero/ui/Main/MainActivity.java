package com.corptia.bringero.ui.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.corptia.bringero.Adapter.ViewPagerAdapter;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.lib.CustomViewPager;
import com.corptia.bringero.ui.Main.login.LoginFragment;
import com.corptia.bringero.ui.Main.signup.SignupFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {


    //For Fragment
    TabLayout tabLayout;
    CustomViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
