package com.corptia.bringero.ui.order.main.current;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;
import com.corptia.bringero.type.DeliveryOrderFilterArray;
import com.corptia.bringero.type.DeliveryOrderFilterInput;
import com.corptia.bringero.type.DeliveryOrderStatus;
import com.corptia.bringero.type.PaginationInput;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrderPresenter {

    private CurrentOrderView view;

    public OrderPresenter(CurrentOrderView view) {
        this.view = view;
    }

    public void getDeliveryOrder() {

        view.showProgressBar();

        DeliveryOrderFilterInput filterInput = DeliveryOrderFilterInput.builder()
                .customerUserId(Common.CURRENT_USER.getId())
                .status(DeliveryOrderStatus.DELIVERED).build();
        MyApolloClient.getApollowClientAuthorization()
                .query(DeliveryOrdersQuery.builder().filter(filterInput).build())
                .enqueue(new ApolloCall.Callback<DeliveryOrdersQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DeliveryOrdersQuery.Data> response) {

                        DeliveryOrdersQuery.GetAll getAll = response.data().DeliveryOrderQuery().getAll();

                        view.hideProgressBar();
                        if (getAll.status() == 200) {

                            view.DeliveryOrders(getAll.DeliveryOrderData());

                        } else  if (getAll.status() == 404) {
                            view.onNotFoundDeliveryOrders();
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        view.showErrorMessage(e.getMessage());
                        view.hideProgressBar();

                    }
                });

    }

    public void getCurrentOrder() {

        view.showProgressBar();

        List<DeliveryOrderStatus> statusList = new ArrayList<>();
        statusList.add(DeliveryOrderStatus.ASSIGNEDTOPILOT);
        statusList.add(DeliveryOrderStatus.DELIVERING);
        statusList.add(DeliveryOrderStatus.ORDERSREQUESTED);
        statusList.add(DeliveryOrderStatus.STORESPREPARED);
        statusList.add(DeliveryOrderStatus.STORESREPLIED);

        DeliveryOrderFilterInput filterInput = DeliveryOrderFilterInput.builder()
                .customerUserId(Common.CURRENT_USER.getId())
                .iN(DeliveryOrderFilterArray.builder()
                        .status(statusList).build())
                .status(DeliveryOrderStatus.DELIVERED).build();

        MyApolloClient.getApollowClientAuthorization()
                .query(DeliveryOrdersQuery.builder().filter(filterInput).build())
                .enqueue(new ApolloCall.Callback<DeliveryOrdersQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DeliveryOrdersQuery.Data> response) {

                        DeliveryOrdersQuery.GetAll getAll = response.data().DeliveryOrderQuery().getAll();

                        view.hideProgressBar();
                        if (getAll.status() == 200) {

                            view.CurrentOrders(getAll.DeliveryOrderData());

                        } else  if (getAll.status() == 404) {
                            view.onNotFoundCurrentOrders();
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
