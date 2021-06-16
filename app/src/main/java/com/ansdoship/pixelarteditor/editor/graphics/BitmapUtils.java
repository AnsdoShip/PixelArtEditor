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
