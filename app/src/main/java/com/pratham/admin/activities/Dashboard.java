package com.pratham.admin.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pratham.admin.modalclasses.Aser;
import com.pratham.admin.modalclasses.Attendance;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.Completion;
import com.pratham.admin.modalclasses.DashboardItem;
import com.pratham.admin.modalclasses.GroupSession;
import com.pratham.admin.modalclasses.GroupVisit;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.DashRVTouchListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pratham.admin.util.APIs.PushForms;

//import com.pratham.admin.modalclasses.CRLVisit;

public class Dashboard extends BaseActivity implements DashRVClickListener, ConnectionReceiverListener {

    // Ref : https://www.dev2qa.com/android-cardview-with-image-and-text-example/
    String LoggedcrlId = "", LoggedcrlName = "", LoggedCRLnameSwapStd = "";
    DashRVDataAdapter DataAdapter;
    boolean internetIsAvailable = false;
    @BindView(R.id.tv_appInfo)
    TextView tv_appInfo;
    private String deviceID, apkVersion, serialID, WiFiMac, gpsFixDuration;
    private List<DashboardItem> DashboardItemList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        checkConnection();
        //backup Database
        BackupDatabase backupDatabase = new BackupDatabase();
        backupDatabase.backup(this);
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
//        pushNewData();

        populateProgramID();

