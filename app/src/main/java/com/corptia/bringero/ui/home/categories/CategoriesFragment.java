package com.corptia.bringero.ui.home.categories;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.databinding.FragmentCategoriesBinding;
import com.corptia.bringero.databinding.FragmentStoreTypesBinding;
import com.corptia.bringero.graphql.StoreTypesQuery;
import com.corptia.bringero.ui.home.storetypes.Adapter.StoreTypesAdapter;
import com.corptia.bringero.ui.home.storetypes.StoreTypesContract;
import com.corptia.bringero.ui.home.storetypes.StoreTypesPresenter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationView;
import com.corptia.bringero.utils.customAppBar.BarClicks;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;

import java.util.List;

import es.dmoral.toasty.Toasty;


public class CategoriesFragment extends Fragment implements StoreTypesContract.StoreTypesView, BarClicks {
    StoreTypesAdapter adapter;
    StoreTypesPresenter storeTypesPresenter;
    LayoutAnimationController layoutAnimationController;
    Boolean isOffer;
    Handler handler = new Handler();

    FragmentCategoriesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        if (getArguments() != null) {
            isOffer = getArguments().getBoolean(Constants.EXTRA_STORE_OFFER, false);
        }
        storeTypesPresenter = new StoreTypesPresenter(this);
        storeTypesPresenter.getStoreTypes(getString(R.string.special_offers));
        binding.toolBar.setClicks(this);

        View view = binding.getRoot();
        return view;
    }

    @Override
    public void setStoreTypes(List<StoreTypesQuery.StoreCategory> repositoryList) {
        binding.categoriesRecycler.setLayoutAnimation(layoutAnimationController);
        binding.categoriesRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        recycler_store.addItemDecoration(new GridSpacingItemDecoration(
//                2,
//                Common.dpToPx(15, getActivity()),
//                true,
//                0,
//                Common.dpToPx(10, getActivity()),
//                Common.dpToPx(2, getActivity()),
//                Common.dpToPx(2, getActivity())));

        binding.categoriesRecycler.addItemDecoration(new GridSpacingItemDecoration(
                2,
                Common.dpToPx(0, getActivity()), //All Space card in all round
                true,
                0,
                Common.dpToPx(17, getActivity()), // Top
                Common.dpToPx(10, getActivity()), // Left
                Common.dpToPx(10, getActivity()))); // Right


        adapter = new StoreTypesAdapter(getActivity(), repositoryList);
        binding.categoriesRecycler.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayError(String errorMessage) {
        Toasty.error(getActivity(), errorMessage).show();

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {
        handler.post(() -> {

            binding.loading.resumeAnimation();
            binding.loading.setVisibility(View.GONE);
        });

    }

    @Override
    public void onBackClick() {
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onNotficationClick() {

    }

}