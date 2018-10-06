package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class Completion {
    @NonNull
    @PrimaryKey
    @SerializedName("CompletionID")
    public String CompletionID;
    @SerializedName("GroupType")
    public String GroupType; // Community/School
    @SerializedName("VillageID")
    public String VillageID;
    @SerializedName("ProgramId")
    public String ProgramId;
    @SerializedName("GroupId")
    public String GroupID;
    @SerializedName("CourseCompleted")
    public String CourseCompleted;
    @SerializedName("TopicCompleted")
    public String TopicCompleted;
    @SerializedName("StateDate")
    public String StartDate;
    @SerializedName("EndDate")
    public String EndDate;
    @SerializedName("ParentParticipation")
    public int ParentParticipation;
    @SerializedName("PresentParents")
    public int PresentParents;
    @SerializedName("Event")
    public int Event;
    @SerializedName("CreatedBy")
    public String CreatedBy;

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
                ", Event='" + Event + '\'' +
                ", GroupType='" + GroupType + '\'' +
                ", ParentParticipation=" + ParentParticipation +
                ", PresentParents=" + PresentParents +
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

    public String getVillageID() {
        return VillageID;
    }

    public void setVillageID(String villageID) {
        VillageID = villageID;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getCourseCompleted() {
        return CourseCompleted;
    }

    public void setCourseCompleted(String courseCompleted) {
        CourseCompleted = courseCompleted;
    }

    public String getTopicCompleted() {
        return TopicCompleted;
    }

    public void setTopicCompleted(String topicCompleted) {
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

    public int getEvent() {
        return Event;
    }

    public void setEvent(int event) {
        Event = event;
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

    public int getPresentParents() {
        return PresentParents;
    }

    public void setPresentParents(int presentParents) {
        PresentParents = presentParents;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}
