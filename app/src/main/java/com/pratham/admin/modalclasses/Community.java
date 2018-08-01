package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class Community {
    @NonNull
    @PrimaryKey
    @SerializedName("CommunityID")
    public String CommunityID;
    @SerializedName("VillageID")
    public String VillageID;
    @SerializedName("GroupID")
    public String GroupID;
    @SerializedName("CourseAdded")
    public String CourseAdded;
    @SerializedName("TopicAdded")
    public String TopicAdded;
    @SerializedName("StartDate")
    public String StartDate;
    @SerializedName("EndDate")
    public String EndDate;
    @SerializedName("CoachID")
    public String CoachID;
    @SerializedName("Community")
    public String Community; // Community/School
    @SerializedName("CompletedCourseID")
    public String CompletedCourseID;
    @SerializedName("ParentParticipation")
    public int ParentParticipation;
    @SerializedName("PresentStudent")
    public int PresentStudent; // not available
    @SerializedName("sentFlag")
    public int sentFlag;

    @Override
    public String toString() {
        return "Community{" +
                "CommunityID='" + CommunityID + '\'' +
                ", VillageID='" + VillageID + '\'' +
                ", GroupID='" + GroupID + '\'' +
                ", CourseAdded='" + CourseAdded + '\'' +
                ", TopicAdded='" + TopicAdded + '\'' +
                ", StartDate='" + StartDate + '\'' +
                ", EndDate='" + EndDate + '\'' +
                ", CoachID='" + CoachID + '\'' +
                ", Community='" + Community + '\'' +
                ", CompletedCourseID='" + CompletedCourseID + '\'' +
                ", ParentParticipation=" + ParentParticipation +
                ", PresentStudent=" + PresentStudent +
                ", sentFlag=" + sentFlag +
                '}';
    }

    @NonNull
    public String getCommunityID() {
        return CommunityID;
    }

    public void setCommunityID(@NonNull String communityID) {
        CommunityID = communityID;
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

    public String getCourseAdded() {
        return CourseAdded;
    }

    public void setCourseAdded(String courseAdded) {
        CourseAdded = courseAdded;
    }

    public String getTopicAdded() {
        return TopicAdded;
    }

    public void setTopicAdded(String topicAdded) {
        TopicAdded = topicAdded;
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

    public String getCoachID() {
        return CoachID;
    }

    public void setCoachID(String coachID) {
        CoachID = coachID;
    }

    public String getCommunity() {
        return Community;
    }

    public void setCommunity(String community) {
        Community = community;
    }

    public String getCompletedCourseID() {
        return CompletedCourseID;
    }

    public void setCompletedCourseID(String completedCourseID) {
        CompletedCourseID = completedCourseID;
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
