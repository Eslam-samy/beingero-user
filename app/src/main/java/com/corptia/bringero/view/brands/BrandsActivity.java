package com.corptia.bringero.view.brands;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.GetStoresOfASingleCategoryQuery;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandsActivity extends AppCompatActivity implements BrandsContract.BrandsView {

    @BindView(R.id.recycler_brands)
    RecyclerView recycler_brands;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    BrandsAdapter adapter ;
    List<StoreTypes> storeTypesList = new ArrayList<>();

    BrandsPresenter brandsPresenter;

    //For Store Local Category Id
    String categoryId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent!=null)
        {
            categoryId = intent.getStringExtra(Constants.EXTRA_CATEGOTY_ID);
        }

        brandsPresenter = new BrandsPresenter(this);


        recycler_brands.setLayoutManager(new LinearLayoutManager(this));
        recycler_brands.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,this)));

        brandsPresenter.getStores(categoryId);

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

    @Override
    public void setStores(List<GetStoresOfASingleCategoryQuery.Store> repositoryList) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new BrandsAdapter(BrandsActivity.this , repositoryList);
                recycler_brands.setAdapter(adapter);
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
}
