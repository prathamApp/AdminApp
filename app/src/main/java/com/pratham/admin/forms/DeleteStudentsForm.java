package com.pratham.admin.forms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.CustomGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.admin.util.APIs.PushForms;

public class DeleteStudentsForm extends AppCompatActivity implements ConnectionReceiverListener {

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
    List<CustomGroup> StdsName = new ArrayList<CustomGroup>();
    String selectedStudents = "";
    String selectedStudentsName = "";

    List selectedStdList;
    String villageName;
    String groupName;

    boolean internetIsAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_students_form);
        ButterKnife.bind(this);
        // Hide Actionbar
        getSupportActionBar().hide();

        checkConnection();

        //retrive all groups from  DB
        AllGroupsInDB = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();
        Collections.sort(AllGroupsInDB, new Comparator<Groups>() {
            public int compare(Groups v1, Groups v2) {
                return v1.getGroupName().compareTo(v2.getGroupName());
            }
        });

        // Populate Initial Std Spinner
        AllStudentsInDB = AppDatabase.getDatabaseInstance(this).getStudentDao().getAllStudents();
        Collections.sort(AllStudentsInDB, new Comparator<Student>() {
            public int compare(Student v1, Student v2) {
                return v1.getFullName().compareTo(v2.getFullName());
            }
        });

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        Collections.sort(villageList, new Comparator<Village>() {
            public int compare(Village v1, Village v2) {
                return v1.getVillageName().compareTo(v2.getVillageName());
            }
        });
        populateVillages();
    }

    @OnClick(R.id.btn_Submit)
    public void submitForm(View view) {

        if ((sp_Village.getSelectedItemPosition() > 0) && (sp_Groups.getSelectedItemPosition() > 0)
                && (selectedStudents.trim().length() > 0)) {

            checkConnection();

            // DB Entry
            Student sObj = new Student();
            sObj.StudentId = selectedStudents;

            if (btn_Submit.getText().toString().equalsIgnoreCase("Submit")) {
                // Push To Server
                try {
                    if (internetIsAvailable) {
                        Gson gson = new Gson();
                        String DeleteStudentJSON = gson.toJson(Collections.singletonList(sObj));

                        MetaData metaData = new MetaData();
                        metaData.setKeys("pushDataTime");
                        metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));
                        List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
                        String metaDataJSON = customParse(metaDataList);
                        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

                        String json = "{ \"DeleteStudentJSON\":" + DeleteStudentJSON + ",\"metadata\":" + metaDataJSON + "}";
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
                                // delete if pushed
                                for (int i = 0; i < selectedStdList.size(); i++) {
                                    AppDatabase.getDatabaseInstance(DeleteStudentsForm.this).getStudentDao().deleteStudentByID(selectedStdList.get(i).toString());
                                }
                                Toast.makeText(DeleteStudentsForm.this, "Form Data Pushed to Server !!!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(DeleteStudentsForm.this, "Selected Students have been Deleted !!!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                resetForm();
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(DeleteStudentsForm.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                resetForm();
                            }
                        });

                    } else {
                        Toast.makeText(this, "Form Data NOT Pushed to Server as Internet isn't connected !!! ", Toast.LENGTH_SHORT).show();
                        resetForm();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Preview Dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DeleteStudentsForm.this, android.R.style.Theme_Material_Light_Dialog);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setTitle("Form Data Preview");

                dialogBuilder.setMessage("Village Name : " + villageName
                        + "\nGroup Name : " + groupName
                        + "\nSelected Students : " + selectedStudentsName);

                dialogBuilder.setPositiveButton("Correct", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        btn_Submit.setText("Submit");
                    }
                });
                dialogBuilder.setNegativeButton("Wrong", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        btn_Submit.setText("Preview");
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
            }


        } else {
            Toast.makeText(this, "Please Fill All the Fields !!!", Toast.LENGTH_SHORT).show();
        }

    }

    private void resetForm() {
        checkConnection();
        btn_Submit.setText("Preview");
        AllGroupsInDB.clear();
        AllStudentsInDB.clear();
        villageList.clear();
        //retrive all groups from  DB
        AllGroupsInDB = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();
        Collections.sort(AllGroupsInDB, new Comparator<Groups>() {
            public int compare(Groups v1, Groups v2) {
                return v1.getGroupName().compareTo(v2.getGroupName());
            }
        });

        // Populate Initial Std Spinner
        AllStudentsInDB = AppDatabase.getDatabaseInstance(this).getStudentDao().getAllStudents();
        Collections.sort(AllStudentsInDB, new Comparator<Student>() {
            public int compare(Student v1, Student v2) {
                return v1.getFullName().compareTo(v2.getFullName());
            }
        });

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        Collections.sort(villageList, new Comparator<Village>() {
            public int compare(Village v1, Village v2) {
                return v1.getVillageName().compareTo(v2.getVillageName());
            }
        });
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
                villageName = customGroup.getName();
                btn_Submit.setText("Preview");
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
                groupName = customGroup.getName();
                btn_Submit.setText("Preview");
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
                    StdsName.add(new CustomGroup(AllStudentsInDB.get(j).getFullName()));
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
            btn_Submit.setText("Preview");
            selectedStudents = "";
            selectedStudentsName = "";
            selectedStdList = new ArrayList();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedStudents = selectedStudents + "," + Stds.get(i);
                    selectedStudentsName = selectedStudentsName + "," + StdsName.get(i);
                    selectedStdList.add(Stds.get(i));
                }
            }
            selectedStudents = selectedStudents.replaceFirst(",", "");
            selectedStudentsName = selectedStudentsName.replaceFirst(",", "");
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
