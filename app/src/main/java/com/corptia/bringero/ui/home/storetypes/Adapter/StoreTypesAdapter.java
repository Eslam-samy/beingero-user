package com.corptia.bringero.ui.home.storetypes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.graphql.StoreTypesQuery;
import com.corptia.bringero.ui.home.storetypes.StoreTypesFragment;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.ui.stores.StoresActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreTypesAdapter extends RecyclerView.Adapter<StoreTypesAdapter.ViewHolder> {

    Context context;
    List<StoreTypesQuery.StoreCategory>  storeTypesList = new ArrayList<>();

    public StoreTypesAdapter(Context context, List<StoreTypesQuery.StoreCategory>  storeTypesList) {
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

        StoreTypesQuery.StoreCategory storeTypes = storeTypesList.get(position);

        //Picasso.get().load(storeTypes.g())
         //       .into(holder.image_storetype);

        if (storeTypes.StoreType().ImageResponse() !=null) {
            if (storeTypes.StoreType().ImageResponse().data() != null)
                PicassoUtils.setImage(Common.BASE_URL_IMAGE + storeTypes.StoreType().ImageResponse().data().name(), holder.image_storetype);
        }else if (storeTypes.StoreType()._id().isEmpty()) {
            Picasso.get().load(R.drawable.specialoffers).into(holder.image_storetype);
            holder.txt_name_storetype.setText(R.string.special_offer);

        }
//        if ( storeTypes.ImageResponse().data()!=null)
//            PicassoUtils.setImage(Common.BASE_URL_IMAGE + storeTypes.ImageResponse().data().name() , holder.image_storetype);
//        else
//            PicassoUtils.setImage( holder.image_storetype);
        if (storeTypes.storeCount() == 0 && storeTypes.StoreType()._id().isEmpty()){
            holder.txt_count_storetype.setText(context.getString(R.string.No_Offers_available));
        }
        holder.txt_name_storetype.setText(storeTypes.StoreType().name());
        holder.txt_count_storetype.setText(new StringBuilder().append(storeTypes.storeCount()).append(" ").append(storeTypes.storeCount() == 1 ? context.getString(R.string.store) : context.getString(R.string.stores)));

        holder.itemView.setEnabled(true);

                holder.itemView.setOnClickListener(view -> {
                    if (storeTypes.storeCount()> 0) {
                    holder.itemView.setEnabled(false);
                    Intent intent = new Intent(context, StoresActivity.class);
                    intent.putExtra(Constants.EXTRA_CATEGOTY_ID, storeTypes.StoreType()._id());
                    intent.putExtra(Constants.EXTRA_STORE_TYPE_NAME, storeTypes.StoreType().name());
                    intent.putExtra(Constants.EXTRA_STORE_OFFER, storeTypes.StoreType()._id().isEmpty());

                    context.startActivity(intent);
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
        @BindView(R.id.txt_count_storetype)
        TextView txt_count_storetype;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }


    }


}
