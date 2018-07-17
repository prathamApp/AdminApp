package com.pratham.admin.forms;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pratham.admin.R;

import butterknife.ButterKnife;

public class AttendanceForm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();
    }
}
