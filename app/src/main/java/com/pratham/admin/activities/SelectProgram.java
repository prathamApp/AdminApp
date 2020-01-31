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
import android.util.Log;
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

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.async.NetworkCalls;
import com.pratham.admin.async.SaveDataTask;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.NetworkCallListener;
import com.pratham.admin.interfaces.NetworkCallListnerSelectProgram;
import com.pratham.admin.interfaces.OnSavedData;
import com.pratham.admin.interfaces.VillageListLisner;
import com.pratham.admin.modalclasses.Aser;
import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.Completion;
import com.pratham.admin.modalclasses.Course;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.Utility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.admin.util.APIs.DSP;
import static com.pratham.admin.util.APIs.DSPpullAserURL;
import static com.pratham.admin.util.APIs.ECE;
import static com.pratham.admin.util.APIs.ECEpullAserURL;
import static com.pratham.admin.util.APIs.GP;
import static com.pratham.admin.util.APIs.GPpullAserURL;
import static com.pratham.admin.util.APIs.HG;
import static com.pratham.admin.util.APIs.HGpullAserURL;
import static com.pratham.admin.util.APIs.HL;
import static com.pratham.admin.util.APIs.HLpullAserURL;
import static com.pratham.admin.util.APIs.KGBpullAserURL;
import static com.pratham.admin.util.APIs.PI;
import static com.pratham.admin.util.APIs.PIpullAserURL;
import static com.pratham.admin.util.APIs.PullCoaches;
import static com.pratham.admin.util.APIs.PullCourses;
import static com.pratham.admin.util.APIs.PullHLCourseCommunity;
import static com.pratham.admin.util.APIs.PullHLCourseCompletion;
import static com.pratham.admin.util.APIs.RI;
import static com.pratham.admin.util.APIs.RIM;
import static com.pratham.admin.util.APIs.RIMpullAserURL;
import static com.pratham.admin.util.APIs.RIpullAserURL;
import static com.pratham.admin.util.APIs.SC;
import static com.pratham.admin.util.APIs.SCpullAserURL;
import static com.pratham.admin.util.APIs.UPpullAserURL;
import static com.pratham.admin.util.APIs.village;

public class SelectProgram extends BaseActivity implements ConnectionReceiverListener, OnSavedData, VillageListLisner, NetworkCallListnerSelectProgram, NetworkCallListener {

    @BindView(R.id.spinner_state)
    Spinner spinner_state;

    @BindView(R.id.spinner_block)
    Spinner spinner_block;

    @BindView(R.id.rg_programs)
    RadioGroup radioGroupPrograms;

    @BindView(R.id.btn_pullData)
    Button btn_pullData;

    @BindView(R.id.btn_saveData)
    Button btn_saveData;

