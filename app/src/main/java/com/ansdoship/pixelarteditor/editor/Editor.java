package com.ansdoship.pixelarteditor.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ansdoship.pixelarteditor.R;
import com.ansdoship.pixelarteditor.editor.buffer.FillBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.FlipHorizontalBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.FlipVerticalBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.MultiBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.PaintBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.PointBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.RotateBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.SelectionBuffer;
import com.ansdoship.pixelarteditor.editor.graphics.BitmapUtils;
import com.ansdoship.pixelarteditor.ui.view.CanvasView;
import com.ansdoship.pixelarteditor.ui.viewgroup.PaletteList;
import com.ansdoship.pixelarteditor.util.ApplicationUtils;
import com.ansdoship.pixelarteditor.editor.palette.Palette;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFactory;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFlag;
import com.ansdoship.pixelarteditor.editor.graphics.BitmapDecoder;
import com.ansdoship.pixelarteditor.editor.graphics.BitmapEncoder;
import com.ansdoship.pixelarteditor.editor.buffer.ToolBufferPool;
import com.ansdoship.pixelarteditor.util.MathUtils;
import com.ansdoship.pixelarteditor.util.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Editor {
	
    private enum Singleton {
        INSTANCE;
        private final Editor instance;
        Singleton() {
            instance = new Editor();
        }
        public Editor getInstance() {
            return instance;
        }
    }
    
    private Editor() {
        preferences = ApplicationUtils.getApplicationContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    
    public static Editor getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    public final static String PREFERENCES_NAME = "editor_data";
    
    public final static String KEY_IMAGE_NAME = "image_name";
    private String imageName;
    public final static String KEY_IMAGE_FORMAT = "image_format";
    private String imageFormat;
    public final static String KEY_IMAGE_QUALITY = "image_quality";
    private int imageQuality;
    public final static String KEY_IMAGE_PATH = "image_path";
    private String imagePath;
    public final static String KEY_IMAGE_SCALE = "image_scale";
    private int imageScale;
    public final static String KEY_IMAGE_TRANSLATION_X = "image_translation_x";
    private int imageTranslationX;
    public final static String KEY_IMAGE_TRANSLATION_Y = "image_translation_y";
    private int imageTranslationY;
    public final static String KEY_IMAGE_ORIGIN_X = "image_origin_x";
    private int imageOriginX;
    public final static String KEY_IMAGE_ORIGIN_Y = "image_origin_y";
    private int imageOriginY;
    
    public final static String KEY_TOOL_FLAG = "tool_flag";
    private int toolFlag;
    public final static String KEY_SHAPE_FLAG = "shape_flag";
    private int shapeFlag;
    public final static String KEY_PAINT_FLAG = "paint_flag";
    private int paintFlag;
    public final static String KEY_SELECTION_FLAG = "selection_flag";
    private int selectionFlag;
    public final static String KEY_PALETTE_FLAG = "palette_flag";
    private int paletteFlag;

    public final static String KEY_EXTERNAL_PALETTE_NAME = "external_palette_name";
    private String externalPaletteName;
    
    public final static String KEY_PAINT_WIDTH = "paint_width";
    private int paintWidth;
    
    public final static String KEY_GRID_VISIBLE = "grid_visible";
    private boolean gridVisible;
    public final static String KEY_GRID_WIDTH = "grid_width";
    private int gridWidth;
    public final static String KEY_GRID_HEIGHT = "grid_height";
    private int gridHeight;
    public final static String KEY_BACKGROUND_PALETTE = "background_palette";
    private Palette backgroundPalette;
    public final static String KEY_GRID_PALETTE = "grid_palette";
    private Palette gridPalette;
    public final static String KEY_BUILTIN_PALETTE = "builtin_palette";
    private Palette builtinPalette;

    private Palette externalPalette;

    private ToolBufferPool toolBufferPool;

    private boolean scaleMode;

    private boolean readOnlyMode;

    public void loadData() {

        gridPaint = new Paint();
        gridPaint.setAntiAlias(false);
        gridPaint.setDither(false);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);
        canvasBackgroundPaint = new Paint();
        canvasBackgroundPaint.setAntiAlias(false);
        canvasBackgroundPaint.setDither(false);
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(false);
        bitmapPaint.setDither(false);
        bitmapPaint.setFilterBitmap(false);
        paint = new Paint();
        paint.setDither(false);
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.STROKE);
        eraser = new Paint();
        eraser.setDither(false);
        eraser.setAntiAlias(false);
        eraser.setStyle(Paint.Style.STROKE);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        selectionPaint1 = new Paint();
        selectionPaint1.setDither(false);
        selectionPaint1.setAntiAlias(false);
        selectionPaint1.setStyle(Paint.Style.STROKE);
        selectionPaint1.setStrokeCap(Paint.Cap.SQUARE);
        selectionPaint1.setColor(Color.WHITE);
        selectionPaint1.setStrokeWidth(Editor.getInstance().getImageScale() * 0.5f + 0.5f);
        selectionPaint2 = new Paint();
        selectionPaint2.setDither(false);
        selectionPaint2.setAntiAlias(false);
        selectionPaint2.setStyle(Paint.Style.STROKE);
        selectionPaint2.setStrokeCap(Paint.Cap.SQUARE);
        selectionPaint2.setColor(Color.BLACK);
        selectionPaint2.setStrokeWidth(Editor.getInstance().getImageScale() * 0.25f + 0.25f);
        path = new Path();
        matrix = new Matrix();

        String backgroundPaletteString = preferences.getString(KEY_BACKGROUND_PALETTE, null);
        if (backgroundPaletteString == null) {
            backgroundPalette = Palette.createPalette(BACKGROUND_PALETTE_COLORS_DEFAULT);
        }
        else {
            backgroundPalette = PaletteFactory.decodeString(backgroundPaletteString);
            if (backgroundPalette == null) {
                backgroundPalette = Palette.createPalette(BACKGROUND_PALETTE_COLORS_DEFAULT);
            }
        }
        String gridPaletteString = preferences.getString(KEY_GRID_PALETTE, null);
        if (gridPaletteString == null) {
            gridPalette = Palette.createPalette(GRID_PALETTE_COLORS_DEFAULT);
        }
        else {
            gridPalette = PaletteFactory.decodeString(gridPaletteString);
            if (gridPalette == null) {
                gridPalette = Palette.createPalette(GRID_PALETTE_COLORS_DEFAULT);
            }
        }
        String builtinPaletteString = preferences.getString(KEY_BUILTIN_PALETTE, null);
        if (builtinPaletteString == null) {
            builtinPalette = Palette.createPalette(BUILTIN_PALETTE_COLORS_DEFAULT);
        }
        else {
            builtinPalette = PaletteFactory.decodeString(builtinPaletteString);
            if (builtinPalette == null) {
                builtinPalette = Palette.createPalette(BUILTIN_PALETTE_COLORS_DEFAULT);
            }
        }

        setImageName(preferences.getString(KEY_IMAGE_NAME, IMAGE_NAME_DEFAULT()));
        setImageFormat(preferences.getString(KEY_IMAGE_FORMAT, IMAGE_FORMAT_DEFAULT));
        setImageQuality(preferences.getInt(KEY_IMAGE_QUALITY, IMAGE_QUALITY_DEFAULT));
        setImagePath(preferences.getString(KEY_IMAGE_PATH, IMAGE_PATH_DEFAULT()));
        setImageScale(preferences.getInt(KEY_IMAGE_SCALE, IMAGE_SCALE_DEFAULT));
        setImageTranslationX(preferences.getInt(KEY_IMAGE_TRANSLATION_X, IMAGE_TRANSLATION_X_DEFAULT));
        setImageTranslationY(preferences.getInt(KEY_IMAGE_TRANSLATION_Y, IMAGE_TRANSLATION_Y_DEFAULT));
        setImageOriginX(preferences.getInt(KEY_IMAGE_ORIGIN_X, IMAGE_ORIGIN_X_DEFAULT));
        setImageOriginY(preferences.getInt(KEY_IMAGE_ORIGIN_Y, IMAGE_ORIGIN_Y_DEFAULT));
        setToolFlag(preferences.getInt(KEY_TOOL_FLAG, TOOL_FLAG_DEFAULT));
        setShapeFlag(preferences.getInt(KEY_SHAPE_FLAG, SHAPE_FLAG_DEFAULT));
        setPaintFlag(preferences.getInt(KEY_PAINT_FLAG, PAINT_FLAG_DEFAULT));
        setSelectionFlag(preferences.getInt(KEY_SELECTION_FLAG, SELECTION_FLAG_DEFAULT));
        setPaletteFlag(preferences.getInt(KEY_PALETTE_FLAG, PALETTE_FLAG_DEFAULT));
        setExternalPaletteName(preferences.getString(KEY_EXTERNAL_PALETTE_NAME, EXTERNAL_PALETTE_NAME_DEFAULT));
        setPaintWidth(preferences.getInt(KEY_PAINT_WIDTH, PAINT_WIDTH_DEFAULT));
        setGridVisible(preferences.getBoolean(KEY_GRID_VISIBLE, GRID_VISIBLE_DEFAULT));
        setGridWidth(preferences.getInt(KEY_GRID_WIDTH, GRID_WIDTH_DEFAULT));
        setGridHeight(preferences.getInt(KEY_GRID_HEIGHT, GRID_HEIGHT_DEFAULT));
        setScaleMode(SCALE_MODE_DEFAULT);
        setReadOnlyMode(READ_ONLY_MODE_DEFAULT);
        setSelected(SELECTED_DEFAULT);

        if (toolBufferPool == null) {
            String cacheBitmapPathName = getCurrentBitmapPathname();
            if (cacheBitmapPathName != null) {
                replaceCacheBitmap(BitmapDecoder.decodeFile(cacheBitmapPathName));
            }
            if(cacheBitmap == null) {
                replaceCacheBitmap(Bitmap.createBitmap(IMAGE_WIDTH_DEFAULT,
                        IMAGE_HEIGHT_DEFAULT, Bitmap.Config.ARGB_8888));
            }
            setBitmap(cacheBitmap);
        }
        else {
            String cacheBitmapPathName = getCacheBitmapPathname();
            if (cacheBitmapPathName != null) {
                replaceCacheBitmap(BitmapDecoder.decodeFile(cacheBitmapPathName));
            }
            if (cacheBitmap == null) {
                replaceCacheBitmap(Bitmap.createBitmap(IMAGE_WIDTH_DEFAULT,
                        IMAGE_HEIGHT_DEFAULT, Bitmap.Config.ARGB_8888));
                setBitmap(cacheBitmap);
            }
            else {
                toolBufferPool.setCacheBitmap(cacheBitmap);
                toolBufferPool.flushCurrentBitmap();
                replaceCurrentBitmap(toolBufferPool.getCurrentBitmap());
            }
        }

    }

    public void saveData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString(KEY_IMAGE_NAME, imageName);
        editor.putString(KEY_IMAGE_FORMAT, imageFormat);
        editor.putInt(KEY_IMAGE_QUALITY, imageQuality);
        editor.putString(KEY_IMAGE_PATH, imagePath);
        editor.putInt(KEY_IMAGE_SCALE, imageScale);
        editor.putInt(KEY_IMAGE_TRANSLATION_X, imageTranslationX);
        editor.putInt(KEY_IMAGE_TRANSLATION_Y, imageTranslationY);
        editor.putInt(KEY_IMAGE_ORIGIN_X, imageOriginX);
        editor.putInt(KEY_IMAGE_ORIGIN_Y, imageOriginY);
        editor.putInt(KEY_TOOL_FLAG, toolFlag);
        editor.putInt(KEY_SHAPE_FLAG, shapeFlag);
        editor.putInt(KEY_PAINT_FLAG, paintFlag);
        editor.putInt(KEY_SELECTION_FLAG, selectionFlag);
        editor.putInt(KEY_PALETTE_FLAG, paletteFlag);
        editor.putString(KEY_EXTERNAL_PALETTE_NAME, externalPaletteName);
        editor.putInt(KEY_PAINT_WIDTH, paintWidth);
        editor.putBoolean(KEY_GRID_VISIBLE, gridVisible);
        editor.putInt(KEY_GRID_WIDTH, gridWidth);
        editor.putInt(KEY_GRID_HEIGHT, gridHeight);
        editor.putString(KEY_BACKGROUND_PALETTE, PaletteFactory.encodeString(backgroundPalette));
        editor.putString(KEY_GRID_PALETTE, PaletteFactory.encodeString(gridPalette));
        editor.putString(KEY_BUILTIN_PALETTE, PaletteFactory.encodeString(builtinPalette));
        editor.apply();

        String cacheBitmapPathname = getCacheBitmapPathname();
        if (cacheBitmapPathname != null) {
            BitmapEncoder.encodeFile(cacheBitmapPathname,
                    toolBufferPool.getCacheBitmap(), true, BitmapEncoder.CompressFormat.PNG, 100,
                    new BitmapEncoder.Callback() {
                        @Override
                        public void onCreateFailure() {}
                        @Override
                        public void onCompressFailure() {}
                        @Override
                        public void onFileExists(boolean isDirectory) {}
                        @Override
                        public void onIOException(IOException e) {}
                    });
        }
        String currentBitmapPathname = getCurrentBitmapPathname();
        if (currentBitmapPathname != null) {
            BitmapEncoder.encodeFile(currentBitmapPathname,
                    toolBufferPool.getCurrentBitmap(), true, BitmapEncoder.CompressFormat.PNG, 100,
                    new BitmapEncoder.Callback() {
                        @Override
                        public void onCreateFailure() {}
                        @Override
                        public void onCompressFailure() {}
                        @Override
                        public void onFileExists(boolean isDirectory) {}
                        @Override
                        public void onIOException(IOException e) {}
                    });
        }
        BitmapUtils.recycleBitmaps(cacheBitmap, currentBitmap, canvasBackgroundBitmap);

        if (externalPalette != null) {
            PaletteFactory.encodeFile(externalPalette, getExternalPalettePathName(getExternalPaletteName()), true);
        }

    }
    
    public static String IMAGE_NAME_DEFAULT() {
    	return ApplicationUtils.getApplicationContext().getString(R.string.image_name_default);
    }
    public final static String IMAGE_FORMAT_DEFAULT = "png";
    public final static int IMAGE_QUALITY_MAX = 100;
    public final static int IMAGE_QUALITY_MIN = 30;
    public final static int IMAGE_QUALITY_DEFAULT = IMAGE_QUALITY_MAX;
    public static String IMAGE_PATH_DEFAULT() {
        return Utils.getFilesPath("images");
    }
    public final static int IMAGE_SCALE_DEFAULT = 40;
    public final static int IMAGE_TRANSLATION_X_DEFAULT = 0;
    public final static int IMAGE_TRANSLATION_Y_DEFAULT = 0;
    public final static int IMAGE_ORIGIN_X_DEFAULT = 0;
    public final static int IMAGE_ORIGIN_Y_DEFAULT = 0;
    public final static int IMAGE_WIDTH_DEFAULT = 16;
    public final static int IMAGE_HEIGHT_DEFAULT = 16;
    
    public final static int TOOL_FLAG_DEFAULT = ToolFlag.PAINT;
    public final static int SHAPE_FLAG_DEFAULT = ToolFlag.ShapeFlag.LINE;
    public final static int PAINT_FLAG_DEFAULT = ToolFlag.PaintFlag.REPLACE;
    public final static int SELECTION_FLAG_DEFAULT = ToolFlag.SelectionFlag.NONE;
    public final static int PALETTE_FLAG_DEFAULT = PaletteFlag.INTERNAL;

    public final static String EXTERNAL_PALETTE_NAME_DEFAULT = null;
    
    public final static int PAINT_WIDTH_DEFAULT = 1;
    
    public final static boolean GRID_VISIBLE_DEFAULT = false;
    public final static int GRID_WIDTH_DEFAULT = 1;
    public final static int GRID_HEIGHT_DEFAULT = 1;

    public final static int[] BACKGROUND_PALETTE_COLORS_DEFAULT = new int[] {
            Color.DKGRAY, Color.LTGRAY, Color.GRAY
    };
    public final static int[] GRID_PALETTE_COLORS_DEFAULT = new int[] {Color.BLACK};
    public final static int[] BUILTIN_PALETTE_COLORS_DEFAULT = new int[] {
            Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA,
            Color.WHITE, Color.LTGRAY, Color.GRAY, Color.DKGRAY, Color.BLACK, Color.TRANSPARENT
    };

    public final static int MAX_BUFFER_SIZE_DEFAULT = 20;

    public final static boolean SCALE_MODE_DEFAULT = false;
    public final static boolean READ_ONLY_MODE_DEFAULT = false;

    public final static int IMAGE_SCALE_MAX = 64;

    public final static boolean SELECTED_DEFAULT = false;
    
    private final SharedPreferences preferences;

    private Bitmap cacheBitmap;
    private Bitmap currentBitmap;
    private Bitmap canvasBackgroundBitmap;

    private Paint gridPaint;
    private Paint canvasBackgroundPaint;
    private Paint bitmapPaint;
    private Paint paint;
    private Paint eraser;
    private Paint selectionPaint1;
    private Paint selectionPaint2;

    private Matrix matrix;

    private Path path;

    private CanvasView canvasView;

    private PaletteList listPalettes;

    private boolean selected;
    private int selectionBitmapSrcX;
    private int selectionBitmapSrcY;
    private int selectionBitmapSrcWidth;
    private int selectionBitmapSrcHeight;
    private int selectionBitmapDstX;
    private int selectionBitmapDstY;
    private int selectionBitmapDstWidth;
    private int selectionBitmapDstHeight;
    private RotateBuffer selectionBitmapRotateBuffer;
    private FlipVerticalBuffer selectionBitmapFlipVerticalBuffer;
    private FlipHorizontalBuffer selectionBitmapFlipHorizontalBuffer;

    public interface Callback {
        void SelectionCallback();
    }

    private Callback mCallback;

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public Callback getCallback() {
        return mCallback;
    }

    private int downX;
    private int downY;
    private int moveX;
    private int moveY;
    private int upX;
    private int upY;

    public void setDownX(int downX) {
        this.downX = downX;
    }

    public int getDownX() {
        return downX;
    }

    public void setDownY(int downY) {
        this.downY = downY;
    }

    public int getDownY() {
        return downY;
    }

    public void setMoveX(int moveX) {
        this.moveX = moveX;
    }

    public int getMoveX() {
        return moveX;
    }

    public void setMoveY(int moveY) {
        this.moveY = moveY;
    }

    public int getMoveY() {
        return moveY;
    }

    public void setUpX(int upX) {
        this.upX = upX;
    }

    public int getUpX() {
        return upX;
    }

    public void setUpY(int upY) {
        this.upY = upY;
    }

    public int getUpY() {
        return upY;
    }

    private void replaceCacheBitmap(Bitmap newBitmap) {
        if (cacheBitmap == newBitmap) {
            return;
        }
        Bitmap temp = cacheBitmap;
        cacheBitmap = newBitmap;
        BitmapUtils.recycleBitmap(temp);
    }

    private void replaceCurrentBitmap(Bitmap newBitmap) {
        if (currentBitmap == newBitmap) {
            return;
        }
        Bitmap temp = currentBitmap;
        currentBitmap = newBitmap;
        BitmapUtils.recycleBitmap(temp);
    }

    private void replaceCanvasBackgroundBitmap(Bitmap newBitmap) {
        if (canvasBackgroundBitmap == newBitmap) {
            return;
        }
        Bitmap temp = canvasBackgroundBitmap;
        canvasBackgroundBitmap = newBitmap;
        BitmapUtils.recycleBitmap(temp);
    }

    public Path getPath() {
        return path;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public Paint getCanvasBackgroundPaint() {
        return canvasBackgroundPaint;
    }

    public Paint getGridPaint() {
        return gridPaint;
    }

    public Paint getBitmapPaint() {
        return bitmapPaint;
    }

    public Paint getPaint() {
        return paint;
    }

    public Paint getEraser() {
        return eraser;
    }

    public Paint getSelectionPaint1() {
        return selectionPaint1;
    }

    public Paint getSelectionPaint2() {
        return selectionPaint2;
    }

    public void flushGridPaint() {
        gridPaint.setColor(getGridPalette().getCurrentColor());
    }

    public void flushCanvasBackgroundPaint() {
        replaceCanvasBackgroundBitmap(Bitmap.createBitmap(new int[] {
                        getCanvasBackgroundColor1(),
                        getCanvasBackgroundColor2(),
                        getCanvasBackgroundColor2(),
                        getCanvasBackgroundColor1()},
                2, 2, Bitmap.Config.ARGB_8888));
        int imageScale = Editor.getInstance().getImageScale();
        replaceCanvasBackgroundBitmap(Bitmap.createScaledBitmap(
                canvasBackgroundBitmap,
                imageScale * getCanvasBackgroundImageScale(),
                imageScale * getCanvasBackgroundImageScale(), false));
        BitmapShader canvasBackgroundShader = new BitmapShader(
                canvasBackgroundBitmap,
                BitmapShader.TileMode.REPEAT,
                BitmapShader.TileMode.REPEAT);
        canvasBackgroundPaint.setShader(canvasBackgroundShader);
    }

    public void flushPaint(int color) {
        paint.setColor(color);
    }

    public int getCanvasBackgroundImageScale() {
        int result = 0;
        if(imageScale <= 4) {
            result = 16;
        }
        if (imageScale > 4 && imageScale <= 8) {
            result = 8;
        }
        if (imageScale > 8 && imageScale <= 16) {
            result = 4;
        }
        if (imageScale > 16 && imageScale <= 32) {
            result = 2;
        }
        if (imageScale > 32) {
            result = 1;
        }
        return result;
    }

    public void setCanvasViewBackgroundColor (int backgroundColor) {
        backgroundPalette.setColor(0, backgroundColor);
        invalidateCanvasView();
    }

    public int getCanvasViewBackgroundColor () {
        return backgroundPalette.getColor(0);
    }

    public void setCanvasBackgroundColor1 (int backgroundColor) {
        backgroundPalette.setColor(1, backgroundColor);
        invalidateCanvasView();
    }

    public int getCanvasBackgroundColor1() {
        return backgroundPalette.getColor(1);
    }

    public void setCanvasBackgroundColor2 (int backgroundColor) {
        backgroundPalette.setColor(2, backgroundColor);
        invalidateCanvasView();
    }

    public int getCanvasBackgroundColor2() {
        return backgroundPalette.getColor(2);
    }

    public void setPaletteList(PaletteList paletteList) {
        if (listPalettes == paletteList) {
            return;
        }
        listPalettes = paletteList;
    }

    public PaletteList getPaletteList() {
        return listPalettes;
    }

    public void setCanvasView(CanvasView canvasView) {
        if (this.canvasView == canvasView) {
            return;
        }
        this.canvasView = canvasView;
        if (canvasView != null) {

            // Draw
            canvasView.setOnInvalidateListener(new CanvasView.OnInvalidateListener() {

                @Override
                public void onInvalidate(Canvas canvas) {
                    // Clear canvas
                    canvas.drawPaint(eraser);
                    canvas.save();
                    canvas.restore();
                    // Set matrix
                    matrix.setTranslate(
                            (float)(imageTranslationX) / imageScale,
                            (float)(imageTranslationY) / imageScale);
                    matrix.postScale(imageScale, imageScale);
                    // Draw background
                    canvas.drawColor(getCanvasViewBackgroundColor());
                    canvas.drawRect(imageTranslationX, imageTranslationY,
                            imageTranslationX + toolBufferPool.getCurrentBitmap().getWidth() * imageScale,
                            imageTranslationY + toolBufferPool.getCurrentBitmap().getHeight() * imageScale,
                            canvasBackgroundPaint);
                    canvas.save();
                    canvas.restore();
                    // Draw scaled bitmap
                    canvas.drawBitmap(toolBufferPool.getCurrentBitmap(), matrix, bitmapPaint);
                    canvas.save();
                    canvas.restore();
                    // Draw grid
                    if(isGridVisible()) {
                        if(imageScale >= 4) {
                            int width = toolBufferPool.getCurrentBitmap().getWidth();
                            int height = toolBufferPool.getCurrentBitmap().getHeight();
                            for (int i = 0; i <= width; i += gridWidth) {
                                canvas.drawLine(
                                        imageTranslationX + i * imageScale,
                                        imageTranslationY,
                                        imageTranslationX + i * imageScale,
                                        imageTranslationY + height * imageScale, gridPaint);
                            }
                            for (int i = 0; i <= height; i += gridHeight) {
                                canvas.drawLine(
                                        imageTranslationX,
                                        imageTranslationY + i * imageScale,
                                        imageTranslationX + width * imageScale,
                                        imageTranslationY + i * imageScale, gridPaint);
                            }
                            canvas.save();
                            canvas.restore();
                        }
                    }
                    // Draw selection board
                    if (toolFlag == ToolFlag.SELECTION && selected) {
                        float selectionLeft;
                        float selectionTop;
                        float selectionRight;
                        float selectionBottom;
                        RectF selectionRectF;
                        switch (selectionFlag) {
                            case ToolFlag.SelectionFlag.CUT:
                            case ToolFlag.SelectionFlag.COPY:
                                selectionLeft = selectionBitmapDstX * imageScale + selectionPaint1.getStrokeWidth() / 2;
                                selectionTop = selectionBitmapDstY * imageScale + selectionPaint1.getStrokeWidth() / 2;
                                selectionRight = selectionLeft + (selectionBitmapDstWidth - 1) * imageScale
                                        + selectionPaint1.getStrokeWidth();
                                selectionBottom = selectionTop + (selectionBitmapDstHeight - 1) * imageScale
                                        + selectionPaint1.getStrokeWidth();
                                break;
                            case ToolFlag.SelectionFlag.CLEAR:
                            default:
                                selectionLeft = imageTranslationX +
                                        Math.min(downX, moveX) * imageScale + selectionPaint1.getStrokeWidth() / 2;
                                selectionTop = imageTranslationX +
                                        Math.min(downY, moveY) * imageScale + selectionPaint1.getStrokeWidth() / 2;
                                selectionRight = imageTranslationX +
                                        Math.max(downX, moveX) * imageScale + selectionPaint1.getStrokeWidth() / 2 * 3;
                                selectionBottom = imageTranslationX +
                                        Math.max(downY, moveY) * imageScale + selectionPaint1.getStrokeWidth() / 2 * 3;
                                break;
                        }
                        selectionRectF = new RectF(selectionLeft, selectionTop, selectionRight, selectionBottom);
                        canvas.drawRect(selectionRectF, selectionPaint1);
                        canvas.drawRect(selectionRectF, selectionPaint2);
                    }
                }
            });

            canvasView.setOnTouchListener(new View.OnTouchListener() {

                double oldDist = 0;
                double newDist = 0;
                float x;
                float y;
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            x = event.getX(0);
                            y = event.getY(0);
                            selected = false;
                            // Draw current bitmap
                            downX = (int) Math.floor((event.getX(0) - imageTranslationX) / imageScale);
                            downY = (int) Math.floor((event.getY(0) - imageTranslationY) / imageScale);
                            moveX = downX;
                            moveY = downY;
                            path.moveTo(downX, downY);
                            switch (toolFlag) {
                                case ToolFlag.PAINT:
                                    toolBufferPool.addTempToolBuffer(new PointBuffer(paint, downX, downY));
                                    break;
                                case ToolFlag.ERASER:
                                    toolBufferPool.addTempToolBuffer(new PointBuffer(eraser, downX, downY));
                                    break;
                                case ToolFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case ToolFlag.SelectionFlag.CUT:
                                        case ToolFlag.SelectionFlag.COPY:
                                            selectionBitmapDstX = downX - selectionBitmapDstWidth / 2;
                                            selectionBitmapDstY = downY - selectionBitmapDstHeight / 2;
                                            toolBufferPool.clearTempToolBuffers();
                                            toolBufferPool.addTempToolBuffer(new SelectionBuffer(
                                                    selectionBitmapSrcX, selectionBitmapSrcY,
                                                    selectionBitmapSrcWidth, selectionBitmapSrcHeight,
                                                    selectionBitmapDstX, selectionBitmapDstY,
                                                    selectionFlag, selectionBitmapRotateBuffer,
                                                    selectionBitmapFlipVerticalBuffer,
                                                    selectionBitmapFlipHorizontalBuffer));
                                            selected = true;
                                            break;
                                        case ToolFlag.SelectionFlag.CLEAR:
                                        default:
                                            if (downX < 0) {
                                                downX = 0;
                                            }
                                            if (downX >= toolBufferPool.getCurrentBitmap().getWidth()) {
                                                downX = toolBufferPool.getCurrentBitmap().getWidth() - 1;
                                            }
                                            if (downY < 0) {
                                                downY = 0;
                                            }
                                            if (downY >= toolBufferPool.getCurrentBitmap().getHeight()) {
                                                downY = toolBufferPool.getCurrentBitmap().getHeight() - 1;
                                            }
                                            break;
                                    }
                                    break;
                                case ToolFlag.COLORIZE:
                                    if (downX >=0 && downY >= 0 &&
                                            downX < toolBufferPool.getCurrentBitmap().getWidth() &&
                                            downY < toolBufferPool.getCurrentBitmap().getHeight()) {
                                        listPalettes.setCheckedPaletteColor(toolBufferPool.getCurrentBitmap().getPixel(downX, downY));
                                        flushPaint(listPalettes.getCheckedPaletteColor());
                                        switch (paletteFlag) {
                                            case PaletteFlag.BACKGROUND:
                                                if (listPalettes.getCheckedIndex() == 0) {
                                                    setCanvasViewBackgroundColor(toolBufferPool.getCurrentBitmap().getPixel(downX, downY));
                                                }
                                                else {
                                                    flushCanvasBackgroundPaint();
                                                }
                                                break;
                                            case PaletteFlag.GRID:
                                                setGridColor(toolBufferPool.getCurrentBitmap().getPixel(downX, downY));
                                                break;
                                        }
                                    }
                                    break;
                            }
                            canvasView.invalidate();
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            // Record initial distance
                            oldDist = Utils.spacing(event);
                            newDist = oldDist;
                            scaleMode = true;
                            readOnlyMode = true;
                            selectionFlag = ToolFlag.SelectionFlag.NONE;
                            selected = false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(scaleMode) {
                                newDist = Utils.spacing(event);
                                if(newDist != 0) {
                                    if(newDist >= oldDist + 256 * ApplicationUtils.getResources().getDisplayMetrics().density) {
                                        setImageScale(imageScale * 2);
                                        oldDist = newDist;
                                    }
                                    if(newDist <= oldDist - 256 * ApplicationUtils.getResources().getDisplayMetrics().density) {
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
                                switch (toolFlag) {
                                    case ToolFlag.PAINT:
                                    case ToolFlag.ERASER:
                                        path.lineTo(moveX, moveY);
                                        break;
                                    case ToolFlag.SHAPE:
                                        toolBufferPool.clearTempToolBuffers();
                                        path.reset();
                                        switch (shapeFlag) {
                                            case ToolFlag.ShapeFlag.LINE:
                                                path.moveTo(downX, downY);
                                                path.lineTo(moveX, moveY);
                                                break;
                                            case ToolFlag.ShapeFlag.CIRCLE:
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
                                            case ToolFlag.ShapeFlag.ELLIPSE:
                                                int ovalLeft = Math.min(downX, moveX);
                                                int ovalTop = Math.min(downY, moveY);
                                                int ovalRight = Math.max(downX, moveX);
                                                int ovalBottom = Math.max(downY, moveY);
                                                RectF ovalRectF = new RectF(ovalLeft, ovalTop, ovalRight, ovalBottom);
                                                path.addOval(ovalRectF, Path.Direction.CW);
                                                break;
                                            case ToolFlag.ShapeFlag.SQUARE:
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
                                            case ToolFlag.ShapeFlag.RECTANGLE:
                                                int rectLeft = Math.min(downX, moveX);
                                                int rectTop = Math.min(downY, moveY);
                                                int rectRight = Math.max(downX, moveX);
                                                int rectBottom = Math.max(downY, moveY);
                                                RectF rectRectF = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                                                path.addRect(rectRectF, Path.Direction.CW);
                                                break;
                                        }
                                        break;
                                    case ToolFlag.SELECTION:
                                        switch (selectionFlag) {
                                            case ToolFlag.SelectionFlag.CUT:
                                            case ToolFlag.SelectionFlag.COPY:
                                                break;
                                            case ToolFlag.SelectionFlag.CLEAR:
                                            default:
                                                if (moveX < 0) {
                                                    moveX = 0;
                                                }
                                                if (moveX >= toolBufferPool.getCurrentBitmap().getWidth()) {
                                                    moveX = toolBufferPool.getCurrentBitmap().getWidth() - 1;
                                                }
                                                if (moveY < 0) {
                                                    moveY = 0;
                                                }
                                                if (moveY >= toolBufferPool.getCurrentBitmap().getHeight()) {
                                                    moveY = toolBufferPool.getCurrentBitmap().getHeight() - 1;
                                                }
                                                break;
                                        }
                                        selected = true;
                                        break;
                                    case ToolFlag.COLORIZE:
                                        if (moveX >=0 && moveY >= 0 &&
                                                moveX < toolBufferPool.getCurrentBitmap().getWidth() &&
                                                moveY < toolBufferPool.getCurrentBitmap().getHeight()) {
                                            listPalettes.setCheckedPaletteColor(toolBufferPool.getCurrentBitmap().getPixel(moveX, moveY));
                                            flushPaint(listPalettes.getCheckedPaletteColor());
                                            switch (paletteFlag) {
                                                case PaletteFlag.BACKGROUND:
                                                    if (listPalettes.getCheckedIndex() == 0) {
                                                        setCanvasViewBackgroundColor(toolBufferPool.getCurrentBitmap().getPixel(downX, downY));
                                                    }
                                                    else {
                                                        flushCanvasBackgroundPaint();
                                                    }
                                                    break;
                                                case PaletteFlag.GRID:
                                                    setGridColor(toolBufferPool.getCurrentBitmap().getPixel(downX, downY));
                                                    break;
                                            }
                                        }
                                        break;
                                }
                                switch (toolFlag) {
                                    // Draw down point
                                    case ToolFlag.PAINT:
                                        toolBufferPool.addTempToolBuffer(
                                                new MultiBuffer(new PointBuffer(paint, downX, downY), new PaintBuffer(paint, path)));
                                        break;
                                    case ToolFlag.ERASER:
                                        toolBufferPool.addTempToolBuffer(
                                                new MultiBuffer(new PointBuffer(eraser, downX, downY), new PaintBuffer(eraser, path)));
                                        break;
                                    case ToolFlag.SHAPE:
                                        toolBufferPool.addTempToolBuffer(new PaintBuffer(paint, path));
                                        break;
                                    // Draw selection bmp
                                    case ToolFlag.SELECTION:
                                        switch (selectionFlag) {
                                            case ToolFlag.SelectionFlag.CUT:
                                            case ToolFlag.SelectionFlag.COPY:
                                                selectionBitmapDstX = moveX - selectionBitmapDstWidth / 2;
                                                selectionBitmapDstY = moveY - selectionBitmapDstHeight / 2;
                                                toolBufferPool.clearTempToolBuffers();
                                                toolBufferPool.addTempToolBuffer(new SelectionBuffer(
                                                        selectionBitmapSrcX,
                                                        selectionBitmapSrcY,
                                                        selectionBitmapSrcWidth,
                                                        selectionBitmapSrcHeight,
                                                        selectionBitmapDstX,
                                                        selectionBitmapDstY,
                                                        selectionFlag,
                                                        selectionBitmapRotateBuffer,
                                                        selectionBitmapFlipVerticalBuffer,
                                                        selectionBitmapFlipHorizontalBuffer));
                                                break;
                                        }
                                        break;
                                }
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
                                switch (toolFlag) {
                                    case ToolFlag.PAINT:
                                        toolBufferPool.addToolBuffer(
                                                new MultiBuffer(new PointBuffer(paint, downX, downY), new PaintBuffer(paint, path)));
                                        break;
                                    case ToolFlag.ERASER:
                                        toolBufferPool.addToolBuffer(
                                                new MultiBuffer(new PointBuffer(eraser, downX, downY), new PaintBuffer(eraser, path)));
                                        break;
                                    case ToolFlag.SHAPE:
                                        toolBufferPool.addToolBuffer(new PaintBuffer(paint, path));
                                        break;
                                    case ToolFlag.FILL:
                                        if (downX >= 0 && downY >= 0 &&
                                                downX < toolBufferPool.getCurrentBitmap().getWidth() &&
                                                downY < toolBufferPool.getCurrentBitmap().getHeight()) {
                                            toolBufferPool.addToolBuffer(new FillBuffer(downX, downY, paint.getColor()));
                                        }
                                        break;
                                    case ToolFlag.SELECTION:
                                        if (mCallback != null) {
                                            mCallback.SelectionCallback();
                                        }
                                        break;
                                }
                                if (toolFlag != ToolFlag.SELECTION) {
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
            });

        }
    }

    public CanvasView getCanvasView() {
        return canvasView;
    }

    public Bitmap getCacheBitmap() {
        return cacheBitmap;
    }

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public Bitmap getCanvasBackgroundBitmap() {
        return canvasBackgroundBitmap;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelectionBitmapSrcX(int selectionBitmapSrcX) {
        this.selectionBitmapSrcX = selectionBitmapSrcX;
    }

    public int getSelectionBitmapSrcX() {
        return selectionBitmapSrcX;
    }

    public void setSelectionBitmapSrcY(int selectionBitmapSrcY) {
        this.selectionBitmapSrcY = selectionBitmapSrcY;
    }

    public int getSelectionBitmapSrcY() {
        return selectionBitmapSrcY;
    }

    public void setSelectionBitmapSrcWidth(int selectionBitmapSrcWidth) {
        this.selectionBitmapSrcWidth = selectionBitmapSrcWidth;
    }

    public int getSelectionBitmapSrcWidth() {
        return selectionBitmapSrcWidth;
    }

    public void setSelectionBitmapSrcHeight(int selectionBitmapSrcHeight) {
        this.selectionBitmapSrcHeight = selectionBitmapSrcHeight;
    }

    public int getSelectionBitmapSrcHeight() {
        return selectionBitmapSrcHeight;
    }

    public void setSelectionBitmapDstX(int selectionBitmapDstX) {
        this.selectionBitmapDstX = selectionBitmapDstX;
    }

    public int getSelectionBitmapDstX() {
        return selectionBitmapDstX;
    }

    public void setSelectionBitmapDstY(int selectionBitmapDstY) {
        this.selectionBitmapDstY = selectionBitmapDstY;
    }

    public int getSelectionBitmapDstY() {
        return selectionBitmapDstY;
    }

    public void setSelectionBitmapDstWidth(int selectionBitmapDstWidth) {
        this.selectionBitmapDstWidth = selectionBitmapDstWidth;
    }

    public int getSelectionBitmapDstWidth() {
        return selectionBitmapDstWidth;
    }

    public void setSelectionBitmapDstHeight(int selectionBitmapDstHeight) {
        this.selectionBitmapDstHeight = selectionBitmapDstHeight;
    }

    public int getSelectionBitmapDstHeight() {
        return selectionBitmapDstHeight;
    }

    public void swapSelectionBitmapDstWidthHeight() {
        int tempWidth = getSelectionBitmapDstWidth();
        setSelectionBitmapDstWidth(getSelectionBitmapDstHeight());
        setSelectionBitmapDstHeight(tempWidth);
    }

    public void setSelectionBitmapRotateBuffer(RotateBuffer selectionBitmapRotateBuffer) {
        this.selectionBitmapRotateBuffer = selectionBitmapRotateBuffer;
    }

    public RotateBuffer getSelectionBitmapRotateBuffer() {
        return selectionBitmapRotateBuffer;
    }

    public void setSelectionBitmapFlipVerticalBuffer(FlipVerticalBuffer selectionBitmapFlipVerticalBuffer) {
        this.selectionBitmapFlipVerticalBuffer = selectionBitmapFlipVerticalBuffer;
    }

    public FlipVerticalBuffer getSelectionBitmapFlipVerticalBuffer() {
        return selectionBitmapFlipVerticalBuffer;
    }

    public void setSelectionBitmapFlipHorizontalBuffer(FlipHorizontalBuffer selectionBitmapFlipHorizontalBuffer) {
        this.selectionBitmapFlipHorizontalBuffer = selectionBitmapFlipHorizontalBuffer;
    }

    public FlipHorizontalBuffer getSelectionBitmapFlipHorizontalBuffer() {
        return selectionBitmapFlipHorizontalBuffer;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageQuality(int imageQuality) {
        this.imageQuality = MathUtils.clamp(imageQuality, IMAGE_QUALITY_MIN, IMAGE_QUALITY_MAX);
    }

    public int getImageQuality() {
        return imageQuality;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImageScale(int imageScale) {
        if(imageScale >= 1 && imageScale <= IMAGE_SCALE_MAX) {
            this.imageScale = imageScale;
            selectionPaint1.setStrokeWidth(imageScale * 0.5f + 0.5f);
            selectionPaint2.setStrokeWidth(imageScale * 0.25f + 0.25f);
            flushCanvasBackgroundPaint();
            flushGridPaint();
        }
    }

    public int getImageScale() {
        return imageScale;
    }

    public void setImageTranslationX(int imageTranslationX) {
        this.imageTranslationX = imageTranslationX;
    }

    public int getImageTranslationX() {
        return imageTranslationX;
    }

    public void setImageTranslationY(int imageTranslationY) {
        this.imageTranslationY = imageTranslationY;
    }

    public int getImageTranslationY() {
        return imageTranslationY;
    }

    public void setImageOriginX(int imageOriginX) {
        this.imageOriginX = imageOriginX;
    }

    public int getImageOriginX() {
        return imageOriginX;
    }

    public void setImageOriginY(int imageOriginY) {
        this.imageOriginY = imageOriginY;
    }

    public int getImageOriginY() {
        return imageOriginY;
    }

    public void setToolFlag(int toolFlag) {
        this.toolFlag = toolFlag;
    }

    public int getToolFlag() {
        return toolFlag;
    }

    public void setShapeFlag(int shapeFlag) {
        this.shapeFlag = shapeFlag;
    }

    public int getShapeFlag() {
        return shapeFlag;
    }

    public void setPaintFlag(int paintFlag) {
        this.paintFlag = paintFlag;
        switch (paintFlag) {
            case ToolFlag.PaintFlag.REPLACE:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                break;
            case ToolFlag.PaintFlag.OVERRIDE:
                paint.setXfermode(null);
                break;
        }
    }

    public int getPaintFlag() {
        return paintFlag;
    }

    public void setSelectionFlag(int selectionFlag) {
        this.selectionFlag = selectionFlag;
    }

    public int getSelectionFlag() {
        return selectionFlag;
    }

    public void setPaletteFlag(int paletteFlag) {
        this.paletteFlag = paletteFlag;
        if (listPalettes != null) {
            switch (paletteFlag) {
                case PaletteFlag.BACKGROUND:
                    listPalettes.setPalette(backgroundPalette);
                    break;
                case PaletteFlag.GRID:
                    listPalettes.setPalette(gridPalette);
                    break;
                case PaletteFlag.INTERNAL:
                    listPalettes.setPalette(builtinPalette);
                    break;
                case PaletteFlag.EXTERNAL:
                    listPalettes.setPalette(externalPalette);
                    break;
            }
        }
    }

    public void removeExternalPalette() {
        externalPaletteName = null;
        externalPalette = null;
    }

    public int getPaletteFlag() {
        return paletteFlag;
    }

    private void setExternalPaletteName(String externalPaletteName) {
        this.externalPaletteName = externalPaletteName;
    }

    @Nullable
    public String getExternalPaletteName() {
        return externalPaletteName;
    }

    public static String getBackgroundPaletteName() {
        return ApplicationUtils.getResources().getString(R.string.background_palette);
    }

    public static String getGridPaletteName() {
        return ApplicationUtils.getApplicationContext().getString(R.string.grid_palette);
    }

    public static String getBuiltinPaletteName() {
        return ApplicationUtils.getApplicationContext().getString(R.string.builtin_palette);
    }

    public void setPaintWidth(int paintWidth) {
        this.paintWidth = paintWidth;
        paint.setStrokeWidth(paintWidth);
        eraser.setStrokeWidth(paintWidth);
    }

    public int getPaintWidth() {
        return paintWidth;
    }

    public void setGridVisible(boolean gridVisible) {
        this.gridVisible = gridVisible;
        invalidateCanvasView();
    }

    public boolean isGridVisible() {
        return gridVisible;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
        flushGridPaint();
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
        flushGridPaint();
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridColor(int gridColor) {
        gridPalette.setCurrentColor(gridColor);
        flushGridPaint();
        invalidateCanvasView();
    }

    public int getGridColor() {
        return gridPalette.getCurrentColor();
    }

    public void resetBackgroundPalette() {
        backgroundPalette = Palette.createPalette(BACKGROUND_PALETTE_COLORS_DEFAULT);
        flushCanvasBackgroundPaint();
        invalidateCanvasView();
        if (listPalettes != null) {
            listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                    getCanvasBackgroundColor2());
        }
    }

    public Palette getBackgroundPalette() {
        return backgroundPalette;
    }

    public void resetGridPalette() {
        gridPalette = Palette.createPalette(GRID_PALETTE_COLORS_DEFAULT);
        setGridColor(gridPalette.getCurrentColor());
    }

    public Palette getGridPalette() {
        return gridPalette;
    }

    public void resetBuiltinPalette() {
        builtinPalette = Palette.createPalette(BUILTIN_PALETTE_COLORS_DEFAULT);
    }

    public Palette getBuiltinPalette() {
        return builtinPalette;
    }

    public void loadExternalPalette(@Nullable String externalPaletteName) {
        if (externalPalette != null) {
            if (!this.externalPaletteName.equals(externalPaletteName)) {
                PaletteFactory.encodeFile(externalPalette, getExternalPalettePathName(getExternalPaletteName()), true);
            }
        }
        if (externalPaletteName != null) {
            externalPalette = PaletteFactory.decodeFile(getExternalPalettePathName(externalPaletteName));
            if (externalPalette != null) {
                setExternalPaletteName(externalPaletteName);
                if (listPalettes != null) {
                    if (paletteFlag == PaletteFlag.EXTERNAL) {
                        listPalettes.setPalette(externalPalette);
                    }
                }
            }
        }
    }

    @Nullable
    public Palette getExternalPalette() {
        return externalPalette;
    }

    @NonNull
    public static List<String> getExternalPaletteNames() {
        File[] externalPaletteFiles = FileUtils.listFiles(new File(getPalettesPath()),
                new String[]{"palette"}, false).toArray(new File[0]);
        List<String> externalPaletteNames = new ArrayList<>();
        for (File externalPaletteFile : externalPaletteFiles) {
            externalPaletteNames.add(FilenameUtils.getBaseName(externalPaletteFile.getName()));
        }
        return externalPaletteNames;
    }

    public static String getPalettesPath() {
        return Utils.getFilesPath("palettes");
    }

    @NonNull
    public static String getExternalPalettePathName(@NonNull String externalPaletteName) {
        return getPalettesPath() + "/" + externalPaletteName + ".palette";
    }

    @NonNull
    public static String getCacheBitmapName() {
        return "CACHE.png";
    }

    @Nullable
    public static String getCacheBitmapPathname() {
        String cachePath = Utils.getCachePath();
        if (cachePath == null) {
            return null;
        }
        return cachePath + "/" + getCacheBitmapName();
    }

    @NonNull
    public static String getCurrentBitmapName() {
        return "CURRENT.png";
    }

    @Nullable
    public static String getCurrentBitmapPathname() {
        String cachePath = Utils.getCachePath();
        if (cachePath == null) {
            return null;
        }
        return cachePath + "/" + getCurrentBitmapName();
    }

    public ToolBufferPool getToolBufferPool() {
        return toolBufferPool;
    }

    public void setScaleMode(boolean scaleMode) {
        this.scaleMode = scaleMode;
    }

    public boolean isScaleMode() {
        return scaleMode;
    }

    public void setReadOnlyMode(boolean readOnlyMode) {
        this.readOnlyMode = readOnlyMode;
    }

    public boolean isReadOnlyMode() {
        return readOnlyMode;
    }

    public void setStrokeCap(Paint.Cap strokeCap) {
        paint.setStrokeCap(strokeCap);
        eraser.setStrokeCap(strokeCap);
    }


    public void setStrokeJoin(Paint.Join strokeJoin) {
        paint.setStrokeJoin(strokeJoin);
        eraser.setStrokeJoin(strokeJoin);
    }

    public void setBitmap(@NonNull Bitmap bitmap) {
        replaceCacheBitmap(bitmap);
        toolBufferPool = ToolBufferPool.createToolBufferPool(cacheBitmap,
                MAX_BUFFER_SIZE_DEFAULT, false);
        replaceCurrentBitmap(toolBufferPool.getCurrentBitmap());
        invalidateCanvasView();
    }

    public void invalidateCanvasView() {
        if (canvasView != null) {
            canvasView.invalidate();
        }
    }

}
