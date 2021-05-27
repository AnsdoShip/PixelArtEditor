package com.ansdoship.pixelarteditor.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ansdoship.pixelarteditor.Utils;
import com.ansdoship.pixelarteditor.editor.buffers.SelectionBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.ToolBuffer;
import com.ansdoship.pixelarteditor.view.CanvasView;

public class DrawView extends CanvasView {

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Scale params: distance of two fingers
                double oldDist = 0;
                double newDist = 0;
                float x;
                float y;
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            x = event.getX(0);
                            y = event.getY(0);
                            cacheBitmap = Bitmap.createBitmap(currentBitmap);
                            selected = false;
                            // Draw current bitmap
                            downX = (int) Math.floor((event.getX(0) - imageTranslationX) / imageScale);
                            downY = (int) Math.floor((event.getY(0) - imageTranslationY) / imageScale);
                            moveX = downX;
                            moveY = downY;
                            path.moveTo(downX, downY);
                            switch (drawFlag) {
                                case ToolBuffer.DrawFlag.PAINT:
                                    currentCanvas.drawPoint(downX, downY, paint);
                                    break;
                                case ToolBuffer.DrawFlag.ERASER:
                                    currentCanvas.drawPoint(downX, downY, eraser);
                                    break;
                                case ToolBuffer.DrawFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case SelectionBuffer.SelectionFlag.CUT:
                                        case SelectionBuffer.SelectionFlag.COPY:
                                            selectionBitmapX = downX - (int)(selectionBitmap.getWidth() * 0.5f);
                                            selectionBitmapY = downY - (int)(selectionBitmap.getHeight() * 0.5f);
                                            currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                                            selected = true;
                                            break;
                                        case SelectionBuffer.SelectionFlag.CLEAR:
                                        default:
                                            if (downX < 0) {
                                                downX = 0;
                                            }
                                            if (downX >= currentBitmap.getWidth()) {
                                                downX = currentBitmap.getWidth() - 1;
                                            }
                                            if (downY < 0) {
                                                downY = 0;
                                            }
                                            if (downY >= currentBitmap.getHeight()) {
                                                downY = currentBitmap.getHeight() - 1;
                                            }
                                            break;
                                    }
                                    break;
                                case ToolBuffer.DrawFlag.COLORIZE:
                                    if (downX >=0 && downY >= 0 && downX < currentBitmap.getWidth() && downY < currentBitmap.getHeight()) {
                                        listPalettes.setPaletteColor(listPalettes.getCheckedIndex(), currentBitmap.getPixel(downX, downY));
                                        paint.setColor(currentBitmap.getPixel(downX, downY));
                                        switch (paletteId) {
                                            case BACKGROUND_PALETTE:
                                                if (listPalettes.getCheckedIndex() == 0) {
                                                    setCanvasViewBackgroundColor(currentBitmap.getPixel(downX, downY));
                                                } else {
                                                    flushBackgroundBitmap();
                                                    listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                                                            getCanvasBackgroundColor2());
                                                    canvasView.invalidate();
                                                }
                                                break;
                                            case GRID_PALETTE:
                                                setGridColor(currentBitmap.getPixel(downX, downY));
                                                break;
                                        }
                                    }
                                    break;
                            }
                            currentCanvas.save();
                            currentCanvas.restore();
                            canvasView.invalidate();
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            // Record initial distance
                            oldDist = Utils.spacing(event);
                            newDist = oldDist;
                            scaleMode = true;
                            readOnlyMode = true;

                            selectionFlag = -1;
                            selected = false;

                            currentCanvas.drawPaint(eraser);
                            currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                            currentCanvas.save();
                            currentCanvas.restore();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(scaleMode) {
                                newDist = Utils.spacing(event);
                                // If distance of two fingers > 256
                                // Replace 256 with other value to control sensitivity
                                if(newDist != 0) {
                                    if(newDist >= oldDist + 256) {
                                        setImageScale(imageScale * 2);
                                        oldDist = newDist;
                                    }
                                    if(newDist <= oldDist - 256) {
                                        setImageScale(imageScale / 2);
                                        oldDist = newDist;
                                    }
                                }
                                imageTranslationX += event.getX(0) - x;
                                imageTranslationY += event.getY(0) - y;
                                x = event.getX(0);
                                y = event.getY(0);
                            }
                            else if (!readOnlyMode) {
                                // Current path
                                moveX = (int) Math.floor((event.getX(0) - imageTranslationX) / imageScale);
                                moveY = (int) Math.floor((event.getY(0) - imageTranslationY) / imageScale);
                                switch (drawFlag) {
                                    case ToolBuffer.DrawFlag.PAINT:
                                    case ToolBuffer.DrawFlag.ERASER:
                                        path.lineTo(moveX, moveY);
                                        break;
                                    case ToolBuffer.DrawFlag.GRAPH:
                                        path.reset();
                                        switch (graphFlag) {
                                            case ShapeBuffer.GraphFlag.LINE:
                                                path.moveTo(downX, downY);
                                                path.lineTo(moveX, moveY);
                                                break;
                                            case ShapeBuffer.GraphFlag.CIRCLE:
                                                int circleLeft = Math.min(downX, moveX);
                                                int circleTop = Math.min(downY, moveY);
                                                int circleRight = Math.max(downX, moveX);
                                                int circleBottom = Math.max(downY, moveY);
                                                int circleDiameter = Math.min(Math.abs(circleLeft - circleRight), Math.abs(circleTop - circleBottom));
                                                if (moveX > downX) {
                                                    circleRight = circleLeft + circleDiameter;
                                                }
                                                if (moveY > downY) {
                                                    circleBottom = circleTop + circleDiameter;
                                                }
                                                if (moveX < downX) {
                                                    circleLeft = circleRight - circleDiameter;
                                                }
                                                if (moveY < downY) {
                                                    circleTop = circleBottom - circleDiameter;
                                                }
                                                float circleX = (circleRight + circleLeft) * 0.5f;
                                                float circleY = (circleTop + circleBottom) * 0.5f;
                                                path.addCircle(circleX, circleY, circleDiameter * 0.5f, Path.Direction.CW);
                                                break;
                                            case ShapeBuffer.GraphFlag.OVAL:
                                                int ovalLeft = Math.min(downX, moveX);
                                                int ovalTop = Math.min(downY, moveY);
                                                int ovalRight = Math.max(downX, moveX);
                                                int ovalBottom = Math.max(downY, moveY);
                                                RectF ovalRectF = new RectF(ovalLeft, ovalTop, ovalRight, ovalBottom);
                                                path.addOval(ovalRectF, Path.Direction.CW);
                                                break;
                                            case ShapeBuffer.GraphFlag.SQUARE:
                                                int squareLeft = Math.min(downX, moveX);
                                                int squareTop = Math.min(downY, moveY);
                                                int squareRight = Math.max(downX, moveX);
                                                int squareBottom = Math.max(downY, moveY);
                                                int edgeLength = Math.min(Math.abs(squareLeft - squareRight), Math.abs(squareTop - squareBottom));
                                                if (moveX > downX) {
                                                    squareRight = squareLeft + edgeLength;
                                                }
                                                if (moveY > downY) {
                                                    squareBottom = squareTop + edgeLength;
                                                }
                                                if (moveX < downX) {
                                                    squareLeft = squareRight - edgeLength;
                                                }
                                                if (moveY < downY) {
                                                    squareTop = squareBottom - edgeLength;
                                                }
                                                RectF squareRectF = new RectF(squareLeft, squareTop, squareRight, squareBottom);
                                                path.addRect(squareRectF, Path.Direction.CW);
                                                break;
                                            case ShapeBuffer.GraphFlag.RECTANGLE:
                                                int rectLeft = Math.min(downX, moveX);
                                                int rectTop = Math.min(downY, moveY);
                                                int rectRight = Math.max(downX, moveX);
                                                int rectBottom = Math.max(downY, moveY);
                                                RectF rectRectF = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                                                path.addRect(rectRectF, Path.Direction.CW);
                                                break;
                                        }
                                        break;
                                    case ToolBuffer.DrawFlag.SELECTION:
                                        switch (selectionFlag) {
                                            case SelectionBuffer.SelectionFlag.CUT:
                                            case SelectionBuffer.SelectionFlag.COPY:
                                                break;
                                            case SelectionBuffer.SelectionFlag.CLEAR:
                                            default:
                                                if (moveX < 0) {
                                                    moveX = 0;
                                                }
                                                if (moveX >= currentBitmap.getWidth()) {
                                                    moveX = currentBitmap.getWidth() - 1;
                                                }
                                                if (moveY < 0) {
                                                    moveY = 0;
                                                }
                                                if (moveY >= currentBitmap.getHeight()) {
                                                    moveY = currentBitmap.getHeight() - 1;
                                                }
                                                break;
                                        }
                                        selected = true;
                                        break;
                                    case ToolBuffer.DrawFlag.COLORIZE:
                                        if (moveX >=0 && moveY >= 0 && moveX < currentBitmap.getWidth() && moveY < currentBitmap.getHeight()) {
                                            listPalettes.setPaletteColor(listPalettes.getCheckedIndex(), currentBitmap.getPixel(moveX, moveY));
                                            paint.setColor(currentBitmap.getPixel(moveX, moveY));
                                            switch (paletteId) {
                                                case BACKGROUND_PALETTE:
                                                    if (listPalettes.getCheckedIndex() == 0) {
                                                        setCanvasViewBackgroundColor(currentBitmap.getPixel(moveX, moveY));
                                                    } else {
                                                        flushBackgroundBitmap();
                                                        listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                                                                getCanvasBackgroundColor2());
                                                        canvasView.invalidate();
                                                    }
                                                    break;
                                                case GRID_PALETTE:
                                                    setGridColor(currentBitmap.getPixel(moveX, moveY));
                                                    break;
                                            }
                                        }
                                        break;
                                }
                                // Clear canvas
                                currentCanvas.drawPaint(eraser);
                                // Draw cache bitmap
                                currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                                currentCanvas.save();
                                currentCanvas.restore();
                                // Draw path
                                if (drawFlag != ToolBuffer.DrawFlag.ERASER) {
                                    currentCanvas.drawPath(path, paint);
                                }
                                else {
                                    currentCanvas.drawPath(path, eraser);
                                }
                                switch (drawFlag) {
                                    // Draw down point
                                    case ToolBuffer.DrawFlag.PAINT:
                                        currentCanvas.drawPoint(downX, downY, paint);
                                        break;
                                    case ToolBuffer.DrawFlag.ERASER:
                                        currentCanvas.drawPoint(downX, downY, eraser);
                                        break;
                                    // Draw selection bmp
                                    case ToolBuffer.DrawFlag.SELECTION:
                                        switch (selectionFlag) {
                                            case SelectionBuffer.SelectionFlag.CUT:
                                            case SelectionBuffer.SelectionFlag.COPY:
                                                selectionBitmapX = moveX - (int)(selectionBitmap.getWidth() * 0.5f);
                                                selectionBitmapY = moveY - (int)(selectionBitmap.getHeight() * 0.5f);
                                                currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                                                break;
                                        }
                                        break;
                                }
                                currentCanvas.save();
                                currentCanvas.restore();
                            }
                            canvasView.invalidate();
                            break;
                        case MotionEvent.ACTION_UP:
                            upX = (int) Math.floor((event.getX(0) - imageTranslationX) / imageScale);
                            upY = (int) Math.floor((event.getY(0) - imageTranslationY) / imageScale);
                            if (readOnlyMode) {
                                readOnlyMode = false;
                            }
                            else {
                                // Draw current bitmap
                                switch (drawFlag) {
                                    case ToolBuffer.DrawFlag.FILL:
                                        if (downX >=0 && downY >= 0 && downX < currentBitmap.getWidth() && downY < currentBitmap.getHeight()) {
                                            DrawUtils.fill(currentBitmap, downX, downY, paint.getColor());
                                        }
                                        break;
                                    case ToolBuffer.DrawFlag.SELECTION:
                                        switch (selectionFlag) {
                                            case SelectionBuffer.SelectionFlag.CUT:
                                            case SelectionBuffer.SelectionFlag.COPY:
                                                // Clear canvas
                                                currentCanvas.drawPaint(eraser);
                                                // Draw cache bitmap
                                                currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                                                currentCanvas.save();
                                                currentCanvas.restore();
                                                buildSelectionPopup2();
                                                break;
                                            case SelectionBuffer.SelectionFlag.CLEAR:
                                            default:
                                                buildSelectionPopup1();
                                                break;
                                        }
                                        break;
                                }
                                if (drawFlag != ToolBuffer.DrawFlag.SELECTION) {
                                    currentCanvas.save();
                                    currentCanvas.restore();
                                    canvasView.invalidate();
                                }
                            }
                            path.reset();
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            if (event.getPointerCount() <= 2) {
                                if(scaleMode) {
                                    scaleMode = false;
                                }
                            }
                            break;
                    }
                    return true;
                }
            }
        });

        setOnInvalidateListener(new OnInvalidateListener() {
            @Override
            public void onInvalidate(Canvas canvas) {
                // Clear canvas
                canvas.drawPaint(eraser);
                canvas.save();
                canvas.restore();
                // Set matrix
                matrix.setTranslate(imageTranslationX / imageScale, imageTranslationY / imageScale);
                matrix.postScale(imageScale, imageScale);
                // Draw background
                canvas.drawColor(getCanvasViewBackgroundColor());
                canvas.drawBitmap(backgroundBitmap, imageTranslationX, imageTranslationY, backgroundPaint);
                canvas.save();
                canvas.restore();
                // Draw scaled bitmap
                canvas.drawBitmap(currentBitmap, matrix, bitmapPaint);
                canvas.save();
                canvas.restore();
                // Draw grid
                if(drawGrid) {
                    if(imageScale >= 4) {
                        canvas.drawBitmap(gridBitmap, imageTranslationX, imageTranslationY, bitmapPaint);
                        canvas.save();
                        canvas.restore();
                    }
                }
                // Draw selection board
                if (drawFlag == ToolBuffer.DrawFlag.SELECTION && selected) {
                    int selectionLeft;
                    int selectionTop;
                    int selectionRight;
                    int selectionBottom;
                    RectF selectionRectF;
                    switch (selectionFlag) {
                        case SelectionBuffer.SelectionFlag.CUT:
                        case SelectionBuffer.SelectionFlag.COPY:
                            selectionLeft = selectionBitmapX * imageScale + imageScale / 2;
                            selectionTop = selectionBitmapY * imageScale + imageScale / 2;
                            selectionRight = selectionLeft + (selectionBitmap.getWidth() - 1) * imageScale;
                            selectionBottom = selectionTop + (selectionBitmap.getHeight() - 1) * imageScale;
                            break;
                        case SelectionBuffer.SelectionFlag.CLEAR:
                        default:
                            selectionLeft = Math.min(downX, moveX) * imageScale + imageScale / 2;
                            selectionTop = Math.min(downY, moveY) * imageScale + imageScale / 2;
                            selectionRight = Math.max(downX, moveX) * imageScale + imageScale / 2;
                            selectionBottom = Math.max(downY, moveY) * imageScale + imageScale / 2;
                            break;
                    }
                    selectionRectF = new RectF(selectionLeft, selectionTop, selectionRight, selectionBottom);
                    canvas.drawRect(selectionRectF, selectionPaint1);
                    canvas.drawRect(selectionRectF, selectionPaint2);
                }
            }
        });

    }

}
