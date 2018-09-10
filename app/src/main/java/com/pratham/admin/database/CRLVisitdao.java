package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

//import com.pratham.admin.modalclasses.CRLVisit;
import com.pratham.admin.modalclasses.Course;

import java.util.List;

/*
@Dao
public interface CRLVisitdao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCRLVisit(List<CRLVisit> crlVisitList);

    @Query("DELETE FROM CRLVisit")
    public void deleteAllCRLVisits();

    @Query("SELECT * FROM CRLVisit")
    public List<CRLVisit> getAllCRLVisits();

    @Query("UPDATE CRLVisit SET sentFlag=:pushStatus WHERE VisitID =:vID")
    void updateSentFlag(int pushStatus, String vID);

    @Query("SELECT * FROM CRLVisit WHERE sentFlag=:status")
    public List<CRLVisit> getNewCRLVisits(int status);

    @Query("UPDATE CRLVisit SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);

}
*/
