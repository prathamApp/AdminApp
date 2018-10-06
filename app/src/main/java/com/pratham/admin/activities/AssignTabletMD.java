package com.pratham.admin.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.zxing.Result;
import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.QRScanListener;
import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.CrlInfoRecycler;
import com.pratham.admin.modalclasses.TabletManageDevice;
import com.pratham.admin.util.ConnectionReceiver;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AssignTabletMD extends AppCompatActivity implements ZXingScannerView.ResultHandler, ConnectionReceiverListener, QRScanListener {
    @BindView(R.id.program)
    Spinner programSpinner;

    @BindView(R.id.roleCrl)
    Spinner roleCrlSpinner;

    /*@BindView(R.id.block)
    Spinner blockSpinner;*/
    @BindView(R.id.assignedCrlName)
    TextView assignedCrlName;
    @BindView(R.id.assignedCrlId)
    TextView assignedCrlId;
    @BindView(R.id.crlName)
    Spinner crlNameSpinner;

    @BindView(R.id.qr_frame)
    FrameLayout qr_frame;

    @BindView(R.id.qr_pratham_id)
    EditText qr_pratham_id;
    @BindView(R.id.qr_serialNo)
    EditText qr_serialNo;
    @BindView(R.id.txt_count_village)
    TextView txt_count;
    public ZXingScannerView mScannerView;
    String prathamId;
    String QrId;

    @BindView(R.id.successMessage)
    LinearLayout successMessage;


    @BindView(R.id.isDamaged)
    Spinner isDamaged;
    @BindView(R.id.damageType)
    Spinner damageType;
    @BindView(R.id.comments)
    EditText comments;

   /* @BindView(R.id.assigned_crl_name)
    TextView assigned_crl_name;
    @BindView(R.id.assigned_crl_Id)
    TextView assigned_crl_Id;*/


    private Context context;
    List<CRL> allCRLlist;
    boolean permission = false;
    String LoggedcrlName;
    String LoggedcrlId;
    String assignedCRL = "";
    CustomDialogQRScan_MD customDialogQRScan_md;
    List<TabletManageDevice> tabletMD;
    boolean internetIsAvailable = false;
    String tabStatus, isDamagedText, damageTypeText;
    final String ASSIGN = "Assign";
    final String RETURN = "Return";
    List<CrlInfoRecycler> crlList_md = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_tablet);
        ButterKnife.bind(this);
        context = AssignTabletMD.this;
        allCRLlist = new ArrayList<>();
        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        tabStatus = getIntent().getStringExtra("tabStatus");
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (tabStatus.equals(ASSIGN)) {
            ab.setTitle("Assign Tablet");
            isDamaged.setVisibility(View.GONE);
            damageType.setVisibility(View.GONE);
            comments.setVisibility(View.GONE);
        } else if (tabStatus.equals(RETURN)) {
            ab.setTitle("Return Tablet");
            isDamaged.setVisibility(View.VISIBLE);
            damageType.setVisibility(View.VISIBLE);
            comments.setVisibility(View.VISIBLE);
            setLisnerToRuturnTabletSpinner();
        }
        loadCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        //  int crlCount = AppDatabase.getDatabaseInstance(context).getCRLmd_dao().getCRLs_mdCount();
        int crlCount = AppDatabase.getDatabaseInstance(context).getCRLdao().getCRLsCount();
        if (crlCount <= 0) {
            Toast.makeText(context, "Please pull Data ", Toast.LENGTH_SHORT).show();
            AlertDialog builder = new AlertDialog.Builder(this).create();
            builder.setTitle("No CRL's are available");
            builder.setIcon(R.drawable.ic_error_outline_black_24dp);
            builder.setCancelable(false);
            builder.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AssignTabletMD.this.finish();
                }
            });
            builder.show();


        } else {
            List<TabletManageDevice> oldList = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().getAllTabletManageDevice();
            if (!oldList.isEmpty()) {
                for (int i = 0; i < oldList.size(); i++) {
                    oldList.get(i).setOldFlag(true);
                }
                AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().insertTabletAllManageDevice(oldList);
                oldList.clear();
            }

            loadSpinner();
            // qr_spinner_crl.setText(LoggedcrlName);
            setCount();
        }
    }

    private void setLisnerToRuturnTabletSpinner() {
        isDamaged.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                isDamagedText = adapterView.getSelectedItem().toString();

                if (pos > 0) {
                    if (isDamagedText.equals("No")) {
                        damageType.setSelection(0);
                        damageType.setEnabled(false);
                        damageTypeText = "";
                    } else {
                        damageType.setEnabled(true);
                    }
                } else {
                    damageType.setSelection(0);
                    damageType.setEnabled(false);
                    damageTypeText = "";
                    isDamagedText = "No";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        damageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                damageTypeText = adapterView.getSelectedItem().toString();
                if (pos == 0) {
                    damageTypeText = "";
                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadSpinner() {
        //load spinner program From DB
        List<String> programList = new ArrayList<>();
        //List<String> stateList = new ArrayList<>();

        //List<String> crlList_md = new ArrayList<>();

        programList.add("Select Program");
        // programList.addAll(AppDatabase.getDatabaseInstance(context).getCRLmd_dao().getDistinctCRLs_mdProgram());
        programList.addAll(AppDatabase.getDatabaseInstance(context).getCRLdao().getDistinctCRLsdProgram());
        ArrayAdapter programAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, programList);
        programSpinner.setAdapter(programAdapter);

        programSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                List<String> roleList = new ArrayList<>();
                roleList.add("Select Role");
                if (pos > 0) {
                    // roleList.addAll(AppDatabase.getDatabaseInstance(context).getCRLmd_dao().getDistinctCRLs_mdRoleId());
                    roleList.addAll(AppDatabase.getDatabaseInstance(context).getCRLdao().getDistinctCRLsRoleId());
                }
                ArrayAdapter stateAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, roleList);
                roleCrlSpinner.setAdapter(stateAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //load spinner state from DB
      /*  stateList = AppDatabase.getDatabaseInstance(context).getCRLmd_dao().getDistinctCRLs_mdRoleId();
        ArrayAdapter stateAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,stateList);
        roleCrlSpinner.setPrompt("select state");
        roleCrlSpinner.setAdapter(stateAdapter);*/
        roleCrlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                crlList_md.clear();
                crlList_md.add(new CrlInfoRecycler("Select Name "));
                if (pos > 0) {
                    //load spinner crl from db
                    String selectedRole = roleCrlSpinner.getSelectedItem().toString();
                    // List<CRLmd> tempList = AppDatabase.getDatabaseInstance(context).getCRLmd_dao().getDistinctCRLs_mdUserName(selectedRole, programSpinner.getSelectedItem().toString());
                    List<CRL> tempList = AppDatabase.getDatabaseInstance(context).getCRLdao().getDistinctCRLsUserName(selectedRole, programSpinner.getSelectedItem().toString());
                    for (CRL crL : tempList) {
                        crlList_md.add(new CrlInfoRecycler(crL.getCRLId(), crL.getFirstName(), crL.getUserName()));
                    }
                }
                ArrayAdapter crlAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, crlList_md);
                crlNameSpinner.setAdapter(crlAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        crlNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                if (pos > 0) {
                    CrlInfoRecycler crlInfoRecycler = (CrlInfoRecycler) adapterView.getSelectedItem();
                    assignedCRL = crlInfoRecycler.getCrlId();
                    assignedCrlName.setText(crlInfoRecycler.getCrlName());
                    assignedCrlId.setText("( ID: " + crlInfoRecycler.getCrlId() + ")");

                } else {
                    assignedCRL = "";
                    assignedCrlName.setText("");
                    assignedCrlId.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadCamera() {

        if (ContextCompat.checkSelfPermission(AssignTabletMD.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("App requires camera permission to scan QR code");
                builder.setCancelable(false);
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(AssignTabletMD.this, new String[]{Manifest.permission.CAMERA}, 1);
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(AssignTabletMD.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        } else {
            initCamera();
            permission = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "You Need Camera permission", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                initCamera();
                permission = true;
            }
        }

    }

    public void initCamera() {
        mScannerView = new ZXingScannerView(getApplicationContext());
        mScannerView.setResultHandler(AssignTabletMD.this);
        qr_frame.addView((mScannerView));
        mScannerView.startCamera();
        //    mScannerView.resumeCameraPreview(Activity_QRScan.this);
    }

    @Override
    public void handleResult(Result rawResult) {
        QrId = null;
        prathamId = null;
        Log.d("RawResult:::", "****" + rawResult.getText());
        String result = rawResult.getText();
        mScannerView.stopCamera();
        String[] splitted = result.split(":");


        try {
         /*   JSONObject jsonObject = new JSONObject(result);
            QrId = jsonObject.getString("QRCodeID");
            prathamId = jsonObject.getString("PrathamCode");*/
            QrId = splitted[0];
            prathamId = splitted[1];
            if (QrId != null && prathamId != null && splitted.length == 2) {
                List l = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().checkExistanceTabletManageDevice(QrId);
                if (l.isEmpty()) {
                    qr_pratham_id.setText(prathamId);
                    successMessage.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setMessage("This QR Is Already Scanned. Do You Want To Replace Data?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            qr_pratham_id.setText(prathamId);
                            successMessage.setVisibility(View.VISIBLE);
                            dialogInterface.dismiss();
                       /* AppDatabase.getDatabaseInstance(Activity_QRScan.this).getTabTrackDao().insertTabTrack(tabletStatus);
                        Toast.makeText(Activity_QRScan.this, "Updated Successfully ", Toast.LENGTH_LONG).show();*/
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            resetCamera();
                            dialogInterface.dismiss();
                        }
                    });
                    Dialog dialog = builder.create();
                    dialog.show();

                }
            } else {
                Toast.makeText(this, "Invalid QR ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid QR ", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDestroy() {
        if (mScannerView != null) mScannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (permission) {
            mScannerView.stopCamera();
        }
    }

    @OnClick(R.id.qr_btn_reset)
    public void resetCamera() {
        mScannerView.stopCamera();
        mScannerView.startCamera();
        mScannerView.resumeCameraPreview(AssignTabletMD.this);
        prathamId = "";
        qr_pratham_id.setText(prathamId);
        successMessage.setVisibility(View.GONE);
    }

    @OnClick(R.id.qr_btn_save)
    public void saveTabTrack() {
        String serialNO = qr_serialNo.getText().toString();
        prathamId = qr_pratham_id.getText().toString();
        if (!assignedCRL.equals("")) {
            if ((!serialNO.equals("")) || !prathamId.equals("")) {
                if (tabStatus.equals(RETURN)) {
                    if ((isDamagedText.equals("Yes") && !damageTypeText.equals("")) || isDamagedText.equals("No")) {
                        TabletManageDevice tabletManageDevice = new TabletManageDevice();
                        tabletManageDevice.setQR_ID(QrId);
                        tabletManageDevice.setStatus(tabStatus);
                        tabletManageDevice.setLogged_CRL_ID(LoggedcrlId);
                        tabletManageDevice.setAssigned_CRL_ID(assignedCRL);
                        tabletManageDevice.setAssigned_CRL_Name(assignedCrlName.getText().toString());
                        tabletManageDevice.setPratham_ID(prathamId);
                        tabletManageDevice.setTabSerial_ID(serialNO);
                        tabletManageDevice.setIs_Damaged(isDamagedText);
                        tabletManageDevice.setDamageType(damageTypeText);
                        tabletManageDevice.setComment(comments.getText().toString());
                        tabletManageDevice.setOldFlag(false);
                        /*tabletStatus.setLoggedIn_CRL(LoggedcrlId);*/
                        tabletManageDevice.setDate(DateFormat.getDateTimeInstance().format(new Date()));

                        AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().insertTabletManageDevice(tabletManageDevice);
                        Toast.makeText(AssignTabletMD.this, "Inserted Successfully ", Toast.LENGTH_LONG).show();
                        setCount();
                        cleaFields();
                        resetCamera();
                    } else {
                        Toast.makeText(context, "select damage type", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //Assigned Tab
                    TabletManageDevice tabletManageDevice = new TabletManageDevice();
                    tabletManageDevice.setQR_ID(QrId);
                    tabletManageDevice.setPratham_ID(prathamId);
                    tabletManageDevice.setTabSerial_ID(serialNO);
                    tabletManageDevice.setStatus(tabStatus);
                    tabletManageDevice.setLogged_CRL_ID(LoggedcrlId);
                    tabletManageDevice.setAssigned_CRL_ID(assignedCRL);
                    tabletManageDevice.setAssigned_CRL_Name(assignedCrlName.getText().toString());
                    tabletManageDevice.setOldFlag(false);
                    tabletManageDevice.setDate(DateFormat.getDateTimeInstance().format(new Date()));

                    AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().insertTabletManageDevice(tabletManageDevice);
                    Toast.makeText(AssignTabletMD.this, "Inserted Successfully ", Toast.LENGTH_LONG).show();
                    setCount();
                    cleaFields();
                    resetCamera();
                }
            } else {
                Toast.makeText(context, "Scan QR Or Enter Serial Id", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Fill In All The Details", Toast.LENGTH_SHORT).show();
        }
    }

    public void setCount() {
        //todo
        int count = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().getAllTabletManageDevice().size();
        txt_count.setText("Count " + count);
    }

    private void cleaFields() {
        //   qr_spinner_state.setSelection(0);
        //  qr_spinner_crl.setSelection(0);
        //   qr_spinner_crl.setEnabled(false);
        // qr_serialNo.setText("");
        // damageType.setVisibility(View.VISIBLE);
        crlNameSpinner.setSelection(0);
        isDamaged.setSelection(0);
        comments.setText("");
        qr_pratham_id.setText("");
    }


    @Override
    public void onBackPressed() {
        //todo
        tabletMD = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().getAllTabletManageDevice();
        if (!tabletMD.isEmpty()) {
            customDialogQRScan_md = new CustomDialogQRScan_MD(this, tabletMD);
            customDialogQRScan_md.show();
            txt_count.setText("Count " + 0);
        } else {
            finish();
            /*txt_count.setText("Count " + 0);*/
        }
    }

    private void uploadAPI(String url, String json) {
        url = "";
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("UPLOADING ... ");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //todo URL FOR AssignTab
        AndroidNetworking.post(url).setContentType("application/json").addStringBody(json).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                customDialogQRScan_md.dismiss();
                //todo user Based delete
                AppDatabase.getDatabaseInstance(AssignTabletMD.this).getTabletManageDeviceDoa().deleteAllTabletManageDevice();
                finish();
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(AssignTabletMD.this, "NO Internet Connection", Toast.LENGTH_LONG).show();
                //Log.d("anError", "" + anError);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void update() {

        if (internetIsAvailable) {
            Gson gson = new Gson();
            String json = gson.toJson(tabletMD);
            uploadAPI("", json);
        } else {
            Toast.makeText(this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
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

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    @Override
    public void clearChanges() {
        //todo
        AppDatabase.getDatabaseInstance(AssignTabletMD.this).getTabletManageDeviceDoa().deleteAllTabletManageDevice();
        customDialogQRScan_md.dismiss();
    }
}
