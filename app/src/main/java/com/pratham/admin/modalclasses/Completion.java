package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class Completion {
    @NonNull
    @PrimaryKey
    @SerializedName("CompletionID")
    public String CompletionID;
    @SerializedName("VillageID")
    public int VillageID;
    @SerializedName("GroupID")
    public String GroupID;
    @SerializedName("CourseCompleted")
    public int CourseCompleted;
    @SerializedName("TopicCompleted")
    public int TopicCompleted;
    @SerializedName("StartDate")
    public String StartDate;
    @SerializedName("EndDate")
    public String EndDate;
    @SerializedName("GroupType")
    public String GroupType; // Community/School
    @SerializedName("ParentParticipation")
    public int ParentParticipation;
    @SerializedName("PresentStudent")
    public int PresentStudent;
    @SerializedName("sentFlag")
    public int sentFlag;

    @Override
    public String toString() {
        return "Completion{" +
                "CompletionID='" + CompletionID + '\'' +
                ", VillageID=" + VillageID +
                ", GroupID='" + GroupID + '\'' +
                ", CourseCompleted=" + CourseCompleted +
                ", TopicCompleted=" + TopicCompleted +
                ", StartDate='" + StartDate + '\'' +
                ", EndDate='" + EndDate + '\'' +
                ", GroupType='" + GroupType + '\'' +
                ", ParentParticipation=" + ParentParticipation +
                ", PresentStudent=" + PresentStudent +
                ", sentFlag=" + sentFlag +
                '}';
    }

    @NonNull
    public String getCompletionID() {
        return CompletionID;
    }

    public void setCompletionID(@NonNull String completionID) {
        CompletionID = completionID;
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

    public int getCourseCompleted() {
        return CourseCompleted;
    }

    public void setCourseCompleted(int courseCompleted) {
        CourseCompleted = courseCompleted;
    }

    public int getTopicCompleted() {
        return TopicCompleted;
    }

    public void setTopicCompleted(int topicCompleted) {
        TopicCompleted = topicCompleted;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getGroupType() {
        return GroupType;
    }

    public void setGroupType(String groupType) {
        GroupType = groupType;
    }

    public int getParentParticipation() {
        return ParentParticipation;
    }

    public void setParentParticipation(int parentParticipation) {
        ParentParticipation = parentParticipation;
    }

    public int getPresentStudent() {
        return PresentStudent;
    }

    public void setPresentStudent(int presentStudent) {
        PresentStudent = presentStudent;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}
