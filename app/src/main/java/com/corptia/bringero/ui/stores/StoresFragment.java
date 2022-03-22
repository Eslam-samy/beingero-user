package com.corptia.bringero.ui.stores;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corptia.bringero.R;
import com.corptia.bringero.databinding.FragmentStoresBinding;
import com.corptia.bringero.graphql.GetStoresOfASingleCategoryQuery;

import java.util.ArrayList;
import java.util.List;


public class StoresFragment extends Fragment {

    FragmentStoresBinding binding;
    StoresAdapter adapter;
    Boolean isOffer;
    StoresPresenter storePresenter;
    //For Store Local Category Id
    String categoryId = "", storeTypeName;
    Handler handler = new Handler();
    List<GetStoresOfASingleCategoryQuery.Store> storesList = new ArrayList<>();
    int countRespons = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stores, container, false);


        return binding.getRoot();
    }
}