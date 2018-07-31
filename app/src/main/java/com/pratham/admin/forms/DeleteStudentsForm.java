package com.pratham.admin.forms;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.pratham.admin.R;
import com.pratham.admin.custom.MultiSpinner;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.CustomGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeleteStudentsForm extends AppCompatActivity {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.sp_Groups)
    Spinner sp_Groups;
    @BindView(R.id.sp_Students)
    MultiSpinner sp_Students;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    List<Village> villageList = new ArrayList<>();
    List<Student> AllStudentsInDB = new ArrayList<>();
    List<Groups> AllGroupsInDB;
    List registeredGRPs;

    // Selected Students
    List registeredStd;
    private boolean[] selectedStdItems;
    List<CustomGroup> Stds = new ArrayList<CustomGroup>();
    String selectedStudents = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_students_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        //retrive all groups from  DB
        AllGroupsInDB = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();

        // Populate Initial Std Spinner
        AllStudentsInDB = AppDatabase.getDatabaseInstance(this).getStudentDao().getAllStudents();

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        populateVillages();
    }

    private void populateVillages() {

        final List VillageName = new ArrayList();
        if (!villageList.isEmpty()) {
            VillageName.add(new CustomGroup("Select Village"));
            for (int j = 0; j < villageList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(villageList.get(j).getVillageName(), villageList.get(j).getVillageId());
                VillageName.add(customGroup);
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(DeleteStudentsForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
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
                if (AllGroupsInDB.get(i).getVillageId().equals(villageID)) {
                    registeredGRPs.add(new CustomGroup(AllGroupsInDB.get(i).getGroupName(), AllGroupsInDB.get(i).getGroupId()));
                }
            }
        }

        ArrayAdapter grpAdapter = new ArrayAdapter(DeleteStudentsForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGRPs);
        sp_Groups.setAdapter(grpAdapter);
        sp_Groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) registeredGRPs.get(pos);
                String groupId = customGroup.getId();
                // Populate Students according to Group Spinner
                populateStudents(groupId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    // Selected Students
    private void populateStudents(String grpID) {
        // todo get registered grps
        registeredStd = new ArrayList();
        if (!AllStudentsInDB.isEmpty()) {
            for (int j = 0; j < AllStudentsInDB.size(); j++) {
                if (AllStudentsInDB.get(j).getGroupId().equals(grpID)) {
                    registeredStd.add(new CustomGroup(AllStudentsInDB.get(j).getFullName(), AllStudentsInDB.get(j).getStudentId()));
                    Stds.add(new CustomGroup(AllStudentsInDB.get(j).getStudentId()));

                }
            }

            ArrayAdapter StdAdapter = new ArrayAdapter(DeleteStudentsForm.this, android.R.layout.simple_spinner_dropdown_item, registeredStd);
            sp_Students.setAdapter(StdAdapter, false, onStdSelectedListener);
            selectedStdItems = new boolean[StdAdapter.getCount()];
            sp_Students.setHint("Select Students");
            sp_Students.setHintTextColor(Color.BLACK);

        }
    }

    // Std Listener
    private MultiSpinner.MultiSpinnerListener onStdSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            selectedStudents = "";
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedStudents = selectedStudents + "," + Stds.get(i);
                }
            }
            selectedStudents = selectedStudents.replaceFirst(",", "");
        }
    };


}
