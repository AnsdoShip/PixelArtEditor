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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import androidx.annotation.NonNull;

import com.tianscar.simplebitmap.BitmapChanger;
import com.tianscar.simplebitmap.BitmapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ToolBufferPool {

    private Bitmap mCacheBitmap;
    private Bitmap mCurrentBitmap;
    private final Canvas mCanvas;
    private List<ToolBuffer> mToolBufferList;
    private int index;
    private final int maxSize;
    private final Paint mEraser;
    private final Paint mBitmapPaint;
    private final ReentrantReadWriteLock mReadWriteLock;

    private boolean tempMode;

    private ToolBufferPool(Bitmap cacheBitmap, int maxSize, boolean isCopy) {
        if (isCopy) {
            mCacheBitmap = Bitmap.createBitmap(cacheBitmap);
        }
        else {
            mCacheBitmap = cacheBitmap;
        }
        tempMode = false;
        mCurrentBitmap = Bitmap.createBitmap(mCacheBitmap);
        mCanvas = new Canvas();
        mToolBufferList = new ArrayList<>(maxSize);
        index = -1;
        this.maxSize = maxSize;
        mEraser = new Paint();
        mEraser.setDither(false);
        mEraser.setAntiAlias(false);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraser.setStyle(Paint.Style.FILL_AND_STROKE);
        mEraser.setStrokeJoin(Paint.Join.MITER);
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(false);
        mBitmapPaint.setDither(false);
        mBitmapPaint.setFilterBitmap(false);
        mReadWriteLock = new ReentrantReadWriteLock(true);
    }

    @NonNull
    public static ToolBufferPool createToolBufferPool (@NonNull Bitmap cacheBitmap, int maxSize, boolean isCopy) {
        return new ToolBufferPool(cacheBitmap, maxSize, isCopy);
    }

    public void addToolBuffer (@NonNull ToolBuffer toolBuffer) {
        mReadWriteLock.writeLock().lock();
        try {
            setTempModeDisabled();
            drawToolBuffer(mCurrentBitmap, toolBuffer);
            if (getRedoCount() > 0) {
                mToolBufferList = mToolBufferList.subList(0, index + 1);
            }
            mToolBufferList.add(toolBuffer);
            int size = mToolBufferList.size();
            index ++;
            if (size > maxSize) {
                index = maxSize - 1;
                int subIndex = size - maxSize;
                for (int i = 0; i < subIndex; i++) {
                    drawToolBuffer(mCacheBitmap, mToolBufferList.get(i));
                }
                replaceCacheBitmap(Bitmap.createBitmap(mCacheBitmap));
                if (mToolBufferList.get(0).getBufferFlag() == BufferFlag.BITMAP) {
                    BitmapUtils.recycle(((BitmapBuffer)(mToolBufferList.get(0))).getBitmap());
                }
                mToolBufferList = mToolBufferList.subList(subIndex, size);
            }
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    private void setTempModeEnabled () {
        if (tempMode) {
            return;
        }
        tempMode = true;
    }

    private void setTempModeDisabled () {
        if (!tempMode) {
            return;
        }
        tempMode = false;
        flushCurrentBitmap();
    }

    public void addTempToolBuffer (@NonNull ToolBuffer toolBuffer) {
        mReadWriteLock.writeLock().lock();
        try {
            setTempModeEnabled();
            drawToolBuffer(mCurrentBitmap, toolBuffer);
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    public void clearTempToolBuffers() {
        mReadWriteLock.writeLock().lock();
        try {
            if (tempMode) {
                flushCurrentBitmap();
            }
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    public void setCacheBitmap(Bitmap cacheBitmap) {
        mReadWriteLock.writeLock().lock();
        try {
            mCacheBitmap = cacheBitmap;
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    public Bitmap getCacheBitmap() {
        mReadWriteLock.readLock().lock();
        try {
            return mCacheBitmap;
        }
        finally {
            mReadWriteLock.readLock().unlock();
        }
    }

    public Bitmap getCurrentBitmap () {
        mReadWriteLock.readLock().lock();
        try {
            return mCurrentBitmap;
        }
        finally {
            mReadWriteLock.readLock().unlock();
        }
    }

    public void undo () {
        mReadWriteLock.writeLock().lock();
        try {
            index -= 1;
            if (index < -1) {
                index = -1;
                return;
            }
            flushCurrentBitmap();
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    public void redo () {
        mReadWriteLock.writeLock().lock();
        try {
            index += 1;
            if (index >= mToolBufferList.size()) {
                index = mToolBufferList.size() - 1;
                return;
            }
            flushCurrentBitmap();
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    public int getUndoCount () {
        mReadWriteLock.readLock().lock();
        try {
            return index + 1;
        }
        finally {
            mReadWriteLock.readLock().unlock();
        }
    }

    public int getRedoCount () {
        mReadWriteLock.readLock().lock();
        try {
            if (mToolBufferList.size() - 1 > index) {
                return mToolBufferList.size() - 1 - index;
            }
            return 0;
        }
        finally {
            mReadWriteLock.readLock().unlock();
        }
    }

    public void flushCurrentBitmap() {
        mReadWriteLock.writeLock().lock();
        try {
            replaceCurrentBitmap(Bitmap.createBitmap(mCacheBitmap));
            for (int i = 0; i <= index; i ++) {
                drawToolBuffer(mCurrentBitmap, mToolBufferList.get(i));
            }
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    private void replaceCurrentBitmap(Bitmap newBitmap) {
        if (mCurrentBitmap == newBitmap) {
            return;
        }
        Bitmap temp = mCurrentBitmap;
        mCurrentBitmap = newBitmap;
        BitmapUtils.recycle(temp);
    }

    private void replaceCacheBitmap(Bitmap newBitmap) {
        if (mCacheBitmap == newBitmap) {
            return;
        }
        Bitmap temp = mCacheBitmap;
        mCacheBitmap = newBitmap;
        BitmapUtils.recycle(temp);
    }

    private void drawToolBuffer (@NonNull Bitmap bitmap, @NonNull ToolBuffer toolBuffer) {
        Canvas canvas = mCanvas;
        canvas.setBitmap(bitmap);
        switch (toolBuffer.getBufferFlag()) {
            case BufferFlag.MULTIPLE:
                for (ToolBuffer buffer : ((MultiBuffer)toolBuffer).getToolBuffers()) {
                    drawToolBuffer(bitmap, buffer);
                }
                break;
            case BufferFlag.PAINT:
                canvas.drawPath(((PaintBuffer) toolBuffer).getPath(), ((PaintBuffer) toolBuffer).getPaint());
                canvas.save();
                canvas.restore();
                break;
            case BufferFlag.FILL:
                bitmap = new BitmapChanger(bitmap, false).fill(
                        ((FillBuffer) toolBuffer).getFillX(),
                        ((FillBuffer) toolBuffer).getFillY(),
                        ((FillBuffer) toolBuffer).getFillColor()).change();
                break;
            case BufferFlag.SELECTION:
                Bitmap selectedBitmap = Bitmap.createBitmap(bitmap,
                        ((SelectionBuffer) toolBuffer).getSrcX(),
                        ((SelectionBuffer) toolBuffer).getSrcY(),
                        ((SelectionBuffer) toolBuffer).getSrcWidth(),
                        ((SelectionBuffer) toolBuffer).getSrcHeight());
                BitmapChanger selectionChanger = new BitmapChanger(selectedBitmap, false);
                if (((SelectionBuffer) toolBuffer).getRotateBuffer() != null) {
                    selectionChanger.rotateDegrees(((SelectionBuffer) toolBuffer).getRotateBuffer().getDegrees());
                }
                if (((SelectionBuffer) toolBuffer).getFlipVerticalBuffer() != null) {
                    selectionChanger.flipVertically();
                }
                if (((SelectionBuffer) toolBuffer).getFlipHorizontalBuffer() != null) {
                    selectionChanger.flipHorizontally();
                }
                selectedBitmap = selectionChanger.change();
                canvas.drawBitmap(selectedBitmap,
                        ((SelectionBuffer) toolBuffer).getDstX(),
                        ((SelectionBuffer) toolBuffer).getDstY(), mBitmapPaint);
                BitmapUtils.recycle(selectedBitmap);
                canvas.save();
                canvas.restore();
                break;
            case BufferFlag.BITMAP:
                Bitmap bufferBitmap = ((BitmapBuffer) toolBuffer).getBitmap();
                Bitmap tempBitmap;
                BitmapChanger bitmapChanger = new BitmapChanger(bufferBitmap);
                if (((BitmapBuffer) toolBuffer).getRotateBuffer() != null) {
                    bitmapChanger.rotateDegrees(((BitmapBuffer) toolBuffer).getRotateBuffer().getDegrees());
                }
                if (((BitmapBuffer) toolBuffer).getFlipVerticalBuffer() != null) {
                    bitmapChanger.flipVertically();
                }
                if (((BitmapBuffer) toolBuffer).getFlipHorizontalBuffer() != null) {
                    bitmapChanger.flipHorizontally();
                }
                tempBitmap = bitmapChanger.change();
                canvas.drawBitmap(tempBitmap,
                        ((BitmapBuffer) toolBuffer).getX(),
                        ((BitmapBuffer) toolBuffer).getY(), mBitmapPaint);
                BitmapUtils.recycle(tempBitmap);
                canvas.save();
                canvas.restore();
                break;
            case BufferFlag.CLEAR:
                canvas.drawRect(
                        ((ClearBuffer) toolBuffer).getX(),
                        ((ClearBuffer) toolBuffer).getY(),
                        ((ClearBuffer) toolBuffer).getX() +
                                ((ClearBuffer) toolBuffer).getWidth(),
                        ((ClearBuffer) toolBuffer).getY() +
                                ((ClearBuffer) toolBuffer).getHeight(),
                        mEraser);
                canvas.save();
                canvas.restore();
                break;
            case BufferFlag.ROTATE:
                bitmap = new BitmapChanger(bitmap, false).
                        rotateDegrees(((RotateBuffer) toolBuffer).getDegrees()).change();
                break;
            case BufferFlag.FLIP_VERTICAL:
                bitmap = new BitmapChanger(bitmap, false).flipVertically().change();
                break;
            case BufferFlag.FLIP_HORIZONTAL:
                bitmap = new BitmapChanger(bitmap, false).flipHorizontally().change();
                break;
            case BufferFlag.POINT:
                canvas.drawPoint(((PointBuffer)toolBuffer).getPointX(), ((PointBuffer)toolBuffer).getPointY(),
                        ((PointBuffer)toolBuffer).getPaint());
                canvas.save();
                canvas.restore();
                break;
        }
    }

}
