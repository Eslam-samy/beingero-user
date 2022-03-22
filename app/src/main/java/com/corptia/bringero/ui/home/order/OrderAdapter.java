package com.corptia.bringero.ui.home.order;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseViewHolder;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corptia.bringero.Common.Constants.VIEW_TYPE_ITEM;
import static com.corptia.bringero.Common.Constants.VIEW_TYPE_LOADING;

public class OrderAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private boolean isLoaderVisible = false;

    Context context;
    List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderList = new ArrayList<>();

    IOnRecyclerViewClickListener clickListener;
    private boolean clicked;

    //Fast taps (clicks) on RecyclerView opens multiple Orders Activity
    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 1000;

    public void setClickListener(IOnRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public OrderAdapter(Context context, List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderList) {
        this.context = context;
        if (deliveryOrderList != null)
            this.deliveryOrderList = deliveryOrderList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_ITEM:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return deliveryOrderList != null ? deliveryOrderList.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.txt_date_order)
        TextView txt_date_order;
        @BindView(R.id.txt_store)
        TextView stores;
        @BindView(R.id.txt_products)
        TextView products;
        @BindView(R.id.txt_total_price)
        TextView txt_total_price;
        @BindView(R.id.txt_status)
        TextView txt_status;
        @BindView(R.id.txt_order_id)
        TextView txt_order_id;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            DeliveryOrdersQuery.DeliveryOrderDatum orderDatum = deliveryOrderList.get(position);


            if (orderDatum != null) {

                String uriString = "@string/order_status_" + orderDatum.status().rawValue().toLowerCase();
                int stringResource = context.getResources().getIdentifier(uriString, null, context.getPackageName());
                String uriColor = "@color/status_" + orderDatum.status().rawValue().toLowerCase();
                int colorResource = context.getResources().getIdentifier(uriColor, null, context.getPackageName());
                int color = context.getResources().getColor(colorResource);


                String uri = "@drawable/background_order_" + orderDatum.status().rawValue().toLowerCase();
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);
                if (imageResource != 0 && stringResource != 0) {
                    txt_status.setText("" + context.getResources().getString(stringResource));
                    txt_status.setBackground(res);
                    txt_status.setTextColor(color);
                    txt_date_order.setText(orderDatum.createdAt().toString());
                    txt_order_id.setText(new StringBuilder(context.getString(R.string.order_id)).append(" #").append(orderDatum.serial()));


                    double totalPrice = orderDatum.SubTotal() + orderDatum.deliveryCost();
                    txt_total_price.setText(new StringBuilder().append(Common.getDecimalNumber(totalPrice)).append(" ").append(context.getString(R.string.currency)));
                } else {
                    txt_status.setText(orderDatum.status().rawValue().toLowerCase());
                    txt_date_order.setText(orderDatum.createdAt().toString());
                    txt_order_id.setText(new StringBuilder(context.getString(R.string.order_id)).append(" #").append(orderDatum.serial()));
                }

//
//


//        holder.txt_total_price.setText(orderDatum.);

                stores.setText(
                        new StringBuilder().append(orderDatum.StoresCount())
                                .append(" ")
                                .append(orderDatum.StoresCount() == 1 ? context.getString(R.string.store) : context.getString(R.string.stores))
                );
                products.setText(
                        new StringBuilder().append(orderDatum.ItemsCount())
                                .append(" ")
                                .append(orderDatum.ItemsCount() == 1 ? context.getString(R.string.product) : context.getString(R.string.products)));
                if (clickListener != null)
                    itemView.setOnClickListener(view -> {

                        if (clicked) {
                            return;
                        }
                        clicked = true;
                        view.postDelayed(() -> clicked = false, 500);

                        clickListener.onClick(view, position);

                    });
            } else {
            }

        }
    }

    public String getIdOrder(int position) {
        return deliveryOrderList.get(position)._id();
    }

    public int getSerialOrder(int position) {
        return deliveryOrderList.get(position).serial();
    }

    //This For Loading
    public void addItems(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderList) {
        this.deliveryOrderList.addAll(deliveryOrderList);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        deliveryOrderList.add(null);
        notifyItemInserted(deliveryOrderList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = deliveryOrderList.size() - 1;
        DeliveryOrdersQuery.DeliveryOrderDatum item = getItem(position);
        if (item == null) {
//            Log.d("HAZEM", " Done Delete ");
            deliveryOrderList.remove(position);
            notifyItemRemoved(position);
        }

    }

    public DeliveryOrdersQuery.DeliveryOrderDatum getItem(int position) {
        return deliveryOrderList.get(position);
    }

    //-----------------

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == deliveryOrderList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }
}
