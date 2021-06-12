package com.ansdoship.pixelarteditor.editor.buffer;

public class SelectionBuffer extends ToolBuffer {

    private final int mSrcX;
    private final int mSrcY;
    private final int mSrcWidth;
    private final int mSrcHeight;
    private final int mDstX;
    private final int mDstY;
    private final RotateBuffer mRotateBuffer;
    private final FlipVerticalBuffer mFlipVerticalBuffer;
    private final FlipHorizontalBuffer mFlipHorizontalBuffer;

    public SelectionBuffer(int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY) {
        this (srcX, srcY, srcWidth, srcHeight, dstX, dstY, null, null, null);
    }

    public SelectionBuffer(int srcX, int srcY, int srcWidth, int srcHeight,
                           int dstX, int dstY, RotateBuffer rotateBuffer, FlipVerticalBuffer flipVerticalBuffer,
                           FlipHorizontalBuffer flipHorizontalBuffer) {
        mSrcX = srcX;
        mSrcY = srcY;
        mSrcWidth = srcWidth;
        mSrcHeight = srcHeight;
        mDstX = dstX;
        mDstY = dstY;
        mRotateBuffer = rotateBuffer;
        mFlipVerticalBuffer = flipVerticalBuffer;
        mFlipHorizontalBuffer = flipHorizontalBuffer;
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

    public RotateBuffer getRotateBuffer() {
        return mRotateBuffer;
    }

    public FlipVerticalBuffer getFlipVerticalBuffer() {
        return mFlipVerticalBuffer;
    }

    public FlipHorizontalBuffer getFlipHorizontalBuffer() {
        return mFlipHorizontalBuffer;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.SELECTION;
    }

}
