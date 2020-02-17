package com.corptia.bringero.ui.home.cart.checkOut;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.ConfirmOrderMutation;
import com.corptia.bringero.graphql.CreateOrderMutation;
import com.corptia.bringero.graphql.ValidateCouponQuery;
import com.corptia.bringero.type.CreateBuyingOrder;
import com.corptia.bringero.type.DeliveryCouponFilterInput;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheckOutPresenter {
    CheckOutView view;

    public CheckOutPresenter(CheckOutView view) {
        this.view = view;
    }

    public void sendOrder(String couponCode) {

//        view.showProgressBar();
//
//        MyApolloClient.getApollowClientAuthorization().mutate(CreateOrderMutation.builder().build())
//                .enqueue(new ApolloCall.Callback<CreateOrderMutation.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<CreateOrderMutation.Data> response) {
//
//                        view.hideProgressBar();
//
//                        if (response.data().BuyingOrderMutation().create().status() == 200) {
//
//                            String orderId =  response.data().BuyingOrderMutation().create().data()._id();
//                            int serial =  response.data().BuyingOrderMutation().create().data().serial();
//                            view.onSuccessCreateOrder(orderId , serial);
//
////                            view.onSuccessMessage("");
//
//                        } else {
//                            view.showErrorMessage(response.data().BuyingOrderMutation().create().message());
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//                        view.hideProgressBar();
//                        view.showErrorMessage(e.getMessage());
//
//                    }
//                });

        //-----------------------------------------------------------------

        view.showProgressBar();

        CreateBuyingOrder createBuyingOrder;
        if (!couponCode.isEmpty())
        createBuyingOrder = CreateBuyingOrder.builder().couponCode(couponCode).build();
        else
            createBuyingOrder = CreateBuyingOrder.builder().build();


        MyApolloClient.getApollowClientAuthorization()
                .mutate(ConfirmOrderMutation.builder().data(createBuyingOrder).build())
                .enqueue(new ApolloCall.Callback<ConfirmOrderMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ConfirmOrderMutation.Data> response) {

                        view.hideProgressBar();


                        if (response.data().BuyingOrderMutation().create().status() == 200){

                            String orderId = response.data().BuyingOrderMutation().create().data()._id();
                            int serial = response.data().BuyingOrderMutation().create().data().serial();
                            view.onSuccessCreateOrder(orderId, serial);

//                            view.onSuccessMessage("");

                        }
                        else {
                            view.showErrorMessage(response.data().BuyingOrderMutation().create().message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        view.hideProgressBar();
                        view.showErrorMessage(e.getMessage());
                    }
                });

    }

    public void validateCoupon(String couponCode , String userId){

        view.showProgressBar();

        DeliveryCouponFilterInput filterInput = DeliveryCouponFilterInput.builder().code(couponCode).build();

        MyApolloClient.getApollowClientAuthorization()
                .query(ValidateCouponQuery.builder().filter(filterInput).userId(userId).build())
                .enqueue(new ApolloCall.Callback<ValidateCouponQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ValidateCouponQuery.Data> response) {

                        view.hideProgressBar();

                        ValidateCouponQuery.@Nullable GetOne data = response.data().DeliveryCouponQuery().getOne();

                        if (data.status() == 200){

                            view.onSuccessValidateCoupon(data.data());
                        }
                        else
                        {
                            //Failed
                            view.onNotFoundValidateCoupon();
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        view.onNotFoundValidateCoupon();
                        view.hideProgressBar();
                    }
                });

    }
}
