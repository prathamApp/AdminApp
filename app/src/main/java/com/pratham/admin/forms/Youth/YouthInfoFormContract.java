package com.pratham.admin.forms.Youth;

import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.modalclasses.Youth;

import java.util.List;

public interface YouthInfoFormContract {

    interface YouthInfoFormView{

        void showBlock(List<String> Blocks);
        void showVillage(List<Village> Village);
        void showGroup(List<Groups> Group);
        void showToast(int message);
        void intialzeState(String state);
    }

    interface YouthInfoFormPresenter{

        void populateState();
        void populateBlock(String selectedState);
        void populateVillage(String selectedBlock);
        void populateGroup(int selectedGroup);
        void addYouthToDB(Youth youthObj);
        void setView(YouthInfoFormContract.YouthInfoFormView youthInfoFormView);
    }
}
