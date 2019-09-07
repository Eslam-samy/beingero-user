package com.corptia.bringero.view.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_notification)
    RecyclerView recycler_notification;

    NotificationAdapter adapter;

    List<StoreTypes> storeTypesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ButterKnife.bind(this);
        initActionBar();

        recycler_notification.setLayoutManager(new LinearLayoutManager(this));
        recycler_notification.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,this)));

        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));
        storeTypesList.add(new StoreTypes(1,"212"));

        adapter = new NotificationAdapter(this ,storeTypesList );



        recycler_notification.setAdapter(adapter);
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

        switch (item.getItemId()){
            case  android.R.id.home:
                onBackPressed();
                break;
        }

        return true;

    }

}
