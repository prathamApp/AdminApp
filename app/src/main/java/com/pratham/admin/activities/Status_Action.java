package com.pratham.admin.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.pratham.admin.interfaces.DevicePrathamIdLisner;
import com.pratham.admin.interfaces.QRScanListener;
import com.pratham.admin.modalclasses.TabletStatus;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import org.json.JSONArray;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Status_Action extends BaseActivity implements ZXingScannerView.ResultHandler, ConnectionReceiverListener, QRScanListener, DevicePrathamIdLisner {

    public ZXingScannerView mScannerView;
    @BindView(R.id.qr_frame)
    FrameLayout qr_frame;
    @BindView(R.id.statusRadioGroup)
    RadioGroup statusRadioGroup;
    @BindView(R.id.qr_spinner_crl)
    TextView qr_spinner_crl;
    @BindView(R.id.txt_count_village)
    TextView txt_count;
    @BindView(R.id.qr_pratham_id)
    EditText qr_pratham_id;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.qr_serialNo)
    EditText qr_serialNo;
    @BindView(R.id.successMessage)
    LinearLayout successMessage;
    String LoggedcrlId;
    String LoggedcrlName;
    String prathamId;
    String QrId;
    TabletStatus tabletStatus;
    boolean permission = false;
    Context context;
    boolean internetIsAvailable = false;
    List<TabletStatus> tabTracks;
    CustomDialogTabletStatus customDialogTabletStatus;
    private MyDeviceList myDeviceList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status__action);
        ButterKnife.bind(this);

        context = this;
        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        //todo
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("App requires camera permission to scan QR code");
                builder.setCancelable(false);
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(Status_Action.this, new String[]{Manifest.permission.CAMERA}, 1);
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(Status_Action.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        } else {
            initCamera();
            permission = true;
        }
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        qr_spinner_crl.setText(LoggedcrlName);
        List<TabletStatus> oldList = AppDatabase.getDatabaseInstance(this).getTabletStatusDao().getAllTabletStatus();
        if (!oldList.isEmpty()) {
            for (int i = 0; i < oldList.size(); i++) {
                oldList.get(i).setOldFlag(true);
            }
            AppDatabase.getDatabaseInstance(this).getTabletStatusDao().insertAllTabletStatus(oldList);
            oldList.clear();
        }
        setCount();
        setDate();
    }

    private void setDate() {
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
    }

    @OnClick(R.id.btn_DatePicker)
    public void datePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    public void initCamera() {
        mScannerView = new ZXingScannerView(getApplicationContext());
        mScannerView.setResultHandler(Status_Action.this);
        qr_frame.addView((mScannerView));
        mScannerView.startCamera();
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
                if ((!prathamId.equalsIgnoreCase("None"))) {
                    List l = AppDatabase.getDatabaseInstance(this).getTabletStatusDao().checkExistance(QrId);
                    if (l.isEmpty()) {
                        qr_pratham_id.setText(prathamId);
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
                                qr_pratham_id.setText(prathamId);
                                successMessage.setVisibility(View.VISIBLE);
                                qr_serialNo.setText("");
                                qr_serialNo.setEnabled(false);
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
                    if (internetIsAvailable) {
                        String url = APIs.DeviceList + LoggedcrlId;
                        loadDevises(url);
                    } else {
                        checkConnection();
                        new AlertDialog.Builder(this).setTitle("Warning").setMessage("Please fill out the entire form").setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                resetCamera();
                                dialog.dismiss();
                            }
                        }).create().show();
                    }
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
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading Devices...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AndroidNetworking.get(url).setPriority(Priority.MEDIUM).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                myDeviceList = new MyDeviceList(context, response);
               /* myDeviceList.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        resetCamera();
                    }
                });*/
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
                Toast.makeText(context, "Check internet conection", Toast.LENGTH_SHORT).show();
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
        mScannerView.resumeCameraPreview(Status_Action.this);
        prathamId = "";
        QrId = "";
        qr_pratham_id.setText(prathamId);
        successMessage.setVisibility(View.GONE);
        qr_serialNo.setText("");
        qr_serialNo.setEnabled(true);
    }

    @OnClick(R.id.qr_btn_save)
    public void saveTabTrack() {
        String serialNO = qr_serialNo.getText().toString();
        prathamId = qr_pratham_id.getText().toString();
        // String date = btn_DatePicker.getText().toString().trim();
        int selectedRadio = statusRadioGroup.getCheckedRadioButtonId();
        if ((!serialNO.equals("")) || !prathamId.equals("")) {
            if (selectedRadio != -1) {
                tabletStatus = new TabletStatus();
                tabletStatus.setQrID(QrId);
                tabletStatus.setLoggedCRL_Id(LoggedcrlId);
                tabletStatus.setLoggedCRL_Name(LoggedcrlName);
                tabletStatus.setPrathamId(prathamId);
                tabletStatus.setSerialNo(serialNO);
                tabletStatus.setStatus(((RadioButton) statusRadioGroup.findViewById(selectedRadio)).getText().toString());
                tabletStatus.setOldFlag(false);
                tabletStatus.setDate(btn_DatePicker.getText().toString());

                AppDatabase.getDatabaseInstance(this).getTabletStatusDao().insertTabTrack(tabletStatus);
                Toast.makeText(Status_Action.this, "Inserted Successfully ", Toast.LENGTH_LONG).show();
                setCount();
                cleaFields();
                resetCamera();
            } else {
                Toast.makeText(this, "Select Tab Status", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Fill In All The Details", Toast.LENGTH_SHORT).show();
        }
    }

    private void cleaFields() {
        //   qr_spinner_state.setSelection(0);
        //  qr_spinner_crl.setSelection(0);
        //   qr_spinner_crl.setEnabled(false);
        //qr_serialNo.setText("")
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        statusRadioGroup.clearCheck();
//        qr_pratham_id.setText("");
        QrId = "";
    }

    public void setCount() {
        //todo
        int count = AppDatabase.getDatabaseInstance(this).getTabletStatusDao().getAllTabletStatus().size();
        txt_count.setText("Count " + count);
    }

    @Override
    public void onBackPressed() {
        //todo
        tabTracks = AppDatabase.getDatabaseInstance(this).getTabletStatusDao().getAllTabletStatus();
        if (!tabTracks.isEmpty()) {
            customDialogTabletStatus = new CustomDialogTabletStatus(this, tabTracks);
            customDialogTabletStatus.show();
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
        AndroidNetworking.post(url).setContentType("application/json").addStringBody(json).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                customDialogTabletStatus.dismiss();
                //todo
                AppDatabase.getDatabaseInstance(Status_Action.this).getTabletStatusDao().deleteAllTabletStatus();
                finish();
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(Status_Action.this, "NO Internet Connection", Toast.LENGTH_LONG).show();
                //Log.d("anError", "" + anError);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void update() {

        if (internetIsAvailable) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String json = gson.toJson(tabTracks);
            //todo change API
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
        AppDatabase.getDatabaseInstance(Status_Action.this).getTabletStatusDao().deleteAllTabletStatus();
        customDialogTabletStatus.dismiss();
    }

    @Override
    public void getPrathamId(final String prathamIdNew, final String QrIdNEW) {
        if (QrIdNEW != null && prathamIdNew != null) {
            myDeviceList.dismiss();
            /* check prathamID is not none ie for Not blank QR code*/
            if ((!prathamIdNew.equalsIgnoreCase("None"))) {
                //   List l = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().checkExistanceTabletManageDevice(QrIdNEW);
                List l = AppDatabase.getDatabaseInstance(this).getTabletStatusDao().checkExistance(QrId);
                if (l.isEmpty()) {
                    // QrId = QrIdNEW;
                    prathamId = prathamIdNew;
                    qr_pratham_id.setText(prathamId);
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
                            // QrId = QrIdNEW;
                            prathamId = prathamIdNew;
                            qr_pratham_id.setText(prathamId);
                            successMessage.setVisibility(View.VISIBLE);
                            qr_serialNo.setText("");
                            qr_serialNo.setEnabled(false);
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

            }
        } else {
            Toast.makeText(this, "Invalid QR ", Toast.LENGTH_LONG).show();
        }
    }
}
