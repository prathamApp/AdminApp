package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.MetaData;

import java.util.List;

@Dao
public interface MetaDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertMetadata(MetaData metaData);

    @Query("SELECT * FROM MetaData")
    public List<MetaData> getAllMetaData();

    @Query("SELECT value  FROM MetaData WHERE keys=='CRL_ID'")
    public String getCrlMetaData();
}
