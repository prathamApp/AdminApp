package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Completion;
import com.pratham.admin.modalclasses.GroupSession;
import com.pratham.admin.modalclasses.GroupVisit;

import java.util.List;

@Dao
public interface GroupVisitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCRLVisit(List<GroupVisit> GroupVisitDaoList);

    @Query("DELETE FROM GroupVisit")
    public void deleteAllGroupVisit();

    @Query("SELECT * FROM GroupVisit")
    public List<GroupVisit> getAllGroupVisit();

    @Query("SELECT * FROM GroupVisit WHERE sentFlag=:status")
    public List<GroupVisit> getNewGroupVisits(int status);

    @Query("UPDATE GroupVisit SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);

    @Query("UPDATE GroupVisit SET sentFlag=:pushStatus WHERE GroupVisitID=:gID")
    void updateSentFlag(int pushStatus, String gID);

}
