package com.corptia.bringero.Common;

import android.content.Context;

import com.corptia.bringero.graphql.MeQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;

import java.util.List;

public class Common {

    public  static  MeQuery.UserData CURRENT_USER ;

    public static SingleStoreQuery.CurrentStore CURRENT_STORE ;

    public static String CURRENT_USER_TOKEN ;

    public static int dpToPx(int dp, Context context) {
        if (context != null) {
            float density = context.getResources().getDisplayMetrics().density;
            return Math.round((float) dp * density);
        }
        return 0;
    }
}
