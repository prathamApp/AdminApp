package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.CRLmd;

import java.util.List;

@Dao
public interface CRLdao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCRL(List<CRL> crlList);

    @Query("DELETE FROM CRL")
    public void deleteAllCRLs();

    @Query("SELECT * FROM CRL")
    public List<CRL> getAllCRLs();

    @Query("SELECT count(*) FROM CRL")
    public int  getCRLsCount();

    @Query("SELECT RoleId FROM CRL where CRLId=:id")
    public String  getCRLsRoleById(String id);

    @Query("SELECT DISTINCT ProgramName FROM CRL WHERE RoleId=:rollID")
    public List<String>  getDistinctCRLsdProgram(String rollID);

    @Query("SELECT DISTINCT  RoleName FROM CRL WHERE RoleId=:rollID")
    public List<String>  getDistinctCRLsRoleId(String rollID);

    @Query("SELECT DISTINCT UserName,CRLId,FirstName FROM CRL WHERE RoleName=:roleName and ProgramName=:programName")
    public List<CRL>  getDistinctCRLsUserName(String roleName, String programName);
}
