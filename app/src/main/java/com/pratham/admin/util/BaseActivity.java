package com.pratham.admin.util;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pratham.admin.ApplicationController;
import com.pratham.admin.activities.CatchoTransparentActivity;
import com.pratham.admin.interfaces.ConnectionReceiverListener;

import net.alhazmy13.catcho.library.Catcho;

public class BaseActivity extends AppCompatActivity implements ConnectionReceiverListener {
    ConnectionReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*Catcho.Builder(this)
//                .activity(CatchoTransparentActivity.class) //on field
                .recipients("amarkargal199447@gmail.com") // on testing
                .build();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().setConnectionListener(BaseActivity.this);
        // Manually checking internet connection

        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        connectivityReceiver = new ConnectionReceiver();
        // Register the broadcast receiver with the intent filter object.
        registerReceiver(connectivityReceiver, intentFilter);
        BackupDatabase.backup(this);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityReceiver);
    }
}