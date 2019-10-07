package com.corptia.bringero.view.order.main.lastOrder;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.model.CartItems;
import com.corptia.bringero.model.CartModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LastOrderFragment extends Fragment {


    LastOrderVer2Adapter adapter;
    @BindView(R.id.recycler_last_order)
    RecyclerView recycler_last_order;

    public LastOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_last_order, container, false);

        ButterKnife.bind(this , view);



        recycler_last_order.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_last_order.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,getActivity())));

        List<CartModel> storeTypes = new ArrayList<>();

        List<CartItems> cartItems = new ArrayList<>();
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        cartItems.add(new CartItems("Asd"));
        storeTypes.add(new CartModel("", cartItems));

        List<CartItems> cartItems2 = new ArrayList<>();

        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        cartItems2.add(new CartItems("Asd"));
        storeTypes.add(new CartModel("", cartItems2));

        List<CartItems> cartItems3 = new ArrayList<>();

        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        cartItems3.add(new CartItems("Asd"));
        storeTypes.add(new CartModel("", cartItems));


        adapter = new LastOrderVer2Adapter(getActivity() ,storeTypes );

       recycler_last_order.setAdapter(adapter);

        return view;
    }

}
