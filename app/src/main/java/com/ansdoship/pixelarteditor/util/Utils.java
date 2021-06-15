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
import androidx.core.os.EnvironmentCompat;

import com.ansdoship.pixelarteditor.R;

import java.io.File;

public final class Utils {

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

    public static void hideSoftInputFromView(@NonNull Context context, @NonNull View view) {
        ((InputMethodManager) (context.getSystemService(Context.INPUT_METHOD_SERVICE)))
                .hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // Calculate linear distance of two fingers
    public static double spacing(@NonNull MotionEvent event) {
        if(event.getPointerCount() >= 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return Math.pow(x * x + y * y, 0.5);
        }
        return 0;
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
