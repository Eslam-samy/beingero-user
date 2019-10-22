package com.corptia.bringero.ui.MapWork;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ToastUtil {

    public static void showShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static void showCustomToast(Context context , String msg , int codeMsg , Drawable IconDrawable , int tintColor , int txtColor) {

        switch (codeMsg)
        {
            case 404:
                Toasty.error(context, msg, Toast.LENGTH_SHORT, true).show();
                break;
            case 200:
                Toasty.success(context, msg, Toast.LENGTH_SHORT, true).show();
                break;
            case 2:
                Toasty.info(context, msg, Toast.LENGTH_SHORT, true).show();
                break;
            case 3:
                Toasty.warning(context, msg, Toast.LENGTH_SHORT, true).show();
                break;
            case 1000:
                Toasty.custom(context, msg, IconDrawable, tintColor,txtColor,0,true,true).show();
               break;
        }
    }

}
