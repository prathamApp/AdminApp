package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import com.pratham.admin.modalclasses.Community;

import java.util.List;

@Dao
public interface CommunityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCommunity(List<Community> communityList);

    @Query("DELETE FROM Community")
    public void deleteAllCommunity();

    @Query("SELECT * FROM Community")
    public List<Community> getAllCommunity();

}