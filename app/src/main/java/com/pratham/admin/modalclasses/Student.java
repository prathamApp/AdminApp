package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class Student {
    @SerializedName("GroupId")
    public String GroupId;

    @SerializedName("GroupName")
    public String GroupName;

    @SerializedName("FullName")
    public String FullName;

    @SerializedName("Class")
    public String Stud_Class;

    //running Age calculated
    @SerializedName("Age")
    public String Age;

    @SerializedName("Gender")
    public String Gender;

    @SerializedName("sentFlag")
    public int sentFlag = 1;

    @NonNull
    @PrimaryKey
    @SerializedName("StudentId")
    public String StudentId;

    @Ignore
    transient boolean isChecked = false;

    // new fields db version 4
    @SerializedName("FirstName")
    public String FirstName;
    @SerializedName("MiddleName")
    public String MiddleName;
    @SerializedName("LastName")
    public String LastName;
    @SerializedName("CreatedBy")
    public String CreatedBy;
    @SerializedName("CreatedOn")
    public String CreatedOn;
    @SerializedName("UpdatedDate")
    public String UpdatedDate;
    @SerializedName("DOB")
    public String DOB;

    @SerializedName("SchoolType")
    public String SchoolType;
    @SerializedName("GuardianName")
    public String GuardianName;

    @SerializedName("PhoneType")
    public String phoneType;

    @SerializedName("PhoneNumber")
    public String phoneNo;

    @SerializedName("PhoneOwner")
    public String relation_with_phone_owner;
    // new methods
    public Student() {
    }

    public Student(String id, String name) {
        StudentId = id;
        FirstName = name;
    }

    @Override
    public String toString() {
        return this.FirstName;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getStud_Class() {
        return Stud_Class;
    }

    public void setStud_Class(String stud_Class) {
        Stud_Class = stud_Class;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    @NonNull
    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(@NonNull String studentId) {
        StudentId = studentId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getSchoolType() {
        return SchoolType;
    }

    public void setSchoolType(String schoolType) {
        SchoolType = schoolType;
    }

    public String getGuardianName() {
        return GuardianName;
    }

    public void setGuardianName(String guardianName) {
        GuardianName = guardianName;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getRelation_with_phone_owner() {
        return relation_with_phone_owner;
    }

    public void setRelation_with_phone_owner(String relation_with_phone_owner) {
        this.relation_with_phone_owner = relation_with_phone_owner;
    }
}
