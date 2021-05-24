package com.ansdoship.pixelarteditor.util;

public class SelectionCache extends DrawCache {

    private int mSrcX;
    private int mSrcY;
    private int mSrcWidth;
    private int mSrcHeight;
    private int mDstX;
    private int mDstY;
    private int mSelectionFlag;
    private RotateCache mRotateCache;
    private FlipCache mFlipCache;

    public final static class SelectionFlag {
        public final static int CUT = 0;
        public final static int COPY = 1;
        public final static int CLEAR = 2;
    }

    public SelectionCache (int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int selectionFlag) {
        this (srcX, srcY, srcWidth, srcHeight, dstX, dstY, selectionFlag, null, null);
    }

    public SelectionCache (int srcX, int srcY, int srcWidth, int srcHeight,
                           int dstX, int dstY, int selectionFlag,
                           RotateCache rotateCache, FlipCache flipCache) {
        mSrcX = srcX;
        mSrcY = srcY;
        mSrcWidth = srcWidth;
        mSrcHeight = srcHeight;
        mDstX = dstX;
        mDstY = dstY;
        mSelectionFlag = selectionFlag;
        mRotateCache = rotateCache;
        mFlipCache = flipCache;
    }

    @Override
    public int getDrawFlag() {
        return DrawFlag.SELECTION;
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

    public RotateCache getRotateCache() {
        return mRotateCache;
    }

    public FlipCache getFlipCache() {
        return mFlipCache;
    }
}
