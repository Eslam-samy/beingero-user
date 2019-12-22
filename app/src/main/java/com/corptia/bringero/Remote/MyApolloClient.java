package com.corptia.bringero.Remote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.CustomTypeValue;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.type.CustomType;


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MyApolloClient {

    //TODO private static final DEMO String BASE_URL = "";
    private static ApolloClient apolloClient;

    //Without Any Token
    public static ApolloClient getApollowClient(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Log.e("API" , message));

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl(Common.BASE_URL)
                .okHttpClient(okHttpClient)
                .build();

        return apolloClient;
    }




    //----------------------------------------------- Code With Authorization -------------------------------------------------

    public static ApolloClient getApollowClientAuthorization(){


//
//        //Directory where cached responses will be stored
//        File file = new File(""+context.getCacheDir());
//        //Size in bytes of the cache
//        int size = 1024*1024;
//        //Create the http response cache store
//        DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file,size);


        // Custom DateTime Scalar Type
        CustomTypeAdapter dateCustomTypeAdapter = new CustomTypeAdapter<Date>() {
            @Override
            public Date decode(CustomTypeValue value) {

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatParse = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    //Log.d("decode","decode");

                    //Date date = dateFormatParse.parse(value.value.toString());
                    //Date startDate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(value.value.toString()).getTime());
                    Date startDate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(value.value.toString()).getTime());


                   // Log.d("DateDate" , "Date >> "+startDate);
                    return startDate ;
                } catch (ParseException e) {
                    throw new RuntimeException(e);

                }
            }

            @Override
            public CustomTypeValue encode(Date value) {
                //Log.d("encode","encode");
                return new CustomTypeValue.GraphQLString(value.toString());
            }
        };




        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {

               Log.e("API",message);

            }
        });



        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                       /* Request request = chain.request();
                        Request.Builder builder = request.newBuilder();
                        builder.addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVjNDA3OWUxYmNiYTRjMjE5MDVlYTliNCIsInBob25lIjoiKzIwMTAxNDA0Njk0NSIsInJvbGUiOiJTYWxlc1BvaW50QWRtaW4iLCJyb2xlSWQiOiI1Yzk2NmM4ZWM0YzVmNjI3MDRmMmNhN2IiLCJmYWNpbGl0eUlkIjoiNWMxYmI5OGY3MDgzNzIzNWRjODE5Y2M5IiwibGFuZ3VhZ2UiOiJlbiIsImlhdCI6MTU1OTA0ODYyMSwiZXhwIjoxNTYwMzQ0NjIxfQ.E9neeSJqSQlvJDVcp1FxYuxQ_XagyETclMnk7BedCI8").build();
                        builder.method(request.method(), request.body());*/
                        Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer "+ Common.CURRENT_USER.getToken()).build();

                        return chain.proceed(request);

                    }
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl(Common.BASE_URL)
                .okHttpClient(okHttpClient)
                .addCustomTypeAdapter(CustomType.DATE, dateCustomTypeAdapter)
                .build();

        return apolloClient;
    }



    //-----------------------------------------------------------------------------------------------------------------

    public static ApolloClient getApollowClientAuthorization(final String token){

        // Custom DateTime Scalar Type
        CustomTypeAdapter dateCustomTypeAdapter = new CustomTypeAdapter<Date>() {
            @Override
            public Date decode(CustomTypeValue value) {

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatParse = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    // Log.d("decode","decode");

                    //Date date = dateFormatParse.parse(value.value.toString());
                    //Date startDate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(value.value.toString()).getTime());
                    Date startDate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(value.value.toString()).getTime());


                    // Log.d("DateDate" , "Date >> "+startDate);
                    return startDate ;
                } catch (ParseException e) {
                    throw new RuntimeException(e);

                }
            }

            @Override
            public CustomTypeValue encode(Date value) {
                //  Log.d("encode","encode " + value);
                return new CustomTypeValue.GraphQLString(value.toString());
            }
        };



        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {

               Log.e("API", message);

            }
        });



        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer "+token).build();

                        return chain.proceed(request);

                    }
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl(Common.BASE_URL)
                .okHttpClient(okHttpClient)
                .addCustomTypeAdapter(CustomType.DATE, dateCustomTypeAdapter)
                .build();

        return apolloClient;
    }

    //-------------------------------------------------------------------------



}
