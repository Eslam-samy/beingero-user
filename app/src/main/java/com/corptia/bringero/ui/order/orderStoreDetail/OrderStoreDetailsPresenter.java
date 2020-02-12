package com.corptia.bringero.ui.order.orderStoreDetail;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.SingleOrderQuery;
import com.corptia.bringero.utils.PicassoUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OrderStoreDetailsPresenter {

    OrderStoreDetailsView view;

    public OrderStoreDetailsPresenter(OrderStoreDetailsView view) {
        this.view = view;
    }

    public void getOrderStoreDetails(String BUYING_ORDER_ID){

        view.showProgressBar();

        MyApolloClient.getApollowClientAuthorization().query(SingleOrderQuery.builder().buyingOrderId(BUYING_ORDER_ID).build())
                .enqueue(new ApolloCall.Callback<SingleOrderQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SingleOrderQuery.Data> response) {

                        SingleOrderQuery.Get orderResponse = response.data().BuyingOrderQuery().get();

                        if (orderResponse.status() == 200) {


                            view.setOrderStoreDetails(orderResponse);
                            view.hideProgressBar();

                        } else {

                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        view.hideProgressBar();
                    }
                });


    }
}
