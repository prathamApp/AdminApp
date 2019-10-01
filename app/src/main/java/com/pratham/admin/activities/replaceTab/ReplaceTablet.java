package com.pratham.admin.activities.replaceTab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pratham.admin.R;
import com.pratham.admin.activities.CustomDialogQRScan_MD;
import com.pratham.admin.async.NetworkCalls;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.NetworkCallListner;
import com.pratham.admin.interfaces.QRScanListener;
import com.pratham.admin.modalclasses.TabletManageDevice;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReplaceTablet extends BaseActivity implements OperationListener, AssignFragment.OnFragmentInteractionListener, QRScanListener, ConnectionReceiverListener, NetworkCallListner {
    @BindView(R.id.fragmentLayout)
    FrameLayout fragmentLayout;

    FragmentManager fragmentManager;
    private String LoggedcrlId;
    private String LoggedcrlName;
    private List<TabletManageDevice> tabletMD;
    private boolean internetIsAvailable = false;
    private Context context;
    private CustomDialogQRScan_MD customDialogQRScan_md;

    private Fragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_tablet);
        ButterKnife.bind(this);
        context = ReplaceTablet.this;
        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        Fragment_ChooseAssignOrReturn fragment_chooseAssignOrReturn = new Fragment_ChooseAssignOrReturn();
        Bundle bundle = new Bundle();
        bundle.putString("CRLid", LoggedcrlId);
        bundle.putString("CRLname", LoggedcrlName);
        fragment_chooseAssignOrReturn.setArguments(bundle);
        addFragment(fragment_chooseAssignOrReturn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    private void addFragment(Fragment fragment_chooseAssignOrReturn) {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, fragment_chooseAssignOrReturn);
        fragmentTransaction.addToBackStack(fragment_chooseAssignOrReturn.getClass().getName());
        fragmentTransaction.commit();
    }

    @Override
    public void onOperationSelect(Fragment fragment) {
        if (fragment instanceof AssignFragment) {
            if (internetIsAvailable) {
                selectedFragment = fragment;
                NetworkCalls.getNetworkCallsInstance(this).getRequest(this, APIs.GetCollectedTabList + LoggedcrlId, "UPLOADING ... ", "GetCollectedTabList");
            } else {
                addFragment(fragment);
            }
        } else {
            addFragment(fragment);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        //todo
        getSupportFragmentManager().popBackStackImmediate();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            tabletMD = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDoa().getAllReplaceDevice();
            if (!tabletMD.isEmpty()) {
                customDialogQRScan_md = new CustomDialogQRScan_MD(this, tabletMD);
                customDialogQRScan_md.show();
            } else {
                // getSupportFragmentManager().popBackStackImmediate();
            }
        } else {
            finish();
        }
    }

    @Override
    public void update() {
        if (internetIsAvailable) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String json = gson.toJson(tabletMD);
            uploadAPI(APIs.ReplaceTab, json);
        } else {
            checkConnection();
            Toast.makeText(this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void clearChanges() {
        AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().deleteReplaceDevice();
        customDialogQRScan_md.dismiss();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    private void uploadAPI(String url, String json) {
        NetworkCalls.getNetworkCallsInstance(this).postRequest(this, url, "UPLOADING ... ", json, "ReplaceTab");
    }

    @Override
    public void onResponce(String response, String header) {
        if (header.equals("ReplaceTab")) {
            customDialogQRScan_md.dismiss();
            //todo user Based delete

            // AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().deleteAllTabletManageDevice();
            AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().updateReplaceIsPushedFlag();
            finish();
        } else if (header.equals("GetCollectedTabList")) {
            Gson gson = new Gson();
            List<TabletManageDevice> list = gson.fromJson(response, new TypeToken<List<TabletManageDevice>>() {
            }.getType());
            if (list.size() > 0) {
                for (TabletManageDevice tabletManageDevice : list) {
                    tabletManageDevice.setIsPushed(1);
                    List<TabletManageDevice> temp = AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().checkExistanceTabletManageDevice(tabletManageDevice.getId());
                    if (temp.size() == 0) {
                        AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().insertTabletManageDevice(tabletManageDevice);
                    }
                }
            }
            addFragment(selectedFragment);
        }
    }

    @Override
    public void onError(ANError anError, String header) {
        if (header.equals("ReplaceTab")) {
            Toast.makeText(context, "NO Internet Connection", Toast.LENGTH_LONG).show();
        } else if (header.equals("GetCollectedTabList")) {
            addFragment(selectedFragment);
        }
    }
}
