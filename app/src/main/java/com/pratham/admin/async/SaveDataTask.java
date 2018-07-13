package com.pratham.admin.async;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.interfaces.OnSavedData;

import java.util.List;

public class SaveDataTask extends AsyncTask<Void, Integer, Void> {
    private List CRLList;
    private List studentList;
    private List groupsList;
    private List villageList;
    private Context context;
    private ProgressDialog dialog;
    private OnSavedData onSavedData;

    public SaveDataTask(Context context, OnSavedData onSavedData,List CRLList, List studentList, List groupsList,
                        List villageList) {
        this.CRLList = CRLList;
        this.studentList = studentList;
        this.groupsList = groupsList;
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

