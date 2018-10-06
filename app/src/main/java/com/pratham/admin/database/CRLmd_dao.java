package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.CRLmd;

import java.util.List;

@Dao
public interface CRLmd_dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCRLmd(List<CRLmd> crlList);

    @Query("DELETE FROM CRLmd")
    public void deleteAllCRLs_md();

    @Query("SELECT * FROM CRLmd")
    public List<CRLmd> getAllCRLs_md();

    @Query("SELECT count(*) FROM CRLmd")
    public int  getCRLs_mdCount();

   /* @Query("SELECT  State FROM CRLmd")
    public List<String>  getDistinctCRLs_mdState();*/

   @Query("SELECT DISTINCT  RoleName FROM CRLmd")
   public List<String>  getDistinctCRLs_mdRoleId();


    @Query("SELECT DISTINCT ProgramName FROM CRLmd")
    public List<String>  getDistinctCRLs_mdProgram();

    @Query("SELECT DISTINCT UserName,CRLId,FirstName FROM CRLmd WHERE RoleName=:roleName and ProgramName=:programName")
    public List<CRLmd>  getDistinctCRLs_mdUserName(String roleName,String programName);

    @Query("SELECT CRLId FROM CRLmd WHERE  UserName=:userName")
    public String getCRLs_md_ID_By_Uname(String userName);

}
