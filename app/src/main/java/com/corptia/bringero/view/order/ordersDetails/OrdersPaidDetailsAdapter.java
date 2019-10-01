package com.corptia.bringero.view.order.ordersDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.PicassoUtils;
import com.corptia.bringero.Utils.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;
import com.corptia.bringero.model.CartModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersPaidDetailsAdapter extends RecyclerView.Adapter<OrdersPaidDetailsAdapter.ViewHolder> {

    Context context;
    List<DeliveryOneOrderQuery.BuyingOrderResponseDatum> orderResponseData  = new ArrayList<>();
    OrdersPaidDetailsItemsAdapter adapterItems ;

    public OrdersPaidDetailsAdapter(Context context, @Nullable List<DeliveryOneOrderQuery.BuyingOrderResponseDatum> orderResponseData) {
        this.context = context;
        this.orderResponseData = orderResponseData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_orders_paid_details_header,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryOneOrderQuery.BuyingOrderResponseDatum order = orderResponseData.get(position);

        //PicassoUtils.setImage("",holder.image_store);
        holder.txt_status.setText(order.status().rawValue());
        holder.txt_name_store.setText(order.StoreResponse().data().name());

        adapterItems = new OrdersPaidDetailsItemsAdapter(context , order.ItemsResponse().data());
        holder.recycler_items.setLayoutManager(new LinearLayoutManager(context));
        holder.recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,context)));
        holder.recycler_items.setAdapter(adapterItems);
        holder.recycler_items.setNestedScrollingEnabled(false);


    }

    @Override
    public int getItemCount() {
        return orderResponseData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recycler_items)
        RecyclerView recycler_items;
        @BindView(R.id.txt_name_store)
        TextView txt_name_store;
        @BindView(R.id.image_store)
        ImageView image_store;
        @BindView(R.id.txt_status)
        TextView txt_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }
    }
}
