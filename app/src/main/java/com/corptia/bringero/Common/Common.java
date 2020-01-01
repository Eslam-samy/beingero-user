package com.corptia.bringero.Common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetCartItemsCountQuery;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;
import com.corptia.bringero.model.CartItemsModel;
import com.corptia.bringero.model.UserModel;
import com.corptia.bringero.type.CartItemFilterInput;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationAdapter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationPresenter;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Common {

    //        public static final String BASE_URL = "http://45.77.157.193:8000/graphql";
    public static final String BASE_URL = "http://bringero.site:8000/graphql";
    public static final String BASE_URL_IMAGE = "http://bringero.site:8000/images/";
    public static final String BASE_URL_IMAGE_UPLOAD = "http://bringero.site:8000/images/";

    public static final double BASE_MAX_PRICE = 500.00;
    public static Boolean IS_AVAILABLE_STORE = false;

    public static String TOKEN_FIREBASE = "";

    public static List<MyCartQuery.StoreDatum> CURRENT_CART;
    public static UserModel CURRENT_USER;

    public static SingleStoreQuery.CurrentStore CURRENT_STORE;

    public static boolean isUpdateCurrentLocation;
    public static boolean isFirstTimeAddLocation = false;

    //Store Data Cart
    public static List<CartItemsModel> CART_ITEMS_MODELS;
    public static List<String> CART_ITEMS_ID;
    public static double TOTAL_CART_PRICE = 0;
    public static int TOTAL_CART_AMOUNT = 0;


    public static int dpToPx(int dp, Context context) {
        if (context != null) {
            float density = context.getResources().getDisplayMetrics().density;
            return Math.round((float) dp * density);
        }
        return 0;
    }

//    public static void getMe(String token, CallbackListener listener) {
//
//        MyApolloClient.getApollowClientAuthorization(token)
//                .query(MeQuery.builder().build())
//                .enqueue(new ApolloCall.Callback<MeQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<MeQuery.Data> response) {
//
//                        MeQuery.UserData userData = response.data().UserQuery().me().UserData();
//
//                        if (response.data().UserQuery().me().status() == 200) {
//
//                            Common.CURRENT_USER = userData;
//                            Common.CURRENT_USER_TOKEN = token;
//                            listener.OnSuccessCallback();
//                        } else {
//                            //
//                            listener.OnFailedCallback();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
////
//                        listener.OnFailedCallback();
//
//                    }
//                });
//
//    }


    public static BottomSheetDialog showDialogSelectLocation(Context context, BottomSheetDialog bottomSheetDialog, SelectDeliveryLocationPresenter presenter) {

        SelectDeliveryLocationAdapter adapter;
        bottomSheetDialog = new BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //I use this line because this class not activity
        View sheetView = inflater.inflate(R.layout.layout_select_delivery_location, null);

        RecyclerView recycler_delivery_location = sheetView.findViewById(R.id.recycler_delivery_location);
        Button btn_select_location_from_map = sheetView.findViewById(R.id.btn_select_location_from_map);
        Button btn_apply_location = sheetView.findViewById(R.id.btn_apply_location);

        recycler_delivery_location.setHasFixedSize(true);
        recycler_delivery_location.setLayoutManager(new LinearLayoutManager(context));
        recycler_delivery_location.setNestedScrollingEnabled(true);
        recycler_delivery_location.addItemDecoration(new LinearSpacingItemDecoration(Common.dpToPx(15, context)));

        adapter = new SelectDeliveryLocationAdapter(context, Common.CURRENT_USER.getDeliveryAddressesList());
        recycler_delivery_location.setAdapter(adapter);

        adapter.setClickListener(new IOnRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Here Update Location Yo CuttentLocation
//                presenter.userUpdateCurrentLocation(adapter.getCurrentDeliveryAddressID(position));
                adapter.selectCurrentLocation(position);

            }
        });

        BottomSheetDialog finalBottomSheetDialog = bottomSheetDialog;
        btn_select_location_from_map.setOnClickListener(view -> {
            //Here Open Maps
            Common.isUpdateCurrentLocation = true;
            context.startActivity(new Intent(context, MapsActivity.class));
            finalBottomSheetDialog.dismiss();
        });

        BottomSheetDialog finalBottomSheetDialog1 = bottomSheetDialog;
        btn_apply_location.setOnClickListener(view -> {
            if (adapter.isChangeLocation())
                presenter.userUpdateCurrentLocation(adapter.getCurrentDeliveryAddressID());
            else
                finalBottomSheetDialog1.dismiss();
        });

        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        bottomSheetDialog.getWindow().setBackgroundDrawableResource(R.drawable.round_up_bottom_sheet);

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();

        return bottomSheetDialog;
    }

    public static boolean isFirstTimeGetCartCount = true;

    public static void GetCartItemsCount() {

        CART_ITEMS_MODELS = new ArrayList<>();
        CART_ITEMS_ID = new ArrayList<>();


        MyApolloClient.getApollowClientAuthorization().query(GetCartItemsCountQuery.builder().filter(CartItemFilterInput.builder().build()).build())
                .enqueue(new ApolloCall.Callback<GetCartItemsCountQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetCartItemsCountQuery.Data> response) {

                        if (response.data().CartItemQuery().getAll().status() == 200) {

                            List<GetCartItemsCountQuery.Data1> Items = response.data().CartItemQuery().getAll().data();

                            if (isFirstTimeGetCartCount) {
                                TOTAL_CART_PRICE = 0;
                                TOTAL_CART_AMOUNT = 0;
                            }

                            for (GetCartItemsCountQuery.Data1 product : Items) {

                                CART_ITEMS_MODELS.add(new CartItemsModel(product.cartProductId(), product.pricingProductId(), product.totalPrice(), product.amount()));
                                CART_ITEMS_ID.add(product.pricingProductId());

                                if (isFirstTimeGetCartCount) {
                                    TOTAL_CART_PRICE += product.totalPrice();
                                    TOTAL_CART_AMOUNT += product.amount();
                                }

                            }

                            isFirstTimeGetCartCount = false;

                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }

}
