package com.corptia.bringero.view.gallaryStore;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.corptia.bringero.R;
import com.corptia.bringero.model.StoreTypes;
import com.corptia.bringero.view.storesDetail.ViewPagerStoreAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class GallaryFragment extends Fragment {



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


    public GallaryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallary, container, false);
        ButterKnife.bind(this,view);


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

        ViewPagerStoreAdapter adapter = new ViewPagerStoreAdapter(
                getActivity().getSupportFragmentManager(),
                storeTypesList);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0, true);
        adapter.notifyDataSetChanged();

        return view;
    }

}
