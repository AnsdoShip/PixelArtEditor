package com.ansdoship.pixart.util;

import android.graphics.Paint;
import android.graphics.Path;

public class PaintCache extends DrawCache {

    private Paint mPaint;
    private Path mPath;

    public final static class PaintFlag {
        public final static int REPLACE = 0;
        public final static int OVERRIDE = 1;
    }

    public PaintCache (Paint paint, Path path) {
        mPaint = new Paint(paint);
        mPath = new Path(path);
    }

    @Override
    public int getDrawFlag() {
        return DrawFlag.PAINT;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Path getPaintPath() {
        return mPath;
    }

}
