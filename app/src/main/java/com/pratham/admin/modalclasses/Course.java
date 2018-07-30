package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

@Entity
public class Course {
    @NonNull
    @PrimaryKey
    @SerializedName("CourseId")
    public String CourseID;
    @SerializedName("CourseName")
    public String CourseName;
    @SerializedName("CourseIdInPos")
    public String CourseIdInPos;
    @SerializedName("CourseCode")
    public String CourseCode;
    @SerializedName("CourseSubject")
    public String CourseSubject;
    @SerializedName("CourseLang")
    public String CourseLang;
    @SerializedName("IsDelete")
    public boolean isDelete;
    @SerializedName("lstTopic")
    @TypeConverters(JSONArrayToString.class)
    public JsonArray listTopic;
    public int sentFlag;

    @Override
    public String toString() {
        return "Course{" +
                "CourseID='" + CourseID + '\'' +
                ", CourseName='" + CourseName + '\'' +
                ", CourseIdInPos='" + CourseIdInPos + '\'' +
                ", CourseCode='" + CourseCode + '\'' +
                ", CourseSubject='" + CourseSubject + '\'' +
                ", CourseLang='" + CourseLang + '\'' +
                ", isDelete=" + isDelete +
                ", listTopic=" + listTopic +
                ", sentFlag=" + sentFlag +
                '}';
    }

    @NonNull
    public String getCourseID() {
        return CourseID;
    }

    public void setCourseID(@NonNull String courseID) {
        CourseID = courseID;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getCourseIdInPos() {
        return CourseIdInPos;
    }

    public void setCourseIdInPos(String courseIdInPos) {
        CourseIdInPos = courseIdInPos;
    }

    public String getCourseCode() {
        return CourseCode;
    }

    public void setCourseCode(String courseCode) {
        CourseCode = courseCode;
    }

    public String getCourseSubject() {
        return CourseSubject;
    }

    public void setCourseSubject(String courseSubject) {
        CourseSubject = courseSubject;
    }

    public String getCourseLang() {
        return CourseLang;
    }

    public void setCourseLang(String courseLang) {
        CourseLang = courseLang;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public JsonArray getListTopic() {
        return listTopic;
    }

    public void setListTopic(JsonArray listTopic) {
        this.listTopic = listTopic;
    }

    public int getSentFlag() {
        return sentFlag;
    }

    public void setSentFlag(int sentFlag) {
        this.sentFlag = sentFlag;
    }
}
