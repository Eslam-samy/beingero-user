package com.corptia.bringero.ui.order.ordersDetails;


import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OrdersPaidDetailsPresenter  {

    OrdersPaidDetailsView view ;

    public OrdersPaidDetailsPresenter(OrdersPaidDetailsView view) {
        this.view = view;
    }

    public void getSingleOrder(String idOrder){


        MyApolloClient.getApollowClientAuthorization().query(DeliveryOneOrderQuery.builder()._id(idOrder).build())
                .enqueue(new ApolloCall.Callback<DeliveryOneOrderQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<DeliveryOneOrderQuery.Data> response) {

                        DeliveryOneOrderQuery.Get responseData   = response.data().DeliveryOrderQuery().get();
                        if (responseData.status() ==200)
                        {
                            view.setSingleOrder(responseData.DeliveryOrderData());
                        }

                        else
                        {
                            view.showErrorMessage(responseData.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }
}
