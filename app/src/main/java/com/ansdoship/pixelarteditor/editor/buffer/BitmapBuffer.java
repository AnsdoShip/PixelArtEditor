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
