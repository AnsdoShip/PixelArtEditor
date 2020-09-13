package com.ansdoship.pixart.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BitmapCache {

    private Bitmap mCacheBitmap;
    private Bitmap mCurrentBitmap;
    private List<DrawCache> mDrawCacheList;
    private int index;
    private int maxSize;

    private BitmapCache (Bitmap cacheBitmap, int maxSize) {
        mCacheBitmap = cacheBitmap;
        mCurrentBitmap = Bitmap.createBitmap(cacheBitmap);
        mDrawCacheList = new ArrayList<>();
        index = 0;
        this.maxSize = maxSize;
    }

    public BitmapCache createBitmapCache (Bitmap cacheBitmap, int maxSize) {
        return new BitmapCache(cacheBitmap, maxSize);
    }

    public BitmapCache createBitmapCache (Bitmap cacheBitmap, DrawCache drawCache, int maxSize) {
        BitmapCache result =  new BitmapCache(cacheBitmap, maxSize);
        result.addDrawCache(drawCache);
        return result;
    }

    public BitmapCache createBitmapCache (Bitmap cacheBitmap, DrawCache[] drawCaches, int maxSize) {
        BitmapCache result =  new BitmapCache(cacheBitmap, maxSize);
        result.addDrawCaches(drawCaches);
        return result;
    }

    public BitmapCache createBitmapCache (Bitmap cacheBitmap, List<DrawCache> drawCacheList, int maxSize) {
        BitmapCache result =  new BitmapCache(cacheBitmap, maxSize);
        result.addDrawCacheList(drawCacheList);
        return result;
    }

    public void addDrawCache (DrawCache drawCache) {
        if (drawCache == null) {
            return;
        }
        if (getRedoCount() > 0) {
            mDrawCacheList.subList(0, index);
        }
        mDrawCacheList.add(drawCache);
        int size = mDrawCacheList.size();
        index ++;
        if (size > maxSize) {
            index = maxSize;
            int subIndex = size - maxSize;
            for (int i = 0; i < subIndex; i++) {
                drawDrawCache(mCacheBitmap, mDrawCacheList.get(i));
            }
            mCurrentBitmap = Bitmap.createBitmap(mCacheBitmap);
            mDrawCacheList.subList(subIndex, size);
        }
    }

    public void addDrawCaches (DrawCache[] drawCaches) {
        List<DrawCache> tempList = CollectionUtils.removeNullElements(Arrays.asList(drawCaches));
        if (tempList.isEmpty()) {
            return;
        }
        if (getRedoCount() > 0) {
            mDrawCacheList.subList(0, index);
        }
        int originalSize = mDrawCacheList.size();
        mDrawCacheList.addAll(tempList);
        int size = mDrawCacheList.size();
        index += (size - originalSize);
        if (size > maxSize) {
            index = maxSize;
            int subIndex = size - maxSize;
            for (int i = 0; i < subIndex; i++) {
                drawDrawCache(mCacheBitmap, mDrawCacheList.get(i));
            }
            mCurrentBitmap = Bitmap.createBitmap(mCacheBitmap);
            mDrawCacheList.subList(subIndex, size);
        }
    }

    public void addDrawCacheList (List<DrawCache> drawCacheList) {
        CollectionUtils.removeNullElements(drawCacheList);
        if (drawCacheList.isEmpty()) {
            return;
        }
        if (getRedoCount() > 0) {
            mDrawCacheList.subList(0, index);
        }
        int originalSize = mDrawCacheList.size();
        mDrawCacheList.addAll(drawCacheList);
        int size = mDrawCacheList.size();
        index += (size - originalSize);
        if (size > maxSize) {
            index = maxSize;
            int subIndex = size - maxSize;
            for (int i = 0; i < subIndex; i++) {
                drawDrawCache(mCacheBitmap, mDrawCacheList.get(i));
            }
            mCurrentBitmap = Bitmap.createBitmap(mCacheBitmap);
            mDrawCacheList.subList(subIndex, size);
        }
    }

    public Bitmap getCacheBitmap() {
        return mCacheBitmap;
    }

    public Bitmap getCurrentBitmap () {
        return mCurrentBitmap;
    }

    public void undo () {
        index -= 1;
        if (index < 0) {
            index = 0;
            return;
        }
        mCurrentBitmap = Bitmap.createBitmap(mCacheBitmap);
        for (int i = 0; i < index; i++) {
            drawDrawCache(mCurrentBitmap, mDrawCacheList.get(i));
        }
    }

    public void redo () {
        index += 1;
        if (index > maxSize) {
            index = maxSize;
            return;
        }
        mCurrentBitmap = Bitmap.createBitmap(mCacheBitmap);
        for (int i = 0; i < index; i++) {
            drawDrawCache(mCurrentBitmap, mDrawCacheList.get(i));
        }
    }

    public int getUndoCount () {
        return index;
    }

    public int getRedoCount () {
        if (mDrawCacheList.size() > index) {
            return mDrawCacheList.size() - index;
        }
        return 0;
    }

    private void drawDrawCache (Bitmap bitmap, DrawCache drawCache) {
        Canvas canvas = new Canvas(bitmap);
        switch (drawCache.getDrawFlag()) {
            case DrawCache.DrawFlag.PAINT:
                canvas.drawPath(((PaintCache)drawCache).getPaintPath(), ((PaintCache)drawCache).getPaint());
                break;
            case DrawCache.DrawFlag.GRAPH:
                canvas.drawPath(((GraphCache)drawCache).getPaintPath(), ((GraphCache)drawCache).getPaint());
                break;
            case DrawCache.DrawFlag.ERASER:
                canvas.drawPath(((EraserCache)drawCache).getPaintPath(), ((EraserCache)drawCache).getPaint());
                break;
            case DrawCache.DrawFlag.FILL:
                DrawUtils.fill(bitmap, ((FillCache)drawCache).getFillX(), ((FillCache)drawCache).getFillY(),
                        ((FillCache)drawCache).getFillColor());
                break;
            case DrawCache.DrawFlag.SELECTION:
                Bitmap tempBitmap = Bitmap.createBitmap(bitmap, ((SelectionCache)drawCache).getSrcX(),
                        ((SelectionCache)drawCache).getSrcY(), ((SelectionCache)drawCache).getSrcWidth(),
                        ((SelectionCache)drawCache).getSrcHeight());
                canvas.drawBitmap(tempBitmap, ((SelectionCache)drawCache).getDstX(), ((SelectionCache)drawCache).getDstY(),
                        new Paint());
                break;
            case DrawCache.DrawFlag.ROTATE:
                BitmapUtils.rotateBitmap(bitmap, ((RotateCache)drawCache).getDegrees());
                break;
            case DrawCache.DrawFlag.FLIP:
                switch (((FlipCache)drawCache).getFlipFlag()) {
                    case FlipCache.FlipFlag.VERTICAL:
                        BitmapUtils.flipBitmapVertically(bitmap);
                        break;
                    case FlipCache.FlipFlag.HORIZONTAL:
                        BitmapUtils.flipBitmapHorizontally(bitmap);
                        break;
                }
                break;
        }
    }

    public static BitmapCache decodeFile (String pathAndName) {
        File file = new File(pathAndName);
        if (file.exists() && pathAndName.endsWith(".cache")) {
            try {
                ObjectInputStream ObjectIS = new ObjectInputStream(new FileInputStream(file));
                return (BitmapCache) ObjectIS.readObject();
            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public boolean saveToFile (String pathAndName, boolean override) {
        boolean result = false;
        File file = new File(pathAndName + ".cache");
        if(!file.exists()) {
            try
            {
                result = file.createNewFile();
                ObjectOutputStream ObjectOS = new ObjectOutputStream(new FileOutputStream(file));
                ObjectOS.writeObject(this);
                ObjectOS.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                result = false;
            }
        }
        else if(file.exists()) {
            if(override) {
                try
                {
                    result = file.createNewFile();
                    ObjectOutputStream ObjectOS = new ObjectOutputStream(new FileOutputStream(file));
                    ObjectOS.writeObject(this);
                    ObjectOS.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    result = false;
                }
            }
        }
        return result;
    }

}
