package com.corptia.bringero.ui.order;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;
import com.corptia.bringero.ui.order.main.current.CurrentOrderAdapter;
import com.corptia.bringero.ui.order.main.current.OrderPresenter;
import com.corptia.bringero.ui.order.main.current.CurrentOrderView;
import com.corptia.bringero.ui.order.ordersDetails.OrdersPaidDetailsActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderActivity extends BaseActivity implements CurrentOrderView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycler_current_orders)
    RecyclerView recycler_current_orders;
    @BindView(R.id.recycler_last_orders)
    RecyclerView recycler_last_orders;

    OrderPresenter presenter = new OrderPresenter(this);
    CurrentOrderAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ButterKnife.bind(this);


        recycler_last_orders.setLayoutManager(new LinearLayoutManager(this));
        //All Status Without Delevired
        fetchCurrentOrders();
        //This Delevired only
        presenter.getDeliveryOrder();

        initActionBar();

    }

    private void fetchLastOrders() {




    }

    private void fetchCurrentOrders() {



    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void DeliveryOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData) {


        runOnUiThread(() -> {

            adapter = new CurrentOrderAdapter(OrderActivity.this ,deliveryOrderData );
            recycler_last_orders.setAdapter(adapter);

            adapter.setClickListener((view, position) -> {

                Intent intent = new Intent(OrderActivity.this , OrdersPaidDetailsActivity.class);
                String orderId = adapter.getIdOrder(position);
                int serialOrder = adapter.getSerialOrder(position);
                intent.putExtra(Constants.EXTRA_ORDER_ID , orderId);
                intent.putExtra(Constants.EXTRA_ORDER_SERIAL , serialOrder);
                startActivity(intent);

            });


        });
    }

    @Override
    public void CurrentOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData) {

    }

    @Override
    public void onNotFoundCurrentOrders() {

    }

    @Override
    public void onNotFoundDeliveryOrders() {

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
