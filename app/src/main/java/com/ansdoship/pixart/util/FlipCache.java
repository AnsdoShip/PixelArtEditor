package com.ansdoship.pixart.util;

public class FlipCache extends DrawCache {

    private int mFlipFlag;

    public final static class FlipFlag {
        public final static int VERTICAL = 0;
        public final static int HORIZONTAL = 1;
    }

    public FlipCache(int flipFlag) {
        mFlipFlag = flipFlag;
    }

    @Override
    public int getDrawFlag() {
        return DrawFlag.FLIP;
    }

    public int getFlipFlag() {
        return mFlipFlag;
    }
}
