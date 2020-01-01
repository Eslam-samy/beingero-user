package com.corptia.bringero.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.storesDetail.StoreDetailActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Utils {


   /* public static FoodApi getApi() {
        return FoodClient.getFoodClient().create(FoodApi.class);
    }*/

   public static String cutName(String name){

       return name.length() >= 30 ? name.substring(0,30)+"..." : name ;

   }

    public static void setImageToPicasso(ImageView imageToPicasso, String id) {
        Picasso.get()
                .load(id)
                .fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageToPicasso, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        e.printStackTrace();
//            Picasso.get(context)
//                .load(id)
//                .fit()
//
//                .into(imageToPicasso, new Callback() {
//                  @Override
//                  public void onSuccess() {
//
//                  }
//
//                  @Override
//                  public void onError() {
//                    Log.v("Picasso", "Could not fetch image");
//                  }
//                });
                    }


                });

    }

    public static AlertDialog showDialogMessage(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message).show();
        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        return alertDialog;
    }




    public static androidx.appcompat.app.AlertDialog showAlertDialogCloseStore(Context context , View.OnClickListener onClickListenerOk, View.OnClickListener onClickListenerContinue){

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context);
        View layout_alert = LayoutInflater.from(context).inflate(R.layout.layout_dialog_alert , null);

        Button btn_ok = layout_alert.findViewById(R.id.btn_ok);
        Button btn_continue = layout_alert.findViewById(R.id.btn_continue);

        alertDialog.setView(layout_alert);
        androidx.appcompat.app.AlertDialog dialog = alertDialog.create();

        btn_ok.setOnClickListener(onClickListenerOk);

        btn_continue.setOnClickListener(onClickListenerContinue);

        dialog.show();

        return dialog;
    }
}