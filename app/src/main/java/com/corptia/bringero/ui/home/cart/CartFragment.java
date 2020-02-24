package com.corptia.bringero.ui.home.cart;


import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GeneralOptionAllQuery;
import com.corptia.bringero.graphql.GeneralOptionQuery;
import com.corptia.bringero.type.GeneralOptionFilter;
import com.corptia.bringero.type.GeneralOptionNameEnum;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.model.EventBus.CalculatePriceEvent;
import com.corptia.bringero.ui.home.cart.Adapter.CartAdapter;
import com.corptia.bringero.ui.home.cart.checkOut.CheckOutActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class CartFragment extends Fragment implements CartContract.CartView {

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    //Adapters
    private CartAdapter cartAdapter;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.total_price)
    TextView total_price;
    @BindView(R.id.layout_checkOut)
    ConstraintLayout layout_checkOut;

    Handler handler = new Handler();
    CartPresenter cartPresenter = new CartPresenter(this);

    double totalPrice = 0;

    //For Placeholder
    @BindView(R.id.layout_placeholder)
    ConstraintLayout layout_placeholder;
    @BindView(R.id.img_placeholder)
    ImageView img_placeholder;
    @BindView(R.id.txt_placeholder_title)
    TextView txt_placeholder_title;
    @BindView(R.id.txt_placeholder_dec)
    TextView txt_placeholder_dec;
    @BindView(R.id.btn_1)
    Button btn_home;
    @BindView(R.id.btn_2)
    Button btn_2;

    @BindView(R.id.loading)
    LottieAnimationView loading;

    CustomLoading loadingDialog;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);

        recycler_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_cart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, getActivity())));

        initPlaceHolder();

        loadingDialog = new CustomLoading(getActivity(), true);

        return view;
    }

    private void initPlaceHolder() {

        recycler_cart.setVisibility(View.GONE);
        btn_2.setVisibility(View.GONE);
        img_placeholder.setImageResource(R.drawable.ic_placeholder_cart);

        btn_home.setText(getString(R.string.menu_home));
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BottomNavigationView) getActivity()
                        .findViewById(R.id.nav_bottomNavigationView))
                        .setSelectedItemId(R.id.nav_home);
            }
        });


        txt_placeholder_title.setText(getString(R.string.placeholder_title_cart));
        txt_placeholder_dec.setText(getString(R.string.placeholder_dec_cart));

        layout_placeholder.setVisibility(View.GONE);

    }

    @Override
    public void setMyCart(MyCartQuery.MyCart myCartData) {

        handler.post(() -> {

            layout_placeholder.setVisibility(View.GONE);
            recycler_cart.setVisibility(View.VISIBLE);
            layout_checkOut.setVisibility(View.VISIBLE);


            totalPrice = myCartData.TotalPrice();

            //stickyRecyclerView.setDataSource(myCartData);
            cartAdapter = new CartAdapter(getActivity(), myCartData.storeData(), true);
            recycler_cart.setAdapter(cartAdapter);


            if (myCartData != null) {
                if (myCartData.storeData().size() != 0) {

                    Common.CURRENT_CART = myCartData.storeData();
                    Common.TOTAL_CART_PRICE = myCartData.TotalPrice();

                    if (getContext() != null)
                        total_price.setText(new StringBuilder().append(Common.getDecimalNumber(Common.TOTAL_CART_PRICE)).append(" ").append(getString(R.string.currency)));

                    layout_checkOut.setVisibility(View.VISIBLE);


                    btn_next.setOnClickListener(view1 -> {

                        loadingDialog.showProgressBar(getActivity(), false);

                        MyApolloClient.getApollowClientAuthorization().query(MyCartQuery.builder().build())
                                .enqueue(new ApolloCall.Callback<MyCartQuery.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<MyCartQuery.Data> response) {
                                        MyCartQuery.MyCart myCart = response.data().CartItemQuery().myCart();

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {


                                                if (myCart.status() == 200) {


                                                    List<Boolean> isAvailableStores = new ArrayList<>();

                                                    for (MyCartQuery.StoreDatum stores : myCart.storeData()) {
                                                        stores.TotalPrice();

                                                        isAvailableStores.add(stores.Store().isAvailable());

                                                        if (stores.Store().orderMinPrice()!=null)
                                                        {
                                                            if (stores.TotalPrice()  < stores.Store().orderMinPrice())
                                                            {
                                                                loadingDialog.hideProgressBar();
                                                                Toasty.info(getActivity() , new StringBuilder().append(" طلبك من المتجر ").append(stores.Store().name()).append(" لا يتعدي الحد المسموح لاتمام عملية الشراء ")).show();
//                                                                break;
                                                                return;
                                                            }
                                                        }

                                                    }
                                                    loadingDialog.hideProgressBar();


                                                    if (isAvailableStores.contains(false)) {

                                                        btn_next.setBackgroundResource(R.drawable.round_button_gray);
                                                        btn_next.setEnabled(false);
                                                        cartPresenter.getMyCart();

                                                        loadingDialog.hideProgressBar();

                                                    } else if (myCart.TotalPrice() > Common.BASE_MAX_PRICE) {

                                                        loadingDialog.hideProgressBar();
                                                    } else {

                                                        getCost(myCart);

                                                    }

                                                }


//                                                 else if (myCart.status() == 404) {
//                                                } else {
//                                                }

                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadingDialog.hideProgressBar();
                                            }
                                        });
                                    }
                                });


                    });
                }
            }
            cartAdapter.setCallBackUpdateCartItemsListener((itemId, amount) -> {
                cartPresenter.updateCartItems(itemId, amount);
            });


            List<Boolean> isAvailableStores = new ArrayList<>();

            for (MyCartQuery.StoreDatum stores : myCartData.storeData()) {
                isAvailableStores.add(stores.Store().isAvailable());
            }

            if (isAvailableStores.contains(false)) {

                btn_next.setBackgroundResource(R.drawable.round_button_gray);
                btn_next.setEnabled(false);
            } else {
                btn_next.setBackgroundResource(R.drawable.round_main_button);
                btn_next.setEnabled(true);
            }


        });
    }

    private void getCost(MyCartQuery.MyCart myCart) {


//
//        MyApolloClient.getApolloClient().query(GeneralOptionQuery.builder().filter(GeneralOptionFilter.builder().name(GeneralOptionNameEnum.DELIVERYCOST).build()).build())
//                .enqueue(new ApolloCall.Callback<GeneralOptionQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<GeneralOptionQuery.Data> response) {
//
//                        if (response.data().GeneralOptionQuery().getOne().status() ==200){
//
//                            Common.DELIVERY_COST = response.data().GeneralOptionQuery().getOne().data().value();
//
//                            btn_next.setBackgroundResource(R.drawable.round_main_button);
//                            btn_next.setEnabled(true);
//                            Common.CURRENT_CART = myCart.storeData();
//                            Intent intent = new Intent(getActivity(), CheckOutActivity.class);
//                            intent.putExtra(Constants.EXTRA_TOTAL_CART, myCart.TotalPrice());
//                            startActivity(intent);
//
//                            loadingDialog.hideProgressBar();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//
//                    }
//                });

        MyApolloClient.getApollowClientAuthorization().query(GeneralOptionAllQuery.builder().build())
                .enqueue(new ApolloCall.Callback<GeneralOptionAllQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GeneralOptionAllQuery.Data> response) {

                        for (GeneralOptionAllQuery.Data1 option : response.data().GeneralOptionQuery().getAll().data()) {

                            if (option.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.DELIVERYCOST.rawValue())) {

                                Common.DELIVERY_COST = option.value();

                            } else if (option.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.MINDELIVERYCOST.rawValue())) {

                                Common.MINDELIVERY_COST = option.value();
                            }
                        }


                        btn_next.setBackgroundResource(R.drawable.round_main_button);
                        btn_next.setEnabled(true);
                        Common.CURRENT_CART = myCart.storeData();
                        Intent intent = new Intent(getActivity(), CheckOutActivity.class);
                        intent.putExtra(Constants.EXTRA_TOTAL_CART, myCart.TotalPrice());
                        startActivity(intent);

                        loadingDialog.hideProgressBar();
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });


