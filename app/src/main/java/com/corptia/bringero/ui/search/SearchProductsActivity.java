package com.corptia.bringero.ui.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetPricedByQuery;
import com.corptia.bringero.type.ProductFilterInput;
import com.corptia.bringero.utils.recyclerview.decoration.GridSpacingItemDecoration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchProductsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.edt_search)
    EditText edt_search;

    @BindView(R.id.recycler_product)
    RecyclerView recycler_product;

    SearchAdapter adapter ;

    String storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        ButterKnife.bind(this);
        initActionBar();

        recycler_product.setLayoutManager(new GridLayoutManager(this, 2));
        recycler_product.addItemDecoration(new GridSpacingItemDecoration(2, Common.dpToPx(15, this), true, 0, Common.dpToPx(10,this)));

        if (getIntent()!=null)
        {
            Intent intent = getIntent();
            storeId = intent.getStringExtra(Constants.EXTRA_STORE_ID);

        }

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

    }

    private void performSearch() {


        ProductFilterInput productFilterInput = ProductFilterInput.builder().name(edt_search.getText().toString()).build();
        MyApolloClient.getApollowClientAuthorization().query(GetPricedByQuery.builder().storeId(storeId).filter(productFilterInput).build())
                .enqueue(new ApolloCall.Callback<GetPricedByQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetPricedByQuery.Data> response) {
                        GetPricedByQuery.@Nullable GetStoreProducts data = response.data().ProductQuery().getStoreProducts();
                        if (data.status() == 200)
                        {
                            adapter = new SearchAdapter(SearchProductsActivity.this , data.Products()) ;
                            recycler_product.setAdapter(adapter);
                        }
                        else
                        {

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//
////        MenuItem item = menu.findItem(R.id.action_search);
//
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }





}
