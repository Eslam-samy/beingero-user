package com.corptia.bringero.ui.order.ordersDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;
import com.corptia.bringero.model.CartModel;
import com.corptia.bringero.type.DeliveryOrderStatus;
import com.corptia.bringero.ui.tracking.TrackingActivity;
import com.corptia.bringero.utils.PicassoUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class OrdersPaidDetailsActivity extends BaseActivity implements OrdersPaidDetailsView {

    @BindView(R.id.recycler_order)
    RecyclerView recycler_order;
    @BindView(R.id.btn_cancel_order)
    Button btn_cancel_order;
    @BindView(R.id.btn_track_package)
    Button btn_track_package;
    @BindView(R.id.txt_order_id)
    TextView txt_order_id;
    OrdersPaidDetailsAdapter adapter ;

    OrdersPaidDetailsPresenter detailsPresenter = new OrdersPaidDetailsPresenter(this);

    String orderid , pilotId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_paid_details);

        ButterKnife.bind(this);

        recycler_order.setLayoutManager(new LinearLayoutManager(this));
        recycler_order.setNestedScrollingEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            orderid = intent.getStringExtra(Constants.EXTRA_ORDER_ID);
            int serialOrder = intent.getIntExtra(Constants.EXTRA_ORDER_SERIAL , 0);
            txt_order_id.setText(new StringBuilder().append(getString(R.string.order_id)).append(" #").append(serialOrder));

            detailsPresenter.getSingleOrder(orderid);


        }


        btn_cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogCancelOrder();

            }
        });


        btn_track_package.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withActivity(OrdersPaidDetailsActivity.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                if (pilotId!=null)
                                {
                                    Intent intentTrack = new Intent(OrdersPaidDetailsActivity.this , TrackingActivity.class);
                                    intentTrack.putExtra(Constants.EXTRA_ORDER_ID , orderid);
                                    intentTrack.putExtra(Constants.EXTRA_PILOT_ID , pilotId);
                                    startActivity(intentTrack);
                                }

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                                Toasty.info(OrdersPaidDetailsActivity.this , "onPermissionDenied").show();

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();



            }
        });

    }


    public void showDialogCancelOrder(){


        //For Dialog
        Dialog dialog;
        dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_cancellation_reasons, null);

       Button btn_change_of_mind= dialogView.findViewById(R.id.btn_change_of_mind);
       Button btn_change_payment_method= dialogView.findViewById(R.id.btn_change_payment_method);
       Button wrong_delivery_address= dialogView.findViewById(R.id.wrong_delivery_address);
       Button forgot_to_apply_voucher= dialogView.findViewById(R.id.forgot_to_apply_voucher);


        btn_change_of_mind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_change_of_mind.setBackgroundResource(R.color.colorDarkGray);
                btn_change_payment_method.setBackgroundResource(R.color.white);
                wrong_delivery_address.setBackgroundResource(R.color.white);
                forgot_to_apply_voucher.setBackgroundResource(R.color.white);
            }
        });

        btn_change_payment_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_change_of_mind.setBackgroundResource(R.color.white);
                btn_change_payment_method.setBackgroundResource(R.color.colorDarkGray);
                wrong_delivery_address.setBackgroundResource(R.color.white);
                forgot_to_apply_voucher.setBackgroundResource(R.color.white);
            }
        });

        wrong_delivery_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_change_of_mind.setBackgroundResource(R.color.white);
                btn_change_payment_method.setBackgroundResource(R.color.white);
                wrong_delivery_address.setBackgroundResource(R.color.colorDarkGray);
                forgot_to_apply_voucher.setBackgroundResource(R.color.white);
            }
        });

        forgot_to_apply_voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_change_of_mind.setBackgroundResource(R.color.white);
                btn_change_payment_method.setBackgroundResource(R.color.white);
                wrong_delivery_address.setBackgroundResource(R.color.white);
                forgot_to_apply_voucher.setBackgroundResource(R.color.colorDarkGray);
            }
        });



        dialog.setContentView(dialogView);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);
        //int mHeight = getResources().getDisplayMetrics().heightPixels; //For Get Height Screen
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

//        Bitmap map= DialogBlur.takeScreenShot(this);
//
//        Bitmap fast=fastblur(map, 10);
//        final Drawable draw=new BitmapDrawable(getResources(),fast);
//        dialog.getWindow().setBackgroundDrawable(draw);

        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void setSingleOrder(DeliveryOneOrderQuery.DeliveryOrderData deliveryOrderData) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter = new OrdersPaidDetailsAdapter(OrdersPaidDetailsActivity.this ,
                        deliveryOrderData.BuyingOrderResponse().BuyingOrderResponseData());

                recycler_order.setAdapter(adapter);

                if (deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.DELIVERED.rawValue()) ||
                        deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.DELIVERING.rawValue())){
                Common.CURRENT_TRACK = deliveryOrderData.AllTrip().data().Tracks();

                pilotId = deliveryOrderData.pilotId();
                }
            }
        });


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
