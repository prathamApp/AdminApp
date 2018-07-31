package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Attendance;

import java.util.List;

@Dao
public interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAttendance(List<Attendance> attendancesList);

    @Query("DELETE FROM Attendance")
    public void deleteAllAttendances();

    @Query("SELECT * FROM Attendance")
    public List<Attendance> getAllAttendances();

    @Query("UPDATE Attendance SET sentFlag=:pushStatus WHERE AttendanceID=:aID")
    void updateSentFlag(int pushStatus, String aID);

}
