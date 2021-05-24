package com.ansdoship.pixelarteditor.util;

public class DrawCache {

    private int mDrawFlag;

    public final static class DrawFlag {
        public final static int PAINT = 0;
        public final static int GRAPH = 1;
        public final static int ERASER = 2;
        public final static int FILL = 3;
        public final static int SELECTION = 4;
        public final static int COLORIZE = 5;
        public final static int ROTATE = 6;
        public final static int FLIP = 7;
    }

    public DrawCache () {
        mDrawFlag = -1;
    }

    public int getDrawFlag() {
        return mDrawFlag;
    }

}
