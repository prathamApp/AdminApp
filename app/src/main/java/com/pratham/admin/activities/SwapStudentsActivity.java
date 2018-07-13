package com.pratham.admin.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.adapters.LeftSideAdapter;
import com.pratham.admin.adapters.RightSideAdapter;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.interfaces.OnCheckBoxSelectedItem;
import com.pratham.admin.interfaces.OnlineChanges;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.TempStudent;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.APIs;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.CustomGroup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;

public class SwapStudentsActivity extends AppCompatActivity implements OnCheckBoxSelectedItem, ConnectionReceiverListener, OnlineChanges {
    @BindView(R.id.swapStudParentLayout)
    LinearLayout swapStudParentLayout;
    @BindView(R.id.leftSide)
    RelativeLayout leftSideLayout;

    @BindView(R.id.rightSide)
    RelativeLayout rightSideLayout;


    @BindView(R.id.recyclerView_leftSide)
    RecyclerView recyclerView_leftSide;

    @BindView(R.id.recyclerView_rightSide)
    RecyclerView recyclerView_rightSide;

    @BindView(R.id.spinner_group_left)
    Spinner spinner_group_left;

    @BindView(R.id.spinner_student_rightSide)
    Spinner spinner_student_rightSide;

    @BindView(R.id.spinner_village)
    Spinner spinner_village;

    @BindView(R.id.tv_selProg)
    TextView tv_programName;

    @BindView(R.id.tv_selState)
    TextView stateName;

    @BindView(R.id.tv_selVillage)
    TextView villageName;

    @BindView(R.id.btn_swap_and_push)
    Button btn_swap_and_push;

