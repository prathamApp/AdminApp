package com.pratham.admin.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.activities.Notification.Notification;
import com.pratham.admin.async.NetworkCalls;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.NetworkCallListener;
import com.pratham.admin.modalclasses.NotificationData;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyService extends Service implements NetworkCallListener {

    Notification notification;
    String LoggedcrlId, LoggedcrlName;
    private List<NotificationData> notificationList = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        notification = new Notification();
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LoggedcrlId = intent.getStringExtra("CRLid");
        LoggedcrlName = intent.getStringExtra("CRLname");
        //Toast.makeText(this, LoggedcrlId, Toast.LENGTH_LONG).show();

        String url = APIs.notificationAPI;
        loadNotifications(url + LoggedcrlId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

    public void loadNotifications(String url) {
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, url, "loading_devises");
    }

    @Override
    public void onResponse(String responsee, String header) {
        if (header.equals("loading_devises")) {
            JSONArray response = null;
            try {
                response = new JSONArray(responsee);
                if (response.length() > 0) {
                    Gson gson = new Gson();
                    Type notificationsList = new TypeToken<ArrayList<NotificationData>>() {
                    }.getType();
                    notificationList = gson.fromJson(response.toString(), notificationsList);

                    Collections.sort(notificationList, new Comparator<NotificationData>() {
                        @Override
                        public int compare(NotificationData lhs, NotificationData rhs) {
                            return rhs.getAutoId().compareTo(lhs.getAutoId());
                        }
                    });

                    String temp = String.valueOf(notificationList.get(0).getAutoId());
                    String tempname = notificationList.get(0).getFromName();
                    Log.e("@@@@@@@@@@",String.valueOf(temp)+tempname);
                    /*NotificationData notificationData = new NotificationData();
                    notificationData.setAutoId(Integer.valueOf(temp));
                    notificationData.setFromName(tempname);
                    AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getNotificationDao().insertNotification(notificationData);
                    BackupDatabase.backup(ApplicationController.getInstance());*/

                    addNotification(tempname, temp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(ANError anError, String header) {

    }

    private void addNotification(String name, String id) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Admin App")
                        .setContentInfo(id)
                        .setContentText(name);

        Intent notificationIntent = new Intent(this, Notification.class);
        notificationIntent.putExtra("CRLid", LoggedcrlId);
        notificationIntent.putExtra("CRLname", LoggedcrlName);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
