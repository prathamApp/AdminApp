package com.pratham.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageDevice extends AppCompatActivity {
    @BindView(R.id.btn_assignTablet)
    Button btn_assignTablet;

    String LoggedcrlName;
    String LoggedcrlId;
    final String CRL_ROLE = "6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);
        ButterKnife.bind(this);
        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRules();
    }

    private void setRules() {
        String role = AppDatabase.getDatabaseInstance(this).getCRLdao().getCRLsRoleById(LoggedcrlId);
        if (role.equalsIgnoreCase(CRL_ROLE)) {
            btn_assignTablet.setVisibility(View.GONE);
        }
    }
    /*  public void pullData(View view) {
        Intent intent = new Intent(this, PullDataMD.class);
        startActivity(intent);
    }*/

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

    public void action_status(View view) {
        Intent intent = new Intent(this, Status_Action.class);
        intent.putExtra("CRLid", LoggedcrlId);
        intent.putExtra("CRLname", LoggedcrlName);
        startActivity(intent);
    }
}
