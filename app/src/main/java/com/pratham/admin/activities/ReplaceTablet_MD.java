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
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.Result;
import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.QRScanListener;
import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.TabletManageDevice;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;

import org.json.JSONArray;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ReplaceTablet_MD extends BaseActivity implements ZXingScannerView.ResultHandler, ConnectionReceiverListener, QRScanListener {
   /* @BindView(R.id.program)
    Spinner programSpinner;

    @BindView(R.id.roleCrl)
    Spinner roleCrlSpinner;*/

    /*@BindView(R.id.block)
    Spinner blockSpinner;*/
   /* @BindView(R.id.assignedCrlName)
    TextView assignedCrlName;*/

    /* @BindView(R.id.qr_pratham_id)
     EditText qr_pratham_id;*/
      /*
        @BindView(R.id.returnTablet)
        TextView returnTablet;*/


    /*@BindView(R.id.assignedCrlId)
    TextView assignedCrlId;*/
    /* @BindView(R.id.crlName)
     Spinner crlNameSpinner;
 */

    final String ASSIGN = "Assign";
    final String RETURN = "Return";
    final String REPLACE = "Replace";
    public ZXingScannerView mScannerView;
    @BindView(R.id.qr_btn_save)
    Button qr_btn_save;
    @BindView(R.id.msg)
    TextView msg;
    @BindView(R.id.qr_frame)
    FrameLayout qr_frame;
    @BindView(R.id.qr_serialNo)
    EditText qr_serialNo;
    @BindView(R.id.txt_count_village)
    TextView txt_count;
    @BindView(R.id.btn_returnTablet)
    Button btn_returnTablet;
    @BindView(R.id.btn_assignTablet)
    Button btn_assignTablet;
    @BindView(R.id.successMessage)
    LinearLayout successMessage;
    @BindView(R.id.isDamaged)
    Spinner isDamaged;
    @BindView(R.id.damageType)
    Spinner damageType;
    @BindView(R.id.comments)
    EditText comments;
    @BindView(R.id.qrData)
    TextView qrData;
    @BindView(R.id.checkbox_no_qr)
    CheckBox checkbox_no_qr;
    String prathamId;
    String QrId;

    String newPratahmId, newQrId, newSerialId;
    String oldPratahmId, oldQrId, oldSerialId;
    List<CRL> allCRLlist;
    boolean permission = false;
    String LoggedcrlName;
    String LoggedcrlId;
    CustomDialogQRScan_MD customDialogQRScan_md;
    List<TabletManageDevice> tabletMD;
    boolean internetIsAvailable = false;
    String tabStatus, isDamagedText, damageTypeText;
    MyDeviceList myDeviceList;
    ProgressDialog progressDialog;
    String comment;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_replace_tablet__md);
        ButterKnife.bind(this);
        context = ReplaceTablet_MD.this;
        allCRLlist = new ArrayList<>();
        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        tabStatus = RETURN;
        /*android.support.v7.app.ActionBar ab = getSupportActionBar();*/


        checkbox_no_qr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              /*  if (tabStatus.equals(RETURN)) {
                   oldPratahmId = null;
                    oldQrId = null;
                    setPrathamIdAndQrIF(oldPratahmId, oldQrId);
                } else if (tabStatus.equals(ASSIGN)) {
                   newPratahmId = null;
                    newQrId = null;
                    setPrathamIdAndQrIF(newPratahmId, newQrId);
                }*/
                if (isChecked) {
                    qr_serialNo.setEnabled(true);
                    qr_serialNo.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    qr_serialNo.setEnabled(false);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCamera();
        controller();
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
                    ReplaceTablet_MD.this.finish();
                }
            });
            builder.show();


        } else {
           /* setRules();*/
            List<TabletManageDevice> oldList = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().getAllTabletManageDevice();
            if (!oldList.isEmpty()) {
                for (int i = 0; i < oldList.size(); i++) {
                    oldList.get(i).setOldFlag(true);
                }
                AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().insertTabletAllManageDevice(oldList);
                oldList.clear();
            }

          /*  loadSpinner();*/
            // qr_spinner_crl.setText(LoggedcrlName);
            setCount();
        }
    }


    private void controller() {
        checkbox_no_qr.setChecked(false);
        if (tabStatus.equals(ASSIGN)) {
            btn_returnTablet.setBackground(getResources().getDrawable(R.drawable.btn_style_upper_rounded));
            btn_assignTablet.setBackground(getResources().getDrawable(R.drawable.btn_style_upper_rounded_white));
            setPrathamIdAndQrIF(newPratahmId, newQrId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn_assignTablet.setElevation(30);
                btn_returnTablet.setElevation(0);
            }
            msg.setText("scan the tablet which want to Assign after that press save");
            isDamaged.setVisibility(View.GONE);
            damageType.setVisibility(View.GONE);
            comments.setVisibility(View.GONE);
            qr_btn_save.setVisibility(View.VISIBLE);
            oldSerialId = qr_serialNo.getText().toString();
            if (newSerialId != null && !newSerialId.equals("")) {
                qr_serialNo.setText(newSerialId);
            } else {
                qr_serialNo.setText("");
            }
        } else if (tabStatus.equals(RETURN)) {
            resetCamera();
            newSerialId = qr_serialNo.getText().toString();
            if (oldSerialId != null && !oldSerialId.equals("")) {
                qr_serialNo.setText(oldSerialId);
            } else {
                qr_serialNo.setText("");
            }
            btn_returnTablet.setBackground(getResources().getDrawable(R.drawable.btn_style_upper_rounded_white));
            btn_assignTablet.setBackground(getResources().getDrawable(R.drawable.btn_style_upper_rounded));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn_returnTablet.setElevation(30);
                btn_returnTablet.setElevation(0);
            }
            setPrathamIdAndQrIF(oldPratahmId, oldQrId);
            msg.setText("first fill details then scan the tablet which want to Return");
            isDamaged.setVisibility(View.VISIBLE);
            damageType.setVisibility(View.VISIBLE);
            comments.setVisibility(View.VISIBLE);
            qr_btn_save.setVisibility(View.INVISIBLE);
            setLisnerToRuturnTabletSpinner();
        } else if (tabStatus.equals(REPLACE)) {
            msg.setText("If all is good then save");
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


    private void loadCamera() {

        if (ContextCompat.checkSelfPermission(ReplaceTablet_MD.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("App requires camera permission to scan QR code");
                builder.setCancelable(false);
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(ReplaceTablet_MD.this, new String[]{Manifest.permission.CAMERA}, 1);
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(ReplaceTablet_MD.this, new String[]{Manifest.permission.CAMERA}, 1);
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
        mScannerView.setResultHandler(ReplaceTablet_MD.this);
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

            QrId = splitted[0];
            prathamId = splitted[1];
            if (QrId != null && prathamId != null && splitted.length == 2) {
                /* check prathamID is not none ie for Not blank QR code*/
                if ((!prathamId.equalsIgnoreCase("None"))) {
                   /* List l = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().checkExistanceTabletManageDevice(QrId);
                    if (l.isEmpty()) {*/
                    successMessage.setVisibility(View.VISIBLE);
                    if (tabStatus.equals(RETURN)) {
                        if (newPratahmId == null || (!newPratahmId.equals(prathamId))) {
                            oldPratahmId = prathamId;
                            oldQrId = QrId;
                            //      qr_pratham_id.setText(oldPratahmId);
                        /*    qr_serialNo.setText("");
                            qr_serialNo.setEnabled(false);*/
                            new AlertDialog.Builder(this)
                                    .setMessage("Step 1 successfully done Continue with step 2")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            tabStatus = ASSIGN;
                                            setPrathamIdAndQrIF(oldPratahmId, oldQrId);
                                               /* returnTablet.setText(Html.fromHtml("<b><i>1.RETURN<i> <u>Pratham_id</u></b> : " + oldPratahmId + "<b><u> QrId</u></b>: " + oldQrId));
                                                returnTablet.setTextColor(Color.parseColor("#FFFFFF"));
                                                returnTablet.setBackgroundColor(Color.parseColor("#6AC259"));*/
                                            comment = comments.getText().toString();
                                            resetCamera();
                                            controller();
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    resetCamera();
                                }
                            }).show();
                        } else {
                            new AlertDialog.Builder(this)
                                    .setMessage("You can not repalce  device with same device")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            resetCamera();
                                        }
                                    }).show();
                        }
                    } else if (tabStatus.equals(ASSIGN)) {
                        if (oldPratahmId == null || !oldPratahmId.equals(prathamId)) {
                            newPratahmId = prathamId;
                            newQrId = QrId;
                            //    qr_pratham_id.setText(prathamId);
                       /*     qr_serialNo.setText("");
                            qr_serialNo.setEnabled(false);*/
                            setPrathamIdAndQrIF(newPratahmId, newQrId);
                            // qrData.setText(Html.fromHtml("<b><i>2.ASSIGN<i> <u>Pratham_id</u></b> : " + newPratahmId + "<b><u> QrId</u></b>: " + newQrId));
                               /* assignTablet.setTextColor(Color.parseColor("#FFFFFF"));
                                assignTablet.setBackgroundColor(Color.parseColor("#6AC259"));*/
                            controller();
                        } else {
                            new AlertDialog.Builder(this)
                                    .setMessage("You can not repalce  device with same device")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            resetCamera();
                                        }
                                    }).show();
                        }
                    }

             /*       } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setMessage("This QR Is Already Scanned. Do You Want To Replace Data?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                qr_pratham_id.setText(prathamId);
                                successMessage.setVisibility(View.VISIBLE);
                                qr_serialNo.setText("");
                                qr_serialNo.setEnabled(false);
                                dialogInterface.dismiss();
                       *//* AppDatabase.getDatabaseInstance(Activity_QRScan.this).getTabTrackDao().insertTabTrack(tabletStatus);
                        Toast.makeText(Activity_QRScan.this, "Updated Successfully ", Toast.LENGTH_LONG).show();*//*
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

                    }*/
                } else {
                    // todo blank QR code
                   /* if (internetIsAvailable) {
                        String url = APIs.DeviceList + LoggedcrlId;
                        loadDevises(url);
                    } else {
                        checkConnection();
                        new AlertDialog.Builder(this).setTitle("Warning").setMessage("Internet not available").setPositiveButton("Close", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetCamera();
                                dialog.dismiss();
                            }
                        }).create().show();
                    }*/
                    Toast.makeText(context, "Blank Qr Not Supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invalid QR ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid QR ", Toast.LENGTH_LONG).show();
        }

    }

    private void loadDevises(String url) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("loading Devices");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AndroidNetworking.get(url).setPriority(Priority.MEDIUM).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                myDeviceList = new MyDeviceList(context, response);
                myDeviceList.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        resetCamera();
                    }
                });
                myDeviceList.show();
            }

            @Override
            public void onError(ANError anError) {
                progressDialog.dismiss();
                resetCamera();
                Toast.makeText(context, "Check internet connection", Toast.LENGTH_SHORT).show();
            }
        });
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
        mScannerView.resumeCameraPreview(ReplaceTablet_MD.this);
        prathamId = "";
        QrId = "";
        //  qr_pratham_id.setText(prathamId);
        successMessage.setVisibility(View.GONE);
      /*  qr_serialNo.setText("");
        qr_serialNo.setEnabled(true);*/
    }

    @OnClick(R.id.qr_btn_save)
    public void saveTabTrack() {
        newSerialId = qr_serialNo.getText().toString();
        //   prathamId = qr_pratham_id.getText().toString();
        // if ((!serialNO.equals("")) || !prathamId.equals("")) {
       /*     if (tabStatus.equals(RETURN)) {*/
        if ((oldPratahmId != null && oldQrId != null && !oldPratahmId.equals("") && !oldQrId.equals("")) || (oldSerialId != null && !oldSerialId.equals(""))) {
            if ((newPratahmId != null && newQrId != null && !newPratahmId.equals("") && !newQrId.equals("")) || (newSerialId != null && !newSerialId.equals(""))) {
                if ((isDamagedText.equals("Yes") && !damageTypeText.equals("")) || isDamagedText.equals("No")) {
                    TabletManageDevice tabletManageDevice = new TabletManageDevice();
                    tabletManageDevice.setQR_ID(oldQrId);
                    tabletManageDevice.setStatus(REPLACE);
                    tabletManageDevice.setLogged_CRL_ID(LoggedcrlId);
                    tabletManageDevice.setAssigned_CRL_Name(LoggedcrlName);
                    tabletManageDevice.setAssigned_CRL_ID(LoggedcrlId);
                    tabletManageDevice.setAssigned_CRL_Name(LoggedcrlName);
                    tabletManageDevice.setPratham_ID(oldPratahmId);
                    tabletManageDevice.setTabSerial_ID(oldSerialId);
                    tabletManageDevice.setIs_Damaged(isDamagedText);
                    tabletManageDevice.setDamageType(damageTypeText);
                    tabletManageDevice.setComment(comment);
                    tabletManageDevice.setOldFlag(false);
                    tabletManageDevice.setNewPrathamID(newPratahmId);
                    tabletManageDevice.setNewQrID(newQrId);
                    tabletManageDevice.setNew_Tab_serial_ID(newSerialId);
                    tabletManageDevice.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().insertTabletManageDevice(tabletManageDevice);
                    Toast.makeText(ReplaceTablet_MD.this, "Inserted Successfully ", Toast.LENGTH_LONG).show();
                    setCount();
                    cleaFields();
                    resetCamera();
                } else {
                    Toast.makeText(context, "select damage type in return process", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Scan qr for Assign ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Scan qr for Return", Toast.LENGTH_SHORT).show();
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
    /*    crlNameSpinner.setSelection(0);*/
        isDamaged.setSelection(0);
        comments.setText("");
        oldPratahmId = null;
        oldQrId = null;
        newQrId = null;
        newPratahmId = null;
        oldSerialId = "";
        newSerialId = "";
        qr_serialNo.setText("");
        setPrathamIdAndQrIF(newPratahmId, newQrId);
        // qr_pratham_id.setText("");
    }

    @Override
    public void onBackPressed() {
        //todo
        tabletMD = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().getAllTabletManageDevice();
        if (!tabletMD.isEmpty()) {
            customDialogQRScan_md = new CustomDialogQRScan_MD(this, tabletMD, 1);
            customDialogQRScan_md.show();
            txt_count.setText("Count " + 0);
        } else {
            finish();
            /*txt_count.setText("Count " + 0);*/
        }
    }

    private void uploadAPI(String url, String json) {
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
                AppDatabase.getDatabaseInstance(ReplaceTablet_MD.this).getTabletManageDeviceDoa().deleteAllTabletManageDevice();
                finish();
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(ReplaceTablet_MD.this, "NO Internet Connection", Toast.LENGTH_LONG).show();
                //Log.d("anError", "" + anError);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void update() {
        if (internetIsAvailable) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String json = gson.toJson(tabletMD);
            uploadAPI(APIs.AssignReturn, json);
        } else {
            checkConnection();
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
        AppDatabase.getDatabaseInstance(ReplaceTablet_MD.this).getTabletManageDeviceDoa().deleteAllTabletManageDevice();
        customDialogQRScan_md.dismiss();
    }

   /* @Override
    public void getPrathamId(final String prathamIdNew, final String QrIdNEW) {
        if (QrIdNEW != null && prathamIdNew != null) {
            myDeviceList.dismiss();
            *//* check prathamID is not none ie for Not blank QR code*//*
            if ((!prathamIdNew.equalsIgnoreCase("None"))) {
                List l = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().checkExistanceTabletManageDevice(QrId);
                if (l.isEmpty()) {
                    // QrId = QrIdNEW;
                    prathamId = prathamIdNew;
                   // qr_pratham_id.setText(prathamId);
                    successMessage.setVisibility(View.VISIBLE);
                    qr_serialNo.setText("");
                    qr_serialNo.setEnabled(false);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setMessage("This QR Is Already Scanned. Do You Want To Replace Data?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //  QrId = QrIdNEW;
                            prathamId = prathamIdNew;
                          //  qr_pratham_id.setText(prathamId);
                            successMessage.setVisibility(View.VISIBLE);
                            qr_serialNo.setText("");
                            qr_serialNo.setEnabled(false);
                            dialogInterface.dismiss();
                       *//* AppDatabase.getDatabaseInstance(Activity_QRScan.this).getTabTrackDao().insertTabTrack(tabletStatus);
                        Toast.makeText(Activity_QRScan.this, "Updated Successfully ", Toast.LENGTH_LONG).show();*//*
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

            }
        } else {
            Toast.makeText(this, "Invalid QR ", Toast.LENGTH_LONG).show();
        }
    }*/

    @OnClick(R.id.refresh)
    public void refresh() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to Restart again")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.btn_returnTablet)
    public void returnTablet() {
        tabStatus = RETURN;
        controller();
    }

    @OnClick(R.id.btn_assignTablet)
    public void assignTablet() {
        tabStatus = ASSIGN;
        controller();
    }

    public void setPrathamIdAndQrIF(String prathamId, String qr_id) {
        if (prathamId != null && qr_id != null) {
            qrData.setText(Html.fromHtml("<b> <u>Pratham_id</u></b> : " + prathamId + "<b><u> QrId</u></b>: " + qr_id));
        } else {
            qrData.setText(Html.fromHtml(""));
        }
    }

}