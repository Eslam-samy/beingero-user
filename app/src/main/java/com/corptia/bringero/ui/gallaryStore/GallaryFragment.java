package com.corptia.bringero.ui.gallaryStore;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.storesDetail.ViewPagerStoreAdapter;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GallaryFragment extends Fragment   {



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



    public GallaryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallary, container, false);
        ButterKnife.bind(this,view);


        txt_name_store.setText(Common.CURRENT_USER.firstName());


        //this code is very important ( getChildFragmentManager() ) instant getActivity().getSupportFragmentManager()
        ViewPagerStoreAdapter adapter = new ViewPagerStoreAdapter(
                getChildFragmentManager() ,
                Common.CURRENT_STORE.ProductTypesStore().data(),
                true);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0, true);
        adapter.notifyDataSetChanged();

        return view;
    }

}
