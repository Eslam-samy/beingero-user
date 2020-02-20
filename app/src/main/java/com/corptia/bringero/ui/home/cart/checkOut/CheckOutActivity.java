package com.corptia.bringero.ui.home.cart.checkOut;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.ValidateCouponQuery;
import com.corptia.bringero.ui.order.ordersDetails.OrdersPaidDetailsActivity;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.home.cart.Adapter.CartAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class CheckOutActivity extends BaseActivity implements CheckOutView {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    private CartAdapter cartAdapter;

    @BindView(R.id.btn_confirm)
    Button btn_confirm;

    @BindView(R.id.image_correct)
    ImageView image_correct;
    @BindView(R.id.txt_title_name_address)
    TextView txt_title_name_address;
    @BindView(R.id.txt_address)
    TextView txt_address;

    @BindView(R.id.txt_subtotal)
    TextView txt_subtotal;
    @BindView(R.id.txt_delivery_fees_new)
    TextView txt_delivery_fees_new;
    @BindView(R.id.txt_delivery_fees_old)
    TextView txt_delivery_fees_old;
    @BindView(R.id.txt_total)
    TextView txt_total;
    @BindView(R.id.total_price)
    TextView total_price;

    @BindView(R.id.txt_date_order)
    TextView txt_date_order;

    @BindView(R.id.btn_control_coupon)
    TextView btn_control_coupon;
    @BindView(R.id.btn_add_coupon)
    Button btn_add_coupon;
    @BindView(R.id.edt_coupon_code)
    EditText edt_coupon_code;
    @BindView(R.id.layout_coupon)
    LinearLayout layout_coupon;

    CheckOutPresenter checkOutPresenter = new CheckOutPresenter(this);

    AlertDialog alertDialog;

    double totalPrice = 0;

    //For Dialog Confirm
    ImageView img_done;
    Button btn_ok;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);


        ButterKnife.bind(this);

        initActionBar();


        if (getIntent() != null) {
            totalPrice = getIntent().getDoubleExtra(Constants.EXTRA_TOTAL_CART, 0);
        }

        alertDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        cartAdapter = new CartAdapter(this, Common.CURRENT_CART, false);
        recycler_cart.setLayoutManager(new LinearLayoutManager(this));
        recycler_cart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, this)));
        recycler_cart.setAdapter(cartAdapter);

        btn_confirm.setOnClickListener(view -> {

            if (layout_coupon.getVisibility() != View.GONE && !edt_coupon_code.isEnabled()) {

                checkOutPresenter.sendOrder(edt_coupon_code.getText().toString());
            } else if (layout_coupon.getVisibility() == View.GONE) {
                checkOutPresenter.sendOrder("");
            } else {
                Toasty.warning(CheckOutActivity.this, "Must be remove coupon or apply").show();
            }

//                HomeActivity.navController.popBackStack();
//                HomeActivity.bottomNavigationView.setVisibility(View.VISIBLE);
//                HomeActivity.fab.show();


            // HomeActivity.navController.navigate(R.id.action_checkOutFragment_to_nav_cart);
            //requireActivity().finish();


            //دول ذي بعض في الفعل
            //getActivity().onBackPressed();
            //NavHostFragment.findNavController(CheckOutFragment.this).popBackStack();


        });

        //Set Current Location
        if (Common.CURRENT_USER != null) {
            image_correct.setVisibility(View.GONE);
            txt_title_name_address.setText(Common.CURRENT_USER.getCurrentDeliveryAddress().getName());
            txt_address.setText(Common.CURRENT_USER.getCurrentDeliveryAddress().getRegion() + " - " + Common.CURRENT_USER.getCurrentDeliveryAddress().getStreet());

            txt_subtotal.setText(new StringBuilder().append(Common.getDecimalNumber(totalPrice)).append(" ").append(getString(R.string.currency)));
            txt_delivery_fees_new.setText(new StringBuilder().append((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size())).append(" ").append(getString(R.string.currency)));

            txt_total.setText(new StringBuilder().append(Common.getDecimalNumber((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size()) + totalPrice)).append(" ").append(getString(R.string.currency)));
            total_price.setText(new StringBuilder().append(Common.getDecimalNumber((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size()) + totalPrice)).append(" ").append(getString(R.string.currency)));

        }


        //------------- For Coupon ----------
        btn_control_coupon.setTag(Constants.TAG_COUPON_ADD);
        btn_control_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btn_control_coupon.getTag().equals(Constants.TAG_COUPON_ADD)) {
                    layout_coupon.setVisibility(View.VISIBLE);
                    btn_control_coupon.setText(getString(R.string.remove));
                    btn_control_coupon.setTag(Constants.TAG_COUPON_REMOVE);

                    edt_coupon_code.setText("");
                    edt_coupon_code.setEnabled(true);
                } else if (btn_control_coupon.getTag().equals(Constants.TAG_COUPON_REMOVE)) {

                    layout_coupon.setVisibility(View.GONE);
                    btn_control_coupon.setText(getString(R.string.add));
                    btn_control_coupon.setTag(Constants.TAG_COUPON_ADD);

                    txt_delivery_fees_new.setText(new StringBuilder().append((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size())).append(" ").append(getString(R.string.currency)));
                    txt_total.setText(new StringBuilder().append(Common.getDecimalNumber((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size()) + totalPrice)).append(" ").append(getString(R.string.currency)));
                    total_price.setText(new StringBuilder().append(Common.getDecimalNumber((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size()) + totalPrice)).append(" ").append(getString(R.string.currency)));

                    edt_coupon_code.setText("");

                    txt_delivery_fees_old.setVisibility(View.INVISIBLE);

                } else if (btn_control_coupon.getTag().equals(Constants.TAG_COUPON_CHANGE)) {
                    layout_coupon.setEnabled(true);

                    btn_control_coupon.setTag(Constants.TAG_COUPON_REMOVE);
                    btn_control_coupon.setText(getString(R.string.remove));
                    btn_add_coupon.setVisibility(View.VISIBLE);

                    edt_coupon_code.setEnabled(true);

                }
            }
        });

        btn_add_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Here validate Coupon
                if (edt_coupon_code.getText().toString().trim().isEmpty()) {
                    Toasty.info(CheckOutActivity.this, getString(R.string.coupon_code_is_required)).show();
                    return;
                }

                checkOutPresenter.validateCoupon(edt_coupon_code.getText().toString(), Common.CURRENT_USER.getId());
            }
        });

    }

    private void initActionBar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgressBar() {

        alertDialog.show();
    }

    @Override
    public void hideProgressBar() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void showErrorMessage(String Message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CheckOutActivity.this, "" + Message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onSuccessMessage(String message) {

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        });

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(CheckOutActivity.this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//            }
//        });

    }


    private void showConfirmDialog(String orderId, int serial) {

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_success, null);
        img_done = dialogView.findViewById(R.id.img_done);
        btn_ok = dialogView.findViewById(R.id.btn_ok);

        Drawable drawable = img_done.getDrawable();

        if (drawable instanceof AnimatedVectorDrawableCompat) {
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        } else if (drawable instanceof AnimatedVectorDrawable) {
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dialog != null)
                    dialog.dismiss();

                alertDialog.dismiss();

                Intent intent = new Intent(CheckOutActivity.this, OrdersPaidDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
                intent.putExtra(Constants.EXTRA_ORDER_SERIAL, serial);
                startActivity(intent);

                finish();
            }
        });

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);


        //finally creating the alert dialog and displaying it
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (dialog != null) {
//            finish();
//        }
    }


    @Override
    public void onSuccessCreateOrder(String orderId, int serial) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showConfirmDialog(orderId, serial);
            }
        });

    }

    @Override
    public void onSuccessValidateCoupon(ValidateCouponQuery.Data1 couponData) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Here fetch data

                //Common.CURRENT_CART

