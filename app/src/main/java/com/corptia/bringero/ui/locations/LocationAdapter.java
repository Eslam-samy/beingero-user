package com.corptia.bringero.ui.locations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.MeQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    Context context ;
    List<MeQuery.DeliveryAddress> deliveryAddressesList = new ArrayList<>();

    IOnRecyclerViewClickListener clickListener ;

    public void setClickListener(IOnRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public LocationAdapter(Context context, List<MeQuery.DeliveryAddress> deliveryAddressesList ) {
        this.context = context;
        this.deliveryAddressesList = new ArrayList<>(deliveryAddressesList) ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_location, parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MeQuery.DeliveryAddress address = deliveryAddressesList.get(position);
        holder.txt_address_name.setText(address.name());
        holder.txt_street.setText(new StringBuilder().append(address.region()).append(" - ").append(address.street())) ;


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
        return deliveryAddressesList.size();
    }

    public void removeItem(int position) {

        deliveryAddressesList.remove(position);
        notifyItemRemoved(position);


    }

    public void restoreItem(MeQuery.DeliveryAddress item, int position) {

        deliveryAddressesList.add(position, item);
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_address_name)
        TextView txt_address_name;
        @BindView(R.id.txt_street)
        TextView txt_street;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);

        }
    }

    public  MeQuery.DeliveryAddress getItems(int position)
    {
        return deliveryAddressesList.get(position);
    }
}
