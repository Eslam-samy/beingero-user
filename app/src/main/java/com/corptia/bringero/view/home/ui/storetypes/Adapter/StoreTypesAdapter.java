package com.corptia.bringero.view.home.ui.storetypes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.R;
import com.corptia.bringero.model.StoreTypes;
import com.corptia.bringero.view.brands.BrandsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreTypesAdapter extends RecyclerView.Adapter<StoreTypesAdapter.ViewHolder> {

    Context context;
    List<StoreTypes> storeTypesList = new ArrayList<>();

    public StoreTypesAdapter(Context context, List<StoreTypes> storeTypesList) {
        this.context = context;
        this.storeTypesList = storeTypesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_storetype, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StoreTypes storeTypes = storeTypesList.get(position);

        Picasso.get().load(storeTypes.getImg())
                .into(holder.image_storetype);

        holder.txt_name_storetype.setText("Data " + (position+1));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context , BrandsActivity.class));
            }
        });


    }

    @Override
    public int getItemCount() {
        return storeTypesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_storetype)
        ImageView image_storetype;
        @BindView(R.id.txt_name_storetype)
        TextView txt_name_storetype;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }


    }
}
