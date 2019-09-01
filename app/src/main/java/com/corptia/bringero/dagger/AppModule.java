package com.corptia.bringero.dagger;

import android.app.Application;
import android.content.Context;

import dagger.Binds;
import dagger.Module;


@Module
public abstract class AppModule {

    /**
     * Provides Dagger with the context
     *
     * @param application application context
     * @return returns the context
     */
    @Binds
    abstract Context provideContext(Application application);
}
