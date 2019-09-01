package com.corptia.bringero.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Utils {


   /* public static FoodApi getApi() {
        return FoodClient.getFoodClient().create(FoodApi.class);
    }*/

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
}