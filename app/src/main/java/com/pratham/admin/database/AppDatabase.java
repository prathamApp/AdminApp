package com.pratham.admin.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.pratham.admin.Service.MyService;
import com.pratham.admin.activities.Notification.Notification;
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
import com.pratham.admin.modalclasses.NotificationData;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.TabTrack;
import com.pratham.admin.modalclasses.TabletManageDevice;
import com.pratham.admin.modalclasses.TabletStatus;
import com.pratham.admin.modalclasses.TempStudent;
import com.pratham.admin.modalclasses.Village;

//import com.pratham.admin.modalclasses.CRLVisit;

@Database(entities = {ECEAsmt.class, Attendance.class, CRL.class, CRLmd.class, /*CRLVisit.class,*/ Coach.class, Course.class, Community.class, Completion.class, Groups.class, Student.class, GroupSession.class, GroupVisit.class, Village.class, MetaData.class, TempStudent.class, TabTrack.class, TabletManageDevice.class, Modal_Log.class, TabletStatus.class, Aser.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase DATABASEINSTANCE;

    public static final String DB_NAME = "prathamDb";

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

    public static AppDatabase getDatabaseInstance(Context context) {
        if (DATABASEINSTANCE == null)
            DATABASEINSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .addMigrations(MIGRATION_6_7)
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
            database.execSQL("CREATE TABLE NotificationData(autoIdID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, transId TEXT, fromId TEXT"
                    + ", fromName TEXT, toId TEXT, toName TEXT"
                    + ", prathamId TEXT, qrId TEXT, serialId TEXT, receiveDate TEXT"
                    + ", comment TEXT, damageType TEXT, isDamaged TEXT, textDesc TEXT"
                    + ", dateAdded TEXT, pushStatus TEXT, lastPushDate TEXT)");
        }
    };

    public static void destroyInstance() {
        DATABASEINSTANCE = null;
    }
}
