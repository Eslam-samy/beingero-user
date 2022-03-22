package com.corptia.bringero.ui.home.order;

import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_START;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.databinding.ActivityMyOrdersBinding;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;
import com.corptia.bringero.ui.order.ordersDetails.OrdersPaidDetailsActivity;
import com.corptia.bringero.utils.recyclerview.PaginationListener;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MyOrdersActivity extends BaseActivity implements OrderView {
    ActivityMyOrdersBinding binding;
    OrderPresenter presenter = new OrderPresenter(this);
    OrderAdapter adapterLastOrder, adapterCurrentOrder;

    Handler handler = new Handler();
    LinearLayoutManager linearLayoutManager;

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_orders);
        super.onCreate(savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerCurrentOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerLastOrders.setLayoutManager(linearLayoutManager);
        binding.recyclerLastOrders.addItemDecoration(new LinearSpacingItemDecoration(15));
        binding.recyclerCurrentOrders.addItemDecoration(new LinearSpacingItemDecoration(15));
        presenter.getDeliveryOrder(currentPage);
        presenter.getCurrentOrder();


        binding.recyclerLastOrders.addOnScrollListener(new PaginationListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {

                isLoading = true;
                currentPage++;
                if (currentPage <= totalPages) {
                    adapterLastOrder.addLoading();
                    presenter.getDeliveryOrder(currentPage);
                } else {
                    isLastPage = true;
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

//        nestedScrollLastOrder.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//
//            if (scrollY > oldScrollY) {
//                Log.i(TAG, "Scroll DOWN");
//            }
//            if (scrollY < oldScrollY) {
//                Log.i(TAG, "Scroll UP");
//            }
//
//            if (scrollY == 0) {
//                Log.i(TAG, "TOP SCROLL");
//            }
//
//            if (scrollY == ( v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() )) {
//                Log.i(TAG, "BOTTOM SCROLL");
//                // here where the trick is going
//
//                if (!isLastPage) {
//                    isLoading = false;
//                    currentPage++;
//                    if (currentPage <= totalPages) {
//                        adapterLastOrder.addLoading();
//                        presenter.getDeliveryOrder(currentPage);
//                    } else {
//                        isLastPage = true;
//                    }
//                }
//
//
//
//            }
//        });

        adapterLastOrder = new OrderAdapter(this, null);
        binding.recyclerLastOrders.setAdapter(adapterLastOrder);

//        binding.layoutPlaceholderCurrentOrder.setOnClickListener(view1 -> ((BottomNavigationView) getActivity()
//                .findViewById(R.id.nav_bottomNavigationView))
//                .setSelectedItemId(R.id.nav_home));
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {
        handler.post(() -> binding.loading.setVisibility(View.GONE));

    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Override
    public void DeliveryOrders(DeliveryOrdersQuery.GetAll deliveryOrderData) {
        handler.post(() -> {
            if (isLoading) {
                adapterLastOrder.removeLoading();
                isLoading = false;
            }
            Log.d("ESLAM", "DeliveryOrders: "+ deliveryOrderData.DeliveryOrderData().size());
            totalPages = deliveryOrderData.pagination().totalPages();
            adapterLastOrder.addItems(deliveryOrderData.DeliveryOrderData());
            adapterLastOrder.setClickListener((view, position) -> {
                Intent intent = new Intent(this, OrdersPaidDetailsActivity.class);
                String orderId = adapterLastOrder.getIdOrder(position);
                int serialOrder = adapterLastOrder.getSerialOrder(position);
                intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
                intent.putExtra(Constants.EXTRA_ORDER_SERIAL, serialOrder);
                startActivity(intent);
            });
            binding.txtLabLastOrder.setVisibility(View.VISIBLE);
            binding.txtLabLastOrder.setVisibility(View.VISIBLE);

        });
    }

    @Override
    public void CurrentOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData) {
        handler.post(() -> {
            adapterCurrentOrder = new OrderAdapter(this, deliveryOrderData);
            binding.recyclerCurrentOrders.setAdapter(adapterCurrentOrder);
            adapterCurrentOrder.setClickListener((view, position) -> {
                Intent intent = new Intent(MyOrdersActivity.this, OrdersPaidDetailsActivity.class);
                String orderId = adapterCurrentOrder.getIdOrder(position);
                int serialOrder = adapterCurrentOrder.getSerialOrder(position);
                intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
                intent.putExtra(Constants.EXTRA_ORDER_SERIAL, serialOrder);
                intent.putExtra(Constants.EXTRA_ORDER_SERIAL, serialOrder);
                startActivity(intent);
            });
            binding.txtLabCurrentOrder.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void onNotFoundCurrentOrders() {
        handler.post(() -> {
            binding.recyclerCurrentOrders.setVisibility(View.GONE);
            binding.layoutPlaceholderCurrentOrder.setVisibility(View.VISIBLE);

        });
    }

    @Override
    public void onNotFoundDeliveryOrders() {

    }
}