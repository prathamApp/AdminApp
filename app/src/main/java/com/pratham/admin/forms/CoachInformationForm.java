package com.pratham.admin.forms;

// multispinner ref : https://github.com/thomashaertel/MultiSpinner

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.pratham.admin.R;
import com.pratham.admin.custom.MultiSpinner;
import com.pratham.admin.database.AppDatabase;
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

public class CoachInformationForm extends AppCompatActivity {

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
    MultiSpinner sp_SubjectExpert;
    @BindView(R.id.sp_Groups)
    Spinner sp_Groups;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    List<Village> villageList = new ArrayList<>();
    private String occupation = "";
    private String speciality = "";
    private String SubjectExpert = "";
    private String education = "";
    private String registeredGroups = "";
    ArrayList groupNameList;
    List<Groups> studGroupList;
    boolean[] selectedItems;
    String[] ExpertSubj;
    String[] selectedESArray;
    String selectedExpertSubjects = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_information_form);
        ButterKnife.bind(this);
        // Hide Actionbar
        getSupportActionBar().hide();

        // Set Default Todays date
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);

        studGroupList = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();
        groupNameList = new ArrayList();

        // Gender code on Submit Click
        int selectedId = rg_Gender.getCheckedRadioButtonId();
        RadioButton selectedGender = (RadioButton) findViewById(selectedId);
        String gender = selectedGender.getText().toString();

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        populateVillages();

        // Populate Occupation Spinner
        populateOccupation();

        // Populate Speciality Spinner
        populateSpeciality();

        // Populate Education Spinner
        populateEducation();

        // Populate Subject Expert Multiselect Spinner
        ExpertSubj = new String[getResources().getStringArray(R.array.array_Subject_Expert).length];
        ExpertSubj = getResources().getStringArray(R.array.array_Subject_Expert);
        populateSubjectExpert();


    }

    private void populateSpeciality() {
        ArrayAdapter specialityAdapter = new ArrayAdapter(CoachInformationForm.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_Speciality));
        sp_Speciality.setAdapter(specialityAdapter);
        sp_Speciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedSpeciality = sp_Speciality.getSelectedItem().toString();
                if (selectedSpeciality.contains("Select")) {
                } else {
                    speciality = selectedSpeciality;
                    Toast.makeText(CoachInformationForm.this, "" + speciality, Toast.LENGTH_SHORT).show();
                }
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
        ArrayAdapter specialityAdapter = new ArrayAdapter(CoachInformationForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGRPs);
        sp_Groups.setAdapter(specialityAdapter);
        sp_Groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedSpeciality = sp_Groups.getSelectedItem().toString();
                if (selectedSpeciality.contains("Select")) {
                } else {
                    registeredGroups = selectedSpeciality;
                    Toast.makeText(CoachInformationForm.this, "" + registeredGroups, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void populateEducation() {
        ArrayAdapter specialityAdapter = new ArrayAdapter(CoachInformationForm.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_Education));
        sp_Education.setAdapter(specialityAdapter);
        sp_Education.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedEdu = sp_Education.getSelectedItem().toString();
                if (selectedEdu.contains("Select")) {
                } else {
                    education = selectedEdu;
                    Toast.makeText(CoachInformationForm.this, "" + education, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    // Listener
    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            selectedESArray = new String[selected.length];
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedESArray[i] = ExpertSubj[i];
                    selectedExpertSubjects = selectedExpertSubjects + "," + selectedESArray[i].toString();
                }
            }
            selectedExpertSubjects = selectedExpertSubjects.replaceFirst(",", "");
            Toast.makeText(CoachInformationForm.this, "" + selectedExpertSubjects, Toast.LENGTH_SHORT).show();


        }
    };

    private void populateSubjectExpert() {
        ArrayAdapter subAdapter = new ArrayAdapter(CoachInformationForm.this, android.R.layout.simple_spinner_dropdown_item, ExpertSubj);
        sp_SubjectExpert.setAdapter(subAdapter, false, onSelectedListener);
        // set initial selection
        selectedItems = new boolean[subAdapter.getCount()];
//        selectedItems[0] = true; // select first item
//        sp_SubjectExpert.setSelected(selectedItems);
        sp_SubjectExpert.setHint("Select Subject Expert");
        sp_SubjectExpert.setHintTextColor(Color.BLACK);

    }

    private void populateOccupation() {
        ArrayAdapter occupationAdapter = new ArrayAdapter(CoachInformationForm.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_Occupation));
        sp_Occupation.setAdapter(occupationAdapter);
        sp_Occupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedOccupation = sp_Occupation.getSelectedItem().toString();
                if (selectedOccupation.contains("Select")) {
                } else {
                    occupation = selectedOccupation;
                    Toast.makeText(CoachInformationForm.this, "" + occupation, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void populateVillages() {
        List VillageName = new ArrayList();
        if (!villageList.isEmpty()) {
            VillageName.add("Select Village");
            for (int j = 0; j < villageList.size(); j++) {
                VillageName.add(villageList.get(j).getVillageName() + "  (ID:: " + villageList.get(j).getVillageId() + ")");
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(CoachInformationForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
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

    @OnClick(R.id.btn_DatePicker)
    public void startDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

}
