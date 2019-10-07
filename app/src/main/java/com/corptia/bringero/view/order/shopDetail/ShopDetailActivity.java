package com.corptia.bringero.view.order.shopDetail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopDetailActivity extends AppCompatActivity {

    @BindView(R.id.recycler_items)
    RecyclerView recycler_items;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ShopDetailAdapter adapter;
    List<StoreTypes> storeTypesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        ButterKnife.bind(this);



        recycler_items.setLayoutManager(new LinearLayoutManager(this));
        recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,this)));

        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));
        storeTypesList.add(new StoreTypes(1,"asd"));

        adapter = new ShopDetailAdapter(this , storeTypesList);
        recycler_items.setAdapter(adapter);


        initActionBar();
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

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
