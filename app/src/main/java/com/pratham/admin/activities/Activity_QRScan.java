package com.pratham.admin.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.pratham.admin.modalclasses.TabTrack;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.ConnectionReceiver;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Activity_QRScan extends AppCompatActivity implements ZXingScannerView.ResultHandler, ConnectionReceiverListener, QRScanListener {

    @BindView(R.id.qr_frame)
    FrameLayout qr_frame;
    /* @BindView(R.id.qr_spinner_state)
     Spinner qr_spinner_state;*/
    //List<String> statewiseCRL;
    // List<String> statewiseCRLID;


    @BindView(R.id.programInfo)
    ConstraintLayout programInfoLayout;
    @BindView(R.id.tv_selProg)
    TextView selProg;
    @BindView(R.id.tv_selState)
    TextView selState;
    @BindView(R.id.tv_selVillage)
    TextView selVillage;
    @BindView(R.id.qr_spinner_crl)
    TextView qr_spinner_crl;
    @BindView(R.id.txt_count_village)
    TextView txt_count;
    @BindView(R.id.qr_pratham_id)
    EditText qr_pratham_id;
    @BindView(R.id.qr_serialNo)
    EditText qr_serialNo;
    public ZXingScannerView mScannerView;


    @BindView(R.id.successMessage)
    LinearLayout successMessage;
    //  String selectedCRL;
    // String selectedCRLID;
    String LoggedcrlId;
    String LoggedcrlName;
    String prathamId;
    String QrId;
    String state;
    TabTrack tabTrack;
    boolean permission = false;

    boolean internetIsAvailable = false;
    List<TabTrack> tabTracks;
    CustomDialogQRScan customDialogQRScan;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        ButterKnife.bind(this);

        /*prathamId = "tsy";
        qr_pratham_id.setText("uuid");
        qr_serialNo.setText("uiuif");
        QrId = "Qfjj";*/
        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        //todo
        if (ContextCompat.checkSelfPermission(Activity_QRScan.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("App requires camera permission to scan QR code");
                builder.setCancelable(false);
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(Activity_QRScan.this, new String[]{Manifest.permission.CAMERA}, 1);
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(Activity_QRScan.this, new String[]{Manifest.permission.CAMERA}, 1);
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

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        SharedPreferences preferences = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        String program = preferences.getString("program", "null");
        state = preferences.getString("state", "null");
        String village = preferences.getString("village", "null");
        if ((!program.equals("null")) && (!state.equals("null")) && (!village.equals("null"))) {
            programInfoLayout.setVisibility(View.VISIBLE);
            selProg.setText(program);
            selState.setText(state);
            selVillage.setText(village);
        } else {
            programInfoLayout.setVisibility(View.INVISIBLE);
        }
        qr_spinner_crl.setText(LoggedcrlName);
        List<TabTrack> oldList=AppDatabase.getDatabaseInstance(this).getTabTrackDao().getAllTabTrack();
        if(!oldList.isEmpty()){
            for(int i=0;i<oldList.size();i++){
                oldList.get(i).setOldFlag(true);
            }
            AppDatabase.getDatabaseInstance(this).getTabTrackDao().insertAllTabTrack(oldList);
            oldList.clear();
        }

        //  qr_spinner_crl.setEnabled(true);
        setCount();
        // initLayout();
    }

    private void initLayout() {
            /*states = getResources().getStringArray(R.array.india_states);
            statesCodes = getResources().getStringArray(R.array.india_states_shortcode);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, states);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            qr_spinner_state.setAdapter(adapter);

        qr_spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedState = qr_spinner_state.getSelectedItemPosition();
                selectedStateCode = statesCodes[selectedState];
                if (selectedStateCode.equals("SELECT STATE")) {
                    Toast.makeText(Activity_QRScan.this, "Please Select State", Toast.LENGTH_SHORT).show();
                } else {
                    showCRL(selectedStateCode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
*/
        //  showCRL();
    }

   /* public void showCRL() {
        List<CRL> crls;
        crls = AppDatabase.getDatabaseInstance(this).getCRLdao().getAllCRLs();
        statewiseCRL = new ArrayList();
     //   statewiseCRLID = new ArrayList<>();
        if (crls != null && !crls.isEmpty()) {
            statewiseCRL.add("SELECT CRL");
            statewiseCRLID.add("SELECT CRL");
            for (int i = 0; i < crls.size(); i++) {
                statewiseCRL.add("" + crls.get(i).getFirstName() + " " + crls.get(i).getLastName());
                statewiseCRLID.add("" + crls.get(i).getCRLId());
            }
            if (!statewiseCRL.isEmpty()) {
                //  qr_spinner_crl.setEnabled(true);
                ArrayAdapter crlAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, statewiseCRL);
                crlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               *//* qr_spinner_crl.setAdapter(crlAdapter);
                qr_spinner_crl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        int position = qr_spinner_crl.getSelectedItemPosition();
                        selectedCRL = statewiseCRL.get(position);
                        selectedCRLID = statewiseCRLID.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });*//*
            } else {
                //  qr_spinner_state.setSelection(0);
                //  qr_spinner_crl.setSelection(0);
                // qr_spinner_crl.setEnabled(false);
            }
        }
    }*/

    public void initCamera() {
        mScannerView = new ZXingScannerView(getApplicationContext());
        mScannerView.setResultHandler(Activity_QRScan.this);
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
                List l = AppDatabase.getDatabaseInstance(this).getTabTrackDao().checkExistance(QrId);
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
                       /* AppDatabase.getDatabaseInstance(Activity_QRScan.this).getTabTrackDao().insertTabTrack(tabTrack);
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
        mScannerView.resumeCameraPreview(Activity_QRScan.this);
        prathamId = "";
        qr_pratham_id.setText(prathamId);
        successMessage.setVisibility(View.GONE);
    }

    @OnClick(R.id.qr_btn_save)
    public void saveTabTrack() {
        String serialNO = qr_serialNo.getText().toString();
        prathamId = qr_pratham_id.getText().toString();
        if (!serialNO.equals("") && !prathamId.equals("")) {
            //  String[] splitted = selectedCRL.split("__");
            tabTrack = new TabTrack();
            tabTrack.setQR_ID(QrId);
            tabTrack.setCRL_ID(LoggedcrlId);
            tabTrack.setCRL_Name(LoggedcrlName);
            tabTrack.setState(state.substring(state.indexOf("(") + 1, state.indexOf(")")));
            tabTrack.setSerial_NO(serialNO);
            tabTrack.setPratham_ID(prathamId);
            tabTrack.setOldFlag(false);
            /*tabTrack.setLoggedIn_CRL(LoggedcrlId);*/
            tabTrack.setDate(DateFormat.getDateTimeInstance().format(new Date()));

            AppDatabase.getDatabaseInstance(this).getTabTrackDao().insertTabTrack(tabTrack);
            Toast.makeText(Activity_QRScan.this, "Inserted Successfully ", Toast.LENGTH_LONG).show();
            setCount();
            cleaFields();
            resetCamera();
        } else {
            Toast.makeText(this, "Fill In All The Details", Toast.LENGTH_SHORT).show();
        }
    }

    private void cleaFields() {
        //   qr_spinner_state.setSelection(0);
        //  qr_spinner_crl.setSelection(0);
        //   qr_spinner_crl.setEnabled(false);
        qr_serialNo.setText("");
        qr_pratham_id.setText("");
    }

    public void setCount() {
        //todo
        int count = AppDatabase.getDatabaseInstance(this).getTabTrackDao().getAllTabTrack().size();
        txt_count.setText("Count " + count);
    }

    @Override
    public void onBackPressed() {
        //todo
        tabTracks = AppDatabase.getDatabaseInstance(this).getTabTrackDao().getAllTabTrack();
        if (!tabTracks.isEmpty()) {
            customDialogQRScan = new CustomDialogQRScan(this, tabTracks);
            customDialogQRScan.show();
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
                customDialogQRScan.dismiss();
                //todo
                AppDatabase.getDatabaseInstance(Activity_QRScan.this).getTabTrackDao().deleteAllTabTracks();
                finish();
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(Activity_QRScan.this, "NO Internet Connection", Toast.LENGTH_LONG).show();
                //Log.d("anError", "" + anError);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void update() {

        if (internetIsAvailable) {
            Gson gson = new Gson();
            String json = gson.toJson(tabTracks);
            uploadAPI(APIs.TabTrackPushAPI+"kiik", json);
        }
        else{
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
        AppDatabase.getDatabaseInstance(Activity_QRScan.this).getTabTrackDao().deleteAllTabTracks();
        customDialogQRScan.dismiss();
    }
}
