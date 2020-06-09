package com.corptia.bringero.Common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetCartItemsCountQuery;
import com.corptia.bringero.graphql.MyCartQuery;
import com.corptia.bringero.graphql.ProductsTypeQuery;
import com.corptia.bringero.graphql.SingleStoreQuery;
import com.corptia.bringero.model.CartItemsModel;
import com.corptia.bringero.model.CurrentDeliveryAddress;
import com.corptia.bringero.model.MyCart;
import com.corptia.bringero.model.UserModel;
import com.corptia.bringero.type.CartItemFilterInput;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.allowLocation.AllowLocationActivity;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationAdapter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationPresenter;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Common {

    //        public static final String BASE_URL = "http://45.77.157.193:8000/graphql";
    public static String BASE_URL = "";
    public static String BASE_URL_IMAGE = "";
    public static String TEST_URL = "";
    public static String TEST_URL_IMAGE = "";
//    public static  String BASE_URL_IMAGE_UPLOAD = "http://bringero.site:8000/images/";

    public static final double BASE_MAX_PRICE = 500.00;
    public static double LAST_APP_VERSION;

    public static String DELIVERY_COST;
    public static String MINDELIVERY_COST;
    public static String MAX_AD_COST_STORE;
    public static String MIN_COUPON_STORE;
    public static int STORE_COUNT;

    public static Boolean IS_AVAILABLE_STORE = false;

    public static String TOKEN_FIREBASE = "";

    public static List<MyCartQuery.StoreDatum> CURRENT_CART;
    public static UserModel CURRENT_USER;

    public static SingleStoreQuery.CurrentStore CURRENT_STORE;
    public static List<ProductsTypeQuery.Data1> CURRENT_PRODUCTS_TYPE;

    public static boolean isUpdateCurrentLocation;
    public static boolean isFirstTimeAddLocation = false;

    //Store Data Cart
    public static List<CartItemsModel> CART_ITEMS_MODELS = new ArrayList<>();
    public static List<String> CART_ITEMS_ID = new ArrayList<>();
    public static double TOTAL_CART_PRICE = 0;
    public static int TOTAL_CART_AMOUNT = 0;

    public static List<MyCart> myLocalCart = new ArrayList<>();
    public static List<String> myLocalCartIds = new ArrayList<>();

    //For Change Langage For number
    //public static DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    public static DecimalFormat decimalFormatDiscount = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
    public static NumberFormat formatter = decimalFormatDiscount;
    public static String CURRENT_IMIE;
    public static boolean adapterIsLoading=false;
    public static double totalPriceCart=0;

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
//        bottomSheetDialog.setCanceledOnTouchOutside(false);
//        bottomSheetDialog.setCancelable(false);

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

            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //Here Open AllowLocationActivity
                Common.isUpdateCurrentLocation = true;
                context.startActivity(new Intent(context, AllowLocationActivity.class));
                finalBottomSheetDialog.dismiss();
            } else {

                //Here Open Maps
                Common.isUpdateCurrentLocation = true;
                context.startActivity(new Intent(context, MapsActivity.class));
                finalBottomSheetDialog.dismiss();

            }


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
//        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();

        return bottomSheetDialog;
    }

    public static boolean isFirstTimeGetCartCount = true;

    public static void GetCartItemsCount(CallbackListener listener) {


        MyApolloClient.getApollowClientAuthorization().query(GetCartItemsCountQuery.builder().filter(CartItemFilterInput.builder().build()).build())
                .enqueue(new ApolloCall.Callback<GetCartItemsCountQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetCartItemsCountQuery.Data> response) {
double myPrice=0;
                        if (response.data().CartItemQuery().getAll().status() == 200) {

                            CART_ITEMS_MODELS = new ArrayList<>();
                            CART_ITEMS_ID = new ArrayList<>();

                            List<GetCartItemsCountQuery.Data1> Items = response.data().CartItemQuery().getAll().data();

                            if (isFirstTimeGetCartCount) {
                                TOTAL_CART_PRICE = 0;
                                TOTAL_CART_AMOUNT = 0;
                            }

                            for (GetCartItemsCountQuery.Data1 product : Items) {

                                CART_ITEMS_MODELS.add(new CartItemsModel(product.cartProductId(), product.pricingProductId(), product.totalPrice(), product.amount(), product.PricingProductResponse().data().ProductResponse().data().isPackaged()));
                                CART_ITEMS_ID.add(product.cartProductId());

                                if (isFirstTimeGetCartCount) {
//                                    Common.LOG("Hello first");
                                    TOTAL_CART_PRICE += product.totalPrice();
                                }else{
                                    myPrice+=product.totalPrice();
                                }

                            }

                            if (isFirstTimeGetCartCount) {
                                TOTAL_CART_AMOUNT = CART_ITEMS_MODELS.size();
                            }


                            if (listener != null)
                                listener.OnSuccessCallback();

                            isFirstTimeGetCartCount = false;

                        } else if (response.data().CartItemQuery().getAll().status() == 404) {

                            TOTAL_CART_PRICE = 0;
                            TOTAL_CART_AMOUNT = 0;
                            CART_ITEMS_MODELS = new ArrayList<>();
                            CART_ITEMS_ID = new ArrayList<>();
                            isFirstTimeGetCartCount = false;

                        }
                        Log.i("TAG Moha price", "onResponse: cart total from server " + myPrice);
                        Log.i("TAG Moha price", "onResponse: cart total bottom " + Common.totalPriceCart);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        if (listener != null)
                            listener.OnFailedCallback();

                    }
                });
    }

    public static String getDecimalNumber(double price) {
        return formatter.format(price);
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static CurrentDeliveryAddress getCurrentDeliveryAddress(String _id, String region, String name, String street,
                                                                   String flatType, LatLng locationPoint,
                                                                   int building, int floor, int flat) {


        CurrentDeliveryAddress currentDeliveryAddressModel = new CurrentDeliveryAddress();

        currentDeliveryAddressModel.setId(_id);
        currentDeliveryAddressModel.setFlatType(flatType);
        currentDeliveryAddressModel.setName(name);
        currentDeliveryAddressModel.setStreet(street);
        currentDeliveryAddressModel.setLocation(locationPoint);
        currentDeliveryAddressModel.setRegion(region);

        currentDeliveryAddressModel.setBuilding(building);
        currentDeliveryAddressModel.setFlat(flat);
        currentDeliveryAddressModel.setFloor(floor);

//        if (building!=null && building.isEmpty() && building.equalsIgnoreCase("0")){
//
//
//        }else {
//            currentDeliveryAddressModel.setBuilding(0);
//            currentDeliveryAddressModel.setFlat(0);
//            currentDeliveryAddressModel.setFloor(0);
//        }


        return currentDeliveryAddressModel;
    }

    public static void LOG(String log) {
        Log.d("HAZEM", "Log : " + log);
    }

}
