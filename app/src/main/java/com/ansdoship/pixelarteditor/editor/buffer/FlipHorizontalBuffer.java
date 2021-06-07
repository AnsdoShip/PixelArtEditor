package com.ansdoship.pixelarteditor.editor.buffer;

public class FlipHorizontalBuffer extends ToolBuffer {

    public FlipHorizontalBuffer() {}

    @Override
    public int getBufferFlag() {
        return BufferFlag.FLIP_HORIZONTAL;
    }

}
