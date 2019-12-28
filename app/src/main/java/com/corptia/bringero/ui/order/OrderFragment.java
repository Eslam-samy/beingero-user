package com.corptia.bringero.ui.order;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;
import com.corptia.bringero.ui.order.main.current.CurrentOrderAdapter;
import com.corptia.bringero.ui.order.main.current.OrderPresenter;
import com.corptia.bringero.ui.order.main.current.CurrentOrderView;
import com.corptia.bringero.ui.order.ordersDetails.OrdersPaidDetailsActivity;
import com.corptia.bringero.utils.recyclerview.PaginationListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_START;


public class OrderFragment extends Fragment implements CurrentOrderView {

    private static final String TAG = "OrderFragment";
    //For Pagination
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int totalPages = 1;

    @BindView(R.id.recycler_current_orders)
    RecyclerView recycler_current_orders;
    @BindView(R.id.recycler_last_orders)
    RecyclerView recycler_last_orders;
//    @BindView(R.id.nestedScrollLastOrder)
//    NestedScrollView nestedScrollLastOrder;

    //For PlaceHolder
    @BindView(R.id.layout_placeholder_currentOrder)
    ConstraintLayout layout_placeholder_currentOrder;
    @BindView(R.id.btn_start_shopping)
    Button btn_start_shopping;
    @BindView(R.id.txt_lab_last_order)
    TextView txt_lab_last_order;
    @BindView(R.id.txt_lab_current_order)
    TextView txt_lab_current_order;

    @BindView(R.id.loading)
    LottieAnimationView loading;

    OrderPresenter presenter = new OrderPresenter(this);
    CurrentOrderAdapter adapterLastOrder, adapterCurrentOrder;

    Handler handler = new Handler();
    LinearLayoutManager linearLayoutManager;

    public OrderFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, view);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler_current_orders.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_last_orders.setLayoutManager(linearLayoutManager);

        presenter.getDeliveryOrder(currentPage);
        presenter.getCurrentOrder();


        recycler_last_orders.addOnScrollListener(new PaginationListener(linearLayoutManager) {
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

        adapterLastOrder = new CurrentOrderAdapter(getActivity(), null);
        recycler_last_orders.setAdapter(adapterLastOrder);

        btn_start_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((BottomNavigationView) getActivity()
                        .findViewById(R.id.nav_bottomNavigationView))
                        .setSelectedItemId(R.id.nav_home);


            }
        });

        return view;
    }

    @Override
    public void DeliveryOrders(DeliveryOrdersQuery.GetAll deliveryOrderData) {

        handler.post(() -> {

            if (isLoading) {
                adapterLastOrder.removeLoading();
                isLoading = false;
            }


            totalPages = deliveryOrderData.pagination().totalPages();
            adapterLastOrder.addItems(deliveryOrderData.DeliveryOrderData());

            adapterLastOrder.setClickListener((view, position) -> {

                Intent intent = new Intent(getActivity(), OrdersPaidDetailsActivity.class);
                String orderId = adapterLastOrder.getIdOrder(position);
                int serialOrder = adapterLastOrder.getSerialOrder(position);
                intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
                intent.putExtra(Constants.EXTRA_ORDER_SERIAL, serialOrder);
                startActivity(intent);

            });

            txt_lab_last_order.setVisibility(View.VISIBLE);
            recycler_last_orders.setVisibility(View.VISIBLE);

        });
    }


    @Override
    public void CurrentOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                adapterCurrentOrder = new CurrentOrderAdapter(getActivity(), deliveryOrderData);
                recycler_current_orders.setAdapter(adapterCurrentOrder);

                adapterCurrentOrder.setClickListener(new IOnRecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                        Intent intent = new Intent(getActivity(), OrdersPaidDetailsActivity.class);
                        String orderId = adapterCurrentOrder.getIdOrder(position);
                        int serialOrder = adapterCurrentOrder.getSerialOrder(position);
                        intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
                        intent.putExtra(Constants.EXTRA_ORDER_SERIAL, serialOrder);
                        intent.putExtra(Constants.EXTRA_ORDER_SERIAL, serialOrder);
                        startActivity(intent);

                    }
                });

                txt_lab_current_order.setVisibility(View.VISIBLE);
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }
}
