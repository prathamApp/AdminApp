package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class Village {
    @NonNull
    @PrimaryKey
    @SerializedName("VillageId")
    public String VillageId;
    @SerializedName("VillageCode")
    public String VillageCode;
    @SerializedName("VillageName")
    public String VillageName;
    @SerializedName("Block")
    public String Block;
    @SerializedName("District")
    public String District;
    @SerializedName("State")
    public String State;
    @SerializedName("CRLId")
    public String CRLId;

    public Village(int id, String name) {
        VillageId = String.valueOf(id);
        VillageName = name;
    }

    public Village() {

    }

    @Override
    public String toString() {
        return this.VillageName;
    }

    @NonNull
    public String getVillageId() {
        return VillageId;
    }

    public void setVillageId(@NonNull String villageId) {
        VillageId = villageId;
    }

    public String getVillageCode() {
        return VillageCode;
    }

    public void setVillageCode(String villageCode) {
        VillageCode = villageCode;
    }

    public String getVillageName() {
        return VillageName;
    }

    public void setVillageName(String villageName) {
        VillageName = villageName;
    }

    public String getBlock() {
        return Block;
    }

    public void setBlock(String block) {
        Block = block;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCRLId() {
        return CRLId;
    }

    public void setCRLId(String CRLId) {
        this.CRLId = CRLId;
    }
}
