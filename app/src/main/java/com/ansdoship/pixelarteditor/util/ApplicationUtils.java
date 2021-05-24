package com.ansdoship.pixelarteditor.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

/**
 * A utility class providing functions about application.
 */
public class ApplicationUtils {

    /**
     * Gets current application object.
     *
     * @return the current application object
     */
    @NonNull
    public static Application getApplication () {
        return CURRENT;
    }

    /**
     * Gets current application context object.
     *
     * @return the current application context object
     */
    @NonNull
    public static Context getApplicationContext () {
        return CURRENT.getApplicationContext();
    }

    /**
     * Gets current app's resources object.
     *
     * @return the current app's resources object
     */
    @NonNull
    public static Resources getResources () {
        return CURRENT.getResources();
    }

    /**
     * Gets current app's asset manager object.
     *
     * @return the current app's asset manager object
     */
    @NonNull
    public static AssetManager getAssets () {
        return CURRENT.getAssets();
    }
 
    @SuppressLint("StaticFieldLeak")
    private static final Application CURRENT;
 
    static {
        try {
            Object activityThread = getActivityThread();
            Object app = activityThread.getClass().getMethod("getApplication")
                    .invoke(activityThread);
            CURRENT = (Application) app;
        }
        catch (Throwable e) {
            throw new IllegalStateException("Cannot access application.", e);
        }
    }
 
    private static Object getActivityThread() {
        Object activityThread = null;
        try {
            @SuppressLint("PrivateApi") Method method =
                    Class.forName("android.app.ActivityThread").getMethod("currentActivityThread");
            method.setAccessible(true);
            activityThread = method.invoke(null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return activityThread;
    }

}