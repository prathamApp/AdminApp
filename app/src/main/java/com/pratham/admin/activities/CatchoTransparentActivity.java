package com.pratham.admin.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Modal_Log;
import com.pratham.admin.util.BackupDatabase;
import com.pratham.admin.util.BaseActivity;
import com.pratham.admin.util.Utility;

import net.alhazmy13.catcho.library.Catcho;
import net.alhazmy13.catcho.library.error.CatchoError;

public class CatchoTransparentActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CatchoError error = (CatchoError) getIntent().getSerializableExtra(Catcho.ERROR);
        Modal_Log log = new Modal_Log();
        log.setCurrentDateTime(new Utility().GetCurrentDate());
        log.setErrorType("ERROR");
        log.setExceptionMessage(error.toString());
        log.setExceptionStackTrace(error.getError());
        log.setMethodName("NO_METHOD");
        log.setDeviceId("");
        AppDatabase.getDatabaseInstance(this).getLogDao().insertLog(log);
        new BackupDatabase().backup(this);
        finish();
    }
}
