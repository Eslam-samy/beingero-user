package com.corptia.bringero.view.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.corptia.bringero.Adapter.ViewPagerAdapter;
import com.corptia.bringero.R;
import com.corptia.bringero.view.Main.login.LoginFragment;
import com.corptia.bringero.view.Main.signup.SignupFragment;
import com.corptia.bringero.view.order.main.current.CurrentOrderFragment;
import com.corptia.bringero.view.order.main.lastOrder.LastOrderFragment;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends AppCompatActivity {

    //For Fragment
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        //Views fragments
        viewPager = findViewById(R.id.viewPaper);
        tabLayout = findViewById(R.id.tabLayout);

        //************ For Fragment ************
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new CurrentOrderFragment(), getString(R.string.current));
        viewPagerAdapter.addFragments(new LastOrderFragment(),  getString(R.string.last_order));

        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        initActionBar();

    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
