package com.pratham.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pratham.admin.R;
import com.pratham.admin.adapters.DashRVDataAdapter;
import com.pratham.admin.forms.AttendanceForm;
import com.pratham.admin.forms.CoachInformationForm;
import com.pratham.admin.forms.CoachRetentionForm;
import com.pratham.admin.forms.CourseCompletionForm;
import com.pratham.admin.forms.CourseEnrollmentForm;
import com.pratham.admin.forms.CrlVisitForm;
import com.pratham.admin.forms.ReportingStudentsForm;
import com.pratham.admin.forms.SchoolSessionForm;
import com.pratham.admin.interfaces.DashRVClickListener;
import com.pratham.admin.modalclasses.DashboardItem;
import com.pratham.admin.util.DashRVTouchListener;

import java.util.ArrayList;
import java.util.List;

public class FormsActivity extends AppCompatActivity implements DashRVClickListener {

    // Ref : https://www.dev2qa.com/android-cardview-with-image-and-text-example/
    String LoggedcrlId = "", LoggedcrlName = "", LoggedCRLnameSwapStd = "";

    private List<DashboardItem> DashboardItemList = null;
    DashRVDataAdapter DataAdapter;

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
            DashboardItemList.add(new DashboardItem("Coach Information", R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem("Course Completion", R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem("Coach Retention", R.drawable.ic_form));
//            DashboardItemList.add(new DashboardItem("School Session", R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem("Course Enrollment", R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem("Reporting Students", R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem("CRL Visit", R.drawable.ic_form));
            DashboardItemList.add(new DashboardItem("Students Attendance", R.drawable.ic_form));
        }
    }

    @Override
    public void onClick(View view, int position) {
        DashboardItem Dash = DashboardItemList.get(position);
        String name = Dash.getName();

        if (name.contains("Coach Information")) {
            Intent intent = new Intent(FormsActivity.this, CoachInformationForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains("Completion")) {
            Intent intent = new Intent(FormsActivity.this, CourseCompletionForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains("Retention")) {
            Intent intent = new Intent(FormsActivity.this, CoachRetentionForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } /*else if (name.contains("Session")) {
            Intent intent = new Intent(FormsActivity.this, SchoolSessionForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } */else if (name.contains("Reporting")) {
            Intent intent = new Intent(FormsActivity.this, ReportingStudentsForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains("CRL")) {
            Intent intent = new Intent(FormsActivity.this, CrlVisitForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains("Student")) {
            Intent intent = new Intent(FormsActivity.this, AttendanceForm.class);
            intent.putExtra("CRLid", LoggedcrlId);
            intent.putExtra("CRLname", LoggedcrlName);
            intent.putExtra("CRLnameSwapStd", LoggedCRLnameSwapStd);
            startActivity(intent);
        } else if (name.contains("Enrollment")) {
            Intent intent = new Intent(FormsActivity.this, CourseEnrollmentForm.class);
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
