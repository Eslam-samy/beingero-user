package com.corptia.bringero.utils;

import android.widget.ImageView;

import com.corptia.bringero.R;
import com.squareup.picasso.Picasso;

public class PicassoUtils {

    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 768;

    public static int size = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

//    private static Picasso with(Context context) {
//        if (picasso != null)
//            return picasso;
//        OkHttpClient okHttpClient = new OkHttpClient();
//        File customCacheDirectory = new File(context.getCacheDir(), "PicassoCache");
//        okHttpClient.setCache(new Cache(customCacheDirectory, 10 * 1024 * 1024)); //10 MB
//        OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);
//        picasso = new Picasso.Builder(context).downloader(okHttpDownloader).build();
//        return picasso;
//    }

    public static void setImage(String url, ImageView target){


        if (!url.contains("null"))
        {

            Picasso.get()
                    .load(url)
                    //.resize(size,size)
                    //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
//                    .onlyScaleDown()
                    //.centerCrop()
                    //.networkPolicy(NetworkPolicy.OFFLINE)
                    .into(target);
        }
        else
        {

            Picasso.get()
                    .load(R.drawable.ic_placeholder_product)
                    //.resize(size,size)
                    //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    //.onlyScaleDown()
                    //.centerCrop()
                    //.networkPolicy(NetworkPolicy.OFFLINE)
                    .into(target);
        }


    }

    public static void setImage(ImageView target){

            Picasso.get()
                    .load(R.drawable.ic_placeholder_product)
                    //.resize(size,size)
                    //.transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    //.onlyScaleDown()
                    //.centerCrop()
                    //.networkPolicy(NetworkPolicy.OFFLINE)
                    .into(target);

    }



}
