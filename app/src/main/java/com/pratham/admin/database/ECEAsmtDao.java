package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pratham.admin.modalclasses.ECEAsmt;

import java.util.List;

@Dao
public interface ECEAsmtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAllECEAsmtList(List<ECEAsmt> studentsList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertECEAsmt(ECEAsmt ECEAsmtData);

    @Query("SELECT * FROM ECEAsmt WHERE StudentId=:StudentId AND AsmtType=:AsmtType")
    public boolean CheckDataExists(String StudentId, int AsmtType);

    @Query("update ECEAsmt set Date=:date,StartTime=:startTime,EndTime=:endTime, ActMatchingCards=:mc, ActSequencingCards=:sc, ActNumberReco=:nr, ActWordReco=:wr, WS11a=:ws11a, WS11b=:ws11b, WS12a=:ws12a, WS12b=:ws12b, OQ11=:oq11, OQ12=:oq12, OQ13=:oq13, OQ14=:oq14, sentFlag=:sentFlag WHERE StudentId=:studentID AND AsmtType =:asmtType")
    public void UpdateECEAsmtData(String date, String startTime, String endTime, int mc, int sc, int nr, int wr, int ws11a, int ws11b, int ws12a, int ws12b, int oq11, int oq12, int oq13, int oq14, int sentFlag, String studentID, int asmtType);

    @Query("DELETE FROM ECEAsmt")
    public void deleteAllECEAsmt();

    @Query("select * from ECEAsmt where StudentId =:StudentID AND AsmtType =:AsmtType")
    public List<ECEAsmt> GetAsmtDataByStudentID(String StudentID, int AsmtType);

    @Query("SELECT * FROM ECEAsmt WHERE sentFlag=:status")
    public List<ECEAsmt> getNewECEAsmt(int status);

    @Query("UPDATE ECEAsmt SET sentFlag=:pushStatus")
    void updateAllSentFlag(int pushStatus);

    @Query("UPDATE ECEAsmt SET sentFlag=:pushStatus WHERE StudentId =:sID AND AsmtType=:AsmtType")
    void updateSentFlag(int pushStatus, String sID, int AsmtType);
}
