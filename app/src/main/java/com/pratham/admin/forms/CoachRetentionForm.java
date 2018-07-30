package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.CustomGroup;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CoachRetentionForm extends AppCompatActivity {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.sp_SelectCoach)
    Spinner sp_SelectCoach;
    @BindView(R.id.rg_DropOut)
    RadioGroup rg_DropOut;
    @BindView(R.id.rb_Yes)
    RadioButton rb_Yes;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;
    List<Village> villageList = new ArrayList<>();
    List<Coach> coachList = new ArrayList<>();
    String selectedCoachID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_retention_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        populateVillages();

        // Populate Coach Spinner
        coachList = AppDatabase.getDatabaseInstance(this).getCoachDao().getAllCoaches();
        populateCoaches();

    }

    @OnClick(R.id.btn_Submit)
    public void submitForm(View view) {

        if ((sp_Village.getSelectedItemPosition() > 0) && (sp_SelectCoach.getSelectedItemPosition() > 0)) {

            String endDate = btn_DatePicker.getText().toString().trim();

            // Dropout code on Submit Click
            int selectedId = rg_DropOut.getCheckedRadioButtonId();
            RadioButton selectedOption = (RadioButton) findViewById(selectedId);
            String DropOut = selectedOption.getText().toString();
            int status;
            if (DropOut.equalsIgnoreCase("Yes")) {
                status = 0; // Inactive
            } else {
                status = 1; // Active
                endDate = ""; // Still Active
            }
            String coachID = selectedCoachID;

            // update flags
            AppDatabase.getDatabaseInstance(CoachRetentionForm.this).getCoachDao().updateSentFlag(0, selectedCoachID);
            AppDatabase.getDatabaseInstance(this).getCoachDao().updateCoachStatus(status, endDate, coachID);

            resetForm();

            Toast.makeText(this, "Form Submitted !!!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Please Select All Fields !", Toast.LENGTH_SHORT).show();
        }

    }

    private void resetForm() {
        populateVillages();
        populateCoaches();
        rg_DropOut.clearCheck();
        rb_Yes.setChecked(true);
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
    }


    private void populateVillages() {

        final List VillageName = new ArrayList();
        if (!villageList.isEmpty()) {
            VillageName.add(new CustomGroup("Select Village"));
            for (int j = 0; j < villageList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(villageList.get(j).getVillageName(), villageList.get(j).getVillageId());
                VillageName.add(customGroup);
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(CoachRetentionForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
            sp_Village.setAdapter(villageAdapter);
        }

        sp_Village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) VillageName.get(pos);
                String vid = customGroup.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void populateCoaches() {
        final List CoachName = new ArrayList();
        if (!coachList.isEmpty()) {
            CoachName.add(new CustomGroup("Select Coach"));
            for (int j = 0; j < coachList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(coachList.get(j).getCoachName(), coachList.get(j).getCoachID());
                CoachName.add(customGroup);
            }
            ArrayAdapter coachAdapter = new ArrayAdapter(CoachRetentionForm.this, android.R.layout.simple_spinner_dropdown_item, CoachName);
            sp_SelectCoach.setAdapter(coachAdapter);
        }

        sp_SelectCoach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) CoachName.get(pos);
                selectedCoachID = customGroup.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @OnClick(R.id.btn_DatePicker)
    public void endDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

}
