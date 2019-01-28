package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class Groups {
    @NonNull
    @PrimaryKey
    @SerializedName("GroupId")
    public String GroupId;
    @SerializedName("GroupName")
    public String GroupName;
    @SerializedName("VillageId")
    public String VillageId;
    @SerializedName("ProgramId")
    public int ProgramId;
    @SerializedName("GroupCode")
    public String GroupCode;
    @SerializedName("SchoolName")
    public String SchoolName;
    @SerializedName("VIllageName")
    public String VIllageName;
    @SerializedName("DeviceId")
    public String DeviceId;

    // new fields db version 4
    @SerializedName("CreatedBy")
    public String CreatedBy;
    @SerializedName("CreatedOn")
    public String CreatedOn;
    @SerializedName("sentFlag")
    public int sentFlag = 1;


    // new Methods
    public Groups() {

    }

    public Groups(String id, String name) {
        GroupId = id;
        GroupName = name;
    }

    @Override
    public String toString() {
        return this.GroupName;
    }

    @NonNull
    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(@NonNull String groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getVillageId() {
        return VillageId;
    }

    public void setVillageId(String villageId) {
        VillageId = villageId;
    }

    public int getProgramId() {
        return ProgramId;
    }

    public void setProgramId(int programId) {
        ProgramId = programId;
    }

    public String getGroupCode() {
        return GroupCode;
    }

    public void setGroupCode(String groupCode) {
        GroupCode = groupCode;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getVIllageName() {
        return VIllageName;
    }

    public void setVIllageName(String VIllageName) {
        this.VIllageName = VIllageName;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
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
}
