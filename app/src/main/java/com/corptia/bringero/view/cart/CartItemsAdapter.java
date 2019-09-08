package com.corptia.bringero.view.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.R;
import com.corptia.bringero.model.CartItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ViewHolder> {

    Context context;
    List<CartItems> cartItems = new ArrayList<>();

    public CartItemsAdapter(Context context, List<CartItems> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_cart_shop,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //@BindView(R.id.)

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
