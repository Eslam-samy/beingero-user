package com.corptia.bringero.view.brands;

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
import com.corptia.bringero.view.brandDetail.BrandDetailActivity;
import com.corptia.bringero.view.home.ui.storetypes.Adapter.StoreTypesAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.ViewHolder> {

    Context context;
    List<StoreTypes> storeTypesList = new ArrayList<>();

    public BrandsAdapter(Context context, List<StoreTypes> storeTypesList) {
        this.context = context;
        this.storeTypesList = storeTypesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_brands, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StoreTypes storeTypes = storeTypesList.get(position);

        Picasso.get().load(storeTypes.getImg())
                .into(holder.image_brands);

        holder.txt_name_brands.setText("Data " + (position+1));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context,BrandDetailActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return storeTypesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_brands)
        ImageView image_brands;
        @BindView(R.id.txt_name_brands)
        TextView txt_name_brands;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }


    }
}
