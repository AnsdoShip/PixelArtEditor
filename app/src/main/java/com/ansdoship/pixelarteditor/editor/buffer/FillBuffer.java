package com.ansdoship.pixelarteditor.editor.buffer;

public class FillBuffer extends ToolBuffer {

    private final int mFillX;
    private final int mFillY;
    private final int mFillColor;

    public FillBuffer(int fillX, int fillY, int fillColor) {
        mFillX = fillX;
        mFillY = fillY;
        mFillColor = fillColor;
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

    @Override
    public int getBufferFlag() {
        return BufferFlag.FILL;
    }

}
