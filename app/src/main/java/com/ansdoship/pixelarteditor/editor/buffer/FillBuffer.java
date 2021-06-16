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

package com.ansdoship.pixelarteditor.editor.buffer;

public class FillBuffer extends ToolBuffer {

    private final int mFillX;
    private final int mFillY;
    private final int mFillColor;

    public FillBuffer(int fillX, int fillY, int fillColor) {
        mFillX = fillX;
        mFillY = fillY;
        mFillColor = fillColor;
    }

    public int getFillX() {
        return mFillX;
    }

    public int getFillY() {
        return mFillY;
    }

    public int getFillColor() {
        return mFillColor;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.FILL;
    }

}
