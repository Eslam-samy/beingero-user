package com.corptia.bringero.view.productDetail;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.CreateCartItemMutation;
import com.corptia.bringero.graphql.SingleProductQuery;
import com.corptia.bringero.type.CreateCartItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProductDetailPresenter {

    private ProductDetailContract.ProductDetailView view ;

    public ProductDetailPresenter(ProductDetailContract.ProductDetailView view) {
        this.view = view;
    }


    public void getSingleProduct(String _id){

        view.showProgressBar();

        MyApolloClient.getApollowClientAuthorization().query(SingleProductQuery.builder()._id(_id).build())
                .enqueue(new ApolloCall.Callback<SingleProductQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SingleProductQuery.Data> response) {

                        SingleProductQuery.GetSingleProduct singleProduct = response.data().PricingProductQuery().GetSingleProduct();

                        if (singleProduct.status() ==  200)
                        {
                            view.setDataProduct(singleProduct.Product());

                        }
                        else
                        {
                            view.showErrorMessage(singleProduct.message());
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        view.showErrorMessage(e.getMessage());
                        view.hideProgressBar();
                    }
                });

    }

    public void addToCart (String pricingProductId){

        CreateCartItem item = CreateCartItem.builder().amount(1).pricingProductId(pricingProductId).build();
        MyApolloClient.getApollowClientAuthorization().mutate(CreateCartItemMutation.builder().data(item).build())
                .enqueue(new ApolloCall.Callback<CreateCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<CreateCartItemMutation.Data> response) {
                        view.hideProgressBar();

                        CreateCartItemMutation.Create createResponse = response.data().CartItemMutation().create();
                        if (createResponse.status() == 200)
                        {
                            view.showErrorMessage("Done");
                        }
                        else
                        {
                            view.showErrorMessage(createResponse.message());
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        view.showErrorMessage( e.getMessage());

                    }
                });
    }

}
