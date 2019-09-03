package com.corptia.bringero.view.brandDetail;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.corptia.bringero.model.StoreTypes;

import java.util.List;

public class ViewPagerBrandAdapter  extends FragmentPagerAdapter {

    private List<StoreTypes> brandList;

    public ViewPagerBrandAdapter(FragmentManager fm, List<StoreTypes> brandList) {
        super(fm);
        this.brandList = brandList;
    }

    @Override
    public Fragment getItem(int i) {
        BrandDetailFragment fragment = new BrandDetailFragment();
        Bundle args = new Bundle();

        //args.putString("EXTRA_DATA_NAME" , brandList.get(i).getStrCategory());
        //args.putString("EXTRA_DATA_DESC" , brandList.get(i).getStrCategoryDescription());
        //args.putString("EXTRA_DATA_IMAGE" , brandList.get(i).getStrCategoryThumb());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return brandList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return brandList.get(position).getStoreTypesName();
    }
}