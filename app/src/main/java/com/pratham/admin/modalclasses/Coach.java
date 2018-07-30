package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Coach {

    @NonNull
    @PrimaryKey
    public String CoachID;
    public String CoachName;
    public int CoachAge;
    public String CoachGender;
    public String CoachOccupation;
    public String CoachSpeciality;
    public String CoachSubjectExpert;
    public String CoachEducation;
    public String CoachGroupID;
    public String StartDate;
    public String EndDate;
    public int CoachActive;
    public String CreatedBy;
    public String CreatedDate;
    public int sentFlag;

    @Override
    public String toString() {
        return "Coach{" +
                "CoachID='" + CoachID + '\'' +
                ", CoachName='" + CoachName + '\'' +
                ", CoachAge=" + CoachAge +
                ", CoachGender='" + CoachGender + '\'' +
                ", CoachOccupation='" + CoachOccupation + '\'' +
                ", CoachSpeciality='" + CoachSpeciality + '\'' +
                ", CoachSubjectExpert='" + CoachSubjectExpert + '\'' +
                ", CoachEducation='" + CoachEducation + '\'' +
                ", CoachGroupID='" + CoachGroupID + '\'' +
                ", StartDate='" + StartDate + '\'' +
                ", EndDate='" + EndDate + '\'' +
                ", CoachActive=" + CoachActive +
                ", CreatedBy='" + CreatedBy + '\'' +
                ", CreatedDate='" + CreatedDate + '\'' +
                ", sentFlag=" + sentFlag +
                '}';
    }

    @NonNull
    public String getCoachID() {
        return CoachID;
    }

    public void setCoachID(@NonNull String coachID) {
        CoachID = coachID;
    }

    public String getCoachName() {
        return CoachName;
    }

    public void setCoachName(String coachName) {
        CoachName = coachName;
    }

    public int getCoachAge() {
        return CoachAge;
    }

    public void setCoachAge(int coachAge) {
        CoachAge = coachAge;
    }

    public String getCoachGender() {
        return CoachGender;
    }

    public void setCoachGender(String coachGender) {
        CoachGender = coachGender;
    }

    public String getCoachOccupation() {
        return CoachOccupation;
    }

    public void setCoachOccupation(String coachOccupation) {
        CoachOccupation = coachOccupation;
    }

    public String getCoachSpeciality() {
        return CoachSpeciality;
    }

    public void setCoachSpeciality(String coachSpeciality) {
        CoachSpeciality = coachSpeciality;
    }

    public String getCoachSubjectExpert() {
        return CoachSubjectExpert;
    }

    public void setCoachSubjectExpert(String coachSubjectExpert) {
        CoachSubjectExpert = coachSubjectExpert;
    }

    public String getCoachEducation() {
        return CoachEducation;
    }

    public void setCoachEducation(String coachEducation) {
        CoachEducation = coachEducation;
    }

    public String getCoachGroupID() {
        return CoachGroupID;
    }

    public void setCoachGroupID(String coachGroupID) {
        CoachGroupID = coachGroupID;
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

    public int getCoachActive() {
        return CoachActive;
    }

    public void setCoachActive(int coachActive) {
        CoachActive = coachActive;
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
