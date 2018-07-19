package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Coach;

import java.util.List;

@Dao
public interface CoachDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCoach(List<Coach> coachList);

    @Query("DELETE FROM Coach")
    public void deleteAllCoaches();

    @Query("SELECT * FROM Coach")
    public List<Coach> getAllCoaches();
}
