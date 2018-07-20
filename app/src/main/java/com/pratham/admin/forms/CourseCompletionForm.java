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
import com.pratham.admin.util.DatePickerFragmentTwo;
import com.pratham.admin.util.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CourseCompletionForm extends AppCompatActivity {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.rg_Groups)
    RadioGroup rg_Groups;
    @BindView(R.id.rg_CourseOne)
    RadioGroup rg_CourseOne;
    @BindView(R.id.rg_TopicOne)
    RadioGroup rg_TopicOne;
    @BindView(R.id.rg_CourseTwo)
    RadioGroup rg_CourseTwo;
    @BindView(R.id.rg_Event)
    RadioGroup rg_Event;
    @BindView(R.id.edt_ParentCount)
    EditText edt_ParentCount;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.btn_DatePickerTwo)
    Button btn_DatePickerTwo;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_completion_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();


        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
        btn_DatePickerTwo.setText(new Utility().GetCurrentDate().toString());
        btn_DatePickerTwo.setPadding(8, 8, 8, 8);
    }

    @OnClick(R.id.btn_DatePicker)
    public void startDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    @OnClick(R.id.btn_DatePickerTwo)
    public void endDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentTwo();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

}