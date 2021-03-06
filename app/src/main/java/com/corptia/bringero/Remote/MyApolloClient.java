package com.corptia.bringero.Remote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.CustomTypeAdapter;
import com.apollographql.apollo.api.CustomTypeValue;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.type.CustomType;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

        CustomTypeAdapter emailTypeAdapter = new CustomTypeAdapter() {
            @Override
            public Object decode(@NotNull CustomTypeValue value) {
                return value;
            }

            @Override
            public @NotNull CustomTypeValue encode(@NotNull Object value) {
                return new CustomTypeValue.GraphQLString(value.toString());
            }
        };

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

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message ->{
           ShowLogs(message);
        });

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl(Common.BASE_URL)
                .okHttpClient(okHttpClient)
                .addCustomTypeAdapter(CustomType.DATE, dateCustomTypeAdapter)
                .addCustomTypeAdapter(CustomType.EMAIL, emailTypeAdapter)
                .build();

        return apolloClient;
    }


    //----------------------------------------------- Code With Authorization -------------------------------------------------

    public static ApolloClient getApollowClientAuthorization(){


        CustomTypeAdapter emailTypeAdapter = new CustomTypeAdapter() {
            @Override
            public Object decode(@NotNull CustomTypeValue value) {
                return value;
            }

            @Override
            public @NotNull CustomTypeValue encode(@NotNull Object value) {
                return new CustomTypeValue.GraphQLString(value.toString());
            }
        };


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


        // Custom Time Scalar Type
        CustomTypeAdapter dateTimeCustomTypeAdapter = new CustomTypeAdapter<Long>() {
            @Override
            public Long decode(CustomTypeValue value) {

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    // Log.d("decode","decode");

                    //Date date = dateFormatParse.parse(value.value.toString());
                    //Date startDate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(value.value.toString()).getTime());
//                    Date startDate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(value.value.toString()).getTime());

                    Date pars = dateFormatParse.parse(value.value.toString());

//                    Common.LOG("HAZEM toString : " + pars.toString());
//                    Common.LOG("HAZEM  getTime : " + pars.getTime());

                    // Log.d("DateDate" , "Date >> "+startDate);
                    return pars.getTime() + (3600 * 2000) ;
                } catch (ParseException e) {
                    throw new RuntimeException(e);

                }
            }

            @Override
            public CustomTypeValue encode(Long value) {
                  Log.d("HAZEM","encode " + value);
                return new CustomTypeValue.GraphQLString(value.toString());
            }
        };




        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {

                ShowLogs(message);

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
                       String token ="";
                       if (Common.CURRENT_USER!=null)
                           token = Common.CURRENT_USER.getToken();

                        Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer "+ token).build();

                        return chain.proceed(request);

                    }
                })
                .addInterceptor(httpLoggingInterceptor)
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl(Common.BASE_URL)
                .okHttpClient(okHttpClient)
                .addCustomTypeAdapter(CustomType.DATE, dateCustomTypeAdapter)
                .addCustomTypeAdapter(CustomType.EMAIL, emailTypeAdapter)
                .addCustomTypeAdapter(CustomType.DATETIME, dateTimeCustomTypeAdapter)
                .build();

        return apolloClient;
    }



    //-----------------------------------------------------------------------------------------------------------------

    public static ApolloClient getApollowClientAuthorization(final String token){

        CustomTypeAdapter emailTypeAdapter = new CustomTypeAdapter() {
            @Override
            public Object decode(@NotNull CustomTypeValue value) {
                return value;
            }

            @Override
            public @NotNull CustomTypeValue encode(@NotNull Object value) {
                return new CustomTypeValue.GraphQLString(value.toString());
            }
        };


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

        // Custom DateTime Scalar Type
        CustomTypeAdapter dateTimeCustomTypeAdapter = new CustomTypeAdapter<Date>() {
            @Override
            public Date decode(CustomTypeValue value) {

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatParse = new SimpleDateFormat("HH:mm:ss.SSS'Z'");
                try {
                    // Log.d("decode","decode");

                    //Date date = dateFormatParse.parse(value.value.toString());
                    //Date startDate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(value.value.toString()).getTime());
                    Date startDate = new java.sql.Date(new SimpleDateFormat("HH:mm:ss.SSS'Z'").parse(value.value.toString()).getTime());


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

                ShowLogs(message);

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
                .addCustomTypeAdapter(CustomType.EMAIL, emailTypeAdapter)
                .addCustomTypeAdapter(CustomType.DATETIME, dateTimeCustomTypeAdapter)
                .build();

        return apolloClient;
    }

    //-------------------------------------------------------------------------


    private static void ShowLogs(String message) {

        Log.e("API" , message);
    }








    //-------------------------------------------------------------------------------------------------------------------


    private static final int TIME_OUT = 5000;
    private static OkHttpClient okHttpClient;

    // get the instance of apollo client with all the headers and correct url
    public static ApolloClient getApolloClient(String authToken) {

        if (okHttpClient == null) {
            okHttpClient = getOkHttpClient(authToken);
        }

        return ApolloClient.builder()
                .serverUrl(Common.BASE_URL)
                .okHttpClient(okHttpClient)
                .build();
    }

    public static ApolloClient getApolloClient() {

        if (okHttpClient == null) {
            okHttpClient = getOkHttpClient(Common.CURRENT_USER.getToken());
        }

        return ApolloClient.builder()
                .serverUrl(Common.BASE_URL)
                .okHttpClient(okHttpClient)
                .build();
    }

    private static OkHttpClient getOkHttpClient(String authToken) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // set the timeouts
        builder.connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS);

        addLoggingInterceptor(builder);
        builder.addInterceptor(new RequestInterceptor(authToken));

        return builder.build();
    }

    private static void addLoggingInterceptor(OkHttpClient.Builder builder) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);
    }

    private static class RequestInterceptor implements Interceptor {

        private String authToken;

        public RequestInterceptor(String authToken) {
            this.authToken = authToken;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request.Builder requestBuilder = request.newBuilder();
            requestBuilder.addHeader("Authorization", "Bearer " + authToken);


            return chain.proceed(requestBuilder.build());
        }
    }



    //-------------------------------------------------------------------------------------------------------------------

}
