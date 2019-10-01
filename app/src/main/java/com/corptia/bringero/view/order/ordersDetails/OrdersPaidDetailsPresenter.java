package com.corptia.bringero.view.order.ordersDetails;


import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;
import com.corptia.bringero.type.DeliveryOrderFilterInput;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OrdersPaidDetailsPresenter  {

    OrdersPaidDetailsView view ;

    public OrdersPaidDetailsPresenter(OrdersPaidDetailsView view) {
        this.view = view;
    }

    public void getSingleOrder(String idOrder){

        DeliveryOrderFilterInput input = DeliveryOrderFilterInput.builder()._id(idOrder).build();

        MyApolloClient.getApollowClientAuthorization().query(DeliveryOneOrderQuery.builder().filter(input).build())
                .enqueue(new ApolloCall.Callback<DeliveryOneOrderQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DeliveryOneOrderQuery.Data> response) {

                        DeliveryOneOrderQuery.GetOne getOne  = response.data().DeliveryOrderQuery().getOne();
                        if (getOne.status() ==200)
                        {
                            view.setSingleOrder(getOne.DeliveryOrderData());
                        }

                        else
                        {
                            view.showErrorMessage(getOne.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }
}
