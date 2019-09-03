package com.corptia.bringero.view.brands;

import com.corptia.bringero.R;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

public class BrandsPresenter  {

    BrandsContract.BrandsView brandsView;

    public BrandsPresenter(BrandsContract.BrandsView brandsView) {
        this.brandsView = brandsView;
    }

    void getBrands(){

        brandsView.showProgressBar();
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

        brandsView.setBrands(storeTypesList);
    }
}
