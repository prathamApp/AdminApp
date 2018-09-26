package com.pratham.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pratham.admin.R;

public class ManageDevice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_device);
    }

    public void pullData(View view) {
        Intent intent = new Intent(this, PullDataMD.class);
        startActivity(intent);
    }

    public void assignTablet(View view) {
        Intent intent = new Intent(this, AssignTabletMD.class);
        startActivity(intent);
    }

    public void returnTablet(View view) {

    }
}
