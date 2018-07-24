package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Course;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllCourses(List<Course> CourseList);

    @Query("DELETE FROM Course")
    public void deleteAllCourses();

    @Query("SELECT * FROM Course")
    public List<Course> getAllCourse();
}
