package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.TabTrack;

import java.util.List;

@Dao
public interface TabTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertTabTrack(TabTrack tabTrack);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllTabTrack(List<TabTrack> tabTrack);

    @Query("SELECT * FROM TabTrack WHERE QR_ID=:id")
    public List<TabTrack> checkExistance(String id);

    @Query("SELECT * FROM TabTrack")
    public List<TabTrack> getAllTabTrack();

    @Query("DELETE FROM tabtrack")
    public void deleteAllTabTracks();

    @Query("DELETE FROM tabtrack WHERE QR_ID=:qrId")
    public void deleteTabTracks(String qrId);
}
