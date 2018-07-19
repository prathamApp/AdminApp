package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.CRLVisit;

import java.util.List;

@Dao
public interface CRLVisitdao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCRLVisit(List<CRLVisit> crlVisitList);

    @Query("DELETE FROM CRLVisit")
    public void deleteAllCRLVisits();

    @Query("SELECT * FROM CRLVisit")
    public List<CRL> getAllCRLVisits();

}
