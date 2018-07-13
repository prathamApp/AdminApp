package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.pratham.admin.R;
import com.pratham.admin.util.DatePickerFragment;
import com.pratham.admin.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CourseCompletionForm extends AppCompatActivity {


    @BindView(R.id.btn_StartDatePicker)
    Button btn_StartDatePicker;
    @BindView(R.id.btn_EndDatePicker)
    Button btn_EndDatePicker;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_completion_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();


        btn_StartDatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_StartDatePicker.setPadding(8, 8, 8, 8);
        btn_EndDatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_EndDatePicker.setPadding(8, 8, 8, 8);
    }

    @OnClick(R.id.btn_StartDatePicker)
    public void startDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    @OnClick(R.id.btn_EndDatePicker)
    public void endDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

}
