package com.pratham.admin.async;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.OnSavedData;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.Course;

import java.util.List;

public class SaveDataTask extends AsyncTask<Void, Integer, Void> {
    private List CRLList;
    private List studentList;
    private List groupsList;
    private List coursesList;
    private List CommunityList;
    private List coachList;
    private List villageList;
    private Context context;
    private ProgressDialog dialog;
    private OnSavedData onSavedData;

    public SaveDataTask(Context context, OnSavedData onSavedData, List CRLList, List studentList, List groupsList,
                        List villageList, List<Course> courseList, List<Coach> coachList, List<Community> communityList) {
        this.CRLList = CRLList;
        this.studentList = studentList;
        this.groupsList = groupsList;
        this.coursesList = courseList;
        this.CommunityList = communityList;
        this.coachList = coachList;
        this.villageList = villageList;
        this.context = context;
        this.onSavedData = onSavedData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
//        dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
        dialog.setTitle("Saving....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        AppDatabase.getDatabaseInstance(context).getCRLdao().insertAllCRL(CRLList);
        AppDatabase.getDatabaseInstance(context).getStudentDao().insertAllStudents(studentList);
        AppDatabase.getDatabaseInstance(context).getGroupDao().insertAllGroups(groupsList);
        AppDatabase.getDatabaseInstance(context).getCoursesDao().insertCourses(coursesList);
        AppDatabase.getDatabaseInstance(context).getCommunityDao().insertCommunity(CommunityList);
        AppDatabase.getDatabaseInstance(context).getCoachDao().insertCoach(coachList);
        AppDatabase.getDatabaseInstance(context).getVillageDao().insertAllVillages(villageList);
        AppDatabase.destroyInstance();
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        Intent swapStudent = new Intent( context,MainActivity.class);
//        context.startActivity(swapStudent);
        dialog.dismiss();
        onSavedData.onDataSaved();

    }

}