    Dialog dialog;
    String[] stateCode;
    List<String> selectedVillage = new ArrayList();
    String selectedBlock = "NO BLOCKS";
    String selectedProgram = null;
    Animation animation;
    boolean apiLoadFlag = false;
    boolean internetIsAvailable = false;
    boolean errorDetected = false;
    List<Village> villageId;
    int groupLoadCount = 0;
    int studLoadCount = 0;
    int countAser = 0;
    private String[] states;
    private int selectedState;
    private String selectedStateName = "MH";
    private List<Village> villageList = new ArrayList();
    private List<CRL> CRLList = new ArrayList();
    private List<Student> studentList = new ArrayList();
    private List<Aser> aserList = new ArrayList();
    private List<Groups> groupsList = new ArrayList();
    private List<Course> CourseList = new ArrayList();
    private List<Community> CommunityList = new ArrayList();
    private List<Completion> CompletionList = new ArrayList();
    private List<Coach> CoachList = new ArrayList();

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
                        case APIs.UP:
                            //todo urban
                            url = APIs.UPpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, APIs.UP);
                            break;
                        case APIs.KGBV:
                            url = APIs.KGBVpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, APIs.KGBV);
                            break;
                        case APIs.ECE:
                            url = APIs.ECEpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, APIs.ECE);
                            break;
                        case RI:
                            url = APIs.RIpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, RI);
                            break;
                        case SC:
                            url = APIs.SCpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, SC);
                            break;
                        case PI:
                            url = APIs.PIpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, PI);
                            break;
                        case HG:
                            url = APIs.HGpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, HG);
                            break;
                        case GP:
                            url = APIs.GPpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, GP);
                            break;
                        case DSP:
                            url = APIs.DSPpullVillagesURL + stateCode[selectedState];
                            loadAPI(url, village, DSP);
                            break;
                        case RIM:
                            url = APIs.RIMpullVillagesURL + stateCode[selectedState];
                            Log.e("RIM",url);
                            loadAPI(url, village, RIM);
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
                                String url = APIs.HLpullCrlsURL + stateCode[selectedState]; /*+ "&programid=1";*/
                                loadAPI(url, APIs.CRL, APIs.HL);
                                break;
                            case APIs.UP:
                                //todo urban
                                String url11 = APIs.UPpullCrlsURL + stateCode[selectedState]; /*+ "&programid=1";*/
                                loadAPI(url11, APIs.CRL, APIs.UP);
                                break;
                            case APIs.KGBV:
                                String murl5 = APIs.KGBVpullCrlsURL + stateCode[selectedState]; /*+ "&programid=1";*/
                                loadAPI(murl5, APIs.CRL, APIs.KGBV);
                                break;
                            case APIs.ECE:
                                String url5 = APIs.ECEpullCrlsURL + stateCode[selectedState];/* + "&programid=8";*/
                                loadAPI(url5, APIs.CRL, APIs.ECE);
                                break;
                            case RI:
                                String url2 = APIs.RIpullCrlsURL + stateCode[selectedState] + "&programid=2";
                                loadAPI(url2, APIs.CRL, RI);
                                break;
                            case SC:
                                String url3 = APIs.SCpullCrlsURL + stateCode[selectedState]; /*+ "&programid=3";*/
                                loadAPI(url3, APIs.CRL, SC);
                                break;
                            case PI:
                                String url4 = APIs.PIpullCrlsURL + stateCode[selectedState] + "&programid=4";
                                loadAPI(url4, APIs.CRL, PI);
                                break;
                            case APIs.HG:
                                String url13 = APIs.HGpullCrlsURL + stateCode[selectedState]; /*+ "&programid=1";*/
                                loadAPI(url13, APIs.CRL, APIs.HG);
                                break;
                            case APIs.GP:
                                String url14 = APIs.GPpullCrlsURL + stateCode[selectedState]; /*+ "&programid=1";*/
                                loadAPI(url14, APIs.CRL, APIs.GP);
                                break;
                            case APIs.DSP:
                                String url15 = APIs.DSPpullCrlsURL + stateCode[selectedState]; /*+ "&programid=1";*/
                                loadAPI(url15, APIs.CRL, APIs.DSP);
                                break;
                            case RIM:
                                String url16 = APIs.RIMpullCrlsURL + stateCode[selectedState] + "&programid=11";
                                loadAPI(url16, APIs.CRL, RIM);
                                break;

                        }
                        btn_saveData.setEnabled(true);
                        btn_pullData.clearAnimation();


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


    private void validateData() {
        if (spinner_state.getSelectedItemPosition() > 0 && spinner_block.getSelectedItemPosition() > 0) {
            // all good
            btn_saveData.setEnabled(true);
            btn_saveData.startAnimation(animation);
            btn_pullData.setEnabled(false);
            btn_pullData.clearAnimation();
        } else {
            // something went wrong
            Toast.makeText(this, "Something went wrong !!!", Toast.LENGTH_SHORT).show();
        }
    }


    public void loadAPI(final String url, final String type, final String program) {
        showDialoginApiCalling(program, type);
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithProgram(this, url, "loadAPI", type, program);
      /*  AndroidNetworking.get(url).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                String json = response.toString();
                // Toast.makeText(SelectProgram.this, json, Toast.LENGTH_LONG).show();
                apiLoadFlag = true;
                parseJSON(json, type, program);
            }

            @Override
            public void onError(ANError error) {
                errorDetected = true;
                if (type.equals("village")) {
                    spinner_state.setSelection(0);
                    dismissShownDialog();
                }
                *//*  dismissShownDialog();*//*
                parseJSON("[]", type, program);
                if (!internetIsAvailable) {
                    Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SelectProgram.this, "Load API Failed." + type, Toast.LENGTH_LONG).show();
                }
                // Log.d("error", "" + error);

                apiLoadFlag = false;
            }
        });*/
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
                case APIs.UP:
                    loadAPI(APIs.UPpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, APIs.UP);
                    break;
                case APIs.KGBV:
                    loadAPI(APIs.KGBVpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, APIs.KGBV);
                    break;
                case APIs.ECE:
                    loadAPI(APIs.ECEpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, APIs.ECE);
                    break;
                case RI:
                    loadAPI(APIs.RIpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, RI);
                    break;
                case SC:
                    loadAPI(APIs.SCpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, SC);
                    break;
                case PI:
                    loadAPI(APIs.PIpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, PI);
                    break;
                case HG:
                    loadAPI(APIs.HGpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, HG);
                    break;
                case APIs.GP:
                    loadAPI(APIs.GPpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, APIs.GP);
                    break;
                case APIs.DSP:
                    loadAPI(APIs.DSPpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, APIs.DSP);
                    break;
                case RIM:
                    loadAPI(APIs.RIMpullGroupsURL + villageId.get(j).getVillageId(), APIs.Group, RIM);
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
        if (studLoadCount == villageId.size()) {
            switch (program) {
                case APIs.HL:
                    loadAserData(HLpullAserURL);
                    break;
                case APIs.UP:
                    loadAserData(UPpullAserURL);
                    break;
                case APIs.KGBV:
                    loadAserData(KGBpullAserURL);
                    break;
                case APIs.ECE:
                    loadAserData(ECEpullAserURL);
                    break;
                case RI:
                    loadAserData(RIpullAserURL);
                    break;
                case SC:
                    loadAserData(SCpullAserURL);
                    break;
                case PI:
                    loadAserData(PIpullAserURL);
                    break;
                case HG:
                    loadAserData(HGpullAserURL);
                    break;
                case APIs.GP:
                    loadAserData(GPpullAserURL);
                    break;
                case APIs.DSP:
                    loadAserData(DSPpullAserURL);
                    break;
                case RIM:
                    loadAserData(RIMpullAserURL);
                    break;
                default:
                    dismissShownDialog();
                    pullStorePersons();
                    // mayur cha code
                    formsAPI();
                    break;
            }

        }
    }

    private void loadAserData(String url) {
        aserList.clear();
        countAser = 0;
        for (String id : selectedVillage) {
            downloadAserData(/*APIs.HGpullAserURL */url + id);
        }
    }

    private void downloadAserData(String url) {
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, url, "downloadAserData");
       /* AndroidNetworking.get(url).setPriority(Priority.LOW).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                // do anything with response
                countAser++;
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Aser>>() {
                }.getType();
                ArrayList<Aser> AserMoadal = gson.fromJson(response.toString(), listType);
                aserList.addAll(AserMoadal);
                if (countAser == villageId.size()) {
                    dismissShownDialog();
                    pullStorePersons();
                    // mayur cha code
                    formsAPI();
                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Toast.makeText(SelectProgram.this, "Failed to load store person", Toast.LENGTH_SHORT).show();
                dismissShownDialog();
                pullStorePersons();
                // mayur cha code
                formsAPI();
            }
        });*/
    }

    private void pullStorePersons() {
        String url = APIs.storePersonAPI + stateCode[selectedState];
        NetworkCalls.getNetworkCallsInstance(this).getRequest(this, url, "loading store person", "storeperson");

       /* final ProgressDialog progressDialog = new ProgressDialog(SelectProgram.this);
        progressDialog.setMessage("loading store person");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AndroidNetworking.get(url).setPriority(Priority.LOW).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                // do anything with response
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<CRL>>() {
                }.getType();
                ArrayList<CRL> CrlMoadal = gson.fromJson(response.toString(), listType);
                CRLList.addAll(CrlMoadal);
                progressDialog.dismiss();

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Toast.makeText(SelectProgram.this, "Failed to load store person", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
*/
    }


    private void loadGroup(String json, String program) {
        //  groupsList.clear();
        groupLoadCount++;
        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Groups>>() {
            }.getType();
            ArrayList<Groups> groupsMoadal = gson.fromJson(json, listType);
            //  Log.d("prathamG", groupsList.toString());
            groupsList.addAll(groupsMoadal);
        }
        if (groupLoadCount == villageId.size()) {

            for (int j = 0; j < villageId.size(); j++) {
                switch (program) {
                    case APIs.HL:
                        loadAPI(APIs.HLpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, APIs.HL);
                        break;
                    case APIs.UP:
                        loadAPI(APIs.UPpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, APIs.UP);
                        break;
                    case APIs.KGBV:
                        loadAPI(APIs.KGBVpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, APIs.KGBV);
                        break;
                    case APIs.ECE:
                        loadAPI(APIs.ECEpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, APIs.ECE);
                        break;
                    case RI:
                        loadAPI(APIs.RIpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, RI);
                        break;

                    case SC:
                        loadAPI(APIs.SCpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, SC);
                        break;
                    case PI:
                        loadAPI(APIs.PIpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, PI);
                        break;

                    case HG:
                        loadAPI(APIs.HGpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, HG);
                        break;
                    case APIs.GP:
                        loadAPI(APIs.GPpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, APIs.GP);
                        break;
                    case APIs.DSP:
                        loadAPI(APIs.DSPpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, APIs.DSP);
                        break;
                    case RIM:
                        loadAPI(APIs.RIMpullStudentsURL + villageId.get(j).getVillageId(), APIs.Student, RIM);
                        break;
                }
            }
        }
    }

    private void loadVillage(String json, String program) {

        spinner_block.setEnabled(true);
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

                        btn_pullData.setEnabled(false);
                        btn_pullData.clearAnimation();

                        ArrayList<Village> villageName = new ArrayList();
                        for (int i = 0; i < villageList.size(); i++) {
                            if (selectedBlock.equals(villageList.get(i).getBlock())) {
                                villageName.add(villageList.get(i));
                            }
                        }

                        groupsList.clear();
                        studentList.clear();
                        groupLoadCount = 0;
                        studLoadCount = 0;
                        countAser = 0;
                        SelectVillageDialog selectVillageDialog = new SelectVillageDialog(SelectProgram.this, villageName);
                        selectVillageDialog.show();
                    }
                } else {
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
        dismissShownDialog();
    }


    @OnClick(R.id.btn_saveData)
    public void saveData() {
        btn_saveData.clearAnimation();
        btn_saveData.setEnabled(false);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SelectProgram.this, android.R.style.Theme_Material_Light_Dialog);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setTitle("Data Preview");
        dialogBuilder.setMessage("CRLList : " + CRLList.size() + "\nstudentList : " + studentList.size() + "\ngroupsList : " + groupsList.size() + "\nCourseList : " + CourseList.size() + "\nCoachList : " + CoachList.size() + "\nCommunityList : " + CommunityList.size() + "\nCompletionList : " + CompletionList.size() + "\nAserList : " + aserList.size());
        if (CRLList.size() > 0) {
            dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    try {
                        new SaveDataTask(SelectProgram.this, SelectProgram.this, CRLList, studentList, groupsList, villageId, CourseList, CoachList, CommunityList, CompletionList, aserList).execute();
                        //save all villages if want only selected then uncomment above line
                        // new SaveDataTask(SelectProgram.this, SelectProgram.this, CRLList, studentList, groupsList, villageList, CourseList, CoachList, CommunityList, CompletionList, aserList).execute();
                    } catch (Exception e) {
                        Modal_Log log = new Modal_Log();
                        log.setCurrentDateTime(new Utility().GetCurrentDate());
                        log.setErrorType("ERROR");
                        log.setExceptionMessage(e.getMessage());
                        log.setExceptionStackTrace(e.getStackTrace().toString());
                        log.setMethodName("SelectProgram" + "_" + "SaveData");
                        log.setDeviceId("");
                        AppDatabase.getDatabaseInstance(SelectProgram.this).getLogDao().insertLog(log);
                        BackupDatabase.backup(SelectProgram.this);

                        e.printStackTrace();
                    }
                }
            });
        }
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                btn_pullData.startAnimation(animation);
                btn_saveData.setEnabled(true);
                CRLList.clear();
                studentList.clear();
                groupsList.clear();
                villageId.clear();
                CourseList.clear();
                CoachList.clear();
                CommunityList.clear();
                CompletionList.clear();
                spinner_block.setSelection(0);
                spinner_state.setSelection(0);
                aserList.clear();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void saveDataToSharedPreference() {
        SharedPreferences sharedPref = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("program", selectedProgram);
        editor.putString("state", selectedStateName);
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


    private void formsAPI() {
        CompletionList.clear();
        CommunityList.clear();
        CoachList.clear();

        try {
            // Pull Courses
            pullCourses();
        } catch (Exception e) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("SaveDataTast" + "_" + "formsAPI");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(SelectProgram.this).getLogDao().insertLog(log);
            BackupDatabase.backup(SelectProgram.this);

            e.printStackTrace();
        }
        try {
            // Pull Coaches
            for (Village village : villageId) {
                pullCoaches(village.getVillageId());
            }
        } catch (Exception e) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("SaveDataTast" + "_" + "formsAPI");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(SelectProgram.this).getLogDao().insertLog(log);
            BackupDatabase.backup(SelectProgram.this);

            e.printStackTrace();
        }

        try {
            // HLCourseCommunity
            for (Village village : villageId) {
                pullHLCourseCommunity(village.getVillageId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("SaveDataTast" + "_" + "formsAPI");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(SelectProgram.this).getLogDao().insertLog(log);
            BackupDatabase.backup(SelectProgram.this);

        }
        try {
            // HLCourseCompletion
            for (Village village : villageId) {
                pullHLCourseCompletion(village.getVillageId());
            }
        } catch (Exception e) {
            e.printStackTrace();

            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("SaveDataTast" + "_" + "formsAPI");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(SelectProgram.this).getLogDao().insertLog(log);
            BackupDatabase.backup(SelectProgram.this);

        }


        // validate data (if all good then enable btn_save)
        validateData();
    }

    private void pullCoaches(String vID) {
        String couchUrl = PullCoaches;
        switch (selectedProgram) {
            case HL:
                showDialoginApiCalling(HL, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=1";
                break;
            case APIs.UP:
                //todo urban
                showDialoginApiCalling(APIs.UP, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=1";
                break;
            case APIs.KGBV:
                showDialoginApiCalling(APIs.KGBV, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=5";
                break;
            case RI:
                showDialoginApiCalling(RI, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=2";
                break;
            case ECE:
                showDialoginApiCalling(RI, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=8";
                break;
            case SC:
                showDialoginApiCalling(SC, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=3";
                break;
            case PI:
                showDialoginApiCalling(PI, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=4";
                break;
            case GP:
                showDialoginApiCalling(GP, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=14";
                break;
            case HG:
                showDialoginApiCalling(HG, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=13";
                break;
            case DSP:
                showDialoginApiCalling(DSP, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=22";
                break;
            case RIM:
                showDialoginApiCalling(RIM, "Pulling Coaches !!!");
                couchUrl = couchUrl + "villageid=" + vID + "&programid=11";
                break;
        }
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, couchUrl, "couchUrl");
       /* AndroidNetworking.get(couchUrl).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                String json = response.toString();
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<ArrayList<Coach>>() {
                    }.getType();
                    ArrayList<Coach> modalCoachList = gson.fromJson(json, listType);
                    CoachList.addAll(modalCoachList);
                } catch (JsonSyntaxException e) {

                    Modal_Log log = new Modal_Log();
                    log.setCurrentDateTime(new Utility().GetCurrentDate());
                    log.setErrorType("ERROR");
                    log.setExceptionMessage(e.getMessage());
                    log.setExceptionStackTrace(e.getStackTrace().toString());
                    log.setMethodName("SelectProgram" + "_" + "coachUrl");
                    log.setDeviceId("");
                    AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                    BackupDatabase.backup(ApplicationController.getInstance());

                    e.printStackTrace();
                }
                dismissShownDialog();
            }

            @Override
            public void onError(ANError error) {
                errorDetected = true;
                if (!internetIsAvailable) {
                    Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SelectProgram.this, "Pull Coaches Failed.", Toast.LENGTH_LONG).show();
                }
                dismissShownDialog();
                apiLoadFlag = false;
            }
        });*/
    }


    private void pullHLCourseCommunity(String vID) {
        String pullHLCourseCommunityUrl = PullHLCourseCommunity + "villageid=" + vID + "&programid=";
        switch (selectedProgram) {
            case HL:
                showDialoginApiCalling(HL, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 1;
                break;
            case APIs.UP:
                showDialoginApiCalling(APIs.UP, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 1;
                break;
            case APIs.KGBV:
                showDialoginApiCalling(APIs.KGBV, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 5;
                break;
            case RI:
                showDialoginApiCalling(RI, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 2;
                break;
            case SC:
                showDialoginApiCalling(SC, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 3;
                break;
            case PI:
                showDialoginApiCalling(PI, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 4;
                break;
            case ECE:
                showDialoginApiCalling(PI, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 8;
                break;
            case GP:
                showDialoginApiCalling(GP, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 14;
                break;
            case HG:
                showDialoginApiCalling(HG, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 13;
                break;
            case DSP:
                showDialoginApiCalling(DSP, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 22;
                break;
            case RIM:
                showDialoginApiCalling(RIM, "Pulling Course Community !!!");
                pullHLCourseCommunityUrl = pullHLCourseCommunityUrl + 11;
                break;
        }

        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, pullHLCourseCommunityUrl, "pullHLCourseCommunityUrl");
      /*  AndroidNetworking.get(pullHLCourseCommunityUrl).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                String json = response.toString();
                Gson gson = new Gson();
                try {
                    Type listType = new TypeToken<ArrayList<Community>>() {
                    }.getType();
                    ArrayList<Community> modalCommunityList = gson.fromJson(json, listType);
                    CommunityList.addAll(modalCommunityList);
                } catch (JsonSyntaxException e) {
                    Modal_Log log = new Modal_Log();
                    log.setCurrentDateTime(new Utility().GetCurrentDate());
                    log.setErrorType("ERROR");
                    log.setExceptionMessage(e.getMessage());
                    log.setExceptionStackTrace(e.getStackTrace().toString());
                    log.setMethodName("SelectProgram" + "_" + "HLCourseCommunityUrl");
                    log.setDeviceId("");
                    AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                    BackupDatabase.backup(ApplicationController.getInstance());

                    e.printStackTrace();
                }
                dismissShownDialog();
            }

            @Override
            public void onError(ANError error) {
                errorDetected = true;
//                spinner_state.setSelection(0);
                if (!internetIsAvailable) {
                    Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SelectProgram.this, "PullCourseCommunity Failed.", Toast.LENGTH_LONG).show();
                }
                dismissShownDialog();
                apiLoadFlag = false;
            }
        });*/
    }

    private void pullCourses() {
        switch (selectedProgram) {
            case HL:
                showDialoginApiCalling(HL, "Pulling Courses !!!");
                break;
            case APIs.UP:
                showDialoginApiCalling(APIs.UP, "Pulling Courses !!!");
                break;
            case APIs.KGBV:
                showDialoginApiCalling(APIs.KGBV, "Pulling Courses !!!");
                break;
            case RI:
                showDialoginApiCalling(RI, "Pulling Courses !!!");
                break;
            case SC:
                showDialoginApiCalling(SC, "Pulling Courses !!!");
                break;
            case PI:
                showDialoginApiCalling(PI, "Pulling Courses !!!");
                break;
            case GP:
                showDialoginApiCalling(GP, "Pulling Courses !!!");
                break;
            case HG:
                showDialoginApiCalling(HG, "Pulling Courses !!!");
                break;
            case DSP:
                showDialoginApiCalling(DSP, "Pulling Courses !!!");
                break;
            case RIM:
                showDialoginApiCalling(RIM, "Pulling Courses !!!");
                break;
        }
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, PullCourses, "PullCourses");
      /*  AndroidNetworking.get(PullCourses).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                String json = response.toString();
                try {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Course>>() {
                    }.getType();
                    ArrayList<Course> modalCoursesList = gson.fromJson(json, listType);
                    CourseList.addAll(modalCoursesList);
                } catch (JsonSyntaxException e) {
                    Modal_Log log = new Modal_Log();
                    log.setCurrentDateTime(new Utility().GetCurrentDate());
                    log.setErrorType("ERROR");
                    log.setExceptionMessage(e.getMessage());
                    log.setExceptionStackTrace(e.getStackTrace().toString());
                    log.setMethodName("SelectProgram" + "_" + "PullCourses");
                    log.setDeviceId("");
                    AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                    BackupDatabase.backup(ApplicationController.getInstance());

                    e.printStackTrace();
                }
                dismissShownDialog();
            }

            @Override
            public void onError(ANError error) {
                errorDetected = true;
                if (!internetIsAvailable) {
                    Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SelectProgram.this, "PullCourses Failed.", Toast.LENGTH_LONG).show();
                }
                dismissShownDialog();
                apiLoadFlag = false;
            }
        });*/
    }

    private void pullHLCourseCompletion(String vID) {
        String PullHLCourseCompletionUrl = PullHLCourseCompletion + "villageid=" + vID + "&programid=";
        switch (selectedProgram) {
            case HL:
                showDialoginApiCalling(HL, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 1;
                break;
            case APIs.UP:
                //todo Urban
                showDialoginApiCalling(APIs.UP, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 1;
                break;
            case APIs.KGBV:
                showDialoginApiCalling(APIs.KGBV, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 5;
                break;
            case RI:
                showDialoginApiCalling(RI, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 2;
                break;
            case SC:
                showDialoginApiCalling(SC, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 3;

                break;
            case PI:
                showDialoginApiCalling(PI, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 4;
                break;
            case ECE:
                showDialoginApiCalling(PI, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 8;
                break;
            case GP:
                showDialoginApiCalling(GP, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 14;
                break;
            case HG:
                showDialoginApiCalling(HG, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 13;
                break;
            case DSP:
                showDialoginApiCalling(DSP, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 22;
                break;
            case RIM:
                showDialoginApiCalling(RIM, "Pulling Course Completion !!!");
                PullHLCourseCompletionUrl = PullHLCourseCompletionUrl + 11;
                break;
        }
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, PullHLCourseCompletionUrl, "PullHLCourseCompletionUrl");
     /*   AndroidNetworking.get(PullHLCourseCompletionUrl).build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                String json = response.toString();
                try {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Completion>>() {
                    }.getType();
                    ArrayList<Completion> modalCompletionList = gson.fromJson(json, listType);
                    CompletionList.addAll(modalCompletionList);
                } catch (JsonSyntaxException e) {
                    Modal_Log log = new Modal_Log();
                    log.setCurrentDateTime(new Utility().GetCurrentDate());
                    log.setErrorType("ERROR");
                    log.setExceptionMessage(e.getMessage());
                    log.setExceptionStackTrace(e.getStackTrace().toString());
                    log.setMethodName("SelectProgram" + "_" + "HLCourseCompletion");
                    log.setDeviceId("");
                    AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                    BackupDatabase.backup(ApplicationController.getInstance());

                    e.printStackTrace();
                }
                dismissShownDialog();
            }

            @Override
            public void onError(ANError error) {
                errorDetected = true;
                if (!internetIsAvailable) {
                    Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SelectProgram.this, "PullCourseCompletion Failed.", Toast.LENGTH_LONG).show();
                }
                dismissShownDialog();
                apiLoadFlag = false;
            }
        });*/
    }

    @Override
    public void onResponce(String response, String header, String type, String program) {
        if (header.equals("loadAPI")) {
            String json = response;
            // Toast.makeText(SelectProgram.this, json, Toast.LENGTH_LONG).show();
            apiLoadFlag = true;
            parseJSON(json, type, program);
        }
    }

    @Override
    public void onError(ANError anError, String header, String type, String program) {
        if (header.equals("loadAPI")) {
            errorDetected = true;
            if (type.equals("village")) {
                spinner_state.setSelection(0);
                dismissShownDialog();
            }
            /*  dismissShownDialog();*/
            parseJSON("[]", type, program);
            if (!internetIsAvailable) {
                Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "Load API Failed." + type, Toast.LENGTH_LONG).show();
            }
            // Log.d("error", "" + error);

            apiLoadFlag = false;
        }
    }

    @Override
    public void onResponse(String response, String header) {
        if (header.equals("downloadAserData")) {
            countAser++;
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Aser>>() {
            }.getType();
            ArrayList<Aser> AserMoadal = gson.fromJson(response.toString(), listType);
            aserList.addAll(AserMoadal);
            if (countAser == villageId.size()) {
                dismissShownDialog();
                pullStorePersons();
                // mayur cha code
                formsAPI();
            }
        } else if (header.equals("storeperson")) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<CRL>>() {
            }.getType();
            ArrayList<CRL> CrlMoadal = gson.fromJson(response, listType);
            CRLList.addAll(CrlMoadal);
        } else if (header.equals("couchUrl")) {
            String json = response.toString();
            Gson gson = new Gson();
            try {
                Type listType = new TypeToken<ArrayList<Coach>>() {
                }.getType();
                ArrayList<Coach> modalCoachList = gson.fromJson(json, listType);
                CoachList.addAll(modalCoachList);
            } catch (JsonSyntaxException e) {

                Modal_Log log = new Modal_Log();
                log.setCurrentDateTime(new Utility().GetCurrentDate());
                log.setErrorType("ERROR");
                log.setExceptionMessage(e.getMessage());
                log.setExceptionStackTrace(e.getStackTrace().toString());
                log.setMethodName("SelectProgram" + "_" + "coachUrl");
                log.setDeviceId("");
                AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                BackupDatabase.backup(ApplicationController.getInstance());

                e.printStackTrace();
            }
            dismissShownDialog();
        } else if (header.equals("pullHLCourseCommunityUrl")) {
            String json = response.toString();
            Gson gson = new Gson();
            try {
                Type listType = new TypeToken<ArrayList<Community>>() {
                }.getType();
                ArrayList<Community> modalCommunityList = gson.fromJson(json, listType);
                CommunityList.addAll(modalCommunityList);
            } catch (JsonSyntaxException e) {
                Modal_Log log = new Modal_Log();
                log.setCurrentDateTime(new Utility().GetCurrentDate());
                log.setErrorType("ERROR");
                log.setExceptionMessage(e.getMessage());
                log.setExceptionStackTrace(e.getStackTrace().toString());
                log.setMethodName("SelectProgram" + "_" + "HLCourseCommunityUrl");
                log.setDeviceId("");
                AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                BackupDatabase.backup(ApplicationController.getInstance());

                e.printStackTrace();
            }
            dismissShownDialog();
        } else if (header.equals("PullCourses")) {
            String json = response.toString();
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Course>>() {
                }.getType();
                ArrayList<Course> modalCoursesList = gson.fromJson(json, listType);
                CourseList.addAll(modalCoursesList);
            } catch (JsonSyntaxException e) {
                Modal_Log log = new Modal_Log();
                log.setCurrentDateTime(new Utility().GetCurrentDate());
                log.setErrorType("ERROR");
                log.setExceptionMessage(e.getMessage());
                log.setExceptionStackTrace(e.getStackTrace().toString());
                log.setMethodName("SelectProgram" + "_" + "PullCourses");
                log.setDeviceId("");
                AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                BackupDatabase.backup(ApplicationController.getInstance());

                e.printStackTrace();
            }
            dismissShownDialog();
        } else if (header.equals("PullHLCourseCompletionUrl")) {
            String json = response.toString();
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Completion>>() {
                }.getType();
                ArrayList<Completion> modalCompletionList = gson.fromJson(json, listType);
                CompletionList.addAll(modalCompletionList);
            } catch (JsonSyntaxException e) {
                Modal_Log log = new Modal_Log();
                log.setCurrentDateTime(new Utility().GetCurrentDate());
                log.setErrorType("ERROR");
                log.setExceptionMessage(e.getMessage());
                log.setExceptionStackTrace(e.getStackTrace().toString());
                log.setMethodName("SelectProgram" + "_" + "HLCourseCompletion");
                log.setDeviceId("");
                AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
                BackupDatabase.backup(ApplicationController.getInstance());

                e.printStackTrace();
            }
            dismissShownDialog();
        }
    }

    @Override
    public void onError(ANError anError, String header) {
        if (header.equals("downloadAserData")) {
            // handle error
            Toast.makeText(SelectProgram.this, "Failed to load store person", Toast.LENGTH_SHORT).show();
            dismissShownDialog();
            pullStorePersons();
            // mayur cha code
            formsAPI();
        } else if (header.equals("storeperson")) {
            // handle error
            Toast.makeText(SelectProgram.this, "Failed to load store person", Toast.LENGTH_SHORT).show();
        } else if (header.equals("couchUrl")) {
            errorDetected = true;
            if (!internetIsAvailable) {
                Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "Pull Coaches Failed.", Toast.LENGTH_LONG).show();
            }
            dismissShownDialog();
            apiLoadFlag = false;
        } else if (header.equals("pullHLCourseCommunityUrl")) {
            errorDetected = true;
//                spinner_state.setSelection(0);
            if (!internetIsAvailable) {
                Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "PullCourseCommunity Failed.", Toast.LENGTH_LONG).show();
            }
            dismissShownDialog();
            apiLoadFlag = false;
        } else if (header.equals("PullCourses")) {
            errorDetected = true;
            if (!internetIsAvailable) {
                Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "PullCourses Failed.", Toast.LENGTH_LONG).show();
            }
            dismissShownDialog();
            apiLoadFlag = false;
        } else if (header.equals("PullHLCourseCompletionUrl")) {
            errorDetected = true;
            if (!internetIsAvailable) {
                Toast.makeText(SelectProgram.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "PullCourseCompletion Failed.", Toast.LENGTH_LONG).show();
            }
            dismissShownDialog();
            apiLoadFlag = false;
        }
    }
}