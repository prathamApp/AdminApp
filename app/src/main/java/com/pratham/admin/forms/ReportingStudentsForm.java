package com.pratham.admin.forms;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.pratham.admin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportingStudentsForm extends AppCompatActivity {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.rg_Groups)
    RadioGroup rg_Groups;
    @BindView(R.id.rg_Student)
    RadioGroup rg_Student;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporting_students_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();
    }
}
