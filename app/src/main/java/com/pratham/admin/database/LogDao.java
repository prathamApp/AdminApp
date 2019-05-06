package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Modal_Log;

import java.util.List;


@Dao
public interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertLog(Modal_Log log);

    @Query("DELETE FROM Logs")
    public void deleteLogs();

    @Query("select * from Logs")
    public List<Modal_Log> getAllLogs();

    @Query("select * from Logs where sentFlag=0")
    public List<Modal_Log> getPushAllLogs();

    @Query("select * from Logs where sentFlag=0 AND sessionId=:s_id")
    public List<Modal_Log> getAllLogs(String s_id);

    @Query("update Logs set sentFlag=1 where sentFlag=0")
    public void setSentFlag();

    @Query("SELECT * FROM Logs WHERE sentFlag=:status")
    public List<Modal_Log> getAllLogs(int status);

    @Query("UPDATE Logs SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);
}