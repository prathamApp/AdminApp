package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/*
WS : Worksheet
OQ : Oral Questions

Values Description :-
Not Attempted : 0
Can Do : 1
Need Help : 2*/

@Entity
public class ECEAsmt {

    // Basic Details
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @SerializedName("ECEAsmtID")
    public int ECEAsmtID;
    @SerializedName("StudentId")
    public String StudentId;
    @SerializedName("AsmtType")
    public int AsmtType;

    // Date & Time
    @SerializedName("Date")
    public String Date;
    @SerializedName("StartTime")
    public String StartTime;
    @SerializedName("EndTime")
    public String EndTime;

    // Activities
    @SerializedName("ActMatchingCards")
    public int ActMatchingCards;
    @SerializedName("ActSequencingCards")
    public int ActSequencingCards;
    @SerializedName("ActNumberReco")
    public int ActNumberReco;
    @SerializedName("ActWordReco")
    public int ActWordReco;

    // Worksheet
    @SerializedName("WS11a")
    public int WS11a;
    @SerializedName("WS11b")
    public int WS11b;
    @SerializedName("WS12a")
    public int WS12a;
    @SerializedName("WS12b")
    public int WS12b;

    // Oral Questions
    @SerializedName("OQ11")
    public int OQ11;
    @SerializedName("OQ12")
    public int OQ12;
    @SerializedName("OQ13")
    public int OQ13;
    @SerializedName("OQ14")
    public int OQ14;

    // Sent Flag
    @SerializedName("sentFlag")
    public int sentFlag = 0;

    @Override
    public String toString() {
        return "ECEAsmt{" +
                "ECEAsmtID=" + ECEAsmtID +
                ", StudentId='" + StudentId + '\'' +
                ", AsmtType=" + AsmtType +
                ", Date='" + Date + '\'' +
                ", StartTime='" + StartTime + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", ActMatchingCards=" + ActMatchingCards +
                ", ActSequencingCards=" + ActSequencingCards +
                ", ActNumberReco=" + ActNumberReco +
                ", ActWordReco=" + ActWordReco +
                ", WS11a=" + WS11a +
                ", WS11b=" + WS11b +
                ", WS12a=" + WS12a +
                ", WS12b=" + WS12b +
                ", OQ11=" + OQ11 +
                ", OQ12=" + OQ12 +
                ", OQ13=" + OQ13 +
                ", OQ14=" + OQ14 +
                ", sentFlag=" + sentFlag +
                '}';
    }

    public int getECEAsmtID() {
        return ECEAsmtID;
    }

    public void setECEAsmtID(int ECEAsmtID) {
        this.ECEAsmtID = ECEAsmtID;
    }

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public int getAsmtType() {
        return AsmtType;
    }

    public void setAsmtType(int asmtType) {
        AsmtType = asmtType;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public int getActMatchingCards() {
        return ActMatchingCards;
    }

    public void setActMatchingCards(int actMatchingCards) {
        ActMatchingCards = actMatchingCards;
    }

    public int getActSequencingCards() {
        return ActSequencingCards;
    }

    public void setActSequencingCards(int actSequencingCards) {
        ActSequencingCards = actSequencingCards;
    }

    public int getActNumberReco() {
        return ActNumberReco;
    }

    public void setActNumberReco(int actNumberReco) {
        ActNumberReco = actNumberReco;
    }

    public int getActWordReco() {
        return ActWordReco;
    }

    public void setActWordReco(int actWordReco) {
        ActWordReco = actWordReco;
    }

    public int getWS11a() {
        return WS11a;
    }

    public void setWS11a(int WS11a) {
        this.WS11a = WS11a;
    }

    public int getWS11b() {
        return WS11b;
    }

    public void setWS11b(int WS11b) {
        this.WS11b = WS11b;
    }

    public int getWS12a() {
        return WS12a;
    }

    public void setWS12a(int WS12a) {
        this.WS12a = WS12a;
    }

    public int getWS12b() {
        return WS12b;
    }

    public void setWS12b(int WS12b) {
        this.WS12b = WS12b;
    }

    public int getOQ11() {
        return OQ11;
    }

    public void setOQ11(int OQ11) {
        this.OQ11 = OQ11;
    }

    public int getOQ12() {
        return OQ12;
    }

    public void setOQ12(int OQ12) {
        this.OQ12 = OQ12;
    }

    public int getOQ13() {
        return OQ13;
    }

    public void setOQ13(int OQ13) {
        this.OQ13 = OQ13;
    }

    public int getOQ14() {
        return OQ14;
    }

    public void setOQ14(int OQ14) {
        this.OQ14 = OQ14;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}