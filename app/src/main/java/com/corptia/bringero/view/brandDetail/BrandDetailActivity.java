package com.corptia.bringero.view.brandDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.corptia.bringero.R;
import com.corptia.bringero.model.StoreTypes;
import com.corptia.bringero.view.home.HomeActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolBar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    List<StoreTypes> storeTypesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail);

        ButterKnife.bind(this);
        initActionBar();
        initIntent();

    }

    private void initIntent() {

        //Intent intent = getIntent();
        //List<StoreTypes> categories = (List<StoreTypes>) intent.getSerializableExtra(HomeActivity.EXTRA_CATEGORY);

        //int position = intent.getIntExtra(HomeActivity.EXTRA_POSITION,0);


        storeTypesList.add(new StoreTypes(R.drawable.img1, "data1"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data2"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data3"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data4"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data5"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data6"));
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data7"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data8"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data9"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data10"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data11"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data12"));
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data13"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data14"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data15"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data16"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data17"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data18"));

        ViewPagerBrandAdapter adapter = new ViewPagerBrandAdapter(
                getSupportFragmentManager(),
                storeTypesList);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0,true);
        adapter.notifyDataSetChanged();
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
