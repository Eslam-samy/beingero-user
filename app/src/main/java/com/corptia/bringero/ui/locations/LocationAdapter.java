package com.corptia.bringero.ui.locations;

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
import com.corptia.bringero.model.CurrentDeliveryAddress;
import com.corptia.bringero.model.DeliveryAddresses;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    Context context ;
    List<DeliveryAddresses> deliveryAddressesList = new ArrayList<>();

    IOnRecyclerViewClickListener clickListener,clickListenerUpdate ;

    public int selectedPosition;
    int tempCurrentPosition;

    public void setClickListenerUpdate(IOnRecyclerViewClickListener clickListenerUpdate) {
        this.clickListenerUpdate = clickListenerUpdate;
    }

    public void setClickListener(IOnRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public LocationAdapter(Context context, List<DeliveryAddresses> deliveryAddressesList ) {
        this.context = context;
        this.deliveryAddressesList = new ArrayList<>(deliveryAddressesList) ;
        selectedPosition = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_location, parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DeliveryAddresses address = deliveryAddressesList.get(position);

        if (address!=null) {

            holder.txt_address_name.setText(address.getName());
//            holder.txt_street.setText(new StringBuilder()
//                    .append(address.getRegion())
//                    .append(" - ")
//                    .append(address.getStreet().length() > 30 ? address.getStreet().substring(0,30) : address.getStreet()));


            if (selectedPosition == -1) {
                if (Common.CURRENT_USER.getCurrentDeliveryAddress().getId().equals(address.getId())) {
                    holder.img_selected.setVisibility(View.VISIBLE);
                    selectedPosition = position;
                    tempCurrentPosition = position;

                    //TODO IF UPDATE LOCATION ( CURRENT LOCATION )
                    CurrentDeliveryAddress currentDeliveryAddress = new CurrentDeliveryAddress();
                    currentDeliveryAddress.setId(address.getId());
                    currentDeliveryAddress.setBuilding(address.getBuilding());
                    currentDeliveryAddress.setRegion(address.getRegion());
                    currentDeliveryAddress.setName(address.getName());
                    currentDeliveryAddress.setStreet(address.getStreet());
                    currentDeliveryAddress.setFlatType(address.getFlatType());
                    currentDeliveryAddress.setFloor(address.getFloor());
                    currentDeliveryAddress.setFlat(address.getFlat());
                    currentDeliveryAddress.setLocation(address.getLocation());

                    Common.CURRENT_USER.setCurrentDeliveryAddress(currentDeliveryAddress);


                } else
                    holder.img_selected.setVisibility(View.INVISIBLE);
            } else {
                if (selectedPosition == position) {
                    holder.img_selected.setVisibility(View.VISIBLE);
                } else holder.img_selected.setVisibility(View.INVISIBLE);
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (clickListener != null) {
                        clickListener.onClick(view, position);
                    }
                }
            });

            holder.btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListenerUpdate != null) {
                        clickListenerUpdate.onClick(view, position);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return deliveryAddressesList.size();
    }

    public void removeItem(int position) {

        deliveryAddressesList.remove(position);
        notifyItemRemoved(position);


    }

    public void restoreItem(DeliveryAddresses item, int position) {

//        Common.CURRENT_USER.getDeliveryAddressesList().add(position,item);
        deliveryAddressesList.add(position, item);
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_address_name)
        TextView txt_address_name;
        @BindView(R.id.btn_update)
        ImageView btn_update;
        @BindView(R.id.img_selected)
        ImageView img_selected;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);

        }
    }

    public  DeliveryAddresses getItems(int position)
    {
        return deliveryAddressesList.get(position);
    }

    public void selectCurrentLocation(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }


}
