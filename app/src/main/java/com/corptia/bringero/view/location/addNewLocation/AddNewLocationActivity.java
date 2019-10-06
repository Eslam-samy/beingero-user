package com.corptia.bringero.view.location.addNewLocation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.corptia.bringero.R;
import com.corptia.bringero.view.location.deliveryLocation.SelectDeliveryLocationPresenter;
import com.corptia.bringero.view.location.deliveryLocation.SelectDeliveryLocationView;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNewLocationActivity extends AppCompatActivity implements SelectDeliveryLocationView {

    @BindView(R.id.input_region)
    TextInputLayout input_region;
    @BindView(R.id.input_address_name)
    TextInputLayout input_address_name;
    @BindView(R.id.input_flatType)
    TextInputLayout input_flatType;
    @BindView(R.id.input_street)
    TextInputLayout input_street;
    @BindView(R.id.input_building)
    TextInputLayout input_building;
    @BindView(R.id.input_floor)
    TextInputLayout input_floor;
    @BindView(R.id.input_flat)
    TextInputLayout input_flat;
    @BindView(R.id.btn_save)
    Button btn_save;

    SelectDeliveryLocationPresenter presenter = new SelectDeliveryLocationPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_location);

        initView();
    }

    private void initView() {

        ButterKnife.bind(this);
        btn_save.setOnClickListener(view -> {

        });

    }

    @Override
    public void onSuccessUpdateCurrentLocation() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onLoginSuccess(String message) {

    }

    @Override
    public void onLoginError(String message) {

    }
}
