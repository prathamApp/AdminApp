package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Attendance;
import com.pratham.admin.modalclasses.Coach;

import java.util.List;

@Dao
public interface CoachDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCoach(List<Coach> coachList);

    @Query("DELETE FROM Coach")
    public void deleteAllCoaches();

    @Query("SELECT * FROM Coach")
    public List<Coach> getAllCoaches();

    @Query("SELECT * FROM Coach where CoachID =:cID")
    public List<Coach> getCoachByID(String cID);

    @Query("SELECT * FROM Coach where CoachVillageID =:vID")
    public List<Coach> getCoachByVillageID(String vID);

    @Query("UPDATE Coach SET CoachActive = :cActive, EndDate = :eDate WHERE CoachID =:cID")
    void updateCoachStatus(int cActive, String eDate, String cID);

    @Query("UPDATE Coach SET sentFlag=:pushStatus WHERE CoachID =:cID")
    void updateSentFlag(int pushStatus, String cID);

    @Query("SELECT * FROM Coach WHERE sentFlag=:status")
    public List<Coach> getNewCoaches(int status);

    @Query("UPDATE Coach SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);
}
