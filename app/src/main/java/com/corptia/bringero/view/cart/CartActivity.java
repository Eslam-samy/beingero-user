package com.corptia.bringero.view.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.corptia.bringero.Adapter.HeaderDataImpl;
import com.corptia.bringero.Adapter.MyData;
import com.corptia.bringero.Adapter.NewCartAdapter;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.Utils.stickyheader.stickyView.StickHeaderItemDecoration;
import com.corptia.bringero.model.CartItems;
import com.corptia.bringero.model.CartModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity {

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

        List<CartItems> brand1 = new ArrayList<>();
        brand1.add(new CartItems("Product 11"));
        brand1.add(new CartItems("Product 12"));
        brand1.add(new CartItems("Product 4"));
        brand1.add(new CartItems("Product 4"));
        brand1.add(new CartItems("Product 3"));
        brand1.add(new CartItems("Product 2"));

        List<CartItems> brand2 = new ArrayList<>();
        brand2.add(new CartItems("Product 11"));
        brand2.add(new CartItems("Product 12"));
        brand2.add(new CartItems("Product 4"));
        brand2.add(new CartItems("Product 4"));
        brand2.add(new CartItems("Product 3"));
        brand2.add(new CartItems("Product 2"));

        List<CartItems> brand3 = new ArrayList<>();
        brand3.add(new CartItems("Product 11"));
        brand3.add(new CartItems("Product 12"));
        brand3.add(new CartItems("Product 4"));
        brand3.add(new CartItems("Product 4"));
        brand3.add(new CartItems("Product 3"));
        brand3.add(new CartItems("Product 2"));

        cartItemsList.add(new CartModel("Hazem" , brand1));
        cartItemsList.add(new CartModel("Hazem" , brand2));
        cartItemsList.add(new CartModel("Hazem" , brand3));

        cartAdapter = new CartAdapter(this , cartItemsList);

        recycler_cart.setAdapter(cartAdapter);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,this)));
        //Set Visable android:visibility="visible"


//        NewCartAdapter adapter = new NewCartAdapter();
//        setData(adapter);
//
//        recycler_cart.setAdapter(adapter);
//        //recycler_cart.setLayoutManager(layoutManager);
//        recycler_cart.addItemDecoration(new StickHeaderItemDecoration(adapter , Common.dpToPx(15,this)));
    }


    private void setData(NewCartAdapter adapter) {
        HeaderDataImpl headerData1 = new HeaderDataImpl(HeaderDataImpl.HEADER_TYPE_1, R.layout.item_card_cart_header);

        List<MyData> items = new ArrayList<>();
        items.add(new MyData("sasd",new ArrayList<>()));


        adapter.setHeaderAndData(items, headerData1);

        items =new ArrayList<>();
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        adapter.setHeaderAndData(items, headerData1);

        items =new ArrayList<>();
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        adapter.setHeaderAndData(items, headerData1);


        items =new ArrayList<>();
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        adapter.setHeaderAndData(items, headerData1);


        items =new ArrayList<>();
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        adapter.setHeaderAndData(items, headerData1);


        items =new ArrayList<>();
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        adapter.setHeaderAndData(items, headerData1);


        items =new ArrayList<>();
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        items.add(new MyData("sasd",new ArrayList<>()));
        adapter.setHeaderAndData(items, headerData1);

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
