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

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment implements CartContract.CartView {

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    CartAdapter cartAdapter;
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
            Log.d("HAZEM", "setMyCart: " + myCartData.size());
            cartAdapter = new CartAdapter(getActivity(), myCartData);
            recycler_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
            recycler_cart.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, getActivity())));
            recycler_cart.setAdapter(cartAdapter);
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
