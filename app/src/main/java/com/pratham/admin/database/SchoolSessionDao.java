package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.SchoolSession;

import java.util.List;

@Dao
public interface SchoolSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCRLVisit(List<SchoolSession> schoolSessionDaoList);

    @Query("DELETE FROM SchoolSession")
    public void deleteAllSchoolSession();

    @Query("SELECT * FROM SchoolSession")
    public List<SchoolSession> getAllSchoolSession();

}
