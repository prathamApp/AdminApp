package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Color;
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
import com.pratham.admin.custom.MultiSpinner;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.modalclasses.Attendance;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Student;
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
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.admin.util.APIs.PushForms;

public class AttendanceForm extends AppCompatActivity implements ConnectionReceiverListener {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.sp_Groups)
    Spinner sp_Groups;
    @BindView(R.id.sp_Students)
    MultiSpinner sp_Students;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.rg_Present)
    RadioGroup rg_Present;
    @BindView(R.id.rb_Yes)
    RadioButton rb_Yes;
    boolean internetIsAvailable = false;
    UUID uuid;
    String uniqueAttendanceID = "";

    List<Village> villageList = new ArrayList<>();
    List<Student> AllStudentsInDB = new ArrayList<>();
    List<Groups> AllGroupsInDB;
    List registeredGRPs;

    // Selected Students
    List registeredStd;
    private boolean[] selectedStdItems;
    List<CustomGroup> Stds = new ArrayList<CustomGroup>();
    String selectedStudents = "";

    String vid;
    String groupId;
    String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        checkConnection();

        // Generate Random UUID
        uniqueAttendanceID = UUID.randomUUID().toString();


        //retrive all groups from  DB
        AllGroupsInDB = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();

        // Populate Initial Std Spinner
        AllStudentsInDB = AppDatabase.getDatabaseInstance(this).getStudentDao().getAllStudents();

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        populateVillages();

        // Set Default Todays date
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);

    }

    @OnClick(R.id.btn_Submit)
    public void saveForm(View view) {
        if ((sp_Village.getSelectedItemPosition() > 0) && (sp_Groups.getSelectedItemPosition() > 0)
                && (selectedStudents.trim().length() > 0)) {

            try {

                // Presenty code
                int selectedId = rg_Present.getCheckedRadioButtonId();
                RadioButton selectedGender = (RadioButton) findViewById(selectedId);
                String present = selectedGender.getText().toString();
                int presentStatus = 0; // 0: Absent, 1: Present
                if (present.equalsIgnoreCase("Yes"))
                    presentStatus = 1;
                else
                    presentStatus = 0;

                date = btn_DatePicker.getText().toString().trim();

                // DB Entry
                Attendance aObj = new Attendance();
                aObj.AttendanceID = uniqueAttendanceID;
                aObj.VillageID = vid;
                aObj.GroupID = groupId;
                aObj.StudentID = selectedStudents;
                aObj.Date = date;
                aObj.Present = presentStatus;
                aObj.sentFlag = 0;

                AppDatabase.getDatabaseInstance(this).getAttendanceDao().insertAttendance(Collections.singletonList(aObj));
                Toast.makeText(this, "Form Saved to Database !!!", Toast.LENGTH_SHORT).show();
                checkConnection();
                // Push To Server
                try {
                    if (internetIsAvailable) {
                        Gson gson = new Gson();
                        String AttendanceJSON = gson.toJson(Collections.singletonList(aObj));

                        MetaData metaData = new MetaData();
                        metaData.setKeys("pushDataTime");
                        metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));
                        List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
                        String metaDataJSON = customParse(metaDataList);
                        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

                        String json = "{ \"AttendanceJSON\":" + AttendanceJSON + ",\"metadata\":" + metaDataJSON + "}";
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
                                // update flag
                                AppDatabase.getDatabaseInstance(AttendanceForm.this).getAttendanceDao().updateSentFlag(1, uniqueAttendanceID);
                                Toast.makeText(AttendanceForm.this, "Form Data Pushed to Server !!!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                resetForm();
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(AttendanceForm.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                                AppDatabase.getDatabaseInstance(AttendanceForm.this).getAttendanceDao().updateSentFlag(0, uniqueAttendanceID);
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
            } catch (Exception e) {

            }

        } else {
            Toast.makeText(this, "Please Fill All the Fields !!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void resetForm() {
        populateVillages();
        rg_Present.clearCheck();
        rb_Yes.setChecked(true);
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
        uniqueAttendanceID = UUID.randomUUID().toString();
        checkConnection();
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


    @OnClick(R.id.btn_DatePicker)
    public void startDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    private void populateVillages() {

        final List VillageName = new ArrayList();
        if (!villageList.isEmpty()) {
            VillageName.add(new CustomGroup("Select Village"));
            for (int j = 0; j < villageList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(villageList.get(j).getVillageName(), villageList.get(j).getVillageId());
                VillageName.add(customGroup);
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(AttendanceForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
            sp_Village.setAdapter(villageAdapter);
        }

        sp_Village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) VillageName.get(pos);
                vid = customGroup.getId();

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

        ArrayAdapter grpAdapter = new ArrayAdapter(AttendanceForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGRPs);
        sp_Groups.setAdapter(grpAdapter);
        sp_Groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) registeredGRPs.get(pos);
                groupId = customGroup.getId();
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

            ArrayAdapter StdAdapter = new ArrayAdapter(AttendanceForm.this, android.R.layout.simple_spinner_dropdown_item, registeredStd);
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


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }
}
