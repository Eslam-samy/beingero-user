package com.corptia.bringero.ui.storesDetail;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.graphql.ProductsTypeQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;

import java.util.List;

public class ViewPagerStoreAdapter extends FragmentPagerAdapter {

    private List<ProductsTypeQuery.Data1> productsTypes;

    public ViewPagerStoreAdapter(FragmentManager fm, List<ProductsTypeQuery.Data1> productsTypes) {
        super(fm);
        this.productsTypes = productsTypes;
    }

    @Override
    public Fragment getItem(int i) {

        StoreDetailFragment fragment = new StoreDetailFragment();
        Bundle args = new Bundle();

        args.putString(Constants.EXTRA_PRODUCT_TYPE_ID, productsTypes.get(i)._id());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return productsTypes.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return productsTypes.get(position).name();
    }
}