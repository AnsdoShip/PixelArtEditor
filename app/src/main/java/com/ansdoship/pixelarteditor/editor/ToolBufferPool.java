package com.ansdoship.pixelarteditor.editor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.ansdoship.pixelarteditor.editor.buffers.FillBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.FlipBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.MultiBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.PaintBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.PointBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.RotateBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.SelectionBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.ToolBuffer;
import com.ansdoship.pixelarteditor.graphics.BitmapChanger;
import com.ansdoship.pixelarteditor.graphics.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public final class ToolBufferPool {

    private Bitmap mCacheBitmap;
    private Bitmap mCurrentBitmap;
    private List<ToolBuffer> mToolBufferList;
    private int index;
    private final int maxSize;

    private boolean tempMode;

    private ToolBufferPool(Bitmap cacheBitmap, int maxSize, boolean isCopy) {
        if (isCopy) {
            mCacheBitmap = Bitmap.createBitmap(cacheBitmap);
        }
        else {
            mCacheBitmap = cacheBitmap;
        }
        tempMode = false;
        mCurrentBitmap = Bitmap.createBitmap(mCacheBitmap);
        mToolBufferList = new ArrayList<>(maxSize);
        index = -1;
        this.maxSize = maxSize;
    }

    @NonNull
    public static ToolBufferPool createToolBufferPool (@NonNull Bitmap cacheBitmap, int maxSize, boolean isCopy) {
        return new ToolBufferPool(cacheBitmap, maxSize, isCopy);
    }

    public void addToolBuffer (@NonNull ToolBuffer toolBuffer) {
        setTempModeDisabled();
        drawToolBuffer(mCurrentBitmap, toolBuffer);
        if (getRedoCount() > 0) {
            mToolBufferList = mToolBufferList.subList(0, index + 1);
        }
        mToolBufferList.add(toolBuffer);
        int size = mToolBufferList.size();
        index ++;
        if (size > maxSize) {
            index = maxSize - 1;
            int subIndex = size - maxSize;
            for (int i = 0; i < subIndex; i++) {
                drawToolBuffer(mCacheBitmap, mToolBufferList.get(i));
            }
            replaceCacheBitmap(Bitmap.createBitmap(mCacheBitmap));
            mToolBufferList = mToolBufferList.subList(subIndex, size);
        }
    }

    private void setTempModeEnabled () {
        if (tempMode) {
            return;
        }
        tempMode = true;
    }

    private void setTempModeDisabled () {
        if (!tempMode) {
            return;
        }
        tempMode = false;
        flushCurrentBitmap();
    }

    public void addTempToolBuffer (@NonNull ToolBuffer toolBuffer) {
        setTempModeEnabled();
        drawToolBuffer(mCurrentBitmap, toolBuffer);
    }

    public void clearTempToolBuffers() {
        if (tempMode) {
            flushCurrentBitmap();
        }
    }

    public Bitmap getCacheBitmap() {
        return mCacheBitmap;
    }

    public Bitmap getCurrentBitmap () {
        return mCurrentBitmap;
    }

    public void undo () {
        index -= 1;
        if (index < -1) {
            index = -1;
            return;
        }
        flushCurrentBitmap();
    }

    public void redo () {
        index += 1;
        if (index >= mToolBufferList.size()) {
            index = mToolBufferList.size() - 1;
            return;
        }
        flushCurrentBitmap();
    }

    public int getUndoCount () {
        return index + 1;
    }

    public int getRedoCount () {
        if (mToolBufferList.size() - 1 > index) {
            return mToolBufferList.size() - 1 - index;
        }
        return 0;
    }

    private void flushCurrentBitmap() {
        replaceCurrentBitmap(Bitmap.createBitmap(mCacheBitmap));
        for (int i = 0; i <= index; i ++) {
            drawToolBuffer(mCurrentBitmap, mToolBufferList.get(i));
        }
    }

    private void replaceCurrentBitmap(Bitmap newBitmap) {
        Bitmap temp = mCurrentBitmap;
        mCurrentBitmap = newBitmap;
        BitmapUtils.recycleBitmap(temp);
    }

    private void replaceCacheBitmap(Bitmap newBitmap) {
        Bitmap temp = mCacheBitmap;
        mCacheBitmap = newBitmap;
        BitmapUtils.recycleBitmap(temp);
    }

    private void drawToolBuffer (@NonNull Bitmap bitmap, @NonNull ToolBuffer toolBuffer) {
        Canvas canvas = new Canvas(bitmap);
        switch (toolBuffer.getBufferFlag()) {
            case BufferFlag.MULTIPLE:
                for (ToolBuffer buffer : ((MultiBuffer)toolBuffer).getToolBuffers()) {
                    drawToolBuffer(bitmap, buffer);
                }
                break;
            case BufferFlag.PAINT:
                canvas.drawPath(((PaintBuffer) toolBuffer).getPath(), ((PaintBuffer) toolBuffer).getPaint());
                break;
            case BufferFlag.FILL:
                new BitmapChanger(bitmap, false).fill(
                        ((FillBuffer) toolBuffer).getFillX(),
                        ((FillBuffer) toolBuffer).getFillY(),
                        ((FillBuffer) toolBuffer).getFillColor());
                break;
            case BufferFlag.SELECTION:
                switch (((SelectionBuffer) toolBuffer).getSelectionFlag()) {
                    case BufferFlag.SelectionFlag.CLEAR:
                        // FIXME: CLEAR EVENT
                        break;
                    case BufferFlag.SelectionFlag.CUT:
                        // FIXME: CLEAR EVENT
                    case BufferFlag.SelectionFlag.COPY:
                        Bitmap selectedBitmap = Bitmap.createBitmap(bitmap, ((SelectionBuffer) toolBuffer).getSrcX(),
                                ((SelectionBuffer) toolBuffer).getSrcY(), ((SelectionBuffer) toolBuffer).getSrcWidth(),
                                ((SelectionBuffer) toolBuffer).getSrcHeight());
                        if (((SelectionBuffer) toolBuffer).getRotateBuffer() != null) {
                            new BitmapChanger(selectedBitmap, false).
                                    rotateDegrees(((SelectionBuffer) toolBuffer).getRotateBuffer().getDegrees());
                        }
                        if (((SelectionBuffer) toolBuffer).getFlipBuffer() != null) {
                            switch (((SelectionBuffer) toolBuffer).getFlipBuffer().getFlipFlag()) {
                                case BufferFlag.FlipFlag.VERTICAL:
                                    new BitmapChanger(selectedBitmap, false).flipVertically();
                                    break;
                                case BufferFlag.FlipFlag.HORIZONTAL:
                                    new BitmapChanger(selectedBitmap, false).flipHorizontally();
                                    break;
                            }
                        }
                        canvas.drawBitmap(selectedBitmap, ((SelectionBuffer) toolBuffer).getDstX(),
                                ((SelectionBuffer) toolBuffer).getDstY(), new Paint());
                        BitmapUtils.recycleBitmap(selectedBitmap);
                        break;
                }
                break;
            case BufferFlag.ROTATE:
                new BitmapChanger(bitmap, false).
                        rotateDegrees(((RotateBuffer) toolBuffer).getDegrees());
                break;
            case BufferFlag.FLIP:
                switch (((FlipBuffer) toolBuffer).getFlipFlag()) {
                    case BufferFlag.FlipFlag.VERTICAL:
                        new BitmapChanger(bitmap, false).flipVertically();
                        break;
                    case BufferFlag.FlipFlag.HORIZONTAL:
                        new BitmapChanger(bitmap, false).flipHorizontally();
                        break;
                }
                break;
           case BufferFlag.POINT:
                canvas.drawPoint(((PointBuffer)toolBuffer).getPointX(), ((PointBuffer)toolBuffer).getPointY(),
                        ((PointBuffer)toolBuffer).getPaint());
                break;
        }
    }

}
