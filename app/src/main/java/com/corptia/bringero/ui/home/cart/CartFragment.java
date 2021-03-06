package com.corptia.bringero.ui.home.cart;


import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.databinding.FragmentCartBinding;
import com.corptia.bringero.graphql.GeneralOptionAllQuery;
import com.corptia.bringero.graphql.GeneralOptionQuery;
import com.corptia.bringero.graphql.RemoveCartItemMutation;
import com.corptia.bringero.model.MyCart;
import com.corptia.bringero.type.GeneralOptionFilter;
import com.corptia.bringero.type.GeneralOptionNameEnum;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.customAppBar.BarClicks;
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

public class CartFragment extends Fragment implements CartContract.CartView, BarClicks {

    FragmentCartBinding binding;
    Handler handler = new Handler();
    CartPresenter cartPresenter = new CartPresenter(this);
    CartAdapter cartAdapter;
    double totalPrice = 0;

    //For Placeholder


    CustomLoading loadingDialog;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ButterKnife.bind(this, view);

        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerCart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(10, getActivity())));
        binding.toolBar.setClicks(this);
        initPlaceHolder();

        loadingDialog = new CustomLoading(getActivity(), true);

        return view;
    }

    private void initPlaceHolder() {

        binding.recyclerCart.setVisibility(View.GONE);
        binding.placeHolder.btn2.setVisibility(View.GONE);
        binding.placeHolder.imgPlaceholder.setImageResource(R.drawable.ic_placeholder_cart);

        binding.placeHolder.btn2.setText(getString(R.string.menu_home));


        binding.placeHolder.txtPlaceholderTitle.setText(getString(R.string.placeholder_title_cart));
        binding.placeHolder.txtPlaceholderDec.setText(getString(R.string.placeholder_dec_cart));

        binding.placeHolder.getRoot().setVisibility(View.GONE);

    }

    @Override
    public void setMyCart(MyCartQuery.MyCart myCartData) {

        handler.post(() -> {

            binding.placeHolder.getRoot().setVisibility(View.GONE);
            binding.recyclerCart.setVisibility(View.VISIBLE);
            binding.layoutCheckOut.setVisibility(View.VISIBLE);


            totalPrice = myCartData.TotalPrice();

            //stickyRecyclerView.setDataSource(myCartData);
            cartAdapter = new CartAdapter(getActivity(), myCartData.storeData(), true);
            cartAdapter.setRemoveAllStoreItemsListener((storeItems, position, totalPrice, myCartList) -> {
                CustomLoading customLoading = new CustomLoading(getContext(), true);
                customLoading.showProgressBar(getContext(), false);

                for (MyCartQuery.Item item : storeItems) {

                    Boolean isLast = storeItems.indexOf(item) == storeItems.size() - 1;
                    String cartId = item._id();
                    deleteCartItems(cartId, position, isLast, totalPrice, customLoading, myCartList);
                }
            });

            binding.recyclerCart.setAdapter(cartAdapter);


            if (myCartData != null) {
                if (myCartData.storeData().size() != 0) {

                    Common.CURRENT_CART = myCartData.storeData();
                    Common.TOTAL_CART_PRICE = myCartData.TotalPrice();

                    if (getContext() != null)
                        binding.btnNext.setText(getString(R.string.go_to_checkout) + " - " + new StringBuilder().append(Common.getDecimalNumber(Common.TOTAL_CART_PRICE)).append(" ").append(getString(R.string.currency)));

                    binding.layoutCheckOut.setVisibility(View.VISIBLE);


                    binding.btnNext.setOnClickListener(view1 -> {

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

                                                        if (stores.Store().orderMinPrice() != null) {
                                                            if (stores.TotalPrice() < stores.Store().orderMinPrice()) {
                                                                loadingDialog.hideProgressBar();
                                                                Toasty.info(getActivity(), new StringBuilder().append(" ???????? ???? ???????????? ").append(stores.Store().name()).append(" ???? ?????????? ???????? ?????????????? ???????????? ?????????? ???????????? ")).show();
//                                                                break;
                                                                return;
                                                            }
                                                        }

                                                    }
                                                    loadingDialog.hideProgressBar();


                                                    if (isAvailableStores.contains(false)) {

                                                        binding.btnNext.setBackgroundResource(R.drawable.round_button_gray);
                                                        binding.btnNext.setEnabled(false);
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

                binding.btnNext.setBackgroundResource(R.drawable.round_button_gray);
                binding.btnNext.setEnabled(false);
            } else {
                binding.btnNext.setBackgroundResource(R.drawable._6_gradient_rect);
                binding.btnNext.setEnabled(true);
            }


        });
    }

    public void deleteCartItems(String cartId, int position, Boolean isLast, double totalStorePrice, CustomLoading customLoading, List<MyCartQuery.StoreDatum> myCartList) {
        RemoveCartItemMutation removeCartItemMutation = RemoveCartItemMutation.builder()._id(cartId).build();

        MyApolloClient.getApollowClientAuthorization()
                .mutate(removeCartItemMutation)
                .enqueue(new ApolloCall.Callback<RemoveCartItemMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<RemoveCartItemMutation.Data> response) {

                        if (response.data().CartItemMutation().remove().status() == 200) {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isLast) {
                                        cartAdapter.notifyItemRemoved(position);
                                        Toast.makeText(getContext(), "Items Deleted !", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CalculatePriceEvent(true, cartId, -0, -totalStorePrice));
                                        myCartList.remove(position);
                                        cartAdapter.notifyItemRemoved(position);
                                        cartAdapter.notifyItemRangeChanged(position, myCartList.size());
                                        customLoading.hideProgressBar();
                                    }
                                }
                            });

                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

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
                            } else if (option.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.MAXADCOSTSTORECOUNT.rawValue())) {

                                Common.MAX_AD_COST_STORE = option.value();

                            } else if (option.name().rawValue().equalsIgnoreCase(GeneralOptionNameEnum.MINCOUPONSTORECOUNT.rawValue())) {

                                Common.MIN_COUPON_STORE = option.value();
                            }
                        }


                        binding.btnNext.setBackgroundResource(R.drawable._6_gradient_rect);
                        binding.btnNext.setEnabled(true);
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
        handler.post(() -> {


            showPlaceHolder();

            Common.TOTAL_CART_PRICE = 0;
            Common.TOTAL_CART_AMOUNT = 0;

        });
    }

    private void showPlaceHolder() {

        if (getActivity() != null) {

            binding.placeHolder.getRoot().setVisibility(View.VISIBLE);
            binding.recyclerCart.setVisibility(View.GONE);
            binding.layoutCheckOut.setVisibility(View.GONE);
        }

    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

        handler.post(() -> binding.loading.setVisibility(View.GONE));
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

        binding.btnNext.setText(getString(R.string.go_to_checkout) + " - " + new StringBuilder().append(Common.getDecimalNumber(Common.TOTAL_CART_PRICE)).append(" ").append(getString(R.string.currency)));


        Common.LOG("TOTAL FROM CART FRAGMENT : >> " + Common.TOTAL_CART_PRICE);
        Common.LOG("storePrice : >> " + storePrice);
        Common.LOG("amount : >> " + amount);

        if (Common.TOTAL_CART_PRICE <= 0.1f) {
            showPlaceHolder();
            binding.loading.setVisibility(View.GONE);
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
        binding.recyclerCart.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);
        binding.layoutCheckOut.setVisibility(View.GONE);

        cartPresenter.getMyCart();
    }

    @Override
    public void onBackClick() {
        Navigation.findNavController(getView()).popBackStack();

    }

    @Override
    public void onNotficationClick() {

    }

}


