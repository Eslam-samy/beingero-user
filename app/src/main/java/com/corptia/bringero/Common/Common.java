package com.corptia.bringero.Common;

import android.content.Context;

import com.corptia.bringero.graphql.MeQuery;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;

import java.util.List;

public class Common {

    public static final String BASE_URL = "http://45.77.157.193:8000/graphql";
    public static final String BASE_URL_IMAGE = "http://45.77.157.193:8000/images/";

    public static List<MyCartQuery.StoreDatum> CURRENT_CART;
    public static MeQuery.UserData CURRENT_USER;

    public static SingleStoreQuery.CurrentStore CURRENT_STORE;

    public static String CURRENT_USER_TOKEN;

    public static boolean isUpdateCurrentLocation;


    public static int dpToPx(int dp, Context context) {
        if (context != null) {
            float density = context.getResources().getDisplayMetrics().density;
            return Math.round((float) dp * density);
        }
        return 0;
    }
}
