package com.ansdoship.pixelarteditor.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.Time;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.TimeZone;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;

    public static final String TAG = "CrashHandler";

    private Properties mDeviceCrashInfo;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";

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
        mDeviceCrashInfo = new Properties();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void init() {
        init(ApplicationUtils.getApplication());
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable exception) {
        if (!handleException(exception)) {
            mDefaultHandler.uncaughtException(thread, exception);
        }
        else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(@NonNull Throwable exception) {
        String message = exception.getLocalizedMessage();
        if (message == null) {
            return false;
        }
        collectCrashDeviceInfo();
        String fileName = saveCrashInfoToFile(exception);
        if (fileName != null) {
            Utils.showLongToast(mContext, fileName);
        }
        return true;
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
        mDeviceCrashInfo.put("EXCEPTION", result);
        try {
            String crashDir = getCrashDir();
            if (crashDir == null) {
                return null;
            }
            Time time = new Time(TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
            time.setToNow();
            String date = time.year + "-" + (time.month + 1) + "-" + time.monthDay;
            String currentTime = time.hour + "-" + time.minute + "-" + time.second;
            String fileName = date + "-" + currentTime + CRASH_EXTENSION;
            FileOutputStream trace = new FileOutputStream(new File(crashDir + "/" + fileName));
            mDeviceCrashInfo.store(trace, "");
            trace.flush();
            trace.close();
            return fileName;
        }
        catch (Exception e) {
            Log.e(TAG, "Error while writing report file", e);
        }
        return null;
    }

    private void collectCrashDeviceInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(
                    mContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName + "");
                mDeviceCrashInfo.put(VERSION_CODE, Integer.toString(pi.versionCode));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error while collecting package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldObject = field.get(null);
                mDeviceCrashInfo.put(field.getName(), fieldObject + "");
            }
            catch (Exception e) {
                Log.e(TAG, "Error while collecting crash info", e);
            }
        }
    }

    @Nullable
    public static String getCrashDir() {
        return Utils.getFilesPath("crashes");
    }

}