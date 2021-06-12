package com.ansdoship.pixelarteditor.editor.graphics;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class BitmapUtils {

    private BitmapUtils (){}

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    public static void recycleBitmap(@Nullable Bitmap... bitmaps) {
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bitmap : bitmaps) {
            recycleBitmap(bitmap);
        }
    }

}
