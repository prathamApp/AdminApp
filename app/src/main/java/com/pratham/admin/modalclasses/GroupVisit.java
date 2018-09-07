package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class GroupVisit {
    @NonNull
    @PrimaryKey
    @SerializedName("GroupVisitID")
    public String GroupVisitID;

    @SerializedName("VillageID")
    public int VillageID;
    @SerializedName("Village")
    public String Village;

    @SerializedName("DateVisited")
    public String DateVisited;
    @SerializedName("StartTime")
    public String StartTime;
    @SerializedName("EndTime")
    public String EndTime;

    @SerializedName("GroupIDVisited")
    public String GroupIDVisited;
    @SerializedName("PresentGroupIDs")
    public String PresentGroupIDs;
    @SerializedName("WorkCrosscheckedGroupIDs")
    public String WorkCrosscheckedGroupIDs;
    @SerializedName("Group")
    public String Group;

    @SerializedName("CoachPresentInVillage")
    public String CoachPresentInVillage;
    @SerializedName("PresentStudents")
    public String PresentStudents;

    @SerializedName("sentFlag")
    public int sentFlag;

    @Override
    public String toString() {
        return "GroupVisit{" +
                "GroupVisitID='" + GroupVisitID + '\'' +
                ", VillageID=" + VillageID +
                ", Village='" + Village + '\'' +
                ", DateVisited='" + DateVisited + '\'' +
                ", StartTime='" + StartTime + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", GroupIDVisited='" + GroupIDVisited + '\'' +
                ", PresentGroupIDs='" + PresentGroupIDs + '\'' +
                ", WorkCrosscheckedGroupIDs='" + WorkCrosscheckedGroupIDs + '\'' +
                ", Group='" + Group + '\'' +
                ", CoachPresentInVillage='" + CoachPresentInVillage + '\'' +
                ", PresentStudents='" + PresentStudents + '\'' +
                ", sentFlag=" + sentFlag +
                '}';
    }

    @NonNull
    public String getGroupVisitID() {
        return GroupVisitID;
    }

    public void setGroupVisitID(@NonNull String groupVisitID) {
        GroupVisitID = groupVisitID;
    }

    public int getVillageID() {
        return VillageID;
    }

    public void setVillageID(int villageID) {
        VillageID = villageID;
    }

    public String getVillage() {
        return Village;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public String getDateVisited() {
        return DateVisited;
    }

    public void setDateVisited(String dateVisited) {
        DateVisited = dateVisited;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getGroupIDVisited() {
        return GroupIDVisited;
    }

    public void setGroupIDVisited(String groupIDVisited) {
        GroupIDVisited = groupIDVisited;
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

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }

    public String getCoachPresentInVillage() {
        return CoachPresentInVillage;
    }

    public void setCoachPresentInVillage(String coachPresentInVillage) {
        CoachPresentInVillage = coachPresentInVillage;
    }

    public String getPresentStudents() {
        return PresentStudents;
    }

    public void setPresentStudents(String presentStudents) {
        PresentStudents = presentStudents;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}