package com.corptia.bringero.ui.home.storetypes;


import com.corptia.bringero.graphql.StoreTypesQuery;

import java.util.List;

public class StoreTypesContract {

    public interface StoreTypesView{

        //void displaySearchResults( List<String> repositoryList);

        void setStoreTypes(List<StoreTypesQuery.StoreCategory> repositoryList);

        void displayError(String errorMessage);

        void showProgressBar();
        void hideProgressBar();

    }

    public interface StoreTypesPresenter{

        void searchStoreTypes(String searchQuery, String afterCursor);
    }
}
