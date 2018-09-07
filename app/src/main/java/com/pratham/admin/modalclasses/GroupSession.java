package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class GroupSession {
    @NonNull
    @PrimaryKey
    @SerializedName("GroupSessionID")
    public String GroupSessionID;

    @SerializedName("VillageID")
    public int VillageID;
    @SerializedName("Village")
    public String Village;

    @SerializedName("GroupIDVisited")
    public String GroupIDVisited;
    @SerializedName("Group")
    public String Group;

    @SerializedName("DateVisited")
    public String DateVisited;
    @SerializedName("StartTime")
    public String StartTime;
    @SerializedName("EndTime")
    public String EndTime;

    @SerializedName("CoachPresentInVillage")
    public String CoachPresentInVillage;
    @SerializedName("WorkCrosscheckedGroupIDs")
    public String WorkCrosscheckedGroupIDs;
    @SerializedName("PresentStudents")
    public String PresentStudents;

    @SerializedName("sentFlag")
    public int sentFlag;

    @Override
    public String toString() {
        return "GroupSession{" +
                "GroupSessionID='" + GroupSessionID + '\'' +
                ", VillageID=" + VillageID +
                ", Village='" + Village + '\'' +
                ", GroupIDVisited='" + GroupIDVisited + '\'' +
                ", Group='" + Group + '\'' +
                ", DateVisited='" + DateVisited + '\'' +
                ", StartTime='" + StartTime + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", CoachPresentInVillage='" + CoachPresentInVillage + '\'' +
                ", WorkCrosscheckedGroupIDs='" + WorkCrosscheckedGroupIDs + '\'' +
                ", PresentStudents='" + PresentStudents + '\'' +
                ", sentFlag=" + sentFlag +
                '}';
    }

    @NonNull
    public String getGroupSessionID() {
        return GroupSessionID;
    }

    public void setGroupSessionID(@NonNull String groupSessionID) {
        GroupSessionID = groupSessionID;
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

    public String getGroupIDVisited() {
        return GroupIDVisited;
    }

    public void setGroupIDVisited(String groupIDVisited) {
        GroupIDVisited = groupIDVisited;
    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
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

    public String getCoachPresentInVillage() {
        return CoachPresentInVillage;
    }

    public void setCoachPresentInVillage(String coachPresentInVillage) {
        CoachPresentInVillage = coachPresentInVillage;
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

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}
