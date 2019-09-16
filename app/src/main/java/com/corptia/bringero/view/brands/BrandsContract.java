package com.corptia.bringero.view.brands;

import com.corptia.bringero.graphql.GetStoresOfASingleCategoryQuery;
import com.corptia.bringero.model.StoreTypes;

import java.util.List;

public class BrandsContract {

    public interface BrandsView
    {

        void setStores( List<GetStoresOfASingleCategoryQuery.Store> repositoryList);
        void displayError(String errorMessage);
        void showProgressBar();
        void hideProgressBar();
    }


    public interface BrandsPresenter{

    }
}
