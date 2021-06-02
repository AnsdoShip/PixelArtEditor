package com.ansdoship.pixelarteditor.editor.buffer;

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
