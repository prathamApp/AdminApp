package com.pratham.admin.forms;

import android.app.DialogFragment;
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
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Village;
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
    Spinner sp_SubjectExpert;
    @BindView(R.id.sp_Groups)
    Spinner sp_Groups;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    List<Village> villageList = new ArrayList<>();
    private String occupation = "";
    private String speciality;


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

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        populateVillages();

        // Gender code on Submit Click
        int selectedId = rg_Gender.getCheckedRadioButtonId();
        RadioButton selectedGender = (RadioButton) findViewById(selectedId);
        String gender = selectedGender.getText().toString();

        // Populate Occupation Spinner
        populateOccupation();

        // Populate Speciality Spinner
        populateSpeciality();

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

                String selected_village = adapterView.getItemAtPosition(pos).toString();
                String selectedVillageID = "null";
                List<Groups> villageWiseGroups = new ArrayList();

                String splitedVillageBracetRemoved = selected_village.substring(selected_village.indexOf("(") + 1, selected_village.indexOf(")"));
                String[] splitedVillageData = splitedVillageBracetRemoved.split("ID::");
                try {
                    selectedVillageID = splitedVillageData[1].trim();
                    selectedVillageID = selectedVillageID.replace(")", "");
                } catch (Exception e) {

                }

                AppDatabase.destroyInstance();
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
