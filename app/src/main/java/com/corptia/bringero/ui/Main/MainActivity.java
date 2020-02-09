package com.corptia.bringero.ui.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.corptia.bringero.Adapter.ViewPagerAdapter;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.utils.language.LocaleHelper;
import com.corptia.bringero.utils.lib.CustomViewPager;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.Main.login.LoginFragment;
import com.corptia.bringero.ui.Main.signup.SignupFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {


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
        viewPagerAdapter.addFragments(new LoginFragment(), getString(R.string.login));
        viewPagerAdapter.addFragments(new SignupFragment(), getString(R.string.signup));

        viewPager.setOffscreenPageLimit(0);
        viewPager.setPagingEnabled(false);

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

//    @Nullable
//    @Override
//    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
//        LocaleHelper.setLocale(this,"en");
//        return super.onCreateView(parent, name, context, attrs);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Common.CURRENT_USER!=null)
        LocaleHelper.setLocale(this, Common.CURRENT_USER.getLanguage().toLowerCase());
    }

//
//    //For Get Language
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(LocaleHelper.onAttach(base));
//    }
}
