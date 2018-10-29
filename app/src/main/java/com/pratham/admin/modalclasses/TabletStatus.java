package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class TabletStatus {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @Expose(serialize = false)
    private Integer id;
    @Expose
    String qrID;
    @Expose
    String loggedCRL_Id;
    @Expose
    String loggedCRL_Name;
    @Expose
    String prathamId;
    @Expose
    String date;
    @Expose
    String serialNo;
    @Expose
    String status;

    @Expose(serialize = false)
    @SerializedName("oldFlag")
    boolean oldFlag = false;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public String getLoggedCRL_Id() {
        return loggedCRL_Id;
    }

    public void setLoggedCRL_Id(String loggedCRL_Id) {
        this.loggedCRL_Id = loggedCRL_Id;
    }

    public String getLoggedCRL_Name() {
        return loggedCRL_Name;
    }

    public void setLoggedCRL_Name(String loggedCRL_Name) {
        this.loggedCRL_Name = loggedCRL_Name;
    }

    public String getPrathamId() {
        return prathamId;
    }

    public void setPrathamId(String prathamId) {
        this.prathamId = prathamId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getQrID() {
        return qrID;
    }

    public void setQrID(String qrID) {
        this.qrID = qrID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getOldFlag() {
        return oldFlag;
    }

    public void setOldFlag(boolean oldFlag) {
        this.oldFlag = oldFlag;
    }
}
