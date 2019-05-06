package com.pratham.admin.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.DialogInterface;
import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.PermissionResult;
import com.pratham.admin.util.PermissionUtils;
import com.pratham.admin.util.Utility;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.admin.util.ActivityManagePermission.isPermissionsGranted;

public class MainActivity extends BaseActivity implements DialogInterface, ConnectionReceiverListener, PermissionResult {

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

    String lastOfflineSavedDate;
    String crlName;
    String crlID;
    boolean internetIsAvailable = false;
    private PermissionResult permissionResult;
    private String permissionsAsk[];
    private final int KEY_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            String[] permissionArray = new String[]{PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_ACCESS_FINE_LOCATION};
            if (!isPermissionsGranted(MainActivity.this, permissionArray))
                askCompactPermissions(permissionArray, this);
        }

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pinfo.versionName;
            SharedPreferences preferences = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
            String version = preferences.getString("version", "null");
            if (!versionName.equals(version)) {
                clearData();
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


    @Override
    protected void onResume() {
        //todo username password remove comment
        super.onResume();

//        userName.setText("amolmoghe");
//        password.setText("pratham@123");

        // check connection & then upgrade latest version if available
        ApplicationController.getInstance().setConnectionListener(this);
        checkConnection();

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
                checkVersion();
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
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setMessage("Denying the permissions may cause in application failure." + "\nPermissions can also be given through app settings.");

        alertDialogBuilder.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                /*UPLOAD TO SERVER*/
                dialog.dismiss();
                checkVersion();
            }
        });
        alertDialogBuilder.show();
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
            dialogBuilder.setTitle("Upgrade your app !");
            dialogBuilder.setMessage("Please upgrade your app in order to use latest features.");
            dialogBuilder.setPositiveButton("Upgrade", new android.content.DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface dialog, int whichButton) {

                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.pratham.admin&hl=en")));
                        dialog.dismiss();
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
            });

            dialogBuilder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
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
                    showDialog(Crl.get(i).getFirstName() + " " + Crl.get(i).getLastName());

                    userPass = true;
                    break;
                }
            }
            if (!userPass) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Invalid Credentials");
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
//            BackupDatabase.backup(MainActivity.this);
        }
    }

    private void showDialog(String crlName) {
        List tempList = AppDatabase.getDatabaseInstance(this).getTempStudentDao().getAllempStudent();
        CustomDialog customDialog;
        if (tempList.size() >= 1) {
            customDialog = new CustomDialog(this, tempList, crlName, lastOfflineSavedDate);
            customDialog.show();
        } else {
            openNextActivity();
        }

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

    @Override
    public void openNextActivity() {
        Intent intent = new Intent(MainActivity.this, Dashboard.class);
        intent.putExtra("CRLid", crlID);
        intent.putExtra("CRLname", crlName);
        intent.putExtra("CRLnameSwapStd", crlName + "(" + crlID + ")");
        startActivity(intent);
    }

}