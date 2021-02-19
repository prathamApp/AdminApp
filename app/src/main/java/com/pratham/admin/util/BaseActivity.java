package com.pratham.admin.util;

import android.content.Context;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.activities.CatchoTransparentActivity;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.modalclasses.EventMessage;

import net.alhazmy13.catcho.library.Catcho;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class BaseActivity extends AppCompatActivity implements ConnectionReceiverListener {
    ConnectionReceiver connectivityReceiver;
    //In App Update Variables
    private AppUpdateManager appUpdateManager;
    private Task<AppUpdateInfo> appUpdateInfoTask;
    private int APP_UPDATE_TYPE_SUPPORTED = AppUpdateType.FLEXIBLE;
    private int REQUEST_UPDATE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Catcho.Builder(this)
                .activity(CatchoTransparentActivity.class) //on field
//                .recipients("sujitghadge437@gmail.com") // on testing
                .build();

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
    protected void onPause() {
        BackupDatabase.backup(this);
        super.onPause();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.e("#", "onStart");
        EventMessage message = new EventMessage();
        message.setMessage("GOT");
        EventBus.getDefault().post(message);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));
    }

    @Subscribe
    public void onEvent(@NonNull EventMessage eventMessage) {
        if (eventMessage.getMessage().equalsIgnoreCase("CHECK_UPDATE")) {
            checkForUpdate();
        } else if (eventMessage.getMessage().equalsIgnoreCase("START_UPDATE")) {
            startUpdate();
        }
    }

    //Flexible Update
    private void checkForUpdate() {
/*        if (BuildConfig.DEBUG) {
            appUpdateManager = new FakeAppUpdateManager(this);
            ((FakeAppUpdateManager) appUpdateManager).setUpdateAvailable(0);
            Log.e("##########  ->", "Fake");
        } else {*/
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Log.e("##########  ->", "Original");
        //}
        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(installStateUpdatedListener);

        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        Log.e("########## 1 ->", String.valueOf(appUpdateInfoTask));
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            Log.e("########## 2 ->", "SuccessListener");
            if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                //send message if update is available
                EventMessage updateAvailable = new EventMessage();
                updateAvailable.setMessage("UPDATE_AVAILABLE");
                EventBus.getDefault().post(updateAvailable);
            } else {
                Log.e("########## 5 ->", "No Update available");
            }
        });
    }

    //Listener for checking Install Status
    InstallStateUpdatedListener installStateUpdatedListener = new
            InstallStateUpdatedListener() {
                @Override
                public void onStateUpdate(InstallState state) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                        //send message if update is downloaded
                        Log.e("#", "InstallStateUpdated: state: " + state.installStatus());
                        appUpdateManager.completeUpdate();
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        Log.e("#", "InstallStateInstalled: state: " + state.installStatus());
                        if (appUpdateManager != null) {
                            appUpdateManager.unregisterListener(installStateUpdatedListener);
                        }

                    } else {
                        Log.e("#", "InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            };

    public void startUpdate() {
        // Start an update.
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfoTask.getResult(),
                    AppUpdateType.FLEXIBLE,
                    this,
                    REQUEST_UPDATE);
            Log.e("########## 3 ->", "All Condition true");
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
/*        if (BuildConfig.DEBUG) {
            FakeAppUpdateManager fakeAppUpdate = (FakeAppUpdateManager) appUpdateManager;
            if (fakeAppUpdate.isConfirmationDialogVisible()) {
                fakeAppUpdate.userAcceptsUpdate();
                fakeAppUpdate.downloadStarts();
                fakeAppUpdate.downloadCompletes();
                fakeAppUpdate.completeUpdate();
                fakeAppUpdate.installCompletes();
            }
        }*/

    }
}