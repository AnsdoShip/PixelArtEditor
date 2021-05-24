package com.ansdoship.pixelarteditor.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.os.EnvironmentCompat;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FileUtils {

    public static List<String> getAllDirectoryPaths (String directoryPath) {
        List<String> list = new ArrayList<>();
        File directory = new File(directoryPath);
        if (!directory.exists() || directory.isFile()) {
            return list;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return list;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                list.add(file.getAbsolutePath());
            }
        }
        return list;
    }

    public static List<String> getAllFilePaths (String directoryPath, FileFilter filter) {
        List<String> list = new ArrayList<>();
        File directory = new File(directoryPath);
        if (!directory.exists() || directory.isFile()) {
            return list;
        }
        if (filter != null) {
            File[] files = directory.listFiles(filter);
            if (files == null) {
                return list;
            }
            for (File file : files) {
                if (!file.isDirectory()) {
                    list.add(file.getAbsolutePath());
                }
            }
        }
        else {
            File[] files = directory.listFiles();
            if (files == null) {
                return list;
            }
            for (File file : files) {
                if (!file.isDirectory()) {
                    list.add(file.getAbsolutePath());
                }
            }
        }
        return list;
    }

    public static String getFileName(@NonNull String pathAndName) {
        if (pathAndName.length() <= 0) {
            return null;
        }
        int index = pathAndName.lastIndexOf('/');
        if (index >= 0 && index < pathAndName.length()) {
            return pathAndName.substring(index + 1);
        }
        return pathAndName;
    }

    public static String getFilePathNoName (@NonNull String pathAndName) {
        if (pathAndName.length() <= 0) {
            return null;
        }
        int index = pathAndName.lastIndexOf('/');
        if (index >= 0 && index < pathAndName.length()) {
            return pathAndName.substring(0, index);
        }
        return pathAndName;
    }

    public static String getFileNameNoExtension(@NonNull String pathAndName) {
        pathAndName = getFileName(pathAndName);
        if (pathAndName == null) {
            return null;
        }
        int dot = pathAndName.lastIndexOf('.');
        if (dot >= 0 && dot < pathAndName.length()) {
            return pathAndName.substring(0, dot);
        }
        return pathAndName;
    }

    public static boolean deleteFile (String pathAndName) {
        File file = new File(pathAndName);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    public static boolean renameFile (String pathAndOldName, String newName, boolean override) {
        File file = new File(pathAndOldName);
        if (file.exists()) {
            if (!file.isFile()) {
                return false;
            }
            File newFile = new File(getFilePathNoName(pathAndOldName) + "/" + newName);
            if (!override) {
                if (newFile.exists()) {
                    if (newFile.isFile()) {
                        return false;
                    }
                    else {
                        return file.renameTo(newFile);
                    }
                }
            }
            else {
                if (newFile.exists() && newFile.isFile()) {
                    deleteFile(newFile.getAbsolutePath());
                }
                return file.renameTo(newFile);
            }
        }
        return false;
    }

    public static String getParentDirectoryPath (String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.getAbsolutePath().equals("/")) {
                return "/";
            }
            else {
                File parentFile = file.getParentFile();
                if (parentFile == null) {
                    return null;
                }
                if (parentFile.canWrite()) {
                    return parentFile.getAbsolutePath();
                }
                else {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    public static String getStorageDirectoryPath () {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return Environment.getDataDirectory().getAbsolutePath();
        }
        else {
            if (EnvironmentCompat.getStorageState(Environment.getExternalStorageDirectory()).equals(Environment.MEDIA_MOUNTED)) {
                return Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }
        return null;
    }

    public static String getContextCachePath (Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return context.getCacheDir().getAbsolutePath();
        }
        else {
            if (context.getExternalCacheDir() == null) {
                if (!EnvironmentCompat.getStorageState(Environment.getExternalStorageDirectory()).equals(Environment.MEDIA_MOUNTED)) {
                    return null;
                }
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Android/data/" + context.getPackageName() + "/cache");
                if (dir.mkdirs()) {
                    return dir.getAbsolutePath();
                }
                else {
                    return null;
                }
            }
            return context.getExternalCacheDir().getAbsolutePath();
        }
    }

    public static String getContextFilesPath (Context context, @NonNull String type) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if (!type.equals("")) {
                type = "/" + type;
            }
            File dir = new File(context.getFilesDir().getAbsolutePath() + type);
            if (dir.mkdirs()) {
                return dir.getAbsolutePath();
            }
            else {
                return null;
            }
        }
        else {
            if (context.getExternalFilesDir(type) == null) {
                if (!EnvironmentCompat.getStorageState(Environment.getExternalStorageDirectory()).equals(Environment.MEDIA_MOUNTED)) {
                    return null;
                }
                if (!type.equals("")) {
                    type = "/" + type;
                }
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Android/data/" + context.getPackageName() + "/files" + type);
                if (dir.mkdirs()) {
                    return dir.getAbsolutePath();
                }
                else {
                    return null;
                }
            }
            return Objects.requireNonNull(context.getExternalFilesDir(type)).getAbsolutePath();
        }
    }

}
