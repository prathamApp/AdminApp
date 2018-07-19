package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;

@Entity
public class Community {
    public String VillageID;
    public String GroupID;
    public int CourseAdded;
    public int TopicAdded;
    public String StartDate;
    public String EndDate;
    public String CoachID;
    public String Community; // Community/School
    public int CompletedCourseID;
    public int ParentParticipation;
    public int PresentStudent;

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

    public int getCourseAdded() {
        return CourseAdded;
    }

    public void setCourseAdded(int courseAdded) {
        CourseAdded = courseAdded;
    }

    public int getTopicAdded() {
        return TopicAdded;
    }

    public void setTopicAdded(int topicAdded) {
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

    public int getCompletedCourseID() {
        return CompletedCourseID;
    }

    public void setCompletedCourseID(int completedCourseID) {
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
