package com.corptia.bringero.view.productDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.SingleProductQuery;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailActivity extends AppCompatActivity implements ProductDetailContract.ProductDetailView {

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
    String productId = "";


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


        btn_addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                productDetailPresenter.addToCart(productId);

            }
        });

    }

    private void initView() {
        ButterKnife.bind(this);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setDataProduct(SingleProductQuery.Product data) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                collapsingToolbarLayout.setTitle(data.StoreResponse().data().name());
                txt_name_product.setText(data.ProductResponse().ProductData().name());
                txt_price.setText(""+data.storePrice() + getString(R.string.currency));
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

        runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this, ""+Message, Toast.LENGTH_SHORT).show());

    }
}
