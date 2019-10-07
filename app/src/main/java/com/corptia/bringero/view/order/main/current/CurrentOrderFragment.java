package com.corptia.bringero.view.order.main.current;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;
import com.corptia.bringero.model.CartItems;
import com.corptia.bringero.model.CartModel;
import com.corptia.bringero.view.order.ordersDetails.OrdersPaidDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentOrderFragment extends Fragment implements CurrentOrderView{


    CurrentOrderAdapter adapter;
    @BindView(R.id.recycler_current_order)
    RecyclerView recycler_current_order;
    Handler handler = new Handler();

    CurrentOrderPresenter currentOrderPresenter ;

    public CurrentOrderFragment() {
        // Required empty public constructor
        currentOrderPresenter = new CurrentOrderPresenter(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_order, container, false);
        ButterKnife.bind(this , view);



        recycler_current_order.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recycler_current_order.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,getActivity())));


        List<CartModel> storeTypes = new ArrayList<>();

        List<CartItems> cartItems = new ArrayList<>();
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        storeTypes.add(new CartModel("", cartItems));

        List<CartItems> cartItems2 = new ArrayList<>();

        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        storeTypes.add(new CartModel("", cartItems2));

        List<CartItems> cartItems3 = new ArrayList<>();

        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        storeTypes.add(new CartModel("", cartItems));





        return view;
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

    @Override
    public void DeliveryOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                adapter = new CurrentOrderAdapter(getActivity() ,deliveryOrderData );
                recycler_current_order.setAdapter(adapter);

                adapter.setClickListener(new IOnRecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        Intent intent = new Intent(getActivity() , OrdersPaidDetailsActivity.class);
                        String orderId = adapter.getIdOrder(position);
                        int serialOrder = adapter.getSerialOrder(position);
                        intent.putExtra(Constants.EXTRA_ORDER_ID , orderId);
                        intent.putExtra(Constants.EXTRA_ORDER_SERIAL , serialOrder);
                        startActivity(intent);

                    }
                });

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        currentOrderPresenter.getDeliveryOrder();
    }
}
