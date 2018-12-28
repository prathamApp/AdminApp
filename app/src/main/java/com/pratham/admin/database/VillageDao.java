package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Village;

import java.util.List;

@Dao
public interface VillageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllVillages(List<Village> villagesList);

    @Query("DELETE FROM Village")
    public void deleteAllVillages();

    @Query("SELECT * FROM Village")
    public List<Village> getAllVillages();

    @Query("SELECT DISTINCT State FROM Village ORDER BY State ASC")
    public List<String> getState();

    @Query("SELECT DISTINCT Block FROM Village WHERE State=:st ORDER BY Block ASC")
    public List<String> GetStatewiseBlock(String st);

    @Query("SELECT * FROM Village WHERE Block=:bk ORDER BY VillageName ASC")
    public List<Village> GetVillages(String bk);
}
