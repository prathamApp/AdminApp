package com.pratham.admin.activities.replaceTab;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.modalclasses.TabletManageDevice;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CollectFragment extends Fragment implements ZXingScannerView.ResultHandler, ConnectionReceiverListener {

    public ZXingScannerView mScannerView;

    @BindView(R.id.qr_frame)
    FrameLayout qr_frame;

    private boolean internetIsAvailable = false;

    @BindView(R.id.village)
    Spinner village;
    @BindView(R.id.txt_count_village)
    TextView txt_count;
    @BindView(R.id.qr_pratham_id)
    EditText qr_pratham_id;
    @BindView(R.id.qr_serialNo)
    EditText qr_serialNo;
    @BindView(R.id.successMessage)
    LinearLayout successMessage;

    @BindView(R.id.isDamaged)
    Spinner isDamaged;
    @BindView(R.id.damageType)
    Spinner damageType;
    @BindView(R.id.comments)
    EditText comments;
    private String tabStatus, isDamagedText, damageTypeText;
    private String LoggedcrlId;
    private String LoggedcrlName;
    private String prathamId;
    private String QrId = "";
    private String villageName, villageID;
    // private String state;
    // private TabTrack tabTrack;
    //  private boolean permission = false;

    //  private boolean internetIsAvailable = false;
    //  private List<TabTrack> tabTracks;
    // private CustomDialogQRScan customDialogQRScan;

    private Context context;

    private List<TabletManageDevice> mainList;


    public CollectFragment() {
        // Required empty public constructor
    }

    public static CollectFragment newInstance(String crlId, String crlName, List<TabletManageDevice> mainList) {
        CollectFragment fragment = new CollectFragment();
        Bundle args = new Bundle();
        args.putString("CRLid", crlId);
        args.putString("CRLname", crlName);
        args.putParcelableArrayList("mainList", (ArrayList<? extends Parcelable>) mainList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoggedcrlId = getArguments().getString("CRLid");
        LoggedcrlName = getArguments().getString("CRLname");
        mainList = getArguments().getParcelableArrayList("mainList");
        Utility.clearCheckInternetInstance();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("App requires camera permission to scan QR code");
                builder.setCancelable(false);
                builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 1);
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 1);
            }
        } else {
            initCamera();
            // permission = true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(context, "You Need Camera permission", Toast.LENGTH_SHORT).show();
                //todo close fragment
            } else {
                initCamera();
                //   permission = true;
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
      /*  List<TabletManageDevice> oldList = AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDao().getAllReplaceDevice();
        if (!oldList.isEmpty()) {
            for (int i = 0; i < oldList.size(); i++) {
                oldList.get(i).setOldFlag(true);
            }
            AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDao().insertTabletAllManageDevice(oldList);
            oldList.clear();
        }*/

        tabStatus = "Replace_collect";
        setCount();
        setVillagesToSpinner();
        setLisnerToRuturnTabletSpinner();
    }

    private void setVillagesToSpinner() {
        final List<Village> villages = AppDatabase.getDatabaseInstance(context).getVillageDao().getAllVillages();
        if (villages.size() > 0) {
            villages.add(0, new Village(-1, "select Villege"));
        } else {
            villages.add(0, new Village(-1, "No village"));
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, villages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        village.setAdapter(adapter);
        village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Village village = villages.get(position);
                if (position > 0) {
                    if (!village.getVillageId().equalsIgnoreCase("-1")) {
                        villageID = village.getVillageId();
                        villageName = village.getVillageName();
                    } else {
                        villageID = "";
                        villageName = "";
                    }
                } else {
                    villageID = "";
                    villageName = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    @Override
    public void handleResult(Result rawResult) {
        QrId = null;
        prathamId = null;
        // Log.d("RawResult:::", "****" + rawResult.getText());
        String result = rawResult.getText();
        mScannerView.stopCamera();
        String[] splitted = result.split(":");

        try {
            QrId = splitted[0];
            prathamId = splitted[1];
            //  Log.d("::::Tag", QrId + "  " + prathamId);
            if (QrId != null && prathamId != null && splitted.length == 2 && (!prathamId.equalsIgnoreCase("None"))) {
//                List l = AppDatabase.getDatabaseInstance(context).getTabTrackDao().checkExistance(QrId);
//                if (l.isEmpty()) {
                if (!checkExistence(QrId)) {
                    Log.d(":::", QrId + "  " + prathamId);
                    qr_pratham_id.setText(prathamId);
                    successMessage.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("This QR Is Already Scanned. Do You Want To Replace Data?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            qr_pratham_id.setText(prathamId);
                            successMessage.setVisibility(View.VISIBLE);
                            dialogInterface.dismiss();
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
                Toast.makeText(context, "Invalid QR ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("QRScan" + "_" + "handleResult");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());

            e.printStackTrace();
            Toast.makeText(context, "Invalid QR ", Toast.LENGTH_LONG).show();
        }

    }

    public void initCamera() {
        mScannerView = new ZXingScannerView(context.getApplicationContext());
        mScannerView.setResultHandler(this);
        qr_frame.addView((mScannerView));
        mScannerView.startCamera();
        //    mScannerView.resumeCameraPreview(Activity_QRScan.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.qr_btn_reset)
    public void resetCamera() {
        mScannerView.stopCamera();
        mScannerView.startCamera();
        mScannerView.resumeCameraPreview(this);
        prathamId = "";
        QrId = "";
        qr_pratham_id.setText(prathamId);
        successMessage.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mScannerView.stopCamera();
    }


    @OnClick(R.id.qr_btn_save)
    public void saveTabTrack() {
        String serialNO = qr_serialNo.getText().toString().trim();
        prathamId = qr_pratham_id.getText().toString();
        if (!LoggedcrlId.equals("")) {
            if ((!serialNO.equals("")) || !prathamId.equals("")) {
                if (!villageID.equals("") && !villageName.equals("")) {
                    if ((isDamagedText.equals("Yes") && !damageTypeText.equals("")) || isDamagedText.equals("No")) {

                        TabletManageDevice tabletManageDevice = new TabletManageDevice();

                        tabletManageDevice.setId(Utility.GetUniqueID().toString());
                        tabletManageDevice.setCollectedTabQrID(QrId);
                        tabletManageDevice.setCollectedTabPrathamID(prathamId);
                        tabletManageDevice.setCollectedTab_serial_ID(serialNO);
                        tabletManageDevice.setCollected_date(new Utility().GetCurrentDateTime(false));

                        tabletManageDevice.setPratham_ID("");
                        tabletManageDevice.setQR_ID("");
                        tabletManageDevice.setTabSerial_ID("");
                        tabletManageDevice.setDate("");

                        tabletManageDevice.setStatus(tabStatus);
                        tabletManageDevice.setLogged_CRL_ID(LoggedcrlId);
                        tabletManageDevice.setLogged_CRL_NAME(LoggedcrlName);
                        //In this case logged crl and assigned crl are same
                        tabletManageDevice.setAssigned_CRL_ID(LoggedcrlId);
                        tabletManageDevice.setAssigned_CRL_Name(LoggedcrlName);
                        tabletManageDevice.setIs_Damaged(isDamagedText);
                        tabletManageDevice.setDamageType(damageTypeText);
                        tabletManageDevice.setComment(comments.getText().toString());
                        tabletManageDevice.setVillageID(villageID);
                        tabletManageDevice.setVillageName(villageName);
                        tabletManageDevice.setOldFlag(false);
                        tabletManageDevice.setIsPushed(0);
//                        AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDao().insertTabletManageDevice(tabletManageDevice);
                        mainList.add(tabletManageDevice);
                        BackupDatabase.backup(context);
                        Toast.makeText(context, "Inserted Successfully ", Toast.LENGTH_LONG).show();
                        setCount();
                        cleaFields();
                        resetCamera();
                    } else {
                        Toast.makeText(context, "select damage type", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "select village", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Scan QR Or Enter Serial Id", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Fill In All The Details", Toast.LENGTH_SHORT).show();
        }
    }

    private void cleaFields() {
        //   qr_spinner_state.setSelection(0);
        //  qr_spinner_crl.setSelection(0);
        //   qr_spinner_crl.setEnabled(false);
        qr_serialNo.setText("");
        qr_pratham_id.setText("");
        isDamaged.setSelection(0);
        village.setSelection(0);
        comments.setText("");
    }

    public void setCount() {
        //todo
//        int count = AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDao().getAllReplaceDevice().size();
        int count = mainList.size();
        txt_count.setText("Count " + count);
    }


    private boolean checkExistence(String qrId) {
        for (int i = 0; i < mainList.size(); i++) {
            if (mainList.get(i).getQR_ID().equalsIgnoreCase(qrId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            Utility.showNoInternetDialog(context);
            internetIsAvailable = false;
        } else {
            Utility.dismissNoInternetDialog();
            internetIsAvailable = true;
        }
    }

}
