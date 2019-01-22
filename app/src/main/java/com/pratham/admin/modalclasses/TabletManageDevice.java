package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class TabletManageDevice {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @Expose(serialize = false)
    private Integer id;

    @Expose
    @SerializedName("QR_ID")
    String QR_ID;

    @Expose
    @SerializedName("Pratham_ID")
    String Pratham_ID;

    @Expose
    @SerializedName("CRL_ID")
    String assigned_CRL_ID;

    @Expose
    @SerializedName("CRL_Name")
    String assigned_CRL_Name;

    @Expose
    @SerializedName("Tab_serial_ID")
    String tabSerial_ID;

    @Expose
    @SerializedName("Date")
    String date;

    @Expose
    @SerializedName("CRL_ID_LoggedIN")
    String logged_CRL_ID;


    @Expose
    @SerializedName("CRL_NAME_LoggedIN")
    String logged_CRL_NAME;

    @Expose
    @SerializedName("is_Damaged")
    String is_Damaged;

    @Expose
    @SerializedName("damageType")
    String damageType;

    @Expose
    @SerializedName("operation_Type")
    String status;

    @Expose
    @SerializedName("comment")
    String comment;

    @Expose
    @SerializedName("newPrathamID")
    String newPrathamID;

    @Expose
    @SerializedName("newQrID")
    String newQrID;

    @Expose
    @SerializedName("new_Tab_serial_ID")
    String new_Tab_serial_ID;

    @Expose(serialize = false)
    boolean oldFlag = false;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public boolean getOldFlag() {
        return oldFlag;
    }

    public void setOldFlag(boolean oldFlag) {
        this.oldFlag = oldFlag;
    }

    @NonNull
    public String getQR_ID() {
        return QR_ID;
    }

    public void setQR_ID(@NonNull String QR_ID) {
        this.QR_ID = QR_ID;
    }

    public String getAssigned_CRL_ID() {
        return assigned_CRL_ID;
    }

    public void setAssigned_CRL_ID(String assigned_CRL_ID) {
        this.assigned_CRL_ID = assigned_CRL_ID;
    }

    public String getAssigned_CRL_Name() {
        return assigned_CRL_Name;
    }

    public void setAssigned_CRL_Name(String assigned_CRL_Name) {
        this.assigned_CRL_Name = assigned_CRL_Name;
    }


    public String getPratham_ID() {
        return Pratham_ID;
    }

    public void setPratham_ID(String pratham_ID) {
        Pratham_ID = pratham_ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLogged_CRL_ID() {
        return logged_CRL_ID;
    }

    public void setLogged_CRL_ID(String logged_CRL_ID) {
        this.logged_CRL_ID = logged_CRL_ID;
    }

    public String getLogged_CRL_NAME() {
        return logged_CRL_NAME;
    }

    public void setLogged_CRL_NAME(String logged_CRL_NAME) {
        this.logged_CRL_NAME = logged_CRL_NAME;
    }

    public String getIs_Damaged() {
        return is_Damaged;
    }

    public void setIs_Damaged(String is_Damaged) {
        this.is_Damaged = is_Damaged;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTabSerial_ID() {
        return tabSerial_ID;
    }

    public void setTabSerial_ID(String tabSerial_ID) {
        this.tabSerial_ID = tabSerial_ID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNewPrathamID() {
        return newPrathamID;
    }

    public void setNewPrathamID(String newPrathamID) {
        this.newPrathamID = newPrathamID;
    }

    public String getNewQrID() {
        return newQrID;
    }

    public void setNewQrID(String newQrID) {
        this.newQrID = newQrID;
    }

    public String getNew_Tab_serial_ID() {
        return new_Tab_serial_ID;
    }

    public void setNew_Tab_serial_ID(String new_Tab_serial_ID) {
        this.new_Tab_serial_ID = new_Tab_serial_ID;
    }
}
