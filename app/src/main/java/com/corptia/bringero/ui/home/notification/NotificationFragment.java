package com.corptia.bringero.ui.home.notification;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.airbnb.lottie.LottieAnimationView;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.NotificationQuery;
import com.corptia.bringero.ui.order.orderStoreDetail.OrderStoreDetailsActivity;
import com.corptia.bringero.ui.order.ordersDetails.OrdersPaidDetailsActivity;
import com.corptia.bringero.utils.recyclerview.PaginationListener;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_START;


public class NotificationFragment extends Fragment implements NotificationView {


    @BindView(R.id.recycler_notification)
    RecyclerView recycler_notification;
    @BindView(R.id.loading)
    LottieAnimationView loading;

    NotificationAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    Handler handler = new Handler();

    //For Pagination
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int totalPages = 1;

    //For Placeholder
    @BindView(R.id.layout_placeholder)
    ConstraintLayout layout_placeholder;
    @BindView(R.id.img_placeholder)
    ImageView img_placeholder;
    @BindView(R.id.txt_placeholder_title)
    TextView txt_placeholder_title;
    @BindView(R.id.txt_placeholder_dec)
    TextView txt_placeholder_dec;
    @BindView(R.id.btn_1)
    Button btn_1;
    @BindView(R.id.btn_2)
    Button btn_2;

    NotificationPresenter presenter = new NotificationPresenter(this);

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.bind(this, view);


        linearLayoutManager = new LinearLayoutManager(getActivity());
        recycler_notification.setLayoutManager(linearLayoutManager);
        recycler_notification.addItemDecoration(new LinearSpacingItemDecoration(15));
//        recycler_notification.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, getActivity())));

        adapter = new NotificationAdapter(getActivity(), null);
        recycler_notification.setAdapter(adapter);

        presenter.getNotification(currentPage);


        recycler_notification.addOnScrollListener(new PaginationListener(linearLayoutManager) {
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

        return view;
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
                        intent = new Intent(getActivity(), OrderStoreDetailsActivity.class);
                        intent.putExtra(Constants.BUYING_ORDER_ID, notification.docId());
                    } else if (notification.model().equalsIgnoreCase("DeliveryOrder")) {
                        intent = new Intent(getActivity(), OrdersPaidDetailsActivity.class);
                        intent.putExtra(Constants.EXTRA_ORDER_ID, notification.docId());
                    }
                    if (intent != null)
                        startActivity(intent);
                }
            });
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

    @Override
    public void onNotFoundData() {

        handler.post(() -> {
            recycler_notification.setVisibility(View.GONE);

            layout_placeholder.setVisibility(View.VISIBLE);
            btn_2.setVisibility(View.GONE);
            btn_1.setVisibility(View.GONE);

            img_placeholder.setImageResource(R.drawable.ic_placeholder_notification);
            txt_placeholder_title.setText(getString(R.string.placeholder_title_notification));
            txt_placeholder_dec.setText(getString(R.string.placeholder_dec_notification));


        });

    }

    @Override
    public void onErrorConnection() {

    }
}
