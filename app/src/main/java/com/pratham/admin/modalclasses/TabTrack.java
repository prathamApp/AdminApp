package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class TabTrack {
    @NonNull
    @PrimaryKey
    @SerializedName("QR_ID")
    String QR_ID;
    @SerializedName("CRL_ID")
    String CRL_ID;
    @SerializedName("CRL_Name")
    String CRL_Name;
    @SerializedName("State")
    String State;
    @SerializedName("Pratham_ID")
    String Pratham_ID;
    @SerializedName("Date")
    String date;

    boolean oldFlag=false;

    public boolean getOldFlag() {
        return oldFlag;
    }

    public void setOldFlag(boolean oldFlag) {
        this.oldFlag = oldFlag;
    }


    /*  @SerializedName("LoggedIn_CRL")
    String LoggedIn_CRL;*/

  /*  public String getLoggedIn_CRL() {
        return LoggedIn_CRL;
    }

    public void setLoggedIn_CRL(String loggedIn_CRL) {
        LoggedIn_CRL = loggedIn_CRL;
    }
*/

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @NonNull
    public String getQR_ID() {
        return QR_ID;
    }

    public void setQR_ID(@NonNull String QR_ID) {
        this.QR_ID = QR_ID;
    }

    public String getCRL_ID() {
        return CRL_ID;
    }

    public void setCRL_ID(String CRL_ID) {
        this.CRL_ID = CRL_ID;
    }

    public String getCRL_Name() {
        return CRL_Name;
    }

    public void setCRL_Name(String CRL_Name) {
        this.CRL_Name = CRL_Name;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPratham_ID() {
        return Pratham_ID;
    }

    public void setPratham_ID(String pratham_ID) {
        Pratham_ID = pratham_ID;
    }

    public String getSerial_NO() {
        return Serial_NO;
    }

    public void setSerial_NO(String serial_NO) {
        Serial_NO = serial_NO;
    }

    @SerializedName("Serial_NO")

    String Serial_NO;

}
