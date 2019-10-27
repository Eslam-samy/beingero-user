package com.corptia.bringero.ui.locations;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationsActivity extends BaseActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_location)
    RecyclerView recyclerView;


    LocationAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,this)));

        List<StoreTypes> storeTypes = new ArrayList<>();
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));
        storeTypes.add(new StoreTypes(1,"asd"));

       // adapter = new LocationAdapter(this , storeTypes);

        recyclerView.setAdapter(adapter);
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
