package com.pratham.admin.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.pratham.admin.ApplicationController;
import com.pratham.admin.activities.CatchoTransparentActivity;
import com.pratham.admin.activities.CheckInternetDialog;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Modal_Log;

import net.alhazmy13.catcho.library.Catcho;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class Utility {

    private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    private final DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    public static String targetPath = "";
    public static String recievedFilePath = "";
    static ProgressDialog dialog;
    static CheckInternetDialog checkInternetDialog;

    public static void showDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static String getCurrentVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e1.getMessage());
            log.setExceptionStackTrace(e1.getStackTrace().toString());
            log.setMethodName("Utility" + "_" + "getCurrentVersion");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());
            e1.printStackTrace();
        }
        String currentVersion = pInfo.versionName;
        return currentVersion;
    }


    public String GetCurrentDateTime(boolean getSysTime) {
        if (getSysTime) {
            //
            Calendar cal = Calendar.getInstance();
            return dateFormat.format(cal.getTime());
        } else {
            Calendar cal = Calendar.getInstance();
            return dateFormat.format(cal.getTime());
//            Log.d("GetCurrentDateTime ", "" + MyApplication.getAccurateTimeStamp());
//            return MyApplication.getAccurateTimeStamp();
        }
    }

    public String GetDeviceID() {
        String deviceID = "";
        return deviceID = Settings.Secure.getString(ApplicationController.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    public String GetCurrentDate() {
        Calendar cal = Calendar.getInstance();
        return dateFormat1.format(cal.getTime());
//        Log.d("GetDate ", "" + MyApplication.getAccurateDate());
//        return MyApplication.getAccurateDate();
    }

    public static UUID GetUniqueID() {
        return UUID.randomUUID();
    }

    public int ConvertBooleanToInt(Boolean val) {
        return (val) ? 1 : 0;
    }

    public static String getProperty(String key, Context context) {
        try {
            Properties properties = new Properties();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (Exception ex) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(ex.getMessage());
            log.setExceptionStackTrace(ex.getStackTrace().toString());
            log.setMethodName("Utility" + "_" + "getProperty");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());

            return null;
        }
    }

    public long DateDifferentExample(String from, String to) {
        String dateStart = from;
        String dateStop = to;

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

        Date d1 = null;
        Date d2 = null;
        long diff = 0;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");

        } catch (Exception e) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("Utility" + "_" + "DateDifference");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());

            e.printStackTrace();
        }
        // in milis
        return diff;
    }

    public static void getCatcho(Context context) {
        Catcho.Builder(context)
                .activity(CatchoTransparentActivity.class)
                .recipients("your-email@domain.com")
                .build();
    }

    public static void clearCheckInternetInstance() {
        checkInternetDialog = null;
    }

    public static void showNoInternetDialog(Context context) {
        if (checkInternetDialog == null) {
            checkInternetDialog = new CheckInternetDialog(context);
        }
        if (!checkInternetDialog.isShowing()) {
            checkInternetDialog.show();
        }
    }

    public static void dismissNoInternetDialog() {
        if (checkInternetDialog != null) {
            checkInternetDialog.dismiss();
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    public static String getDeviceSerialID() {
        return Build.SERIAL;
    }

    public static String getWifiMac(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        return macAddress;
    }

    public static String getOSVersion() {
//        String osVersionNum = Build.VERSION.RELEASE;
        String osVersionName = "";

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            osVersionName = field.getName();
//            int osApiLevel = -1;

            try {
//                osApiLevel = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return osVersionName;
    }
    public static String getAndroidOSVersion() {
        return Build.VERSION.RELEASE;
    }
    public static int getApiLevel() {
        int osApiLevel = -1;
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            try {
                osApiLevel = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return osApiLevel;
    }

    public static String getInternalStorageStatus() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long bytesAvailable, internalStorageSize;
        bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        internalStorageSize = bytesAvailable / (1024 * 1024);
        String storage = String.valueOf(internalStorageSize);
        return "" + storage + " MB";
    }

    public static String getDeviceResolution(Activity context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Configuration config = context.getResources().getConfiguration();
        String strwidth = String.valueOf(width);
        String strheight = String.valueOf(height);

        String resolution = strwidth + "px x " + strheight + "px (" + config.densityDpi + " dpi)";
        return resolution;
    }

    public static String getDeviceManufacturer() {
        return "" + Build.MANUFACTURER;
    }
    public static String getDeviceModel() {
        return "" + Build.MODEL;
    }

    public static String getAppVersion(Context context) {
        PackageInfo pInfo = null;
        String verCode = "";
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            verCode = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode;
    }
}
