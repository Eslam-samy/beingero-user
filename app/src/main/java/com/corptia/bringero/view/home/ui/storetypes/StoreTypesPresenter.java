package com.corptia.bringero.view.home.ui.storetypes;

import com.corptia.bringero.R;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

public class StoreTypesPresenter {


    StoreTypesContract.StoreTypesView storeTypesView ;

    public StoreTypesPresenter(StoreTypesContract.StoreTypesView storeTypesView) {
        this.storeTypesView = storeTypesView;
    }

    void getStoreTypes(){

        storeTypesView.showProgressBar();
        List<StoreTypes> storeTypesList = new ArrayList<>();

        //Set Data
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data"));

        storeTypesView.setStoreTypes(storeTypesList);
    }

}
