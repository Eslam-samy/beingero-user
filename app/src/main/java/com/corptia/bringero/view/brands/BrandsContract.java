package com.corptia.bringero.view.brands;

import com.corptia.bringero.model.StoreTypes;

import java.util.List;

public class BrandsContract {

    public interface BrandsView
    {

        void setBrands( List<StoreTypes> repositoryList);
        void displayError(String errorMessage);
        void showProgressBar();
        void hideProgressBar();
    }


    public interface BrandsPresenter{

    }
}
