package com.pratham.admin.activities.DeviceInformation;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.MetaData;

import org.androidannotations.annotations.EBean;

import java.lang.reflect.Field;

@EBean
public class DeviceInfoPrsenter implements DeviceInfoContract.DeviceInfoPresenter {

    Context context;
    DeviceInfoContract.DeviceInfoView deviceInfoView;

    MetaData metaData;
    private String deviceID, apkVersion, serialID, WiFiMac,
            osVersionName, osVersionNum, availStorage, resolution, manufacturer, model;
    private int osApiLevel;
    private long internalStorageSize;

    DeviceInfoPrsenter(Context context){
        this.context=context;
    }

    @Override
    public void populateDeviceInfo() {
        //osversion();
        //getStorage();
    }

    @Override
    public void setView(DeviceInfoContract.DeviceInfoView deviceInfoView) {
        this.deviceInfoView=deviceInfoView;
    }

    /*@Override
    public void addMetaData() {
        metaData = new MetaData();
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
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

    }*/

    /*//for getting os versionName and ApiLevel
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
        deviceInfoView.showDeviceInfo(osApiLevel, availStorage);
    }
*/
}
