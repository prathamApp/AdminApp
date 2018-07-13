package com.pratham.admin.modalclasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity
public class Student {
    @SerializedName("GroupId")
    String GroupId;

    @SerializedName("GroupName")
    String GroupName;

    @SerializedName("FullName")
    String FullName;

    @SerializedName("Class")
    String Stud_Class;

    @SerializedName("Age")
    String Age;

    @SerializedName("Gender")
    String Gender;

    @NonNull
    @PrimaryKey
    @SerializedName("StudentId")
    String StudentId;

    @Ignore
    transient boolean  isChecked =false;

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getStud_Class() {
        return Stud_Class;
    }

    public void setStud_Class(String stud_Class) {
        Stud_Class = stud_Class;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    @NonNull
    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(@NonNull String studentId) {
        StudentId = studentId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
