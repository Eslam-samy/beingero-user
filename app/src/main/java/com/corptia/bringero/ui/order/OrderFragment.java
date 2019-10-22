package com.corptia.bringero.ui.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corptia.bringero.Adapter.ViewPagerAdapter;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.order.main.current.CurrentOrderFragment;
import com.corptia.bringero.ui.order.main.lastOrder.LastOrderFragment;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OrderFragment extends Fragment {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPaper)
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    public  OrderFragment () {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this , view);

        //************ For Fragment ************
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragments(new CurrentOrderFragment(), getString(R.string.current));
        viewPagerAdapter.addFragments(new LastOrderFragment(),  getString(R.string.last_order));

        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
