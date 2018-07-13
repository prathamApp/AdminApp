package com.pratham.admin.forms;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.pratham.admin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CourseEnrollmentForm extends AppCompatActivity {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.rg_Groups)
    RadioGroup rg_Groups;
    @BindView(R.id.rg_CourseOne)
    RadioGroup rg_CourseOne;
    @BindView(R.id.cb_Topics)
    LinearLayout cb_Topics;
    @BindView(R.id.rg_CourseTwo)
    RadioGroup rg_CourseTwo;
    @BindView(R.id.rg_CourseThree)
    RadioGroup rg_CourseThree;
    @BindView(R.id.rg_CourseFour)
    RadioGroup rg_CourseFour;
    @BindView(R.id.cb_Topics_CourseFour)
    LinearLayout cb_Topics_CourseFour;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.cb_Coach)
    LinearLayout cb_Coach;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_enrollment_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();
    }
}
