package com.corptia.bringero.ui.home.notification;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.airbnb.lottie.L;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseViewHolder;
import com.corptia.bringero.graphql.NotificationQuery;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.corptia.bringero.Common.Constants.VIEW_TYPE_ITEM;
import static com.corptia.bringero.Common.Constants.VIEW_TYPE_LOADING;

public class NotificationAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private boolean isLoaderVisible = false;

    Context context;
    List<NotificationQuery.NotificationDatum> notificationList = new ArrayList<>();

    IOnRecyclerViewClickListener clickListener;

    private boolean clicked;


    public void setClickListener(IOnRecyclerViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

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
        @BindView(R.id.txt_time)
        TextView txt_time;
        @BindView(R.id.card_image_notification)
        CardView card_image_notification;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            NotificationQuery.NotificationDatum notification = notificationList.get(position);

            //For Show Ago
            if (notification.createdAtDateTime() != null) {

                try {


                    long time = Long.parseLong(notification.createdAtDateTime().toString());
                    long now = System.currentTimeMillis();
                    CharSequence ago =
                            DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);

                    txt_time.setText(ago);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//                try {
//                    Common.LOG("Error sdfsdfsdf : " +notification.createdAtDateTime());
//
//                    Common.LOG("time : " + time);
//                    long now = System.currentTimeMillis();
//                    CharSequence ago =
//                            DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
//
//                    Common.LOG("Error : " + ago.toString());
//                    txt_time.setText("ago " + ago);
//
//                } catch (ParseException e) {
//                    Common.LOG("Error : " + e.getMessage());
//                    e.printStackTrace();
//                }


            }

//            txt_date_notification.setText(notification.createdAt().toString());
            txt_messages_notification.setText(notification.message());
            String uriColor = "@color/blue1";
            String uriColor2 = "@color/blue1";

            if (notification.docStatus() != null)
                uriColor = "@color/" + notification.docStatus().toLowerCase();
            int colorResource = context.getResources().getIdentifier(uriColor, null, context.getPackageName());
            if (colorResource != 0) {
                card_image_notification.setCardBackgroundColor(context.getResources().getColor(colorResource));
            } else {
                card_image_notification.setCardBackgroundColor(context.getResources().getColor(R.color.blue1));
            }
            String uriImage = "@drawable/ic_notification_approved";

            if (notification.docStatus() != null)
                uriImage = "@drawable/ic_notification_" + notification.docStatus().toLowerCase();
//            Common.LOG("Hi : " + uriImage);
            int imageResource = context.getResources().getIdentifier(uriImage, null, context.getPackageName());
//            Drawable res = context.getResources().getDrawable(imageResource);
            if (imageResource != 0) {
                image_notification.setImageResource(imageResource);

            } else {
                image_notification.setImageResource(R.drawable.ic_logo_white);
            }

            //For Resolved Status To Arabic
//            String uriString = "@string/order_status_" + notification.docStatus().toLowerCase();
//            int stringResource = context.getResources().getIdentifier(uriString, null, context.getPackageName());
//            txt_status.setText(""+context.getResources().getString(stringResource));
//            Common.LOG("uriString : " + uriString);
//            int stringResource = context.getResources().getIdentifier(uriString, null, context.getPackageName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (clicked) {
                        return;
                    }
                    clicked = true;
                    view.postDelayed(() -> clicked = false, 500);

                    clickListener.onClick(view, position);
                }
            });

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

    public NotificationQuery.NotificationDatum getItem(int position) {
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
