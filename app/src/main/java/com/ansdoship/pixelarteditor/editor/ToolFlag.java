/*
 * Copyright (C) 2021 AnsdoShip Studio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.ansdoship.pixelarteditor.editor;

public final class ToolFlag {

    public final static int PAINT = 0;

    public final static int SHAPE = 1;

    public final static int ERASER = 2;

    public final static int FILL = 3;

    public final static int SELECTION = 4;

    public final static int COLORIZE = 5;

    public static final class ShapeFlag {

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

        public final static int NONE = -1;

        public final static int SELECTED = 0;

        public final static int CUT = 1;

        public final static int COPY = 2;

    }

}
