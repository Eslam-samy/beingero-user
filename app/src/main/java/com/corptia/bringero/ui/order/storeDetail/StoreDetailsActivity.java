package com.corptia.bringero.ui.order.storeDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.SingleOrderQuery;
import com.corptia.bringero.ui.storesDetail.StoreDetailActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreDetailsActivity extends AppCompatActivity {

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


    String BUYING_ORDER_ID = "";

    StoreDetailAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);

        ButterKnife.bind(this);

        initActionBar();


        recycler_items.setLayoutManager(new LinearLayoutManager(this));


        if (getIntent() != null) {
            BUYING_ORDER_ID = getIntent().getStringExtra(Constants.BUYING_ORDER_ID);
        }

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

                                    txt_total_price.setText(new StringBuilder().append(orderResponse.SingleOrder().TotalPrice()).append(getString(R.string.currency)));
                                    txt_order_id.setText(new StringBuilder().append("# ").append(orderResponse.SingleOrder().serial()));

                                    List<SingleOrderQuery.ItemsDatum> items = orderResponse.SingleOrder().ItemsResponse().ItemsData();
                                    adapter = new StoreDetailAdapter(StoreDetailsActivity.this, items);
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
}
