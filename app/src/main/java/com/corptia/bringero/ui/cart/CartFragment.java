package com.corptia.bringero.ui.cart;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.model.EventBus.CalculatePriceEvent;
import com.corptia.bringero.ui.cart.Adapter.CartAdapter;
import com.corptia.bringero.ui.cart.checkOut.CheckOutActivity;
import com.corptia.bringero.ui.home.HomeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartFragment extends Fragment implements CartContract.CartView {

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    //Adapters
    private CartAdapter cartAdapter;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.total_price)
    TextView total_price;

    Handler handler = new Handler();
    CartPresenter cartPresenter = new CartPresenter(this);

    double totalPrice = 0;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void setMyCart(MyCartQuery.MyCart myCartData) {

        handler.post(() -> {

            totalPrice = myCartData.TotalPrice();
            Log.d("HAZEM" , "OROGINAL TOTLA :: " + myCartData.TotalPrice());

            //stickyRecyclerView.setDataSource(myCartData);
            cartAdapter = new CartAdapter(getActivity(), myCartData.storeData(), true);
            recycler_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
            recycler_cart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, getActivity())));
            recycler_cart.setAdapter(cartAdapter);


            if (myCartData != null) {
                if (myCartData.storeData().size() != 0) {

                    Common.CURRENT_CART = myCartData.storeData();

                    total_price.setText(new StringBuilder().append(totalPrice).append(getString(R.string.currency)));


                    btn_next.setOnClickListener(view1 -> {
//                        HomeActivity.navController.navigate(R.id.action_nav_cart_to_checkOutFragment);
//                        HomeActivity.bottomNavigationView.setVisibility(View.GONE);
//                        HomeActivity.fab.hide();


                        startActivity(new Intent(getActivity(), CheckOutActivity.class));


                    });
                }
            }
            cartAdapter.setCallBackUpdateCartItemsListener((itemId, amount) -> {
                cartPresenter.updateCartItems(itemId, amount);
            });


        });

    }

    @Override
    public void setPlaceholder() {
        handler.post(new Runnable() {
            @Override
            public void run() {

                recycler_cart.setVisibility(View.GONE);
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void calculatePrice(CalculatePriceEvent event) {
        if (event != null) {
            if (event.getProductId() != null) {
                calculateCartTotalPrice(event.getProductId(), event.getAmount(), event.getStorePrice());

            } else {
                //double total = Double.parseDouble(total_price.getText().toString());

                totalPrice -= event.getTotalProductPrice();
                total_price.setText(""+(totalPrice) + getString(R.string.currency));

            }
        } else {

        }


    }

    private void calculateCartTotalPrice(String productId, int amount, double storePrice) {

        cartPresenter.updateCartItems(productId, amount);

        totalPrice += storePrice;

        total_price.setText(new StringBuilder().append(totalPrice).append(getString(R.string.currency)));

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        cartPresenter.getMyCart();
    }
}
