package com.corptia.bringero.utils.customAppBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.notification.NotificationActivity;


public class CustomAppBar extends ConstraintLayout {
    private String label;
    private int icon;
    boolean hasBackIcon, HasText, isFinish;
    boolean hasNotsIcon = true;
    int notifications;
    BarClicks clicks;

    public CustomAppBar(Context context) {
        super(context);
        initAttributes(context, null);
    }

    public void setClicks(BarClicks clicks) {
        this.clicks = clicks;
    }

    public CustomAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }


    public CustomAppBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.custom_app_bar, this, true);
        backClickListner();
        notificationClickListner();
        TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.CustomAppBar);

//        Common.count.observeForever(integer -> {
//            if (integer > 0) {
//                notifications = integer;
//                showNotifications();
//            }
//        });

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomAppBar_text_value:
                    setLabel(a.getString(attr));
                    break;
                case R.styleable.CustomAppBar_is_left_icon:
                    hideBackIcon();
                    break;
                case R.styleable.CustomAppBar_is_right_icon:
                    hideNotsIcon();
                    break;
                case R.styleable.CustomAppBar_is_text:
                    hideText();
                    break;
                case R.styleable.CustomAppBar_is_finish:
                    isFinish = true;
                    break;
            }
        }
        a.recycle();

    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(final String label) {
        this.label = label;
        ((TextView) findViewById(R.id.header)).setText(this.label);
    }

    /**
     * @return the icon
     */
    public int getIcon() {
        return icon;
    }


    public void hideBackIcon() {
        findViewById(R.id.back).setEnabled(false);
        findViewById(R.id.imageView5).setVisibility(View.GONE);
    }

    private void showNotifications() {

        ((TextView) findViewById(R.id.notifi_count)).setText(String.valueOf(notifications));
        findViewById(R.id.notifi_count).setVisibility(View.VISIBLE);
    }

    //
    public void hideNotsIcon() {
        findViewById(R.id.notification_image).setVisibility(View.GONE);
        findViewById(R.id.notifi_count).setVisibility(View.GONE);
    }

    public void hideText() {
        findViewById(R.id.header).setVisibility(View.GONE);
    }

    private void backClickListner() {
        findViewById(R.id.back).setOnClickListener(view -> {

            if (clicks != null) {
                Log.d("ESLAM", "backClickListner: 11111111111 ");
                clicks.onBackClick();
            } else {
                Log.d("ESLAM", "backClickListner: 22222222222 ");
                ((Activity) getContext()).finish();

            }
        });
    }

    private void notificationClickListner() {
        findViewById(R.id.notification_image).setOnClickListener(v -> {
            getContext().startActivity(new Intent(getContext(), NotificationActivity.class));
            if (isFinish) {
                ((Activity) getContext()).finish();
                this.isFinish = false;
            }
        });
    }
}

