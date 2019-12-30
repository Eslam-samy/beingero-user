package com.corptia.bringero.ui.order.ordersDetails;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.order.storeDetail.StoreDetailsActivity;
import com.corptia.bringero.ui.storesDetail.StoreDetailActivity;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersPaidDetailsAdapter extends RecyclerView.Adapter<OrdersPaidDetailsAdapter.ViewHolder> {

    Context context;
    List<DeliveryOneOrderQuery.BuyingOrderResponseDatum> orderResponseData = new ArrayList<>();
//    OrdersPaidDetailsItemsAdapter adapterItems ;

    public OrdersPaidDetailsAdapter(Context context, @Nullable List<DeliveryOneOrderQuery.BuyingOrderResponseDatum> orderResponseData) {
        this.context = context;
        this.orderResponseData = orderResponseData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_orders_paid_details_header, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryOneOrderQuery.BuyingOrderResponseDatum order = orderResponseData.get(position);

        //PicassoUtils.setImage("",holder.image_store);
//        holder.txt_status.setText(order.status().rawValue());

        holder.txt_name_store.setText(order.StoreResponse().data().name());
        holder.txt_total_products.setText("" + order.ItemsResponse().data().size());
        holder.txt_total_price.setText(new StringBuilder().append(order.TotalPrice()).append(" ").append(context.getString(R.string.currency)));

        if (order.StoreResponse().data().ImageResponse().data() != null)
            PicassoUtils.setImage(Common.BASE_URL_IMAGE + order.StoreResponse().data().ImageResponse().data().name(), holder.image_store);
        else
            PicassoUtils.setImage(holder.image_store);

        if (Common.CURRENT_USER!=null)
            if (Common.CURRENT_USER.getLanguage().equalsIgnoreCase("ar"))
                holder.img_arrow.setImageResource(R.drawable.ic_arrow_rtl);
//
//        adapterItems = new OrdersPaidDetailsItemsAdapter(context , order.ItemsResponse().data());
//        holder.recycler_items.setLayoutManager(new LinearLayoutManager(context));
//        holder.recycler_items.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15,context)));
//        holder.recycler_items.setAdapter(adapterItems);
//        holder.recycler_items.setNestedScrollingEnabled(false);

        holder.itemView.setOnClickListener(view -> {

            Intent intent = new Intent(context, StoreDetailsActivity.class);
            intent.putExtra(Constants.BUYING_ORDER_ID, order._id());
            intent.putExtra(Constants.EXTRA_STORE_NAME, order.StoreResponse().data().name());
            if (order.StoreResponse().data().ImageResponse().status()==200)
            intent.putExtra(Constants.EXTRA_STORE_IMAGE, order.StoreResponse().data().ImageResponse().data().name());
            else
            intent.putExtra(Constants.EXTRA_STORE_IMAGE, "");
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return orderResponseData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //        @BindView(R.id.recycler_items)
//        RecyclerView recycler_items;
        @BindView(R.id.txt_name_store)
        TextView txt_name_store;
        @BindView(R.id.image_store)
        ImageView image_store;
        @BindView(R.id.img_arrow)
        ImageView img_arrow;
        @BindView(R.id.txt_total_price)
        TextView txt_total_price;
        @BindView(R.id.txt_total_products)
        TextView txt_total_products;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
