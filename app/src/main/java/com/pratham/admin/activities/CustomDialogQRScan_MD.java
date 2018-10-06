package com.pratham.admin.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.pratham.admin.R;
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

    List<TabletManageDevice> changesList;
    Context context;
    AssignTabletMD assignTabletMD;
    QRScanListener qrScanListener;
    QRScanAdapter_MD qrScanAdapter_md;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().deleteAllTabletManageDevice();
        AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().insertTabletAllManageDevice(changesList);
        assignTabletMD.finish();
    }

    public CustomDialogQRScan_MD(@NonNull Context context, List changesList) {
        super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        this.changesList = changesList;
        this.context = context;
        assignTabletMD = (AssignTabletMD) context;
        qrScanListener = (QRScanListener) context;
    }

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
    }

    @Override
    public void delete(int position) {
        String qrId = changesList.get(position).getQR_ID();
        AppDatabase.getDatabaseInstance(context).getTabletManageDeviceDoa().deleteTabletManageDevice(qrId);
        changesList.remove(position);
        setCount();
        qrScanAdapter_md.notifyDataSetChanged();
        if (changesList.isEmpty()) {
            this.dismiss();
        }
    }


}
