package com.corptia.bringero.ui.location.deliveryLocation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.model.DeliveryAddresses;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectDeliveryLocationAdapter extends RecyclerView.Adapter<SelectDeliveryLocationAdapter.ViewHolder> {

    Context context;
    List<DeliveryAddresses> deliveryAddressList = new ArrayList<>();

    IOnRecyclerViewClickListener clickListener;

    public int selectedPosition;
    int tempCurrentPosition;

    public void setClickListener(IOnRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public SelectDeliveryLocationAdapter(Context context, List<DeliveryAddresses> deliveryAddressList) {
        this.context = context;
        this.deliveryAddressList = deliveryAddressList;
        selectedPosition = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart_delivery_location, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryAddresses address = deliveryAddressList.get(position);
        holder.txt_address.setText(address.getStreet());
        holder.txt_title_name_address.setText(address.getName());
        if (selectedPosition == -1) {
            if (Common.CURRENT_USER.getCurrentDeliveryAddress().getId().equals(address.getId())) {
                holder.image_correct.setImageResource(R.drawable.ic_checked_group);
                selectedPosition = position;
                tempCurrentPosition = position;
            } else
                holder.image_correct.setImageResource(R.drawable.ic_unchecked_group);
        } else {
            if (selectedPosition == position) {
                holder.image_correct.setImageResource(R.drawable.ic_checked_group);
            } else holder.image_correct.setImageResource(R.drawable.ic_unchecked_group);

        }

        holder.itemView.setOnClickListener(view -> {

            if (clickListener != null) {
                clickListener.onClick(view, position);
            }

        });

    }

    @Override
    public int getItemCount() {
        return deliveryAddressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_title_name_address)
        TextView txt_title_name_address;
        @BindView(R.id.txt_address)
        TextView txt_address;
        @BindView(R.id.image_correct)
        ImageView image_correct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public String getCurrentDeliveryAddressID() {
        if (selectedPosition == -1)
            return deliveryAddressList.get(0).getId();
        else
            return deliveryAddressList.get(selectedPosition).getId();
    }

    public void selectCurrentLocation(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public boolean isChangeLocation() {
        return tempCurrentPosition != selectedPosition;
    }
}
