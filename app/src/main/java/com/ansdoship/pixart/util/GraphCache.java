package com.ansdoship.pixart.util;

import android.graphics.Paint;
import android.graphics.Path;

public class GraphCache extends DrawCache {

    private Paint mPaint;
    private Path mPath;

    public final static class GraphFlag {
        public final static int LINE = 0;
        public final static int CIRCLE = 1;
        public final static int OVAL = 2;
        public final static int SQUARE = 3;
        public final static int RECTANGLE = 4;
    }

    public GraphCache (Paint paint, Path path) {
        mPaint = new Paint(paint);
        mPath = new Path(path);
    }

    @Override
    public int getDrawFlag() {
        return DrawFlag.GRAPH;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Path getPaintPath() {
        return mPath;
    }

}
