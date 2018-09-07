package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Completion;
import com.pratham.admin.modalclasses.GroupSession;

import java.util.List;

@Dao
public interface GroupSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCRLVisit(List<GroupSession> GroupSessionDaoList);

    @Query("DELETE FROM GroupSession")
    public void deleteAllGroupSession();

    @Query("SELECT * FROM GroupSession")
    public List<GroupSession> getAllGroupSession();

    @Query("SELECT * FROM GroupSession WHERE sentFlag=:status")
    public List<GroupSession> getNewGroupSessions(int status);

    @Query("UPDATE GroupSession SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);

    @Query("UPDATE GroupSession SET sentFlag=:pushStatus WHERE GroupSessionID=:gID")
    void updateSentFlag(int pushStatus, String gID);

}