//        MyApolloClient.getApolloClient().query(GeneralOptionQuery.builder().filter(GeneralOptionFilter.builder().name(GeneralOptionNameEnum.DELIVERYCOST).build()).build())
//                .enqueue(new ApolloCall.Callback<GeneralOptionQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<GeneralOptionQuery.Data> response) {
//
//                        if (response.data().GeneralOptionQuery().getOne().status() == 200) {
//
//                            Common.DELIVERY_COST = response.data().GeneralOptionQuery().getOne().data().value();
//
//                            btn_next.setBackgroundResource(R.drawable.round_main_button);
//                            btn_next.setEnabled(true);
//                            Common.CURRENT_CART = myCart.storeData();
//                            Intent intent = new Intent(getActivity(), CheckOutActivity.class);
//                            intent.putExtra(Constants.EXTRA_TOTAL_CART, myCart.TotalPrice());
//                            startActivity(intent);
//
//                            loadingDialog.hideProgressBar();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//
//                    }
//                });


    }

    @Override
    public void setPlaceholder() {
        handler.post(new Runnable() {
            @Override
            public void run() {


                showPlaceHolder();

                Common.TOTAL_CART_PRICE = 0;
                Common.TOTAL_CART_AMOUNT = 0;

            }
        });
    }

    private void showPlaceHolder() {

        if (getActivity() != null) {

            layout_placeholder.setVisibility(View.VISIBLE);
            recycler_cart.setVisibility(View.GONE);
            layout_checkOut.setVisibility(View.GONE);
        }

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

        handler.post(() -> loading.setVisibility(View.GONE));
    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void calculatePrice(CalculatePriceEvent event) {
//        if (event != null) {


        if (event.isSuccess()) {

            calculateCartTotalPrice(event.getProductId(), event.getAmount(), event.getStorePrice());

        } else {
        }
//            if (event.getProductId() != null) {
//            } else {
//                //double total = Double.parseDouble(total_price.getText().toString());
//                totalPrice -= event.getTotalProductPrice();
//                Common.TOTAL_CART_PRICE -= event.getTotalProductPrice();
//                total_price.setText(new StringBuilder().append(Common.TOTAL_CART_PRICE).append(" ").append(getString(R.string.currency)));
//
//                if (totalPrice <= 0) {
//                    showPlaceHolder();
//                    loading.setVisibility(View.GONE);
//                    Common.TOTAL_CART_PRICE = 0;
//                    Common.TOTAL_CART_AMOUNT = 0;
//                }
//
//            }
//        } else {
//
//        }


    }

    private void calculateCartTotalPrice(String productId, double amount, double storePrice) {


        cartPresenter.updateCartItems(productId, amount);

        totalPrice += storePrice;
        Common.TOTAL_CART_PRICE += storePrice;
        Common.TOTAL_CART_AMOUNT += amount;

        total_price.setText(new StringBuilder().append(Common.getDecimalNumber(Common.TOTAL_CART_PRICE)).append(" ").append(getString(R.string.currency)));


        Common.LOG("TOTAL FROM CART FRAGMENT : >> " + Common.TOTAL_CART_PRICE);
        Common.LOG("storePrice : >> " + storePrice);
        Common.LOG("amount : >> " + amount);

        if (Common.TOTAL_CART_PRICE <= 0.1f) {
            showPlaceHolder();
            loading.setVisibility(View.GONE);
            Common.TOTAL_CART_PRICE = 0;
            Common.TOTAL_CART_AMOUNT = 0;
        }


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
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
//        Common.isFirstTimeGetCartCount = true;
//        Common.LOG("Hello onStop");
        Common.GetCartItemsCount(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        recycler_cart.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        layout_checkOut.setVisibility(View.GONE);

        cartPresenter.getMyCart();
    }
}


