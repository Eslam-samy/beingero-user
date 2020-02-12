package com.corptia.bringero.ui.location.deliveryLocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.util.StringUtil;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.CustomLoading;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class SelectDeliveryLocationActivity extends BaseActivity implements SelectDeliveryLocationView {

    @BindView(R.id.btn_select_location)
    TextView btn_select_location;
    @BindView(R.id.btn_set_location)
    Button btn_set_location;

    BottomSheetDialog bottomSheetDialog;

    SelectDeliveryLocationPresenter presenter = new SelectDeliveryLocationPresenter(this);
    CustomLoading loading ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_delivery_location);

        initView();

        loading = new CustomLoading(this  , true);


        if (Common.CURRENT_USER!=null) {
            if (Common.CURRENT_USER.getCurrentDeliveryAddress() != null) {

                String region = Common.CURRENT_USER.getCurrentDeliveryAddress().getRegion();
                String name = Common.CURRENT_USER.getCurrentDeliveryAddress().getName();

                if (name!=null && region!=null) {
                    btn_select_location.setText(new StringBuilder().append(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())
                            .append(" (")
                            .append(region.substring(0, 1).toUpperCase() + region.substring(1).toLowerCase())
                            .append(")"));
                }

            } else
                btn_select_location.setText(getString(R.string.select_location));
        }

        btn_select_location.setOnClickListener(view -> bottomSheetDialog = Common.showDialogSelectLocation(SelectDeliveryLocationActivity.this ,bottomSheetDialog , presenter));

        btn_set_location.setOnClickListener(view -> {

            btn_set_location.setEnabled(false);

            //UpdateLocation And Goto Home Activity
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition( R.anim.fade_in, R.anim.fade_out );
            finish();

        });

    }

    private void initView() {
        ButterKnife.bind(this);
    }


//    private void showDialogSelectLocation() {
//
//        bottomSheetDialog = new BottomSheetDialog(this , R.style.AppBottomSheetDialogTheme);
//        bottomSheetDialog.setTitle(getString(R.string.set_location));
//        bottomSheetDialog.setCanceledOnTouchOutside(true);
//        bottomSheetDialog.setCancelable(true);
//        View sheetView = getLayoutInflater().inflate(R.layout.layout_select_delivery_location, null);
//
//        RecyclerView recycler_delivery_location = sheetView.findViewById(R.id.recycler_delivery_location);
//        Button btn_select_location_from_map = sheetView.findViewById(R.id.btn_select_location_from_map);
//        Button btn_apply_location = sheetView.findViewById(R.id.btn_apply_location);
//
//        recycler_delivery_location.setHasFixedSize(true);
//        recycler_delivery_location.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new SelectDeliveryLocationAdapter(this, Common.CURRENT_USER.deliveryAddresses());
//        recycler_delivery_location.setAdapter(adapter);
//
//        adapter.setClickListener(new IOnRecyclerViewClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                //Here Update Location Yo CuttentLocation
//
////                presenter.userUpdateCurrentLocation(adapter.getCurrentDeliveryAddressID(position));
//            }
//        });
//
//        btn_select_location_from_map.setOnClickListener(view -> {
//
//            Log.d("HAZEM" , "HEre");
//
//            //Here Open Maps
//            Dexter.withActivity(SelectDeliveryLocationActivity.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                    .withListener(new PermissionListener() {
//                        @Override
//                        public void onPermissionGranted(PermissionGrantedResponse response) {
//
//                            Common.isUpdateCurrentLocation = true;
//                            startActivity(new Intent(SelectDeliveryLocationActivity.this , MapsActivity.class));
//                            bottomSheetDialog.dismiss();
//                        }
//
//                        @Override
//                        public void onPermissionDenied(PermissionDeniedResponse response) {
//
//                            Toasty.info(SelectDeliveryLocationActivity.this,"Must Granted ACCESS LOCATION")
//                                    .show();
//                        }
//
//                        @Override
//                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                            token.continuePermissionRequest();
//
//                        }
//                    }).check();
//
//
//        });
//
//        bottomSheetDialog.setContentView(sheetView);
//        bottomSheetDialog.show();
//    }

    @Override
    public void onSuccessUpdateCurrentLocation() {

        bottomSheetDialog.dismiss();
        //Here Refresh get me ^_^
        startActivity(new Intent(SelectDeliveryLocationActivity.this , HomeActivity.class));
        finish();
    }

    @Override
    public void onSuccessUpdateNestedLocation() {

    }

    @Override
    public void onSuccessRemovedLocation() {

    }

    @Override
    public void showProgressBar() {
        loading.showProgressBar(this,false);
    }

    @Override
    public void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.hideProgressBar();
            }
        });
    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }
}
