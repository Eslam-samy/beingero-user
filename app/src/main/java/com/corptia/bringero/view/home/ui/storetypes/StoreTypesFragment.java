package com.corptia.bringero.view.home.ui.storetypes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.GridSpacingItemDecoration;
import com.corptia.bringero.graphql.GetAllCategoriesQuery;
import com.corptia.bringero.model.StoreTypes;
import com.corptia.bringero.view.home.ui.storetypes.Adapter.StoreTypesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


public class StoreTypesFragment extends Fragment implements StoreTypesContract.StoreTypesView {

    @BindView(R.id.recycler_store)
    RecyclerView recycler_store;

    //private StoreTypesViewModel storeTypesViewModel;

    //Data
    StoreTypesAdapter adapter;
    List<StoreTypes> storeTypesList = new ArrayList<>();

    StoreTypesPresenter storeTypesPresenter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // storeTypesViewModel = ViewModelProviders.of(this).get(StoreTypesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_store_types, container, false);

        ButterKnife.bind(this, root);

        //final TextView textView = root.findViewById(R.id.text_home);


       /* storeTypesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        storeTypesPresenter = new StoreTypesPresenter(this);
        storeTypesPresenter.getStoreTypes();

        return root;
    }

    @Override
    public void setStoreTypes(List<GetAllCategoriesQuery.StoreCategory> repositoryList) {

        recycler_store.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recycler_store.addItemDecoration(new GridSpacingItemDecoration(2, Common.dpToPx(15, getActivity()), true, 0, Common.dpToPx(10, getActivity())));
        adapter = new StoreTypesAdapter(getActivity(), repositoryList);
        recycler_store.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayError(String errorMessage) {

        Toasty.error(getActivity() , errorMessage).show();
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }


}