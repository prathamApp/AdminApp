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

    @Query("SELECT count(*) FROM CRL")
    public int getCRLsCount();

    @Query("SELECT RoleId FROM CRL where CRLId=:id")
    public String getCRLsRoleById(String id);

    @Query("SELECT DISTINCT ProgramName FROM CRL WHERE RoleId=:rollID")
    public List<String> getDistinctCRLsdProgram(String rollID);

    @Query("SELECT DISTINCT  RoleName FROM CRL WHERE RoleId=:rollID and ProgramName=:programName")
    public List<String> getDistinctCRLsRoleId(String rollID, String programName);

    @Query("SELECT DISTINCT UserName,CRLId,FirstName FROM CRL WHERE RoleName=:roleName and ProgramName=:programName")
    public List<CRL> getDistinctCRLsUserName(String roleName, String programName);


    @Query("SELECT Block FROM CRL WHERE CRLId=:CrlID")
    public String getCRLsBlockName(String CrlID);

    @Query("SELECT * from CRL Where Block=:block and RoleId=5")
    public List<CRL> getCRLBlockHeadBYBlockName(String block);

    @Query("SELECT * from CRL Where Block=:block and RoleId=4")
    public List<CRL> getDistrictLeaderBYBlockName(String block);
}
