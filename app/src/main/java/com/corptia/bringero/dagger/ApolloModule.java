package com.corptia.bringero.dagger;


import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloClient;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@Module
public class ApolloModule {

    //Bae url of the Git Hub graph Api
    private static final String BASE_URL = "";

    //access token
    private static final String ACCESS_TOKEN = "your token";

    /**
     * This method constructs and returns the Auth interceptor
     * by adding the Authorization header with token to the request
     *
     * @return auth Interceptor okhttp
     */
    @Provides
    @Singleton
    Interceptor provideInterceptor() {
        return chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .method(original.method(), original.body())
                    .header("Authorization", "Bearer " + ACCESS_TOKEN);

            Request request = builder.build();
            return chain.proceed(request);
        };

    }

    /**
     * This method builds the Okhttp client and adds the
     * authentication interceptor in it.
     *
     * @param authInterceptor which contains the auth header
     * @return okHttpClient
     */
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Interceptor authInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build();
    }

    /**
     * This method builds the Apollo client using the okhttp client and
     * the Base url
     *
     * @param okHttpClient okhttp3 client which contains the auth interceptor
     * @return apolloClient
     */
    @Provides
    @Singleton
    ApolloClient provideApolloClient(@NonNull OkHttpClient okHttpClient) {

        return ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build();
    }

}
