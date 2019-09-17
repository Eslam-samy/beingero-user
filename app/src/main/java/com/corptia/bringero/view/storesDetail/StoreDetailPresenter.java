package com.corptia.bringero.view.storesDetail;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoreDetailPresenter {

    StoreDetailContract.StoreDetailView view ;

    public StoreDetailPresenter(StoreDetailContract.StoreDetailView view) {
        this.view = view;
    }


    void getStoreDetailHeader(String storeId){

        MyApolloClient.getApollowClient().query(SingleStoreHeaderQuery.builder().storeId(storeId).build())
                .enqueue(new ApolloCall.Callback<SingleStoreHeaderQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SingleStoreHeaderQuery.Data> response) {

                        SingleStoreHeaderQuery.Get data = response.data().StoreQuery().get();
                        if (data.status() == 200){
                            view.setStoresDetailHeader(data.StoreDetail());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }
}
