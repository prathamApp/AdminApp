package com.pratham.admin.forms;

// multispinner ref : https://github.com/thomashaertel/MultiSpinner

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.CustomGroup;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
    @BindView(R.id.rb_Male)
    RadioButton rb_Male;
    @BindView(R.id.sp_Occupation)
    Spinner sp_Occupation;
    @BindView(R.id.sp_Speciality)
    Spinner sp_Speciality;
    @BindView(R.id.sp_Education)
    Spinner sp_Education;
    @BindView(R.id.sp_SubjectExpert)
    MultiSpinner sp_SubjectExpert;
    @BindView(R.id.sp_Groups)
    MultiSpinner sp_Groups;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    List<Village> villageList = new ArrayList<>();
    private String occupation = "";
    private String speciality = "";
    private String education = "";

    boolean[] selectedItems;
    String[] ExpertSubj;
    String[] selectedESArray;
    String selectedExpertSubjects = "";

    List<Groups> AllGroupsInDB;
    List registeredGRPs;

    UUID UUID;
    String uniqueCoachID = "";
    String grpID = "";

    // Selected Groups
    List<String> selectedGroupsArray;
    List registeredGroups;
    private boolean[] selectedGroupItems;
    List<String> Grps;
    String selectedGroups = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_information_form);
        ButterKnife.bind(this);
        // Hide Actionbar
        getSupportActionBar().hide();

        // Generate Random UUID
        uniqueCoachID = UUID.randomUUID().toString();

        // Set Default Todays date
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);

        //retrive all groups from  DB
        AllGroupsInDB = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();

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
                } else if (selectedSpeciality.equalsIgnoreCase("Others")) {
                    // Dialog
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CoachInformationForm.this);
                    LayoutInflater inflater = CoachInformationForm.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.others_dialog, null);
                    dialogBuilder.setView(dialogView);
                    final EditText edt = (EditText) dialogView.findViewById(R.id.edt_Others);
                    dialogBuilder.setCancelable(false);
                    dialogBuilder.setTitle("Please Mention Others ");
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            speciality = edt.getText().toString().trim();
                            if (edt.getText().toString().trim().equalsIgnoreCase("")) {
                                populateSpeciality();
                            } else {
                                Toast.makeText(CoachInformationForm.this, "Speciality = " + speciality, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //pass
                            populateSpeciality();
                        }
                    });
                    AlertDialog b = dialogBuilder.create();
                    b.show();
                } else {
                    speciality = selectedSpeciality;
//                    Toast.makeText(CoachInformationForm.this, "" + speciality, Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(CoachInformationForm.this, "" + education, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // VISITED GROUPS
    private void populateRegisteredGroups(String villageID) {
        // todo get registered grps
        registeredGroups = new ArrayList();
        if (AllGroupsInDB != null) {
            Grps = new ArrayList<>();
            for (int i = 0; i < AllGroupsInDB.size(); i++) {
                if (AllGroupsInDB.get(i).getVillageId().equals(villageID)) {
                    registeredGroups.add(new CustomGroup(AllGroupsInDB.get(i).getGroupName(), AllGroupsInDB.get(i).getGroupId()));
                    Grps.add(AllGroupsInDB.get(i).getGroupId());
                }
            }
        }

        ArrayAdapter grpAdapter = new ArrayAdapter(CoachInformationForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGroups);
        sp_Groups.setAdapter(grpAdapter, false, onVGSelectedListener);
        // set initial selection
        selectedGroupItems = new boolean[grpAdapter.getCount()];
        sp_Groups.setHint("Select Groups");
        sp_Groups.setHintTextColor(Color.BLACK);

    }

    // VG Listener
    private MultiSpinner.MultiSpinnerListener onVGSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            List<String> grp_sel = new ArrayList<>();
            selectedGroupsArray = new ArrayList<>();
            selectedGroups = "";
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedGroupsArray.add(Grps.get(i));
                    selectedGroups = selectedGroups + "," + Grps.get(i);
                }
            }
            selectedGroups = selectedGroups.replaceFirst(",", "");
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

    // Listener
    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            selectedESArray = new String[selected.length];
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedESArray[i] = ExpertSubj[i];
                    selectedExpertSubjects = selectedExpertSubjects + "," + selectedESArray[i];
                }
            }
            selectedExpertSubjects = selectedExpertSubjects.replaceFirst(",", "");
