package com.ansdoship.pixelarteditor.editor.buffers;

import com.ansdoship.pixelarteditor.editor.BufferFlag;

public class FlipBuffer extends ToolBuffer {

    private final int mFlipFlag;

    public FlipBuffer(int flipFlag) {
        mFlipFlag = flipFlag;
    }

    public int getFlipFlag() {
        return mFlipFlag;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.FLIP;
    }

}
