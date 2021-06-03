package com.ansdoship.pixelarteditor.util;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.ansdoship.pixelarteditor.editor.graphics.BitmapUtils;

import java.util.HashMap;
import java.util.Map;

public final class BitmapPool {

    public BitmapPool() {
        bitmaps = new HashMap<>();
    }
    
    private final Map<String, Bitmap> bitmaps;

    public void addBitmap(@NonNull String id, @NonNull Bitmap bitmap) {
        bitmaps.put(id, bitmap);
    }

    public Bitmap getBitmap(@NonNull String id) {
        return bitmaps.get(id);
    }

    public Bitmap removeBitmap(@NonNull String id) {
        return bitmaps.remove(id);
    }

    public void removeAllBitmaps() {
        bitmaps.clear();
    }

    public void recycleBitmap(@NonNull String id) {
        Bitmap bitmap = removeBitmap(id);
        BitmapUtils.recycleBitmap(bitmap);
    }

    public void recycleAllBitmaps() {
        for (String id : bitmaps.keySet()) {
            recycleBitmap(id);
        }
    }
    
}
