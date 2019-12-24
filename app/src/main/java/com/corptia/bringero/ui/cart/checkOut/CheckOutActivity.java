package com.corptia.bringero.ui.cart.checkOut;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.cart.Adapter.CartAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

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
    @BindView(R.id.txt_delivery_fees)
    TextView txt_delivery_fees;
    @BindView(R.id.txt_total)
    TextView txt_total;
    @BindView(R.id.total_price)
    TextView total_price;

    @BindView(R.id.txt_date_order)
    TextView txt_date_order;

    CheckOutPresenter checkOutPresenter = new CheckOutPresenter(this);

    AlertDialog alertDialog;

    double totalPrice = 0;

    //For Dialog Confirm
    ImageView img_done;
    Button btn_ok;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

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

//                HomeActivity.navController.popBackStack();
//                HomeActivity.bottomNavigationView.setVisibility(View.VISIBLE);
//                HomeActivity.fab.show();


            checkOutPresenter.sendOrder();

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

            txt_subtotal.setText(new StringBuilder().append(totalPrice).append(" ").append(getString(R.string.currency)));
            txt_delivery_fees.setText(new StringBuilder().append(20).append(" ").append(getString(R.string.currency)));
            txt_total.setText(new StringBuilder().append(20 + totalPrice).append(" ").append(getString(R.string.currency)));

            total_price.setText(new StringBuilder().append(20 + totalPrice).append(" ").append(getString(R.string.currency)));

        }

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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showConfirmDialog();

            }
        });

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


    private void showConfirmDialog() {

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
                finish();
            }
        });

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
