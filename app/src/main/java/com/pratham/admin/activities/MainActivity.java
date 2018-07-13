package com.pratham.admin.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.DialogInterface;
import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.MetaData;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements DialogInterface {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pinfo.versionName;
            SharedPreferences preferences = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
            String version = preferences.getString("version", "null");
            if (!versionName.equals(version)) {
                Toast.makeText(this, "New Version Available", Toast.LENGTH_SHORT).show();
                clearData();
                SharedPreferences sharedPref = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("version", versionName);
                editor.commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "On Exception data cleared", Toast.LENGTH_SHORT).show();
            clearData();
        }
    }

    @Override
    protected void onResume() {
        //todo username password remove comment
        super.onResume();
        //userName.setText("amolmoghe");
        //password.setText("pratham@123");
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

    @OnClick(R.id.btn_login)
    public void loginCheck(View view) {
        String CRLuserName = userName.getText().toString();
        String CRLpassword = password.getText().toString();
        if (CRLuserName.equals("admin") && CRLpassword.equals("admin")) {
            if (AppDatabase.getDatabaseInstance(this).getCRLdao().getAllCRLs().isEmpty()) {
                Intent intent = new Intent(this, SelectProgram.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Enter CRL login details or clear data", Toast.LENGTH_LONG).show();
            }
        } else {
            boolean userPass = false;
            List<CRL> Crl = AppDatabase.getDatabaseInstance(this).getCRLdao().getAllCRLs();
            for (int i = 0; i < Crl.size(); i++) {
                // CRL crl=Crl.get(i);
                if ((Crl.get(i).getUserName().equals(CRLuserName)) && (Crl.get(i).getPassword().equals(CRLpassword))) {

                    String crlID = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getCrlMetaData();
                    if (crlID != null) {
                        if (!(crlID.equals(Crl.get(i).getCRLId()))) {
                            AppDatabase.getDatabaseInstance(this).getTempStudentDao().deleteTempStudent();
                        }
                    }
                    MetaData metaData = new MetaData();
                    metaData.setKeys("CRLloginTime");
                    metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));
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
        AppDatabase.getDatabaseInstance(this).getStudentDao().deleteAllStudents();
        AppDatabase.getDatabaseInstance(this).getVillageDao().deleteAllVillages();
        AppDatabase.getDatabaseInstance(this).getCRLdao().deleteAllCRLs();
        AppDatabase.getDatabaseInstance(this).getTempStudentDao().deleteTempStudent();
        AppDatabase.destroyInstance();
        SharedPreferences preferences = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        programInfoLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void openNextActivity() {
        //todo show dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.option_dialog);
        RelativeLayout scan_qr_code = (RelativeLayout) dialog.findViewById(R.id.scan_qr_code);
        RelativeLayout swap_student = (RelativeLayout) dialog.findViewById(R.id.swap_student);
        scan_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity_QRScan.class);
                intent.putExtra("CRLid", crlID);
                intent.putExtra("CRLname", crlName);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        swap_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SwapStudentsActivity.class);
                intent.putExtra("CRLname", crlName + "(" + crlID + ")");
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
