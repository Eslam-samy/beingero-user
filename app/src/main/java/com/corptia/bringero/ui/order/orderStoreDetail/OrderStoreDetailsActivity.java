package com.corptia.bringero.ui.order.orderStoreDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.SingleOrderQuery;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class OrderStoreDetailsActivity extends BaseActivity implements OrderStoreDetailsView {

    @BindView(R.id.recycler_items)
    RecyclerView recycler_items;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.txt_order_id)
    TextView txt_order_id;
    @BindView(R.id.txt_name)
    TextView txt_name;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.img_store)
    ImageView img_store;
    @BindView(R.id.shimmerLayout_loading)
    ShimmerFrameLayout shimmerLayout_loading;
    @BindView(R.id.ratingBar)
    RatingBar myRatingBar;

    @BindView(R.id.root)
    AppBarLayout root;

    String BUYING_ORDER_ID = "";

    OrderStoreDetailAdapter adapter;

    OrderStoreDetailsPresenter presenter = new OrderStoreDetailsPresenter(this);

    AlertDialog dialog;

    CustomLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_store_details);

        ButterKnife.bind(this);
        loading = new CustomLoading(this , true);

        shimmerLayout_loading.setVisibility(View.VISIBLE);

        initActionBar();


        recycler_items.setLayoutManager(new LinearLayoutManager(this));
        recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(0, this)));

        Intent intent = getIntent();

        if (intent != null) {
            BUYING_ORDER_ID = intent.getStringExtra(Constants.BUYING_ORDER_ID);
//            EXTRA_STORE_NAME = intent.getStringExtra(Constants.EXTRA_STORE_NAME);
//            EXTRA_STORE_IMAGE = intent.getStringExtra(Constants.EXTRA_STORE_IMAGE);
        }

//        txt_name.setText(EXTRA_STORE_NAME);
//        if (EXTRA_STORE_IMAGE!=null || !EXTRA_STORE_IMAGE.isEmpty())
//        PicassoUtils.setImage(Common.BASE_URL_IMAGE + EXTRA_STORE_IMAGE , img_store);


        shimmerLayout_loading.setVisibility(View.VISIBLE);

        presenter.getOrderStoreDetails(BUYING_ORDER_ID);


    }

//    private void fetchData(String BUYING_ORDER_ID) {
//
//        MyApolloClient.getApollowClientAuthorization().query(SingleOrderQuery.builder().buyingOrderId(BUYING_ORDER_ID).build())
//                .enqueue(new ApolloCall.Callback<SingleOrderQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<SingleOrderQuery.Data> response) {
//
//
//
//                        if (orderResponse.status() == 200) {
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//
//
//
//                                }
//                            });
//
//
//                        } else {
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//
//                    }
//                });
//
//    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setOrderStoreDetails(SingleOrderQuery.Get storeDetails) {

        SingleOrderQuery.Get orderResponse = storeDetails;
        SingleOrderQuery.@Nullable Data1 storeData = orderResponse.SingleOrder().StoreResponse().data();

        runOnUiThread(() -> {

            shimmerLayout_loading.setVisibility(View.GONE);
            root.setVisibility(View.VISIBLE);

            txt_name.setText(storeData.name());
            if (storeData.ImageResponse().status() == 200)
                PicassoUtils.setImage(Common.BASE_URL_IMAGE + storeData.ImageResponse().data().name(), img_store);

            txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(orderResponse.SingleOrder().TotalPrice())).append(getString(R.string.currency)));

            txt_order_id.setText(new StringBuilder().append("# ").append(orderResponse.SingleOrder().serial()));

            List<SingleOrderQuery.ItemsDatum> items = orderResponse.SingleOrder().ItemsResponse().ItemsData();
            adapter = new OrderStoreDetailAdapter(OrderStoreDetailsActivity.this, items);
            recycler_items.setAdapter(adapter);

            if (storeData.Rate().Service().TotalRate() == 0 && storeDetails.SingleOrder().status().rawValue().equalsIgnoreCase("Delivered"))
                showDialogRating(storeData);

            myRatingBar.setRating(storeData.Rate().Service().TotalRate());

        });

    }




    private void showDialogRating(SingleOrderQuery.Data1 storeData) {

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_rating, null);

        CircleImageView img_store = dialogView.findViewById(R.id.img_store);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        Button btn_submit = dialogView.findViewById(R.id.btn_submit);

        //Set Image Store
        if (storeData.ImageResponse().status() == 200)
            Picasso.get()
                    .load(Common.BASE_URL_IMAGE + storeData.ImageResponse().data().name())
                    .placeholder(R.drawable.ic_placeholder_store)
                    .into(img_store);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ratingBar.getRating() != 0f) {

                    presenter.storeServiceRating(BUYING_ORDER_ID, (int) ratingBar.getRating());

                } else {
                    Toasty.warning(OrderStoreDetailsActivity.this, "Sorry Can't ratting by 0").show();
                }

            }
        });


        builder.setCancelable(true);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);


        //finally creating the alert dialog and displaying it
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();

    }

    @Override
    public void showProgressBar() {
        loading.showProgressBar(this , false);
    }

    @Override
    public void hideProgressBar() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.hideProgressBar();
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
    public void onFailedRating() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.hideProgressBar();
            }
        });

    }

    @Override
    public void onSuccessRating(int rating) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                dialog.dismiss();
                myRatingBar.setRating(rating);

            }
        });
    }
}
