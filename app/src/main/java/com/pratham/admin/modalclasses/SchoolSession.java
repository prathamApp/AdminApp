package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class SchoolSession {
    @NonNull
    @PrimaryKey
    public String SchoolSessionID;
    public int VillageID;
    public String GroupID;
    public int CoursesAdded;
    public int TopicsAdded;
    public String ReportingDate;
    public String CoachID;
    public String GroupType; // Community/ School
    public int sentFlag;

    @Override
    public String toString() {
        return "SchoolSession{" +
                "SchoolSessionID='" + SchoolSessionID + '\'' +
                ", VillageID=" + VillageID +
                ", GroupID='" + GroupID + '\'' +
                ", CoursesAdded=" + CoursesAdded +
                ", TopicsAdded=" + TopicsAdded +
                ", ReportingDate='" + ReportingDate + '\'' +
                ", CoachID='" + CoachID + '\'' +
                ", GroupType='" + GroupType + '\'' +
                ", sentFlag=" + sentFlag +
                '}';
    }

    @NonNull
    public String getSchoolSessionID() {
        return SchoolSessionID;
    }

    public void setSchoolSessionID(@NonNull String schoolSessionID) {
        SchoolSessionID = schoolSessionID;
    }

    public int getVillageID() {
        return VillageID;
    }

    public void setVillageID(int villageID) {
        VillageID = villageID;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public int getCoursesAdded() {
        return CoursesAdded;
    }

    public void setCoursesAdded(int coursesAdded) {
        CoursesAdded = coursesAdded;
    }

    public int getTopicsAdded() {
        return TopicsAdded;
    }

    public void setTopicsAdded(int topicsAdded) {
        TopicsAdded = topicsAdded;
    }

    public String getReportingDate() {
        return ReportingDate;
    }

    public void setReportingDate(String reportingDate) {
        ReportingDate = reportingDate;
    }

    public String getCoachID() {
        return CoachID;
    }

    public void setCoachID(String coachID) {
        CoachID = coachID;
    }

    public String getGroupType() {
        return GroupType;
    }

    public void setGroupType(String groupType) {
        GroupType = groupType;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}
