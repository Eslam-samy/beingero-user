package com.corptia.bringero.ui.order;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;
import com.corptia.bringero.ui.order.main.current.CurrentOrderAdapter;
import com.corptia.bringero.ui.order.main.current.OrderPresenter;
import com.corptia.bringero.ui.order.main.current.CurrentOrderView;
import com.corptia.bringero.ui.order.ordersDetails.OrdersPaidDetailsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OrderFragment extends Fragment implements CurrentOrderView {

    @BindView(R.id.recycler_current_orders)
    RecyclerView recycler_current_orders;
    @BindView(R.id.recycler_last_orders)
    RecyclerView recycler_last_orders;

    //For PlaceHolder
    @BindView(R.id.layout_placeholder_currentOrder)
    ConstraintLayout layout_placeholder_currentOrder;
    @BindView(R.id.btn_start_shopping)
    Button btn_start_shopping;

    OrderPresenter presenter = new OrderPresenter(this);
    CurrentOrderAdapter adapter ;

    Handler handler = new Handler();

    public  OrderFragment () {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this , view);

        recycler_current_orders.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_last_orders.setLayoutManager(new LinearLayoutManager(getActivity()));
        presenter.getDeliveryOrder();
        presenter.getCurrentOrder();

        return view;
    }

    @Override
    public void DeliveryOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                adapter = new CurrentOrderAdapter(getActivity(),deliveryOrderData );
                recycler_last_orders.setAdapter(adapter);

                adapter.setClickListener((view, position) -> {

                    Intent intent = new Intent(getActivity() , OrdersPaidDetailsActivity.class);
                    String orderId = adapter.getIdOrder(position);
                    int serialOrder = adapter.getSerialOrder(position);
                    intent.putExtra(Constants.EXTRA_ORDER_ID , orderId);
                    intent.putExtra(Constants.EXTRA_ORDER_SERIAL , serialOrder);
                    startActivity(intent);

                });

            }
        });
    }


    @Override
    public void CurrentOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                adapter = new CurrentOrderAdapter(getActivity() ,deliveryOrderData );
                recycler_current_orders.setAdapter(adapter);

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
    public void onNotFoundCurrentOrders() {

        handler.post(() -> {

                recycler_current_orders.setVisibility(View.GONE);
                layout_placeholder_currentOrder.setVisibility(View.VISIBLE);

        });
    }

    @Override
    public void onNotFoundDeliveryOrders() {

        handler.post(() -> {

//            recycler_current_orders.setVisibility(View.GONE);
//            layout_placeholder_currentOrder.setVisibility(View.VISIBLE);

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
}
