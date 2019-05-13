package com.pratham.admin.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.pratham.admin.R;
import com.pratham.admin.async.NetworkCalls;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.DevicePrathamIdLisner;
import com.pratham.admin.interfaces.NetworkCallListner;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.ROll_ID;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageDevice extends BaseActivity implements DevicePrathamIdLisner, ConnectionReceiverListener, NetworkCallListner {
    @BindView(R.id.btn_assignTablet)
    CardView btn_assignTablet;

    @BindView(R.id.returnTablet)
    CardView returnTablet;

    @BindView(R.id.btn_replaceTab)
    CardView btn_replaceTab;

    @BindView(R.id.acionstatus)
    CardView acionstatus;

    String LoggedcrlName;
    String LoggedcrlId;
    Context context;
    boolean internetIsAvailable = false;
    //ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);
        ButterKnife.bind(this);
        // Hide Actionbar
        getSupportActionBar().hide();
        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        context = ManageDevice.this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        setRules();
    }

    private void setRules() {
        String role = AppDatabase.getDatabaseInstance(this).getCRLdao().getCRLsRoleById(LoggedcrlId);
        if (role != null) {
            switch (role) {
                case ROll_ID.BRG_CRL_Tutor:
                    btn_assignTablet.setVisibility(View.GONE);
                    returnTablet.setVisibility(View.GONE);
                    acionstatus.setVisibility(View.GONE);
                    /*only crl can replace tab*/
                    btn_replaceTab.setVisibility(View.VISIBLE);
                    break;
                case ROll_ID.Block_Head:
                    acionstatus.setVisibility(View.GONE);
                    btn_replaceTab.setVisibility(View.GONE);
                    break;
                case ROll_ID.District_Head:
                    acionstatus.setVisibility(View.GONE);
                    btn_replaceTab.setVisibility(View.GONE);
                    break;
                case ROll_ID.Program_Head:
                    acionstatus.setVisibility(View.GONE);
                    btn_replaceTab.setVisibility(View.GONE);
                    break;
                case ROll_ID.State_Program_Head:
                    acionstatus.setVisibility(View.GONE);
                    btn_replaceTab.setVisibility(View.GONE);
                    break;
                case ROll_ID.Vendor:
                    btn_replaceTab.setVisibility(View.GONE);
                    break;
                case ROll_ID.Admin:
                    break;
            }
        } else {
            Toast.makeText(context, "No rolls available", Toast.LENGTH_SHORT).show();
        }
    }

    public void assignTablet(View view) {
        Intent intent = new Intent(this, AssignTabletMD.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        intent.putExtra("tabStatus", "Assign");
        startActivity(intent);
    }

    public void returnTablet(View view) {
        Intent intent = new Intent(this, AssignTabletMD.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        intent.putExtra("tabStatus", "Return");
        startActivity(intent);
    }

    public void replaceTablet(View view) {
        Intent intent = new Intent(this, ReplaceTablet_MD.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        intent.putExtra("tabStatus", "Replace");
        startActivity(intent);
    }

    public void action_status(View view) {
        Intent intent = new Intent(this, Status_Action.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        startActivity(intent);
    }

    public void myDeviceList(View view) {
        //todo check internret connection
        if (internetIsAvailable) {
            String url = APIs.DeviceList + LoggedcrlId;
            loadDevises(url);
        } else {
            checkConnection();
            new AlertDialog.Builder(ManageDevice.this).setTitle("Warning").setMessage("No Intenet Found").setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    private void loadDevises(String url) {
        NetworkCalls.getNetworkCallsInstance(this).getRequest(this, url, "Loading Devices..", "loading_devises");
       /* progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading Devices..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AndroidNetworking.get(url).setPriority(Priority.MEDIUM).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                if (response.length() > 0) {
                    MyDeviceList myDeviceList = new MyDeviceList(context, response);
                    myDeviceList.show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(ManageDevice.this).create();
                    alertDialog.setTitle("No Device found");
                    alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                    alertDialog.setButton("OK", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }

            @Override
            public void onError(ANError anError) {
                progressDialog.dismiss();
                Toast.makeText(context, "check internet connection", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public void getPrathamId(String prathamId, String QrId) {

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

    public void scanQR(View view) {
        Intent intent = new Intent(ManageDevice.this, Activity_QRScan.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        startActivity(intent);
    }

    @Override
    public void onResponce(String response1, String header) {
        if (header.equals("loading_devises")) {
            JSONArray response = null;
            try {
                response = new JSONArray(response1);
                if (response.length() > 0) {
                    MyDeviceList myDeviceList = new MyDeviceList(context, response);
                    myDeviceList.show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(ManageDevice.this).create();
                    alertDialog.setTitle("No Device found");
                    alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                    alertDialog.setButton("OK", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(ANError anError, String header) {
        if (header.equals("loading_devises")) {
            Toast.makeText(context, "check internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
