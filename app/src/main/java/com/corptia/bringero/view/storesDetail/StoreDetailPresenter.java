package com.corptia.bringero.view.storesDetail;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetProductQuery;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;
import com.corptia.bringero.type.ProductFilterInput;

import org.jetbrains.annotations.NotNull;

public class StoreDetailPresenter {

    StoreDetailContract.StoreDetailView view;

    public StoreDetailPresenter(StoreDetailContract.StoreDetailView view) {
        this.view = view;
    }


    void getStoreDetailHeader(String storeId) {

        MyApolloClient.getApollowClient().query(SingleStoreHeaderQuery.builder().storeId(storeId).build())
                .enqueue(new ApolloCall.Callback<SingleStoreHeaderQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SingleStoreHeaderQuery.Data> response) {

                        SingleStoreHeaderQuery.Get data = response.data().StoreQuery().get();
                        if (data.status() == 200) {
                            view.setStoresDetailHeader(data.StoreDetail());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }


    public void getProductStore(String typeId, boolean isPrice) {

        ProductFilterInput productFilterInput = null;
        if (isPrice)
            productFilterInput = ProductFilterInput.builder().typeId(typeId).build();
//        else
//            productFilterInput = ProductFilterInput.builder().typeId(typeId).notPricedBy(Common.CURRENT_STORE._id()).build();

        MyApolloClient.getApollowClientAuthorization().query(GetProductQuery.builder().filter(productFilterInput).build())
                .enqueue(new ApolloCall.Callback<GetProductQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetProductQuery.Data> response) {


                        if (response.data().ProductQuery().getAll().status() == 200) {
                            view.setProduct(response.data().ProductQuery().getAll().Product());
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }
}
