package com.corptia.bringero.view.cart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.model.CartModel;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<MyCartQuery.StoreDatum> myCartList =new ArrayList<>();
    CartItemsAdapter itemsAdapter;

    public CartAdapter(Context context, List<MyCartQuery.StoreDatum> cartModels) {
        this.context = context;
        this.myCartList = cartModels;
        Log.d("HAZEM", "setMyCart: " + cartModels.size());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_header,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyCartQuery.StoreDatum cartModel = myCartList.get(position);
        itemsAdapter = new CartItemsAdapter(context ,cartModel.Items());

        holder.recycler_items.setAdapter(itemsAdapter);
    }

    @Override
    public int getItemCount() {
        return myCartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recycler_items)
        RecyclerView recycler_items;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
            recycler_items.setLayoutManager(new LinearLayoutManager(context));
            recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,context)));
        }
    }
}
