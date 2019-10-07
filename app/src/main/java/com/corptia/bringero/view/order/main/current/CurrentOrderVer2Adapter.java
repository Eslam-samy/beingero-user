package com.corptia.bringero.view.order.main.current;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.model.CartModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentOrderVer2Adapter extends RecyclerView.Adapter<CurrentOrderVer2Adapter.ViewHolder> {

    Context context;
    List<CartModel> cartItems = new ArrayList<>();
    CurrentOrderAdapter adapter;

    public CurrentOrderVer2Adapter(Context context, List<CartModel> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order_current_ver2, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //adapter = new CurrentOrderAdapter(context , cartItems.get(position).getCartItems());
        holder.recycler_brands.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recycler_brands)
        RecyclerView recycler_brands;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            recycler_brands.setLayoutManager(new LinearLayoutManager(context));
            recycler_brands.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, context)));
        }


    }
}
