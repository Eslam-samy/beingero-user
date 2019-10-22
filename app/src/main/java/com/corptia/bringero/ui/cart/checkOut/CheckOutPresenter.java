package com.corptia.bringero.ui.cart.checkOut;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.CreateOrderMutation;

import org.jetbrains.annotations.NotNull;

public class CheckOutPresenter {
    CheckOutView view ;

    public CheckOutPresenter(CheckOutView view) {
        this.view = view;
    }

    public void sendOrder(){

        view.showProgressBar();

        MyApolloClient.getApollowClientAuthorization().mutate(CreateOrderMutation.builder().build())
                .enqueue(new ApolloCall.Callback<CreateOrderMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateOrderMutation.Data> response) {

                        view.hideProgressBar();

                        if (response.data().BuyingOrderMutation().create().status() == 200){

                            view.showErrorMessage(response.data().BuyingOrderMutation().create().message());


                        }
                        else
                        {

                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        view.hideProgressBar();
                    }
                });

    }
}
