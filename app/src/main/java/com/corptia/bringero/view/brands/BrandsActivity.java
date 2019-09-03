package com.corptia.bringero.view.brands;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.GridSpacingItemDecoration;
import com.corptia.bringero.Utils.LinearSpacingItemDecoration;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandsActivity extends AppCompatActivity {

    @BindView(R.id.recycler_brands)
    RecyclerView recycler_brands;

    BrandsAdapter adapter ;
    List<StoreTypes> storeTypesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands);

        ButterKnife.bind(this);



        recycler_brands.setLayoutManager(new LinearLayoutManager(this));
        recycler_brands.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,this)));

        //Set Data
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img1, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img2, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img3, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img4, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img5, "data"));
        storeTypesList.add(new StoreTypes(R.drawable.img6, "data"));

        adapter = new BrandsAdapter(this , storeTypesList);
        recycler_brands.setAdapter(adapter);

    }
}
