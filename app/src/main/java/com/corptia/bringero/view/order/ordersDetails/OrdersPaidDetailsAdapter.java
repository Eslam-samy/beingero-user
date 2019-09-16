package com.corptia.bringero.view.order.ordersDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.model.CartModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersPaidDetailsAdapter extends RecyclerView.Adapter<OrdersPaidDetailsAdapter.ViewHolder> {

    Context context;
    List<CartModel> cartModels  = new ArrayList<>();
    OrdersPaidDetailsItemsAdapter adapterItems ;

    public OrdersPaidDetailsAdapter(Context context, List<CartModel> cartModels) {
        this.context = context;
        this.cartModels = cartModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_orders_paid_details_header,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartModel cartModel = cartModels.get(position);
        adapterItems = new OrdersPaidDetailsItemsAdapter(context , cartModel.getCartItems());


        holder.recycler_items.setLayoutManager(new LinearLayoutManager(context));
        holder.recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,context)));
        holder.recycler_items.setAdapter(adapterItems);
        holder.recycler_items.setNestedScrollingEnabled(false);


    }

    @Override
    public int getItemCount() {
        return cartModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recycler_items)
        RecyclerView recycler_items;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }
    }
}
