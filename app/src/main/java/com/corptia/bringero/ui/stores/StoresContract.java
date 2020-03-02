package com.corptia.bringero.ui.stores;

import com.corptia.bringero.graphql.GetStoresOfASingleCategoryQuery;

import java.util.List;

public class StoresContract {

    public interface StoresView
    {

        void setStores( List<GetStoresOfASingleCategoryQuery.Store> repositoryList);
        void setStoresOffline( List<GetStoresOfASingleCategoryQuery.Store> repositoryList);
        void displayError(String errorMessage);
        void showProgressBar();
        void hideProgressBar();
    }


    public interface BrandsPresenter{

    }
}
