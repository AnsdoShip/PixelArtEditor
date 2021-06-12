package com.ansdoship.pixelarteditor.editor.buffer;

public class ClearBuffer extends ToolBuffer {

    private final int x;
    private final int y;
    private final int mWidth;
    private final int mHeight;

    public ClearBuffer(int x, int y, int width, int height) {
       this.x = x;
       this.y = y;
       mWidth = width;
       mHeight = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.CLEAR;
    }

}
