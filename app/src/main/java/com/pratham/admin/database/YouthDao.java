package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pratham.admin.modalclasses.Youth;

import java.util.List;

@Dao
public interface YouthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllYouths(List<Youth> youthList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertYouth(Youth youth);

    @Query("DELETE FROM Youth")
    public void deleteAllYouths();

    @Query("SELECT * FROM Youth")
    public List<Youth> getAllYouth();

    @Query("SELECT * FROM Youth WHERE sentFlag=:status")
    public List<Youth> getNewYouths(int status);

    @Query("UPDATE Youth SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);
}
