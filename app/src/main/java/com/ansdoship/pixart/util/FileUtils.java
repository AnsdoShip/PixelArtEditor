package com.ansdoship.pixart.util;

import android.os.Build;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class FileUtils {

    public static List<String> getAllDirectories (String directoryPath) {
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

    public static List<String> getAllFiles (String directoryPath, FileFilter filter) {
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

    public static String getFileName(String pathAndName) {
        pathAndName = pathAndName.trim();
        String[] temp = pathAndName.split("\\\\");
        return temp[temp.length - 1];
    }

    public static String getParentDirectory (String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.getAbsolutePath().equals("/")) {
                return "/";
            }
            else {
                return file.getParent();
            }
        }
        return null;
    }

    public static List<String> getExternalSDCardPathList() {
        List<String> paths = new ArrayList<>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED)
                && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
            paths.add(extFile.getAbsolutePath());
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile.getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                paths.add(mountPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    public static String getStorageDirectory () {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return Environment.getDataDirectory().getAbsolutePath();
        }
        else {
            return getExternalSDCardPathList().get(0);
        }
    }

}
