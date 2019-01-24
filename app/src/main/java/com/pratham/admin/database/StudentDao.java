package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.pratham.admin.modalclasses.Student;

import java.util.List;

@Dao
public interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllStudents(List<Student> studentsList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertStudent(Student student);

    @Update
    public int updateAllStudent(List<Student> studList);

    @Query("DELETE FROM Student")
    public void deleteAllStudents();

    @Query("SELECT * FROM Student")
    public List<Student> getAllStudents();

    @Query("SELECT * FROM student WHERE GroupId=:gID")
    public List<Student> getGroupwiseStudents(String gID);

    @Query("SELECT * FROM student WHERE StudentId =:sID")
    public List<Student> getStudentByID(String sID);

    @Query("DELETE FROM Student Where StudentId=:stdID")
    public void deleteStudentByID(String stdID);

    @Query("SELECT * FROM Student WHERE GroupID =:GroupID ")
    public List<Student> GetAllStudentsByGroupID(String GroupID);

    @Query("select * from Student where StudentID =:studentID")
    public Student GetStudentDataByStdID(String studentID);

    @Query("SELECT * FROM Student WHERE sentFlag=:status")
    public List<Student> getNewStudents(int status);

    @Query("UPDATE Student SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);

    @Query("UPDATE Student SET sentFlag=:pushStatus WHERE StudentId =:sID")
    void updateSentFlag(int pushStatus, String sID);

    @Query("DELETE FROM Student WHERE Gender='deleted'")
    public void removeDeletedStudentRecords();

    @Query("DELETE FROM Student WHERE GroupId=:grpID")
    public void deleteDeletedGrpsStdRecords(String grpID);

}
