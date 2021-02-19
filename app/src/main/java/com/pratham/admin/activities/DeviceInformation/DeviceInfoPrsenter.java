package com.pratham.admin.activities.DeviceInformation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.MetaData;

import org.androidannotations.annotations.EBean;

import java.lang.reflect.Field;

@EBean
public class DeviceInfoPrsenter implements DeviceInfoContract.DeviceInfoPresenter {

    Context context;
    DeviceInfoContract.DeviceInfoView deviceInfoView;

    MetaData metaData;
    private String deviceID, serialID, WiFiMac,
            osVersionName, osVersionNum, availStorage, manufacturer, model, resolution;
    private int osApiLevel;
    private long internalStorageSize;

    DeviceInfoPrsenter(Context context){
        this.context=context;
    }

    @SuppressLint("NewApi")
    @Override
    public void populateDeviceInfo(String res) {
        resolution=res;
        osversion();
        getStorage();
    }

    @Override
    public void setView(DeviceInfoContract.DeviceInfoView deviceInfoView) {
        this.deviceInfoView=deviceInfoView;
    }

    @Override
    public void addMetaData() {
       /* metaData = new MetaData();
        metaData.setKeys("DeviceID");
        metaData.setValue(deviceID);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("SerialID");
        metaData.setValue(serialID);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("WiFiMac");
        metaData.setValue(WiFiMac);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("osVersionNum");
        metaData.setValue(osVersionNum);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("osVersionName");
        metaData.setValue(osVersionName);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("osApiLevel");
        metaData.setValue(String.valueOf(osApiLevel));
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("internalAvailableStorage");
        metaData.setValue(availStorage);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("screenResolution");
        metaData.setValue(resolution);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("manufacturer");
        metaData.setValue(manufacturer);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("model");
        metaData.setValue(model);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);*/

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

    //for getting internal available storage and device info
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
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

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        WiFiMac = wInfo.getMacAddress();


        //Model details
        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;

        //get deviceID and serialID
        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        serialID = android.os.Build.SERIAL;
        deviceInfoView.showDeviceInfo(osApiLevel, osVersionNum, osVersionName, availStorage, WiFiMac, deviceID, serialID,
                manufacturer, model, resolution);
    }
}
