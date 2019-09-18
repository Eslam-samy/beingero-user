package com.corptia.bringero.view.stores;

import android.os.Handler;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetStoresOfASingleCategoryQuery;

import org.jetbrains.annotations.NotNull;

public class StoresPresenter {

    StoresContract.BrandsView brandsView;
    Handler handler ;

    public StoresPresenter(StoresContract.BrandsView brandsView) {
        this.brandsView = brandsView;
        handler = new Handler();
    }

    void getStores(String categoryId){

        brandsView.showProgressBar();

        MyApolloClient.getApollowClientAuthorization().query(GetStoresOfASingleCategoryQuery.builder().typeId(categoryId).build())
                .enqueue(new ApolloCall.Callback<GetStoresOfASingleCategoryQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetStoresOfASingleCategoryQuery.Data> response) {

                        if (response.data().StoreQuery().getAll().status() == 200){

                            response.data().StoreQuery().getAll().Stores();
                            brandsView.setStores(response.data().StoreQuery().getAll().Stores());

                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }
}
