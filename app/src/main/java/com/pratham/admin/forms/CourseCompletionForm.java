package com.pratham.admin.forms;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pratham.admin.R;
import com.pratham.admin.adapters.CourseTopicRVDataAdapter;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.DashRVClickListener;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.CourseTopicItem;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.util.CustomGroup;
import com.pratham.admin.util.DatePickerFragmentOne;
import com.pratham.admin.util.DatePickerFragmentTwo;
import com.pratham.admin.util.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CourseCompletionForm extends AppCompatActivity implements DashRVClickListener {

    @BindView(R.id.sp_Village)
    Spinner sp_Village;
    @BindView(R.id.sp_Groups)
    Spinner sp_Groups;

    @BindView(R.id.rg_Event)
    RadioGroup rg_Event;
    @BindView(R.id.edt_ParentCount)
    EditText edt_ParentCount;
    @BindView(R.id.btn_DatePicker)
    Button btn_DatePicker;
    @BindView(R.id.btn_DatePickerTwo)
    Button btn_DatePickerTwo;
    @BindView(R.id.btn_Submit)
    Button btn_Submit;
    @BindView(R.id.tv_Course_Warning)
    TextView tv_Course_Warning;

    List<Village> villageList = new ArrayList<>();
    List<Groups> AllGroupsInDB;
    List<Community> CourseTopicsByGrp;
    List registeredGRPs;
    RecyclerView CourseTopicsRV;

    private List<CourseTopicItem> CourseTopicItemList = new ArrayList<>();
    CourseTopicRVDataAdapter DataAdapter;
    String topics = "";

    List selectedCourseIDs;
    List selectedTopicIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_completion_form);
        ButterKnife.bind(this);

        // Hide Actionbar
        getSupportActionBar().hide();

        btn_DatePicker.setText(new Utility().GetCurrentDate().toString());
        btn_DatePicker.setPadding(8, 8, 8, 8);
        btn_DatePickerTwo.setText(new Utility().GetCurrentDate().toString());
        btn_DatePickerTwo.setPadding(8, 8, 8, 8);

        //retrive all groups from  DB
        AllGroupsInDB = AppDatabase.getDatabaseInstance(this).getGroupDao().getAllGroups();

        // Populate Village Spinner
        villageList = AppDatabase.getDatabaseInstance(this).getVillageDao().getAllVillages();
        populateVillages();

        // Create the recyclerview.
        CourseTopicsRV = (RecyclerView) findViewById(R.id.card_view_recycler_list);
        // Create the grid layout manager with 2 columns.
//        CourseTopicsRV.addOnItemTouchListener(new DashRVTouchListener(getApplicationContext(), CourseTopicsRV, CourseCompletionForm.this));
    }

    /* Initialise items in list. */
    private void initializeItemList(String groupId) {
        CourseTopicItemList.clear();
        CourseTopicsByGrp = AppDatabase.getDatabaseInstance(this).getCommunityDao().getCommunityByGroupID(groupId);

        // If No Courses Available
        if (sp_Village.getSelectedItemPosition() > 0 && sp_Groups.getSelectedItemPosition() > 0)
            if (CourseTopicsByGrp.size() == 0) {
                tv_Course_Warning.setVisibility(View.VISIBLE);
            } else {
                tv_Course_Warning.setVisibility(View.GONE);
            }

        for (int i = 0; i < CourseTopicsByGrp.size(); i++) {
            CourseTopicItemList.add(new CourseTopicItem(CourseTopicsByGrp.get(i).AddedCourseID, CourseTopicsByGrp.get(i).CourseAdded, CourseTopicsByGrp.get(i).AddedTopicsID, CourseTopicsByGrp.get(i).TopicAdded, false));
        }
        if (DataAdapter == null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            // Set layout manager.
            CourseTopicsRV.setLayoutManager(gridLayoutManager);
            // Create recycler view data adapter with item list.
            DataAdapter = new CourseTopicRVDataAdapter(CourseTopicItemList);
            // Set data adapter.
            CourseTopicsRV.setAdapter(DataAdapter);
        } else {
            DataAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.btn_Submit)
    public void submitForm(View view) {

        // getting Selected Items
        for (int i = 0; i < CourseTopicRVDataAdapter.ItemList.size(); i++) {
            selectedCourseIDs = new ArrayList();
            selectedTopicIDs = new ArrayList();
            if (CourseTopicRVDataAdapter.ItemList.get(i).getSelected()) {
                selectedCourseIDs.add(CourseTopicRVDataAdapter.ItemList.get(i).getCourseIDs());
                selectedCourseIDs.add(CourseTopicRVDataAdapter.ItemList.get(i).getTopicIDs());
            }
        }
    }

    @OnClick(R.id.btn_DatePicker)
    public void startDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentOne();
        newFragment.show(getFragmentManager(), "DatePicker");
    }

    @OnClick(R.id.btn_DatePickerTwo)
    public void endDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragmentTwo();
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
            ArrayAdapter villageAdapter = new ArrayAdapter(CourseCompletionForm.this, android.R.layout.simple_spinner_dropdown_item, VillageName);
            sp_Village.setAdapter(villageAdapter);
        }

        sp_Village.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) VillageName.get(pos);
                String vid = customGroup.getId();

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

        ArrayAdapter grpAdapter = new ArrayAdapter(CourseCompletionForm.this, android.R.layout.simple_spinner_dropdown_item, registeredGRPs);
        sp_Groups.setAdapter(grpAdapter);
        sp_Groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                CustomGroup customGroup = (CustomGroup) registeredGRPs.get(pos);
                String groupId = customGroup.getId();

                // Populate Courses of grps
                // Recycler View
                initializeItemList(groupId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @Override
    public void onClick(View view, int position) {
        CourseTopicItem item = CourseTopicItemList.get(position);
        String Course = item.getCourse();
        String Topic = item.getTopic();
        Toast.makeText(this, "" + Course + "\n" + Topic, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
