package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Course;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.CustomGroup;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SchoolSessionForm extends AppCompatActivity {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.sp_Groups)
    Spinner sp_Groups;
    @BindView(R.id.sp_Course)
    Spinner sp_Course;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    List<Village> villageList = new ArrayList<>();
    List<Course> courseList = new ArrayList<>();

    private String registeredGroups = "";
    List<Student> AllStudentsInDB = new ArrayList<>();
    List<Groups> AllGroupsInDB;
    List registeredGRPs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_session_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);


        //retrive all groups from  DB
        AllGroupsInDB = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        populateVillages();

        // Populate Science Course Spinner
        courseList = AppDatabase.getDatabaseInstance(this).getCourses().getAllCourse();
        populateCourses();

    }

    private void populateCourses() {
        List CourseName = new ArrayList();
        if (!courseList.isEmpty()) {
            CourseName.add(new CustomGroup("Select Course"));
            for (int j = 0; j < courseList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(courseList.get(j).getCourseName(), courseList.get(j).getCourseID());
                CourseName.add(customGroup);
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(SchoolSessionForm.this, android.R.layout.simple_spinner_dropdown_item, CourseName);
            sp_Course.setAdapter(villageAdapter);
        } else {
            CourseName.add("Select Course");
            ArrayAdapter villageAdapter = new ArrayAdapter(SchoolSessionForm.this, android.R.layout.simple_spinner_dropdown_item, CourseName);
            sp_Course.setAdapter(villageAdapter);
        }

    }

    private void populateVillages() {

        final List VillageName = new ArrayList();
        if (!villageList.isEmpty()) {
            VillageName.add(new CustomGroup("Select Village"));
            for (int j = 0; j < villageList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(villageList.get(j).getVillageName(), villageList.get(j).getVillageId());
                VillageName.add(customGroup);
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(SchoolSessionForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
            sp_Village.setAdapter(villageAdapter);
        }

        sp_Village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) VillageName.get(pos);
                String vid = customGroup.getId();

                // Populate Registered Groups Spinner
                populateRegisteredGroups(vid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void populateRegisteredGroups(String villageID) {
        // todo get registered grps
        registeredGRPs = new ArrayList();
        registeredGRPs.add(new CustomGroup("Select Groups"));
        if (AllGroupsInDB != null) {
            for (int i = 0; i < AllGroupsInDB.size(); i++) {
                if (AllGroupsInDB.get(i).getVillageId().equals(villageID))
                    registeredGRPs.add(new CustomGroup(AllGroupsInDB.get(i).getGroupName(), AllGroupsInDB.get(i).getGroupId()));
            }
        }

        ArrayAdapter grpAdapter = new ArrayAdapter(SchoolSessionForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGRPs);
        sp_Groups.setAdapter(grpAdapter);
        sp_Groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) registeredGRPs.get(pos);
                String groupId = customGroup.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @OnClick(R.id.btn_DatePicker)
    public void visitDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }
}
