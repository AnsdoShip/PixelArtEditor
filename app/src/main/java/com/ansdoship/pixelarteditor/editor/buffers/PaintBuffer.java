package com.ansdoship.pixelarteditor.editor.buffers;

import android.graphics.Paint;
import android.graphics.Path;

import com.ansdoship.pixelarteditor.editor.BufferFlag;

public class PaintBuffer extends ToolBuffer {

    private final Paint mPaint;
    private final Path mPath;

    public PaintBuffer(Paint paint, Path path) {
        mPaint = new Paint(paint);
        mPath = new Path(path);
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Path getPath() {
        return mPath;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.PAINT;
    }

}
