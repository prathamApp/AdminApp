package com.pratham.admin.activities.Notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.admin.R;
import com.pratham.admin.activities.ManageDevice;
import com.pratham.admin.activities.MyDeviceList;
import com.pratham.admin.adapters.NotificationAdapter;
import com.pratham.admin.async.NetworkCalls;
import com.pratham.admin.interfaces.NetworkCallListener;
import com.pratham.admin.modalclasses.DeviseList;
import com.pratham.admin.modalclasses.NotificationData;
import com.pratham.admin.util.APIs;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class Notification extends AppCompatActivity implements NetworkCallListener {

    //region Variables
    private List<NotificationData> notificationList = new ArrayList<>();
    private RecyclerView nf_recyclerView;
    private NotificationAdapter nfAdapter;

    String LoggedcrlName;
    String LoggedcrlId;
    Context context;

    @BindView(R.id.search)
    EditText search;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        context = Notification.this;
        //Toast.makeText(context, LoggedcrlId+" | "+LoggedcrlName, Toast.LENGTH_SHORT).show();

        String url = APIs.notificationAPI;
        loadNotifications(url + LoggedcrlId);

        nf_recyclerView = findViewById(R.id.recycler_list);

        /*nfAdapter = new NotificationAdapter(context, notificationList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        nf_recyclerView.setLayoutManager(layoutManager);
        nf_recyclerView.setItemAnimator(new DefaultItemAnimator());
        nf_recyclerView.setAdapter(nfAdapter);
        nfAdapter.notifyDataSetChanged();*/

        //notificationData();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nf_recyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<NotificationData> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (int i = 0; i < notificationList.size(); i++) {
            final String damageType = notificationList.get(i).getDamageType();
            final String fromName = notificationList.get(i).getFromName();
            final String fromId = notificationList.get(i).getFromId();
            //if the existing elements contains the search input
            if (damageType.toLowerCase().contains(text.toLowerCase())
                    || fromName.toLowerCase().contains(text.toLowerCase())
                    || fromId.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(notificationList.get(i));
            }
        }
        //calling a method of the adapter class and passing the filtered list
        nfAdapter.filterList(filterdNames);
    }

    private void loadNotifications(String url) {
        NetworkCalls.getNetworkCallsInstance(this).getRequest(this, url, "Loading Notifications..", "loading_devises");
    }

    @Override
    public void onResponse(String response1, String header) {
        if (header.equals("loading_devises")) {
            JSONArray response = null;
            try {
                response = new JSONArray(response1);
                if (response.length() > 0) {
                    //MyDeviceList myDeviceList = new MyDeviceList(context, response);
                    //myDeviceList.show();
                    Gson gson = new Gson();
                    Type notificationsList = new TypeToken<ArrayList<NotificationData>>() {
                    }.getType();
                    this.context = context;
                    notificationList = gson.fromJson(response.toString(), notificationsList);
                    nfAdapter = new NotificationAdapter(context, notificationList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    nf_recyclerView.setLayoutManager(layoutManager);
                    nf_recyclerView.setItemAnimator(new DefaultItemAnimator());
                    //for stable view of recyclerview.
                    nf_recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
                    nf_recyclerView.setAdapter(nfAdapter);
                    Collections.sort(notificationList, new StringDateComparator());
                    nfAdapter.notifyDataSetChanged();

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Notification.this).create();
                    alertDialog.setTitle("No Notification Found");
                    alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                    alertDialog.setButton("OK", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(ANError anError, String header) {

    }

    //class for sorting date in descending order
    class StringDateComparator implements Comparator<NotificationData> {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        public int compare(NotificationData lhs, NotificationData rhs) {
            try {
                return dateFormat.parse(rhs.getReceiveDate()).compareTo(dateFormat.parse(lhs.getReceiveDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
