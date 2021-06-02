package com.ansdoship.pixelarteditor.editor.buffers;

import com.ansdoship.pixelarteditor.editor.BufferFlag;

public class MultiBuffer extends ToolBuffer {

    private final ToolBuffer[] toolBuffers;

    public MultiBuffer (ToolBuffer... toolBuffers) {
        this.toolBuffers = toolBuffers;
    }

    public ToolBuffer[] getToolBuffers() {
        return toolBuffers;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.MULTIPLE;
    }

}
