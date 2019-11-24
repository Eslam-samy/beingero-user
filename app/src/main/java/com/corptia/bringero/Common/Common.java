package com.corptia.bringero.Common;

import android.content.Context;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.MeQuery;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;
import com.corptia.bringero.type.RoleEnum;
import com.corptia.bringero.ui.Main.login.LoginPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Common {

//        public static final String BASE_URL = "http://45.77.157.193:8000/graphql";
    public static final String BASE_URL = "http://bringero.site:8000/graphql";
    public static final String BASE_URL_IMAGE = "http://bringero.site:8000/images/";
    public static final String BASE_URL_IMAGE_UPLOAD = "http://bringero.site:8000/images/";
    public static String TOKEN_FIREBASE = "";

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

    public static void getMe(String token, CallbackListener listener) {

        MyApolloClient.getApollowClientAuthorization(token)
                .query(MeQuery.builder().build())
                .enqueue(new ApolloCall.Callback<MeQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<MeQuery.Data> response) {

                        MeQuery.UserData userData = response.data().UserQuery().me().UserData();

                        if (response.data().UserQuery().me().status() == 200) {

                            Common.CURRENT_USER = userData;
                            Common.CURRENT_USER_TOKEN = token;
                            listener.OnSuccessCallback();
                        } else {
                            //
                            listener.OnFailedCallback();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
//
                        listener.OnFailedCallback();

                    }
                });

    }
}
