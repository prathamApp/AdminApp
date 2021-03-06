package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;

import java.util.List;

@Dao
public interface CommunityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCommunity(List<Community> communityList);

    @Query("DELETE FROM Community")
    public void deleteAllCommunity();

    @Query("SELECT * FROM Community")
    public List<Community> getAllCommunity();

    @Query("SELECT * FROM Community Where GroupID=:gid")
    public List<Community> getCommunityByGroupID(String gid);

    @Query("UPDATE Community SET sentFlag=:pushStatus WHERE CommunityID =:cID")
    void updateSentFlag(int pushStatus, String cID);

    @Query("SELECT * FROM Community WHERE sentFlag=:status")
    public List<Community> getNewCommunities(int status);

    @Query("UPDATE Community SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);

}
