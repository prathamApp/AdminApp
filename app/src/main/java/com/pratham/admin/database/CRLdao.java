package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.CRL;

import java.util.List;

@Dao
public interface CRLdao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCRL(List<CRL> crlList);

    @Query("DELETE FROM CRL")
    public void deleteAllCRLs();

    @Query("SELECT * FROM CRL")
    public List<CRL> getAllCRLs();

}
