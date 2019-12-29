package com.corptia.bringero.ui.order.main.current;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

public class CurrentOrderAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private boolean isLoaderVisible = false;

    Context context;
    List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderList = new ArrayList<>();

    IOnRecyclerViewClickListener clickListener;

    public void setClickListener(IOnRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public CurrentOrderAdapter(Context context, List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderList) {
        this.context = context;
        if (deliveryOrderList != null)
            this.deliveryOrderList = deliveryOrderList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_ITEM:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_order_current, parent, false));
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

                String uri = "@drawable/background_order_" + orderDatum.status().rawValue().toLowerCase();
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);

                txt_status.setText(context.getResources().getString(stringResource));
                txt_status.setBackground(res);

                txt_date_order.setText(orderDatum.createdAt().toString());
                txt_order_id.setText(new StringBuilder(context.getString(R.string.order_id)).append(" #").append(orderDatum.serial()));




//
//


//        holder.txt_total_price.setText(orderDatum.);

                txt_content_count.setText(
                        new StringBuilder().append(orderDatum.StoresCount())
                                .append(" ")
                                .append(context.getString(R.string.stores))
                                .append(" , ")
                                .append(orderDatum.ItemsCount())
                                .append(" ")
                                .append(context.getString(R.string.products)));


                if (clickListener != null)
                    itemView.setOnClickListener(view -> clickListener.onClick(view, position));
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
            Log.d("HAZEM", " Done Delete ");
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
