package com.corptia.bringero.Common;

import android.content.Context;

public class Common {

    public static String CURRENT_NUMBER = "";
    public static String CURRENT_USER_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZDdmNGNmMWFiYzNjMzJmZTNhNDRhYjgiLCJmaXJzdE5hbWUiOiJIYXplbSIsImxhc3ROYW1lIjoiRWxuYWhhcyIsImxhbmd1YWdlIjoiYXIiLCJyb2xlTmFtZSI6IlN0b3JlQWRtaW4iLCJpYXQiOjE1Njg2NDQ5OTIsImV4cCI6MTU2OTk0MDk5Mn0.1rp-IS02G7mbMRYtplJG4ZgIUOfW1eKw-ejJJUCot5g";

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
