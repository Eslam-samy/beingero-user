package com.corptia.bringero.view.cart;


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

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.view.cart.Adapter.CartAdapter;
import com.corptia.bringero.view.home.HomeActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartFragment extends Fragment implements CartContract.CartView {

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    //Adapters
    private CartAdapter cartAdapter;
    @BindView(R.id.btn_next)
    Button btn_next;

    Handler handler = new Handler();
    CartPresenter cartPresenter = new CartPresenter(this);

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);
        cartPresenter.getMyCart();


        return view;
    }

    @Override
    public void setMyCart(List<MyCartQuery.StoreDatum> myCartData) {


        handler.post(() -> {

            //stickyRecyclerView.setDataSource(myCartData);
            cartAdapter = new CartAdapter(getActivity(), myCartData , true);
            recycler_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
            recycler_cart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, getActivity())));
            recycler_cart.setAdapter(cartAdapter);


            if (myCartData!= null)
            {
                if (myCartData.size()!=0) {
                    Common.CURRENT_CART = myCartData;

                    btn_next.setOnClickListener(view1 -> {
                        HomeActivity.navController.navigate(R.id.action_nav_cart_to_checkOutFragment);
                        HomeActivity.bottomNavigationView.setVisibility(View.GONE);
                        HomeActivity.fab.hide();
                    });
                }
            }
            cartAdapter.setCallBackUpdateCartItemsListener((itemId, amount) -> {
                cartPresenter.updateCartItems(itemId , amount);
            });


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
