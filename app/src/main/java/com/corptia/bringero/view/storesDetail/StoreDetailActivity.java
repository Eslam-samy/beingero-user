package com.corptia.bringero.view.storesDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.GetProductQuery;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;
import com.corptia.bringero.model.StoreTypes;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreDetailActivity extends AppCompatActivity implements StoreDetailContract.StoreDetailView {

    @BindView(R.id.toolBar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.image_store)
    ImageView image_store;
    @BindView(R.id.txt_status)
    TextView txt_status;
    @BindView(R.id.txt_name_store)
    TextView txt_name_store;


    List<StoreTypes> storeTypesList = new ArrayList<>();

    //Presenter
    StoreDetailPresenter presenter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        ButterKnife.bind(this);
        initActionBar();
        initIntent();

        presenter = new StoreDetailPresenter(this);
        Intent intent = getIntent();
        String storeId = intent.getStringExtra(Constants.EXTRA_STORE_ID);
        presenter.getStoreDetailHeader(storeId);

        Log.d("HAZEM" , "Welcome Activity Again");

    }

    private void initIntent() {

        //Intent intent = getIntent();
        //List<StoreTypes> categories = (List<StoreTypes>) intent.getSerializableExtra(HomeActivity.EXTRA_CATEGORY);

        //int position = intent.getIntExtra(HomeActivity.EXTRA_POSITION,0);


        storeTypesList.add(new StoreTypes(R.drawable.img1, "اغذيه"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "مستحضرات تجميل"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "منظفات"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "مجمدات"));
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

        ViewPagerStoreAdapter adapter = new ViewPagerStoreAdapter(
                getSupportFragmentManager(),
                Common.CURRENT_STORE.ProductTypesStore().data(),
                true); //TODO this change And Not Run Becase I work this adapter To StoreAdmin

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0, true);
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

    @Override
    public void setStoresDetailHeader(SingleStoreHeaderQuery.StoreDetail detail) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                txt_name_store.setText(detail.name());

            }
        });

    }

    @Override
    public void displayError(String errorMessage) {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void setProduct(List<GetProductQuery.Product> product) {

    }
}
