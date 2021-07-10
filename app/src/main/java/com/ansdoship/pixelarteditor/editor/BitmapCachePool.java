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

package com.ansdoship.pixelarteditor.editor;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.tianscar.quickbitmap.BitmapPool;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class BitmapCachePool {

    private static final int INDEX_DEFAULT = -1;

    private int index;
    private final ReentrantReadWriteLock mReadWriteLock;
    private final BitmapPool mBitmapPool;

    public BitmapCachePool() {
        mBitmapPool = new BitmapPool(BitmapPool.getDefaultDirectory(),
                BitmapPool.getDefaultLruCacheMaxSize(),
                BitmapPool.getDefaultDiskLruCacheMaxSize() * 2);
        index = INDEX_DEFAULT;
        mReadWriteLock = new ReentrantReadWriteLock(true);
    }

    public void add (@NonNull Bitmap bitmap) {
        mReadWriteLock.writeLock().lock();
        try {
            currentIndex += 1;
            endIndex -= redoCount();
            endIndex += 1;
            mBitmapPool.put(Integer.toString(currentIndex), bitmap);
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    @NonNull
    public Bitmap current () {
        mReadWriteLock.readLock().lock();
        Bitmap result;
        try {
            result = Objects.requireNonNull(mBitmapPool.get(Integer.toString(currentIndex)));
        }
        finally {
            mReadWriteLock.readLock().unlock();
        }
        return result;
    }

    public void undo () {
        mReadWriteLock.writeLock().lock();
        try {
            currentIndex -= 1;
            int startIndex = getStartIndex();
            if (currentIndex < startIndex) {
                currentIndex = startIndex;
            }
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    public void redo () {
        mReadWriteLock.writeLock().lock();
        try {
            currentIndex += 1;
            int endIndex = getEndIndex();
            if (currentIndex > endIndex) {
                currentIndex = endIndex;
            }
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

    public int undoCount () {
        mReadWriteLock.readLock().lock();
        int result;
        try {
            int startIndex = getStartIndex();
            result = currentIndex - startIndex;
        }
        finally {
            mReadWriteLock.readLock().unlock();
        }
        return result;
    }

    public int redoCount () {
        mReadWriteLock.readLock().lock();
        int result;
        try {
            int endIndex = getEndIndex();
            result = endIndex - currentIndex;
        }
        finally {
            mReadWriteLock.readLock().unlock();
        }
        return result;
    }

    private int getStartIndex() {
        if (mBitmapPool.get(Integer.toString(startIndex)) != null) {
            return startIndex;
        }
        while (mBitmapPool.get(Integer.toString(startIndex)) == null) {
            startIndex ++;
            if (startIndex >= endIndex) {
                resetIndex();
                break;
            }
        }
        return startIndex;
    }

    private int getEndIndex() {
        if (mBitmapPool.get(Integer.toString(endIndex)) != null) {
            return endIndex;
        }
        while (mBitmapPool.get(Integer.toString(endIndex)) == null) {
            endIndex --;
            if (endIndex <= startIndex) {
                resetIndex();
                break;
            }
        }
        return endIndex;
    }

    private void resetIndex() {
        startIndex = endIndex = currentIndex = INDEX_DEFAULT;
        mBitmapPool.release();
    }

    public boolean isEmpty() {
        return getStartIndex() == getEndIndex();
    }

    public void release() {
        mReadWriteLock.writeLock().lock();
        try {
            mBitmapPool.release();
        }
        finally {
            mReadWriteLock.writeLock().unlock();
        }
    }

}
