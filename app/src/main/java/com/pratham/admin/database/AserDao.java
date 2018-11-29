package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Aser;

import java.util.List;

/**
 * Created by PEF on 29/11/2018.
 */
@Dao
public interface AserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllAserList(List<Aser> studentsList);

    @Query("DELETE FROM Aser")
    public void deleteAllAser();
}
