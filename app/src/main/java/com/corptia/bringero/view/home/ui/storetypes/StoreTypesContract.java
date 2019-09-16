package com.corptia.bringero.view.home.ui.storetypes;


import com.corptia.bringero.graphql.GetAllCategoriesQuery;

import java.util.List;

public class StoreTypesContract {

    public interface StoreTypesView{

        //void displaySearchResults( List<String> repositoryList);

        void setStoreTypes( List<GetAllCategoriesQuery.StoreCategory> repositoryList);

        void displayError(String errorMessage);

        void showProgressBar();
        void hideProgressBar();

    }

    public interface StoreTypesPresenter{

        void searchStoreTypes(String searchQuery, String afterCursor);
    }
}
