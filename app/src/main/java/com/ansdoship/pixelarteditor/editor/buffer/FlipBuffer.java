package com.ansdoship.pixelarteditor.editor.buffer;

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
