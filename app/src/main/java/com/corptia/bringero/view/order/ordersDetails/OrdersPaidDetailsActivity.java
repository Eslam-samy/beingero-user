package com.corptia.bringero.view.order.ordersDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.corptia.bringero.R;
import com.corptia.bringero.model.CartItems;
import com.corptia.bringero.model.CartModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersPaidDetailsActivity extends AppCompatActivity {

    @BindView(R.id.recycler_order)
    RecyclerView recycler_order;
    OrdersPaidDetailsAdapter adapter ;

    List<CartModel> cartModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_paid_details);

        ButterKnife.bind(this);

        List<CartItems> brand1 = new ArrayList<>();
        brand1.add(new CartItems("Product 11"));
        brand1.add(new CartItems("Product 11"));
        brand1.add(new CartItems("Product 11"));
        brand1.add(new CartItems("Product 11"));


        List<CartItems> brand2 = new ArrayList<>();
        brand2.add(new CartItems("Product 11"));
        brand2.add(new CartItems("Product 11"));
        brand2.add(new CartItems("Product 11"));
        brand2.add(new CartItems("Product 11"));


        List<CartItems> brand3 = new ArrayList<>();
        brand3.add(new CartItems("Product 11"));
        brand3.add(new CartItems("Product 11"));
        brand3.add(new CartItems("Product 11"));
        brand3.add(new CartItems("Product 11"));
        brand3.add(new CartItems("Product 11"));
        brand3.add(new CartItems("Product 11"));
        brand3.add(new CartItems("Product 11"));


        cartModelList.add(new CartModel("Hazem" , brand1));
        cartModelList.add(new CartModel("Hazem" , brand2));
        cartModelList.add(new CartModel("Hazem" , brand3));


        recycler_order.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrdersPaidDetailsAdapter(this , cartModelList);
        recycler_order.setAdapter(adapter);

    }
}
