package com.corptia.bringero.view.order.ordersDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.DialogBlur;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;
import com.corptia.bringero.model.CartItems;
import com.corptia.bringero.model.CartModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corptia.bringero.Utils.DialogBlur.fastblur;

public class OrdersPaidDetailsActivity extends AppCompatActivity implements OrdersPaidDetailsView {

    @BindView(R.id.recycler_order)
    RecyclerView recycler_order;
    @BindView(R.id.btn_cancel_order)
    Button btn_cancel_order;
    @BindView(R.id.txt_order_id)
    TextView txt_order_id;
    OrdersPaidDetailsAdapter adapter ;

    List<CartModel> cartModelList = new ArrayList<>();

    OrdersPaidDetailsPresenter detailsPresenter = new OrdersPaidDetailsPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_paid_details);

        ButterKnife.bind(this);

        recycler_order.setLayoutManager(new LinearLayoutManager(this));
        recycler_order.setNestedScrollingEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            String orderid = intent.getStringExtra(Constants.EXTRA_ORDER_ID);
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
}
