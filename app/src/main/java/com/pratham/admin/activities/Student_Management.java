package com.pratham.admin.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.activities.AddNewGroup.AddNewGroup_;
import com.pratham.admin.forms.DeleteStudentsForm;
import com.pratham.admin.util.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Student_Management extends BaseActivity/* implements ConnectionReceiverListener */ {

    @BindView(R.id.tv_appInfo)
    TextView tv_appInfo;

    String LoggedcrlId = "", LoggedcrlName = "", LoggedCRLnameSwapStd = "";
    boolean internetIsAvailable = false;
    private String deviceID, apkVersion, serialID, WiFiMac, gpsFixDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__management);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        initialDashboardSetup();

    }

    private void initialDashboardSetup() {

        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        LoggedCRLnameSwapStd = getIntent().getStringExtra("CRLnameSwapStd");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeAppInfo();

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

        tv_appInfo.setText("Apk Version : " + apkVersion + "\t\t\tWiFi-MAC : " + WiFiMac +
                "\nDevice ID : " + deviceID + "\t\t\tSerial ID : " + serialID);
    }

    public void AddNewGroup(View view) {
        Intent intent = new Intent(this, AddNewGroup_.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
        startActivity(intent);
    }

    public void AddNewStudent(View view) {
        Intent intent = new Intent(this, AddNewStudent.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
        startActivity(intent);
    }

    public void EditStudent(View view) {
        Intent intent = new Intent(this, EditStudent.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
        startActivity(intent);
    }

    public void DeleteStudent(View view) {
        Intent intent = new Intent(this, DeleteStudentsForm.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
        startActivity(intent);
    }

    public void SwapStudents(View view) {
        Intent intent = new Intent(this, SwapStudentsActivity.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
        startActivity(intent);
    }

    public void gotoECEAssessment(View view) {
        Intent intent = new Intent(this, ECESampleAssessment.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
        startActivity(intent);
    }
}
