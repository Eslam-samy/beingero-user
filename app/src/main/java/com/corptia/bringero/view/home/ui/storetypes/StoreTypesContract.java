package com.corptia.bringero.view.home.ui.storetypes;

import androidx.annotation.NonNull;

import com.corptia.bringero.model.StoreTypes;

import java.util.List;

public class StoreTypesContract {

    public interface StoreTypesView{

        //void displaySearchResults( List<String> repositoryList);

        void setStoreTypes( List<StoreTypes> repositoryList);

        void displayError(String errorMessage);

        void showProgressBar();
        void hideProgressBar();

    }

    public interface StoreTypesPresenter{

        void searchStoreTypes(String searchQuery, String afterCursor);
    }
}