        populateApkVersion();

    }

    private void populateApkVersion() {
        MetaData metaData = new MetaData();
        PackageInfo pInfo = null;
        String verCode = "";
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            verCode = pInfo.versionName;
            metaData.setKeys("apkVersion");
            metaData.setValue(verCode);
            AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void populateProgramID() {
        SharedPreferences preferences = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        String program = preferences.getString("program", "null");
        int pid = 0;
        if (program.contains("Learning"))
            pid = 1;
        else if (program.contains("India"))
            pid = 2;
        else if (program.contains("Institutes"))
            pid = 10;
        else if (program.contains("Second"))
            pid = 3;
        else if (program.contains("Urban"))
            pid = 6;
        else if (program.contains("Gaon"))
            pid = 13;
        else if (program.contains("Government"))
            pid = 14;
        else if (program.contains("ECE"))
            pid = 8;
        else if (program.contains("KGBV"))
            pid = 5;

        MetaData metaData = new MetaData();
        metaData.setKeys("ProgramID");
        metaData.setValue(String.valueOf(pid));
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);
    }

    private void initializeAppInfo() {
        deviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        serialID = android.os.Build.SERIAL;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            apkVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        WiFiMac = wInfo.getMacAddress();

        tv_appInfo.setText("Apk Version : " + apkVersion + "\nWiFi MAC : " + WiFiMac + "\nDevice ID : " + deviceID + "\nSerial ID : " + serialID);
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
        initializeAppInfo();
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
//        List<Course> coursesObj = new ArrayList<>();
//        coursesObj = AppDatabase.getDatabaseInstance(this).getCoursesDao().getNewCourses(0);
//        List<CRLVisit> CRLVisitObj = new ArrayList<>();
//        CRLVisitObj = AppDatabase.getDatabaseInstance(this).getCRLVisitdao().getNewCRLVisits(0);
        List<GroupSession> GroupSessionObj = new ArrayList<>();
        GroupSessionObj = AppDatabase.getDatabaseInstance(this).getGroupSessionDao().getNewGroupSessions(0);
        List<GroupVisit> GroupVisitObj = new ArrayList<>();
        GroupVisitObj = AppDatabase.getDatabaseInstance(this).getGroupVisitDao().getNewGroupVisits(0);

        List<Student> stdObj = new ArrayList<>();
        stdObj = AppDatabase.getDatabaseInstance(this).getStudentDao().getNewStudents(0);
        List<Aser> aserObj = new ArrayList<>();
        aserObj = AppDatabase.getDatabaseInstance(this).getAserDao().getNewAser(0);
        List<Groups> grpObj = new ArrayList<>();
        grpObj = AppDatabase.getDatabaseInstance(this).getGroupDao().getNewGroups(0);

        // Push To Server
        try {
            if (internetIsAvailable) {
                Gson gson = new Gson();

                MetaData metaData = new MetaData();
                metaData.setKeys("pushDataTime");
                metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));
                List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
                String metaDataJSON = customParse(metaDataList);
                AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

                String json = "{ \"AttendanceJSON\":" + "" + gson.toJson(aObj).toString() + ",\"CoachesJSON\":" + "" + gson.toJson(coachesObj).toString() + ",\"CommunitiesJSON\":" + "" + gson.toJson(communitiesObj).toString() + ",\"CompletionsJSON\":" + "" + gson.toJson(completionsObj).toString()
                        + ",\"StudentJSON\":" + "" + gson.toJson(stdObj).toString()
                        + ",\"AserJSON\":" + "" + gson.toJson(aserObj).toString()
                        + ",\"GroupsJSON\":" + "" + gson.toJson(grpObj).toString()
                        + ",\"GroupVisitsJSON\":" + "" + gson.toJson(GroupSessionObj).toString() + ",\"GroupSessionJSON\":" + "" + gson.toJson(GroupVisitObj).toString() + ",\"metadata\":" + "" + metaDataJSON + "}";

                Log.d("json all push :::", json);

                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("UPLOADING ... ");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                if ((aObj.size() == 0) && (coachesObj.size() == 0) && (communitiesObj.size() == 0) && (completionsObj.size() == 0)
                    /*&& (CRLVisitObj.size() == 0)*/) {
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
//                            AppDatabase.getDatabaseInstance(Dashboard.this).getCRLVisitdao().updateAllSentFlag(1);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getGroupSessionDao().updateAllSentFlag(1);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getGroupVisitDao().updateAllSentFlag(1);

                            AppDatabase.getDatabaseInstance(Dashboard.this).getStudentDao().updateAllSentFlag(1);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getAserDao().updateAllSentFlag(1);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getGroupDao().updateAllSentFlag(1);

                            Toast.makeText(Dashboard.this, "Data Pushed !", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }

                        @Override
                        public void onError(ANError anError) {
                            AppDatabase.getDatabaseInstance(Dashboard.this).getAttendanceDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCoachDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCommunityDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCompletionDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getCoursesDao().updateAllSentFlag(0);
//                            AppDatabase.getDatabaseInstance(Dashboard.this).getCRLVisitdao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getGroupVisitDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getGroupSessionDao().updateAllSentFlag(0);

                            AppDatabase.getDatabaseInstance(Dashboard.this).getStudentDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getAserDao().updateAllSentFlag(0);
                            AppDatabase.getDatabaseInstance(Dashboard.this).getGroupDao().updateAllSentFlag(0);

                            Toast.makeText(Dashboard.this, "Error in Data Pushing !", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    });
                }

            } else {
                //No Internet
                Toast.makeText(this, "No Internet !\nUnable to Push !", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* Initialise items in list. */
    private void initializeItemList() {
        if (DashboardItemList == null) {
            DashboardItemList = new ArrayList<DashboardItem>();
//            DashboardItemList.add(new DashboardItem("Scan QR Code", R.drawable.qr_code_selector));
//            DashboardItemList.add(new DashboardItem("Swap Students", R.drawable.swap_selector));
            DashboardItemList.add(new DashboardItem("Forms", R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem("Student Management", R.drawable.ic_pos));
//            DashboardItemList.add(new DashboardItem("Pull Data", R.drawable.ic_pull));
            DashboardItemList.add(new DashboardItem("Push Data", R.drawable.ic_push));
            DashboardItemList.add(new DashboardItem("Manage Device", R.drawable.tablet));
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
        } else if (name.contains("Management")) {
            Intent intent = new Intent(Dashboard.this, Student_Management.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains("Pull")) {
            Intent intent = new Intent(Dashboard.this, PullData.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains("Push")) {
            /*
            Intent intent = new Intent(Dashboard.this, PushData.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
            */
            turnOnWifi();
            pushNewData();

        } else if (name.contains("Manage Device")) {
            Intent intent = new Intent(Dashboard.this, ManageDevice.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            startActivity(intent);
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
