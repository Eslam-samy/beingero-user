package com.corptia.bringero.ui.productDetail;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.SingleProductQuery;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailActivity extends BaseActivity implements ProductDetailContract.ProductDetailView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.txt_name_product)
    TextView txt_name_product;
    @BindView(R.id.btn_addToCart)
    Button btn_addToCart;
    @BindView(R.id.txt_price)
    TextView txt_price;
    @BindView(R.id.image_product)
    ImageView image_product;
    String productId = "", imageUrl = "";
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;


    ProductDetailPresenter productDetailPresenter = new ProductDetailPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initView();
        setupActionBar();


        Intent intent = getIntent();
        productId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID);
        productDetailPresenter.getSingleProduct(productId);
        imageUrl = intent.getStringExtra(Constants.EXTRA_PRODUCT_IMAGE);
        PicassoUtils.setImage(Common.BASE_URL_IMAGE + imageUrl, image_product);


        btn_addToCart.setOnClickListener(view -> productDetailPresenter.addToCart(productId));

    }

    private void initView() {
        ButterKnife.bind(this);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setTitleEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(true);

        }
    }

    @Override
    public void setDataProduct(SingleProductQuery.Product data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //getSupportActionBar().setTitle("hfjfjhffffffffffffffjfgjh");
                toolbar.setTitle("");
                toolbar_title.setText(data.StoreResponse().data().name());

                //collapsingToolbarLayout.setTitle(data.StoreResponse().data().name());
                txt_name_product.setText(data.ProductResponse().ProductData().name());
                txt_price.setText("" + data.storePrice() + getString(R.string.currency));
            }
        });

//        productId = data._id();
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showErrorMessage(String Message) {
        runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, "" + Message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onSuccessMessage(String message) {
        Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
