/*
 * Copyright (C) 2021 AnsdoShip Studio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.ansdoship.pixelarteditor.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ansdoship.pixelarteditor.R;
import com.tianscar.module.ApplicationUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;

    public static final String TAG = "CrashHandler";

    public static final String CRASH_EXTENSION = ".crash";

    private enum Singleton {
        INSTANCE;
        private final CrashHandler instance;
        Singleton() {
            instance = new CrashHandler();
        }
        public CrashHandler getInstance() {
            return instance;
        }
    }

    public static CrashHandler getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    public void init(@NonNull Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void init() {
        init(ApplicationUtils.getApplication());
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable exception) {
        String filePathname = handleException(exception);
        if (filePathname != null) {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Utils.showLongToast(mContext, mContext.getString(R.string.error_app_crashed) + "\n" + filePathname);
                    Looper.loop();
                }
            }.start();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }, 3500);
        }
    }

    @Nullable
    private String handleException(@NonNull Throwable exception) {
        String message = exception.getLocalizedMessage();
        if (message != null) {

            return saveCrashInfoToFile(exception);
        }
        return null;
    }

    @Nullable
    public static File[] getCrashReportFiles() {
        String crashDir = getCrashDir();
        if (crashDir == null) {
            return null;
        }
        return FileUtils.listFiles(new File(crashDir),
                new String[] {CRASH_EXTENSION}, false).toArray(new File[0]);
    }

    @SuppressLint("SimpleDateFormat")
    @Nullable
    private String saveCrashInfoToFile(@NonNull Throwable exception) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        exception.printStackTrace(printWriter);
        Throwable cause = exception.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String result = info.toString();
        printWriter.close();
        try {
            String crashDir = getCrashDir();
            if (crashDir == null) {
                return null;
            }
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            String filePathname = crashDir + "/" + formatter.format(date) + CRASH_EXTENSION;
            File file = new File(filePathname);
            FileUtils.write(file, "# " + date.toString(), "UTF-8", false);
            FileUtils.write(file, "\n\n# ANDROID_SDK_INT=" + Build.VERSION.SDK_INT, "UTF-8", true);
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(
                    mContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                FileUtils.write(file, "\n\n# APP_VERSION_NAME=" + pi.versionName +
                        "\n# APP_VERSION_CODE=" + pi.versionCode, "UTF-8", true);
            }
            FileUtils.write(file, "\n\n" + result, "UTF-8", true);
            return filePathname;
        }
        catch (Exception e) {
            Log.e(TAG, "Error while writing report file", e);
        }
        return null;
    }

    @Nullable
    public static String getCrashDir() {
        return Utils.getFilesPath("crashes");
    }

}