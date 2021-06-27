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

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.os.EnvironmentCompat;

import com.ansdoship.pixelarteditor.R;
import com.tianscar.androidutils.ApplicationUtils;

import java.io.File;
import java.lang.reflect.Field;

public final class Utils {

    public @Nullable static TextView getMessageView(@NonNull AlertDialog dialog) {
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(dialog);
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            return (TextView) mMessage.get(mAlertController);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showLongToast(@NonNull Context context, @StringRes int textResId) {
        showToast(context, textResId, Toast.LENGTH_LONG);
    }

    public static void showLongToast(@NonNull Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    public static void showShortToast(@NonNull Context context, @StringRes int textResId) {
        showToast(context, textResId, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(@NonNull Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    public static void showToast(@NonNull Context context, @StringRes int textResId, int duration) {
        showToast(context, context.getString(textResId), duration);
    }

    public static void showToast(@NonNull Context context, CharSequence text, int duration) {
        View view = View.inflate(context, R.layout.toast, null);
        TextView tvToast = view.findViewById(R.id.tv_toast);
        tvToast.setText(text);
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }

    @Nullable
    public static String getCachePath () {
        Context context = ApplicationUtils.getApplicationContext();
        if (isExternalStorageMounted()) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir == null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Android/data/" + context.getPackageName() + "/cache");
                    if (dir.mkdirs()) {
                        return dir.getAbsolutePath();
                    }
                }
            }
            else {
                return externalCacheDir.getAbsolutePath();
            }
        }
        else {
            return context.getCacheDir().getAbsolutePath();
        }
        return null;
    }

    @Nullable
    public static String getFilesPath (@NonNull String type) {
        Context context = ApplicationUtils.getApplicationContext();
        if (!type.equals("")) {
            type = "/" + type;
        }
        if (isExternalStorageMounted()) {
            File externalFilesDir = context.getExternalFilesDir(type);
            if (externalFilesDir == null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/Android/data/" + context.getPackageName() + "/files" + type);
                    if (dir.mkdirs()) {
                        return dir.getAbsolutePath();
                    }
                }
            }
            else {
                return externalFilesDir.getAbsolutePath();
            }
        }
        else {
            File dir = new File(context.getFilesDir().getAbsolutePath() + type);
            if (dir.mkdirs()) {
                return dir.getAbsolutePath();
            }
        }
        return null;
    }

    public static boolean isExternalStorageMounted() {
        return EnvironmentCompat.getStorageState(Environment.getExternalStorageDirectory()).
                equals(Environment.MEDIA_MOUNTED);
    }

}
