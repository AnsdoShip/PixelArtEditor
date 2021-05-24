package com.ansdoship.pixelarteditor.util;

public final class Flags {

    public final static class ToolFlag {
        public final static int PAINT = 0;
        public final static int SHAPE = 1;
        public final static int ERASER = 2;
        public final static int FILL = 3;
        public final static int SELECTION = 4;
        public final static int COLORIZE = 5;
    }

    public final static class ShapeFlag {
        public final static int LINE = 0;
        public final static int CIRCLE = 1;
        public final static int ELLIPSE = 2;
        public final static int SQUARE = 3;
        public final static int RECTANGLE = 4;
    }

    public final static class PaintFlag {
        public final static int REPLACE = 0;
        public final static int OVERRIDE = 1;
    }
    
    public final static class SelectionFlag {
    	public final static int ROTATE = 0;
        public final static int FLIP = 1;
    }
    
    public final static class PaletteFlag {
        public final static int BACKGROUND = 0;
        public final static int GRID = 1;
        public final static int INTERNAL = 2;
        public final static int EXTERNAL = 3;
    }

}
