package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Groups;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllGroups(List<Groups> groupsList);

    @Query("DELETE FROM Groups")
    public void deleteAllGroups();

    @Query("SELECT * FROM Groups ")
    public List<Groups> getAllGroups();
}
