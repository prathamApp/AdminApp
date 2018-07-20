package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Completion;

import java.util.List;

@Dao
public interface CompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCompletion(List<Completion> completionList);

    @Query("DELETE FROM Completion")
    public void deleteAllCompletion();

    @Query("SELECT * FROM Completion")
    public List<Completion> getAllCompletion();

}