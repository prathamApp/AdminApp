package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class Youth {

    @NonNull
    @PrimaryKey
    @SerializedName("YouthId")
    public String youthId;

    @SerializedName("GroupId")
    public String groupId;

    @SerializedName("GroupName")
    public String groupName;

    @SerializedName("FirstName")
    public String firstName;

    @SerializedName("MiddleName")
    public String middleName;

    @SerializedName("LastName")
    public String lastName;

    @SerializedName("PhoneNumber")
    public String phoneNumber;

    @SerializedName("GuardianName")
    public String guardianName;

    @SerializedName("BirthDate")
    public String birthDate;

    @SerializedName("Gender")
    public String gender;

    @SerializedName("MaritalStatus")
    public String maritalStatus;

    @SerializedName("AreYouStudying")
    public int areyoustudying;

    @SerializedName("Education")
    public String education;

    @SerializedName("Occupation")
    public String occupation;

    @SerializedName("HaveSmartphone")
    public int haveSmartphone;

    @SerializedName("UseSmartphone")
    public int useSmartphone;

    @SerializedName("WantToJoin")
    public int wantToJoin;

    @SerializedName("CreatedBy")
    public String createdBy;

    @SerializedName("CreatedOn")
    public String createdOn;

    @SerializedName("IsDeleted")
    public int isDeleted;

    @SerializedName("sentFlag")
    public int sentFlag = 1;

    public Youth() {
    }

    @NonNull
    public String getyouthId() {
        return youthId;
    }

    public void setyouthId(@NonNull String youthId) {
        youthId = youthId;
    }

    public String getgroupId() {
        return groupId;
    }

    public void setgroupId(String groupId) {
        groupId = groupId;
    }

    public String getgroupName() {
        return groupName;
    }

    public void setgroupName(String groupName) {
        groupName = groupName;
    }

    public String getfirstName() {
        return firstName;
    }

    public void setfirstName(String firstName) {
        firstName = firstName;
    }

    public String getmiddleName() {
        return middleName;
    }

    public void setmiddleName(String middleName) {
        middleName = middleName;
    }

    public String getlastName() {
        return lastName;
    }

    public void setlastName(String lastName) {
        lastName = lastName;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setphoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber;
    }

    public String getguardianName() {
        return guardianName;
    }

    public void setguardianName(String guardianName) {
        guardianName = guardianName;
    }

    public String getbirthDate() {
        return birthDate;
    }

    public void setbirthDate(String birthDate) {
        birthDate = birthDate;
    }

    public String getgender() {
        return gender;
    }

    public void setgender(String gender) {
        gender = gender;
    }

    public String getmaritalStatus() {
        return maritalStatus;
    }

    public void setmaritalStatus(String maritalStatus) {
        maritalStatus = maritalStatus;
    }

    public int getAreyoustudying() {
        return areyoustudying;
    }

    public void setAreyoustudying(int areyoustudying) {
        this.areyoustudying = areyoustudying;
    }

    public String geteducation() {
        return education;
    }

    public void seteducation(String education) {
        education = education;
    }

    public String getoccupation() {
        return occupation;
    }

    public void setoccupation(String occupation) {
        occupation = occupation;
    }

    public int getHaveSmartphone() {
        return haveSmartphone;
    }

    public void setHaveSmartphone(int haveSmartphone) {
        this.haveSmartphone = haveSmartphone;
    }

    public int getUseSmartphone() {
        return useSmartphone;
    }

    public void setUseSmartphone(int useSmartphone) {
        this.useSmartphone = useSmartphone;
    }

    public int getWantToJoin() {
        return wantToJoin;
    }

    public void setWantToJoin(int wantToJoin) {
        this.wantToJoin = wantToJoin;
    }

    public String getcreatedBy() {
        return createdBy;
    }

    public void setcreatedBy(String createdBy) {
        createdBy = createdBy;
    }

    public String getcreatedOn() {
        return createdOn;
    }

    public void setcreatedOn(String createdOn) {
        createdOn = createdOn;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }

    public int isDeleted() {
        return isDeleted;
    }

    public void setDeleted(int deleted) {
        isDeleted = deleted;
    }
}
