package com.pratham.admin.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.async.SaveDataTask;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.OnSavedData;
import com.pratham.admin.interfaces.VillageListLisner;
import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.ConnectionReceiver;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.admin.util.APIs.village;

public class SelectProgram extends AppCompatActivity implements ConnectionReceiverListener, OnSavedData, VillageListLisner {
    @BindView(R.id.spinner_state)
    Spinner spinner_state;

    /*@BindView(R.id.spinner_village)
    Spinner spinner_village;*/

    @BindView(R.id.spinner_block)
    Spinner spinner_block;

    @BindView(R.id.rg_programs)
    RadioGroup radioGroupPrograms;

    @BindView(R.id.btn_pullData)
    Button btn_pullData;

    @BindView(R.id.btn_saveData)
    Button btn_saveData;

    Dialog dialog;
    private String[] states;
    private int selectedState;
    String[] stateCode;
    private String selectedStateName = "MH";
    List selectedVillage = new ArrayList();
    String selectedBlock = "NO BLOCKS";
    String selectedProgram = null;
    Animation animation;
    boolean apiLoadFlag = false;
    boolean internetIsAvailable = false;
    private List<Village> villageList = new ArrayList();
    private List<CRL> CRLList = new ArrayList();
    private List<Student> studentList = new ArrayList();
    private List<Groups> groupsList = new ArrayList();
    List<Village> villageId;
    int groupLoadCount = 0;
    int studLoadCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection();
        setContentView(R.layout.activity_select_program);
        ButterKnife.bind(this);

