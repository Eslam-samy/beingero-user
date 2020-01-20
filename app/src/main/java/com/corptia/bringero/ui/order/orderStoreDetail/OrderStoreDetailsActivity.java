package com.corptia.bringero.ui.order.orderStoreDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.SingleOrderQuery;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderStoreDetailsActivity extends BaseActivity {

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


    String BUYING_ORDER_ID = "" ,EXTRA_STORE_NAME ,EXTRA_STORE_IMAGE;

    OrderStoreDetailAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_store_details);

        ButterKnife.bind(this);

        initActionBar();


        recycler_items.setLayoutManager(new LinearLayoutManager(this));
        recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(0 , this)));

        Intent intent = getIntent();

        if (intent != null) {
            BUYING_ORDER_ID = intent.getStringExtra(Constants.BUYING_ORDER_ID);
            EXTRA_STORE_NAME = intent.getStringExtra(Constants.EXTRA_STORE_NAME);
            EXTRA_STORE_IMAGE = intent.getStringExtra(Constants.EXTRA_STORE_IMAGE);
        }

        txt_name.setText(EXTRA_STORE_NAME);
        if (EXTRA_STORE_IMAGE!=null || !EXTRA_STORE_IMAGE.isEmpty())
        PicassoUtils.setImage(Common.BASE_URL_IMAGE + EXTRA_STORE_IMAGE , img_store);

        fetchData(BUYING_ORDER_ID);

    }

    private void fetchData(String BUYING_ORDER_ID) {

        MyApolloClient.getApollowClientAuthorization().query(SingleOrderQuery.builder().buyingOrderId(BUYING_ORDER_ID).build())
                .enqueue(new ApolloCall.Callback<SingleOrderQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SingleOrderQuery.Data> response) {

                        SingleOrderQuery.Get orderResponse = response.data().BuyingOrderQuery().get();
                        if (orderResponse.status() == 200) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(orderResponse.SingleOrder().TotalPrice())).append(getString(R.string.currency)));
                                    txt_order_id.setText(new StringBuilder().append("# ").append(orderResponse.SingleOrder().serial()));

                                    List<SingleOrderQuery.ItemsDatum> items = orderResponse.SingleOrder().ItemsResponse().ItemsData();
                                    adapter = new OrderStoreDetailAdapter(OrderStoreDetailsActivity.this, items);
                                    recycler_items.setAdapter(adapter);

                                }
                            });


                        } else {

                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
