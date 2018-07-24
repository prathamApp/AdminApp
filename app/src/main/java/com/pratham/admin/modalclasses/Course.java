package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Course {
    @NonNull
    @PrimaryKey
    public String CourseID;
    public String CourseName;
    public String CourseIdInPos;
    public String CourseCode;
    public String CourseSubject;
    public String CourseLang;
    public int isDelete;
    public String listTopic;

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
                ", listTopic='" + listTopic + '\'' +
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

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getListTopic() {
        return listTopic;
    }

    public void setListTopic(String listTopic) {
        this.listTopic = listTopic;
    }
}
