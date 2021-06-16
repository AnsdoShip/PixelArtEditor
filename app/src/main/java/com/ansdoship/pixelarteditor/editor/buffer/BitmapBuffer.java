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

package com.ansdoship.pixelarteditor.editor.buffer;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class BitmapBuffer extends ToolBuffer {

    private final Bitmap mBitmap;
    private final int x;
    private final int y;
    private final RotateBuffer mRotateBuffer;
    private final FlipVerticalBuffer mFlipVerticalBuffer;
    private final FlipHorizontalBuffer mFlipHorizontalBuffer;

    public BitmapBuffer(@NonNull Bitmap bitmap, int x, int y) {
        this (bitmap, x, y, null, null, null);
    }

    public BitmapBuffer(@NonNull Bitmap bitmap, int x, int y,
                        RotateBuffer rotateBuffer, FlipVerticalBuffer flipVerticalBuffer,
                        FlipHorizontalBuffer flipHorizontalBuffer) {
        mBitmap = bitmap;
        this.x = x;
        this.y = y;
        mRotateBuffer = rotateBuffer;
        mFlipVerticalBuffer = flipVerticalBuffer;
        mFlipHorizontalBuffer = flipHorizontalBuffer;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public RotateBuffer getRotateBuffer() {
        return mRotateBuffer;
    }

    public FlipVerticalBuffer getFlipVerticalBuffer() {
        return mFlipVerticalBuffer;
    }

    public FlipHorizontalBuffer getFlipHorizontalBuffer() {
        return mFlipHorizontalBuffer;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.BITMAP;
    }

}
