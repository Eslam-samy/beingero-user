package com.corptia.bringero.ui.stores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.graphql.GetStoresOfASingleCategoryQuery;
import com.corptia.bringero.ui.storesDetail.StoreDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.ViewHolder> {

    Context context;
    List<GetStoresOfASingleCategoryQuery.Store> storeTypesList = new ArrayList<>();

    public StoresAdapter(Context context, List<GetStoresOfASingleCategoryQuery.Store> storeTypesList) {
        this.context = context;
        this.storeTypesList = storeTypesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_stores, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GetStoresOfASingleCategoryQuery.Store storeTypes = storeTypesList.get(position);

        if (storeTypes.ImageResponse().data()!=null)
            Picasso.get().load(Common.BASE_URL_IMAGE + storeTypes.ImageResponse().data().name())
            .placeholder(R.drawable.ic_placeholder_store)
            .into(holder.img_store);
//            PicassoUtils.setImage(Common.BASE_URL_IMAGE + storeTypes.ImageResponse().data().name() , holder.img_store);

        holder.txt_name_store.setText(storeTypes.name());
        if (storeTypes.orderMaxPreparingMinutes() <=10)
        holder.txt_preparing_time.setText(new StringBuilder().append(storeTypes.orderMaxPreparingMinutes()).append(" ").append(context.getString(R.string.minute)));
        else
            holder.txt_preparing_time.setText(new StringBuilder().append(storeTypes.orderMaxPreparingMinutes()).append(" ").append(context.getString(R.string.minutes)));

        holder.itemView.setEnabled(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (storeTypes.isAvailable()){
                    holder.itemView.setEnabled(false);

                    Intent intentStore = new Intent(context , StoreDetailActivity.class);
                    intentStore.putExtra(Constants.EXTRA_STORE_ID , storeTypes._id());
                    intentStore.putExtra(Constants.EXTRA_ADMIN_USER_ID , storeTypes.adminUserId());
                    intentStore.putExtra(Constants.EXTRA_STORE_NAME , storeTypes.name());
                    if (storeTypes.ImageResponse().data()!=null)
                        intentStore.putExtra(Constants.EXTRA_STORE_IMAGE , storeTypes.ImageResponse().data().name());
                    else
                        intentStore.putExtra(Constants.EXTRA_STORE_IMAGE , "null");

                    Common.IS_AVAILABLE_STORE = storeTypes.isAvailable();


                    context.startActivity(intentStore);
                }
                else
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    View layout_alert = LayoutInflater.from(context).inflate(R.layout.layout_dialog_alert , null);

                    Button btn_ok = layout_alert.findViewById(R.id.btn_ok);
                    Button btn_continue = layout_alert.findViewById(R.id.btn_continue);



                    alertDialog.setView(layout_alert);
                    AlertDialog dialog = alertDialog.create();

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btn_continue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent intentStore = new Intent(context , StoreDetailActivity.class);
                            intentStore.putExtra(Constants.EXTRA_STORE_ID , storeTypes._id());
                            intentStore.putExtra(Constants.EXTRA_ADMIN_USER_ID , storeTypes.adminUserId());
                            intentStore.putExtra(Constants.EXTRA_STORE_NAME , storeTypes.name());
                            if (storeTypes.ImageResponse().data()!=null)
                                intentStore.putExtra(Constants.EXTRA_STORE_IMAGE , storeTypes.ImageResponse().data().name());
                            else
                                intentStore.putExtra(Constants.EXTRA_STORE_IMAGE , "null");

                            Common.IS_AVAILABLE_STORE = storeTypes.isAvailable();

                            dialog.dismiss();

                            context.startActivity(intentStore);
                        }
                    });

                    dialog.show();


                }


            }
        });

        if (storeTypes.isAvailable()){
            holder.img_lock.setVisibility(View.GONE);
        }else
            holder.img_lock.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return storeTypesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_store)
        ImageView img_store;
        @BindView(R.id.img_lock)
        ImageView img_lock;
        @BindView(R.id.txt_name_store)
        TextView txt_name_store;
        @BindView(R.id.txt_preparing_time)
        TextView txt_preparing_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }


    }
}
