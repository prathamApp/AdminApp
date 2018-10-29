package com.pratham.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.util.ROll_ID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageDevice extends AppCompatActivity {
    @BindView(R.id.btn_assignTablet)
    Button btn_assignTablet;

    @BindView(R.id.returnTablet)
    Button returnTablet;

    @BindView(R.id.acionstatus)
    Button acionstatus;

    String LoggedcrlName;
    String LoggedcrlId;
   /* final String Admin = "7";
    final String BRG_CRL_Tutor = "6";
    final String Block_Head = "5";
    final String District_Head = "4";
    final String State_Program_Head = "3";
    final String Program_Head = "2";*/

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
        switch (role) {
            case ROll_ID.BRG_CRL_Tutor:
                btn_assignTablet.setVisibility(View.GONE);
                acionstatus.setVisibility(View.GONE);
                break;
            case ROll_ID.Block_Head:
                acionstatus.setVisibility(View.GONE);
                break;
            case ROll_ID.District_Head:
                acionstatus.setVisibility(View.GONE);
                break;

            case ROll_ID.Program_Head:
                acionstatus.setVisibility(View.GONE);
                break;
            case ROll_ID.State_Program_Head:
                acionstatus.setVisibility(View.GONE);
                break;
            case ROll_ID.National_Program_Head:
                acionstatus.setVisibility(View.GONE);
                break;
            case ROll_ID.Store:

                break;
            case ROll_ID.Admin:
                acionstatus.setVisibility(View.GONE);
                break;
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
