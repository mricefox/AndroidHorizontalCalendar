package com.mricefox.androidhorizontalcalendar;

import android.app.Application;
import android.os.StrictMode;

import com.mricefox.androidhorizontalcalendar.library.assist.MFLog;
import com.squareup.leakcanary.LeakCanary;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        setStrictMode();
        super.onCreate();
        LeakCanary.install(this);
    }

    private void setStrictMode() {
        MFLog.d("Enabling StrictMode policy over Sample application");
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }
}
