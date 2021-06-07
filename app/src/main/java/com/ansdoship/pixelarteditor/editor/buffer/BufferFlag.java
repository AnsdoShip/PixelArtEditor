package com.ansdoship.pixelarteditor.editor.buffer;

public final class BufferFlag {

    public final static int PAINT = 0;

    public final static int FILL = 1;

    public final static int SELECTION = 2;

    public final static int ROTATE = 3;

    public final static int FLIP_VERTICAL = 4;

    public final static int FLIP_HORIZONTAL = 5;

    public final static int POINT = 6;

    public final static class SelectionFlag {

        public final static int CUT = 0;

        public final static int COPY = 1;

        public final static int CLEAR = 2;

    }

    public final static int MULTIPLE = 100;

}
