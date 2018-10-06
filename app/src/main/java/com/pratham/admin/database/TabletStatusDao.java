package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.TabTrack;
import com.pratham.admin.modalclasses.TabletStatus;

import java.util.List;
@Dao
public interface TabletStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertTabTrack(TabletStatus tabletStatus);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllTabletStatus(List<TabletStatus> tabletStatuse);

    @Query("SELECT * FROM TabletStatus WHERE qrID=:id")
    public List<TabletStatus> checkExistance(String id);

    @Query("SELECT * FROM TabletStatus")
    public List<TabletStatus> getAllTabletStatus();

    @Query("DELETE FROM TabletStatus")
    public void deleteAllTabletStatus();

    @Query("DELETE FROM TabletStatus WHERE qrID=:qrId")
    public void deleteTabTabletStatus(String qrId);
}
