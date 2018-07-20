package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class CRLVisit {
    @NonNull
    @PrimaryKey
    public String VisitID;
    public int VillageID;
    public String DateVisited;
    public String GroupIDVisited;
    public String CoachPresentInVillage;
    public String CoachPresentWithGroup;
    public String PresentGroupIDs;
    public String WorkCrosscheckedGroupIDs;
    public String PresentStudents;
    public String Village;
    public String Group;

    @Override
    public String toString() {
        return "CRLVisit{" +
                "VisitID='" + VisitID + '\'' +
                ", VillageID=" + VillageID +
                ", DateVisited='" + DateVisited + '\'' +
                ", GroupIDVisited='" + GroupIDVisited + '\'' +
                ", CoachPresentInVillage='" + CoachPresentInVillage + '\'' +
                ", CoachPresentWithGroup='" + CoachPresentWithGroup + '\'' +
                ", PresentGroupIDs='" + PresentGroupIDs + '\'' +
                ", WorkCrosscheckedGroupIDs='" + WorkCrosscheckedGroupIDs + '\'' +
                ", PresentStudents='" + PresentStudents + '\'' +
                ", Village='" + Village + '\'' +
                ", Group='" + Group + '\'' +
                '}';
    }

    public String getVisitID() {
        return VisitID;
    }

    public void setVisitID(String visitID) {
        VisitID = visitID;
    }

    public int getVillageID() {
        return VillageID;
    }

    public void setVillageID(int villageID) {
        VillageID = villageID;
    }

    public String getDateVisited() {
        return DateVisited;
    }

    public void setDateVisited(String dateVisited) {
        DateVisited = dateVisited;
    }

    public String getGroupIDVisited() {
        return GroupIDVisited;
    }

    public void setGroupIDVisited(String groupIDVisited) {
        GroupIDVisited = groupIDVisited;
    }

    public String getCoachPresentInVillage() {
        return CoachPresentInVillage;
    }

    public void setCoachPresentInVillage(String coachPresentInVillage) {
        CoachPresentInVillage = coachPresentInVillage;
    }

    public String getCoachPresentWithGroup() {
        return CoachPresentWithGroup;
    }

    public void setCoachPresentWithGroup(String coachPresentWithGroup) {
        CoachPresentWithGroup = coachPresentWithGroup;
    }

    public String getPresentGroupIDs() {
        return PresentGroupIDs;
    }

    public void setPresentGroupIDs(String presentGroupIDs) {
        PresentGroupIDs = presentGroupIDs;
    }

    public String getWorkCrosscheckedGroupIDs() {
        return WorkCrosscheckedGroupIDs;
    }

    public void setWorkCrosscheckedGroupIDs(String workCrosscheckedGroupIDs) {
        WorkCrosscheckedGroupIDs = workCrosscheckedGroupIDs;
    }

    public String getPresentStudents() {
        return PresentStudents;
    }

    public void setPresentStudents(String presentStudents) {
        PresentStudents = presentStudents;
    }

    public String getVillage() {
        return Village;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }
}
