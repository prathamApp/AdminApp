package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.pratham.admin.R;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CoachInformationForm extends AppCompatActivity{

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.edt_Name)
    EditText edt_Name;
    @BindView(R.id.edt_Age)
    EditText edt_Age;
    @BindView(R.id.rg_Gender)
    RadioGroup rg_Gender;
    @BindView(R.id.sp_Occupation)
    Spinner sp_Occupation;
    @BindView(R.id.sp_Speciality)
    Spinner sp_Speciality;
    @BindView(R.id.sp_Education)
    Spinner sp_Education;
    @BindView(R.id.sp_SubjectExpert)
    Spinner sp_SubjectExpert;
    @BindView(R.id.sp_Groups)
    Spinner sp_Groups;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_information_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();


        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
    }

    @OnClick(R.id.btn_DatePicker)
    public void startDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

}