package com.corptia.bringero.ui.storesDetail.single_store;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.databinding.ActivitySingleStoreBinding;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.ProductsTypeQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;
import com.corptia.bringero.model.EventBus.CalculateCartEvent;
import com.corptia.bringero.type.StoreFilterInput;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.ui.home.HomeModefiedActivity;
import com.corptia.bringero.ui.search.SearchProductsActivity;
import com.corptia.bringero.ui.storesDetail.StoreDetailContract;
import com.corptia.bringero.ui.storesDetail.StoreDetailPresenter;
import com.corptia.bringero.ui.storesDetail.single_store.adapters.CategoryAdapter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SingleStoreActivity extends BaseActivity implements StoreDetailContract.StoreDetailView {

    ActivitySingleStoreBinding binding;

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
    int actionBarHeight;
    //Search
    public static final int EXTRA_REVEAL_CENTER_PADDING = 40;

    CategoryAdapter adapter;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_store);
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        Intent intent = getIntent();
        if (intent != null) {
            adminUserId = intent.getStringExtra(Constants.EXTRA_ADMIN_USER_ID);
            storeId = intent.getStringExtra(Constants.EXTRA_STORE_ID);
            storeName = intent.getStringExtra(Constants.EXTRA_STORE_NAME);
            storeImage = intent.getStringExtra(Constants.EXTRA_STORE_IMAGE);
        }

        imageUrl = Common.BASE_URL_IMAGE + storeImage;
        if (!storeImage.equalsIgnoreCase("null"))
//            PicassoUtils.setImage(imageUrl, binding.imageStore);
            binding.toolBar.setLabel(storeName);

//        binding.txtNameStore.setText(storeName);
        presenter = new StoreDetailPresenter(this);

        binding.searchConstraint.setOnClickListener((View.OnClickListener) view -> {

            EventBus.getDefault().postSticky(new CalculateCartEvent(false, 0, 0));

            Intent intent1 = new Intent(SingleStoreActivity.this, SearchProductsActivity.class);
            intent1.putExtra(Constants.EXTRA_STORE_ID, storeId);
            intent1.putExtra(Constants.EXTRA_STORE_IMAGE, imageUrl);
            startActivity(intent1);

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
                                for (int i = 0; i < Common.CURRENT_STORE.ProductTypesStore().data().size(); i++) {
                                    typeIds.add(Common.CURRENT_STORE.ProductTypesStore().data().get(i)._id());
                                }
                                MyApolloClient.getApollowClientAuthorization().query(ProductsTypeQuery.builder().typesIds(typeIds).build())
                                        .enqueue(new ApolloCall.Callback<ProductsTypeQuery.Data>() {
                                            @Override
                                            public void onResponse(@NotNull Response<ProductsTypeQuery.Data> response) {
                                                ProductsTypeQuery.GetAll productsResponse = response.data().ProductTypeQuery().getAll();
                                                runOnUiThread(() -> {
                                                    if (productsResponse.status() == 200) {
                                                        Common.CURRENT_PRODUCTS_TYPE = productsResponse.data();
                                                        List<ProductsTypeQuery.Data1> typesList;
                                                        typesList = new ArrayList<>();
                                                        typesList.add(new ProductsTypeQuery.Data1("Offres", "0", getString(R.string.offers), null));
//                                for
                                                        typesList.addAll(productsResponse.data());
                                                        binding.categoriesRecycler.setLayoutManager(new LinearLayoutManager(SingleStoreActivity.this, RecyclerView.HORIZONTAL, false));
                                                        adapter = new CategoryAdapter(SingleStoreActivity.this, typesList);
                                                        binding.categoriesRecycler.setAdapter(adapter);

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(@NotNull ApolloException e) {

                                            }
                                        });

//                                adapter.notifyDataSetChanged();

                            }

                        });


                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }


    @Override
    public void showProgressBar() {
        Log.d("ESLAM", "showProgressBar: shw proogress bar ");

    }

    @Override
    public void hideProgressBar() {
        Log.d("ESLAM", "showProgressBar: hidr proogress bar ");
        handler.post(() -> binding.loading.setVisibility(View.GONE));


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


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void request(CalculateCartEvent event) {
        if (event != null) {
            if (event.isSuccess()) {
                countOfCart = Common.TOTAL_CART_AMOUNT;
                totalPriceCart += event.getProductPrice();
                isHaveCart = true;
                binding.speedCart.getRoot().setVisibility(View.VISIBLE);
                binding.speedCart.txtTotalPriceCart.setText(new StringBuilder().append(Common.getDecimalNumber(totalPriceCart)).append(" ").append(getString(R.string.currency)));
                binding.speedCart.btnViewCart.setText(new StringBuilder().append(getString(R.string.view_cart)));
                Common.totalPriceCart = totalPriceCart;
                if (totalPriceCart <= 0.0f) {
                    isHaveCart = false;
                    binding.speedCart.getRoot().setVisibility(View.GONE);
                }
            } else {
                if (Common.TOTAL_CART_AMOUNT == 0) {
                    isHaveCart = false;
                    binding.speedCart.getRoot().setVisibility(View.GONE);
                }
            }
            if (isHaveCart) {
                setMargins(binding.productsRecycler, 0, actionBarHeight, 0, 0);
            } else {
                setMargins(binding.productsRecycler, 0, 0, 0, 0);

            }

        }


    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void getSpeedCart() {

        if (Common.TOTAL_CART_AMOUNT <= 0) {
            Common.TOTAL_CART_AMOUNT = 0;
            isHaveCart = false;
            binding.speedCart.getRoot().setVisibility(View.GONE);
        } else {
            isHaveCart = true;
            binding.speedCart.getRoot().setVisibility(View.VISIBLE);
            totalPriceCart = Common.TOTAL_CART_PRICE;
            countOfCart = Common.TOTAL_CART_AMOUNT;
            binding.speedCart.txtTotalPriceCart.setText(new StringBuilder().append(Common.getDecimalNumber(totalPriceCart)).append(" ").append(getString(R.string.currency)));
            binding.speedCart.btnViewCart.setText(new StringBuilder().append(getString(R.string.view_cart)));
        }

        if (isHaveCart) {
            setMargins(binding.productsRecycler, 0, actionBarHeight, 0, 0);
        } else {
            setMargins(binding.productsRecycler, 0, 0, 0, 0);

        }
        Common.LOG("actionBarHeight : " + actionBarHeight);

        binding.speedCart.getRoot().setOnClickListener(view -> gotoCart());

    }

    void gotoCart() {
        try {
            Intent intent = new Intent(SingleStoreActivity.this, HomeModefiedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.EXTRA_SPEED_CART, "EXTRA_SPEED_CART");
            EventBus.getDefault().unregister(this);
            startActivity(intent);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}