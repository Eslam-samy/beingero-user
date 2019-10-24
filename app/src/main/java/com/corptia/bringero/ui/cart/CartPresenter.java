package com.corptia.bringero.ui.cart;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.graphql.UpdateCartItemMutation;
import com.corptia.bringero.type.UpdateCartItem;

import org.jetbrains.annotations.NotNull;

public class CartPresenter {

    CartContract.CartView view;

    public CartPresenter(CartContract.CartView view) {
        this.view = view;
    }

    public void getMyCart() {

        MyApolloClient.getApollowClientAuthorization().query(MyCartQuery.builder().build())
                .enqueue(new ApolloCall.Callback<MyCartQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<MyCartQuery.Data> response) {
                        MyCartQuery.MyCart myCart = response.data().CartItemQuery().myCart();
                        if (myCart.status() == 200) {
                            view.setMyCart(myCart);
                        }

                        else  if (myCart.status() == 404)
                        {
                            view.setPlaceholder();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }

    public void updateCartItems(String itemsId , int amount){

        UpdateCartItem updateAmount = UpdateCartItem.builder().amount(amount).build();
        MyApolloClient.getApollowClientAuthorization().mutate(UpdateCartItemMutation.builder().id(itemsId).data(updateAmount).build())
                .enqueue(new ApolloCall.Callback<UpdateCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateCartItemMutation.Data> response) {

                        if (response.data().CartItemMutation().update().status() == 200)
                        {

                        }
                        else
                        {

                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }
}
