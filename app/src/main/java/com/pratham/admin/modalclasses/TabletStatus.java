package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class TabletStatus {
    String loggedUserId;
    String prathamId;
    @NonNull
    @PrimaryKey
    String qrID;
    String date;
    String status;
    boolean oldFlag=false;

    public String getLoggedUserId() {
        return loggedUserId;
    }

    public void setLoggedUserId(String loggedUserId) {
        this.loggedUserId = loggedUserId;
    }

    public String getPrathamId() {
        return prathamId;
    }

    public void setPrathamId(String prathamId) {
        this.prathamId = prathamId;
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
