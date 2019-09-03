package com.corptia.bringero.view.brands;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandsActivity extends AppCompatActivity implements BrandsContract.BrandsView {

    @BindView(R.id.recycler_brands)
    RecyclerView recycler_brands;

    BrandsAdapter adapter ;
    List<StoreTypes> storeTypesList = new ArrayList<>();

    BrandsPresenter brandsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brands);

        ButterKnife.bind(this);

        brandsPresenter = new BrandsPresenter(this);


        recycler_brands.setLayoutManager(new LinearLayoutManager(this));
        recycler_brands.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,this)));

        brandsPresenter.getBrands();

    }

    @Override
    public void setBrands(List<StoreTypes> repositoryList) {

        adapter = new BrandsAdapter(this , repositoryList);
        recycler_brands.setAdapter(adapter);
    }

    @Override
    public void displayError(String errorMessage) {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }
}
