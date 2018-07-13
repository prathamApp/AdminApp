package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.TempStudent;

import java.util.List;

@Dao
public interface TempStudentdao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTempStudent(List<TempStudent> tempChangesList);

    @Query("DELETE FROM TempStudent")
    public void deleteTempStudent();

    @Query("SELECT * FROM TempStudent")
    public List<TempStudent> getAllempStudent();

}
