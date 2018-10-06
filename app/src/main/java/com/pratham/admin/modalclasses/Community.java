package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class Community {
    @NonNull
    @PrimaryKey
    @SerializedName("CommunityID")
    public String CommunityID;
    @SerializedName("Community")
    public String Community; // Community/School
    @SerializedName("VillageID")
    public String VillageID;
    @SerializedName("ProgramId")
    public String ProgramId;
    @SerializedName("GroupId")
    public String GroupID;
    @SerializedName("CourseAdded")
    public String CourseAdded;
    @SerializedName("TopicAdded")
    public String TopicAdded;
    @SerializedName("CoachId")
    public String CoachID;
    @SerializedName("StartDate")
    public String StartDate;
    @SerializedName("EndDate")
    public String EndDate;
    @SerializedName("ParentParticipation")
    public int ParentParticipation;
    @SerializedName("PresentStudent")
    public int PresentStudent; // not available

    @SerializedName("AddedCourseID")
    public String AddedCourseID;// Courses IDs
    @SerializedName("AddedTopicsID")
    public String AddedTopicsID;// Topics IDs

    @SerializedName("CreatedBy")
    public String CreatedBy;
    @SerializedName("CreatedDatet")
    public String CreatedDate;

    @SerializedName("sentFlag")
    public int sentFlag;


    @Override
    public String toString() {
        return "Community{" + "CommunityID='" + CommunityID + '\'' + ", Community='" + Community + '\'' + ", VillageID='" + VillageID + '\'' + ", GroupID='" + GroupID + '\'' + ", CourseAdded='" + CourseAdded + '\'' + ", TopicAdded='" + TopicAdded + '\'' + ", CoachID='" + CoachID + '\'' + ", StartDate='" + StartDate + '\'' + ", EndDate='" + EndDate + '\'' + ", ParentParticipation=" + ParentParticipation + ", PresentStudent=" + PresentStudent + ", AddedCourseID='" + AddedCourseID + '\'' + ", AddedTopicsID='" + AddedTopicsID + '\'' + ", CreatedBy='" + CreatedBy + '\'' + ", CreatedDate='" + CreatedDate + '\'' + ", sentFlag=" + sentFlag + '}';
    }

    @NonNull
    public String getCommunityID() {
        return CommunityID;
    }

    public void setCommunityID(@NonNull String communityID) {
        CommunityID = communityID;
    }

    public String getCommunity() {
        return Community;
    }

    public void setCommunity(String community) {
        Community = community;
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

    public String getCoachID() {
        return CoachID;
    }

    public void setCoachID(String coachID) {
        CoachID = coachID;
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

    public String getAddedCourseID() {
        return AddedCourseID;
    }

    public void setAddedCourseID(String addedCourseID) {
        AddedCourseID = addedCourseID;
    }

    public String getAddedTopicsID() {
        return AddedTopicsID;
    }

    public void setAddedTopicsID(String addedTopicsID) {
        AddedTopicsID = addedTopicsID;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}
