package com.corptia.bringero.ui.storesDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.ui.search.SearchProductsActivity;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.GetNotPricedByQuery;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;
import com.corptia.bringero.type.StoreFilterInput;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreDetailActivity extends BaseActivity implements StoreDetailContract.StoreDetailView {

    @BindView(R.id.toolBar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.image_store)
    ImageView image_store;
    @BindView(R.id.txt_status)
    TextView txt_status;
    @BindView(R.id.txt_name_store)
    TextView txt_name_store;

    String adminUserId = "";
    String storeId = "";
    String storeName = "";
    String storeImage = "";
    String imageUrl = "";
    //Presenter
    StoreDetailPresenter presenter;

    //Search
    public static final int EXTRA_REVEAL_CENTER_PADDING = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        ButterKnife.bind(this);

        if (Common.CURRENT_USER.language().equalsIgnoreCase("ar")) {
            viewPager.setRotationY(180);
        }

        Intent intent = getIntent();
        adminUserId = intent.getStringExtra(Constants.EXTRA_ADMIN_USER_ID);
        storeId = intent.getStringExtra(Constants.EXTRA_STORE_ID);
        storeName = intent.getStringExtra(Constants.EXTRA_STORE_NAME);
        storeImage = intent.getStringExtra(Constants.EXTRA_STORE_IMAGE);

        imageUrl = Common.BASE_URL_IMAGE + storeImage;
        if (!storeImage.equalsIgnoreCase("null"))
            PicassoUtils.setImage(imageUrl, image_store);


        txt_name_store.setText(storeName);

        initActionBar();
        initIntent();

        presenter = new StoreDetailPresenter(this);
        //presenter.getStoreDetailHeader(storeId);

    }

    private void initIntent() {

        StoreFilterInput storeFilterInput = StoreFilterInput.builder().adminUserId(adminUserId).build();
        MyApolloClient.getApollowClientAuthorization(Common.CURRENT_USER_TOKEN).query(SingleStoreQuery.builder().filter(storeFilterInput).build())
                .enqueue(new ApolloCall.Callback<SingleStoreQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SingleStoreQuery.Data> response) {

                        SingleStoreQuery.GetAll responseData = response.data().StoreQuery().getAll();

                        runOnUiThread(() -> {

                            if (responseData.status() == 200) {
                                Common.CURRENT_STORE = responseData.CurrentStore().get(0);


                                ViewPagerStoreAdapter adapter = new ViewPagerStoreAdapter(
                                        getSupportFragmentManager(),
                                        Common.CURRENT_STORE.ProductTypesStore().data(),
                                        true);

                                viewPager.setAdapter(adapter);
                                tabLayout.setupWithViewPager(viewPager);
                                viewPager.setCurrentItem(0, true);
                                adapter.notifyDataSetChanged();

                            } else {


                            }

                        });


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
    public void setStoresDetailHeader(SingleStoreHeaderQuery.StoreDetail detail) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {


            }
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

    @Override
    public void setProduct(List<GetStoreProductsQuery.Product> product) {

    }

    @Override
    public void setProductNotPriced(List<GetNotPricedByQuery.Product> product) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_search:
                Intent intent = new Intent(StoreDetailActivity.this, SearchProductsActivity.class);
                intent.putExtra(Constants.EXTRA_STORE_ID, storeId);
                intent.putExtra(Constants.EXTRA_STORE_IMAGE, imageUrl);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
