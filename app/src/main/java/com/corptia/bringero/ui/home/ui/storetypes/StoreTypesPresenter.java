package com.corptia.bringero.ui.home.ui.storetypes;

import android.os.Handler;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.StoreTypesQuery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoreTypesPresenter {


    StoreTypesContract.StoreTypesView storeTypesView;
    Handler handler = new Handler();

    public StoreTypesPresenter(StoreTypesContract.StoreTypesView storeTypesView) {
        this.storeTypesView = storeTypesView;
    }

    void getStoreTypes() {

        storeTypesView.showProgressBar();
        //Set Data
        //storeTypesList.add(new StoreTypes(R.drawable.img1, "data"));

        MyApolloClient.getApollowClientAuthorization().query(StoreTypesQuery.builder().build())
                .enqueue(new ApolloCall.Callback<StoreTypesQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<StoreTypesQuery.Data> response) {

                        StoreTypesQuery.@Nullable GetAllStoresCount allStores = response.data().StoreTypeQuery().getAllStoresCount();

                        if (allStores.status() == 200) {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    @Nullable List<StoreTypesQuery.StoreCategory> storeCategoryList = allStores.StoreCategory();
                                    storeTypesView.setStoreTypes(storeCategoryList);
                                    storeTypesView.hideProgressBar();
                                }
                            });


                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
//                .enqueue(new ApolloCall.Callback<GetAllCategoriesQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<GetAllCategoriesQuery.Data> response) {
//
//                        if (response.data().StoreTypeQuery().getAll().status() == 200) {
//
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    List<GetAllCategoriesQuery.StoreCategory> storeCategoryList = response.data().StoreTypeQuery().getAll().StoreCategory();
//                                    storeTypesView.setStoreTypes(storeCategoryList);
//                                    storeTypesView.hideProgressBar();
//                                }
//                            });
//
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//
//                    }
//                });

        //storeTypesView.setStoreTypes(storeTypesList);
    }

}
