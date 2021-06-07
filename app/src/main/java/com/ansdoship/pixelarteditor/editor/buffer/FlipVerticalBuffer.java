package com.ansdoship.pixelarteditor.editor.buffer;

public class FlipVerticalBuffer extends ToolBuffer {

    public FlipVerticalBuffer() {}

    @Override
    public int getBufferFlag() {
        return BufferFlag.FLIP_VERTICAL;
    }

}