    CustomDialogShowOnlineChanges customDialogShowOnlineChanges;
    List<Student> leftStudentList = new ArrayList<>();
    List<Student> rightStudentList = new ArrayList<>();
    List<Student> swappedStudents = new ArrayList<>();
    List<Student> swappedRightSideStudents = new ArrayList<>();
    List<Village> villageList = new ArrayList<>();
    List<Student> tempAllStudent;
    LeftSideAdapter leftSideAdapter;
    RightSideAdapter rightSideAdapter;
    List<Groups> studGroupList;
    List<Student> tempStorageList;
    ArrayList groupNameList;
    Snackbar snackbar;
    String idLeftGroupSpinner = "$$$L";
    String idRightGroupSpinner = "$$$R";
    String nameLeftGroupSpinner = "";
    String nameRightGroupSpinner = "";
    String selectedGroupNameRightSpinner;
    String selectedGroupNameLeftSpinner;
    String program;
    boolean DataChangedFlag = false;
    boolean internetIsAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection();
        setContentView(R.layout.activity_swap_students);
        ButterKnife.bind(this);
        SharedPreferences preferences = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        program = preferences.getString("program", "null");
        String state = preferences.getString("state", "null");
        String village = preferences.getString("village", "null");
        stateName.setText(state);
        villageName.setText(village);
        tv_programName.setText(program);
        setCRLActionBar();

        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        setVillagesToSpinner();
        tempAllStudent = AppDatabase.getDatabaseInstance(this).getStudentDao().getAllStudents();
        studGroupList = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();
        groupNameList = new ArrayList();


    }

    private void setVillagesToSpinner() {
        List VillageName = new ArrayList();
        if (!villageList.isEmpty()) {
            for (int j = 0; j < villageList.size(); j++) {
                VillageName.add(villageList.get(j).getVillageName() + "  (ID:: " + villageList.get(j).getVillageId()+")");
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(SwapStudentsActivity.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
            spinner_village.setAdapter(villageAdapter);
        }
        spinner_village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                groupNameList.clear();
                String selected_village = adapterView.getItemAtPosition(pos).toString();
                String selectedVillageID = "null";
                List<Groups> villageWiseGroups = new ArrayList();
               /* for (int i = 0; i < villageList.size(); i++) {
                    if (selected_village.equals(villageList.get(i).getVillageName())) {
                        selectedVillageID = villageList.get(i).getVillageId();
                        break;
                    }
                }*/
                String splitedVillageBracetRemoved =selected_village.substring(selected_village.indexOf("(") + 1, selected_village.indexOf(")"));
                String[] splitedVillageData = splitedVillageBracetRemoved.split("ID::");
                try {
                    selectedVillageID = splitedVillageData[1].trim();
                    selectedVillageID=selectedVillageID.replace(")","");
                    /*   idRightGroupSpinner = splitedGroupData[1].trim();*/
                } catch (Exception e) {

                }
                for (int i = 0; i < studGroupList.size(); i++) {
                    if (selectedVillageID.equals(studGroupList.get(i).getVillageId())) {
                        villageWiseGroups.add(studGroupList.get(i));
                    }
                }

                if (villageWiseGroups.size() == 0) {
                    groupNameList.add(new CustomGroup("NO GROUPS"));
                    /*  groupNameList.add("NO GROUPS");*/
                } else {
                    groupNameList.add(new CustomGroup("SELECT GROUPS"));
                    //  groupNameList.add("SELECT GROUPS");
                    for (int i = 0; i < villageWiseGroups.size(); i++) {
                        if (villageWiseGroups.get(i).getGroupName() != null && (!"".equals(villageWiseGroups.get(i).getGroupName()))) {
                            groupNameList.add(new CustomGroup(villageWiseGroups.get(i).getGroupName(), villageWiseGroups.get(i).getGroupId()));
                        }
                    }
                    AppDatabase.destroyInstance();
                    setStudentDataToLeftSideSpinnerFromDb();
                    setStudentDataToRightSideSpinnerFromDb();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationController.getInstance().setConnectionListener(this);
        tempStorageList = new ArrayList();
    }

    private void setCRLActionBar() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("CRLname");
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setIcon(R.drawable.ic_person_black_24dp);
        ab.setTitle(name);

    }

    private void setStudentDataToLeftSideSpinnerFromDb() {
       /* ArrayList groupNameList = new ArrayList();
        for (int i = 0; i < studGroupList.size(); i++) {
            groupNameList.add(studGroupList.get(i).getGroupName());
        }*/
       /* List customGroups = new ArrayList();
        for (int i = 0; i < groupNameList.size(); i++) {
            String[] splitedGroupData=null;
            try {
                splitedGroupData = groupNameList.get(i).toString().split("=>");
                customGroups.add(new CustomGroup(splitedGroupData[0].trim(), splitedGroupData[1].trim()));
            } catch (ArrayIndexOutOfBoundsException e) {
                customGroups.add(new CustomGroup(splitedGroupData[0].trim()));
            }
        }*/
        ArrayAdapter leftSideGroup = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groupNameList);
        spinner_group_left.setAdapter(leftSideGroup);
        try {
            spinner_group_left.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CustomGroup customGroup = (CustomGroup) parent.getItemAtPosition(position);
                    selectedGroupNameLeftSpinner = customGroup.getName();
                    if ((!selectedGroupNameLeftSpinner.equals("SELECT GROUPS")) && (!selectedGroupNameLeftSpinner.equals("NO GROUPS"))) {
                        recyclerView_leftSide.setVisibility(View.VISIBLE);
                        loadStudentGroupWiseToLeftRecycler(selectedGroupNameLeftSpinner, customGroup.getId());
                    } else {
                        if (tempAllStudent != null) {
                            for (int i = 0; i < tempAllStudent.size(); i++) {
                                if (idLeftGroupSpinner.equals(tempAllStudent.get(i).getGroupId())) {
                                    tempAllStudent.get(i).setChecked(false);
                                }
                            }
                        }
                        recyclerView_leftSide.setVisibility(View.INVISIBLE);
                        idLeftGroupSpinner = "$$$L";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStudentGroupWiseToLeftRecycler(String selectedGroupName, String idLeftGroupSpinnerLocal) {
        if (!idLeftGroupSpinner.equals("$$$L") && idLeftGroupSpinner != null) {
            if (swappedStudents != null) {
                swappedStudents.clear();
            }
            if (tempAllStudent != null) {
                for (int i = 0; i < tempAllStudent.size(); i++) {
                    if (idLeftGroupSpinner.equals(tempAllStudent.get(i).getGroupId())) {
                        tempAllStudent.get(i).setChecked(false);
                    }
                }
            }
        }
        idLeftGroupSpinner = null;
        /*for (int i = 0; i < studGroupList.size(); i++) {
            if (studGroupList.get(i).getGroupName().equals(selectedGroupName)) {
                idLeftGroupSpinner = studGroupList.get(i).getGroupId();
                nameLeftGroupSpinner = studGroupList.get(i).getGroupName();
                break;
            }
        }*/

        nameLeftGroupSpinner = selectedGroupName;
        idLeftGroupSpinner = idLeftGroupSpinnerLocal;

        if (idLeftGroupSpinner != null) {
            if (idLeftGroupSpinner.equals(idRightGroupSpinner)) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Group is Already selected !!! ");
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
                //alertDialog.setMessage("No Groups Available");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spinner_group_left.setSelection(0);
                        idLeftGroupSpinner = "$$$L";
                    }
                });
                alertDialog.show();
            } else {
                if (leftStudentList != null) {
                    leftStudentList.clear();
                }
                if (tempAllStudent != null) {
                    for (int i = 0; i < tempAllStudent.size(); i++) {
                        if (idLeftGroupSpinner.equals(tempAllStudent.get(i).getGroupId())) {
                            leftStudentList.add(tempAllStudent.get(i));
                        }
                    }
                }
                leftSideAdapter = new LeftSideAdapter(this, leftStudentList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView_leftSide.setLayoutManager(layoutManager);
                recyclerView_leftSide.setAdapter(leftSideAdapter);
                leftSideAdapter.notifyDataSetChanged();
                recyclerView_leftSide.setItemAnimator(new SlideInRightAnimator());
                recyclerView_leftSide.getItemAnimator().setAddDuration(1000);
                recyclerView_leftSide.getItemAnimator().setRemoveDuration(1000);
            }
        }
    }

    @OnClick(R.id.btn_move_to_left)
    public void moveLeft() {
        if (validateGroupName()) {
            if (swappedRightSideStudents.size() == 0) {
                Toast.makeText(this, "select a student for shifting", Toast.LENGTH_SHORT).show();
            } else {
                DataChangedFlag = true;
                //recyclerView_leftSide.scrollToPosition(0);
                for (int i = 0; i < swappedRightSideStudents.size(); i++) {
                    swappedRightSideStudents.get(i).setChecked(false);
                    swappedRightSideStudents.get(i).setGroupId(idLeftGroupSpinner);
                    swappedRightSideStudents.get(i).setGroupName(nameLeftGroupSpinner);
                    if (rightStudentList.contains(swappedRightSideStudents.get(i))) {
                        int pos = rightStudentList.indexOf(swappedRightSideStudents.get(i));
                        rightStudentList.remove(swappedRightSideStudents.get(i));
                        rightSideAdapter.notifyItemRemoved(pos);

                        leftStudentList.add(i, swappedRightSideStudents.get(i));
                        leftSideAdapter.notifyItemInserted(i);
                    }
                    if (tempAllStudent.contains(swappedRightSideStudents.get(i))) {
                        int index = tempAllStudent.indexOf(swappedRightSideStudents.get(i));
                        tempAllStudent.get(index).setGroupId(swappedRightSideStudents.get(i).getGroupId());
                        tempAllStudent.get(index).setGroupName(swappedRightSideStudents.get(i).getGroupName());
                    }
                }
                tempStorageList.addAll(swappedRightSideStudents);
                swappedRightSideStudents.clear();
            }
        } else {
            snackbar = Snackbar.make(swapStudParentLayout, "Please Select Group", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }


    private void setStudentDataToRightSideSpinnerFromDb() {
        ArrayAdapter rightSideGroup = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groupNameList);
        spinner_student_rightSide.setAdapter(rightSideGroup);
        spinner_student_rightSide.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CustomGroup customGroup = (CustomGroup) parent.getItemAtPosition(position);
                selectedGroupNameRightSpinner = customGroup.getName();

                if ((!selectedGroupNameRightSpinner.equals("SELECT GROUPS")) && (!selectedGroupNameRightSpinner.equals("NO GROUPS"))) {
                    recyclerView_rightSide.setVisibility(View.VISIBLE);
                    loadStudentGroupWiseToRightRecycler(selectedGroupNameRightSpinner, customGroup.getId());
                } else {
                    if (tempAllStudent != null) {
                        for (int i = 0; i < tempAllStudent.size(); i++) {
                            if (idRightGroupSpinner.equals(tempAllStudent.get(i).getGroupId())) {
                                tempAllStudent.get(i).setChecked(false);
                            }
                        }
                    }
                    recyclerView_rightSide.setVisibility(View.INVISIBLE);
                    idRightGroupSpinner = "$$$R";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadStudentGroupWiseToRightRecycler(String selectedGroupName, String idRightGroupSpinnerLocal) {

        if (!idRightGroupSpinner.equals("$$$R") && idRightGroupSpinner != null) {
            if (swappedRightSideStudents != null) {
                swappedRightSideStudents.clear();
            }
            if (tempAllStudent != null) {
                for (int i = 0; i < tempAllStudent.size(); i++) {
                    if (idRightGroupSpinner.equals(tempAllStudent.get(i).getGroupId())) {
                        tempAllStudent.get(i).setChecked(false);
                    }
                }
            }
        }
        idRightGroupSpinner = null;
        /*for (int i = 0; i < studGroupList.size(); i++) {
            if (studGroupList.get(i).getGroupName().equals(selectedGroupName)) {
                idRightGroupSpinner = studGroupList.get(i).getGroupId();
                nameRightGroupSpinner = studGroupList.get(i).getGroupName();
                break;
            }
        }*/

        nameRightGroupSpinner = selectedGroupName;
        idRightGroupSpinner = idRightGroupSpinnerLocal;

        if (idRightGroupSpinner != null) if (idLeftGroupSpinner.equals(idRightGroupSpinner)) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Group is Already selected !!! ");
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setIcon(R.drawable.ic_error_outline_black_24dp);
            //alertDialog.setMessage("No Groups Available");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    spinner_student_rightSide.setSelection(0);
                    idRightGroupSpinner = "$$$R";
                }
            });
            alertDialog.show();
        } else {
            //  rightStudentList = AppDatabase.getDatabaseInstance(this).getStudentDao().getGroupwiseStudents(idRightGroupSpinner);
            // AppDatabase.destroyInstance();
            if (rightStudentList != null) {
                rightStudentList.clear();
            }
            if (tempAllStudent != null) {
                for (int i = 0; i < tempAllStudent.size(); i++) {
                    if (idRightGroupSpinner.equals(tempAllStudent.get(i).getGroupId())) {
                        rightStudentList.add(tempAllStudent.get(i));
                    }
                }
            }
            rightSideAdapter = new RightSideAdapter(this, rightStudentList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView_rightSide.setLayoutManager(layoutManager);
            recyclerView_rightSide.setAdapter(rightSideAdapter);
            rightSideAdapter.notifyDataSetChanged();
            recyclerView_rightSide.setItemAnimator(new SlideInLeftAnimator());
            recyclerView_rightSide.getItemAnimator().setAddDuration(1000);
            recyclerView_rightSide.getItemAnimator().setRemoveDuration(1000);
        }
    }

    @OnClick(R.id.btn_move_to_right)
    public void moveRight() {
        if (validateGroupName()) {
            if (swappedStudents.size() == 0) {
                Toast.makeText(this, "select a student for shifting", Toast.LENGTH_SHORT).show();
            } else {
                DataChangedFlag = true;
                //recyclerView_rightSide.scrollToPosition(0);
                for (int i = 0; i < swappedStudents.size(); i++) {
                    swappedStudents.get(i).setChecked(false);
                    swappedStudents.get(i).setGroupId(idRightGroupSpinner);
                    swappedStudents.get(i).setGroupName(nameRightGroupSpinner);
                    if (leftStudentList.contains(swappedStudents.get(i))) {
                        int pos = leftStudentList.indexOf(swappedStudents.get(i));
                        leftStudentList.remove(swappedStudents.get(i));
                        leftSideAdapter.notifyItemRemoved(pos);
                        rightStudentList.add(i, swappedStudents.get(i));
                        rightSideAdapter.notifyItemInserted(i);
                    }
                    if (tempAllStudent.contains(swappedStudents.get(i))) {
                        int index = tempAllStudent.indexOf(swappedStudents.get(i));
                        tempAllStudent.get(index).setGroupId(swappedStudents.get(i).getGroupId());
                        tempAllStudent.get(index).setGroupName(swappedStudents.get(i).getGroupName());

                    }
                }
                tempStorageList.addAll(swappedStudents);
                swappedStudents.clear();
            }
        } else {
            snackbar = Snackbar.make(swapStudParentLayout, "Please Select Group", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }


    @Override
    public void addStudent(int pos, Student student) {
        if (swappedStudents.contains(student)) {
            swappedStudents.remove(student);
            leftStudentList.get(pos).setChecked(false);
        } else {
            swappedStudents.add(student);
            leftStudentList.get(pos).setChecked(true);
        }
    }


    @Override
    public void setSelectedRightSide(int pos, Student student) {
        if (swappedRightSideStudents.contains(student)) {
            swappedRightSideStudents.remove(student);
            rightStudentList.get(pos).setChecked(false);
        } else {
            swappedRightSideStudents.add(student);
            rightStudentList.get(pos).setChecked(true);
        }

       /* if (leftSideAdapter!=null)
            leftSideAdapter.notifyDataSetChanged();*/
        //  Toast.makeText(SwapStudentsActivity.this, " RS " + swappedRightSideStudents.size(), Toast.LENGTH_SHORT).show();

    }

    //todo try cath to all db operations and api operations
    @OnClick(R.id.btn_swap_and_push)
    public void pushToDB() {
        List<Student> convertedTempToStud = new ArrayList();
        List<TempStudent> tempDbStudentList = AppDatabase.getDatabaseInstance(this).getTempStudentDao().getAllempStudent();
        if (tempDbStudentList != null && tempDbStudentList.size() > 0) {
            for (int i = 0; i < tempDbStudentList.size(); i++) {
                Student student = new Student();
                student.setStudentId(tempDbStudentList.get(i).getStudentId());
                student.setAge(tempDbStudentList.get(i).getAge());
                student.setFullName(tempDbStudentList.get(i).getFullName());
                student.setGender(tempDbStudentList.get(i).getGender());
                student.setGroupId(tempDbStudentList.get(i).getGroupId());
                student.setGroupName(tempDbStudentList.get(i).getGroupName());
                student.setStud_Class(tempDbStudentList.get(i).getStud_Class());
                convertedTempToStud.add(student);
            }
        }

        if (!tempStorageList.isEmpty()) {
            /*List duplicateElements = new ArrayList();
            List<Student> originalDbList = new ArrayList();
            originalDbList = AppDatabase.getDatabaseInstance(this).getStudentDao().getAllStudents();
            for (int j = 0; j < tempStorageList.size(); j++) {
                for (int i = 0; i < originalDbList.size(); i++) {
                    if (originalDbList.get(i).getStudentId().equals(tempStorageList.get(j).getStudentId())) {
                        if (originalDbList.get(i).getGroupId().equals(tempStorageList.get(j).getGroupId())) {
                            duplicateElements.add(tempStorageList.get(j));
                        }
                    }
                }
            }
            tempStorageList.removeAll(duplicateElements);*/
            if (!tempStorageList.isEmpty()) {
                List unique = new ArrayList();
                for (int i = 0; i < convertedTempToStud.size(); i++) {
                    boolean flag = false;
                    for (int j = 0; j < tempStorageList.size(); j++) {
                        if (convertedTempToStud.get(i).getStudentId().equals(tempStorageList.get(j).getStudentId())) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag == false) {
                        unique.add(convertedTempToStud.get(i));
                    }
                }
                tempStorageList.addAll(unique);
                unique.clear();
                List<TempStudent> convertedStudToTemp = new ArrayList();
                if (tempStorageList.size() > 0) {
                    for (int i = 0; i < tempStorageList.size(); i++) {
                        TempStudent student = new TempStudent();
                        student.setStudentId(tempStorageList.get(i).getStudentId());
                        student.setAge(tempStorageList.get(i).getAge());
                        student.setFullName(tempStorageList.get(i).getFullName());
                        student.setGender(tempStorageList.get(i).getGender());
                        student.setGroupId(tempStorageList.get(i).getGroupId());
                        student.setGroupName(tempStorageList.get(i).getGroupName());
                        student.setStud_Class(tempStorageList.get(i).getStud_Class());
                        convertedStudToTemp.add(student);
                    }
                }
                AppDatabase.getDatabaseInstance(this).getTempStudentDao().deleteTempStudent();
                AppDatabase.getDatabaseInstance(this).getTempStudentDao().insertTempStudent(convertedStudToTemp);
                convertedStudToTemp.clear();
                convertedStudToTemp = AppDatabase.getDatabaseInstance(this).getTempStudentDao().getAllempStudent();
                tempStorageList.clear();
                if (convertedStudToTemp.size() > 0) {
                    for (int i = 0; i < convertedStudToTemp.size(); i++) {
                        Student student = new Student();
                        student.setStudentId(convertedStudToTemp.get(i).getStudentId());
                        student.setAge(convertedStudToTemp.get(i).getAge());
                        student.setFullName(convertedStudToTemp.get(i).getFullName());
                        student.setGender(convertedStudToTemp.get(i).getGender());
                        student.setGroupId(convertedStudToTemp.get(i).getGroupId());
                        student.setGroupName(convertedStudToTemp.get(i).getGroupName());
                        student.setStud_Class(convertedStudToTemp.get(i).getStud_Class());
                        tempStorageList.add(student);
                    }
                }
                customDialogShowOnlineChanges = new CustomDialogShowOnlineChanges(this, tempStorageList);
                customDialogShowOnlineChanges.show();
            } else {
                Toast.makeText(SwapStudentsActivity.this, "No Changes Available", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SwapStudentsActivity.this, "No Changes Available", Toast.LENGTH_LONG).show();
        }

    }

    private void tempChangesSave() {
        List convertedStudToTemp = new ArrayList();
        for (int i = 0; i < tempStorageList.size(); i++) {
            TempStudent tempStudent = new TempStudent();
            tempStudent.setStudentId(tempStorageList.get(i).getStudentId());
            tempStudent.setAge(tempStorageList.get(i).getAge());
            tempStudent.setFullName(tempStorageList.get(i).getFullName());
            tempStudent.setGender(tempStorageList.get(i).getGender());
            tempStudent.setGroupId(tempStorageList.get(i).getGroupId());
            tempStudent.setGroupName(tempStorageList.get(i).getGroupName());
            tempStudent.setStud_Class(tempStorageList.get(i).getStud_Class());
            convertedStudToTemp.add(tempStudent);
        }
        if (!convertedStudToTemp.isEmpty()) {
            AppDatabase.getDatabaseInstance(this).getTempStudentDao().insertTempStudent(convertedStudToTemp);
        }
        SharedPreferences sharedPref = this.getSharedPreferences("prathamInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("offlineSaveTime", DateFormat.getDateTimeInstance().format(new Date()));
        editor.commit();

    }

    private void uploadAPI(String url, String json) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("UPLOADING ... ");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        AndroidNetworking.post(url).setContentType("application/json").addStringBody(json).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.d("responce", response);
                AppDatabase.getDatabaseInstance(SwapStudentsActivity.this).getStudentDao().updateAllStudent(tempStorageList);
                AppDatabase.getDatabaseInstance(SwapStudentsActivity.this).getTempStudentDao().deleteTempStudent();
                tempStorageList.clear();
                dialog.dismiss();
                finish();
            }

            @Override
            public void onError(ANError anError) {

                Toast.makeText(SwapStudentsActivity.this, "NO Internet Connection", Toast.LENGTH_LONG).show();
                tempChangesSave();
                //Log.d("anError", "" + anError);
                dialog.dismiss();
            }
        });
    }

    public boolean validateGroupName() {
        boolean returnFlag = true;
        Animation vibrateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.vibrate);
        if ((selectedGroupNameRightSpinner.equals("SELECT GROUPS")) || (selectedGroupNameRightSpinner.equals("NO GROUPS"))) {
            spinner_student_rightSide.startAnimation(vibrateAnimation);
            returnFlag = false;
        }
        if ((selectedGroupNameLeftSpinner.equals("SELECT GROUPS")) || (selectedGroupNameLeftSpinner.equals("NO GROUPS"))) {
            spinner_group_left.startAnimation(vibrateAnimation);
            returnFlag = false;
        }
        return returnFlag;
    }


    @Override
    public void onBackPressed() {
        finish();
//        super.onBackPressed();
  /*      if (DataChangedFlag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("WARNING !!!");
            builder.setMessage("Do you want to leave this page?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    if (tempAllStudent != null) tempAllStudent.clear();
                    if (tempStorageList != null) tempStorageList.clear();
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            finish();
        }*/
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
    public void update() {
        if (internetIsAvailable) {
            if (!tempStorageList.isEmpty()) {
                //List<Student> updatedStudent = AppDatabase.getDatabaseInstance(this).getStudentDao().getAllStudents();
                Gson gson = new Gson();
                String updatedStudentJSON = gson.toJson(tempStorageList);
                MetaData metaData = new MetaData();
                metaData.setKeys("pushDataTime");
                metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));

                AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);
                List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
                String metaDataJSON = customParse(metaDataList);
                String json = "{ \"student\":" + updatedStudentJSON + ",\"metadata\":" + metaDataJSON + "}";
                AppDatabase.destroyInstance();
                switch (program) {
                    case APIs.HL:
                        uploadAPI(APIs.HLpushToServerURL, json);
                        break;
                    case APIs.RI:
                        uploadAPI(APIs.RIpushToServerURL, json);
                        break;
                    case APIs.SC:
                        uploadAPI(APIs.SCpushToServerURL, json);
                        break;
                    case APIs.PI:
                        uploadAPI(APIs.PIpushToServerURL, json);
                        break;

                }
            } else {
                Toast.makeText(this, "No updates available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            tempChangesSave();
            if (DataChangedFlag) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("NO INTERNET CONNECTION.");
                builder.setMessage("Changes are saved on local database ");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        if (tempStorageList != null) {
                            tempStorageList.clear();
                        }
                        customDialogShowOnlineChanges.dismiss();
                        finish();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
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

    @Override
    public void clearList() {
        spinner_group_left.setSelection(0);
        spinner_student_rightSide.setSelection(0);
        tempStorageList.clear();
        swappedRightSideStudents.clear();
        swappedStudents.clear();
        rightStudentList.clear();
        leftStudentList.clear();
        tempAllStudent.clear();
        tempAllStudent = AppDatabase.getDatabaseInstance(this).getStudentDao().getAllStudents();
        AppDatabase.getDatabaseInstance(this).getTempStudentDao().deleteTempStudent();
    }
}
