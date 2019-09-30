package com.corptia.bringero.view.order.main.current;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CurrentOrderPresenter {

    private CurrentOrderView view;

    public CurrentOrderPresenter(CurrentOrderView view) {
        this.view = view;
    }

    public void getDeliveryOrder() {

        view.showProgressBar();

        MyApolloClient.getApollowClientAuthorization()
                .query(DeliveryOrdersQuery.builder().build())
                .enqueue(new ApolloCall.Callback<DeliveryOrdersQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DeliveryOrdersQuery.Data> response) {

                        DeliveryOrdersQuery.GetAll getAll = response.data().DeliveryOrderQuery().getAll();

                        view.hideProgressBar();
                        if (getAll.status() == 200) {

                            view.DeliveryOrders(getAll.DeliveryOrderData());

                        } else {
                            view.showErrorMessage(getAll.message());

                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        view.showErrorMessage(e.getMessage());
                        view.hideProgressBar();

                    }
                });

    }
}
