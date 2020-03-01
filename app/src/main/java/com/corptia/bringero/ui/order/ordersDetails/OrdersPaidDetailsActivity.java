package com.corptia.bringero.ui.order.ordersDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;
import com.corptia.bringero.graphql.SingleOrderQuery;
import com.corptia.bringero.model.UserModel;
import com.corptia.bringero.type.DeliveryOrderStatus;
import com.corptia.bringero.ui.order.orderStoreDetail.OrderStoreDetailsActivity;
import com.corptia.bringero.ui.tracking.TrackingActivity;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class OrdersPaidDetailsActivity extends BaseActivity implements OrdersPaidDetailsView {

    @BindView(R.id.recycler_order)
    RecyclerView recycler_order;
    //    @BindView(R.id.btn_cancel_order)
//    Button btn_cancel_order;
    @BindView(R.id.btn_track_package)
    Button btn_track_package;
    @BindView(R.id.txt_order_id)
    TextView txt_order_id;
    OrdersPaidDetailsAdapter adapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.txt_title_name_address)
    TextView txt_title_name_address;
    @BindView(R.id.txt_address)
    TextView txt_address;

    @BindView(R.id.txt_subtotal)
    TextView txt_subtotal;
    @BindView(R.id.txt_delivery_fees)
    TextView txt_delivery_fees;
    @BindView(R.id.txt_total)
    TextView txt_total;

    @BindView(R.id.txt_date_order)
    TextView txt_date_order;


    //For Tracking Line
    @BindView(R.id.img_requsted)
    ImageView img_requsted;
    @BindView(R.id.img_confirmed)
    ImageView img_confirmed;
    @BindView(R.id.img_delivering)
    ImageView img_delivering;
    @BindView(R.id.img_delivered)
    ImageView img_delivered;

    //pilot
    @BindView(R.id.layout_pilot)
    ConstraintLayout layout_pilot;
    @BindView(R.id.img_pilot)
    CircleImageView img_pilot;
    @BindView(R.id.txt_name)
    TextView txt_name;
    @BindView(R.id.ratingPilot)
    RatingBar ratingPilot;
    @BindView(R.id.root_rating)
    RelativeLayout root_rating;


    @BindView(R.id.layout_data)
    LinearLayout layout_data;
    @BindView(R.id.shimmerLayout_loading)
    ShimmerFrameLayout shimmerLayout_loading;

    OrdersPaidDetailsPresenter detailsPresenter = new OrdersPaidDetailsPresenter(this);

    String orderid, pilotId;


    //Dialog
    AlertDialog dialog;
    CircleImageView img_rate_store;
    RatingBar ratingBar;
    Button btn_submit;
    CustomLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_paid_details);

        if (Common.CURRENT_USER == null) {
            Common.CURRENT_USER = new UserModel();
            String token = (String) PrefUtils.getFromPrefs(this, PrefKeys.USER_TOKEN_API, "Null");
//            Common.LOG("My token Pref :  " + token);
            Common.CURRENT_USER.setToken(token);
        }

        loading = new CustomLoading(this, true);

        ButterKnife.bind(this);

        recycler_order.setLayoutManager(new LinearLayoutManager(this));
        recycler_order.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(10, this)));
        recycler_order.setNestedScrollingEnabled(true);

        Intent intent = getIntent();

        initActionBar();

        if (intent != null) {
            orderid = intent.getStringExtra(Constants.EXTRA_ORDER_ID);
            int serialOrder = intent.getIntExtra(Constants.EXTRA_ORDER_SERIAL, 0);


//            getSupportActionBar().setTitle(new StringBuilder().append(getString(R.string.order_id)).append(" #").append(serialOrder));

//            txt_order_id.setText(new StringBuilder().append(getString(R.string.order_id)).append(" #").append(serialOrder));

            detailsPresenter.getSingleOrder(orderid);

        }


//        btn_cancel_order.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                showDialogCancelOrder();
//
//            }
//        });


        btn_track_package.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dexter.withActivity(OrdersPaidDetailsActivity.this)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                if (pilotId != null) {
                                    Intent intentTrack = new Intent(OrdersPaidDetailsActivity.this, TrackingActivity.class);
                                    intentTrack.putExtra(Constants.EXTRA_ORDER_ID, orderid);
                                    intentTrack.putExtra(Constants.EXTRA_PILOT_ID, pilotId);
                                    startActivity(intentTrack);
                                }

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                                Toasty.info(OrdersPaidDetailsActivity.this, "onPermissionDenied").show();

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });

    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }


