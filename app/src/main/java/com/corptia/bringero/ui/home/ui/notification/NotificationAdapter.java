package com.corptia.bringero.ui.home.ui.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseViewHolder;
import com.corptia.bringero.graphql.NotificationQuery;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corptia.bringero.Common.Constants.VIEW_TYPE_ITEM;
import static com.corptia.bringero.Common.Constants.VIEW_TYPE_LOADING;

public class NotificationAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private boolean isLoaderVisible = false;

    Context context;
    List<NotificationQuery.NotificationDatum> notificationList = new ArrayList<>();


    public NotificationAdapter(Context context, @Nullable List<NotificationQuery.NotificationDatum> notificationList) {
        this.context = context;
        if (notificationList != null)
            this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_ITEM:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_notification, parent, false));
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
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == notificationList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.image_notification)
        ImageView image_notification;
        @BindView(R.id.txt_messages_notification)
        TextView txt_messages_notification;
        @BindView(R.id.txt_date_notification)
        TextView txt_date_notification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            NotificationQuery.NotificationDatum notification = notificationList.get(position);

            txt_date_notification.setText(notification.createdAt().toString());
            txt_messages_notification.setText(notification.message());

        }

        @Override
        protected void clear() {

        }
    }


    public void addItems(List<NotificationQuery.NotificationDatum> notificationList) {
        this.notificationList.addAll(notificationList);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        notificationList.add(null);
        notifyItemInserted(notificationList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = notificationList.size() - 1;
        NotificationQuery.NotificationDatum item = getItem(position);
        if (item == null) {
            notificationList.remove(position);
            notifyItemRemoved(position);
        }

    }

    NotificationQuery.NotificationDatum getItem(int position) {
        return notificationList.get(position);
    }

    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }
}
