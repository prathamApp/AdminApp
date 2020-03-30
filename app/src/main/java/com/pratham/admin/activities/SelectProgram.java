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
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
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
import com.pratham.admin.modalclasses.ProgramsModal;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.modalclasses.Youth;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.Utility;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pratham.admin.util.APIs.PullCoaches;
import static com.pratham.admin.util.APIs.PullCourses;
import static com.pratham.admin.util.APIs.PullHLCourseCommunity;
import static com.pratham.admin.util.APIs.PullHLCourseCompletion;
import static com.pratham.admin.util.APIs.SERVER_PROGRAMID;
import static com.pratham.admin.util.APIs.SERVER_STATECODE;
import static com.pratham.admin.util.APIs.SERVER_VILLAGE;
import static com.pratham.admin.util.APIs.pullAserURL;
import static com.pratham.admin.util.APIs.pullYouthsServerURL;
import static com.pratham.admin.util.APIs.village;

public class SelectProgram extends BaseActivity implements ConnectionReceiverListener, OnSavedData, VillageListLisner, NetworkCallListnerSelectProgram, NetworkCallListener {

    @BindView(R.id.spinner_program)
    Spinner spinner_program;

    @BindView(R.id.spinner_state)
    Spinner spinner_state;

    @BindView(R.id.spinner_block)
    Spinner spinner_block;

//    @BindView(R.id.rg_programs)
//    RadioGroup radioGroupPrograms;

    @BindView(R.id.btn_pullData)
    Button btn_pullData;

    @BindView(R.id.btn_saveData)
    Button btn_saveData;

