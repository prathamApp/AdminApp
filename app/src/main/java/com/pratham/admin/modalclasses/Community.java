package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Community {
    @NonNull
    @PrimaryKey
    public String VillageID;
    public String GroupID;
    public String CourseAdded;
    public String TopicAdded;
    public String StartDate;
    public String EndDate;
    public String CoachID;
    public String Community; // Community/School
    public String CompletedCourseID;
    public int ParentParticipation;
    public int PresentStudent; // not available

    @Override
    public String toString() {
        return "Community{" +
                "VillageID='" + VillageID + '\'' +
                ", GroupID='" + GroupID + '\'' +
                ", CourseAdded=" + CourseAdded +
                ", TopicAdded=" + TopicAdded +
                ", StartDate='" + StartDate + '\'' +
                ", EndDate='" + EndDate + '\'' +
                ", CoachID='" + CoachID + '\'' +
                ", Community='" + Community + '\'' +
                ", CompletedCourseID=" + CompletedCourseID +
                ", ParentParticipation=" + ParentParticipation +
                ", PresentStudent=" + PresentStudent +
                '}';
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
}
