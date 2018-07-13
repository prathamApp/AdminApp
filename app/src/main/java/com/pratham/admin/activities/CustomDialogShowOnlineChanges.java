package com.pratham.admin.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.adapters.OnlineChangesListAdapter;
import com.pratham.admin.interfaces.OnlineChanges;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CustomDialogShowOnlineChanges extends Dialog {
    @BindView(R.id.count)
    TextView txt_count;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.title)
    LinearLayout title;
    List changesList;
    Context context;
    OnlineChanges onlineChanges;

    public CustomDialogShowOnlineChanges(@NonNull Context context, List changesList) {
        super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        this.changesList = changesList;
        this.context = context;
        onlineChanges = (OnlineChanges) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_list_dialog);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        title.setVisibility(View.GONE);
        message.setText("Do You Want To Upload Following Changes?");
        txt_count.setText("Total Students:" + changesList.size());

        OnlineChangesListAdapter tempAdapter = new OnlineChangesListAdapter(changesList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(tempAdapter);
        tempAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.txt_Ok)
    public void update() {
        onlineChanges.update();
    }

    /*@OnClick(R.id.close)
    public void closeDialog() {
        if(changesList!=null){
            changesList.clear();
        }
        this.dismiss();
    }*/

    @OnClick(R.id.txt_clear_changes_village)
    public void clearChangesList() {
        onlineChanges.clearList();

        this.dismiss();
    }
}