//                Delevary cost * count store = all delevaryCost
//
//                DelevaryCost * 50 / 100
//
//                        -----------------------------------------
//                if(Delevary Cost - dicountValue < =0 )
//
//                Delevary Cost - dicountValue = All DelevaryCost * Count Store

                double allDelivery = Integer.parseInt(Common.DELIVERY_COST) * Common.CURRENT_CART.size();
                double finalDeliveryCost = 0;

                if (couponData.userCanUse().canUse()) {

                    edt_coupon_code.setEnabled(false);
                    btn_control_coupon.setTag(Constants.TAG_COUPON_CHANGE);
                    btn_control_coupon.setText(getString(R.string.change));
                    btn_add_coupon.setVisibility(View.GONE);

                    if (couponData.discountFixed())
                        finalDeliveryCost = (allDelivery * (1-couponData.discountRatio()));
                    else {
                        finalDeliveryCost = ( Integer.parseInt(Common.DELIVERY_COST) - couponData.dicountValue()) *  Common.CURRENT_CART.size();
                    }

                    if (finalDeliveryCost <= Double.parseDouble(Common.MINDELIVERY_COST)) {
                        finalDeliveryCost = Double.parseDouble(Common.MINDELIVERY_COST);
                    }

                    txt_delivery_fees_old.setText(new StringBuilder().append((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size())).append(" ").append(getString(R.string.currency)));
                    txt_delivery_fees_old.setPaintFlags(txt_delivery_fees_old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_delivery_fees_old.setVisibility(View.VISIBLE);

                    txt_delivery_fees_new.setText(new StringBuilder().append(Common.getDecimalNumber(finalDeliveryCost)).append(" ").append(getString(R.string.currency)));

                    txt_total.setText(new StringBuilder().append(Common.getDecimalNumber(finalDeliveryCost + totalPrice)).append(" ").append(getString(R.string.currency)));
                    total_price.setText(new StringBuilder().append(Common.getDecimalNumber(finalDeliveryCost + totalPrice)).append(" ").append(getString(R.string.currency)));

                } else {
                    Toasty.warning(CheckOutActivity.this, getString(R.string.you_have_run_out_of_this_coupon)).show();
                }

            }
        });

    }

    @Override
    public void onNotFoundValidateCoupon() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasty.warning(CheckOutActivity.this, "لا يوجد خصم علي هذا الكوبون").show();
            }
        });

    }
}
