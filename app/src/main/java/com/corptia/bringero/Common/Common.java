package com.corptia.bringero.Common;

import android.content.Context;

public class Common {

    public static String CURRENT_NUMBER = "";

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
