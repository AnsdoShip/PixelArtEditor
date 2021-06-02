package com.ansdoship.pixelarteditor.editor.buffer;

import android.graphics.Paint;

public class PointBuffer extends ToolBuffer {

    private final int mPointX;
    private final int mPointY;
    private final Paint mPaint;

    public PointBuffer(Paint paint, int pointX, int pointY) {
        mPaint = new Paint(paint);
        mPointX = pointX;
        mPointY = pointY;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public int getPointX() {
        return mPointX;
    }

    public int getPointY() {
        return mPointY;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.POINT;
    }

}
