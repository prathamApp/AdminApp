package com.pratham.admin.activities.AddNewGroup;

import android.content.Context;

import com.pratham.admin.R;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Village;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

@EBean
public class AddNewGroupPresenter implements AddNewGroupContract.AddNewGroupPresenter{

    Context context;
    AddNewGroupContract.AddNewGroupView addNewGroupView;
    List<String> Blocks;
    List<Village> Villages;

    public AddNewGroupPresenter(Context context){
        this.context=context;
    }

    @Override
    public void setView(AddNewGroupContract.AddNewGroupView addNewGroupView) {
        this.addNewGroupView=addNewGroupView;
        Blocks = new ArrayList<>();
        Villages = new ArrayList<>();
    }

    @Override
    public void populateBlock(String selectedState) {
        //Get Villages Data for Blocks AllSpinners
        Blocks.clear();
        Blocks = AppDatabase.getDatabaseInstance(context).getVillageDao().GetStatewiseBlock(selectedState);
        //Blocks.add(0, getString(R.string.selectblock));
        addNewGroupView.showBlock(Blocks);
    }

    @Override
    public void populateVillage(String selectedBlock) {
        Villages.clear();
        Villages = AppDatabase.getDatabaseInstance(context).getVillageDao().GetVillages(selectedBlock);
        addNewGroupView.showVillage(Villages);
    }

    @Override
    public void addGroupToDB(Groups groupsObj) {
        groupsObj.ProgramId = Integer.parseInt(AppDatabase.getDatabaseInstance(context).getMetaDataDao().getProgramID());
        groupsObj.CreatedBy = AppDatabase.getDatabaseInstance(context).getMetaDataDao().getCrlMetaData();
        AppDatabase.getDatabaseInstance(context).getGroupDao().insertGroup(groupsObj);
        addNewGroupView.showToast(R.string.recInsertSuccess);
    }

}