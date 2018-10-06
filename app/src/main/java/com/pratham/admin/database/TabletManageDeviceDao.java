package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.TabTrack;
import com.pratham.admin.modalclasses.TabletManageDevice;

import java.util.List;
@Dao
public interface TabletManageDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insertTabletManageDevice(TabletManageDevice tabletManageDevice);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTabletAllManageDevice(List<TabletManageDevice> tabletManageDevices);

    @Query("SELECT * FROM TabletManageDevice WHERE QR_ID=:id")
    public List<TabletManageDevice> checkExistanceTabletManageDevice(String id);

    @Query("SELECT * FROM TabletManageDevice")
    public List<TabletManageDevice> getAllTabletManageDevice();

    @Query("DELETE FROM TabletManageDevice")
    public void deleteAllTabletManageDevice();

    @Query("DELETE FROM TabletManageDevice WHERE QR_ID=:qrId")
    public void deleteTabletManageDevice(String qrId);
}
