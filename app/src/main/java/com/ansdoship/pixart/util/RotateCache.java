package com.ansdoship.pixart.util;

public class RotateCache extends DrawCache {

    private int mDegrees;

    public RotateCache(int degrees) {
        mDegrees = degrees;
    }

    @Override
    public int getDrawFlag() {
        return DrawFlag.ROTATE;
    }

    public int getDegrees() {
        return mDegrees;
    }
}
