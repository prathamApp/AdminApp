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

    @Query("update Student set Stud_Class=:stdClass,GuardianName=:guardianName,SchoolType=:stdSchoolType,DOB=:DOB,Age=:Age,Gender=:gender,phoneType=:phoneType,relation_with_phone_owner=:relation_with_phone_owner,phoneNo=:moNumber, sentFlag=:sentFlag WHERE StudentId=:StudentUniqID")
    public void UpdateStudent(String stdClass, String guardianName, String stdSchoolType, String DOB, String Age, String gender, String phoneType, String relation_with_phone_owner, String moNumber, int sentFlag, String StudentUniqID);
}
