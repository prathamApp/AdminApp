package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
@Entity
public class TabletManageDevice {
    @NonNull
    @PrimaryKey
    @SerializedName("QR_ID")
    String QR_ID;
    @SerializedName("CRL_ID")
    String assigned_CRL_ID;
    @SerializedName("CRL_Name")
    String assigned_CRL_Name;
    @SerializedName("Pratham_ID")
    String Pratham_ID;
    @SerializedName("Tab_serial_ID")
    String tabSerial_ID;
    @SerializedName("Date")
    String date;
    @SerializedName("CRL_ID_Admin")
    String logged_CRL_ID;

    @SerializedName("is_Damaged")
    String is_Damaged;

    @SerializedName("damageType")
    String damageType;

    @SerializedName("operation_Type")
    String status;

    @SerializedName("comment")
    String comment;




    boolean oldFlag=false;

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
}
