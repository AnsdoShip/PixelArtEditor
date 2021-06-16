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

public class SelectionBuffer extends ToolBuffer {

    private final int mSrcX;
    private final int mSrcY;
    private final int mSrcWidth;
    private final int mSrcHeight;
    private final int mDstX;
    private final int mDstY;
    private final RotateBuffer mRotateBuffer;
    private final FlipVerticalBuffer mFlipVerticalBuffer;
    private final FlipHorizontalBuffer mFlipHorizontalBuffer;

    public SelectionBuffer(int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY) {
        this (srcX, srcY, srcWidth, srcHeight, dstX, dstY, null, null, null);
    }

    public SelectionBuffer(int srcX, int srcY, int srcWidth, int srcHeight,
                           int dstX, int dstY, RotateBuffer rotateBuffer, FlipVerticalBuffer flipVerticalBuffer,
                           FlipHorizontalBuffer flipHorizontalBuffer) {
        mSrcX = srcX;
        mSrcY = srcY;
        mSrcWidth = srcWidth;
        mSrcHeight = srcHeight;
        mDstX = dstX;
        mDstY = dstY;
        mRotateBuffer = rotateBuffer;
        mFlipVerticalBuffer = flipVerticalBuffer;
        mFlipHorizontalBuffer = flipHorizontalBuffer;
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
        return BufferFlag.SELECTION;
    }

}