//    public void showDialogCancelOrder(){
//
//
//        //For Dialog
//        Dialog dialog;
//        dialog = new Dialog(this);
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_cancellation_reasons, null);
//
//       Button btn_change_of_mind= dialogView.findViewById(R.id.btn_change_of_mind);
//       Button btn_change_payment_method= dialogView.findViewById(R.id.btn_change_payment_method);
//       Button wrong_delivery_address= dialogView.findViewById(R.id.wrong_delivery_address);
//       Button forgot_to_apply_voucher= dialogView.findViewById(R.id.forgot_to_apply_voucher);
//
//
//        btn_change_of_mind.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn_change_of_mind.setBackgroundResource(R.color.colorDarkGray);
//                btn_change_payment_method.setBackgroundResource(R.color.white);
//                wrong_delivery_address.setBackgroundResource(R.color.white);
//                forgot_to_apply_voucher.setBackgroundResource(R.color.white);
//            }
//        });
//
//        btn_change_payment_method.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn_change_of_mind.setBackgroundResource(R.color.white);
//                btn_change_payment_method.setBackgroundResource(R.color.colorDarkGray);
//                wrong_delivery_address.setBackgroundResource(R.color.white);
//                forgot_to_apply_voucher.setBackgroundResource(R.color.white);
//            }
//        });
//
//        wrong_delivery_address.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn_change_of_mind.setBackgroundResource(R.color.white);
//                btn_change_payment_method.setBackgroundResource(R.color.white);
//                wrong_delivery_address.setBackgroundResource(R.color.colorDarkGray);
//                forgot_to_apply_voucher.setBackgroundResource(R.color.white);
//            }
//        });
//
//        forgot_to_apply_voucher.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                btn_change_of_mind.setBackgroundResource(R.color.white);
//                btn_change_payment_method.setBackgroundResource(R.color.white);
//                wrong_delivery_address.setBackgroundResource(R.color.white);
//                forgot_to_apply_voucher.setBackgroundResource(R.color.colorDarkGray);
//            }
//        });
//
//
//
//        dialog.setContentView(dialogView);
//
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER);
//        //int mHeight = getResources().getDisplayMetrics().heightPixels; //For Get Height Screen
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
////        Bitmap map= DialogBlur.takeScreenShot(this);
////
////        Bitmap fast=fastblur(map, 10);
////        final Drawable draw=new BitmapDrawable(getResources(),fast);
////        dialog.getWindow().setBackgroundDrawable(draw);
//
//        dialog.setCancelable(true);
//        dialog.show();
//    }

    @Override
    public void setSingleOrder(DeliveryOneOrderQuery.DeliveryOrderData deliveryOrderData) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                layout_data.setVisibility(View.VISIBLE);

                txt_order_id.setText(new StringBuilder().append(getString(R.string.order_id)).append(" #").append(deliveryOrderData.serial()));

                adapter = new OrdersPaidDetailsAdapter(OrdersPaidDetailsActivity.this,
                        deliveryOrderData.BuyingOrderResponse().BuyingOrderResponseData());

                recycler_order.setAdapter(adapter);

                if (deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.DELIVERED.rawValue()) ||
                        deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.DELIVERING.rawValue())) {
//                Common.CURRENT_TRACK = deliveryOrderData.AllTrip().data().Tracks();

                    pilotId = deliveryOrderData.pilotId();

                }

                if (deliveryOrderData.PilotUserResponse().status() == 200) {

                    txt_name.setText(deliveryOrderData.PilotUserResponse().data().fullName());

                    if (deliveryOrderData.PilotUserResponse().data().AvatarResponse().status() == 200)
                        PicassoUtils.setImage(Common.BASE_URL_IMAGE + deliveryOrderData.PilotUserResponse().data().AvatarResponse().data().name(), img_pilot);
                }


                //Here Total And Address
                if (deliveryOrderData.customerDeliveryAddress() != null) {
                    txt_title_name_address.setText(deliveryOrderData.customerDeliveryAddress().name());
                    txt_address.setText(deliveryOrderData.customerDeliveryAddress().region() + " - " + deliveryOrderData.customerDeliveryAddress().street());
                } else {
                    txt_title_name_address.setText(Common.CURRENT_USER.getCurrentDeliveryAddress().getName());
                    txt_address.setText(Common.CURRENT_USER.getCurrentDeliveryAddress().getRegion() + " - " + Common.CURRENT_USER.getCurrentDeliveryAddress().getStreet());
                }


                txt_subtotal.setText(new StringBuilder().append(Common.getDecimalNumber(deliveryOrderData.SubTotal())).append(" ").append(getString(R.string.currency)));
                txt_delivery_fees.setText(new StringBuilder().append(deliveryOrderData.deliveryCost()).append(" ").append(getString(R.string.currency)));
                txt_total.setText(new StringBuilder().append(Common.getDecimalNumber(deliveryOrderData.SubTotal() + deliveryOrderData.deliveryCost())).append(" ").append(getString(R.string.currency)));

