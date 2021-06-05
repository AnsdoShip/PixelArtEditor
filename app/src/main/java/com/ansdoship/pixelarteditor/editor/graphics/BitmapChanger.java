package com.ansdoship.pixelarteditor.editor.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;

import androidx.annotation.NonNull;

import com.ansdoship.pixelarteditor.util.MathUtils;

import java.util.Stack;

public final class BitmapChanger {

    private Bitmap dst;
    private final Canvas canvas;
    private final Paint paint;
    private final Path path;
    private final Matrix matrix;

    public BitmapChanger(@NonNull Bitmap src) {
        this(src, true);
    }

    public BitmapChanger(@NonNull Bitmap src, boolean isCopy) {
        if (isCopy) {
            dst = Bitmap.createBitmap(src);
        }
        else {
            dst = src;
        }
        paint = new Paint();
        paint.setAntiAlias(false);
        canvas = new Canvas();
        path = new Path();
        matrix = new Matrix();
    }

    public BitmapChanger cut (int x, int y, int width, int height, boolean outBounds) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be > 0");
        }
        int originalWidth = dst.getWidth();
        int originalHeight = dst.getHeight();
        int beginX = x;
        int beginY = y;
        int endX = beginX + width - 1;
        int endY = beginY + height - 1;
        if (beginX >= 0 && beginY >= 0 && beginX < originalWidth && beginY < originalHeight
        && endX >= 0 && endY >= 0 && endX < originalWidth && endY < originalHeight) {
            replaceDst(Bitmap.createBitmap(dst, beginX, beginY, width, height));
            return this;
        }
        if (outBounds) {
            Bitmap temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            if (beginX >= originalWidth || beginY >= originalHeight || endX < 0 || endY < 0) {
                replaceDst(temp);
                return this;
            }
            int pasteX = 0;
            int pasteY = 0;
            if (beginX < 0) {
                pasteX = - beginX;
                beginX = 0;
            }
            if (beginY < 0) {
                pasteY = - beginY;
                beginY = 0;
            }
            if (endX >= originalWidth) {
                endX = originalWidth - 1;
            }
            if (endY >= originalHeight) {
                endY = originalHeight - 1;
            }
            replaceDst(Bitmap.createBitmap(dst, beginX, beginY,
                    endX - beginX + 1, endY - beginY + 1));
            canvas.setBitmap(temp);
            canvas.drawBitmap(dst, pasteX, pasteY, paint);
            replaceDst(temp);
        }
        else {
            if (beginX < 0) {
                beginX = 0;
            }
            if (beginY < 0) {
                beginY = 0;
            }
            if (endX >= originalWidth) {
                endX = originalWidth - 1;
            }
            if (endY >= originalHeight) {
                endY = originalHeight - 1;
            }
            replaceDst(Bitmap.createBitmap(dst, beginX, beginY,
                    endX - beginX + 1, endY - beginY + 1));
        }
        return this;
    }

    public BitmapChanger crop (int left, int top, int right, int bottom, boolean outBounds) {
        right = right - 1;
        bottom = bottom - 1;
        int x = Math.min(left, right);
        int y = Math.min(top, bottom);
        int width = Math.max(left, right) - x + 1;
        int height = Math.max(top, bottom) - y + 1;
        return cut (x, y, width, height, outBounds);
    }

    public BitmapChanger crop (@NonNull Rect rect, boolean outBounds) {
        return crop (rect.left, rect.top, rect.right, rect.bottom, outBounds);
    }

    public BitmapChanger crop (@NonNull RectF rectF, boolean outBounds) {
        return crop ((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom,
                outBounds);
    }

    public BitmapChanger clipPath (Path path) {
        Bitmap temp = Bitmap.createBitmap(dst.getWidth(), dst.getHeight(),
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(temp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            canvas.clipPath(path);
        }
        else {
            canvas.clipPath(path, Region.Op.REPLACE);
        }
        canvas.drawBitmap(dst, 0, 0, paint);
        replaceDst(temp);
        return this;
    }

    public BitmapChanger clipOval () {
        path.reset();
        path.addOval(new RectF(0, 0, dst.getWidth(), dst.getHeight()), Path.Direction.CW);
        return clipPath(path);
    }

    public BitmapChanger clipRoundRect (float radiusX, float radiusY) {
        path.reset();
        path.addRoundRect(new RectF(0, 0, dst.getWidth(), dst.getHeight()), radiusX, radiusY, Path.Direction.CW);
        return clipPath(path);
    }

    public BitmapChanger clipRoundRect (float[] radii) {
        path.reset();
        path.addRoundRect(new RectF(0, 0, dst.getWidth(), dst.getHeight()), radii, Path.Direction.CW);
        return clipPath(path);
    }

    public BitmapChanger matrixChange(Matrix matrix) {
        replaceDst(Bitmap.createBitmap(dst, 0, 0, dst.getWidth(), dst.getHeight(), matrix, false));
        return this;
    }

    public BitmapChanger rotateDegrees (int degrees) {
        matrix.reset();
        matrix.setRotate(degrees, dst.getWidth() * 0.5f, dst.getHeight() * 0.5f);
        return matrixChange(matrix);
    }

    public BitmapChanger rotateDegrees (float degrees) {
        matrix.reset();
        matrix.setRotate(degrees, dst.getWidth() * 0.5f, dst.getHeight() * 0.5f);
        return matrixChange(matrix);
    }

    public BitmapChanger rotateRadians (int radians) {
        return rotateDegrees(MathUtils.rad2deg(radians));
    }

    public BitmapChanger rotateRadians (float radians) {
        return rotateDegrees(MathUtils.rad2deg(radians));
    }

    public BitmapChanger flipHorizontally () {
        matrix.reset();
        matrix.postScale(-1, 1);
        return matrixChange(matrix);
    }

    public BitmapChanger flipVertically () {
        matrix.reset();
        matrix.postScale(1, -1);
        return matrixChange(matrix);
    }

    public BitmapChanger scale (float scaleX, float scaleY, boolean filter) {
        if (scaleX <= 0) {
            throw new IllegalArgumentException("ScaleX must be > 0");
        }
        if (scaleY <= 0) {
            throw new IllegalArgumentException("ScaleY must be > 0");
        }
        matrix.reset();
        matrix.postScale(scaleX, scaleY);
        return matrixChange(matrix);
    }

    public BitmapChanger scale (float scale, boolean filter) {
        return scale(scale, scale, filter);
    }

    public BitmapChanger scaleX (float scaleX, boolean filter) {
        return scale(scaleX, 1, filter);
    }

    public BitmapChanger scaleY (float scaleY, boolean filter) {
        return scale(1, scaleY, filter);
    }

    public BitmapChanger resize (int width, int height, boolean filter) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be > 0");
        }
        replaceDst(Bitmap.createScaledBitmap(dst, width, height, filter));
        return this;
    }

    public BitmapChanger resizeWidth (int width, boolean filter) {
        return resize(width, dst.getHeight(), filter);
    }

    public BitmapChanger resizeHeight (int height, boolean filter) {
        return resize(dst.getWidth(), height, filter);
    }

    public Bitmap change () {
        return dst;
    }

    private void replaceDst(Bitmap newDst) {
        if (dst == newDst) {
            return;
        }
        Bitmap temp = dst;
        dst = newDst;
        BitmapUtils.recycleBitmap(temp);
    }

    public BitmapChanger fill (int x, int y, int color) {
        Stack<Point> pointStack = new Stack<>();
        Point seed;
        if (dst.getPixel(x, y) != color) {
            pointStack.push(new Point(x, y));
        }
        while (true) {
            if (!pointStack.isEmpty()) {
                seed = pointStack.pop();
                int oldColor = dst.getPixel(seed.x, seed.y);
                int leftX = seed.x;
                int rightX = seed.x;
                int boundaryLeft = -1;
                int boundaryRight = -1;
                if (oldColor != color) {
                    while(true) {
                        if (leftX - 1 >= 0) {
                            if(dst.getPixel(leftX - 1, seed.y) != oldColor) {
                                boundaryLeft = leftX;
                                break;
                            }
                            leftX--;
                        } else {
                            if(leftX == 0) {
                                boundaryLeft = leftX;
                            }
                            break;
                        }
                    }
                    while(true) {
                        if (rightX + 1 < dst.getWidth()) {
                            if (dst.getPixel(rightX + 1, seed.y) != oldColor) {
                                boundaryRight = rightX;
                                break;
                            }
                            rightX++;
                        } else {
                            if (rightX + 1 == dst.getWidth()) {
                                boundaryRight = rightX;
                            }
                            break;
                        }
                    }
                    if (boundaryLeft != -1 && boundaryRight != -1) {
                        for (int detectX = boundaryLeft; detectX <= boundaryRight; detectX ++) {
                            if (seed.y + 1 < dst.getHeight()) {
                                if (dst.getPixel(detectX, seed.y + 1) == oldColor) {
                                    if (detectX < boundaryRight) {
                                        if (dst.getPixel(detectX + 1, seed.y + 1) != oldColor) {
                                            pointStack.push(new Point(detectX, seed.y + 1));
                                        }
                                    }
                                    else {
                                        pointStack.push(new Point(detectX, seed.y + 1));
                                    }
                                }
                            }
                            if (seed.y - 1 >= 0) {
                                if (dst.getPixel(detectX, seed.y - 1) == oldColor) {
                                    if (detectX < boundaryRight) {
                                        if (dst.getPixel(detectX + 1, seed.y - 1) != oldColor) {
                                            pointStack.push(new Point(detectX, seed.y - 1));
                                        }
                                    }
                                    else {
                                        pointStack.push(new Point(detectX, seed.y - 1));
                                    }
                                }
                            }
                        }
                    }
                }
                fillLine(dst, seed.x, seed.y, color);
            }
            else {
                break;
            }
        }
        return this;
    }

    private static void fillLine(@NonNull Bitmap bitmap, int x, int y, int color) {
        if (x + 1 < bitmap.getWidth()) {
            if (bitmap.getPixel(x + 1, y) == bitmap.getPixel(x, y)) {
                fillLineRight(bitmap, x + 1, y, color);
            }
        }
        fillLineLeft(bitmap, x, y, color);
    }

    private static void fillLineLeft(@NonNull Bitmap bitmap, int x, int y, int color) {
        int oldColor = bitmap.getPixel(x, y);
        if (oldColor == color) {
            return;
        }
        while(true) {
            if(x >= 0) {
                if(bitmap.getPixel(x, y) == oldColor) {
                    bitmap.setPixel(x, y, color);
                    x --;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
    }

    private static void fillLineRight(@NonNull Bitmap bitmap, int x, int y, int color) {
        int oldColor = bitmap.getPixel(x, y);
        if (oldColor == color) {
            return;
        }
        while(true) {
            if(x < bitmap.getWidth()) {
                if(bitmap.getPixel(x, y) == oldColor) {
                    bitmap.setPixel(x, y, color);
                    x ++;
                }
                else {
                    break;
                }
            }
            else {
                break;
            }
        }
    }

}
