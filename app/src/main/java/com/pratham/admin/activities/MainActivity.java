package com.pratham.admin.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.async.NetworkCalls;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.DialogInterface;
import com.pratham.admin.interfaces.NetworkCallListener;
import com.pratham.admin.modalclasses.Aser;
import com.pratham.admin.modalclasses.Attendance;
import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.Completion;
import com.pratham.admin.modalclasses.ECEAsmt;
import com.pratham.admin.modalclasses.EventMessage;
import com.pratham.admin.modalclasses.GroupSession;
import com.pratham.admin.modalclasses.GroupVisit;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.LocaleManager;
import com.pratham.admin.util.PermissionResult;
import com.pratham.admin.util.PermissionUtils;
import com.pratham.admin.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.admin.util.APIs.PushForms;
import static com.pratham.admin.util.ActivityManagePermission.isPermissionsGranted;

public class MainActivity extends BaseActivity implements DialogInterface, ConnectionReceiverListener, PermissionResult, NetworkCallListener {

    @BindView(R.id.userName)
    EditText userName;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.programInfo)
    ConstraintLayout programInfoLayout;
    @BindView(R.id.tv_selProg)
    TextView selProg;
    @BindView(R.id.tv_selState)
    TextView selState;
    @BindView(R.id.tv_selVillage)
    TextView selVillage;

    @BindView(R.id.tv_english)
    TextView engLang;
    @BindView(R.id.tv_hindi)
    TextView hinLang;
    @BindView(R.id.tv_updateApp)
    TextView updateApp;

    String lastOfflineSavedDate;
    String crlName;
    String crlID;
    boolean internetIsAvailable = false;
    private PermissionResult permissionResult;
    private String permissionsAsk[];
    private final int KEY_PERMISSION = 200;

    boolean isUpdateClicked = false;
    Context context;
    Locale myLocale;
    String currentLanguage = "en", currentLang;
    String hindiLang = "hi";
    String englishLang = "en";


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = MainActivity.this;
        addMetaData();
