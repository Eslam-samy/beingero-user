package com.corptia.bringero.view.order.main.current;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.R;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;
import com.corptia.bringero.model.CartItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.ViewHolder> {

    Context context;
    List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderList = new ArrayList<>();

    public CurrentOrderAdapter(Context context, List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderList) {
        this.context = context;
        this.deliveryOrderList = deliveryOrderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order_current, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentOrderAdapter.ViewHolder holder, int position) {

        DeliveryOrdersQuery.DeliveryOrderDatum orderDatum = deliveryOrderList.get(position);

        holder.txt_date_order.setText(orderDatum.createdAt().toString());
        holder.txt_status.setText(orderDatum.status().rawValue());
        holder.txt_order_id.setText(new StringBuilder(context.getString(R.string.order_id)).append(" #").append(orderDatum.serial()));
    }

    @Override
    public int getItemCount() {
        return deliveryOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_store)
        ImageView image_store;
        @BindView(R.id.txt_date_order)
        TextView txt_date_order;
        @BindView(R.id.txt_content_count)
        TextView txt_content_count;
        @BindView(R.id.txt_total_price)
        TextView txt_total_price;
        @BindView(R.id.txt_status)
        TextView txt_status;
        @BindView(R.id.txt_order_id)
        TextView txt_order_id;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            image_store.setVisibility(View.GONE);
        }
    }
}
