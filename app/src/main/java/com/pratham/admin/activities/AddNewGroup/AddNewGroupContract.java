package com.pratham.admin.activities.AddNewGroup;

import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Village;

import java.util.List;

public interface AddNewGroupContract {

    interface AddNewGroupView{
        void showBlock(List<String> Blocks);
        void showVillage(List<Village> Village);
        void showToast(int message);

    }

    interface AddNewGroupPresenter{
        void populateBlock(String selectedState);
        void populateVillage(String selectedBlock);
        void addGroupToDB(Groups groupsObj);
        void setView(AddNewGroupContract.AddNewGroupView addNewGroupView);
    }
}
