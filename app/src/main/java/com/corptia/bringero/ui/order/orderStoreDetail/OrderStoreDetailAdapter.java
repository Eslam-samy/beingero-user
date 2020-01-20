package com.corptia.bringero.ui.order.orderStoreDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.SingleOrderQuery;
import com.corptia.bringero.utils.PicassoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderStoreDetailAdapter extends RecyclerView.Adapter<OrderStoreDetailAdapter.ViewHolder> {

    Context context;
    List<SingleOrderQuery.ItemsDatum> itemsList = new ArrayList<>();

    public OrderStoreDetailAdapter(Context context, List<SingleOrderQuery.ItemsDatum> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_orders_details_items , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SingleOrderQuery.ItemsDatum item = itemsList.get(position);

        //holder.image_product;
        if (item.PricingProductResponse().data().ProductResponse().data().ImageResponse().status() == 200)
            PicassoUtils.setImage(Common.BASE_URL_IMAGE + item.PricingProductResponse().data().ProductResponse().data().ImageResponse().data().name() ,
                    holder.image_product);

        holder.txt_name_product.setText(item.productName().length() > 40 ? item.productName().substring(0,40) +"..." : item.productName());

        holder.txt_price.setText(new StringBuilder().append(Common.getDecimalNumber(item.storePrice())).append(" ").append(context.getString(R.string.currency)));

        holder.txt_amount.setText("x"+item.amount());

        holder.txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(item.amount()*item.storePrice())).append(" ").append(context.getString(R.string.currency)));

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_product)
        ImageView image_product;
        @BindView(R.id.txt_name_product)
        TextView txt_name_product;
        @BindView(R.id.txt_price)
        TextView txt_price;
        @BindView(R.id.txt_amount)
        TextView txt_amount;
        @BindView(R.id.txt_total_price)
        TextView txt_total_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this , itemView);

        }
    }
}
