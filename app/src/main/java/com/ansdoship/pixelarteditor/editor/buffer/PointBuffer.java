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

import android.graphics.Paint;

public class PointBuffer extends ToolBuffer {

    private final float mPointX;
    private final float mPointY;
    private final Paint mPaint;

    public PointBuffer(Paint paint, float pointX, float pointY) {
        mPaint = new Paint(paint);
        mPointX = pointX;
        mPointY = pointY;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public float getPointX() {
        return mPointX;
    }

    public float getPointY() {
        return mPointY;
    }

    @Override
    public int getBufferFlag() {
        return BufferFlag.POINT;
    }

}
