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
import android.widget.Toast;

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

    private String apkVersion, resolution;

    @Bean(DeviceInfoPrsenter.class)
    DeviceInfoContract.DeviceInfoPresenter deviceInfoPrsenter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @AfterViews
    public void initialize(){
        deviceInfoPrsenter.setView(DeviceInformation.this);
        initializeAppInfo();
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

        deviceInfoPrsenter.populateDeviceInfo(resolution);
        deviceInfoPrsenter.addMetaData();
    }

    @Override
    public void showDeviceInfo(int api, String osVnum, String osVname, String availStorag, String wifi, String devId,
                               String serId, String manuf, String mod, String resol) {
        String apkVer = apkVersion;

        //displaying all value in textView
        tv_infocontent.setText(Html.fromHtml(getString(R.string.apkVersion) + apkVer +
                getString(R.string.wifiMac) + wifi +
                getString(R.string.deviceId) + devId +
                getString(R.string.serialId) + serId +
                getString(R.string.versionId) + osVname+
                getString(R.string.versionNumber) + osVnum+
                getString(R.string.apiLevel)+ api +
                getString(R.string.screenResolution) + resol+
                getString(R.string.manufactureName) + manuf +
                getString(R.string.model) + mod +
                getString(R.string.availableStorage) + availStorag
        ));

    }
}
