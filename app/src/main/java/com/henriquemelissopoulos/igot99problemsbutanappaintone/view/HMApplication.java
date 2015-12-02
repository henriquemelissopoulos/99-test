package com.henriquemelissopoulos.igot99problemsbutanappaintone.view;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.BuildConfig;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by h on 02/12/15.
 */
public class HMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initCrashlytics();
    }

    private void initCrashlytics() {
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().build()).build());
        Crashlytics.getInstance().core.setBool("debug", BuildConfig.DEBUG);
    }
}
