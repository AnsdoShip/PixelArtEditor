package com.ansdoship.pixart.util;

import android.graphics.Paint;
import android.graphics.Path;

public class EraserCache extends DrawCache {

    private Paint mPaint;
    private Path mPath;

    public EraserCache(Paint paint, Path path) {
        mPaint = new Paint(paint);
        mPath = new Path(path);
    }

    @Override
    public int getDrawFlag() {
        return DrawFlag.ERASER;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Path getPaintPath() {
        return mPath;
    }

}
