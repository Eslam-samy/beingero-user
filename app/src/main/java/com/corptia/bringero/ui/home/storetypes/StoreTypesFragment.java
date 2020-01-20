package com.corptia.bringero.ui.home.storetypes;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.StoreTypesQuery;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;
import com.corptia.bringero.ui.home.storetypes.Adapter.StoreTypesAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class StoreTypesFragment extends Fragment implements StoreTypesContract.StoreTypesView {

    @BindView(R.id.recycler_store)
    RecyclerView recycler_store;

    @BindView(R.id.loading)
    LottieAnimationView loading;

    //private StoreTypesViewModel storeTypesViewModel;

    //Data
    StoreTypesAdapter adapter;
    StoreTypesPresenter storeTypesPresenter;

    LayoutAnimationController layoutAnimationController;

    Handler handler = new Handler();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // storeTypesViewModel = ViewModelProviders.of(this).get(StoreTypesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_store_types, container, false);

        ButterKnife.bind(this, root);

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);

        storeTypesPresenter = new StoreTypesPresenter(this);
        storeTypesPresenter.getStoreTypes();

        return root;
    }

    @Override
    public void setStoreTypes(List<StoreTypesQuery.StoreCategory> repositoryList) {

        recycler_store.setLayoutAnimation(layoutAnimationController);
        recycler_store.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        recycler_store.addItemDecoration(new GridSpacingItemDecoration(
//                2,
//                Common.dpToPx(15, getActivity()),
//                true,
//                0,
//                Common.dpToPx(10, getActivity()),
//                Common.dpToPx(2, getActivity()),
//                Common.dpToPx(2, getActivity())));

        recycler_store.addItemDecoration(new GridSpacingItemDecoration(
                2,
                Common.dpToPx(5, getActivity()),
                true,
                0,
                Common.dpToPx(17, getActivity()),
                Common.dpToPx(10, getActivity()),
                Common.dpToPx(10, getActivity())));


        adapter = new StoreTypesAdapter(getActivity(), repositoryList);
        recycler_store.setAdapter(adapter);

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

        handler.post(new Runnable() {
            @Override
            public void run() {

                loading.resumeAnimation();
                loading.setVisibility(View.GONE);
            }
        });

    }


}