//        Toast.makeText(this, "New Version", Toast.LENGTH_SHORT).show();
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            String[] permissionArray = new String[]{ PermissionUtils.Manifest_READ_EXTERNAL_STORAGE,PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_ACCESS_FINE_LOCATION};
            if (!isPermissionsGranted(MainActivity.this, permissionArray))
                askCompactPermissions(permissionArray, this);
        }

        // check connection & then upgrade latest version if available
        ApplicationController.getInstance().setConnectionListener(this);
        checkConnection();

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pinfo.versionName;
            SharedPreferences preferences = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
            String version = preferences.getString("version", "null");
            if (!versionName.equals(version)) {
              //  clearData();
                SharedPreferences sharedPref = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("version", versionName);
                editor.commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("MainActivity" + "_" + "packageNameOnCreate");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());

            e.printStackTrace();
            Toast.makeText(ApplicationController.getInstance(), "On Exception data cleared", Toast.LENGTH_SHORT).show();
           clearData();
        }
    }


    public void addMetaData() {
        MetaData metaData;

        metaData = new MetaData();
        metaData.setKeys("DeviceName");
        metaData.setValue(Utility.getDeviceName());
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);


        metaData = new MetaData();
        metaData.setKeys("DeviceID");
        metaData.setValue("" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("SerialID");
        metaData.setValue(Utility.getDeviceSerialID());
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("WiFiMac");
        metaData.setValue(Utility.getWifiMac(context));
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("osVersionNum");
        metaData.setValue(Utility.getAndroidOSVersion());
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("osVersionName");
        metaData.setValue(Utility.getOSVersion());
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("osApiLevel");
        metaData.setValue("" + Utility.getApiLevel());
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("internalAvailableStorage");
        metaData.setValue(Utility.getInternalStorageStatus());
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("screenResolution");
        metaData.setValue(Utility.getDeviceResolution(this));
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("manufacturer");
        metaData.setValue(Utility.getDeviceManufacturer());
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("model");
        metaData.setValue(Utility.getDeviceModel());
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("apkVersion");
        metaData.setValue(Utility.getAppVersion(this));
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

        metaData = new MetaData();
        metaData.setKeys("DBVersion");
        metaData.setValue(""+AppDatabase.DB_VERSION);
        AppDatabase.getDatabaseInstance(context).getMetaDataDao().insertMetadata(metaData);

    }


    public void askCompactPermissions(String permissions[], PermissionResult permissionResult) {
        permissionsAsk = permissions;
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }

    public boolean isPermissionGranted(Context context, String permission) {
        boolean granted = ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED));
        return granted;
    }


    private void internalRequestPermission(String[] permissionAsk) {
        String arrayPermissionNotGranted[];
        ArrayList<String> permissionsNotGranted = new ArrayList<>();

        for (int i = 0; i < permissionAsk.length; i++) {
            if (!isPermissionGranted(MainActivity.this, permissionAsk[i])) {
                permissionsNotGranted.add(permissionAsk[i]);
            }
        }

        if (permissionsNotGranted.isEmpty()) {

            if (permissionResult != null)
                permissionResult.permissionGranted();
        } else {

            arrayPermissionNotGranted = new String[permissionsNotGranted.size()];
            arrayPermissionNotGranted = permissionsNotGranted.toArray(arrayPermissionNotGranted);
            ActivityCompat.requestPermissions(MainActivity.this, arrayPermissionNotGranted, KEY_PERMISSION);
        }
    }

    @Subscribe
    public void onMessageEvent(EventMessage eventMessage) {
        if (eventMessage.getMessage().equalsIgnoreCase("GOT")) {
            EventMessage startUpdate = new EventMessage();
            startUpdate.setMessage("CHECK_UPDATE");
            EventBus.getDefault().post(startUpdate);
        } else if (eventMessage.getMessage().equalsIgnoreCase("UPDATE_AVAILABLE")) {
            Log.e("#", "AVAILABLE");
            showUpdateButton();
        }
    }

    public void showUpdateButton() {
        updateApp.setVisibility(View.VISIBLE);
        updateApp.setOnClickListener(v -> {
            EventMessage startUpdate = new EventMessage();
            startUpdate.setMessage("START_UPDATE");
            EventBus.getDefault().post(startUpdate);
            updateApp.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*userName.setText("ganeshtupe54");
        password.setText("pratham");*/
        /*userName.setText("pravinthorat");
        password.setText("pratham123");*/


        userName.setText("");
        password.setText("");
        userName.requestFocus();
        SharedPreferences preferences = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        String program = preferences.getString("program", "null");
        String state = preferences.getString("state", "null");
        String village = preferences.getString("village", "null");
        lastOfflineSavedDate = preferences.getString("offlineSaveTime", "null");
        if ((!program.equals("null")) && (!state.equals("null")) && (!village.equals("null"))) {
            programInfoLayout.setVisibility(View.VISIBLE);
            selProg.setText(program);
            selState.setText(state);
            selVillage.setText(village);
        } else {
            programInfoLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void checkConnection() {

        try {
            boolean isConnected = ConnectionReceiver.isConnected();
            if (!isConnected) {
                internetIsAvailable = false;
            } else {
                internetIsAvailable = true;
                //checkVersion();
            }
        } catch (Exception e) {
            e.printStackTrace();

            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("MainActivity" + "_" + "checkConnection");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            internetIsAvailable = false;
            Toast.makeText(this, R.string.noInterntCon, Toast.LENGTH_SHORT).show();
        } else {
            internetIsAvailable = true;
        }
    }

    @Override
    public void permissionGranted() {

    }

    @Override
    public void permissionDenied() {
        showPermissionWarningDilog();
    }

    @Override
    public void permissionForeverDenied() {
        showPermissionWarningDilog();
    }

    private void showPermissionWarningDilog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.alert));
        alertDialogBuilder.setMessage("" + "\n" + R.string.denyPermission);

        alertDialogBuilder.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                /*UPLOAD TO SERVER*/
                dialog.dismiss();

            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public void onResponse(String response, String header) {
        /*if (isUpdateClicked)
            updateApp();*/
    }

    private void updateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pratham.admin&hl=en")));
        } catch (Exception e) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("MainActivity" + "_" + "checkVersion");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());

            e.printStackTrace();
        }
    }

    @Override
    public void onError(ANError anError, String header) {
        Toast.makeText(this, R.string.datapushfailed, Toast.LENGTH_SHORT).show();
    }


    private class GetLatestVersion extends AsyncTask<String, String, String> {
        String latestVersion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                String urlOfAppFromPlayStore = "https://play.google.com/store/apps/details?id=com.pratham.admin&hl=en";
                // Document doc = w3cDom.fromJsoup(Jsoup.connect(urlOfAppFromPlayStore).get());
                //Log.d(TAG,"playstore doc "+getStringFromDoc(doc));
                latestVersion = Jsoup.connect(urlOfAppFromPlayStore)
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
                Log.d("latest::", latestVersion);
                //latestVersion = doc.getElementsByTagName("softwareVersion").first().text();
                //latestVersion = "1.5";
            } catch (Exception e) {
                Modal_Log log = new Modal_Log();
                log.setCurrentDateTime(new Utility().GetCurrentDate());
                log.setErrorType("ERROR");
                log.setExceptionMessage(e.getMessage());
                log.setExceptionStackTrace(e.getStackTrace().toString());
                log.setMethodName("MainActivity" + "_" + "getLatestVersion");
                log.setDeviceId("");
                AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                BackupDatabase.backup(ApplicationController.getInstance());

                e.printStackTrace();
            }
            return latestVersion;
        }
    }

    private void checkVersion() {
        String latestVersion = "";
        String currentVersion = Utility.getCurrentVersion(MainActivity.this);
        Log.d("version::", "Current version = " + currentVersion);
        try {
            latestVersion = new GetLatestVersion().execute().get();
            Log.d("version::", "Latest version = " + latestVersion);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // Force Update Code
        if ((!currentVersion.equals(latestVersion)) && latestVersion != null) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setIcon(R.drawable.ic_warning);
            dialogBuilder.setTitle(R.string.upgradeApp);
            dialogBuilder.setMessage(R.string.plzUpgradeApp);
            dialogBuilder.setPositiveButton(R.string.upgrade, new android.content.DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface dialog, int whichButton) {
                    pushNewData();
                    dialog.dismiss();
                    isUpdateClicked = true;
                }
            });

            dialogBuilder.setNegativeButton(R.string.Cancel, new android.content.DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface dialog, int whichButton) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            AlertDialog b = dialogBuilder.create();
            b.show();
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

    public void pushNewData() {
        final List<Attendance> aObj;
        final List<Coach> coachesObj;
        final List<Community> communitiesObj;
        final List<Completion> completionsObj;
        final List<GroupSession> GroupSessionObj;
        final List<GroupVisit> GroupVisitObj;
        final List<Student> stdObj;
        final List<Aser> aserObj;
        final List<Groups> grpObj;
        final List<ECEAsmt> ECEAsmtObj;
        final List<Modal_Log> LogObj;
        List<MetaData> metaDataList;
        checkConnection();

        aObj = AppDatabase.getDatabaseInstance(this).getAttendanceDao().getNewAttendances(0);
        coachesObj = AppDatabase.getDatabaseInstance(this).getCoachDao().getNewCoaches(0);
        communitiesObj = AppDatabase.getDatabaseInstance(this).getCommunityDao().getNewCommunities(0);
        completionsObj = AppDatabase.getDatabaseInstance(this).getCompletionDao().getNewCompletions(0);
        GroupSessionObj = AppDatabase.getDatabaseInstance(this).getGroupSessionDao().getNewGroupSessions(0);
        GroupVisitObj = AppDatabase.getDatabaseInstance(this).getGroupVisitDao().getNewGroupVisits(0);
        stdObj = AppDatabase.getDatabaseInstance(this).getStudentDao().getNewStudents(0);
        aserObj = AppDatabase.getDatabaseInstance(this).getAserDao().getNewAser(0);
        grpObj = AppDatabase.getDatabaseInstance(this).getGroupDao().getNewGroups(0);
        ECEAsmtObj = AppDatabase.getDatabaseInstance(this).getECEAsmtDao().getNewECEAsmt(0);
        LogObj = AppDatabase.getDatabaseInstance(this).getLogDao().getAllLogs(0);

        // Push To Server
        try {
            if (internetIsAvailable) {

                // Preview
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setTitle(R.string.dataPreview);

                // Prepare Data
                Gson gson = new Gson();
                MetaData metaData = new MetaData();
                metaData.setKeys("pushDataTime");
                metaData.setValue(new Utility().GetCurrentDateTime(false));
                metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
                String metaDataJSON = customParse(metaDataList);

                final String json = "{ \"AttendanceJSON\":" + "" + gson.toJson(aObj).toString()
                        + ",\"CoachesJSON\":" + "" + gson.toJson(coachesObj).toString()
                        + ",\"CommunitiesJSON\":" + "" + gson.toJson(communitiesObj).toString()
                        + ",\"CompletionsJSON\":" + "" + gson.toJson(completionsObj).toString()
                        + ",\"StudentJSON\":" + "" + gson.toJson(stdObj).toString()
                        + ",\"AserJSON\":" + "" + gson.toJson(aserObj).toString()
                        + ",\"GroupsJSON\":" + "" + gson.toJson(grpObj).toString()
                        + ",\"GroupVisitsJSON\":" + "" + gson.toJson(GroupVisitObj).toString()
                        + ",\"GroupSessionJSON\":" + "" + gson.toJson(GroupSessionObj).toString()
                        + ",\"ECEAsmtJSON\":" + "" + gson.toJson(ECEAsmtObj).toString()
                        + ",\"LogJSON\":" + "" + gson.toJson(LogObj).toString()
                        + ",\"metadata\":" + "" + metaDataJSON + "}";

                //     Log.d("json all push :::", json);

                // Preview Message
                dialogBuilder.setTitle(R.string.pushDataPreview);
                dialogBuilder.setMessage("Attendance : " + aObj.size()
                        + "\nCoaches : " + coachesObj.size()
                        + "\nCommunities : " + communitiesObj.size()
                        + "\nCompletions : " + completionsObj.size()
                        + "\nStudents : " + stdObj.size()
                        + "\nAsers : " + aserObj.size()
                        + "\nGroups : " + grpObj.size()
                        + "\nGroupVisits : " + GroupVisitObj.size()
                        + "\nGroupSessions : " + GroupSessionObj.size()
                        + "\nECE Assessments : " + ECEAsmtObj.size()
                        + "\nLogs : " + LogObj.size()
                );


                dialogBuilder.setPositiveButton(R.string.confirm, new android.content.DialogInterface.OnClickListener() {
                    public void onClick(android.content.DialogInterface dialog, int whichButton) {

                        try {
                            // Push Process
                            ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setTitle(R.string.uploading);
                            pd.setCancelable(false);
                            pd.setCanceledOnTouchOutside(false);
                            pd.show();

                            if ((aObj.size() == 0) && (coachesObj.size() == 0) && (communitiesObj.size() == 0) && (completionsObj.size() == 0)
                                    && (GroupVisitObj.size() == 0) && (GroupSessionObj.size() == 0)
                                    && (stdObj.size() == 0) && (aserObj.size() == 0) && (grpObj.size() == 0) && (ECEAsmtObj.size() == 0) && (LogObj.size() == 0)
                                /*&& (CRLVisitObj.size() == 0)*/) {
                                // No Data Available
                                //updateApp();
                                Toast.makeText(MainActivity.this, R.string.noNewDataPush, Toast.LENGTH_LONG).show();
                                Log.d("json not pushed:::", json);
                                if (pd.isShowing())
                                    pd.dismiss();
                            } else {
                                pd.dismiss();
                                NetworkCalls.getNetworkCallsInstance(MainActivity.this).postRequest(MainActivity.this, PushForms, "uploading...", json, "push_forms");
                               /* AndroidNetworking.post(PushForms).setContentType("application/json").addStringBody(json).build().getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("responce", response);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getMetaDataDao().insertMetadata(metaData);
                                        showDialog(true);
                                        // update flag
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getAttendanceDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCoachDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCommunityDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCompletionDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupSessionDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupVisitDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getStudentDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getAserDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getECEAsmtDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getLogDao().updateAllSentFlag(1);


                                        if (pd.isShowing())
                                            pd.dismiss();
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        new BlurPopupWindow.Builder(Dashboard.this)
                                                .setContentView(R.layout.app_failure_dialog)
                                                .setGravity(Gravity.CENTER)
                                                .setScaleRatio(0.2f)
                                                .setDismissOnClickBack(true)
                                                .setDismissOnTouchBackground(true)
                                                .setBlurRadius(10)
                                                .setTintColor(0x30000000)
                                                .build()
                                                .show();
//                                        No need to reset flag as All the Data will be either sent or not at all
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getAttendanceDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCoachDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCommunityDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCompletionDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupVisitDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupSessionDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getStudentDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getAserDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getECEAsmtDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getLogDao().updateAllSentFlag(0);

                                        Toast.makeText(Dashboard.this, "Error in Data Pushing !", Toast.LENGTH_LONG).show();
                                        if (pd.isShowing())
                                            pd.dismiss();
                                    }
                                });*/
                            }
                        } catch (Exception e) {
                            Modal_Log log = new Modal_Log();
                            log.setCurrentDateTime(new Utility().GetCurrentDate());
                            log.setErrorType("ERROR");
                            log.setExceptionMessage(e.getMessage());
                            log.setExceptionStackTrace(e.getStackTrace().toString());
                            log.setMethodName("Dashboard" + "_" + "PushNewData");
                            log.setDeviceId("");
                            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                            BackupDatabase.backup(ApplicationController.getInstance());

                            e.printStackTrace();
                        }

                        dialog.dismiss();

                    }
                });

                dialogBuilder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(android.content.DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        isUpdateClicked = false;
                    }
                });

                AlertDialog b = dialogBuilder.create();
                b.show();
            } else {
                //No Internet
                try {
                    new BlurPopupWindow.Builder(MainActivity.this)
                            .setContentView(R.layout.app_failure_dialog)
                            .setGravity(Gravity.CENTER)
                            .setScaleRatio(0.2f)
                            .setDismissOnClickBack(true)
                            .setDismissOnTouchBackground(true)
                            .setBlurRadius(10)
                            .setTintColor(0x30000000)
                            .build()
                            .show();
                } catch (Exception e) {

                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("Dashboard" + "_" + "PushNewData");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());

            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_login)
    public void loginCheck(View view) {
        String CRLuserName = userName.getText().toString();
        String CRLpassword = password.getText().toString();
        if (CRLuserName.equals("admin") && CRLpassword.equals("admin")) {
            Intent intent = new Intent(this, SelectProgram.class);
            startActivity(intent);
        } else {
            boolean userPass = false;

            List<CRL> Crl = AppDatabase.getDatabaseInstance(this).getCRLdao().getAllCRLs();
            for (int i = 0; i < Crl.size(); i++) {
                if ((Crl.get(i).getUserName().equals(CRLuserName)) && (Crl.get(i).getPassword().equals(CRLpassword))) {

                    String crlID = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getCrlMetaData();
                    if (crlID != null) {
                        if (!(crlID.equals(Crl.get(i).getCRLId()))) {
                            AppDatabase.getDatabaseInstance(this).getTempStudentDao().deleteTempStudent();
                        }
                    }
                    MetaData metaData = new MetaData();
                    metaData.setKeys("CRLloginTime");
//                    metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));
                    metaData.setValue(new Utility().GetCurrentDateTime(false));
                    AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

                    metaData.setKeys("CRL_ID");
                    metaData.setValue(Crl.get(i).getCRLId());
                    AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);


                    AppDatabase.destroyInstance();
                    crlName = Crl.get(i).getFirstName() + " " + Crl.get(i).getLastName(); //+ " (" + Crl.get(i).getCRLId() + ")";
                    this.crlID = Crl.get(i).getCRLId();
                    //showDialog();
                    pushDataOnLogin();

                    userPass = true;
                    break;
                }
            }
            if (!userPass) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(R.string.invalidCredential);
                alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                alertDialog.setButton("OK", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        userName.setText("");
                        password.setText("");
                        userName.requestFocus();
                    }
                });
                alertDialog.show();
            }
        }

        // Make a db backup if Storage permission granted
        if (isPermissionGranted(MainActivity.this, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE)) {
            // Initiate Backup DB
            BackupDatabase.backup(MainActivity.this);
        }
    }

    private void showDialog() {
        //todo push data when crl logs in
        pushDataOnLogin();

//        openNextActivity();
        //todo
        /*List tempList = AppDatabase.getDatabaseInstance(this).getTempStudentDao().getAllempStudent();
        CustomDialog customDialog;
        if (tempList.size() >= 1) {
            customDialog = new CustomDialog(this, tempList, crlName, lastOfflineSavedDate);
            customDialog.show();
        } else {
            openNextActivity();
        }*/

    }

    private void pushDataOnLogin() {
       /* final List<Attendance> aObj;
        final List<Coach> coachesObj;
        final List<Community> communitiesObj;
        final List<Completion> completionsObj;
        final List<GroupSession> GroupSessionObj;
        final List<GroupVisit> GroupVisitObj;
        final List<Student> stdObj;
        final List<Aser> aserObj;
        final List<Groups> grpObj;
        final List<ECEAsmt> ECEAsmtObj;
        final List<Modal_Log> LogObj;*/
        List<MetaData> metaDataList;
        checkConnection();

       /* aObj = AppDatabase.getDatabaseInstance(this).getAttendanceDao().getNewAttendances(0);
        coachesObj = AppDatabase.getDatabaseInstance(this).getCoachDao().getNewCoaches(0);
        communitiesObj = AppDatabase.getDatabaseInstance(this).getCommunityDao().getNewCommunities(0);
        completionsObj = AppDatabase.getDatabaseInstance(this).getCompletionDao().getNewCompletions(0);
        GroupSessionObj = AppDatabase.getDatabaseInstance(this).getGroupSessionDao().getNewGroupSessions(0);
        GroupVisitObj = AppDatabase.getDatabaseInstance(this).getGroupVisitDao().getNewGroupVisits(0);
        stdObj = AppDatabase.getDatabaseInstance(this).getStudentDao().getNewStudents(0);
        aserObj = AppDatabase.getDatabaseInstance(this).getAserDao().getNewAser(0);
        grpObj = AppDatabase.getDatabaseInstance(this).getGroupDao().getNewGroups(0);
        ECEAsmtObj = AppDatabase.getDatabaseInstance(this).getECEAsmtDao().getNewECEAsmt(0);
        LogObj = AppDatabase.getDatabaseInstance(this).getLogDao().getAllLogs(0);*/

        // Push To Server
        try {
            if (internetIsAvailable) {

                // Preview
               /* AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setTitle("Data Preview");*/

                // Prepare Data

                String metaData = addMetaDataToJson();
//            String json = gson.toJson(mainList);
                String json = "{ \"ManageDevicesJSON\":" + "" + "[]"
                        + ",\"MetaData\":" + "" + metaData + "}";


                /*Gson gson = new Gson();
                MetaData metaData = new MetaData();
                metaData.setKeys("pushDataTime");
                metaData.setValue(new Utility().GetCurrentDateTime(false));
                metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
                String metaDataJSON = customParse(metaDataList);

                final String json = "{ \"AttendanceJSON\":" + "" + gson.toJson(aObj).toString()
                        + ",\"CoachesJSON\":" + "" + gson.toJson(coachesObj).toString()
                        + ",\"CommunitiesJSON\":" + "" + gson.toJson(communitiesObj).toString()
                        + ",\"CompletionsJSON\":" + "" + gson.toJson(completionsObj).toString()
                        + ",\"StudentJSON\":" + "" + gson.toJson(stdObj).toString()
                        + ",\"AserJSON\":" + "" + gson.toJson(aserObj).toString()
                        + ",\"GroupsJSON\":" + "" + gson.toJson(grpObj).toString()
                        + ",\"GroupVisitsJSON\":" + "" + gson.toJson(GroupVisitObj).toString()
                        + ",\"GroupSessionJSON\":" + "" + gson.toJson(GroupSessionObj).toString()
                        + ",\"ECEAsmtJSON\":" + "" + gson.toJson(ECEAsmtObj).toString()
                        + ",\"LogJSON\":" + "" + gson.toJson(LogObj).toString()
                        + ",\"metadata\":" + "" + metaDataJSON + "}";
*/
                //     Log.d("json all push :::", json);

                // Preview Message
               /* dialogBuilder.setTitle("Push Data Preview");
                dialogBuilder.setMessage("Attendance : " + aObj.size()
                        + "\nCoaches : " + coachesObj.size()
                        + "\nCommunities : " + communitiesObj.size()
                        + "\nCompletions : " + completionsObj.size()
                        + "\nStudents : " + stdObj.size()
                        + "\nAsers : " + aserObj.size()
                        + "\nGroups : " + grpObj.size()
                        + "\nGroupVisits : " + GroupVisitObj.size()
                        + "\nGroupSessions : " + GroupSessionObj.size()
                        + "\nECE Assessments : " + ECEAsmtObj.size()
                        + "\nLogs : " + LogObj.size()
                );*/


             /*   dialogBuilder.setPositiveButton("Confirm", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(android.content.DialogInterface dialog, int whichButton) {
*/
                try {
                    // Push Process
                    ProgressDialog pd = new ProgressDialog(MainActivity.this);
                    pd.setTitle("UPLOADING ... ");
                    pd.setCancelable(false);
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();

                            /*if ((aObj.size() == 0) && (coachesObj.size() == 0) && (communitiesObj.size() == 0) && (completionsObj.size() == 0)
                                    && (GroupVisitObj.size() == 0) && (GroupSessionObj.size() == 0)
                                    && (stdObj.size() == 0) && (aserObj.size() == 0) && (grpObj.size() == 0) && (ECEAsmtObj.size() == 0) && (LogObj.size() == 0)
                                *//*&& (CRLVisitObj.size() == 0)*//*) {
                                // No Data Available
//                                updateApp();
                                Toast.makeText(MainActivity.this, "No New Data found for Pushing !", Toast.LENGTH_LONG).show();
                                Log.d("json not pushed:::", json);
                                if (pd.isShowing())
                                    pd.dismiss();
                            } else {*/
//                    pd.dismiss();
                    NetworkCalls.getNetworkCallsInstance(MainActivity.this).postRequest(MainActivity.this, APIs.ReplaceTab, "uploading...", json, "push_forms");
                    pd.dismiss();
                               /* AndroidNetworking.post(PushForms).setContentType("application/json").addStringBody(json).build().getAsString(new StringRequestListener() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("responce", response);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getMetaDataDao().insertMetadata(metaData);
                                        showDialog(true);
                                        // update flag
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getAttendanceDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCoachDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCommunityDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCompletionDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupSessionDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupVisitDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getStudentDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getAserDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getECEAsmtDao().updateAllSentFlag(1);
                                        AppDatabase.getDatabaseInstance(Dashboard.this).getLogDao().updateAllSentFlag(1);


                                        if (pd.isShowing())
                                            pd.dismiss();
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        new BlurPopupWindow.Builder(Dashboard.this)
                                                .setContentView(R.layout.app_failure_dialog)
                                                .setGravity(Gravity.CENTER)
                                                .setScaleRatio(0.2f)
                                                .setDismissOnClickBack(true)
                                                .setDismissOnTouchBackground(true)
                                                .setBlurRadius(10)
                                                .setTintColor(0x30000000)
                                                .build()
                                                .show();
//                                        No need to reset flag as All the Data will be either sent or not at all
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getAttendanceDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCoachDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCommunityDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getCompletionDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupVisitDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupSessionDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getStudentDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getAserDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getGroupDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getECEAsmtDao().updateAllSentFlag(0);
//                                        AppDatabase.getDatabaseInstance(Dashboard.this).getLogDao().updateAllSentFlag(0);

                                        Toast.makeText(Dashboard.this, "Error in Data Pushing !", Toast.LENGTH_LONG).show();
                                        if (pd.isShowing())
                                            pd.dismiss();
                                    }
                                });*/
//                            }
                } catch (Exception e) {
                    Modal_Log log = new Modal_Log();
                    log.setCurrentDateTime(new Utility().GetCurrentDate());
                    log.setErrorType("ERROR");
                    log.setExceptionMessage(e.getMessage());
                    log.setExceptionStackTrace(e.getStackTrace().toString());
                    log.setMethodName("Dashboard" + "_" + "PushNewData");
                    log.setDeviceId("");
                    AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                    BackupDatabase.backup(ApplicationController.getInstance());

                    e.printStackTrace();
                }

//                    }
                /* });*/

               /* dialogBuilder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(android.content.DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                AlertDialog b = dialogBuilder.create();
                b.show();*/
            } else {
                //No Internet
                Toast.makeText(this, R.string.noInterntCon, Toast.LENGTH_SHORT).show();

               /* try {
                    new BlurPopupWindow.Builder(MainActivity.this)
                            .setContentView(R.layout.app_failure_dialog)
                            .setGravity(Gravity.CENTER)
                            .setScaleRatio(0.2f)
                            .setDismissOnClickBack(true)
                            .setDismissOnTouchBackground(true)
                            .setBlurRadius(10)
                            .setTintColor(0x30000000)
                            .build()
                            .show();
                } catch (Exception e) {

                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }*/

            }

            openNextActivity();

        } catch (Exception e) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("Dashboard" + "_" + "PushNewData");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());

            e.printStackTrace();
        }
    }

    private String addMetaDataToJson() {

        MetaData metaData = new MetaData();
        metaData.setKeys("pushDataTime");
        metaData.setValue(new Utility().GetCurrentDateTime(false));
        List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
        String metaDataJSON = customParse(metaDataList);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);
        return metaDataJSON;
    }

    @OnClick(R.id.btn_clearData)
    public void clearData() {
        userName.setText("");
        password.setText("");
        AppDatabase.getDatabaseInstance(this).getGroupDao().deleteAllGroups();
        AppDatabase.getDatabaseInstance(this).getAserDao().deleteAllAser();
        AppDatabase.getDatabaseInstance(this).getStudentDao().deleteAllStudents();
        AppDatabase.getDatabaseInstance(this).getVillageDao().deleteAllVillages();
        AppDatabase.getDatabaseInstance(this).getCRLdao().deleteAllCRLs();
        AppDatabase.getDatabaseInstance(this).getECEAsmtDao().deleteAllECEAsmt();
        AppDatabase.getDatabaseInstance(this).getAttendanceDao().deleteAllAttendances();
        AppDatabase.getDatabaseInstance(this).getTempStudentDao().deleteTempStudent();
        AppDatabase.getDatabaseInstance(this).getCoachDao().deleteAllCoaches();
        AppDatabase.getDatabaseInstance(this).getCoursesDao().deleteAllCourses();
        AppDatabase.getDatabaseInstance(this).getCommunityDao().deleteAllCommunity();
        AppDatabase.getDatabaseInstance(this).getCompletionDao().deleteAllCompletion();
        AppDatabase.getDatabaseInstance(this).getGroupSessionDao().deleteAllGroupSession();
        AppDatabase.getDatabaseInstance(this).getGroupVisitDao().deleteAllGroupVisit();
        AppDatabase.getDatabaseInstance(this).getLogDao().deleteLogs();
        AppDatabase.getDatabaseInstance(this).getYouthDao().deleteAllYouths();
        AppDatabase.destroyInstance();
        SharedPreferences preferences = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pinfo.versionName;
            SharedPreferences pref = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = pref.edit();
            ed.putString("version", versionName);
            ed.commit();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        programInfoLayout.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.tv_english)
    public void setEngLang() {
        setNewLocale(this, LocaleManager.ENGLISH);
    }

    @OnClick(R.id.tv_hindi)
    public void setHinLang() {
        setNewLocale(this, LocaleManager.HINDI);
    }

    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    /*public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            Toast.makeText(MainActivity.this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public void openNextActivity() {
        Intent intent = new Intent(MainActivity.this, Dashboard.class);
        intent.putExtra("CRLid", crlID);
        intent.putExtra("CRLname", crlName);
        intent.putExtra("CRLnameSwapStd", crlName + "(" + crlID + ")");
        intent.putExtra("localeName", hindiLang);
        startActivity(intent);
        finish();
    }

}