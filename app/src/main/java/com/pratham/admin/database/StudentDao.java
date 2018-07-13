package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.pratham.admin.modalclasses.Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllStudents(List<Student> studentsList);

    @Update
    public int updateAllStudent(List<Student> studList);

    @Query("DELETE FROM Student")
    public void deleteAllStudents();

    @Query("SELECT * FROM Student")
    public List<Student> getAllStudents();

    @Query("SELECT * FROM student WHERE GroupId=:gID")
    public List<Student> getGroupwiseStudents(String gID);
}
