package com.corptia.bringero.ui.location.deliveryLocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.home.HomeActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectDeliveryLocationActivity extends AppCompatActivity implements SelectDeliveryLocationView {

    @BindView(R.id.btn_select_location)
    Button btn_select_location;
    @BindView(R.id.btn_set_location)
    Button btn_set_location;

    BottomSheetDialog bottomSheetDialog;
    SelectDeliveryLocationAdapter adapter;

    SelectDeliveryLocationPresenter presenter = new SelectDeliveryLocationPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_delivery_location);

        initView();

        if (Common.CURRENT_USER!=null) {
            if (Common.CURRENT_USER.currentDeliveryAddress() != null) {
                btn_select_location.setText(new StringBuilder().append(Common.CURRENT_USER.currentDeliveryAddress().name())
                        .append(" (")
                        .append(Common.CURRENT_USER.currentDeliveryAddress().region())
                        .append(")"));
            } else
                btn_select_location.setText("Select Location");
        }

        btn_select_location.setOnClickListener(view -> showDialogSelectLocation());

        btn_set_location.setOnClickListener(view -> {

            //UpdateLocation And Goto Home Activity
            startActivity(new Intent(this, HomeActivity.class));

        });

    }

    private void initView() {
        ButterKnife.bind(this);
    }


    private void showDialogSelectLocation() {

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setTitle(getString(R.string.set_location));
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_select_delivery_location, null);

        RecyclerView recycler_delivery_location = sheetView.findViewById(R.id.recycler_delivery_location);
        Button btn_select_location_from_map = sheetView.findViewById(R.id.btn_select_location_from_map);

        recycler_delivery_location.setHasFixedSize(true);
        recycler_delivery_location.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SelectDeliveryLocationAdapter(this, Common.CURRENT_USER.deliveryAddresses());
        recycler_delivery_location.setAdapter(adapter);

        adapter.setClickListener(new IOnRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Here Update Location Yo CuttentLocation

                presenter.userUpdateCurrentLocation(adapter.getCurrentDeliveryAddressID(position));
            }
        });

        btn_select_location_from_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Here Open Maps
                Common.isUpdateCurrentLocation = true;
                startActivity(new Intent(SelectDeliveryLocationActivity.this , MapsActivity.class));
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    @Override
    public void onSuccessUpdateCurrentLocation() {

        bottomSheetDialog.dismiss();
        //Here Refresh get me ^_^
        startActivity(new Intent(SelectDeliveryLocationActivity.this , HomeActivity.class));
    }

    @Override
    public void onSuccessUpdateNestedLocation() {

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }
}
