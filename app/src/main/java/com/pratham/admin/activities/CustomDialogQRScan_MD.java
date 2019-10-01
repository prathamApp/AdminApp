package com.pratham.admin.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.activities.replaceTab.ReplaceTablet;
import com.pratham.admin.adapters.QRScanAdapter_MD;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.QRRecyclerListener;
import com.pratham.admin.interfaces.QRScanListener;
import com.pratham.admin.modalclasses.TabletManageDevice;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomDialogQRScan_MD extends Dialog implements QRRecyclerListener {
    @BindView(R.id.count)
    TextView txt_count;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    private List<TabletManageDevice> changesList;
    Context context;
    private Context assignTabletMD;
    private QRScanListener qrScanListener;
    private QRScanAdapter_MD qrScanAdapter_md;

    public CustomDialogQRScan_MD(@NonNull Context context, List changesList) {
        super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        this.changesList = changesList;
        this.context = context;
        assignTabletMD = context;
        qrScanListener = (QRScanListener) context;
    }

/*
    public CustomDialogQRScan_MD(@NonNull Context context, List changesList, int temp) {
        super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        this.changesList = changesList;
        this.context = context;
        assignTabletMD = (ReplaceTablet_MD) context;
        qrScanListener = (ReplaceTablet_MD) context;
    }
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_list_dialog);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        message.setText("Do You Want To Upload Following Changes?");
        setCount();
        qrScanAdapter_md = new QRScanAdapter_MD(this, changesList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(qrScanAdapter_md);
        qrScanAdapter_md.notifyDataSetChanged();
    }

    public void setCount() {
        txt_count.setText("Total Changes:" + changesList.size());
    }

    @OnClick(R.id.txt_Ok)
    public void update() {
        qrScanListener.update();
    }


    @OnClick(R.id.txt_clear_changes_village)
    public void clearChangesList() {
        qrScanListener.clearChanges();
        this.dismiss();
        ((Activity) assignTabletMD).finish();
    }

    @Override
    public void delete(final int position) {
        new AlertDialog.Builder(context).setMessage("Do you want to delete?")
                .setPositiveButton("yes", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String qrId = changesList.get(position).getQR_ID();
                        AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().deleteTabletManageDevice(qrId);
                        changesList.remove(position);
                        setCount();
                        qrScanAdapter_md.notifyDataSetChanged();
                        if (changesList.isEmpty()) {
                            dialog.dismiss();
                        }
                    }
                }).setNegativeButton("No", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (context instanceof AssignTabletMD) {
            AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().deleteAssignAndReturnDevice();
        } else if (context instanceof ReplaceTablet) {
            AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().deleteReplaceDevice();
        }
        AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().insertTabletAllManageDevice(changesList);
        ((Activity) assignTabletMD).finish();
    }
}
