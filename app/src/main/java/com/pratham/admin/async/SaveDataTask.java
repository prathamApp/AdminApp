package com.pratham.admin.async;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.OnSavedData;
import com.pratham.admin.modalclasses.Aser;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.Completion;
import com.pratham.admin.modalclasses.Course;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.util.BackupDatabase;

import java.util.List;

public class SaveDataTask extends AsyncTask<Void, Integer, Void> {
    private List CRLList;
    private List studentList;
    private List groupsList;
    private List coursesList;
    private List CommunityList;
    private List CompletionList;
    private List coachList;
    private List villageList;
    private List<Aser> aserList;
    private Context context;
    private ProgressDialog dialog;
    private OnSavedData onSavedData;

    public SaveDataTask(Context context, OnSavedData onSavedData, List CRLList, List studentList, List groupsList,
                        List villageList, List<Course> courseList, List<Coach> coachList, List<Community> communityList, List<Completion> completionList, List<Aser> aserList) {
        this.CRLList = CRLList;
        this.studentList = studentList;
        this.groupsList = groupsList;
        this.coursesList = courseList;
        this.CommunityList = communityList;
        this.CompletionList = completionList;
        this.coachList = coachList;
        this.villageList = villageList;
        this.context = context;
        this.onSavedData = onSavedData;
        this.aserList = aserList;
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
        AppDatabase.getDatabaseInstance(context).getCompletionDao().insertCompletion(CompletionList);
        AppDatabase.getDatabaseInstance(context).getCoachDao().insertCoach(coachList);
        AppDatabase.getDatabaseInstance(context).getVillageDao().insertAllVillages(villageList);
        AppDatabase.getDatabaseInstance(context).getAserDao().insertAllAserList(aserList);
        AppDatabase.destroyInstance();
        BackupDatabase.backup(context);

        try {
            // Delete Deleted Group's Students
            List<Groups> delGrps = AppDatabase.getDatabaseInstance(context).getGroupDao().GetDeletedGroups();
            for (int i = 0; i < delGrps.size(); i++) {
                AppDatabase.getDatabaseInstance(context).getStudentDao().deleteDeletedGrpsStdRecords(delGrps.get(i).GroupId);
            }
            // Delete Deleted Groups
            AppDatabase.getDatabaseInstance(context).getGroupDao().removeDeletedGroupRecords();

            // Delete Deleted Students
            AppDatabase.getDatabaseInstance(context).getStudentDao().removeDeletedStudentRecords();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BackupDatabase.backup(context);

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

