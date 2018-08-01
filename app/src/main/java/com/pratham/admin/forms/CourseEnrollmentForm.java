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
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.custom.MultiSpinner;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.ConnectionReceiverListener;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.Course;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Topic;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.ConnectionReceiver;
import com.pratham.admin.util.CustomGroup;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import java.lang.reflect.Type;
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

// CourseEnrollment = CourseCommunity

public class CourseEnrollmentForm extends AppCompatActivity implements ConnectionReceiverListener {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.sp_Groups)
    MultiSpinner sp_Groups;
    @BindView(R.id.sp_Course)
    Spinner sp_Course;
    @BindView(R.id.ms_sp_Topics)
    MultiSpinner ms_sp_Topics;
    @BindView(R.id.sp_SelectCoach)
    MultiSpinner sp_SelectCoach;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    //    @BindView(R.id.edt_PresentStdCount)
//    EditText edt_PresentStdCount;
//    @BindView(R.id.rg_ParentsParticipation)
//    RadioGroup rg_ParentsParticipation;
    @BindView(R.id.rg_Community)
    RadioGroup rg_Community;
    @BindView(R.id.rb_Yes)
    RadioButton rb_Yes;
    @BindView(R.id.rb_Community)
    RadioButton rb_Community;


    @BindView(R.id.btn_Submit)
    Button btn_Submit;

    List<Village> villageList = new ArrayList<>();
    List<Groups> AllGroupsInDB;
    List registeredGRPs;
    List<Course> courseList = new ArrayList<>();
    List<Coach> coachList = new ArrayList<>();
    List<Course> courseDetails = new ArrayList<>();
    JsonArray topicDetails = new JsonArray();
    List<Topic> TopicList;

    boolean[] selectedItems;
    List<String> selectedTopicsArray;
    String selectedTopics = "";
    String selectedTopicNames = "";
    List Topics;
    String vid = "";
    String courseID = "";
    List<String> TT;
    List<String> TopicNames;
    private String courseName = "";

    // Selected Groups
    List<String> selectedGroupsArray;
    List registeredGroups;
    private boolean[] selectedGroupItems;
    List<String> Grps;
    String selectedGroups = "";

    // Coaches PC
    List<CustomGroup> registeredPCGRPs;
    private boolean[] selectedPCItems;
    List<String> PC = new ArrayList<>();
    String selectedPC = "";

    boolean internetIsAvailable = false;

    UUID uuid;
    String uniqueCommunityID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_enrollment_form);
        ButterKnife.bind(this);

        checkConnection();

        // Hide Actionbar
        getSupportActionBar().hide();

        uniqueCommunityID = UUID.randomUUID().toString();

        // Set Default Todays date
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);

        //retrive all groups from  DB
        AllGroupsInDB = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        populateVillages();

        // Populate Science Course Spinner
        courseList = AppDatabase.getDatabaseInstance(this).getCoursesDao().getAllCourse();
        populateCourses();

        // Populate Coach Spinner
        coachList = AppDatabase.getDatabaseInstance(this).getCoachDao().getAllCoaches();
        populateCoaches();

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


    @OnClick(R.id.btn_Submit)
    public void submitForm(View view) {

        if ((sp_Village.getSelectedItemPosition() > 0) && (selectedGroups.trim().length() > 0)
                && (selectedPC.trim().length() > 0) && (sp_Course.getSelectedItemPosition() > 0)) {

            try {

                checkConnection();

/*                // parentsParticipation
                int selectedId = rg_ParentsParticipation.getCheckedRadioButtonId();
                RadioButton selectedOption = (RadioButton) findViewById(selectedId);
                String parentsParticipation = selectedOption.getText().toString();
                int status;
                if (parentsParticipation.equalsIgnoreCase("Yes")) {
                    status = 1; // Active
                } else {
                    status = 0; // InActive
                }*/

                // Community
                int selectedCId = rg_Community.getCheckedRadioButtonId();
                RadioButton selectedCOption = (RadioButton) findViewById(selectedCId);
                String Community = selectedCOption.getText().toString();

                Community commObj = new Community();
                commObj.CommunityID = uniqueCommunityID;
                commObj.VillageID = vid;
                commObj.GroupID = selectedGroups;
                commObj.CourseAdded = courseName;
                commObj.TopicAdded = selectedTopicNames;
                commObj.AddedTopicsID = selectedTopics;
                commObj.StartDate = btn_DatePicker.getText().toString().trim();
                commObj.EndDate = "";
                commObj.CoachID = selectedPC;
                commObj.Community = Community;
                commObj.AddedCourseID = courseID;
                commObj.ParentParticipation = 0;
                commObj.PresentStudent = 0;
                commObj.sentFlag = 0;

                AppDatabase.getDatabaseInstance(this).getCommunityDao().insertCommunity(Collections.singletonList(commObj));
                Toast.makeText(this, "Form Saved to Database !!!", Toast.LENGTH_SHORT).show();

                // Push To Server
                try {
                    if (internetIsAvailable) {
                        Gson gson = new Gson();
                        String CommunityJSON = gson.toJson(Collections.singletonList(commObj));

                        MetaData metaData = new MetaData();
                        metaData.setKeys("pushDataTime");
                        metaData.setValue(DateFormat.getDateTimeInstance().format(new Date()));
                        List<MetaData> metaDataList = AppDatabase.getDatabaseInstance(this).getMetaDataDao().getAllMetaData();
                        String metaDataJSON = customParse(metaDataList);
                        AppDatabase.getDatabaseInstance(this).getMetaDataDao().insertMetadata(metaData);

                        String json = "{ \"CommunityJSON\":" + CommunityJSON + ",\"metadata\":" + metaDataJSON + "}";
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
                                AppDatabase.getDatabaseInstance(CourseEnrollmentForm.this).getCommunityDao().updateSentFlag(1, uniqueCommunityID);
                                Toast.makeText(CourseEnrollmentForm.this, "Form Data Pushed to Server !!!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                resetForm();
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(CourseEnrollmentForm.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                                AppDatabase.getDatabaseInstance(CourseEnrollmentForm.this).getCommunityDao().updateSentFlag(0, uniqueCommunityID);
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
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Please enter all the fields !!!", Toast.LENGTH_SHORT).show();
        }


    }

    private void resetForm() {
        uniqueCommunityID = UUID.randomUUID().toString();
        populateVillages();
        populateCoaches();
        populateCourses();
        rg_Community.clearCheck();
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
        rb_Community.setChecked(true);
//        rg_ParentsParticipation.clearCheck();
//        edt_PresentStdCount.getText().clear();
//        rb_Yes.setChecked(true);
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
            ArrayAdapter villageAdapter = new ArrayAdapter(CourseEnrollmentForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
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

        ArrayAdapter grpAdapter = new ArrayAdapter(CourseEnrollmentForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGroups);
        sp_Groups.setAdapter(grpAdapter, false, onVGSelectedListener);
        // set initial selection
        selectedGroupItems = new boolean[grpAdapter.getCount()];
        sp_Groups.setHint("Select Groups");
        sp_Groups.setHintTextColor(Color.BLACK);

    }

    // Groups Listener
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

    private void populateCourses() {
        final List CourseName = new ArrayList();
        if (!courseList.isEmpty()) {
            CourseName.add(new CustomGroup("Select Course"));
            for (int j = 0; j < courseList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(courseList.get(j).getCourseName(), courseList.get(j).getCourseID());
                CourseName.add(customGroup);
            }
            ArrayAdapter villageAdapter = new ArrayAdapter(CourseEnrollmentForm.this, android.R.layout.simple_spinner_dropdown_item, CourseName);
            sp_Course.setAdapter(villageAdapter);
        }

        sp_Course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) CourseName.get(pos);
                courseID = "";
                courseName = "";
                courseID = customGroup.getId();
                courseName = customGroup.getName();

                // Populate Registered Groups Spinner
                if (courseID != null)
                    populateTopics(courseID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void populateTopics(String cid) {
        // Get Topics according to Course
        courseDetails = AppDatabase.getDatabaseInstance(this).getCoursesDao().getAllCourseDetails(cid);
        topicDetails = courseDetails.get(0).getListTopic();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Topic>>() {
        }.getType();
        TopicList = gson.fromJson(topicDetails.toString(), listType);

        TT = new ArrayList<>();
        TopicNames = new ArrayList<>();
        Topics = new ArrayList();

        for (int i = 0; i < TopicList.size(); i++) {
            Topics.add(new CustomGroup(TopicList.get(i).TopicName, TopicList.get(i).TopicID));
            TT.add(TopicList.get(i).TopicID);
            TopicNames.add(TopicList.get(i).TopicName);
        }

        ArrayAdapter subAdapter = new ArrayAdapter(CourseEnrollmentForm.this, android.R.layout.simple_spinner_dropdown_item, Topics);
        ms_sp_Topics.setAdapter(subAdapter, false, onSelectedListener);
        selectedItems = new boolean[subAdapter.getCount()];
        ms_sp_Topics.setHint("Select Topics");
        ms_sp_Topics.setHintTextColor(Color.BLACK);
    }

    // Listener
    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            selectedTopics = "";
            selectedTopicNames = "";
            // Do something here with the selected items
            selectedTopicsArray = new ArrayList<>();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedTopicsArray.add(TT.get(i));
                    selectedTopics = selectedTopics + "," + TT.get(i);
                    selectedTopicNames = selectedTopicNames + ",\n" + TopicNames.get(i);
                }
            }
            selectedTopics = selectedTopics.replaceFirst(",", "");
            selectedTopicNames = selectedTopicNames.replaceFirst(",\n", "");
        }
    };


    // Present Groups
    private void populateCoaches() {
        registeredPCGRPs = new ArrayList();
        for (int j = 0; j < coachList.size(); j++) {
            CustomGroup customGroup = new CustomGroup(coachList.get(j).getCoachName(), coachList.get(j).getCoachID());
            registeredPCGRPs.add(customGroup);
            PC.add(coachList.get(j).getCoachID());
        }

        ArrayAdapter coachAdapter = new ArrayAdapter(CourseEnrollmentForm.this, android.R.layout.simple_spinner_dropdown_item, registeredPCGRPs);
        sp_SelectCoach.setAdapter(coachAdapter, false, onPCSelectedListener);
        // set initial selection
        selectedPCItems = new boolean[coachAdapter.getCount()];
        sp_SelectCoach.setHint("Select Coach");
        sp_SelectCoach.setHintTextColor(Color.BLACK);

    }

    // PC Listener
    private MultiSpinner.MultiSpinnerListener onPCSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            List<String> selectedPCArray = new ArrayList<>();
            selectedPC = "";
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedPCArray.add(PC.get(i));
                    selectedPC = selectedPC + "," + PC.get(i);
                }
            }
            selectedPC = selectedPC.replaceFirst(",", "");
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

