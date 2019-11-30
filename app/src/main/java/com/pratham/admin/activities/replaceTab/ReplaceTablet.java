package com.pratham.admin.activities.replaceTab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.pratham.admin.interfaces.NetworkCallListener;
import com.pratham.admin.interfaces.QRScanListener;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.TabletManageDevice;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReplaceTablet extends BaseActivity implements OperationListener, AssignFragment.OnFragmentInteractionListener, QRScanListener, ConnectionReceiverListener, NetworkCallListener {
    @BindView(R.id.fragmentLayout)
    FrameLayout fragmentLayout;

    FragmentManager fragmentManager;
    private String LoggedcrlId;
    private String LoggedcrlName;
    //    private List<TabletManageDevice> tabletMD;
    private boolean internetIsAvailable = false;
    private Context context;
    private CustomDialogQRScan_MD customDialogQRScan_md;

    private String currentFragment = "";

    private Fragment selectedFragment;

    private List<TabletManageDevice> mainList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_tablet);
        ButterKnife.bind(this);
        Utility.clearCheckInternetInstance();
        context = ReplaceTablet.this;
        mainList = new ArrayList<>();
        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        Fragment_ChooseAssignOrReturn fragment_chooseAssignOrReturn = new Fragment_ChooseAssignOrReturn();
        Bundle bundle = new Bundle();
        bundle.putString("CRLid", LoggedcrlId);
        bundle.putString("CRLname", LoggedcrlName);
        bundle.putParcelableArrayList("mainList", (ArrayList<? extends Parcelable>) mainList);
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
        mainList.clear();
        if (fragment instanceof AssignFragment) {
            currentFragment = "assign";
            if (internetIsAvailable) {
                selectedFragment = fragment;
                NetworkCalls.getNetworkCallsInstance(this).getRequest(this, APIs.GetCollectedTabList + LoggedcrlId, "UPDATING ... ", "GetCollectedTabList");
            } else {
                addFragment(fragment);
            }
        } else {
            currentFragment = "collect";
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

        ListIterator<TabletManageDevice> iterator = mainList.listIterator();

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (currentFragment.equalsIgnoreCase("assign")) {
//                for (int i = 0; i < mainList.size(); i++) {
                while (iterator.hasNext()) {
                    TabletManageDevice tabletManageDevice = iterator.next();
                    if (tabletManageDevice.getQR_ID() != null && tabletManageDevice.getPratham_ID() != null) {
                        if (tabletManageDevice.getQR_ID().equalsIgnoreCase("") && tabletManageDevice.getPratham_ID().equalsIgnoreCase("")) {
                            iterator.remove();
                        }
                   /* if (mainList.get(iterator.nextIndex()).getQR_ID() != null && mainList.get(iterator.nextIndex()).getPratham_ID() != null) {
                        if (!mainList.get(iterator.nextIndex()).getQR_ID().equalsIgnoreCase("") && mainList.get(iterator.nextIndex()).getPratham_ID().equalsIgnoreCase("")) {

                        }
                    }*/
                    }
                }
            }

//            tabletMD = AppDatabase.getDatabaseInstance(this).getTabletManageDeviceDao().getAllReplaceDevice();
            if (!mainList.isEmpty()) {
//                customDialogQRScan_md = new CustomDialogQRScan_MD(this, tabletMD);
                customDialogQRScan_md = new CustomDialogQRScan_MD(this, mainList);
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
            String metaData = addMetaDataToJson();
//            String json = gson.toJson(mainList);
            String json = "{ \"ManageDevicesJSON\":" + "" + gson.toJson(mainList)
                    + ",\"MetaData\":" + "" + metaData + "}";
            Log.d("@@@@@", json);
            uploadAPI(APIs.ReplaceTab, json);
        } else {
            checkConnection();
            Toast.makeText(this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void clearChanges() {
//        AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDao().deleteReplaceDevice();
        mainList.clear();
        customDialogQRScan_md.dismiss();
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            Utility.showNoInternetDialog(this);
            internetIsAvailable = false;
        } else {
            Utility.dismissNoInternetDialog();
            internetIsAvailable = true;
        }
    }


    private String addMetaDataToJson() {

        MetaData metaData = new MetaData();
        metaData.setKeys("pushDataTime");
        metaData.setValue(new Utility().GetCurrentDateTime(false));
        List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
        String metaDataJSON = customParse(metaDataList);
        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);
        return metaDataJSON;
    }

    private String customParse(List<MetaData> metaDataList) {
        String json = "{";

        for (int i = 0; i < metaDataList.size(); i++) {
            json = json + "\"" + metaDataList.get(i).getKeys() + "\":\"" + metaDataList.get(i).getValue() + "\"";
            if (i < metaDataList.size() - 1) {
                json = json + ",";
            }
        }
        json = json + "}";

        return json;
    }


    private void uploadAPI(String url, String json) {
        NetworkCalls.getNetworkCallsInstance(this).postRequest(this, url, "UPLOADING ... ", json, "ReplaceTab");
    }

    @Override
    public void onResponse(String response, String header) {
        if (header.equals("ReplaceTab")) {
            customDialogQRScan_md.dismiss();
            //todo user Based delete

            // AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDao().deleteAllTabletManageDevice();
            AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDao().updateReplaceIsPushedFlag();
            finish();
        } else if (header.equals("GetCollectedTabList")) {
            Gson gson = new Gson();
            List<TabletManageDevice> list = gson.fromJson(response, new TypeToken<List<TabletManageDevice>>() {
            }.getType());
            if (list.size() > 0) {
                for (TabletManageDevice tabletManageDevice : list) {
                    tabletManageDevice.setIsPushed(1);
                  /*  List<TabletManageDevice> temp = AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDao().checkExistanceTabletManageDevice(tabletManageDevice.getId());
                    if (temp.size() == 0) {*/
                    if (!checkExistence(tabletManageDevice.getId()))
                        mainList.add(tabletManageDevice);
//                    AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDao().insertTabletManageDevice(tabletManageDevice);
//                    }
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


    private boolean checkExistence(String id) {
        for (int i = 0; i < mainList.size(); i++) {
            if (mainList.get(i).getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
