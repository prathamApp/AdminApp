package com.pratham.admin.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

import com.pratham.admin.modalclasses.Aser;
import com.pratham.admin.modalclasses.Attendance;
import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.CRLmd;
import com.pratham.admin.modalclasses.Coach;
import com.pratham.admin.modalclasses.Community;
import com.pratham.admin.modalclasses.Completion;
import com.pratham.admin.modalclasses.Course;
import com.pratham.admin.modalclasses.ECEAsmt;
import com.pratham.admin.modalclasses.GroupSession;
import com.pratham.admin.modalclasses.GroupVisit;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.TabTrack;
import com.pratham.admin.modalclasses.TabletManageDevice;
import com.pratham.admin.modalclasses.TabletStatus;
import com.pratham.admin.modalclasses.TempStudent;
import com.pratham.admin.modalclasses.Village;
import com.pratham.admin.modalclasses.Youth;

//import com.pratham.admin.modalclasses.CRLVisit;

@Database(entities = {ECEAsmt.class, Attendance.class, CRL.class, CRLmd.class,
        /*CRLVisit.class,*/ Coach.class, Course.class, Community.class,
        Completion.class, Groups.class, Student.class, GroupSession.class,
        GroupVisit.class, Village.class, MetaData.class, TempStudent.class,
        TabTrack.class, TabletManageDevice.class, Modal_Log.class, TabletStatus.class,
        Aser.class, Youth.class}, version = AppDatabase.DB_VERSION, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase DATABASEINSTANCE;

    public static final String DB_NAME = "prathamDb";
    public static final int DB_VERSION = 9;

    public abstract ECEAsmtDao getECEAsmtDao();

    public abstract AttendanceDao getAttendanceDao();

    public abstract CRLdao getCRLdao();

    public abstract LogDao getLogDao();

    public abstract CRLmd_dao getCRLmd_dao();

    public abstract TabletStatusDao getTabletStatusDao();

//    public abstract CRLVisitdao getCRLVisitdao();

    public abstract CoachDao getCoachDao();

    public abstract CourseDao getCoursesDao();

    public abstract CommunityDao getCommunityDao();

    public abstract CompletionDao getCompletionDao();

    public abstract GroupSessionDao getGroupSessionDao();

    public abstract GroupVisitDao getGroupVisitDao();

    public abstract TabTrackDao getTabTrackDao();

    public abstract TabletManageDeviceDao getTabletManageDeviceDao();

    public abstract GroupDao getGroupDao();

    public abstract StudentDao getStudentDao();

    public abstract AserDao getAserDao();

    public abstract VillageDao getVillageDao();

    public abstract MetaDataDao getMetaDataDao();

    public abstract TempStudentdao getTempStudentDao();

    //public abstract NotificationDao getNotificationDao();

    public abstract YouthDao getYouthDao();

    public static AppDatabase getDatabaseInstance(Context context) {
        if (DATABASEINSTANCE == null)
            DATABASEINSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .addMigrations(MIGRATION_7_8)
                    .addMigrations(MIGRATION_8_9)
                    .allowMainThreadQueries()
                    .build();
        return DATABASEINSTANCE;
    }

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Alter Queries for new columns as we don't want to lose existing data
            database.execSQL("ALTER TABLE Aser ADD COLUMN sentFlag INTEGER DEFAULT 0");

            database.execSQL("ALTER TABLE Groups ADD COLUMN CreatedBy TEXT");
            database.execSQL("ALTER TABLE Groups ADD COLUMN CreatedOn TEXT");
            database.execSQL("ALTER TABLE Groups ADD COLUMN sentFlag INTEGER DEFAULT 0");

            database.execSQL("ALTER TABLE Student ADD COLUMN FirstName TEXT");
            database.execSQL("ALTER TABLE Student ADD COLUMN MiddleName TEXT");
            database.execSQL("ALTER TABLE Student ADD COLUMN LastName TEXT");
            database.execSQL("ALTER TABLE Student ADD COLUMN CreatedBy TEXT");
            database.execSQL("ALTER TABLE Student ADD COLUMN CreatedOn TEXT");
            database.execSQL("ALTER TABLE Student ADD COLUMN UpdatedDate TEXT");
            database.execSQL("ALTER TABLE Student ADD COLUMN DOB TEXT");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Alter Queries for new columns as we don't want to lose existing data
            database.execSQL("ALTER TABLE Aser ADD COLUMN English INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Aser ADD COLUMN EnglishSelected INTEGER DEFAULT 0");
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Alter Queries for new columns as we don't want to lose existing data
            database.execSQL("ALTER TABLE Student ADD COLUMN SchoolType INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Student ADD COLUMN GuardianName TEXT DEFAULT ''");
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Alter Queries for new columns as we don't want to lose existing data
            database.execSQL("CREATE TABLE ECEAsmt(ECEAsmtID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, StudentId TEXT, AsmtType INTEGER"
                    + ", Date TEXT, StartTime TEXT, EndTime TEXT"
                    + ", ActMatchingCards INTEGER, ActSequencingCards INTEGER, ActNumberReco INTEGER, ActWordReco INTEGER"
                    + ", WS11a INTEGER, WS11b INTEGER, WS12a INTEGER, WS12b INTEGER"
                    + ", OQ11 INTEGER, OQ12 INTEGER, OQ13 INTEGER, OQ14 INTEGER"
                    + ", sentFlag INTEGER DEFAULT 0)");
        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Alter Queries for new columns as we don't want to lose existing data
            database.execSQL("CREATE TABLE Youth(youthId TEXT PRIMARY KEY NOT NULL, groupId TEXT, groupName TEXT"
                    + ", firstName TEXT, middleName TEXT, lastName TEXT"
                    + ", phoneNumber TEXT, guardianName TEXT, birthDate TEXT, gender TEXT"
                    + ", maritalStatus TEXT, areyoustudying INTEGER NOT NULL, education TEXT, occupation TEXT"
                    + ", haveSmartphone INTEGER NOT NULL, useSmartphone INTEGER NOT NULL, wantToJoin INTEGER NOT NULL"
                    + ", createdBy TEXT, createdOn TEXT, sentFlag INTEGER NOT NULL, isDeleted INTEGER NOT NULL)");
        }
    };
    static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Student ADD COLUMN 'phoneNo' TEXT ");
            database.execSQL("ALTER TABLE Student ADD COLUMN 'phoneType' TEXT ");
            database.execSQL("ALTER TABLE Student ADD COLUMN 'relation_with_phone_owner' TEXT ");

            //for change schooltype int to string
            database.execSQL("CREATE TABLE `StudentNew` (`GroupId` TEXT, `GroupName` TEXT, `FullName` TEXT, `Stud_Class` TEXT, `Age` TEXT, `Gender` TEXT, `sentFlag` INTEGER NOT NULL, `StudentId` TEXT NOT NULL, `FirstName` TEXT, `MiddleName` TEXT, `LastName` TEXT, `CreatedBy` TEXT, `CreatedOn` TEXT, `UpdatedDate` TEXT, `DOB` TEXT, `SchoolType` TEXT, `GuardianName` TEXT, `phoneType` TEXT, `phoneNo` TEXT, `relation_with_phone_owner` TEXT, PRIMARY KEY(`StudentId`))");
            database.execSQL("INSERT INTO StudentNew (GroupId, GroupName, FullName,Stud_Class,Age,Gender,sentFlag,StudentId,FirstName,MiddleName,LastName,CreatedBy,CreatedOn,UpdatedDate,DOB,SchoolType,GuardianName,phoneType,phoneNo,relation_with_phone_owner) SELECT GroupId, GroupName, FullName,Stud_Class,Age,Gender,sentFlag,StudentId,FirstName,MiddleName,LastName,CreatedBy,CreatedOn,UpdatedDate,DOB,SchoolType,GuardianName,phoneType,phoneNo,relation_with_phone_owner FROM Student");
            database.execSQL("DROP TABLE Student");
            database.execSQL("ALTER TABLE StudentNew RENAME TO Student");
            Log.d("VROM", "Migration_8_9");

        }
    };

    public static void destroyInstance() {
        DATABASEINSTANCE = null;
    }
}
