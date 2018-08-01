package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.CustomGroup;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.admin.util.APIs.PushForms;

public class CoachRetentionForm extends AppCompatActivity implements ConnectionReceiverListener {

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
    boolean internetIsAvailable = false;
    List<Coach> updatedCoachList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach_retention_form);
        ButterKnife.bind(this);

        checkConnection();

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

            checkConnection();

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

            updatedCoachList = AppDatabase.getDatabaseInstance(CoachRetentionForm.this).getCoachDao().getCoachByID(selectedCoachID);

            // get Coach info by id
            Coach cObj = new Coach();
            cObj.CoachID = updatedCoachList.get(0).CoachID;
            cObj.CoachName = updatedCoachList.get(0).CoachName;
            cObj.CoachAge = updatedCoachList.get(0).CoachAge;
            cObj.CoachGender = updatedCoachList.get(0).CoachGender;
            cObj.CoachSubjectExpert = updatedCoachList.get(0).CoachSubjectExpert;
            cObj.CoachOccupation = updatedCoachList.get(0).CoachOccupation;
            cObj.CoachSpeciality = updatedCoachList.get(0).CoachSpeciality;
            cObj.CoachEducation = updatedCoachList.get(0).CoachEducation;
            cObj.CoachActive = status; // new Values 1:Active, 0:Inactive
            cObj.CoachGroupID = updatedCoachList.get(0).CoachGroupID;
            cObj.StartDate = updatedCoachList.get(0).StartDate;
            cObj.EndDate = endDate; // new Values
            cObj.CreatedBy = updatedCoachList.get(0).CreatedBy;
            cObj.CreatedDate = updatedCoachList.get(0).CreatedDate;
            cObj.sentFlag = 0;

            AppDatabase.getDatabaseInstance(this).getCoachDao().updateCoachStatus(status, endDate, coachID);
            Toast.makeText(this, "Form Submitted to DB !!!", Toast.LENGTH_SHORT).show();

            // Push To Server
            try {
                if (internetIsAvailable) {
                    Gson gson = new Gson();
                    String CoachJSON = gson.toJson(Collections.singletonList(cObj));

                    MetaData metaData = new MetaData();
                    metaData.setKeys("pushDataTime");
                    metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));
                    List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
                    String metaDataJSON = customParse(metaDataList);
                    AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

                    String json = "{ \"CoachJSON\":" + CoachJSON + ",\"metadata\":" + metaDataJSON + "}";
                    Log.d("json :::", json);

                    final ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setTitle("UPLOADING ... ");
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    AndroidNetworking.post(PushForms).setContentType("application/json").addStringBody(json).build().getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("responce", response);
                            AppDatabase.getDatabaseInstance(CoachRetentionForm.this).getCoachDao().updateSentFlag(1, selectedCoachID);
                            Toast.makeText(CoachRetentionForm.this, "Form Data Pushed to Server !!!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            resetForm();
                        }

                        @Override
                        public void onError(ANError anError) {
                            Toast.makeText(CoachRetentionForm.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                            AppDatabase.getDatabaseInstance(CoachRetentionForm.this).getCoachDao().updateSentFlag(0, selectedCoachID);
                            dialog.dismiss();
                            resetForm();
                        }
                    });

                } else {
                    Toast.makeText(this, "Form Data not Pushed to Server as Internet isn't connected !!! ", Toast.LENGTH_SHORT).show();
                    resetForm();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        ApplicationController.getInstance().setConnectionListener(this);
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    private String customParse(List<MetaData> metaDataList) {
        String json = "{";

        for (int i = 0; i < metaDataList.size(); i++) {
            json = json + "\"" + metaDataList.get(i).getKeys() + "\":\"" + metaDataList.get(i).getValue() + "\"";
            if (i < metaDataList.size() - 1) {
                json = json + ",";
            }
        }
        json = json + "}";

        return json;
    }

}
