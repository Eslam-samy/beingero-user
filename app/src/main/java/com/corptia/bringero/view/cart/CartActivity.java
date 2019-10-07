package com.corptia.bringero.view.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.model.CartModel;
import com.corptia.bringero.view.cart.Adapter.CartAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements CartContract.CartView {

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    CartAdapter cartAdapter ;
    List<CartModel> cartItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ButterKnife.bind(this);
        recycler_cart.setHasFixedSize(true);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    public void setMyCart(List<MyCartQuery.StoreDatum> myCartData) {
        runOnUiThread(() -> {
            cartAdapter = new CartAdapter(CartActivity.this , myCartData , true);
            recycler_cart.setAdapter(cartAdapter);
            recycler_cart.setLayoutManager(new LinearLayoutManager(CartActivity.this));
            recycler_cart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,CartActivity.this)));


        });

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

 /*   private void setData(NewCartAdapter adapter) {
        HeaderDataImpl headerData1 = new HeaderDataImpl(HeaderDataImpl.HEADER_TYPE_1, R.layout.item_card_cart_header);

        List<MyData> items = new ArrayList<>();

        List<CartItems> cartItems1 = new ArrayList<>();
        cartItems1.add(new CartItems("Product 1"));
        cartItems1.add(new CartItems("Product 2"));
        cartItems1.add(new CartItems("Product 3"));
        items.add(new MyData("Header 1 " , cartItems1));
        adapter.setHeaderAndData(items, headerData1);

        items = new ArrayList<>();
        List<CartItems> cartItems2 = new ArrayList<>();
        cartItems2.add(new CartItems("Product 4"));
        cartItems2.add(new CartItems("Product 5"));
        cartItems2.add(new CartItems("Product 6"));
        items.add(new MyData("Header 1 " , cartItems1));
        adapter.setHeaderAndData(items, headerData1);



    }*/

}
