package com.pratham.admin.activities.DeviceInformation;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.util.BaseActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Field;

@EActivity(R.layout.activity_device_information)
public class DeviceInformation extends BaseActivity implements DeviceInfoContract.DeviceInfoView {

    @ViewById(R.id.infocontent)
    TextView tv_infocontent;

    MetaData metaData;
    private String deviceID, apkVersion, serialID, WiFiMac,
            osVersionName, osVersionNum, availStorage, resolution, manufacturer, model;
    private int osApiLevel;
    private long internalStorageSize;

    @Bean(DeviceInfoPrsenter.class)
    DeviceInfoContract.DeviceInfoPresenter deviceInfoPrsenter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @AfterViews
    public void initialize(){
        deviceInfoPrsenter.setView(DeviceInformation.this);
        deviceInfoPrsenter.populateDeviceInfo();
        initializeAppInfo();
    }

    //for getting os versionName and ApiLevel
    private void osversion(){
        osVersionNum = Build.VERSION.RELEASE;

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            osVersionName = field.getName();
            osApiLevel = -1;

            try {
                osApiLevel = field.getInt(new Object());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //for getting internal available storage
    private void getStorage(){
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long bytesAvailable;
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        }
        else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        }
        internalStorageSize = bytesAvailable / (1024 * 1024);
        String storage = String.valueOf(internalStorageSize);
        availStorage = storage+" MB";
        Log.e("SSSSSSSs",availStorage);
        showDeviceInfo(osApiLevel, availStorage);
    }


    //all device info
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initializeAppInfo() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            apkVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        WiFiMac = wInfo.getMacAddress();

        osversion();
        getStorage();

        //screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        String strwidth = String.valueOf(width);
        String strheight = String.valueOf(height);

        Configuration config = getBaseContext().getResources().getConfiguration();
        resolution= "W "+strwidth+" x H "+strheight+" pixels dpi:"+config.densityDpi;

        //Model details
        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;

        //get deviceID and serialID
        deviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        serialID = android.os.Build.SERIAL;

        //displaying all value in textView
        tv_infocontent.setText(Html.fromHtml(getString(R.string.apkVersion) + apkVersion +
                getString(R.string.wifiMac) + WiFiMac +
                getString(R.string.deviceId) + deviceID +
                getString(R.string.serialId) + serialID +
                getString(R.string.versionId) + osVersionName+
                getString(R.string.versionNumber) + osVersionNum+
                getString(R.string.apiLevel)+ osApiLevel +
                getString(R.string.screenResolution) + resolution+
                getString(R.string.manufactureName) + manufacturer +
                getString(R.string.model) + model +
                getString(R.string.availableStorage) + internalStorageSize+ " MB"
        ));

        addMetadata();
    }

    //add information to metadata table
    private void addMetadata(){
        metaData = new MetaData();
        metaData.setKeys("DeviceID");
        metaData.setValue(deviceID);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("SerialID");
        metaData.setValue(serialID);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("WiFiMac");
        metaData.setValue(WiFiMac);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("osVersionNum");
        metaData.setValue(osVersionNum);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("osVersionName");
        metaData.setValue(osVersionName);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("osApiLevel");
        metaData.setValue(String.valueOf(osApiLevel));
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("internalAvailableStorage");
        metaData.setValue(availStorage);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("screenResolution");
        metaData.setValue(resolution);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("manufacturer");
        metaData.setValue(manufacturer);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("model");
        metaData.setValue(model);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);
    }

    @Override
    public void showDeviceInfo(int osApiLevl, String availStorag) {
        osApiLevel=osApiLevl;
        availStorage=availStorag;
    }
}
