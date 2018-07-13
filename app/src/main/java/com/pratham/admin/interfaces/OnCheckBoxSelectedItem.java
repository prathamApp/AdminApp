package com.pratham.admin.interfaces;

import com.pratham.admin.modalclasses.Student;

public interface OnCheckBoxSelectedItem {
    public void addStudent(int pos, Student student);
    public void setSelectedRightSide(int pos, Student student);
}
