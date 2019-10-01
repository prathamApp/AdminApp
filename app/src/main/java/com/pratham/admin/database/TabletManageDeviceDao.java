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


    @Query("DELETE FROM TabletManageDevice WHERE QR_ID=:qrId")
    public void deleteTabletManageDevice(String qrId);

    @Query("SELECT * FROM TabletManageDevice WHERE isPushed=0")
    public List<TabletManageDevice> getAllTabletManageDevice();

    @Query("DELETE FROM TabletManageDevice")
    public void deleteAllTabletManageDevice();

    @Query("Update TabletManageDevice set isPushed=1")
    public void updateIsPushedFlag();



    /*Assign Return*/

    @Query("SELECT * FROM TabletManageDevice WHERE isPushed=0 AND (status='Assign' OR status='Return')")
    public List<TabletManageDevice> getAllAssignAndReturnDevice();

    @Query("Update TabletManageDevice set isPushed=1 WHERE (status='Assign' OR status='Return')")
    public void updateAssignAndReturnIsPushedFlag();

    @Query("DELETE FROM TabletManageDevice  WHERE (status='Assign' OR status='Return')")
    public void deleteAssignAndReturnDevice();


    /*Replace*/

    @Query("SELECT * FROM TabletManageDevice WHERE isPushed=0 AND (status='Replace_assign' OR status='Replace_collect')")
    public List<TabletManageDevice> getAllReplaceDevice();

    @Query("DELETE FROM TabletManageDevice WHERE isPushed=0 AND (status='Replace_assign' OR status='Replace_collect')")
    public void deleteReplaceDevice();

    @Query("Update TabletManageDevice set isPushed=1 WHERE (status='Replace_assign' OR status='Replace_collect')")
    public void updateReplaceIsPushedFlag();

    @Query("SELECT * FROM TabletManageDevice WHERE id=:id")
    public List<TabletManageDevice> checkExistanceTabletManageDevice(String id);

    @Query("SELECT * FROM TabletManageDevice WHERE logged_CRL_ID=:crlID AND Pratham_ID='' AND tabSerial_ID=''")
    public List<TabletManageDevice> getCollectedTablist(String crlID);
}