//            Toast.makeText(CoachInformationForm.this, "" + selectedExpertSubjects, Toast.LENGTH_SHORT).show();


        }
    };


    private void populateOccupation() {
        ArrayAdapter occupationAdapter = new ArrayAdapter(CoachInformationForm.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.array_Occupation));
        sp_Occupation.setAdapter(occupationAdapter);
        sp_Occupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedOccupation = sp_Occupation.getSelectedItem().toString();
                if (selectedOccupation.contains("Select")) {
                } else if (selectedOccupation.equalsIgnoreCase("Others")) {
                    // Dialog
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CoachInformationForm.this);
                    LayoutInflater inflater = CoachInformationForm.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.others_dialog, null);
                    dialogBuilder.setView(dialogView);
                    final EditText edt = (EditText) dialogView.findViewById(R.id.edt_Others);
                    dialogBuilder.setCancelable(false);
                    dialogBuilder.setTitle("Please Mention Others ");
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            occupation = edt.getText().toString().trim();
                            if (edt.getText().toString().trim().equalsIgnoreCase("")) {
                                populateOccupation();
                            } else {
                                Toast.makeText(CoachInformationForm.this, "Occupation = " + occupation, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //pass
                            populateOccupation();
                        }
                    });
                    AlertDialog b = dialogBuilder.create();
                    b.show();
                } else {
                    occupation = selectedOccupation;
//                    Toast.makeText(CoachInformationForm.this, "" + occupation, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void populateVillages() {
        final List VillageName = new ArrayList();
        if (!villageList.isEmpty()) {
            VillageName.add(new CustomGroup("Select Village"));
            for (int j = 0; j < villageList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(villageList.get(j).getVillageName(), villageList.get(j).getVillageId());
                VillageName.add(customGroup);
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(CoachInformationForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
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

    @OnClick(R.id.btn_DatePicker)
    public void startDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    @OnClick(R.id.btn_Submit)
    public void submitData(View view) {

        if ((sp_Village.getSelectedItemPosition() > 0) && (edt_Name.getText().toString().trim().length() > 0)
                && (edt_Age.getText().toString().trim().length() > 0) && (sp_Education.getSelectedItemPosition() > 0)
                && (sp_Speciality.getSelectedItemPosition() > 0) && (selectedGroups.trim().length() > 0)
                && (selectedExpertSubjects.trim().length() > 0)) {

            try {
                // Gender code on Submit Click
                int selectedId = rg_Gender.getCheckedRadioButtonId();
                RadioButton selectedGender = (RadioButton) findViewById(selectedId);
                String gender = selectedGender.getText().toString();

                String date = btn_DatePicker.getText().toString().trim();

                // DB Entry
                Coach cObj = new Coach();
                cObj.CoachID = uniqueCoachID;
                cObj.CoachName = edt_Name.getText().toString().trim();
                cObj.CoachAge = Integer.parseInt(edt_Age.getText().toString().trim());
                cObj.CoachGender = gender;
                cObj.CoachSubjectExpert = selectedExpertSubjects.trim();
                cObj.CoachOccupation = occupation;
                cObj.CoachSpeciality = speciality;
                cObj.CoachEducation = education;
                cObj.CoachActive = 1;
                cObj.CoachGroupID = selectedGroups;
                cObj.StartDate = date;
                cObj.EndDate = "";
                cObj.CreatedBy = "";
                cObj.CreatedDate = date;
                cObj.sentFlag = 0;

                AppDatabase.getDatabaseInstance(this).getCoachDao().insertCoach(Collections.singletonList(cObj));

                Toast.makeText(this, "Form Submitted !!!", Toast.LENGTH_SHORT).show();
                resetForm();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Please fill all the fields !!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void resetForm() {
        populateVillages();
        populateEducation();
        populateOccupation();
        populateSpeciality();
        edt_Name.getText().clear();
        edt_Age.getText().clear();
        populateSubjectExpert();
        rg_Gender.clearCheck();
        rb_Male.setChecked(true);
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
        uniqueCoachID = UUID.randomUUID().toString();
    }

}
