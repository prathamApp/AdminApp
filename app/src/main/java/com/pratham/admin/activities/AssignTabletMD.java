package com.pratham.admin.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.Toast;

import com.google.zxing.Result;
import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.CRLmd;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AssignTabletMD extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    @BindView(R.id.program)
    Spinner programSpinner;

    @BindView(R.id.roleCrl)
    Spinner roleCrlSpinner;

    /*@BindView(R.id.block)
    Spinner blockSpinner;*/

    @BindView(R.id.crlName)
    Spinner crlNameSpinner;



    @BindView(R.id.qr_frame)
    FrameLayout qr_frame;

    @BindView(R.id.qr_pratham_id)
    EditText qr_pratham_id;
    @BindView(R.id.qr_serialNo)
    EditText qr_serialNo;
    public ZXingScannerView mScannerView;
    String prathamId;
    String QrId;

    @BindView(R.id.successMessage)
    LinearLayout successMessage;
    private Context context;
    List<CRLmd> allCRLlist;
    boolean permission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_tablet);
        ButterKnife.bind(this);
        context = AssignTabletMD.this;
        allCRLlist = new ArrayList<>();
        loadCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int crlCount = AppDatabase.getDatabaseInstance(context).getCRLmd_dao().getCRLs_mdCount();
        if (crlCount <= 0) {
            Toast.makeText(context, "Please pull Data ", Toast.LENGTH_SHORT).show();
        } else {
            loadSpinner();
        }
    }
    private void loadSpinner() {
        //load spinner program From DB
        List<String> programList = new ArrayList<>();
        //List<String> stateList = new ArrayList<>();

        //List<String> crlList = new ArrayList<>();


        programList = AppDatabase.getDatabaseInstance(context).getCRLmd_dao().getDistinctCRLs_mdProgram();
        ArrayAdapter programAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,programList);
        programSpinner.setAdapter(programAdapter);

        programSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<String>  roleList = AppDatabase.getDatabaseInstance(context).getCRLmd_dao().getDistinctCRLs_mdRoleId();
                ArrayAdapter stateAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,roleList);
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
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedRole= roleCrlSpinner.getSelectedItem().toString();
                List<String> crlList = AppDatabase.getDatabaseInstance(context).getCRLmd_dao().getDistinctCRLs_mdUserName(selectedRole,programSpinner.getSelectedItem().toString());
                ArrayAdapter crlAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,crlList);
                crlNameSpinner.setAdapter(crlAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //load spinner crl from db

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
        mScannerView.resumeCameraPreview(AssignTabletMD.this);
        prathamId = "";
        qr_pratham_id.setText(prathamId);
        successMessage.setVisibility(View.GONE);
    }

}