    Dialog dialog;
    String[] stateCode;
    List<String> selectedVillage = new ArrayList();
    String selectedBlock = "NO BLOCKS";
    public static String selectedProgramID= null;
    public static String selectedProgramName = null;
    Animation animation;
    boolean apiLoadFlag = false;
    boolean internetIsAvailable = false;
    boolean errorDetected = false;
    List<Village> villageId;
    int groupLoadCount = 0;
    int studLoadCount = 0;
    int countAser = 0;
    int youthLoadCount =0;
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
    private List<Youth> youthList = new ArrayList();
    private ArrayList<ProgramsModal> programsList = new ArrayList<>();
    List<String> prgrms = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection();
        setContentView(R.layout.activity_select_program);
        ButterKnife.bind(this);
        //   spinner_village.setEnabled(false);
        spinner_block.setEnabled(false);
        spinner_state.setEnabled(false);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        states = getResources().getStringArray(R.array.india_states);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, states);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_state.setAdapter(adapter);
        loadprogams();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().setConnectionListener(this);
    }

    private void loadprogams() {
        //NetworkCalls.getNetworkCallsInstance(this).getRequest(this, url, "Loading Programs..", "loading_devises");

        AndroidNetworking.get(APIs.programsAPI)
                .addHeaders("Content-Type", "application/json").build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        programsList.clear();
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ProgramsModal>>() {
                        }.getType();
                        programsList = gson.fromJson(response.toString(), listType);
                        if (programsList != null) {
                            ProgramsModal modalProgram = new ProgramsModal();
                            modalProgram.setProgramId(-1);
                            modalProgram.setProgramName("Select Program");
                            LinkedHashSet hs = new LinkedHashSet(programsList);//to remove redundant values
                            programsList.clear();
                            programsList.addAll(hs);
                            programsList.add(0, modalProgram);
                            //pullDataView.showProgram(prgrmList);
                            showPrograms(programsList);
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    private void showPrograms(final List<ProgramsModal> programsModalList){
        for (ProgramsModal mp : programsList) {
            prgrms.add(mp.getProgramName());
        }
        Toast.makeText(this, "Please Select Program", Toast.LENGTH_SHORT).show();
        ArrayAdapter programAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, prgrms);
        spinner_program.setAdapter(programAdapter);
        spinner_program.setVisibility(View.VISIBLE);

        spinner_program.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                btn_saveData.setEnabled(false);
                selectedProgramID = String.valueOf(programsModalList.get(position).getProgramId());
                selectedProgramName = programsModalList.get(position).getProgramName();

                if(spinner_program.getSelectedItemPosition() > 0)
                loadStates();
                else {
                    spinner_state.setEnabled(false);
                    spinner_state.setSelection(0);
                    spinner_block.setEnabled(false);
                    spinner_block.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadStates(){
        spinner_state.setEnabled(true);
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                apiLoadFlag = false;
                // spinner_village.setVisibility(View.INVISIBLE);
                btn_pullData.setEnabled(false);
                btn_pullData.clearAnimation();
                btn_saveData.setEnabled(false);
                btn_saveData.clearAnimation();
                getVillageStatewise(selectedProgramID, selectedProgramName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getVillageStatewise(String spID, String spName) {
        //  if (ConnectionManager.getConnectionManager().checkConnection(this)) {
        if (internetIsAvailable) {
            selectedState = spinner_state.getSelectedItemPosition();
            selectedStateName = states[selectedState];
            if (!states[selectedState].equals("Select State")) {
                String url;
                stateCode = getResources().getStringArray(R.array.india_states_shortcode);
                url = APIs.pullVillagesServerURL + spID + APIs.SERVER_STATE + stateCode[selectedState];
                loadAPI(url,village,spName);
            } else {
                //Toast.makeText(this, "Please Select State", Toast.LENGTH_SHORT).show();
                spinner_block.setSelection(0);
                //    spinner_village.setSelection(0);
                spinner_block.setEnabled(false);
                //    spinner_village.setEnabled(false);
            }
        }
        else {
            spinner_state.setSelection(0);
            Toast.makeText(this, R.string.noInterntCon, Toast.LENGTH_SHORT).show();
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
                        String crlURL;
                        crlURL = APIs.pullCrlsServerURL + selectedProgramID + APIs.SERVER_STATECODE + stateCode[selectedState];
                        Log.e("pullData",crlURL);
                        loadAPI(crlURL, APIs.CRL, selectedProgramName);
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


    public void loadAPI(final String url, final String type, final String programname) {
        showDialoginApiCalling(programname, type);
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithProgram(this, url, "loadAPI", type, programname);
    }

    private void showDialoginApiCalling(String program, String type) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        dialog.setTitle(getString(R.string.pulling) + program + " " + type);
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
                loadGroup(json, program, selectedProgramName);
                break;
            case APIs.Student:
                loadStudent(json, program);
                break;
        }
    }

    private void loadCRL(String json, String spID) {
        CRLList.clear();

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<CRL>>() {
        }.getType();
        ArrayList<CRL> CrlMoadal = gson.fromJson(json, listType);
        CRLList.addAll(CrlMoadal);
        // Log.d("prathamC", CRLList.toString());

        for (int j = 0; j < villageId.size(); j++) {
            String crlURL;
            crlURL = APIs.pullGroupsServerURL + spID + SERVER_VILLAGE + villageId.get(j).getVillageId();
            loadAPI(crlURL, APIs.Group, selectedProgramName);
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
            loadAserData(pullAserURL+program);
            dismissShownDialog();
            //pullStorePersons();
            // mayur cha code
            //formsAPI();
        }
    }

    private void loadAserData(String url) {
        aserList.clear();
        countAser = 0;
        for (String id : selectedVillage) {
            String loadAserURL = url+ SERVER_VILLAGE +id;
            downloadAserData(loadAserURL);
        }
    }

    private void downloadAserData(String url) {
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, url, "downloadAserData");
    }

    private void pullStorePersons() {
        String url = APIs.storePersonAPI + selectedProgramID + SERVER_STATECODE + stateCode[selectedState];
        NetworkCalls.getNetworkCallsInstance(this).getRequest(this, url, "loading store person", "storeperson");
    }


    private void loadGroup(String json, String spID, String spName) {
        //  groupsList.clear();
        String loadgrpURL;
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
                loadgrpURL = APIs.pullStudentsServerURL + spID + SERVER_VILLAGE + villageId.get(j).getVillageId();
                loadAPI(loadgrpURL, APIs.Student, spName);
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
            blockNames.add("Select Block");
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
                if ((!selectedBlock.equals("NO BLOCK")) && (!selectedBlock.equals("Select Block"))) {
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
        dialogBuilder.setTitle(R.string.dataPreview);
        dialogBuilder.setMessage("CRLList : " + CRLList.size() + "\nstudentList : " + studentList.size() + "\ngroupsList : " + groupsList.size() + "\nCourseList : " + CourseList.size() + "\nCoachList : " + CoachList.size() + "\nCommunityList : " + CommunityList.size() + "\nCompletionList : " + CompletionList.size() + "\nAserList : " + aserList.size() + "\nYouthList : " + youthList.size());
        if (CRLList.size() > 0) {
            dialogBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    try {
                        new SaveDataTask(SelectProgram.this, SelectProgram.this, CRLList, studentList, groupsList, villageId, CourseList, CoachList, CommunityList, CompletionList, aserList, youthList).execute();
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
        dialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
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
                youthList.clear();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void saveDataToSharedPreference() {
        SharedPreferences sharedPref = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("program", selectedProgramName);
        editor.putString("state", selectedStateName);
        editor.putString("village", selectedVillage.get(0).toString());
        editor.putString("programId",selectedProgramID);
        editor.commit();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            internetIsAvailable = false;
            Toast.makeText(this, R.string.noInterntCon, Toast.LENGTH_SHORT).show();
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
        CourseList.clear();
        youthList.clear();

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
        try {
            // Pull Youths
            for (Village village : villageId) {
                pullYouth(village.getVillageId());
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

        // validate data (if all good then enable btn_save)
        validateData();
    }

    private void pullCoaches(String vID) {
        String couchUrl = PullCoaches;
        showDialoginApiCalling(selectedProgramName, "Pulling Coaches !!!");
        couchUrl = couchUrl + SERVER_VILLAGE + vID + SERVER_PROGRAMID + selectedProgramID;

        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, couchUrl, "couchUrl");
    }


    private void pullHLCourseCommunity(String vID) {
        String pullHLCourseCommunityUrl = PullHLCourseCommunity + SERVER_VILLAGE + vID + SERVER_PROGRAMID + selectedProgramID;
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, pullHLCourseCommunityUrl, "pullHLCourseCommunityUrl");
    }

    private void pullCourses() {
        showDialoginApiCalling(selectedProgramName, "Pulling Courses !!!");
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, PullCourses, "PullCourses");
    }

    private void pullHLCourseCompletion(String vID) {
        showDialoginApiCalling(selectedProgramName, "Pulling Course Completion !!!");
        String PullHLCourseCompletionUrl = PullHLCourseCompletion + SERVER_VILLAGE + vID + SERVER_PROGRAMID + selectedProgramID;
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, PullHLCourseCompletionUrl, "PullHLCourseCompletionUrl");
    }

    private void pullYouth(String vID) {
        showDialoginApiCalling(selectedProgramName, "Pulling Youth !!!");
        String PullHLYouthUrl = pullYouthsServerURL + selectedProgramID + SERVER_VILLAGE + vID;
        //String PullHLYouthUrl = "http://swap.prathamcms.org/api/HLYouth/GetHLYouthInfo?ProgramId=1&VillageId=2735";
        NetworkCalls.getNetworkCallsInstance(this).getRequestWithautLoader(this, PullHLYouthUrl, "PullYouth");
    }

    @Override
    public void onResponce(String response, String header, String type, String program) {
        if (header.equals("loadAPI")) {
            String json = response;
            // Toast.makeText(SelectProgram.this, json, Toast.LENGTH_LONG).show();
            apiLoadFlag = true;
            parseJSON(json, type, selectedProgramID);
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
        }else if (header.equals("storeperson")) {
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
        } else if (header.equals("PullYouth")) {
            String json = response.toString();
            Gson gson = new Gson();
            try {
                Type listType = new TypeToken<ArrayList<Youth>>() {
                }.getType();
                Log.e("JSON : ",json);
                ArrayList<Youth> modalYouthList = gson.fromJson(json, listType);
                youthList.addAll(modalYouthList);
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
                Toast.makeText(SelectProgram.this, R.string.noInterntCon, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "Pull Coaches Failed.", Toast.LENGTH_LONG).show();
            }
            dismissShownDialog();
            apiLoadFlag = false;
        } else if (header.equals("pullHLCourseCommunityUrl")) {
            errorDetected = true;
//                spinner_state.setSelection(0);
            if (!internetIsAvailable) {
                Toast.makeText(SelectProgram.this, R.string.noInterntCon, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "PullCourseCommunity Failed.", Toast.LENGTH_LONG).show();
            }
            dismissShownDialog();
            apiLoadFlag = false;
        } else if (header.equals("PullCourses")) {
            errorDetected = true;
            if (!internetIsAvailable) {
                Toast.makeText(SelectProgram.this, R.string.noInterntCon, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "PullCourses Failed.", Toast.LENGTH_LONG).show();
            }
            dismissShownDialog();
            apiLoadFlag = false;
        } else if (header.equals("PullHLCourseCompletionUrl")) {
            errorDetected = true;
            if (!internetIsAvailable) {
                Toast.makeText(SelectProgram.this, R.string.noInterntCon, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "PullCourseCompletion Failed.", Toast.LENGTH_LONG).show();
            }
            dismissShownDialog();
            apiLoadFlag = false;
        }  else if (header.equals("youthUrl")) {
            errorDetected = true;
            if (!internetIsAvailable) {
                Toast.makeText(SelectProgram.this, R.string.noInterntCon, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SelectProgram.this, "Pull Youth Failed.", Toast.LENGTH_LONG).show();
            }
            dismissShownDialog();
            apiLoadFlag = false;
        }
    }
}