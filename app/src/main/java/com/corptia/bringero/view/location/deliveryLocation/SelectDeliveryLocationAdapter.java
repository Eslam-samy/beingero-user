package com.corptia.bringero.view.location.deliveryLocation;

import android.content.Context;
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
import com.corptia.bringero.graphql.MeQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectDeliveryLocationAdapter extends RecyclerView.Adapter<SelectDeliveryLocationAdapter.ViewHolder> {

    Context context;
    List<MeQuery.DeliveryAddress> deliveryAddressList = new ArrayList<>();

    IOnRecyclerViewClickListener clickListener ;

    public void setClickListener(IOnRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public SelectDeliveryLocationAdapter(Context context, List<MeQuery.DeliveryAddress> deliveryAddressList) {
        this.context = context;
        this.deliveryAddressList = deliveryAddressList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart_delivery_location,parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MeQuery.DeliveryAddress address = deliveryAddressList.get(position);
        holder.txt_address.setText(address.street());
        holder.txt_title_name_address.setText(address.name());

        if(Common.CURRENT_USER.currentDeliveryAddress()._id().equals(address._id())){
            holder.image_correct.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clickListener!=null)
                {
                    clickListener.onClick(view , position);
                }


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

            ButterKnife.bind(this,itemView);
        }
    }

    public String getCurrentDeliveryAddressID ( int position){
        return deliveryAddressList.get(position)._id();
    }
}
