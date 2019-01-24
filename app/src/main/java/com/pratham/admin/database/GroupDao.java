package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Groups;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllGroups(List<Groups> groupsList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertGroup(Groups grp);

    @Query("DELETE FROM Groups")
    public void deleteAllGroups();

    @Query("SELECT * From Groups WHERE VillageId =:vid ORDER BY GroupName ASC")
    public List<Groups> GetGroups(int vid);

    @Query("SELECT * FROM Groups ")
    public List<Groups> getAllGroups();

    @Query("SELECT * FROM Groups WHERE sentFlag=:status")
    public List<Groups> getNewGroups(int status);

    @Query("UPDATE Groups SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);

    @Query("UPDATE Groups SET sentFlag=:pushStatus WHERE GroupId =:gID")
    void updateSentFlag(int pushStatus, String gID);

    @Query("select * from Groups WHERE DeviceID = 'deleted'")
    public List<Groups> GetDeletedGroups();

    @Query("DELETE FROM Groups WHERE DeviceId='deleted'")
    public void removeDeletedGroupRecords();

}
