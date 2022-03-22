package com.corptia.bringero.ui.home.cart.checkOut;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.corptia.bringero.databinding.ActivityCheckOutBinding;
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

    ActivityCheckOutBinding binding;


    private CartAdapter cartAdapter;


    CheckOutPresenter checkOutPresenter = new CheckOutPresenter(this);

    AlertDialog alertDialog;

    double totalPrice = 0;

    //For Dialog Confirm
    ImageView img_done;
    Button btn_ok;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;
    AlertDialog dialog;
    private String couponCode = "";


    double deliveryValue = 0;
    String oldTotal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out);
        ButterKnife.bind(this);


        if (getIntent() != null) {
            totalPrice = getIntent().getDoubleExtra(Constants.EXTRA_TOTAL_CART, 0);
        }

        alertDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        cartAdapter = new CartAdapter(this, Common.CURRENT_CART, false);
        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerCart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, this)));
        binding.recyclerCart.setAdapter(cartAdapter);

        binding.btnConfirm.setOnClickListener(view -> {
            checkOutPresenter.sendOrder(couponCode);
        });

        binding.removeCoupon.setOnClickListener(view -> {
            binding.couponMessage.setText("");
            couponCode = "";
            binding.applyCoupon.setVisibility(View.VISIBLE);
            binding.removeCoupon.setVisibility(View.GONE);
            binding.inputCoupon.setText("");
            binding.txtDiscount.setText("0.0" + " " + getString(R.string.currency));
            binding.txtTotal.setText(oldTotal);

        });
        binding.inputCoupon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 3) {
                    binding.applyCoupon.setBackground(getDrawable(R.drawable._6_gradient_rect));
                } else {
                    binding.applyCoupon.setBackground(getDrawable(R.drawable._6_gray_rect));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Set Current Location
        if (Common.CURRENT_USER != null) {

            //TODO sho delivery icon
            binding.deliveryLocationCard.imageCorrect.setVisibility(View.GONE);
            binding.deliveryLocationCard.txtTitleNameAddress.setText(Common.CURRENT_USER.getCurrentDeliveryAddress().getName());
            binding.deliveryLocationCard.txtAddress.setText(Common.CURRENT_USER.getCurrentDeliveryAddress().getRegion() + " - " + Common.CURRENT_USER.getCurrentDeliveryAddress().getStreet());
            binding.txtSubtotal.setText(new StringBuilder().append(Common.getDecimalNumber(totalPrice)).append(" ").append(getString(R.string.currency)));

            Log.d("DDD", "onCreate: store size and max" + Common.STORE_COUNT + "cart: " + Common.CURRENT_CART.size() + "max : " + Common.MAX_AD_COST_STORE);
            if (Common.CURRENT_CART.size() <= Integer.parseInt(Common.MAX_AD_COST_STORE)) {
                deliveryValue = 15;

                binding.txtDeliveryFeesNew.setText(new StringBuilder().append(10).append(" ").append("-").append(" ").append(15).append(" ").append(getString(R.string.currency)));
                binding.txtTotal.setText(new StringBuilder().append(Common.getDecimalNumber(totalPrice + 10)).append(" ")
                        .append("-").append(" ").append(Common.getDecimalNumber(totalPrice + 15)).append(" ").append(getString(R.string.currency)));
            } else {
                deliveryValue = (Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size());
                binding.txtDeliveryFeesNew.setText(new StringBuilder().append((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size())).append(" ").append(getString(R.string.currency)));
                binding.txtTotal.setText(new StringBuilder().append(Common.getDecimalNumber((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size()) + totalPrice)).append(" ").append(getString(R.string.currency)));
            }
        }


        //------------- For Coupon ----------

        binding.applyCoupon.setOnClickListener(view -> {
            couponCode = binding.inputCoupon.getText().toString();
            if (couponCode.length() > 3) {
                if (binding.inputCoupon.getText().toString().trim().isEmpty()) {
                    Toasty.info(CheckOutActivity.this, getString(R.string.coupon_code_is_required)).show();
                    return;
                }
                checkOutPresenter.validateCoupon(couponCode, Common.CURRENT_USER.getId());
            }
            //Here validate Coupon
        });
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
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {

                double allDelivery = Integer.parseInt(Common.DELIVERY_COST) * Common.CURRENT_CART.size();
                double finalDeliveryCost = 0;

                if (couponData.userCanUse().canUse()) {

                    binding.couponMessage.setTextColor(getColor(R.color.approved));
                    binding.couponMessage.setText(getString(R.string.coupon_applied));
                    binding.applyCoupon.setVisibility(View.GONE);
                    binding.removeCoupon.setVisibility(View.VISIBLE);
                    oldTotal = binding.txtTotal.getText().toString();


//                    edt_coupon_code.setEnabled(false);
//                    btn_control_coupon.setTag(Constants.TAG_COUPON_CHANGE);
//                    btn_control_coupon.setText(getString(R.string.change));

                    if (!couponData.discountFixed()) {
                        finalDeliveryCost = (allDelivery * (1 - couponData.discountRatio()));
                    } else {
                        finalDeliveryCost = (Integer.parseInt(Common.DELIVERY_COST) - couponData.discountValue()) * Common.CURRENT_CART.size();
                    }
                    if (finalDeliveryCost <= Double.parseDouble(Common.MINDELIVERY_COST)) {
                        finalDeliveryCost = Double.parseDouble(Common.MINDELIVERY_COST);
                    }

//                    txt_delivery_fees_old.setText(new StringBuilder().append((Double.parseDouble(Common.DELIVERY_COST) * Common.CURRENT_CART.size())).append(" ").append(getString(R.string.currency)));
//                    txt_delivery_fees_old.setPaintFlags(txt_delivery_fees_old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    txt_delivery_fees_old.setVisibility(View.VISIBLE);
                    double val = deliveryValue - finalDeliveryCost;
                    binding.txtDiscount.setText(val + " " + getString(R.string.currency));

//                    binding.txtDeliveryFeesNew.setText(new StringBuilder().append(Common.getDecimalNumber(finalDeliveryCost)).append(" ").append(getString(R.string.currency)));

                    binding.txtTotal.setText(new StringBuilder().append(Common.getDecimalNumber(finalDeliveryCost + totalPrice)).append(" ").append(getString(R.string.currency)));

                } else {
                    binding.couponMessage.setTextColor(getColor(R.color.colorDelete));
                    binding.couponMessage.setText(getString(R.string.you_have_run_out_of_this_coupon));
                }

            }
        });

    }

    @Override
    public void onNotFoundValidateCoupon() {

        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                binding.couponMessage.setTextColor(getColor(R.color.colorDelete));
                binding.couponMessage.setText("لا يوجد خصم علي هذا الكوبون");
            }
        });

    }
}
