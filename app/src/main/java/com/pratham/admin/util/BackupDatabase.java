package com.pratham.admin.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.pratham.admin.activities.SelectProgram;
import com.pratham.admin.database.AppDatabase;
import com.pratham.admin.modalclasses.Modal_Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import static com.pratham.admin.database.AppDatabase.DB_NAME;

public class BackupDatabase {

    public static void backup(Context mContext) {
        try {
            File sd = new File(Environment.getExternalStorageDirectory() + "/PrathamBackups" + "/.PrathamAdmin");
            if (!sd.exists())
                sd.mkdirs();

            if (sd.canWrite()) {
                File currentDB = mContext.getDatabasePath(DB_NAME);
                File parentPath = currentDB.getParentFile();
                for (File f : parentPath.listFiles()) {
                    File temp = new File(sd, f.getName());
                    if (!temp.exists()) temp.createNewFile();
                    FileChannel src = new FileInputStream(f).getChannel();
                    FileChannel dst = new FileOutputStream(temp).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            } else {
                Log.d("gg", "kk");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Modal_Log log = new Modal_Log();
            log.setCurrentDateTime(new Utility().GetCurrentDate());
            log.setErrorType("ERROR");
            log.setExceptionMessage(e.getMessage());
            log.setExceptionStackTrace(e.getStackTrace().toString());
            log.setMethodName("BackupDatabase" + "_" + "backup");
            log.setDeviceId("");
            AppDatabase.getDatabaseInstance(mContext).getLogDao().insertLog(log);
            BackupDatabase.backup(mContext);

        }
    }
}
