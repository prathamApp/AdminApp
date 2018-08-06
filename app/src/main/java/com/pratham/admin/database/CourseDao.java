package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.Completion;
import com.pratham.admin.modalclasses.Course;

import java.util.List;

@Dao
public interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCourses(List<Course> CourseList);

    @Query("DELETE FROM Course")
    public void deleteAllCourses();

    @Query("SELECT * FROM Course")
    public List<Course> getAllCourse();

    @Query("SELECT * FROM Course WHERE CourseID=:cID")
    public List<Course> getAllCourseDetails(String cID);

    @Query("SELECT * FROM Course WHERE sentFlag=:status")
    public List<Course> getNewCourses(int status);

    @Query("UPDATE Course SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);

}
