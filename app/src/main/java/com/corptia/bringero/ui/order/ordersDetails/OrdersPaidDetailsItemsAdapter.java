package com.corptia.bringero.ui.order.ordersDetails;

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
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersPaidDetailsItemsAdapter extends RecyclerView.Adapter<OrdersPaidDetailsItemsAdapter.ViewHolder> {

    Context context;
    List<DeliveryOneOrderQuery.Data6> orderItems;

    public OrdersPaidDetailsItemsAdapter(Context context, @Nullable List<DeliveryOneOrderQuery.Data6> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_orders_paid_details_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryOneOrderQuery.Data6 items = orderItems.get(position);

        holder.txt_name_product.setText(items.productName());
        holder.txt_price.setText(new StringBuilder().append(items.storePrice()).append(context.getString(R.string.currency)));

        PicassoUtils.setImage(Common.BASE_URL_IMAGE + items.PricingProductResponse().data().ProductResponse().data().ImageResponse().data().name() ,
                holder.image_product);

//        if (items..StoreResponse().data().ImageResponse().data()!=null)
//            PicassoUtils.setImage(Common.BASE_URL_IMAGE + items.StoreResponse().data().ImageResponse().data().name() , holder.image_store);
//        else
//            PicassoUtils.setImage( holder.image_store);

    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_name_product)
        TextView txt_name_product;
        @BindView(R.id.txt_price)
        TextView txt_price;
        @BindView(R.id.image_product)
        ImageView image_product;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
