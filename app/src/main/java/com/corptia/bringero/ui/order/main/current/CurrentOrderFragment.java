package com.corptia.bringero.ui.order.main.current;


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
import com.corptia.bringero.graphql.DeliveryOrdersQuery;
import com.corptia.bringero.ui.order.ordersDetails.OrdersPaidDetailsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentOrderFragment extends Fragment implements CurrentOrderView{


    int sumCall =0;

    CurrentOrderAdapter adapter;
    @BindView(R.id.recycler_current_order)
    RecyclerView recycler_current_order;
    Handler handler = new Handler();

    OrderPresenter orderPresenter;

    public CurrentOrderFragment() {
        // Required empty public constructor
        orderPresenter = new OrderPresenter(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_order, container, false);
        ButterKnife.bind(this , view);



        recycler_current_order.setLayoutManager(new LinearLayoutManager(getActivity()));
        //recycler_current_order.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,getActivity())));


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
    public void DeliveryOrders(DeliveryOrdersQuery.GetAll deliveryOrderData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                sumCall++;

//                adapter = new CurrentOrderAdapter(getActivity() ,deliveryOrderData );
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
    public void CurrentOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                sumCall++;

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

                if (sumCall == 2){

                }

            }
        });

    }

    @Override
    public void onNotFoundCurrentOrders() {
        sumCall++;
    }

    @Override
    public void onNotFoundDeliveryOrders() {
        sumCall++;
    }

    @Override
    public void onStart() {
        super.onStart();
//        orderPresenter.getDeliveryOrder();
    }
}
