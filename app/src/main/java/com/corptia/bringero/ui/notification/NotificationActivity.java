package com.corptia.bringero.ui.notification;

import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_START;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.databinding.ActivityNotificationBinding;
import com.corptia.bringero.graphql.NotificationQuery;
import com.corptia.bringero.ui.home.notification.NotificationPresenter;
import com.corptia.bringero.ui.home.notification.NotificationView;
import com.corptia.bringero.ui.order.orderStoreDetail.OrderStoreDetailsActivity;
import com.corptia.bringero.ui.order.ordersDetails.OrdersPaidDetailsActivity;
import com.corptia.bringero.utils.recyclerview.PaginationListener;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends BaseActivity implements NotificationView {


    com.corptia.bringero.ui.home.notification.NotificationAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    ActivityNotificationBinding binding;
    Handler handler = new Handler();

    //For Pagination
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int totalPages = 1;

    NotificationPresenter presenter = new NotificationPresenter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);

        linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerNotification.setLayoutManager(linearLayoutManager);
        binding.recyclerNotification.addItemDecoration(new LinearSpacingItemDecoration(15));
//        recycler_notification.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, getActivity())));

        adapter = new com.corptia.bringero.ui.home.notification.NotificationAdapter(this, null);
        binding.recyclerNotification.setAdapter(adapter);

        presenter.getNotification(currentPage);


        binding.recyclerNotification.addOnScrollListener(new PaginationListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                if (currentPage <= totalPages) {
                    adapter.addLoading();
                    presenter.getNotification(currentPage);
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

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                binding.loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Override
    public void setNotification(NotificationQuery.GetAll notificationList) {

        handler.post(() -> {

            if (isLoading) {
                adapter.removeLoading();
                isLoading = false;
            }

            totalPages = notificationList.pagination().totalPages();
            adapter.addItems(notificationList.NotificationData());

            adapter.setClickListener(new IOnRecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    NotificationQuery.NotificationDatum notification = adapter.getItem(position);
                    Intent intent = null;
//                    Common.LOG("status : " + notification.status().rawValue());
//                    Common.LOG("docStatus : " + notification.docStatus());
//                    Common.LOG("model : " + notification.model());
//                    Common.LOG("_id : " + notification._id());
//                    Common.LOG("docId : " + notification.docId());
//                    Common.LOG("userId : " + notification.userId());

                    if (notification.model().equalsIgnoreCase("BuyingOrder")) {
                        intent = new Intent(NotificationActivity.this, OrderStoreDetailsActivity.class);
                        intent.putExtra(Constants.BUYING_ORDER_ID, notification.docId());
                    } else if (notification.model().equalsIgnoreCase("DeliveryOrder")) {
                        intent = new Intent(NotificationActivity.this, OrdersPaidDetailsActivity.class);
                        intent.putExtra(Constants.EXTRA_ORDER_ID, notification.docId());
                    }
                    if (intent != null)
                        startActivity(intent);
                }
            });
        });


    }

    @Override
    public void onNotFoundData() {
        handler.post(() -> {
            binding.recyclerNotification.setVisibility(View.GONE);

            binding.placeHolder.getRoot().setVisibility(View.VISIBLE);
            binding.placeHolder.btn2.setVisibility(View.GONE);
            binding.placeHolder.btn1.setVisibility(View.GONE);

            binding.placeHolder.imgPlaceholder.setImageResource(R.drawable.ic_placeholder_notification);
            binding.placeHolder.txtPlaceholderTitle.setText(getString(R.string.placeholder_title_notification));
            binding.placeHolder.txtPlaceholderDec.setText(getString(R.string.placeholder_dec_notification));


        });
    }

    @Override
    public void onErrorConnection() {

    }
}
