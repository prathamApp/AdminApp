package com.pratham.admin.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.pratham.admin.modalclasses.CRL;
import com.pratham.admin.modalclasses.Groups;
import com.pratham.admin.modalclasses.MetaData;
import com.pratham.admin.modalclasses.Student;
import com.pratham.admin.modalclasses.TabTrack;
import com.pratham.admin.modalclasses.TempStudent;
import com.pratham.admin.modalclasses.Village;

@Database(entities = {CRL.class, Groups.class, Student.class, Village.class, MetaData.class, TempStudent.class, TabTrack.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase DATABASEINSTANCE;

    public abstract CRLdao getCRLdao();

    public abstract TabTrackDao getTabTrackDao();

    public abstract GroupDao getGroupDao();

    public abstract StudentDao getStudentDao();

    public abstract VillageDao getVillageDao();

    public abstract MetaDataDao getMetaDataDao();

    public abstract TempStudentdao getTempStudentDao();

    public static AppDatabase getDatabaseInstance(Context context) {
        if (DATABASEINSTANCE == null)
            DATABASEINSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "prathamDb").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        return DATABASEINSTANCE;
    }

    public static void destroyInstance() {
        DATABASEINSTANCE = null;
    }
}
