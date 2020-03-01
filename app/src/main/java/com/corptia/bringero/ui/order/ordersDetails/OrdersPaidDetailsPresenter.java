package com.corptia.bringero.ui.order.ordersDetails;


import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.BuyingOrderRatingMutation;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;
import com.corptia.bringero.graphql.DeliveryOrderRatingMutation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OrdersPaidDetailsPresenter {

    OrdersPaidDetailsView view;

    public OrdersPaidDetailsPresenter(OrdersPaidDetailsView view) {
        this.view = view;
    }

    public void getSingleOrder(String idOrder) {

        view.showProgressBar();

        MyApolloClient.getApollowClientAuthorization().query(DeliveryOneOrderQuery.builder()._id(idOrder).build())
                .enqueue(new ApolloCall.Callback<DeliveryOneOrderQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DeliveryOneOrderQuery.Data> response) {

                        view.hideProgressBar();
                        DeliveryOneOrderQuery.Get responseData = response.data().DeliveryOrderQuery().get();
                        if (responseData.status() == 200) {
                            view.setSingleOrder(responseData.DeliveryOrderData());
                        } else {
                            view.showErrorMessage(responseData.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        view.hideProgressBar();
                    }
                });

    }

    public void pilotRating(String BUYING_ORDER_ID , int rating){

        view.onShowLoadingDialog();

        MyApolloClient
                .getApollowClientAuthorization()
                .mutate(DeliveryOrderRatingMutation.builder()._id(BUYING_ORDER_ID).rate(rating).build())
                .enqueue(new ApolloCall.Callback<DeliveryOrderRatingMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DeliveryOrderRatingMutation.Data> response) {

                        view.onHideLoadingDialog();

                        if (response.data().DeliveryOrderMutation().pilotDeliveryRating().status() == 200){

                            view.onSuccessRating();

                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        view.onHideLoadingDialog();
                    }
                });

    }
}
