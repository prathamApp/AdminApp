package com.pratham.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pratham.admin.R;
import com.pratham.admin.adapters.DashRVDataAdapter;
import com.pratham.admin.forms.AttendanceForm.AttendanceForm_;
import com.pratham.admin.forms.CoachInformationForm;
import com.pratham.admin.forms.CoachRetentionForm;
import com.pratham.admin.forms.CourseCompletionForm;
import com.pratham.admin.forms.CourseEnrollmentForm;
import com.pratham.admin.forms.CrlVisitForm;
import com.pratham.admin.forms.DeleteStudentsForm;
import com.pratham.admin.forms.GroupSessionForm;
import com.pratham.admin.forms.GroupVisitForm;
import com.pratham.admin.forms.Youth.YouthInfoFormActivity_;
import com.pratham.admin.interfaces.DashRVClickListener;
import com.pratham.admin.modalclasses.DashboardItem;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.DashRVTouchListener;

import java.util.ArrayList;
import java.util.List;

public class FormsActivity extends BaseActivity implements DashRVClickListener {

    // Ref : https://www.dev2qa.com/android-cardview-with-image-and-text-example/
    String LoggedcrlId = "", LoggedcrlName = "", LoggedCRLnameSwapStd = "";
    DashRVDataAdapter DataAdapter;
    private List<DashboardItem> DashboardItemList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);
        // Hide Actionbar
        getSupportActionBar().hide();

        LoggedcrlId = getIntent().getStringExtra("CRLid");
        LoggedcrlName = getIntent().getStringExtra("CRLname");
        LoggedCRLnameSwapStd = getIntent().getStringExtra("CRLnameSwapStd");

        // Recycler View
        initializeItemList();

        // Create the recyclerview.
        RecyclerView dashRecyclerView = (RecyclerView) findViewById(R.id.card_view_recycler_list);
        // Create the grid layout manager with 2 columns.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        // Set layout manager.
        dashRecyclerView.setLayoutManager(gridLayoutManager);

        // Create recycler view data adapter with item list.
        DataAdapter = new DashRVDataAdapter(DashboardItemList);
        // Set data adapter.
        dashRecyclerView.setAdapter(DataAdapter);

        dashRecyclerView.addOnItemTouchListener(new DashRVTouchListener(getApplicationContext(), dashRecyclerView, FormsActivity.this));

    }

    /* Initialise items in list. */
    private void initializeItemList() {
        if (DashboardItemList == null) {
            DashboardItemList = new ArrayList<DashboardItem>();
            DashboardItemList.add(new DashboardItem(getString(R.string.coachinformation), R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem(getString(R.string.coursecompletion), R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem(getString(R.string.coachretention), R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem(getString(R.string.groupsession), R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem(getString(R.string.groupvisit), R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem(getString(R.string.courseenrollment), R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem(getString(R.string.studentsattendance), R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem(getString(R.string.addyouth), R.drawable.ic_form));
        }
    }

    @Override
    public void onClick(View view, int position) {
        DashboardItem Dash = DashboardItemList.get(position);
        String name = Dash.getName();

        if (name.contains(getString(R.string.coachinformation))) {
            Intent intent = new Intent(FormsActivity.this, CoachInformationForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains(getString(R.string.coursecompletion))) {
            Intent intent = new Intent(FormsActivity.this, CourseCompletionForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains(getString(R.string.coachretention))) {
            Intent intent = new Intent(FormsActivity.this, CoachRetentionForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains(getString(R.string.groupsession))) {
            Intent intent = new Intent(FormsActivity.this, GroupSessionForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains("Delete")) {
            Intent intent = new Intent(FormsActivity.this, DeleteStudentsForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.equalsIgnoreCase("CRL Visit")) {
            Intent intent = new Intent(FormsActivity.this, CrlVisitForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains(getString(R.string.studentsattendance))) {
            Intent intent = new Intent(FormsActivity.this, AttendanceForm_.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains(getString(R.string.courseenrollment))) {
            Intent intent = new Intent(FormsActivity.this, CourseEnrollmentForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains(getString(R.string.groupvisit))) {
            Intent intent = new Intent(FormsActivity.this, GroupVisitForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        }else if (name.contains(getString(R.string.addyouth))) {
            Intent intent = new Intent(FormsActivity.this, YouthInfoFormActivity_.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        }
    }

    @Override
    public void onLongClick(View view, int position) {

    }
}
