package com.pratham.admin.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.adapters.DashRVDataAdapter;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.DashRVClickListener;
import com.pratham.admin.modalclasses.Attendance;
import com.pratham.admin.modalclasses.CRLVisit;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.Completion;
import com.pratham.admin.modalclasses.Course;
import com.pratham.admin.modalclasses.DashboardItem;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.DashRVTouchListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.pratham.admin.util.APIs.PushForms;

public class Dashboard extends AppCompatActivity implements DashRVClickListener, ConnectionReceiverListener {

    // Ref : https://www.dev2qa.com/android-cardview-with-image-and-text-example/
    String LoggedcrlId = "", LoggedcrlName = "", LoggedCRLnameSwapStd = "";

    private List<DashboardItem> DashboardItemList = null;
    DashRVDataAdapter DataAdapter;
    boolean internetIsAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Hide Actionbar
        getSupportActionBar().hide();

        checkConnection();

        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        LoggedCRLnameSwapStd = getIntent().getStringExtra("CRLnameSwapStd");

        // Recycler View
        initializeItemList();

        // Create the recyclerview.
        RecyclerView dashRecyclerView = (RecyclerView) findViewById(R.id.card_view_recycler_list);
        // Create the grid layout manager with 2 columns.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        // Set layout manager.
        dashRecyclerView.setLayoutManager(gridLayoutManager);

        // Create recycler view data adapter with item list.
        DataAdapter = new DashRVDataAdapter(DashboardItemList);
        // Set data adapter.
        dashRecyclerView.setAdapter(DataAdapter);

        dashRecyclerView.addOnItemTouchListener(new DashRVTouchListener(getApplicationContext(), dashRecyclerView, Dashboard.this));

        // Start WiFi
        turnOnWifi();
        pushNewData();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start WiFi
        turnOnWifi();
        checkConnection();
        ApplicationController.getInstance().setConnectionListener(this);
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    private void turnOnWifi() {
        //enable wifi
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        if (!wifiEnabled) {
            wifiManager.setWifiEnabled(true);
        }
    }

    private String customParse(List<MetaData> metaDataList) {
        String json = "{";

        for (int i = 0; i < metaDataList.size(); i++) {
            json = json + "\"" + metaDataList.get(i).getKeys() + "\":\"" + metaDataList.get(i).getValue() + "\"";
            if (i < metaDataList.size() - 1) {
                json = json + ",";
            }
        }
        json = json + "}";

        return json;
    }


    private void pushNewData() {

        checkConnection();

        List<Attendance> aObj = new ArrayList<>();
        aObj = AppDatabase.getDatabaseInstance(this).getAttendanceDao().getNewAttendances(0);
        List<Coach> coachesObj = new ArrayList<>();
        coachesObj = AppDatabase.getDatabaseInstance(this).getCoachDao().getNewCoaches(0);
        List<Community> communitiesObj = new ArrayList<>();
        communitiesObj = AppDatabase.getDatabaseInstance(this).getCommunityDao().getNewCommunities(0);
        List<Completion> completionsObj = new ArrayList<>();
        completionsObj = AppDatabase.getDatabaseInstance(this).getCompletionDao().getNewCompletions(0);
        List<Course> coursesObj = new ArrayList<>();
        coursesObj = AppDatabase.getDatabaseInstance(this).getCoursesDao().getNewCourses(0);
        List<CRLVisit> CRLVisitObj = new ArrayList<>();
        CRLVisitObj = AppDatabase.getDatabaseInstance(this).getCRLVisitdao().getNewCRLVisits(0);

        // Push To Server
        try {
            if (internetIsAvailable) {
                Gson gson = new Gson();

                String AttendanceJSON = "";
                String CoachesJSON = "";
                String CommunitiesJSON = "";
                String CompletionsJSON = "";
                String CoursesJSON = "";
                String CRLVisitsJSON = "";

                if (aObj.size() > 0)
                    AttendanceJSON = gson.toJson(Collections.singletonList(aObj));
                if (coachesObj.size() > 0)
                    CoachesJSON = gson.toJson(Collections.singletonList(coachesObj));
                if (communitiesObj.size() > 0)
                    CommunitiesJSON = gson.toJson(Collections.singletonList(communitiesObj));
                if (completionsObj.size() > 0)
                    CompletionsJSON = gson.toJson(Collections.singletonList(completionsObj));
                if (coursesObj.size() > 0)
                    CoursesJSON = gson.toJson(Collections.singletonList(coursesObj));
                if (CRLVisitObj.size() > 0)
                    CRLVisitsJSON = gson.toJson(Collections.singletonList(CRLVisitObj));

                MetaData metaData = new MetaData();
                metaData.setKeys("pushDataTime");
                metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));
                List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
                String metaDataJSON = customParse(metaDataList);
                AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

                String json = "{ \"AttendanceJSON\":" + "" + AttendanceJSON
                        + ",\"CoachesJSON\":" + "" + CoachesJSON
                        + ",\"CommunitiesJSON\":" + "" + CommunitiesJSON
                        + ",\"CompletionsJSON\":" + "" + CompletionsJSON
                        + ",\"CoursesJSON\":" + "" + CoursesJSON
                        + ",\"CRLVisitsJSON\":" + "" + CRLVisitsJSON
                        + ",\"metadata\":" + "" + metaDataJSON
                        + "}";

                Log.d("json :::", json);

                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("UPLOADING ... ");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                if ((aObj.size() == 0) && (coachesObj.size() == 0)
                        && (communitiesObj.size() == 0) && (completionsObj.size() == 0)
                        && (coursesObj.size() == 0) && (CRLVisitObj.size() == 0)) {
                    // No Data Available
                    dialog.dismiss();
                    Log.d("json not pushed:::", json);
                } else {
                    AndroidNetworking.post(PushForms).setContentType("application/json").addStringBody(json).build().getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("responce", response);

                            // update flag
                            AppDatabase.getDatabaseInstance(Dashboard.this).getAttendanceDao().updateAllSentFlag(1);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCoachDao().updateAllSentFlag(1);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCommunityDao().updateAllSentFlag(1);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCompletionDao().updateAllSentFlag(1);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCoursesDao().updateAllSentFlag(1);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCRLVisitdao().updateAllSentFlag(1);
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(ANError anError) {
                            AppDatabase.getDatabaseInstance(Dashboard.this).getAttendanceDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCoachDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCommunityDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCompletionDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCoursesDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCRLVisitdao().updateAllSentFlag(0);
                            dialog.dismiss();
                        }
                    });
                }

            } else {
                //No Internet
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* Initialise items in list. */
    private void initializeItemList() {
        if (DashboardItemList == null) {
            DashboardItemList = new ArrayList<DashboardItem>();
            DashboardItemList.add(new DashboardItem("Scan or Code", R.drawable.qr_code_selector));
            DashboardItemList.add(new DashboardItem("Swap Students", R.drawable.swap_selector));
            DashboardItemList.add(new DashboardItem("Forms", R.drawable.ic_form));
        }
    }

    @Override
    public void onClick(View view, int position) {
        DashboardItem Dash = DashboardItemList.get(position);
        String name = Dash.getName();

        if (name.contains("Scan")) {
            Intent intent = new Intent(Dashboard.this, Activity_QRScan.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            startActivity(intent);
        } else if (name.contains("Swap")) {
            Intent intent = new Intent(Dashboard.this, SwapStudentsActivity.class);
            intent.putExtra("CRLname", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains("Forms")) {
            Intent intent = new Intent(Dashboard.this, FormsActivity.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
