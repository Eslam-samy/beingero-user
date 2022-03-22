package com.corptia.bringero.ui.stores;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.databinding.ActivityStoresBinding;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.GetStoresOfASingleCategoryQuery;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoresActivity extends BaseActivity implements StoresContract.StoresView {

    ActivityStoresBinding binding;
    StoresAdapter adapter;
    Boolean isOffer;
    StoresPresenter storePresenter;
    //For Store Local Category Id
    String categoryId = "", storeTypeName;
    Handler handler = new Handler();
    List<GetStoresOfASingleCategoryQuery.Store> storesList = new ArrayList<>();
    int countRespons = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stores);
        countRespons = 0;
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            categoryId = intent.getStringExtra(Constants.EXTRA_CATEGOTY_ID);
            storeTypeName = intent.getStringExtra(Constants.EXTRA_STORE_TYPE_NAME);
            isOffer = intent.getBooleanExtra(Constants.EXTRA_STORE_OFFER, false);
        }
        binding.toolBar.setLabel(storeTypeName);
        storePresenter = new StoresPresenter(this);
        binding.recyclerBrands.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerBrands.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(0, this)));
        storePresenter.getStores(categoryId, true, isOffer);

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

        runOnUiThread(() -> {

            countRespons += 1;
            Common.LOG(" Size Before " + storesList.size());
            storesList.addAll(repositoryList);
            Common.LOG(" Size After " + storesList.size());

            Common.LOG("" + countRespons);
            if (countRespons == 2) {
                adapter = new StoresAdapter(StoresActivity.this, storesList);
                binding.recyclerBrands.setAdapter(adapter);
            } else if (countRespons == 1) {
                storePresenter.getStores(categoryId, false, isOffer);
            }


        });
    }

    @Override
    public void setStoresOffline(List<GetStoresOfASingleCategoryQuery.Store> repositoryList) {
        runOnUiThread(() -> {
            adapter = new StoresAdapter(StoresActivity.this, repositoryList);
            binding.recyclerBrands.setAdapter(adapter);
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

        handler.post(() -> binding.loading.setVisibility(View.GONE));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
