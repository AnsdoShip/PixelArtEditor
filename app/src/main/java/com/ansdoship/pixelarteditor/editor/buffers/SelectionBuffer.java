package com.ansdoship.pixelarteditor.editor.buffers;

import android.graphics.Point;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.ansdoship.pixelarteditor.editor.BufferFlag;

public class SelectionBuffer extends ToolBuffer {

    private final int mSrcX;
    private final int mSrcY;
    private final int mSrcWidth;
    private final int mSrcHeight;
    private final int mDstX;
    private final int mDstY;
    private final int mSelectionFlag;
    private final RotateBuffer mRotateBuffer;
    private final FlipBuffer mFlipBuffer;

    public SelectionBuffer(@NonNull Rect srcRect, @NonNull Point dstOrigin, int selectionFlag) {
        this(srcRect, dstOrigin, selectionFlag, null, null);
    }

    public SelectionBuffer(@NonNull Rect srcRect, @NonNull Point dstOrigin, int selectionFlag,
                           RotateBuffer rotateBuffer, FlipBuffer flipBuffer) {
        this(srcRect.left, srcRect.top, srcRect.width(), srcRect.height(), dstOrigin.x, dstOrigin.y,
                selectionFlag, rotateBuffer, flipBuffer);
    }

    public SelectionBuffer(int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int selectionFlag) {
        this (srcX, srcY, srcWidth, srcHeight, dstX, dstY, selectionFlag, null, null);
    }

    public SelectionBuffer(int srcX, int srcY, int srcWidth, int srcHeight,
                           int dstX, int dstY, int selectionFlag,
                           RotateBuffer rotateBuffer, FlipBuffer flipBuffer) {
        mSrcX = srcX;
        mSrcY = srcY;
        mSrcWidth = srcWidth;
        mSrcHeight = srcHeight;
        mDstX = dstX;
        mDstY = dstY;
        mSelectionFlag = selectionFlag;
        mRotateBuffer = rotateBuffer;
        mFlipBuffer = flipBuffer;
    }

    public int getSrcX() {
        return mSrcX;
    }

    public int getSrcY() {
        return mSrcY;
    }

    public int getSrcWidth() {
        return mSrcWidth;
    }

    public int getSrcHeight() {
        return mSrcHeight;
    }

    public int getDstX() {
        return mDstX;
    }

    public int getDstY() {
        return mDstY;
    }

    public int getSelectionFlag() {
        return mSelectionFlag;
    }

    public RotateBuffer getRotateBuffer() {
        return mRotateBuffer;
    }

    public FlipBuffer getFlipBuffer() {
        return mFlipBuffer;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.SELECTION;
    }

}
