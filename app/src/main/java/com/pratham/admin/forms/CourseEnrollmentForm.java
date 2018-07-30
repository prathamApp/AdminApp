package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.graphics.Color;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.pratham.admin.R;
import com.pratham.admin.custom.MultiSpinner;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.Course;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Topic;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.CustomGroup;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.Utility;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CourseEnrollmentForm extends AppCompatActivity {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.sp_Groups)
    Spinner sp_Groups;
    @BindView(R.id.sp_Course)
    Spinner sp_Course;
    @BindView(R.id.ms_sp_Topics)
    MultiSpinner ms_sp_Topics;
    @BindView(R.id.sp_SelectCoach)
    Spinner sp_SelectCoach;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.edt_PresentStdCount)
    EditText edt_PresentStdCount;
    @BindView(R.id.rg_ParentsParticipation)
    RadioGroup rg_ParentsParticipation;
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
    List Topics;
    private String selectedCoachID = "";
    String vid = "";
    String courseID = "";
    String grpId = "";
    List<String> TT;
    private String courseName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_enrollment_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

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

    @OnClick(R.id.btn_Submit)
    public void submitForm(View view) {

        if ((sp_Village.getSelectedItemPosition() > 0) && (sp_Groups.getSelectedItemPosition() > 0)
                && (sp_SelectCoach.getSelectedItemPosition() > 0) && (sp_Course.getSelectedItemPosition() > 0)
                && (edt_PresentStdCount.getText().toString().trim().length() > 0)) {

            try {

                // parentsParticipation
                int selectedId = rg_ParentsParticipation.getCheckedRadioButtonId();
                RadioButton selectedOption = (RadioButton) findViewById(selectedId);
                String parentsParticipation = selectedOption.getText().toString();
                int status;
                if (parentsParticipation.equalsIgnoreCase("Yes")) {
                    status = 1; // Active
                } else {
                    status = 0; // InActive
                }

                // Community
                int selectedCId = rg_Community.getCheckedRadioButtonId();
                RadioButton selectedCOption = (RadioButton) findViewById(selectedCId);
                String Community = selectedCOption.getText().toString();

                Community commObj = new Community();
                commObj.VillageID = vid;
                commObj.GroupID = grpId;
                commObj.CourseAdded = courseName;
                commObj.TopicAdded = selectedTopics;
                commObj.StartDate = btn_DatePicker.getText().toString().trim();
                commObj.EndDate = "";
                commObj.CoachID = selectedCoachID;
                commObj.Community = Community;
                commObj.CompletedCourseID = courseID;
                commObj.ParentParticipation = status;
                commObj.PresentStudent = Integer.parseInt(edt_PresentStdCount.getText().toString().trim());
                commObj.sentFlag = 0;

                AppDatabase.getDatabaseInstance(this).getCommunityDao().insertCommunity(Collections.singletonList(commObj));

                Toast.makeText(this, "Form Submitted !!!", Toast.LENGTH_SHORT).show();
                resetForm();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void resetForm() {
        populateVillages();
        populateCoaches();
        populateCourses();
        rg_Community.clearCheck();
        rg_ParentsParticipation.clearCheck();
        edt_PresentStdCount.getText().clear();
        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
        rb_Community.setChecked(true);
        rb_Yes.setChecked(true);
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

    private void populateRegisteredGroups(String villageID) {
        // todo get registered grps
        registeredGRPs = new ArrayList();
        registeredGRPs.add(new CustomGroup("Select Groups"));
        if (AllGroupsInDB != null) {
            for (int i = 0; i < AllGroupsInDB.size(); i++) {
                if (AllGroupsInDB.get(i).getVillageId().equals(villageID))
                    registeredGRPs.add(new CustomGroup(AllGroupsInDB.get(i).getGroupName(), AllGroupsInDB.get(i).getGroupId()));
            }
        }

        ArrayAdapter grpAdapter = new ArrayAdapter(CourseEnrollmentForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGRPs);
        sp_Groups.setAdapter(grpAdapter);
        sp_Groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) registeredGRPs.get(pos);
                grpId = customGroup.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

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
        Topics = new ArrayList();

        for (int i = 0; i < TopicList.size(); i++) {
            Topics.add(new CustomGroup(TopicList.get(i).TopicName, TopicList.get(i).TopicID));
            TT.add(TopicList.get(i).TopicID);
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
            // Do something here with the selected items
            selectedTopicsArray = new ArrayList<>();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    selectedTopicsArray.add(TT.get(i));
                    selectedTopics = selectedTopics + "," + TT.get(i);
                }
            }
            selectedTopics = selectedTopics.replaceFirst(",", "");
        }
    };


    private void populateCoaches() {
        final List CoachName = new ArrayList();
        if (!coachList.isEmpty()) {
            CoachName.add(new CustomGroup("Select Coach"));
            for (int j = 0; j < coachList.size(); j++) {
                CustomGroup customGroup = new CustomGroup(coachList.get(j).getCoachName(), coachList.get(j).getCoachID());
                CoachName.add(customGroup);
            }
            ArrayAdapter coachAdapter = new ArrayAdapter(CourseEnrollmentForm.this, android.R.layout.simple_spinner_dropdown_item, CoachName);
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

}
