package com.corptia.bringero.ui.stores;

import android.os.Handler;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetStoresOfASingleCategoryQuery;
import com.corptia.bringero.type.SortDirectionEnum;
import com.corptia.bringero.type.StoreFilterInput;
import com.corptia.bringero.type.StoreSortByEnum;
import com.corptia.bringero.type.StoreSortingInput;
import com.corptia.bringero.type.StoreStatus;

import org.jetbrains.annotations.NotNull;

public class StoresPresenter {

    StoresContract.StoresView storesView;
    Handler handler;

    public StoresPresenter(StoresContract.StoresView storesView) {
        this.storesView = storesView;
        handler = new Handler();
    }

    void getStores(String typeId, boolean isAvailable) {

        storesView.showProgressBar();

        StoreSortingInput sortingInput = StoreSortingInput.builder().sortBy(StoreSortByEnum.DISPLAYPRIORITY).sortDirection(SortDirectionEnum.DESC).build();
        StoreFilterInput filterInput = StoreFilterInput.builder().storeTypeId(typeId).status(StoreStatus.ACTIVE).isAvailable(isAvailable).build();

        MyApolloClient.getApollowClientAuthorization()
                .query(GetStoresOfASingleCategoryQuery.builder()
                        .sorting(sortingInput)
                        .filter(filterInput)
                        .build())
                .enqueue(new ApolloCall.Callback<GetStoresOfASingleCategoryQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetStoresOfASingleCategoryQuery.Data> response) {


                        if (response.data().StoreQuery().getAll().status() == 200) {

                            if (!isAvailable)
                                storesView.hideProgressBar();

//                            if (isAvailable)
                                storesView.setStores(response.data().StoreQuery().getAll().Stores());
//                            else
//                                storesView.setStoresOffline(response.data().StoreQuery().getAll().Stores());

                        }
                        else if (response.data().StoreQuery().getAll().status() == 404)
                        {

                            if (!isAvailable)
                                storesView.hideProgressBar();

//                            if (isAvailable)
                            storesView.setStores(response.data().StoreQuery().getAll().Stores());
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }
}