//                total_price.setText(new StringBuilder().append(20 + 500.00).append(" ").append(getString(R.string.currency)));


                //For Tracking Line
                if (deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.ORDERSREQUESTED.rawValue())) {
                    img_requsted.setImageResource(R.drawable.tracking_status_requsted);
                    layout_pilot.setVisibility(View.GONE);
                } else if (deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.ASSIGNEDTOPILOT.rawValue())) {
                    img_confirmed.setImageResource(R.drawable.tracking_status_confirmed);
                    layout_pilot.setVisibility(View.VISIBLE);
                } else if (deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.STORESREPLIED.rawValue())) {
                    img_confirmed.setImageResource(R.drawable.tracking_status_confirmed);
                    layout_pilot.setVisibility(View.GONE);
                } else if (deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.STORESPREPARED.rawValue())) {
                    img_confirmed.setImageResource(R.drawable.tracking_status_confirmed);
                    layout_pilot.setVisibility(View.GONE);
                } else if (deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.DELIVERING.rawValue())) {
                    img_confirmed.setImageResource(R.drawable.tracking_status_confirmed);
                    img_delivering.setImageResource(R.drawable.tracking_status_delivering);
                    layout_pilot.setVisibility(View.VISIBLE);
                    btn_track_package.setVisibility(View.VISIBLE);
                } else if (deliveryOrderData.status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.DELIVERED.rawValue())) {

                    img_confirmed.setImageResource(R.drawable.tracking_status_confirmed);
                    img_delivering.setImageResource(R.drawable.tracking_status_delivering);
                    img_delivered.setImageResource(R.drawable.tracking_status_delivered);
                    layout_pilot.setVisibility(View.VISIBLE);

                    if (deliveryOrderData.pilotDeliveryRating() != null) {
                        ratingPilot.setRating(deliveryOrderData.pilotDeliveryRating());
                        root_rating.setBackgroundColor(Color.TRANSPARENT);
                    } else {


                        showDialogRating(deliveryOrderData);

                        root_rating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (ratingPilot.getRating() == 0f) {
                                    showDialogRating(deliveryOrderData);
                                }
                            }
                        });

                    }
                }


//                    ORDERSREQUESTED("OrdersRequested"),
//
//                            STORESREPLIED("StoresReplied"),
//
//                            STORESPREPARED("StoresPrepared"),
//
//                            ASSIGNEDTOPILOT("AssignedToPilot"),
//
//                            DELIVERING("Delivering"),
//
//                            DELIVERED("Delivered"),

            }
        });


    }

    @Override
    public void onShowLoadingDialog() {

        loading.showProgressBar(OrdersPaidDetailsActivity.this, true);
    }

    @Override
    public void onHideLoadingDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.hideProgressBar();
            }
        });

    }

    @Override
    public void onSuccessRating() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                dialog.dismiss();
                ratingPilot.setRating(ratingBar.getRating());
                root_rating.setBackgroundColor(Color.TRANSPARENT);

            }
        });
    }

    @Override
    public void onFailedRating() {

    }

    @Override
    public void showProgressBar() {

        shimmerLayout_loading.setVisibility(View.VISIBLE);
        layout_data.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressBar() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                shimmerLayout_loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDialogRating(DeliveryOneOrderQuery.DeliveryOrderData pilotData) {

        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_rating, null);

        img_rate_store = dialogView.findViewById(R.id.img_store);
        ratingBar = dialogView.findViewById(R.id.ratingBar);
        btn_submit = dialogView.findViewById(R.id.btn_submit);

        //Set Image Store
        if (pilotData.PilotUserResponse().data().AvatarResponse().status() == 200)
            Picasso.get()
                    .load(Common.BASE_URL_IMAGE + pilotData.PilotUserResponse().data().AvatarResponse().data().name())
                    .placeholder(R.drawable.ic_placeholder_store)
                    .into(img_rate_store);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ratingBar.getRating() != 0f) {

                    detailsPresenter.pilotRating(pilotData._id(), (int) ratingBar.getRating());

                } else {
                    Toasty.warning(OrdersPaidDetailsActivity.this, "Sorry Can't ratting by 0").show();
                }

            }
        });


        builder.setCancelable(true);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);


        //finally creating the alert dialog and displaying it
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();

    }
}
