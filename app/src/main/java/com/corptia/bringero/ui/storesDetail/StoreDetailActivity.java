package com.corptia.bringero.ui.storesDetail;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.ProductsTypeQuery;
import com.corptia.bringero.graphql.SpeedCartQuery;
import com.corptia.bringero.model.EventBus.CalculateCartEvent;
import com.corptia.bringero.type.SortDirectionEnum;
import com.corptia.bringero.type.StoreSortByEnum;
import com.corptia.bringero.type.StoreSortingInput;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.ui.search.SearchProductsActivity;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.GetNotPricedByQuery;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;
import com.corptia.bringero.type.StoreFilterInput;
import com.duolingo.open.rtlviewpager.RtlViewPager;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreDetailActivity extends BaseActivity implements StoreDetailContract.StoreDetailView {

    @BindView(R.id.toolBar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    RtlViewPager viewPager;

    @BindView(R.id.image_store)
    ImageView image_store;
    @BindView(R.id.txt_status)
    TextView txt_status;
    @BindView(R.id.txt_name_store)
    TextView txt_name_store;
    @BindView(R.id.btn_search)
    ImageView btn_search;

    String adminUserId = "";
    String storeId = "";
    String storeName = "";
    String storeImage = "";
    String imageUrl = "";
    //Presenter
    StoreDetailPresenter presenter;

    //Speed Cart
    double totalPriceCart;
    int countOfCart;
    boolean isHaveCart = false;
    @BindView(R.id.txt_totalPriceCart)
    TextView txt_totalPriceCart;
    @BindView(R.id.btn_view_cart)
    TextView btn_view_cart;
    @BindView(R.id.layout_speed_cart)
    ConstraintLayout layout_speed_cart;

    int actionBarHeight;
    //Search
    public static final int EXTRA_REVEAL_CENTER_PADDING = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        ButterKnife.bind(this);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

//        if (Common.CURRENT_USER.getLanguage().equalsIgnoreCase("ar")) {
//            viewPager.setRotationY(180);
//        }

        Intent intent = getIntent();
        if (intent != null) {
            adminUserId = intent.getStringExtra(Constants.EXTRA_ADMIN_USER_ID);
            storeId = intent.getStringExtra(Constants.EXTRA_STORE_ID);
            storeName = intent.getStringExtra(Constants.EXTRA_STORE_NAME);
            storeImage = intent.getStringExtra(Constants.EXTRA_STORE_IMAGE);
        }

        imageUrl = Common.BASE_URL_IMAGE + storeImage;
        if (!storeImage.equalsIgnoreCase("null"))
            PicassoUtils.setImage(imageUrl, image_store);


        txt_name_store.setText(storeName);

        initActionBar();

        presenter = new StoreDetailPresenter(this);
        //presenter.getStoreDetailHeader(storeId);

        //Here Fetch Data From Cart I have Display layout cart
        //TODO


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EventBus.getDefault().postSticky(new CalculateCartEvent(false, 0, 0));

                Intent intent = new Intent(StoreDetailActivity.this, SearchProductsActivity.class);
                intent.putExtra(Constants.EXTRA_STORE_ID, storeId);
                intent.putExtra(Constants.EXTRA_STORE_IMAGE, imageUrl);
                startActivity(intent);

            }
        });
    }

    private void getSingleStore() {

        StoreFilterInput storeFilterInput = StoreFilterInput.builder().adminUserId(adminUserId).build();

        MyApolloClient.getApollowClientAuthorization().query(SingleStoreQuery.builder().filter(storeFilterInput).build())
                .enqueue(new ApolloCall.Callback<SingleStoreQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SingleStoreQuery.Data> response) {

                        SingleStoreQuery.GetAll responseData = response.data().StoreQuery().getAll();

                        runOnUiThread(() -> {
                            if (responseData.status() == 200) {
                                Common.CURRENT_STORE = responseData.CurrentStore().get(0);

                                List<String> typeIds = new ArrayList<>();
                                for (int i = 0; i< Common.CURRENT_STORE.ProductTypesStore().data().size() ; i++) {
                                typeIds.add(Common.CURRENT_STORE.ProductTypesStore().data().get(i)._id());
                            }



                            MyApolloClient.getApollowClientAuthorization().query(ProductsTypeQuery.builder().typesIds(typeIds).build())
                                    .enqueue(new ApolloCall.Callback<ProductsTypeQuery.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<ProductsTypeQuery.Data> response) {

                                            ProductsTypeQuery.GetAll productsResponse = response.data().ProductTypeQuery().getAll();
                                            runOnUiThread(() -> {

                                                if (productsResponse.status() == 200){
                                                    Common.CURRENT_PRODUCTS_TYPE = productsResponse.data();
                                                    List<ProductsTypeQuery.Data1> typesList;
                                                    typesList = new ArrayList<>();
                                                    typesList.add(new ProductsTypeQuery.Data1("Offres","0",getString(R.string.offers), null));
//                                for ()
                                                    typesList.addAll(productsResponse.data());

                                                    ViewPagerStoreAdapter adapter = new ViewPagerStoreAdapter(
                                                            getSupportFragmentManager(),
                                                            typesList);

                                                    viewPager.setAdapter(adapter);
                                                    tabLayout.setupWithViewPager(viewPager);
                                                    viewPager.setCurrentItem(0, true);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(@NotNull ApolloException e) {

                                        }
                                    });

//                                adapter.notifyDataSetChanged();

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
    public void setProduct(GetStoreProductsQuery.GetStoreProducts product) {

    }

    @Override
    public void setPlaceholder() {

    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        return true;
//    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

//            case R.id.action_search:
//                Intent intent = new Intent(StoreDetailActivity.this, SearchProductsActivity.class);
//                intent.putExtra(Constants.EXTRA_STORE_ID, storeId);
//                intent.putExtra(Constants.EXTRA_STORE_IMAGE, imageUrl);
//                startActivity(intent);
//                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSpeedCart();
        getSingleStore();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().postSticky(new CalculateCartEvent(false, 0, 0));
        EventBus.getDefault().unregister(this);
    }

    private void RunAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.scale_amount_price);
        a.reset();
//        txt_countOfCart.clearAnimation();
//        txt_countOfCart.startAnimation(a);
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void request(CalculateCartEvent event) {
        if (event != null) {
            if (event.isSuccess()) {
                countOfCart = Common.TOTAL_CART_AMOUNT;
                totalPriceCart += event.getProductPrice();
                isHaveCart = true;
                layout_speed_cart.setVisibility(View.VISIBLE);
                txt_totalPriceCart.setText(new StringBuilder().append(Common.getDecimalNumber(totalPriceCart)).append(" ").append(getString(R.string.currency)));
                btn_view_cart.setText(new StringBuilder().append(getString(R.string.view_cart)));
                Common.totalPriceCart=totalPriceCart;
                if (totalPriceCart <= 0.0f) {
                    isHaveCart = false;
                    layout_speed_cart.setVisibility(View.GONE);
                }
            } else {
                if (Common.TOTAL_CART_AMOUNT == 0) {
                    isHaveCart = false;
                    layout_speed_cart.setVisibility(View.GONE);
                }
            }

            if (isHaveCart) {
                viewPager.setPadding(0, 0, 0, actionBarHeight);
            } else {
                viewPager.setPadding(0, 0, 0, 0);
            }

        }


    }

    public void getSpeedCart() {


        if (Common.TOTAL_CART_AMOUNT <= 0) {
            Common.TOTAL_CART_AMOUNT = 0;
            isHaveCart = false;
            layout_speed_cart.setVisibility(View.GONE);
        } else {
            isHaveCart = true;
            layout_speed_cart.setVisibility(View.VISIBLE);
            totalPriceCart = Common.TOTAL_CART_PRICE;
            countOfCart = Common.TOTAL_CART_AMOUNT;

            txt_totalPriceCart.setText(new StringBuilder().append(Common.getDecimalNumber(totalPriceCart)).append(" ").append(getString(R.string.currency)));
//            btn_view_cart.setText(new StringBuilder().append(getString(R.string.view_cart)).append(" ( ").append(countOfCart).append(" ) "));
            btn_view_cart.setText(new StringBuilder().append(getString(R.string.view_cart)));
        }

        if (isHaveCart) {
            viewPager.setPadding(0, 0, 0, actionBarHeight);
        } else {
            viewPager.setPadding(0, 0, 0, 0);
        }
        Common.LOG("actionBarHeight : " + actionBarHeight);

//        MyApolloClient.getApollowClientAuthorization().query(SpeedCartQuery.builder().build())
//                .enqueue(new ApolloCall.Callback<SpeedCartQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<SpeedCartQuery.Data> response) {
//
//                        runOnUiThread(() -> {
//
//                            SpeedCartQuery.MyCart dataCart = response.data().CartItemQuery().myCart();
//                            if (dataCart.status() == 200) {
//
//                                isHaveCart = true;
//                                layout_speed_cart.setVisibility(View.VISIBLE);
//                                totalPriceCart = dataCart.TotalPrice();
//                                countOfCart = dataCart.ItemsCount();
//
//                                txt_totalPriceCart.setText(new StringBuilder().append(totalPriceCart).append(" ").append(getString(R.string.currency)));
//                                btn_view_cart.setText(new StringBuilder().append(getString(R.string.view_cart)).append(" ( ").append(countOfCart).append(" ) "));
//
//
//
//                            } else {
//                                isHaveCart = false;
//                                layout_speed_cart.setVisibility(View.GONE);
//                            }
//
//                        });
//
//
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//
//                    }
//                });

        layout_speed_cart.setOnClickListener(view -> gotoCart());

    }

    void gotoCart() {
        try {
            Intent intent = new Intent(StoreDetailActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.EXTRA_SPEED_CART, "EXTRA_SPEED_CART");
            EventBus.getDefault().unregister(this);
            startActivity(intent);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}
