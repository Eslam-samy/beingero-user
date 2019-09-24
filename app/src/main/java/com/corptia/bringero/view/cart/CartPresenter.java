package com.corptia.bringero.view.cart;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.MyCartQuery;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CartPresenter {

    CartContract.CartView view;

    public CartPresenter(CartContract.CartView view) {
        this.view = view;
    }

    public void getMyCart() {

        Log.d("HAZEM" , "SIZE : From API : START" );

        MyApolloClient.getApollowClientAuthorization().query(MyCartQuery.builder().build())
                .enqueue(new ApolloCall.Callback<MyCartQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<MyCartQuery.Data> response) {
                        MyCartQuery.MyCart myCart = response.data().CartItemQuery().myCart();
                        if (myCart.status() == 200) {
                            view.setMyCart(myCart.storeData());
                            Log.d("HAZEM" , "SIZE : From API : " + myCart.storeData());
                        }

                        else
                        {
                            Log.d("HAZEM" , "SIZE : From API : " + myCart.storeData());

                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("HAZEM" , "SIZE : From API : " + e.getMessage());

                    }
                });

    }
}
