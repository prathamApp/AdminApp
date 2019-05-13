package com.pratham.admin;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.androidnetworking.AndroidNetworking;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.util.ConnectionReceiver;

public class ApplicationController extends Application {
    private static ApplicationController INSTANCE;

    public ApplicationController() {
        INSTANCE = this;
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
        INSTANCE = this;
    }

    public static synchronized ApplicationController getInstance() {
        return INSTANCE;
    }

    public void setConnectionListener(ConnectionReceiverListener listener) {
        ConnectionReceiver.connectionReceiverListener = listener;
    }
}
