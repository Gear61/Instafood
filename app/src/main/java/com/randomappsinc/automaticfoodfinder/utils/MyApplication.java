package com.randomappsinc.automaticfoodfinder.utils;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.IoniconsModule;

public final class MyApplication extends Application {

    private static Context mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new IoniconsModule());
        mInstance = getApplicationContext();
    }

    public static Context getAppContext() {
        return mInstance;
    }
}
