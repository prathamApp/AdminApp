package com.pratham.admin.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import com.pratham.admin.modalclasses.Aser;
import com.pratham.admin.modalclasses.NotificationData;

@Dao
public interface NotificationDao {
    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertNotification(NotificationData notificationData);*/
}
