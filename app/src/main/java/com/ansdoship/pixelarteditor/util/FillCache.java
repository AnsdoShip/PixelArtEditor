package com.ansdoship.pixelarteditor.util;

public class FillCache extends DrawCache {

    private int mFillX;
    private int mFillY;
    private int mFillColor;

    public FillCache(int fillX, int fillY, int fillColor) {
        mFillX = fillX;
        mFillY = fillY;
        mFillColor = fillColor;
    }

    @Override
    public int getDrawFlag() {
        return DrawFlag.FILL;
    }

    public int getFillX() {
        return mFillX;
    }

    public int getFillY() {
        return mFillY;
    }

    public int getFillColor() {
        return mFillColor;
    }
}
