package com.pratham.admin.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.adapters.QRScanAdapter_TabletStatus;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.QRRecyclerListener;
import com.pratham.admin.interfaces.QRScanListener;
import com.pratham.admin.modalclasses.TabletStatus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomDialogTabletStatus extends Dialog implements QRRecyclerListener {
    @BindView(R.id.count)
    TextView txt_count;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.recycler)
    RecyclerView recycler;

    List<TabletStatus> changesList;
    Context context;
    Status_Action status_action;
    QRScanListener qrScanListener;
    QRScanAdapter_TabletStatus qrScanAdapter_tabletStatus;

    public CustomDialogTabletStatus(@NonNull Context context, List changesList) {
        super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        this.changesList = changesList;
        this.context = context;
        status_action = (Status_Action) context;
        qrScanListener = (QRScanListener) context;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppDatabase.getDatabaseInstance(context).getTabletStatusDao().deleteAllTabletStatus();
        AppDatabase.getDatabaseInstance(context).getTabletStatusDao().insertAllTabletStatus(changesList);
        status_action.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_status_tablet);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        message.setText(R.string.uploadChange);
        setCount();
        qrScanAdapter_tabletStatus = new QRScanAdapter_TabletStatus(this, changesList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(qrScanAdapter_tabletStatus);
        qrScanAdapter_tabletStatus.notifyDataSetChanged();
    }

    public void setCount() {
        txt_count.setText(R.string.totalchanges + changesList.size());
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
        String qrId = changesList.get(position).getQrID();
        AppDatabase.getDatabaseInstance(context).getTabletStatusDao().deleteTabTabletStatus(qrId);
        changesList.remove(position);
        setCount();
        qrScanAdapter_tabletStatus.notifyDataSetChanged();
        if (changesList.isEmpty()) {
            this.dismiss();
        }
    }


}
