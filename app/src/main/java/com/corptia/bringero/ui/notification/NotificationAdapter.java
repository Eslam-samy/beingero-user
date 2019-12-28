package com.corptia.bringero.ui.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corptia.bringero.R;
import com.corptia.bringero.model.StoreTypes;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    List<StoreTypes> storeTypesList = new ArrayList<>();

    public NotificationAdapter(Context context, List<StoreTypes> storeTypesList) {
        this.context = context;
        this.storeTypesList = storeTypesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_notification, parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return storeTypesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_notification)
        ImageView image_notification;
        @BindView(R.id.txt_messages_notification)
        TextView txt_messages_notification;
        @BindView(R.id.txt_date_notification)
        TextView txt_date_notification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);

        }
    }
}
