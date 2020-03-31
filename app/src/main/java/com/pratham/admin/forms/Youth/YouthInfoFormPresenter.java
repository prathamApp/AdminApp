package com.pratham.admin.forms.Youth;

import android.content.Context;

import com.pratham.admin.ApplicationController;
import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.modalclasses.Youth;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.Utility;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

@EBean
public class YouthInfoFormPresenter implements YouthInfoFormContract.YouthInfoFormPresenter{

    Context context;
    YouthInfoFormContract.YouthInfoFormView youthInfoFormView;
    List<String> Blocks;
    List<Village> Villages;
    List<Groups> Groups;
    List<String> States;

    public YouthInfoFormPresenter(Context context){
        this.context=context;
    }

    @Override
    public void setView(YouthInfoFormContract.YouthInfoFormView youthInfoFormView) {
        this.youthInfoFormView=youthInfoFormView;
        Blocks = new ArrayList<>();
        Villages = new ArrayList<>();
        Groups = new ArrayList<>();
        States = new ArrayList<>();
    }

    @Override
    public void populateState() {
        States.clear();
        States = AppDatabase.getDatabaseInstance(context).getVillageDao().getState();
        youthInfoFormView.intialzeState(States.get(0));
    }

    @Override
    public void populateBlock(String selectedState) {
        //Get Villages Data for Blocks AllSpinners
        Blocks.clear();
        Blocks = AppDatabase.getDatabaseInstance(context).getVillageDao().GetStatewiseBlock(selectedState);
        //Blocks.add(0, getString(R.string.selectblock));
        youthInfoFormView.showBlock(Blocks);
    }

    @Override
    public void populateVillage(String selectedBlock) {
      Villages.clear();
      Villages = AppDatabase.getDatabaseInstance(context).getVillageDao().GetVillages(selectedBlock);
      youthInfoFormView.showVillage(Villages);
    }

    @Override
    public void populateGroup(int selectedGroup) {
        Groups.clear();
        Groups = AppDatabase.getDatabaseInstance(context).getGroupDao().GetGroups(selectedGroup);
        youthInfoFormView.showGroup(Groups);

    }

    @Override
    public void addYouthToDB(Youth youthObj) {
        try {
            youthObj.createdBy = AppDatabase.getDatabaseInstance(context).getMetaDataDao().getCrlMetaData();
            AppDatabase.getDatabaseInstance(context).getYouthDao().insertYouth(youthObj);
            //Toast.makeText(YouthInfoForm.this, R.string.recInsertSuccess, Toast.LENGTH_SHORT).show();
            youthInfoFormView.showToast(R.string.recInsertSuccess);

        } catch (Exception e) {
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("AddYouth" + "_" + "Submit");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(ApplicationController.getInstance()).getLogDao().insertLog(log);
            BackupDatabase.backup(ApplicationController.getInstance());

            e.printStackTrace();
            youthInfoFormView.showToast(R.string.recInsertFailed);
//          Toast.makeText(context, R.string.recInsertFailed, Toast.LENGTH_SHORT).show();
        }

    }
}