        //   spinner_village.setEnabled(false);
        spinner_block.setEnabled(false);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        states = getResources().getStringArray(R.array.india_states);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, states);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_state.setAdapter(adapter);
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                apiLoadFlag = false;
                // spinner_village.setVisibility(View.INVISIBLE);
                btn_pullData.setEnabled(false);
                btn_pullData.clearAnimation();
                btn_saveData.setEnabled(false);
                btn_saveData.clearAnimation();
                getVillageStatewise();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        radioGroupPrograms.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                spinner_state.setSelection(0);
                //  spinner_village.setVisibility(View.INVISIBLE);
                btn_pullData.setEnabled(false);
                btn_saveData.setEnabled(false);
                btn_saveData.clearAnimation();
                btn_pullData.clearAnimation();
                if (studentList != null) studentList.clear();
                if (CRLList != null) CRLList.clear();
                if (groupsList != null) groupsList.clear();
                if (villageList != null) villageList.clear();
            }
        });
        ApplicationController.getInstance().setConnectionListener(this);
    }

    private void getVillageStatewise() {
        //  if (ConnectionManager.getConnectionManager().checkConnection(this)) {
        if (internetIsAvailable) {
            int selectedRadioButtonId = radioGroupPrograms.getCheckedRadioButtonId();
            if (selectedRadioButtonId == -1) {
                Toast.makeText(this, "Please Select Program", Toast.LENGTH_SHORT).show();
            } else {
                RadioButton radioButton = findViewById(selectedRadioButtonId);
                selectedProgram = radioButton.getText().toString();
                // Log.d("prathamLog", selectedProgram);
                selectedState = spinner_state.getSelectedItemPosition();
                selectedStateName = states[selectedState];
                if (!states[selectedState].equals("SELECT STATE")) {
                    String url = "";
                    stateCode = getResources().getStringArray(R.array.india_states_shortcode);
                    switch (selectedProgram) {
                        case APIs.HL:
                            url = APIs.HLpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, APIs.HL);
                            break;
                        case APIs.RI:
                            url = APIs.RIpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, APIs.RI);
                            break;
                        case APIs.SC:
                            url = APIs.SCpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, APIs.SC);
                            break;
                        case APIs.PI:
                            url = APIs.PIpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, APIs.PI);
                            break;
                    }
                } else {
                    Toast.makeText(this, "Please Select State", Toast.LENGTH_SHORT).show();
                    spinner_block.setSelection(0);
                    //    spinner_village.setSelection(0);
                    spinner_block.setEnabled(false);
                    //    spinner_village.setEnabled(false);
                }
            }
        } else {

            spinner_state.setSelection(0);
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_pullData)
    public void pullData(View view) {
        //if (ConnectionManager.getConnectionManager().checkConnection(this)) {
        villageId = new ArrayList();
        if (internetIsAvailable) {
            if (!selectedVillage.isEmpty()) {
                for (int j = 0; j < selectedVillage.size(); j++) {
                    for (int i = 0; i < villageList.size(); i++) {
                        if (selectedVillage.get(j).equals(villageList.get(i).getVillageId())) {
                            villageId.add(villageList.get(i));
                        }
                    }
                }
                if (!villageId.isEmpty()) {
                    if (apiLoadFlag) {
                        apiLoadFlag = false;


                        switch (selectedProgram) {
                            case APIs.HL:
                                String url = APIs.HLpullCrlsURL + stateCode[selectedState] + "&programid=1";
                                loadAPI(url, APIs.CRL, APIs.HL);
                                break;
                            case APIs.RI:
                                loadAPI(APIs.RIpullCrlsURL, APIs.CRL, APIs.RI);
                                break;
                            case APIs.SC:
                                loadAPI(APIs.SCpullCrlsURL, APIs.CRL, APIs.SC);
                                break;
                            case APIs.PI:
                                loadAPI(APIs.PIpullCrlsURL, APIs.CRL, APIs.PI);
                                break;

                        }
                    } else {
                        btn_saveData.setEnabled(false);
                        btn_saveData.clearAnimation();
                        btn_pullData.setEnabled(true);
                        // btn_pullData.startAnimation(animation);
                        Toast.makeText(this, "API LOADING FAILED", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {

                Toast.makeText(this, "Please Select Village", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadAPI(final String url, final String type, final String program) {
        showDialoginApiCalling(program, type);
        AndroidNetworking.get(url).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                String json = response.toString();
                // Toast.makeText(SelectProgram.this, json, Toast.LENGTH_LONG).show();
                apiLoadFlag = true;
                parseJSON(json, type, program);
            }

            @Override
            public void onError(ANError error) {
                spinner_state.setSelection(0);
                if (!internetIsAvailable) {
                    Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SelectProgram.this, "Plaese check internet connection.", Toast.LENGTH_LONG).show();
                }
                // Log.d("error", "" + error);
                dismissShownDialog();
                apiLoadFlag = false;
            }
        });
        //return json;
    }

    private void showDialoginApiCalling(String program, String type) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        dialog.setTitle("Pulling... " + program + " " + type);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissShownDialog() {
        if (dialog != null) dialog.dismiss();
        dialog = null;
    }

    private void parseJSON(String json, String type, String program) {
        switch (type) {
            case village:
                loadVillage(json, program);
                break;
            case APIs.CRL:
                loadCRL(json, program);
                break;
            case APIs.Group:
                loadGroup(json, program);
                break;
            case APIs.Student:
                loadStudent(json, program);
                break;

        }
    }

    private void loadCRL(String json, String program) {
        CRLList.clear();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<CRL>>() {
        }.getType();
        ArrayList<CRL> CrlMoadal = gson.fromJson(json, listType);
        CRLList.addAll(CrlMoadal);
        // Log.d("prathamC", CRLList.toString());
        for (int j = 0; j < villageId.size(); j++) {
            switch (program) {
                case APIs.HL:
                    loadAPI(APIs.HLpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, APIs.HL);
                    break;
                case APIs.RI:
                    loadAPI(APIs.RIpullGroupsURL + villageId, APIs.Group, APIs.RI);
                    break;

                case APIs.SC:
                    loadAPI(APIs.SCpullGroupsURL + villageId, APIs.Group, APIs.SC);
                    break;
                case APIs.PI:
                    loadAPI(APIs.PIpullGroupsURL + villageId, APIs.Group, APIs.PI);
                    break;

            }
        }
    }

    private void loadStudent(String json, String program) {
        studLoadCount++;
        // studentList.clear();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Student>>() {
        }.getType();
        ArrayList<Student> studentMoadal = gson.fromJson(json, listType);

        studentList.addAll(studentMoadal);
        btn_saveData.setEnabled(true);
        btn_saveData.startAnimation(animation);
        btn_pullData.setEnabled(false);
        btn_pullData.clearAnimation();
        if (studLoadCount == villageId.size()) {
            dismissShownDialog();
        }
        //  Log.d("prathamS", studentList.toString());
    }

    private void loadGroup(String json, String program) {
        //  groupsList.clear();
        groupLoadCount++;
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Groups>>() {
        }.getType();
        ArrayList<Groups> groupsMoadal = gson.fromJson(json, listType);
        //  Log.d("prathamG", groupsList.toString());
        groupsList.addAll(groupsMoadal);

        if (groupLoadCount == villageId.size()) {
            if (groupsList.isEmpty()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("No Groups Available");
                alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                //alertDialog.setMessage("No Groups Available");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                btn_pullData.clearAnimation();
                btn_pullData.setEnabled(false);
                dismissShownDialog();
                alertDialog.show();
            } else {
                for (int j = 0; j < villageId.size(); j++) {
                    switch (program) {
                        case APIs.HL:
                            loadAPI(APIs.HLpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, APIs.HL);
                            break;
                        case APIs.RI:
                            loadAPI(APIs.RIpullStudentsURL + villageId, APIs.Student, APIs.RI);
                            break;

                        case APIs.SC:
                            loadAPI(APIs.SCpullStudentsURL + villageId, APIs.Student, APIs.SC);
                            break;
                        case APIs.PI:
                            loadAPI(APIs.PIpullStudentsURL + villageId, APIs.Student, APIs.PI);
                            break;
                    }
                }
            }
        }
    }

    private void loadVillage(String json, String program) {

        spinner_block.setEnabled(true);
        // spinner_village.setEnabled(false);
        // spinner_village.setSelection(0);


        villageList.clear();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Village>>() {
        }.getType();
        ArrayList<Village> modalClassesList = gson.fromJson(json, listType);
        villageList.addAll(modalClassesList);
        ArrayList blockNames = new ArrayList();
        LinkedHashSet hs = new LinkedHashSet();
        if (villageList.size() == 0) {
            blockNames.add("NO BLOCK");
        } else {
            blockNames.add("SELECT BLOCK");
            for (int i = 0; i < villageList.size(); i++) {
                blockNames.add(villageList.get(i).getBlock());
            }
        }
        //remove Duplicate
        hs.addAll(blockNames);
        blockNames.clear();
        blockNames.addAll(hs);
        ArrayAdapter blockAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, blockNames);
        spinner_block.setAdapter(blockAdapter);
        spinner_block.setVisibility(spinner_block.VISIBLE);
        spinner_block.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBlock = parent.getItemAtPosition(position).toString();
                if ((!selectedBlock.equals("NO BLOCK")) && (!selectedBlock.equals("SELECT BLOCK"))) {
                    if (apiLoadFlag) {

                        //  spinner_village.setEnabled(true);
                        btn_pullData.setEnabled(false);
                        btn_pullData.clearAnimation();

                        ArrayList<Village> villageName = new ArrayList();
                        //  spinner_village.setVisibility(View.VISIBLE);
                        //villageName.add("SELECT VILLAGE");
                        for (int i = 0; i < villageList.size(); i++) {
                            if (selectedBlock.equals(villageList.get(i).getBlock())) {
                                villageName.add(villageList.get(i));
                            }
                        }

                        //   ArrayAdapter villageAdapter = new ArrayAdapter(SelectProgram.this, android.R.layout.simple_spinner_dropdown_item, villageName);
                        /*spinner_village.setAdapter(villageAdapter);*/
                        groupsList.clear();
                        studentList.clear();
                        groupLoadCount = 0;
                        studLoadCount = 0;
                        SelectVillageDialog selectVillageDialog = new SelectVillageDialog(SelectProgram.this, villageName);
                        selectVillageDialog.show();
                        dismissShownDialog();
                    }
                } else {
                    // spinner_village.setSelection(0);
                    //  spinner_village.setEnabled(false);
                    btn_pullData.setEnabled(false);
                    btn_pullData.clearAnimation();
                    btn_saveData.setEnabled(false);
                    btn_saveData.clearAnimation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Log.d("prathamV", villageList.toString());
        dismissShownDialog();
    }


    @OnClick(R.id.btn_saveData)
    public void saveData() {
        btn_saveData.clearAnimation();
        btn_saveData.setEnabled(false);
        AppDatabase.getDatabaseInstance(this).getGroupDao().deleteAllGroups();
        AppDatabase.getDatabaseInstance(this).getStudentDao().deleteAllStudents();
        AppDatabase.getDatabaseInstance(this).getVillageDao().deleteAllVillages();
        AppDatabase.getDatabaseInstance(this).getCRLdao().deleteAllCRLs();

        if (groupsList.isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("No Groups Available");
            alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
            //alertDialog.setMessage("No Groups Available");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();

        } else {
            new SaveDataTask(SelectProgram.this, SelectProgram.this, CRLList, studentList, groupsList, villageId).execute();
        }

    }

    private void saveDataToSharedPreference() {
        SharedPreferences sharedPref = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("program", selectedProgram);
        editor.putString("state", selectedStateName);
        //todo Shared preferences
        editor.putString("village", selectedVillage.get(0).toString());
        editor.commit();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectionReceiver.isConnected();
        if (!isConnected) {
            internetIsAvailable = false;
        } else {
            internetIsAvailable = true;
        }
    }

    @Override
    public void onDataSaved() {
        saveDataToSharedPreference();
        finish();
    }

    @Override
    public void getSelectedVillage(List selectdVillageList) {
        selectedVillage.clear();
        selectedVillage.addAll(selectdVillageList);
        if (selectedVillage.isEmpty()) {
            btn_saveData.setEnabled(false);
            btn_saveData.clearAnimation();
            btn_pullData.setEnabled(false);
            btn_pullData.clearAnimation();
        } else {
            btn_pullData.setEnabled(true);
            btn_pullData.startAnimation(animation);
        }
    }
}
