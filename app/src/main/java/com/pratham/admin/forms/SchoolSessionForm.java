package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Course;
import com.pratham.admin.modalclasses.Groups;
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

    ArrayList groupNameList;
    List<Groups> studGroupList;
    private String registeredGroups = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_session_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);

        studGroupList = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();
        groupNameList = new ArrayList();

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
            CourseName.add("Select Course");
            for (int j = 0; j < courseList.size(); j++) {
                CourseName.add(courseList.get(j).getCourseName() + "  (ID:: " + courseList.get(j).getCourseID() + ")");
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
        List VillageName = new ArrayList();
        if (!villageList.isEmpty()) {
            VillageName.add("Select Village");
            for (int j = 0; j < villageList.size(); j++) {
                VillageName.add(villageList.get(j).getVillageName() + "  (ID:: " + villageList.get(j).getVillageId() + ")");
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(SchoolSessionForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
            sp_Village.setAdapter(villageAdapter);
        }

        sp_Village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedState = sp_Village.getSelectedItem().toString();
//                Toast.makeText(CoachInformationForm.this, "" + selectedState, Toast.LENGTH_SHORT).show();

                groupNameList.clear();
                String selected_village = adapterView.getItemAtPosition(pos).toString();
                String selectedVillageID = "null";
                List<Groups> villageWiseGroups = new ArrayList();

                try {
                    String splitedVillageBracetRemoved = selected_village.substring(selected_village.indexOf("(") + 1, selected_village.indexOf(")"));
                    String[] splitedVillageData = splitedVillageBracetRemoved.split("ID::");
                    selectedVillageID = splitedVillageData[1].trim();
                    selectedVillageID = selectedVillageID.replace(")", "");
                } catch (Exception e) {

                }
                for (int i = 0; i < studGroupList.size(); i++) {
                    if (selectedVillageID.equals(studGroupList.get(i).getVillageId())) {
                        villageWiseGroups.add(studGroupList.get(i));
                    }
                }

                if (villageWiseGroups.size() == 0) {
                    groupNameList.add(new CustomGroup("No Groups to Select"));
                } else {
                    groupNameList.add(new CustomGroup("Select Registered Groups"));
                    for (int i = 0; i < villageWiseGroups.size(); i++) {
                        if (villageWiseGroups.get(i).getGroupName() != null && (!"".equals(villageWiseGroups.get(i).getGroupName()))) {
                            groupNameList.add(new CustomGroup(villageWiseGroups.get(i).getGroupName(), villageWiseGroups.get(i).getGroupId()));
                        }
                    }
                }

                // Populate Registered Groups Spinner
                populateRegisteredGroups();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void populateRegisteredGroups() {
        // todo get registered grps
        String[] registeredGRPs = new String[groupNameList.size()];
        if (groupNameList != null) {
            for (int i = 0; i < groupNameList.size(); i++) {
                registeredGRPs[i] = groupNameList.get(i).toString();
            }
        }
        ArrayAdapter specialityAdapter = new ArrayAdapter(SchoolSessionForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGRPs);
        sp_Groups.setAdapter(specialityAdapter);
        sp_Groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedSpeciality = sp_Groups.getSelectedItem().toString();
                if (selectedSpeciality.contains("Select")) {
                } else {
                    registeredGroups = selectedSpeciality;
                    Toast.makeText(SchoolSessionForm.this, "" + registeredGroups, Toast.LENGTH_SHORT).show();
                }
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
