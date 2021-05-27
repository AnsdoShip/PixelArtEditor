package com.ansdoship.pixelarteditor.editor.buffers;

import com.ansdoship.pixelarteditor.editor.BufferFlag;

public class RotateBuffer extends ToolBuffer {

    private final int mDegrees;

    public RotateBuffer(int degrees) {
        mDegrees = degrees;
    }

    public int getDegrees() {
        return mDegrees;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.ROTATE;
    }

}
