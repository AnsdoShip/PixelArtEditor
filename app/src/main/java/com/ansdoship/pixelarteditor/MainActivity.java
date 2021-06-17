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

package com.ansdoship.pixelarteditor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.ansdoship.pixelarteditor.editor.OriginFlag;
import com.ansdoship.pixelarteditor.editor.buffer.BitmapBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.ClearBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.FillBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.FlipHorizontalBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.FlipVerticalBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.MultiBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.PaintBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.PointBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.RotateBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.SelectionBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.ToolBufferPool;
import com.ansdoship.pixelarteditor.editor.graphics.BitmapDecoder;
import com.ansdoship.pixelarteditor.editor.graphics.BitmapEncoder;
import com.ansdoship.pixelarteditor.editor.graphics.BitmapUtils;
import com.ansdoship.pixelarteditor.editor.graphics.ColorFactory;
import com.ansdoship.pixelarteditor.editor.palette.Palette;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFactory;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFlag;
import com.ansdoship.pixelarteditor.editor.ToolFlag;
import com.ansdoship.pixelarteditor.ui.view.CanvasView;
import com.ansdoship.pixelarteditor.ui.view.CheckedImageView;
import com.ansdoship.pixelarteditor.ui.view.ColorPickerView;
import com.ansdoship.pixelarteditor.ui.view.PaletteView;
import com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView.FileListAdapter;
import com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView.ImageViewListAdapter;
import com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView.PaletteListAdapter;
import com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView.TextViewListAdapter;
import com.ansdoship.pixelarteditor.ui.viewgroup.CheckedImageGroup;
import com.ansdoship.pixelarteditor.ui.viewgroup.PaletteList;
import com.ansdoship.pixelarteditor.util.ApplicationUtils;
import com.ansdoship.pixelarteditor.util.CrashHandler;
import com.ansdoship.pixelarteditor.util.MarkdownUtils;
import com.ansdoship.pixelarteditor.util.MathUtils;
import com.ansdoship.pixelarteditor.util.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // DATA
    public final static String PREFERENCES_NAME = "app_data";

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
    public final static String KEY_IMAGE_TO_CENTER_X = "image_to_center_x";
    private int imageToCenterX;
    public final static String KEY_IMAGE_TO_CENTER_Y = "image_to_center_y";
    private int imageToCenterY;

    public final static String KEY_ORIGIN_FLAG_HORIZONTAL = "origin_flag_horizontal";
    private int originFlagHorizontal;
    public final static String KEY_ORIGIN_FLAG_VERTICAL = "origin_flag_vertical";
    private int originFlagVertical;

    public final static String KEY_TOOL_FLAG = "tool_flag";
    private int toolFlag;
    public final static String KEY_SHAPE_FLAG = "shape_flag";
    private int shapeFlag;
    public final static String KEY_PAINT_FLAG = "paint_flag";
    private int paintFlag;
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

    private void loadData() {

        if (toolBufferPool == null) {
            String cacheBitmapPathname = getCurrentBitmapPathname();
            if (cacheBitmapPathname != null) {
                replaceCacheBitmap(BitmapDecoder.decodeFile(cacheBitmapPathname));
            }
            if(cacheBitmap == null) {
                replaceCacheBitmap(Bitmap.createBitmap(IMAGE_WIDTH_DEFAULT,
                        IMAGE_HEIGHT_DEFAULT, Bitmap.Config.ARGB_8888));
            }
            setBitmap(cacheBitmap);
        }
        else {
            String cacheBitmapPathname = getCacheBitmapPathname();
            if (cacheBitmapPathname != null) {
                replaceCacheBitmap(BitmapDecoder.decodeFile(cacheBitmapPathname));
            }
            if (cacheBitmap == null) {
                replaceCacheBitmap(Bitmap.createBitmap(IMAGE_WIDTH_DEFAULT,
                        IMAGE_HEIGHT_DEFAULT, Bitmap.Config.ARGB_8888));
                setBitmap(cacheBitmap);
            }
            else {
                toolBufferPool.setCacheBitmap(cacheBitmap);
                toolBufferPool.flushCurrentBitmap();
            }
        }

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
        selectionPaint1.setStrokeWidth(imageScale * 0.5f + 0.5f);
        selectionPaint2 = new Paint();
        selectionPaint2.setDither(false);
        selectionPaint2.setAntiAlias(false);
        selectionPaint2.setStyle(Paint.Style.STROKE);
        selectionPaint2.setStrokeCap(Paint.Cap.SQUARE);
        selectionPaint2.setColor(Color.BLACK);
        selectionPaint2.setStrokeWidth(imageScale * 0.25f + 0.25f);
        path = new Path();
        matrix = new Matrix();
        canvasBackgroundMatrix = new Matrix();

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
        externalPaletteName = preferences.getString(KEY_EXTERNAL_PALETTE_NAME, EXTERNAL_PALETTE_NAME_DEFAULT);
        loadExternalPalette(externalPaletteName);

        imageName = preferences.getString(KEY_IMAGE_NAME, IMAGE_NAME_DEFAULT());
        flushImageNameView();
        imageFormat = preferences.getString(KEY_IMAGE_FORMAT, IMAGE_FORMAT_DEFAULT);
        setImageQuality(preferences.getInt(KEY_IMAGE_QUALITY, IMAGE_QUALITY_DEFAULT));
        imagePath = preferences.getString(KEY_IMAGE_PATH, IMAGE_PATH_DEFAULT());
        preSetImageScale(preferences.getInt(KEY_IMAGE_SCALE, IMAGE_SCALE_DEFAULT));
        imageToCenterX = preferences.getInt(KEY_IMAGE_TO_CENTER_X, IMAGE_TO_CENTER_X_DEFAULT());
        imageToCenterY = preferences.getInt(KEY_IMAGE_TO_CENTER_Y, IMAGE_TO_CENTER_Y_DEFAULT());
        originFlagHorizontal = preferences.getInt(KEY_ORIGIN_FLAG_HORIZONTAL, ORIGIN_FLAG_HORIZONTAL_DEFAULT);
        originFlagVertical = preferences.getInt(KEY_ORIGIN_FLAG_VERTICAL, ORIGIN_FLAG_VERTICAL_DEFAULT);
        toolFlag = preferences.getInt(KEY_TOOL_FLAG, TOOL_FLAG_DEFAULT);
        shapeFlag = preferences.getInt(KEY_SHAPE_FLAG, SHAPE_FLAG_DEFAULT);
        setPaintFlag(preferences.getInt(KEY_PAINT_FLAG, PAINT_FLAG_DEFAULT));
        selectionFlag = SELECTION_FLAG_DEFAULT;
        setPaletteFlag(preferences.getInt(KEY_PALETTE_FLAG, PALETTE_FLAG_DEFAULT));
        setPaintWidth(preferences.getInt(KEY_PAINT_WIDTH, PAINT_WIDTH_DEFAULT));
        setGridVisible(preferences.getBoolean(KEY_GRID_VISIBLE, GRID_VISIBLE_DEFAULT));
        setGridWidth(preferences.getInt(KEY_GRID_WIDTH, GRID_WIDTH_DEFAULT));
        setGridHeight(preferences.getInt(KEY_GRID_HEIGHT, GRID_HEIGHT_DEFAULT));
        scaleMode = SCALE_MODE_DEFAULT;
        readOnlyMode = READ_ONLY_MODE_DEFAULT;
        selected = SELECTED_DEFAULT;

        flushPaint(listPalettes.getCheckedPaletteColor());

    }

    private void saveData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString(KEY_IMAGE_NAME, imageName);
        editor.putString(KEY_IMAGE_FORMAT, imageFormat);
        editor.putInt(KEY_IMAGE_QUALITY, imageQuality);
        editor.putString(KEY_IMAGE_PATH, imagePath);
        editor.putInt(KEY_IMAGE_SCALE, imageScale);
        editor.putInt(KEY_IMAGE_TO_CENTER_X, imageToCenterX);
        editor.putInt(KEY_IMAGE_TO_CENTER_Y, imageToCenterY);
        editor.putInt(KEY_ORIGIN_FLAG_HORIZONTAL, originFlagHorizontal);
        editor.putInt(KEY_ORIGIN_FLAG_VERTICAL, originFlagVertical);
        editor.putInt(KEY_TOOL_FLAG, toolFlag);
        editor.putInt(KEY_SHAPE_FLAG, shapeFlag);
        editor.putInt(KEY_PAINT_FLAG, paintFlag);
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
                    toolBufferPool.getCacheBitmap(), true, BitmapEncoder.CompressFormat.PNG, 100);
        }
        String currentBitmapPathname = getCurrentBitmapPathname();
        if (currentBitmapPathname != null) {
            BitmapEncoder.encodeFile(currentBitmapPathname,
                    getCurrentBitmap(), true, BitmapEncoder.CompressFormat.PNG, 100);
        }
        BitmapUtils.recycleBitmap(cacheBitmap, getCurrentBitmap(), canvasBackgroundBitmap);

        if (externalPalette != null) {
            PaletteFactory.encodeFile(externalPalette, getExternalPalettePathname(externalPaletteName), true);
        }

    }

    public static String IMAGE_NAME_DEFAULT() {
    	return ApplicationUtils.getResources().getString(R.string.image_name_default);
    }
    public final static String IMAGE_FORMAT_DEFAULT = "png";
    public final static int IMAGE_QUALITY_MAX = 100;
    public final static int IMAGE_QUALITY_MIN = 30;
    public final static int IMAGE_QUALITY_DEFAULT = IMAGE_QUALITY_MAX;
    public static String IMAGE_PATH_DEFAULT() {
        return Utils.getFilesPath("images");
    }
    public final static int IMAGE_SCALE_DEFAULT = 16;
    public final static int IMAGE_SCALE_MIN = 1;
    public final static int IMAGE_SCALE_MAX = 64;
    private int IMAGE_TO_CENTER_X_DEFAULT() {
        return - getCurrentBitmap().getWidth() * imageScale / 2;
    }
    private int IMAGE_TO_CENTER_Y_DEFAULT() {
        return - getCurrentBitmap().getHeight() * imageScale / 2;
    }
    public final static int ORIGIN_FLAG_HORIZONTAL_DEFAULT = OriginFlag.LEFT;
    public final static int ORIGIN_FLAG_VERTICAL_DEFAULT = OriginFlag.TOP;
    public final static int IMAGE_WIDTH_DEFAULT = 32;
    public final static int IMAGE_HEIGHT_DEFAULT = 32;

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

    public final static int MAX_BUFFER_SIZE_DEFAULT = 40;

    public final static boolean SCALE_MODE_DEFAULT = false;
    public final static boolean READ_ONLY_MODE_DEFAULT = false;

    public final static boolean SELECTED_DEFAULT = false;

    public final static int IMAGE_WIDTH_MIN = 1;
    public final static int IMAGE_HEIGHT_MIN = 1;
    public final static int IMAGE_WIDTH_MAX = 2048;
    public final static int IMAGE_HEIGHT_MAX = 2048;

    private SharedPreferences preferences;

    private Bitmap cacheBitmap;
    private Bitmap canvasBackgroundBitmap;

    private Paint gridPaint;
    private Paint canvasBackgroundPaint;
    private Paint bitmapPaint;
    private Paint paint;
    private Paint eraser;
    private Paint selectionPaint1;
    private Paint selectionPaint2;

    private Matrix matrix;
    private Matrix canvasBackgroundMatrix;

    private Path path;

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

    private int downX;
    private int downY;
    private int moveX;
    private int moveY;
    private int upX;
    private int upY;

    private void replaceCacheBitmap(Bitmap newBitmap) {
        if (cacheBitmap == newBitmap) {
            return;
        }
        Bitmap temp = cacheBitmap;
        cacheBitmap = newBitmap;
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

    private void flushGridPaint() {
        gridPaint.setColor(gridPalette.getCurrentColor());
    }

    private void flushCanvasBackgroundPaint() {
        replaceCanvasBackgroundBitmap(Bitmap.createBitmap(new int[] {
                        getCanvasBackgroundColor1(),
                        getCanvasBackgroundColor2(),
                        getCanvasBackgroundColor2(),
                        getCanvasBackgroundColor1()},
                2, 2, Bitmap.Config.ARGB_8888));
        int canvasBackgroundImageScale = getCanvasBackgroundImageScale();
        replaceCanvasBackgroundBitmap(Bitmap.createScaledBitmap(
                canvasBackgroundBitmap,
                imageScale * canvasBackgroundImageScale,
                imageScale * canvasBackgroundImageScale, false));
        BitmapShader canvasBackgroundShader = new BitmapShader(
                canvasBackgroundBitmap,
                BitmapShader.TileMode.REPEAT,
                BitmapShader.TileMode.REPEAT);
        canvasBackgroundPaint.setShader(canvasBackgroundShader);
        listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                getCanvasBackgroundColor2());
    }

    private void flushPaint(int color) {
        paint.setColor(color);
    }

    private int getCanvasBackgroundImageScale() {
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

    private void setCanvasViewBackgroundColor (int backgroundColor) {
        backgroundPalette.setColor(0, backgroundColor);
        canvasView.invalidate();
    }

    private int getCanvasViewBackgroundColor () {
        return backgroundPalette.getColor(0);
    }

    private void setCanvasBackgroundColor1 (int backgroundColor) {
        backgroundPalette.setColor(1, backgroundColor);
        canvasView.invalidate();
    }

    private int getCanvasBackgroundColor1() {
        return backgroundPalette.getColor(1);
    }

    private void setCanvasBackgroundColor2 (int backgroundColor) {
        backgroundPalette.setColor(2, backgroundColor);
        canvasView.invalidate();
    }

    private int getCanvasBackgroundColor2() {
        return backgroundPalette.getColor(2);
    }

    private Bitmap getCurrentBitmap() {
        return toolBufferPool.getCurrentBitmap();
    }

    private void swapSelectionBitmapDstWidthHeight() {
        int tempWidth = selectionBitmapDstWidth;
        selectionBitmapDstWidth = selectionBitmapDstHeight;
        selectionBitmapDstHeight = tempWidth;
    }

    private void setImageQuality(int imageQuality) {
        this.imageQuality = MathUtils.clamp(imageQuality, IMAGE_QUALITY_MIN, IMAGE_QUALITY_MAX);
    }

    private void setImageScale(int imageScale) {
        int newScale = MathUtils.clamp(imageScale, IMAGE_SCALE_MIN, IMAGE_SCALE_MAX);
        int offset = this.imageScale - newScale;
        this.imageScale = newScale;
        imageToCenterX += offset * getCurrentBitmap().getWidth() / 2;
        imageToCenterY += offset * getCurrentBitmap().getHeight() / 2;
        selectionPaint1.setStrokeWidth(imageScale * 0.5f + 0.5f);
        selectionPaint2.setStrokeWidth(imageScale * 0.25f + 0.25f);
        flushCanvasBackgroundPaint();
        flushGridPaint();
        flushImageScaleView();
        canvasView.invalidate();
    }

    private void preSetImageScale(int imageScale) {
        this.imageScale = MathUtils.clamp(imageScale, IMAGE_SCALE_MIN, IMAGE_SCALE_MAX);
        selectionPaint1.setStrokeWidth(imageScale * 0.5f + 0.5f);
        selectionPaint2.setStrokeWidth(imageScale * 0.25f + 0.25f);
        flushCanvasBackgroundPaint();
        flushGridPaint();
        flushImageScaleView();
        canvasView.invalidate();
    }

    private void setPaintFlag(int paintFlag) {
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

    private void setPaletteFlag(int paletteFlag) {
        this.paletteFlag = paletteFlag;
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
                if (externalPalette == null) {
                    this.paletteFlag = PALETTE_FLAG_DEFAULT;
                    listPalettes.setPalette(builtinPalette);
                }
                else {
                    listPalettes.setPalette(externalPalette);
                }
                break;
        }
    }

    private void removeExternalPalette() {
        externalPaletteName = null;
        externalPalette = null;
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

    private void setPaintWidth(int paintWidth) {
        this.paintWidth = paintWidth;
        paint.setStrokeWidth(paintWidth);
        eraser.setStrokeWidth(paintWidth);
    }

    private void setGridVisible(boolean gridVisible) {
        this.gridVisible = gridVisible;
        canvasView.invalidate();
    }

    private void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
        flushGridPaint();
    }

    private void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
        flushGridPaint();
    }

    private void setGridSize(int gridWidth, int gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        flushGridPaint();
    }

    private void setGridColor(int gridColor) {
        gridPalette.setCurrentColor(gridColor);
        flushGridPaint();
        canvasView.invalidate();
    }

    private int getGridColor() {
        return gridPalette.getCurrentColor();
    }

    private void resetBackgroundPalette() {
        backgroundPalette = Palette.createPalette(BACKGROUND_PALETTE_COLORS_DEFAULT);
        flushCanvasBackgroundPaint();
        canvasView.invalidate();
        listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                getCanvasBackgroundColor2());
    }

    private void resetGridPalette() {
        gridPalette = Palette.createPalette(GRID_PALETTE_COLORS_DEFAULT);
        setGridColor(gridPalette.getCurrentColor());
    }

    private void resetBuiltinPalette() {
        builtinPalette = Palette.createPalette(BUILTIN_PALETTE_COLORS_DEFAULT);
    }

    private void loadExternalPalette(@Nullable String externalPaletteName) {
        if (externalPalette != null) {
            if (!this.externalPaletteName.equals(externalPaletteName)) {
                PaletteFactory.encodeFile(externalPalette,
                        getExternalPalettePathname(this.externalPaletteName), true);
            }
        }
        if (externalPaletteName != null) {
            externalPalette = PaletteFactory.decodeFile(getExternalPalettePathname(externalPaletteName));
            if (externalPalette != null) {
                this.externalPaletteName = externalPaletteName;
                if (paletteFlag == PaletteFlag.EXTERNAL) {
                    listPalettes.setPalette(externalPalette);
                    flushPaint(listPalettes.getCheckedPaletteColor());
                }
            }
        }
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
    public static String getExternalPalettePathname(@NonNull String externalPaletteName) {
        return getPalettesPath() + "/" + externalPaletteName + ".palette";
    }

    @NonNull
    private String getImagePathname(@NonNull String imageName) {
        return imagePath + "/" + imageName;
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

    private void setStrokeCap(Paint.Cap strokeCap) {
        paint.setStrokeCap(strokeCap);
        eraser.setStrokeCap(strokeCap);
    }


    private void setStrokeJoin(Paint.Join strokeJoin) {
        paint.setStrokeJoin(strokeJoin);
        eraser.setStrokeJoin(strokeJoin);
    }

    private void setBitmap(@NonNull Bitmap bitmap) {
        replaceCacheBitmap(bitmap);
        toolBufferPool = ToolBufferPool.createToolBufferPool(cacheBitmap,
                MAX_BUFFER_SIZE_DEFAULT, false);
        canvasView.invalidate();
        flushImageSizeView();
        flushImageScaleView();
        flushPointerCoordsView(upX - getOriginX(), upY - getOriginY());
    }

    private boolean dataSaved = false;
    private boolean uiLoaded = false;

    // WIDGETS
    // TopBar
    private TextView tvImageName;
    private ImageButton imgUndo;
    private ImageButton imgRedo;
    private ImageButton imgMenu;
    private ImageButton imgRecenter;
    private ImageButton imgGrid;
    private TextView tvImageSize;
    private TextView tvImageScale;
    private TextView tvPointerCoords;
    private TextView tvSelectionSize;
    // ToolBar
    private TextView tvPaintWidth;
    private CheckedImageGroup groupTools;
    private CheckedImageView imgPaint;
    private CheckedImageView imgShape;
    private CheckedImageView imgEraser;
    private CheckedImageView imgFill;
    private CheckedImageView imgSelection;
    private CheckedImageView imgColorize;
    // PaletteBar
    private ImageButton imgPalette;
    private PaletteList listPalettes;
    // CanvasView
    private CanvasView canvasView;

    // POPUPS
    // Selection popup 1
    private void flushSelectionBitmapStatus() {
        selectionBitmapSrcX = Math.min(downX, moveX);
        selectionBitmapSrcY = Math.min(downY, moveY);
        selectionBitmapSrcWidth = Math.abs(downX - moveX) + 1;
        selectionBitmapSrcHeight = Math.abs(downY - moveY) + 1;
        selectionBitmapDstX = selectionBitmapSrcX;
        selectionBitmapDstY = selectionBitmapSrcY;
        selectionBitmapDstWidth = selectionBitmapSrcWidth;
        selectionBitmapDstHeight = selectionBitmapSrcHeight;
    }
    private void buildSelectionPopup1() {
        View view = View.inflate(this, R.layout.popup_selection_1, null);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final PopupWindow window = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        int xOffset = upX * imageScale + getImageTranslationX();
        int yOffset = upY * imageScale + getImageTranslationY();
        int gravity;
        if (yOffset + view.getMeasuredHeight() < canvasView.getHeight()) {
            gravity = Gravity.START;
        }
        else {
            gravity = Gravity.END;
            yOffset -= view.getMeasuredHeight();
            yOffset += imageScale;
        }
        if (xOffset + view.getMeasuredWidth() < canvasView.getWidth()) {
            gravity = gravity | Gravity.TOP;
        }
        else {
            gravity = gravity | Gravity.BOTTOM;
            xOffset -= view.getMeasuredWidth();
            xOffset += imageScale;
        }
        switch (gravity) {
            case Gravity.START | Gravity.TOP:
                window.setAnimationStyle(R.style.animTranslateInLeftTop);
                break;
            case Gravity.END| Gravity.TOP:
                window.setAnimationStyle(R.style.animTranslateInRightTop);
                break;
            case Gravity.START | Gravity.BOTTOM:
                window.setAnimationStyle(R.style.animTranslateInLeftBottom);
                break;
            case Gravity.END | Gravity.BOTTOM:
                window.setAnimationStyle(R.style.animTranslateInRightBottom);
                break;
        }
        yOffset += getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height",
                "dimen", "android"));
        window.showAtLocation(canvasView, Gravity.START | Gravity.TOP, xOffset, yOffset);
        ImageButton imgCut = view.findViewById(R.id.img_cut);
        ImageButton imgCopy = view.findViewById(R.id.img_copy);
        ImageButton imgClear = view.findViewById(R.id.img_clear);
        ImageButton imgNone = view.findViewById(R.id.img_none);
        imgCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flushSelectionBitmapStatus();
                selectionFlag = ToolFlag.SelectionFlag.CUT;
                flushSelectedBitmap();
                toolBufferPool.addToolBuffer(getClearBuffer());
                window.dismiss();
                buildSelectionPopup2();
            }
        });
        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flushSelectionBitmapStatus();
                selectionFlag = ToolFlag.SelectionFlag.COPY;
                window.dismiss();
                buildSelectionPopup2();
            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flushSelectionBitmapStatus();
                selectionFlag = ToolFlag.SelectionFlag.NONE;
                toolBufferPool.addToolBuffer(getClearBuffer());
                selected = false;
                canvasView.invalidate();
                window.dismiss();
            }
        });
        imgNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionFlag = ToolFlag.SelectionFlag.NONE;
                selected = false;
                canvasView.invalidate();
                window.dismiss();
            }
        });
    }
    private Bitmap selectedBitmap;
    private void flushPasteBitmap(@NonNull Bitmap bitmap) {
        upX = 0;
        upY = 0;
        selectionFlag = ToolFlag.SelectionFlag.CUT;
        selectionBitmapDstX = 0;
        selectionBitmapDstY = 0;
        selectionBitmapDstWidth = bitmap.getWidth();
        selectionBitmapDstHeight = bitmap.getHeight();
        selectedBitmap = bitmap;
        selected = true;
    }
    private void flushSelectedBitmap() {
        selectedBitmap = Bitmap.createBitmap(getCurrentBitmap(),
                selectionBitmapSrcX, selectionBitmapSrcY,
                selectionBitmapSrcWidth, selectionBitmapSrcHeight);
    }
    private @NonNull ClearBuffer getClearBuffer() {
        return new ClearBuffer(
                selectionBitmapSrcX, selectionBitmapSrcY,
                selectionBitmapSrcWidth, selectionBitmapSrcHeight);
    }
    private @NonNull SelectionBuffer getSelectionBuffer() {
        return new SelectionBuffer(
                selectionBitmapSrcX,
                selectionBitmapSrcY,
                selectionBitmapSrcWidth,
                selectionBitmapSrcHeight,
                selectionBitmapDstX,
                selectionBitmapDstY,
                selectionBitmapRotateBuffer,
                selectionBitmapFlipVerticalBuffer,
                selectionBitmapFlipHorizontalBuffer
        );
    }
    private @NonNull BitmapBuffer getBitmapBuffer() {
        return new BitmapBuffer(
                selectedBitmap,
                selectionBitmapDstX,
                selectionBitmapDstY,
                selectionBitmapRotateBuffer,
                selectionBitmapFlipVerticalBuffer,
                selectionBitmapFlipHorizontalBuffer
        );
    }
    // Selection popup 2
    private void buildSelectionPopup2() {
        View view = View.inflate(this, R.layout.popup_selection_2, null);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final PopupWindow window = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        int xOffset = upX * imageScale + getImageTranslationX();
        int yOffset = upY * imageScale + getImageTranslationY();
        int gravity;
        if (yOffset + view.getMeasuredHeight() < canvasView.getHeight()) {
            gravity = Gravity.START;
        }
        else {
            gravity = Gravity.END;
            yOffset -= view.getMeasuredHeight();
            yOffset += imageScale;
        }
        if (xOffset + view.getMeasuredWidth() < canvasView.getWidth()) {
            gravity = gravity | Gravity.TOP;
        }
        else {
            gravity = gravity | Gravity.BOTTOM;
            xOffset -= view.getMeasuredWidth();
            xOffset += imageScale;
        }
        switch (gravity) {
            case Gravity.START | Gravity.TOP:
                window.setAnimationStyle(R.style.animTranslateInLeftTop);
                break;
            case Gravity.END| Gravity.TOP:
                window.setAnimationStyle(R.style.animTranslateInRightTop);
                break;
            case Gravity.START | Gravity.BOTTOM:
                window.setAnimationStyle(R.style.animTranslateInLeftBottom);
                break;
            case Gravity.END | Gravity.BOTTOM:
                window.setAnimationStyle(R.style.animTranslateInRightBottom);
                break;
        }
        yOffset += getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height",
                "dimen", "android"));
        window.showAtLocation(canvasView, Gravity.START | Gravity.TOP, xOffset, yOffset);
        ImageButton imgRotateLeft = view.findViewById(R.id.img_rotate_left);
        ImageButton imgRotateRight = view.findViewById(R.id.img_rotate_right);
        ImageButton imgFlipHorizontal = view.findViewById(R.id.img_flip_horizontal);
        ImageButton imgFlipVertical = view.findViewById(R.id.img_flip_vertical);
        ImageButton imgDone = view.findViewById(R.id.img_done);
        ImageButton imgNone = view.findViewById(R.id.img_none);
        imgRotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int degrees = -90;
                if (selectionBitmapRotateBuffer != null) {
                    degrees = degrees + selectionBitmapRotateBuffer.getDegrees();
                }
                if (degrees <= -360) {
                    degrees = degrees + 360;
                }
                swapSelectionBitmapDstWidthHeight();
                selectionBitmapRotateBuffer = new RotateBuffer(degrees);
                toolBufferPool.clearTempToolBuffers();
                switch (selectionFlag) {
                    case ToolFlag.SelectionFlag.CUT:
                        toolBufferPool.addTempToolBuffer(getBitmapBuffer());
                        break;
                    case ToolFlag.SelectionFlag.COPY:
                        toolBufferPool.addTempToolBuffer(getSelectionBuffer());
                        break;
                }
                canvasView.invalidate();
            }
        });
        imgRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int degrees = 90;
                if (selectionBitmapRotateBuffer != null) {
                    degrees = degrees + selectionBitmapRotateBuffer.getDegrees();
                }
                if (degrees >= 360) {
                    degrees = degrees - 360;
                }
                swapSelectionBitmapDstWidthHeight();
                selectionBitmapRotateBuffer = new RotateBuffer(degrees);
                toolBufferPool.clearTempToolBuffers();
                switch (selectionFlag) {
                    case ToolFlag.SelectionFlag.CUT:
                        toolBufferPool.addTempToolBuffer(getBitmapBuffer());
                        break;
                    case ToolFlag.SelectionFlag.COPY:
                        toolBufferPool.addTempToolBuffer(getSelectionBuffer());
                        break;
                }
                canvasView.invalidate();
            }
        });
        imgFlipHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectionBitmapFlipHorizontalBuffer == null) {
                    selectionBitmapFlipHorizontalBuffer = new FlipHorizontalBuffer();
                }
                else {
                    selectionBitmapFlipHorizontalBuffer = null;
                }
                toolBufferPool.clearTempToolBuffers();
                switch (selectionFlag) {
                    case ToolFlag.SelectionFlag.CUT:
                        toolBufferPool.addTempToolBuffer(getBitmapBuffer());
                        break;
                    case ToolFlag.SelectionFlag.COPY:
                        toolBufferPool.addTempToolBuffer(getSelectionBuffer());
                        break;
                }
                canvasView.invalidate();
            }
        });
        imgFlipVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectionBitmapFlipVerticalBuffer == null) {
                    selectionBitmapFlipVerticalBuffer = new FlipVerticalBuffer();
                }
                else {
                    selectionBitmapFlipVerticalBuffer = null;
                }
                toolBufferPool.clearTempToolBuffers();
                switch (selectionFlag) {
                    case ToolFlag.SelectionFlag.CUT:
                        toolBufferPool.addTempToolBuffer(getBitmapBuffer());
                        break;
                    case ToolFlag.SelectionFlag.COPY:
                        toolBufferPool.addTempToolBuffer(getSelectionBuffer());
                        break;
                }
                canvasView.invalidate();
            }
        });
        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectionFlag) {
                    case ToolFlag.SelectionFlag.CUT:
                        toolBufferPool.addToolBuffer(getBitmapBuffer());
                        break;
                    case ToolFlag.SelectionFlag.COPY:
                        toolBufferPool.addToolBuffer(getSelectionBuffer());
                        break;
                }
                selectionFlag = ToolFlag.SelectionFlag.NONE;
                selectionBitmapRotateBuffer = null;
                selectionBitmapFlipVerticalBuffer = null;
                selectionBitmapFlipHorizontalBuffer = null;
                selected = false;
                canvasView.invalidate();
                window.dismiss();
            }
        });
        imgNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBufferPool.clearTempToolBuffers();
                BitmapUtils.recycleBitmap(selectedBitmap);
                selectionFlag = ToolFlag.SelectionFlag.NONE;
                selectionBitmapRotateBuffer = null;
                selectionBitmapFlipVerticalBuffer = null;
                selectionBitmapFlipHorizontalBuffer = null;
                selected = false;
                canvasView.invalidate();
                window.dismiss();
            }
        });
    }
    // Menu popup
    private void buildMenuPopup() {
        View view = View.inflate(this, R.layout.popup_menu, null);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final PopupWindow window = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.setAnimationStyle(R.style.animTranslateInLeftBottom);
        window.showAsDropDown(imgMenu);
        ImageButton imgLoad = view.findViewById(R.id.img_load);
        ImageButton imgSave = view.findViewById(R.id.img_save);
        ImageButton imgHelp = view.findViewById(R.id.img_help);
        ImageButton imgInfo = view.findViewById(R.id.img_info);
        ImageButton imgExit = view.findViewById(R.id.img_exit);
        imgLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildLoadDialog();
                window.dismiss();
            }
        });
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildSaveDialog(imageName);
                window.dismiss();
            }
        });
        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildHelpDialog();
                window.dismiss();
            }
        });
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildInfoDialog();
                window.dismiss();
            }
        });
        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
                finish();
            }
        });
    }

    // DIALOGS
    // Help dialog
    private void buildHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme).setView(R.layout.dialog_markdown);
        AlertDialog dialog = builder.create();
        dialog.show();
        MarkdownUtils.loadIntoWithAssets(dialog.findViewById(R.id.dialog_markdown), "help.md",true);
    }
    // Info dialog
    private void buildInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme).setView(R.layout.dialog_markdown);
        builder.setNegativeButton(R.string.copyright, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buildCopyrightDialog();
            }
        });
        builder.setPositiveButton(R.string.donate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buildDonateDialog();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        MarkdownUtils.loadIntoWithAssets(dialog.findViewById(R.id.dialog_markdown), "info.md",true);
    }
    // Copyright dialog
    private void buildCopyrightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme).setView(R.layout.dialog_markdown);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                buildInfoDialog();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        MarkdownUtils.loadIntoWithAssets(dialog.findViewById(R.id.dialog_markdown),"copyright.md",false);
    }
    // Donate dialog
    private void buildDonateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.alipay_qrcode));
        builder.setView(imageView);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                buildInfoDialog();
            }
        });
        builder.create().show();
    }
    // Paint flag dialog
    private void buildPaintFlagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        String [] items = {
                getString(R.string.replace),
                getString(R.string.override)
        };
        int checkedItem = -1;
        switch (paintFlag) {
            case ToolFlag.PaintFlag.REPLACE:
                checkedItem = 0;
                break;
            case ToolFlag.PaintFlag.OVERRIDE:
                checkedItem = 1;
                break;
        }
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        paintFlag = ToolFlag.PaintFlag.REPLACE;
                        break;
                    case 1:
                        paintFlag = ToolFlag.PaintFlag.OVERRIDE;
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    // Paint width dialog
    private int dialogTempPaintWidth;
    @SuppressLint("SetTextI18n")
    private void buildPaintWidthDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_paint_width, null);
        SeekBar barPaintWidthValue = view.findViewById(R.id.bar_paint_width_value);
        final TextView tvPaintWidthValue = view.findViewById(R.id.tv_paint_width_value);
        barPaintWidthValue.setProgress(paintWidth - 1);
        tvPaintWidthValue.setText(Integer.toString(paintWidth));
        barPaintWidthValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dialogTempPaintWidth = progress + 1;
                tvPaintWidthValue.setText(Integer.toString(dialogTempPaintWidth));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPaintWidth(dialogTempPaintWidth);
                tvPaintWidth.setText(Integer.toString(paintWidth));
                tvPaintWidth.requestLayout();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        builder.create().show();
    }
    // Paint width dialog
    private int dialogTempImageScale;
    @SuppressLint("SetTextI18n")
    private void buildImageScaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_image_scale, null);
        SeekBar barImageScaleValue = view.findViewById(R.id.bar_image_scale_value);
        final TextView tvImageScaleValue = view.findViewById(R.id.tv_image_scale_value);
        barImageScaleValue.setProgress((int) MathUtils.log(imageScale, 2));
        tvImageScaleValue.setText(imageScale * 100 + "%");
        barImageScaleValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dialogTempImageScale = (int) Math.pow(2, progress);
                tvImageScaleValue.setText(dialogTempImageScale * 100 + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setImageScale(dialogTempImageScale);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        builder.create().show();
    }
    private int dialogTempOriginFlagHorizontal;
    private int dialogTempOriginFlagVertical;
    private void buildOriginFlagDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_origin, null);
        RadioGroup groupOriginFlagHorizontal = view.findViewById(R.id.group_origin_flag_horizontal);
        RadioGroup groupOriginFlagVertical = view.findViewById(R.id.group_origin_flag_vertical);
        switch (originFlagHorizontal) {
            case OriginFlag.LEFT:
                groupOriginFlagHorizontal.check(R.id.btn_origin_flag_horizontal_left);
                break;
            case OriginFlag.CENTER:
                groupOriginFlagHorizontal.check(R.id.btn_origin_flag_horizontal_center);
                break;
            case OriginFlag.RIGHT:
                groupOriginFlagHorizontal.check(R.id.btn_origin_flag_horizontal_right);
                break;
        }
        switch (originFlagVertical) {
            case OriginFlag.TOP:
                groupOriginFlagVertical.check(R.id.btn_origin_flag_vertical_top);
                break;
            case OriginFlag.CENTER:
                groupOriginFlagVertical.check(R.id.btn_origin_flag_vertical_center);
                break;
            case OriginFlag.BOTTOM:
                groupOriginFlagVertical.check(R.id.btn_origin_flag_vertical_bottom);
                break;
        }
        groupOriginFlagHorizontal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_origin_flag_horizontal_left:
                        dialogTempOriginFlagHorizontal = OriginFlag.LEFT;
                        break;
                    case R.id.btn_origin_flag_horizontal_center:
                        dialogTempOriginFlagHorizontal = OriginFlag.CENTER;
                        break;
                    case R.id.btn_origin_flag_horizontal_right:
                        dialogTempOriginFlagHorizontal = OriginFlag.RIGHT;
                        break;
                }
            }
        });
        groupOriginFlagVertical.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_origin_flag_vertical_top:
                        dialogTempOriginFlagVertical = OriginFlag.TOP;
                        break;
                    case R.id.btn_origin_flag_vertical_center:
                        dialogTempOriginFlagVertical = OriginFlag.CENTER;
                        break;
                    case R.id.btn_origin_flag_vertical_bottom:
                        dialogTempOriginFlagVertical = OriginFlag.BOTTOM;
                        break;
                }
            }
        });
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                originFlagHorizontal = dialogTempOriginFlagHorizontal;
                originFlagVertical = dialogTempOriginFlagVertical;
                flushPointerCoordsView(upX - getOriginX(), upY - getOriginY());
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        }).create().show();
    }
    // Shape flag dialog
    private void buildShapeFlagDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_recycler_view, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        RecyclerView recyclerView = (RecyclerView) view;
        List<Drawable> images = new ArrayList<>();
        images.add(VectorDrawableCompat.create(getResources(), R.drawable.ic_line_24, getTheme()));
        images.add(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_circle_24, getTheme()));
        images.add(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_ellipse_24, getTheme()));
        images.add(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_square_24, getTheme()));
        images.add(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_rectangle_24, getTheme()));
        int checkedPosition = -1;
        switch (shapeFlag) {
            case ToolFlag.ShapeFlag.LINE:
                checkedPosition = 0;
                break;
            case ToolFlag.ShapeFlag.CIRCLE:
                checkedPosition = 1;
                break;
            case ToolFlag.ShapeFlag.ELLIPSE:
                checkedPosition = 2;
                break;
            case ToolFlag.ShapeFlag.SQUARE:
                checkedPosition = 3;
                break;
            case ToolFlag.ShapeFlag.RECTANGLE:
                checkedPosition = 4;
                break;
        }
        ImageViewListAdapter adapter = new ImageViewListAdapter(this, images, checkedPosition);
        adapter.setOnItemClickListener(new ImageViewListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        shapeFlag = ToolFlag.ShapeFlag.LINE;
                        imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                                R.drawable.ic_line_24, getTheme()));
                        break;
                    case 1:
                        shapeFlag = ToolFlag.ShapeFlag.CIRCLE;
                        imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                                R.drawable.ic_outline_circle_24, getTheme()));
                        break;
                    case 2:
                        shapeFlag = ToolFlag.ShapeFlag.ELLIPSE;
                        imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                                R.drawable.ic_outline_ellipse_24, getTheme()));
                        break;
                    case 3:
                        shapeFlag = ToolFlag.ShapeFlag.SQUARE;
                        imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                                R.drawable.ic_outline_square_24, getTheme()));
                        break;
                    case 4:
                        shapeFlag = ToolFlag.ShapeFlag.RECTANGLE;
                        imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                                R.drawable.ic_outline_rectangle_24, getTheme()));
                        break;
                }
                alertDialog.dismiss();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this,
                5, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        alertDialog.show();
    }
    // Color picker dialog
    private int dialogTempColor;
    private int dialogTempColorH;
    private float dialogTempColorS;
    private float dialogTempColorV;
    @SuppressLint("SetTextI18n")
    private void buildColorPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_palette, null);
        TabHost tabHost = view.findViewById(R.id.tabhost_palette);
        tabHost.setup();
        TabHost.TabSpec picker = tabHost.newTabSpec("picker");
        picker.setIndicator("RECT");
        picker.setContent(R.id.ambilwarna_dialogView);
        tabHost.addTab(picker);
        TabHost.TabSpec rgb = tabHost.newTabSpec("rgb");
        rgb.setIndicator("RGB");
        rgb.setContent(R.id.ll_palette_rgb);
        tabHost.addTab(rgb);
        TabHost.TabSpec hsv = tabHost.newTabSpec("hsv");
        hsv.setIndicator("HSV");
        hsv.setContent(R.id.ll_palette_hsv);
        tabHost.addTab(hsv);
        builder.setView(view);
        final PaletteView palette = view.findViewById(R.id.palette_dialog);
        palette.setPaletteBackgroundColors(
                getCanvasBackgroundColor1(),
                getCanvasBackgroundColor2());
        final TextView tvPaletteColorValue = view.findViewById(R.id.tv_palette_color_value);

        final ColorPickerView colorPicker = view.findViewById(R.id.tab_picker);
        final TextView tvColorA = view.findViewById(R.id.tv_color_a);
        final TextView tvColorR = view.findViewById(R.id.tv_color_r);
        final TextView tvColorG = view.findViewById(R.id.tv_color_g);
        final TextView tvColorB = view.findViewById(R.id.tv_color_b);
        final TextView tvColorH = view.findViewById(R.id.tv_color_h);
        final TextView tvColorS = view.findViewById(R.id.tv_color_s);
        final TextView tvColorV = view.findViewById(R.id.tv_color_v);
        SeekBar barColorA = view.findViewById(R.id.bar_color_a);
        final SeekBar barColorR = view.findViewById(R.id.bar_color_r);
        final SeekBar barColorG = view.findViewById(R.id.bar_color_g);
        final SeekBar barColorB = view.findViewById(R.id.bar_color_b);
        final SeekBar barColorH = view.findViewById(R.id.bar_color_h);
        final SeekBar barColorS = view.findViewById(R.id.bar_color_s);
        final SeekBar barColorV = view.findViewById(R.id.bar_color_v);
        dialogTempColor = listPalettes.getPaletteColor(listPalettes.getCheckedIndex());
        dialogTempColorH = (int) ColorFactory.hue(dialogTempColor);
        dialogTempColorS = ColorFactory.saturation(dialogTempColor);
        dialogTempColorV = ColorFactory.value(dialogTempColor);

        colorPicker.setOrdinalColor(dialogTempColor);
        tvPaletteColorValue.setText(ColorFactory.colorToHexString(dialogTempColor));
        tvColorA.setText("A: " + Color.alpha(dialogTempColor));
        tvColorR.setText("R: " + Color.red(dialogTempColor));
        tvColorG.setText("G: " + Color.green(dialogTempColor));
        tvColorB.setText("B: " + Color.blue(dialogTempColor));
        tvColorH.setText("H: " + (int) ColorFactory.hue(dialogTempColor));
        tvColorS.setText("S: " + (int) (ColorFactory.saturation(dialogTempColor) * 100));
        tvColorV.setText("V: " + (int) (ColorFactory.value(dialogTempColor) * 100));
        palette.setPaletteColor(dialogTempColor);
        barColorA.setProgress(Color.alpha(dialogTempColor));
        barColorR.setProgress(Color.red(dialogTempColor));
        barColorG.setProgress(Color.green(dialogTempColor));
        barColorB.setProgress(Color.blue(dialogTempColor));
        barColorH.setProgress((int) ColorFactory.hue(dialogTempColor));
        barColorS.setProgress((int) (ColorFactory.saturation(dialogTempColor) * 100));
        barColorV.setProgress((int) (ColorFactory.value(dialogTempColor) * 100));

        colorPicker.setOnColorPickedListener(new ColorPickerView.OnColorPickedListener() {
            @Override
            public void onUpdate(ColorPickerView view, int color) {
                dialogTempColor = Color.argb(Color.alpha(dialogTempColor),Color.red(color),Color.green(color),Color.blue(color));
                tvPaletteColorValue.setText(ColorFactory.colorToHexString(dialogTempColor));
                barColorH.setProgress((int) ColorFactory.hue(dialogTempColor));
                barColorS.setProgress((int) ColorFactory.saturation(dialogTempColor) * 100);
                barColorV.setProgress((int) ColorFactory.value(dialogTempColor) * 100);
                barColorR.setProgress(Color.red(dialogTempColor));
                barColorG.setProgress(Color.green(dialogTempColor));
                barColorB.setProgress(Color.blue(dialogTempColor));
                palette.setPaletteColor(color);
            }
        });

        barColorA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorA.setText("A: " + progress);
                dialogTempColor = ColorFactory.resetAlpha(dialogTempColor, progress);
                tvPaletteColorValue.setText(ColorFactory.colorToHexString(dialogTempColor));
                palette.setPaletteColor(dialogTempColor);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        barColorR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorR.setText("R: " + progress);
                if (fromUser) {
                    dialogTempColor = ColorFactory.resetRed(dialogTempColor, progress);
                    tvPaletteColorValue.setText(ColorFactory.colorToHexString(dialogTempColor));
                    barColorH.setProgress((int) ColorFactory.hue(dialogTempColor));
                    barColorS.setProgress((int) ColorFactory.saturation(dialogTempColor) * 100);
                    barColorV.setProgress((int) ColorFactory.value(dialogTempColor) * 100);
                    palette.setPaletteColor(dialogTempColor);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        barColorG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorG.setText("G: " + progress);
                if (fromUser) {
                    dialogTempColor = ColorFactory.resetGreen(dialogTempColor, progress);
                    tvPaletteColorValue.setText(ColorFactory.colorToHexString(dialogTempColor));
                    barColorH.setProgress((int) ColorFactory.hue(dialogTempColor));
                    barColorS.setProgress((int) ColorFactory.saturation(dialogTempColor) * 100);
                    barColorV.setProgress((int) ColorFactory.value(dialogTempColor) * 100);
                    palette.setPaletteColor(dialogTempColor);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        barColorB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorB.setText("B: " + progress);
                if (fromUser) {
                    dialogTempColor = ColorFactory.resetBlue(dialogTempColor, progress);
                    tvPaletteColorValue.setText(ColorFactory.colorToHexString(dialogTempColor));
                    barColorH.setProgress((int) ColorFactory.hue(dialogTempColor));
                    barColorS.setProgress((int) ColorFactory.saturation(dialogTempColor) * 100);
                    barColorV.setProgress((int) ColorFactory.value(dialogTempColor) * 100);
                    palette.setPaletteColor(dialogTempColor);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        barColorH.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorH.setText("H: " + progress);
                if (fromUser) {
                    dialogTempColorH = progress;
                    dialogTempColor = ColorFactory.resetValue(dialogTempColor, dialogTempColorV);
                    dialogTempColor = ColorFactory.resetSaturation(dialogTempColor, dialogTempColorS);
                    dialogTempColor = ColorFactory.resetHue(dialogTempColor, dialogTempColorH);
                    tvPaletteColorValue.setText(ColorFactory.colorToHexString(dialogTempColor));
                    barColorR.setProgress(Color.red(dialogTempColor));
                    barColorG.setProgress(Color.green(dialogTempColor));
                    barColorB.setProgress(Color.blue(dialogTempColor));
                    palette.setPaletteColor(dialogTempColor);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        barColorS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorS.setText("S: " + progress);
                if (fromUser) {
                    dialogTempColorS = progress * 0.01f;
                    dialogTempColor = ColorFactory.resetValue(dialogTempColor, dialogTempColorV);
                    dialogTempColor = ColorFactory.resetSaturation(dialogTempColor, dialogTempColorS);
                    dialogTempColor = ColorFactory.resetHue(dialogTempColor, dialogTempColorH);
                    tvPaletteColorValue.setText(ColorFactory.colorToHexString(dialogTempColor));
                    barColorR.setProgress(Color.red(dialogTempColor));
                    barColorG.setProgress(Color.green(dialogTempColor));
                    barColorB.setProgress(Color.blue(dialogTempColor));
                    palette.setPaletteColor(dialogTempColor);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        barColorV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorV.setText("V: " + progress);
                if (fromUser) {
                    dialogTempColorV = progress * 0.01f;
                    dialogTempColor = ColorFactory.resetValue(dialogTempColor, dialogTempColorV);
                    dialogTempColor = ColorFactory.resetSaturation(dialogTempColor, dialogTempColorS);
                    dialogTempColor = ColorFactory.resetHue(dialogTempColor, dialogTempColorH);
                    tvPaletteColorValue.setText(ColorFactory.colorToHexString(dialogTempColor));
                    barColorR.setProgress(Color.red(dialogTempColor));
                    barColorG.setProgress(Color.green(dialogTempColor));
                    barColorB.setProgress(Color.blue(dialogTempColor));
                    palette.setPaletteColor(dialogTempColor);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listPalettes.setCheckedPaletteColor(dialogTempColor);
                flushColors(dialogTempColor);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.create().show();
    }

    private void flushColors(int paintColor) {
        flushPaint(paintColor);
        switch (paletteFlag) {
            case PaletteFlag.BACKGROUND:
                if (listPalettes.getCheckedIndex() == 0) {
                    setCanvasViewBackgroundColor(listPalettes.getCheckedPaletteColor());
                }
                else {
                    flushCanvasBackgroundPaint();
                }
                break;
            case PaletteFlag.GRID:
                setGridColor(listPalettes.getCheckedPaletteColor());
                break;
        }
    }
    // Palette flag dialog
    private void buildPaletteFlagDialog () {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_recycler_view, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buildAddPaletteDialog();
            }
        });
        final AlertDialog alertDialog = builder.create();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> internalPaletteNames = new ArrayList<>();
        internalPaletteNames.add(getString(R.string.background_palette));
        internalPaletteNames.add(getString(R.string.grid_palette));
        internalPaletteNames.add(getString(R.string.builtin_palette));
        List<String> externalPaletteNames = getExternalPaletteNames();
        int checkedPosition = -1;
        switch (paletteFlag) {
            case PaletteFlag.BACKGROUND:
                checkedPosition = 0;
                break;
            case PaletteFlag.GRID:
                checkedPosition = 1;
                break;
            case PaletteFlag.INTERNAL:
                checkedPosition = 2;
                break;
            case PaletteFlag.EXTERNAL:
                checkedPosition = 3;
                checkedPosition += externalPaletteNames.indexOf(externalPaletteName);
                break;
        }
        final PaletteListAdapter adapter = new PaletteListAdapter(this,
                internalPaletteNames, externalPaletteNames, checkedPosition);
        adapter.setOnItemClickListener(new PaletteListAdapter.OnItemClickListener() {
            @Override
            public void onInternalPaletteClick(int position) {
                switch (position) {
                    case 0:
                        paletteFlag = PaletteFlag.BACKGROUND;
                        listPalettes.setPalette(backgroundPalette);
                        break;
                    case 1:
                        paletteFlag = PaletteFlag.GRID;
                        listPalettes.setPalette(gridPalette);
                        break;
                    case 2:
                        paletteFlag = PaletteFlag.INTERNAL;
                        listPalettes.setPalette(builtinPalette);
                        break;
                }
                flushPaint(listPalettes.getCheckedPaletteColor());
                alertDialog.dismiss();
            }
            @Override
            public void onExternalPaletteClick(int position) {
                loadExternalPalette(externalPaletteNames.get(position));
                if (externalPalette == null) {
                    alertDialog.dismiss();
                    buildPaletteFlagDialog();
                }
                paletteFlag = PaletteFlag.EXTERNAL;
                listPalettes.setPalette(externalPalette);
                flushPaint(listPalettes.getCheckedPaletteColor());
                alertDialog.dismiss();
            }
            @Override
            public void onResetClick(int position) {
                final int mPosition = position;
                buildResetPaletteDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (mPosition) {
                            case 0:
                                resetBackgroundPalette();
                                break;
                            case 1:
                                resetGridPalette();
                                break;
                            case 2:
                                resetBuiltinPalette();
                                break;
                        }
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
                        }
                        flushPaint(listPalettes.getCheckedPaletteColor());
                    }
                }, null);
            }
            @Override
            public void onRenameClick(int position) {
                buildRenamePaletteDialog(externalPaletteNames.get(position), externalPaletteNames.get(position));
                alertDialog.dismiss();
            }
            @Override
            public void onDeleteClick(final int position) {
                buildDeleteFileDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FileUtils.forceDelete(new File(getExternalPalettePathname(
                                    externalPaletteNames.get(position))));
                            if (paletteFlag == PaletteFlag.EXTERNAL) {
                                paletteFlag = PaletteFlag.INTERNAL;
                                removeExternalPalette();
                                listPalettes.setPalette(builtinPalette);
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        alertDialog.dismiss();
                        buildPaletteFlagDialog();
                    }
                }, null);
            }
        });
        recyclerView.setAdapter(adapter);
        alertDialog.show();
    }
    // Add palette dialog
    private Palette dialogTempPalette;
    private void buildAddPaletteDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        String [] items = {
                getString(R.string.empty_palette),
                getString(R.string.copy_current_palette),
                getString(R.string.automatic_gradient)
        };
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dialogTempPalette = Palette.createPalette(12);
                        break;
                    case 1:
                        switch (paletteFlag) {
                            case PaletteFlag.BACKGROUND:
                                dialogTempPalette = Palette.createPalette(backgroundPalette, 12,
                                        backgroundPalette.getIndex());
                                break;
                            case PaletteFlag.GRID:
                                dialogTempPalette = Palette.createPalette(gridPalette, 12,
                                        gridPalette.getIndex());
                                break;
                            case PaletteFlag.INTERNAL:
                                dialogTempPalette = Palette.createPalette(builtinPalette);
                                break;
                            case PaletteFlag.EXTERNAL:
                                if (externalPalette == null) {
                                    dialogTempPalette = Palette.createPalette(12);
                                }
                                else {
                                    dialogTempPalette = Palette.createPalette(externalPalette);
                                }
                                break;
                        }
                        break;
                    case 2:
                        int originalColor = listPalettes.getCheckedPaletteColor();
                        float originalValue = ColorFactory.value(originalColor);
                        List<Float> valueList = new ArrayList<>();
                        for (float value = originalValue; value > 0.0f; value -= 0.1f) {
                            valueList.add(value);
                        }
                        valueList.add(0.0f);
                        Collections.reverse(valueList);
                        for (float value = originalValue; value < 1.0f; value += 0.1f) {
                            valueList.add(value);
                        }
                        valueList.add(1.0f);
                        Collections.reverse(valueList);
                        Iterator<Float> iterator = valueList.iterator();
                        List<Integer> colorList = new ArrayList<>();
                        while (iterator.hasNext()) {
                            Float next = iterator.next();
                            int color = ColorFactory.resetValue(originalColor, next);
                            if (!colorList.contains(color)) {
                                colorList.add(color);
                            }
                        }
                        int[] colors = new int[12];
                        for (int i = 0; i < colorList.size(); i ++) {
                            colors[i] = colorList.get(i);
                        }
                        originalColor = ColorFactory.resetValue(originalColor, originalValue);
                        dialogTempPalette = Palette.createPalette(colors,
                                colorList.indexOf(originalColor));
                        break;
                }
                buildSavePaletteDialog(null);
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                buildPaletteFlagDialog();
            }
        });
        builder.create().show();
    }
    // Save palette dialog
    private String dialogTempPaletteName;
    private boolean dialogTempPaletteSameName;
    private void buildSavePaletteDialog (String etText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_save_palette, null);
        final EditText etPaletteName = view.findViewById(R.id.et_palette_name);
        etPaletteName.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i ++) {
                            if (Character.toString(source.charAt(i)).equals("/")) {
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });
        if (etText != null) {
            etPaletteName.setText(etText);
        }
        RecyclerView recyclerPalettes = view.findViewById(R.id.recycler_palettes);
        recyclerPalettes.setLayoutManager(new LinearLayoutManager(this));
        List<String> externalPaletteNames = getExternalPaletteNames();
        TextViewListAdapter adapter = new TextViewListAdapter(this, externalPaletteNames,
                VectorDrawableCompat.create(getResources(), R.drawable.ic_baseline_palette_24, getTheme()));
        adapter.setOnItemClickListener(new TextViewListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                etPaletteName.setText(externalPaletteNames.get(position));
            }
        });
        recyclerPalettes.setAdapter(adapter);
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogTempPaletteName = etPaletteName.getText().toString();
                dialogTempPaletteSameName = false;
                PaletteFactory.encodeFile(dialogTempPalette, getExternalPalettePathname(dialogTempPaletteName),
                        false,
                        new PaletteFactory.Callback() {
                            @Override
                            public void onCreateFile(boolean isSuccess) {}
                            @Override
                            public void onException(Exception e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onFileExists(boolean isDirectory) {
                                if (!isDirectory) {
                                    Utils.hideSoftInputFromView(MainActivity.this, etPaletteName);
                                    buildFileSameNameDialog(new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            PaletteFactory.encodeFile(dialogTempPalette,
                                                    getExternalPalettePathname(dialogTempPaletteName), true);
                                            loadExternalPalette(dialogTempPaletteName);
                                            buildPaletteFlagDialog();
                                        }
                                    }, new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            buildSavePaletteDialog(dialogTempPaletteName);
                                        }
                                    });
                                    dialogTempPaletteSameName = true;
                                }
                            }
                            @Override
                            public void onSuccess() {}
                        });
                if (dialogTempPaletteSameName) {
                    return;
                }
                Utils.hideSoftInputFromView(MainActivity.this, etPaletteName);
                buildPaletteFlagDialog();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogTempPalette = null;
                Utils.hideSoftInputFromView(MainActivity.this, etPaletteName);
                buildAddPaletteDialog();
            }
        });
        builder.create().show();
    }
    // Rename palette dialog
    private void buildRenamePaletteDialog (@NonNull final String oldName, @NonNull String showName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_save_palette, null);
        final EditText etPaletteName = view.findViewById(R.id.et_palette_name);
        etPaletteName.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i ++) {
                            if (Character.toString(source.charAt(i)).equals("/")) {
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });
        etPaletteName.setText(showName);
        RecyclerView recyclerPalettes = view.findViewById(R.id.recycler_palettes);
        recyclerPalettes.setLayoutManager(new LinearLayoutManager(this));
        List<String> externalPaletteNames = getExternalPaletteNames();
        TextViewListAdapter adapter = new TextViewListAdapter(this, externalPaletteNames,
                VectorDrawableCompat.create(getResources(), R.drawable.ic_baseline_palette_24, getTheme()));
        adapter.setOnItemClickListener(new TextViewListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                etPaletteName.setText(externalPaletteNames.get(position));
            }
        });
        recyclerPalettes.setAdapter(adapter);
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogTempPaletteName = etPaletteName.getText().toString();
                if (dialogTempPaletteName.equals(oldName)) {
                    dialog.cancel();
                    return;
                }
                File srcFile = new File(getExternalPalettePathname(oldName));
                File destFile = new File(getExternalPalettePathname(dialogTempPaletteName));
                if (destFile.exists()) {
                    if (destFile.isFile()) {
                        dialog.dismiss();
                        buildFileSameNameDialog(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    FileUtils.forceDelete(destFile);
                                    FileUtils.moveFile(srcFile, destFile);
                                    if (paletteFlag == PaletteFlag.EXTERNAL) {
                                        removeExternalPalette();
                                        loadExternalPalette(dialogTempPaletteName);
                                    }
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                                buildPaletteFlagDialog();
                            }
                        }, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                buildRenamePaletteDialog(oldName, dialogTempPaletteName);
                            }
                        });
                    }
                }
                else {
                    try {
                        FileUtils.moveFile(srcFile, destFile);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Utils.hideSoftInputFromView(MainActivity.this, etPaletteName);
                buildPaletteFlagDialog();
            }
        });
        builder.create().show();
    }
    // Reset palette dialog
    private void buildResetPaletteDialog(DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        builder.setMessage(R.string.warning_reset_palette);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(cancelListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView tvMessage = Utils.getMessageView(dialog);
        if (tvMessage != null) {
            tvMessage.setTextSize(16);
            tvMessage.invalidate();
        }
    }
    // Load dialog
    private void buildLoadDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        String[] items = {
                getString(R.string.load_image),
                getString(R.string.new_image),
                getString(R.string.paste_image)
        };
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        buildLoadImageDialog(false);
                        break;
                    case 1:
                        buildNewImageDialog();
                        break;
                    case 2:
                        buildLoadImageDialog(true);
                        break;
                }
            }
        });
        builder.create().show();
    }
    // Load image dialog
    private RecyclerView dialogTempRecyclerImageList;
    private TextView dialogTempTvCurrentPath;
    private AlertDialog loadImageDialog;
    private boolean dialogTempLoadImage;
    private void buildLoadImageDialog (boolean isPaste) {
        dialogTempLoadImage = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_load_image, null);
        builder.setView(view);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                buildLoadDialog();
            }
        });
        loadImageDialog = builder.create();
        dialogTempRecyclerImageList = view.findViewById(R.id.recycler_images);
        dialogTempRecyclerImageList.setLayoutManager(new LinearLayoutManager(this));
        dialogTempTvCurrentPath = view.findViewById(R.id.tv_current_path);
        dialogTempTvCurrentPath.setText(imagePath);
        dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(imagePath, isPaste));
        ImageView imgBack = view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imagePath.equals("/")) {
                    File imageParentDir = new File(imagePath).getParentFile();
                    if (imageParentDir != null) {
                        if (imageParentDir.canRead() && imageParentDir.canWrite()) {
                            imagePath = imageParentDir.getAbsolutePath();
                        }
                    }
                }
                dialogTempTvCurrentPath.setText(imagePath);
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(imagePath, isPaste));
            }
        });
        loadImageDialog.show();
    }
    // New image dialog
    @SuppressLint("SetTextI18n")
    private void buildNewImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_image_size, null);
        EditText etImageWidth = view.findViewById(R.id.et_image_width);
        EditText etImageHeight = view.findViewById(R.id.et_image_height);
        etImageWidth.setText(Integer.toString(getCurrentBitmap().getWidth()));
        etImageHeight.setText(Integer.toString(getCurrentBitmap().getHeight()));
        etImageWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (Integer.parseInt(s.toString()) > IMAGE_WIDTH_MAX) {
                        etImageWidth.removeTextChangedListener(this);
                        etImageWidth.setText(Integer.toString(IMAGE_WIDTH_MAX));
                        etImageWidth.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });
        etImageHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (Integer.parseInt(s.toString()) > IMAGE_HEIGHT_MAX) {
                        etImageHeight.removeTextChangedListener(this);
                        etImageHeight.setText(Integer.toString(IMAGE_HEIGHT_MAX));
                        etImageHeight.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int width;
                int height;
                if (etImageWidth.getText().toString().isEmpty()) {
                    width = IMAGE_WIDTH_DEFAULT;
                }
                else {
                    width = Integer.parseInt(etImageWidth.getText().toString());
                    if (width < IMAGE_WIDTH_MIN) {
                        width = IMAGE_WIDTH_DEFAULT;
                    }
                }
                if (etImageHeight.getText().toString().isEmpty()) {
                    height = IMAGE_HEIGHT_DEFAULT;
                }
                else {
                    height = Integer.parseInt(etImageHeight.getText().toString());
                    if (height < IMAGE_HEIGHT_MIN) {
                        height = IMAGE_HEIGHT_DEFAULT;
                    }
                }
                imageName = IMAGE_NAME_DEFAULT();
                setBitmap(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
                resetImageTranslation();
                flushImageNameView();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                buildLoadDialog();
            }
        });
        builder.setView(view);
        builder.create().show();
    }
    // Resize image dialog
    @SuppressLint("SetTextI18n")
    private void buildResizeImageDialog(int initialWidth, int initialHeight) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_image_size, null);
        EditText etImageWidth = view.findViewById(R.id.et_image_width);
        EditText etImageHeight = view.findViewById(R.id.et_image_height);
        etImageWidth.setText(Integer.toString(initialWidth));
        etImageHeight.setText(Integer.toString(initialHeight));
        etImageWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (Integer.parseInt(s.toString()) > IMAGE_WIDTH_MAX) {
                        etImageWidth.removeTextChangedListener(this);
                        etImageWidth.setText(Integer.toString(IMAGE_WIDTH_MAX));
                        etImageWidth.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });
        etImageHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (Integer.parseInt(s.toString()) > IMAGE_HEIGHT_MAX) {
                        etImageHeight.removeTextChangedListener(this);
                        etImageHeight.setText(Integer.toString(IMAGE_HEIGHT_MAX));
                        etImageHeight.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int width;
                int height;
                if (etImageWidth.getText().toString().isEmpty()) {
                    width = IMAGE_WIDTH_MIN;
                }
                else {
                    width = Integer.parseInt(etImageWidth.getText().toString());
                    if (width < IMAGE_WIDTH_MIN) {
                        width = IMAGE_WIDTH_MIN;
                    }
                }
                if (etImageHeight.getText().toString().isEmpty()) {
                    height = IMAGE_HEIGHT_MIN;
                }
                else {
                    height = Integer.parseInt(etImageHeight.getText().toString());
                    if (height < IMAGE_HEIGHT_MIN) {
                        height = IMAGE_HEIGHT_MIN;
                    }
                }
                if ((width == getCurrentBitmap().getWidth()) &&
                        (height == getCurrentBitmap().getHeight())) {
                    dialog.dismiss();
                    return;
                }
                if ((width < getCurrentBitmap().getWidth()) ||
                height < getCurrentBitmap().getHeight()) {
                    Utils.hideSoftInputFromView(MainActivity.this, etImageWidth);
                    Utils.hideSoftInputFromView(MainActivity.this, etImageHeight);
                    final int finalHeight = height;
                    final int finalWidth = width;
                    buildResizeImageWarningDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setBitmap(Bitmap.createBitmap(getCurrentBitmap(),
                                    0, 0, finalWidth, finalHeight));
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            buildResizeImageDialog(finalWidth, finalHeight);
                        }
                    });
                }
                else {
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawBitmap(getCurrentBitmap(), 0, 0, bitmapPaint);
                    setBitmap(bitmap);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setView(view);
        builder.create().show();
    }
    // Resize dialog
    private void buildResizeImageWarningDialog(DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        builder.setMessage(R.string.warning_resize_image);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(cancelListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView tvMessage = Utils.getMessageView(dialog);
        if (tvMessage != null) {
            tvMessage.setTextSize(16);
            tvMessage.invalidate();
        }
    }
    // Save dialog
    private String dialogTempImageName;
    private EditText dialogTempEtImageName;
    private void buildSaveDialog (String initialImageName) {
        dialogTempLoadImage = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_save_image, null);
        final TextView tvImageFormat = view.findViewById(R.id.tv_image_format);
        tvImageFormat.setText(".");
        tvImageFormat.append(imageFormat);
        tvImageFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildImageFormatDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        tvImageFormat.setText(".");
                        tvImageFormat.append(imageFormat);
                    }
                });
            }
        });
        dialogTempEtImageName = view.findViewById(R.id.et_image_name);
        dialogTempEtImageName.setText(initialImageName);
        dialogTempEtImageName.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i ++) {
                            if (Character.toString(source.charAt(i)).equals("/")) {
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogTempImageName = dialogTempEtImageName.getText().toString();
                BitmapEncoder.CompressFormat compressFormat;
                switch (imageFormat) {
                    case "jpeg":
                        compressFormat = BitmapEncoder.CompressFormat.JPEG;
                        dialogTempImageName = dialogTempImageName + ".jpeg";
                        break;
                    case "bmp":
                        compressFormat = BitmapEncoder.CompressFormat.BMP;
                        dialogTempImageName = dialogTempImageName + ".bmp";
                        break;
                    default:
                        compressFormat = BitmapEncoder.CompressFormat.PNG;
                        dialogTempImageName = dialogTempImageName + ".png";
                        break;
                }
                Utils.hideSoftInputFromView(MainActivity.this, dialogTempEtImageName);
                BitmapEncoder.encodeFile(getImagePathname(dialogTempImageName),
                        getCurrentBitmap(),
                        false, compressFormat, imageQuality,
                        new BitmapEncoder.Callback() {
                            @Override
                            public void onCreateFailure() {}
                            @Override
                            public void onCompressFailure() {}
                            @Override
                            public void onFileExists(boolean isDirectory) {
                                if (!isDirectory) {
                                    buildFileSameNameDialog(new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            BitmapEncoder.encodeFile(getImagePathname(dialogTempImageName),
                                                    getCurrentBitmap(), true,
                                                    compressFormat, imageQuality,
                                                    new BitmapEncoder.Callback() {
                                                        @Override
                                                        public void onCreateFailure() {}
                                                        @Override
                                                        public void onCompressFailure() {}
                                                        @Override
                                                        public void onFileExists(boolean isDirectory) {}
                                                        @Override
                                                        public void onIOException(IOException e) {}
                                                        @Override
                                                        public void onSuccess() {
                                                            imageName = FilenameUtils.getBaseName(dialogTempImageName);
                                                            flushImageNameView();
                                                            Utils.showLongToast(MainActivity.this,
                                                                    getString(R.string.info_saved_successfully) + "\n" +
                                                                            getImagePathname(dialogTempImageName));
                                                        }
                                                    });
                                        }
                                    }, new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            Utils.hideSoftInputFromView(MainActivity.this, dialogTempEtImageName);
                                            buildSaveDialog(FilenameUtils.getBaseName(dialogTempImageName));
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onIOException(IOException e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onSuccess() {
                                imageName = FilenameUtils.getBaseName(dialogTempImageName);
                                flushImageNameView();
                                Utils.showLongToast(MainActivity.this,
                                        getString(R.string.info_saved_successfully) + "\n" +
                                                getImagePathname(dialogTempImageName));
                            }
                        });
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Utils.hideSoftInputFromView(MainActivity.this, dialogTempEtImageName);
            }
        });
        dialogTempRecyclerImageList = view.findViewById(R.id.recycler_images);
        dialogTempRecyclerImageList.setLayoutManager(new LinearLayoutManager(this));
        dialogTempTvCurrentPath = view.findViewById(R.id.tv_current_path);
        dialogTempTvCurrentPath.setText(imagePath);
        dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(imagePath, true));
        ImageView imgBack = view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imagePath.equals("/")) {
                    File imageParentDir = new File(imagePath).getParentFile();
                    if (imageParentDir != null) {
                        if (imageParentDir.canRead() && imageParentDir.canWrite()) {
                            imagePath = imageParentDir.getAbsolutePath();
                        }
                    }
                }
                dialogTempTvCurrentPath.setText(imagePath);
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(imagePath, true));
            }
        });
        builder.setView(view);
        builder.create().show();
    }
    private int dialogTempImageQuality;
    private void buildImageFormatDialog (DialogInterface.OnCancelListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_image_format, null);
        final TabHost tabHost = view.findViewById(R.id.tabhost_image_format);
        tabHost.setup();
        TabHost.TabSpec png = tabHost.newTabSpec("png");
        png.setIndicator("PNG");
        png.setContent(R.id.ll_empty);
        tabHost.addTab(png);
        TabHost.TabSpec jpeg = tabHost.newTabSpec("jpeg");
        jpeg.setIndicator("JPEG");
        jpeg.setContent(R.id.ll_jpeg_quality);
        tabHost.addTab(jpeg);
        TabHost.TabSpec bmp = tabHost.newTabSpec("bmp");
        bmp.setIndicator("BMP");
        bmp.setContent(R.id.ll_empty);
        tabHost.addTab(bmp);
        if (imageFormat.equals("png")) {
            tabHost.setCurrentTabByTag("png");
        }
        if (imageFormat.equals("jpeg")) {
            tabHost.setCurrentTabByTag("jpeg");
        }
        if (imageFormat.equals("bmp")) {
            tabHost.setCurrentTabByTag("bmp");
        }
        dialogTempImageQuality = imageQuality;
        final TextView tvJpegQuality = view.findViewById(R.id.tv_jpeg_quality);
        SeekBar barJpegQuality = view.findViewById(R.id.bar_jpeg_quality);
        tvJpegQuality.setText(R.string.quality);
        tvJpegQuality.append(": ");
        tvJpegQuality.append(Integer.toString(dialogTempImageQuality));
        barJpegQuality.setProgress(dialogTempImageQuality - 30);
        barJpegQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dialogTempImageQuality = progress + 30;
                tvJpegQuality.setText(R.string.quality);
                tvJpegQuality.append(": ");
                tvJpegQuality.append(Integer.toString(dialogTempImageQuality));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (tabHost.getCurrentTab()) {
                    case 0:
                        imageFormat = "png";
                        break;
                    case 1:
                        imageFormat = "jpeg";
                        break;
                    case 2:
                        imageFormat = "bmp";
                        break;
                }
                imageQuality = dialogTempImageQuality;
                dialog.cancel();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(listener);
        builder.create().show();
    }
    @NonNull
    private FileListAdapter flushImageListAdapter (String newPath, boolean isPaste) {
        File[] dirs = new File(newPath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        Collection<File> files = FileUtils.listFiles(new File(newPath),
                new String[] {"png", "jpg", "jpeg", "bmp"}, false);
        List<String> dirNames = new ArrayList<>();
        if (dirs != null) {
            for (File dir : dirs) {
                dirNames.add(dir.getName());
            }
        }
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        FileListAdapter adapter = new FileListAdapter(MainActivity.this, dirNames, fileNames,
                VectorDrawableCompat.create(getResources(), R.drawable.ic_baseline_image_24, getTheme()));
        adapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
            @Override
            public void onDirectoryClick(String name, int position) {
                imagePath = imagePath + "/" + name;
                dialogTempTvCurrentPath.setText(imagePath);
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(imagePath, isPaste));
            }
            @Override
            public void onFileClick(String name, int position) {
                if (dialogTempLoadImage) {
                    String pathname = getImagePathname(name);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    Bitmap bounds = BitmapFactory.decodeFile(pathname, options);
                    if ((options.outWidth <= IMAGE_WIDTH_MAX) &&
                    options.outHeight <= IMAGE_HEIGHT_MAX) {
                        BitmapUtils.recycleBitmap(bounds);
                        Bitmap bitmap = BitmapDecoder.decodeFile(pathname);
                        if (bitmap != null) {
                            if (isPaste) {
                                groupTools.checkIndex(4);
                                flushPasteBitmap(bitmap);
                                buildSelectionPopup2();
                                canvasView.invalidate();
                            }
                            else {
                                imageName = FilenameUtils.getBaseName(name);
                                flushImageNameView();
                                setBitmap(bitmap);
                                resetImageTranslation();
                            }
                            if (loadImageDialog != null) {
                                loadImageDialog.dismiss();
                            }
                        }
                    }
                    else {
                        Utils.showLongToast(MainActivity.this, R.string.error_image_too_large);
                    }
                }
                else {
                    dialogTempEtImageName.setText(FilenameUtils.getBaseName(name));
                }
            }
        });
        return adapter;
    }
    // Delete file dialog
    private void buildDeleteFileDialog(DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        builder.setMessage(R.string.warning_delete_file);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(cancelListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView tvMessage = Utils.getMessageView(dialog);
        if (tvMessage != null) {
            tvMessage.setTextSize(16);
            tvMessage.invalidate();
        }
    }
    // File same name dialog
    private void buildFileSameNameDialog(DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        builder.setMessage(R.string.warning_same_name_file);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(cancelListener);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView tvMessage = Utils.getMessageView(dialog);
        if (tvMessage != null) {
            tvMessage.setTextSize(16);
            tvMessage.invalidate();
        }
    }
    // Image name dialog
    private void buildImageNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        builder.setMessage(tvImageName.getText());
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView tvMessage = Utils.getMessageView(dialog);
        if (tvMessage != null) {
            tvMessage.setTextSize(16);
            tvMessage.invalidate();
        }
    }
    @SuppressLint("SetTextI18n")
    private void buildGridDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        View view = View.inflate(this, R.layout.dialog_grid, null);
        CheckBox boxShowGrid = view.findViewById(R.id.box_show_grid);
        boxShowGrid.setChecked(gridVisible);
        EditText etGridWidth = view.findViewById(R.id.et_grid_width);
        EditText etGridHeight = view.findViewById(R.id.et_grid_height);
        etGridWidth.setText(Integer.toString(gridWidth));
        etGridHeight.setText(Integer.toString(gridHeight));
        etGridWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (Integer.parseInt(s.toString()) > IMAGE_WIDTH_MAX) {
                        etGridWidth.removeTextChangedListener(this);
                        etGridWidth.setText(Integer.toString(IMAGE_WIDTH_MAX));
                        etGridWidth.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });
        etGridHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    if (Integer.parseInt(s.toString()) > IMAGE_HEIGHT_MAX) {
                        etGridHeight.removeTextChangedListener(this);
                        etGridHeight.setText(Integer.toString(IMAGE_HEIGHT_MAX));
                        etGridHeight.addTextChangedListener(this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int width;
                int height;
                if (etGridWidth.getText().toString().isEmpty()) {
                    width = GRID_WIDTH_DEFAULT;
                }
                else {
                    width = Integer.parseInt(etGridWidth.getText().toString());
                    if (width < IMAGE_WIDTH_MIN) {
                        width = GRID_WIDTH_DEFAULT;
                    }
                }
                if (etGridHeight.getText().toString().isEmpty()) {
                    height = GRID_HEIGHT_DEFAULT;
                }
                else {
                    height = Integer.parseInt(etGridHeight.getText().toString());
                    if (height < IMAGE_HEIGHT_MIN) {
                        height = GRID_HEIGHT_DEFAULT;
                    }
                }
                setGridSize(width, height);
                setGridVisible(boxShowGrid.isChecked());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.setView(view).create().show();
    }
    // Permission dialog
    private void buildPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppDialogTheme);
        builder.setMessage(R.string.warning_permission_denied);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView tvMessage = Utils.getMessageView(dialog);
        if (tvMessage != null) {
            tvMessage.setTextSize(16);
            tvMessage.invalidate();
        }
    }

    // ON CLICK
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.tv_image_name:
                buildImageNameDialog();
                break;
            case R.id.tv_image_size:
                buildResizeImageDialog(getCurrentBitmap().getWidth(), getCurrentBitmap().getHeight());
                break;
            case R.id.tv_image_scale:
                buildImageScaleDialog();
                break;
            case R.id.tv_pointer_coords:
                buildOriginFlagDialog();
                break;
            case R.id.img_recenter:
                resetImageTranslation();
                break;
            case R.id.img_grid:
                buildGridDialog();
                break;
            case R.id.img_undo:
                toolBufferPool.undo();
                canvasView.invalidate();
                break;
            case R.id.img_redo:
                toolBufferPool.redo();
                canvasView.invalidate();
                break;
            case R.id.img_menu:
                buildMenuPopup();
                break;
            case R.id.tv_paint_width:
                buildPaintWidthDialog();
                break;
            case R.id.img_palette:
                buildPaletteFlagDialog();
                break;
        }
    }

    // PERMISSION CALLBACK
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        dataSaved = true;
        if (requestCode == 1) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                buildPermissionDialog();
            }
            else {
                recreate();
            }
        }
    }

    // SAVE & LOAD DATA
    // On pause
    @Override
    protected void onPause() {
        if (isFinishing() && (!dataSaved)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    saveData();
                    dataSaved = true;
                }
            });
            thread.start();
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                super.onPause();
            }
        }
        else {
            super.onPause();
        }
    }
    // On save instance state
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (!dataSaved) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    saveData();
                    dataSaved = true;
                }
            });
            thread.start();
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                super.onSaveInstanceState(outState);
            }
        }
        else {
            super.onSaveInstanceState(outState);
        }
    }

    // On restore instance state
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (dataSaved && uiLoaded) {
            loadData();
            dataSaved = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataSaved && uiLoaded) {
            loadData();
            dataSaved = false;
        }
    }

    // On create
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        dataSaved = false;
        uiLoaded = false;

        // Get permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
                return;
            }
        }

        CrashHandler.getInstance().init(this);

        // Set content view
        setContentView(R.layout.activity_main);

        // Set window format
        getWindow().setFormat(PixelFormat.RGBA_8888);

        // Get widgets & set listeners
        // TopBar
        tvImageName = findViewById(R.id.tv_image_name);
        imgUndo = findViewById(R.id.img_undo);
        imgRedo = findViewById(R.id.img_redo);
        imgMenu = findViewById(R.id.img_menu);
        imgRecenter = findViewById(R.id.img_recenter);
        imgGrid = findViewById(R.id.img_grid);
        tvImageSize = findViewById(R.id.tv_image_size);
        tvImageScale = findViewById(R.id.tv_image_scale);
        tvPointerCoords = findViewById(R.id.tv_pointer_coords);
        flushPointerCoordsView(0, 0);
        tvSelectionSize = findViewById(R.id.tv_selection_size);
        flushSelectionSizeView(1, 1);
        tvImageName.setOnClickListener(this);
        imgUndo.setOnClickListener(this);
        imgRedo.setOnClickListener(this);
        imgMenu.setOnClickListener(this);
        imgRecenter.setOnClickListener(this);
        imgGrid.setOnClickListener(this);
        tvImageSize.setOnClickListener(this);
        tvImageScale.setOnClickListener(this);
        tvPointerCoords.setOnClickListener(this);
        // ToolBar
        tvPaintWidth = findViewById(R.id.tv_paint_width);
        groupTools = findViewById(R.id.group_tools);
        imgPaint = findViewById(R.id.img_paint);
        imgShape = findViewById(R.id.img_shape);
        imgEraser = findViewById(R.id.img_eraser);
        imgFill = findViewById(R.id.img_fill);
        imgSelection = findViewById(R.id.img_selection);
        imgColorize = findViewById(R.id.img_colorize);
        tvPaintWidth.setOnClickListener(this);
        // PaletteBar
        imgPalette = findViewById(R.id.img_palette);
        imgPalette.setOnClickListener(this);
        listPalettes = findViewById(R.id.list_palettes);
        // CanvasView
        canvasView = findViewById(R.id.canvas_view);

        uiLoaded = true;

        // Load data
        preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        loadData();

        canvasView.setOnInvalidateListener(new CanvasView.OnInvalidateListener() {

            @Override
            public void onInvalidate(Canvas canvas) {
                // Clear canvas
                canvas.drawPaint(eraser);
                canvas.save();
                canvas.restore();
                // Set matrix
                matrix.setTranslate(
                        (float)(getImageTranslationX()) / imageScale,
                        (float)(getImageTranslationY()) / imageScale);
                matrix.postScale(imageScale, imageScale);
                // Draw background
                canvas.drawColor(getCanvasViewBackgroundColor());
                canvasBackgroundMatrix.reset();
                canvasBackgroundMatrix.setTranslate(getImageTranslationX(), getImageTranslationY());
                canvas.setMatrix(canvasBackgroundMatrix);
                canvas.drawRect(0, 0,
                        getCurrentBitmap().getWidth() * imageScale,
                        getCurrentBitmap().getHeight() * imageScale,
                        canvasBackgroundPaint);
                canvas.setMatrix(null);
                canvas.save();
                canvas.restore();
                // Draw scaled bitmap
                canvas.drawBitmap(getCurrentBitmap(), matrix, bitmapPaint);
                canvas.save();
                canvas.restore();
                // Draw grid
                if(gridVisible) {
                    if(imageScale >= 4) {
                        int width = getCurrentBitmap().getWidth();
                        int height = getCurrentBitmap().getHeight();
                        for (int i = 0; i <= width; i += gridWidth) {
                            canvas.drawLine(
                                    getImageTranslationX() + i * imageScale,
                                    getImageTranslationY(),
                                    getImageTranslationX() + i * imageScale,
                                    getImageTranslationY() + height * imageScale, gridPaint);
                        }
                        for (int i = 0; i <= height; i += gridHeight) {
                            canvas.drawLine(
                                    getImageTranslationX(),
                                    getImageTranslationY() + i * imageScale,
                                    getImageTranslationX() + width * imageScale,
                                    getImageTranslationY() + i * imageScale, gridPaint);
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
                            selectionLeft = getImageTranslationX() +
                                    selectionBitmapDstX * imageScale + selectionPaint1.getStrokeWidth() / 2;
                            selectionTop = getImageTranslationY() +
                                    selectionBitmapDstY * imageScale + selectionPaint1.getStrokeWidth() / 2;
                            selectionRight = selectionLeft + (selectionBitmapDstWidth - 1) * imageScale
                                    + selectionPaint1.getStrokeWidth();
                            selectionBottom = selectionTop + (selectionBitmapDstHeight - 1) * imageScale
                                    + selectionPaint1.getStrokeWidth();
                            break;
                        default:
                            selectionLeft = getImageTranslationX() +
                                    Math.min(downX, moveX) * imageScale + selectionPaint1.getStrokeWidth() / 2;
                            selectionTop = getImageTranslationY() +
                                    Math.min(downY, moveY) * imageScale + selectionPaint1.getStrokeWidth() / 2;
                            selectionRight = getImageTranslationX() +
                                    Math.max(downX, moveX) * imageScale + selectionPaint1.getStrokeWidth() / 2 * 3;
                            selectionBottom = getImageTranslationY() +
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
                        downX = (int) Math.floor((event.getX(0) - getImageTranslationX()) / imageScale);
                        downY = (int) Math.floor((event.getY(0) - getImageTranslationY()) / imageScale);
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
                                        selectionBitmapDstX = downX - selectionBitmapDstWidth / 2;
                                        selectionBitmapDstY = downY - selectionBitmapDstHeight / 2;
                                        toolBufferPool.clearTempToolBuffers();
                                        toolBufferPool.addTempToolBuffer(
                                                getBitmapBuffer());
                                        selected = true;
                                        break;
                                    case ToolFlag.SelectionFlag.COPY:
                                        selectionBitmapDstX = downX - selectionBitmapDstWidth / 2;
                                        selectionBitmapDstY = downY - selectionBitmapDstHeight / 2;
                                        toolBufferPool.clearTempToolBuffers();
                                        toolBufferPool.addTempToolBuffer(
                                                getSelectionBuffer());
                                        selected = true;
                                        break;
                                    default:
                                        if (downX < 0) {
                                            downX = 0;
                                        }
                                        if (downX >= getCurrentBitmap().getWidth()) {
                                            downX = getCurrentBitmap().getWidth() - 1;
                                        }
                                        if (downY < 0) {
                                            downY = 0;
                                        }
                                        if (downY >= getCurrentBitmap().getHeight()) {
                                            downY = getCurrentBitmap().getHeight() - 1;
                                        }
                                        break;
                                }
                                break;
                        }
                        canvasView.invalidate();
                        flushSelectionSizeView(Math.abs(moveX - downX) + 1, Math.abs(moveY - downY) + 1);
                        flushPointerCoordsView(downX - getOriginX(), downY - getOriginY());
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // Record initial distance
                        oldDist = Utils.spacing(event);
                        newDist = oldDist;
                        scaleMode = true;
                        readOnlyMode = true;
                        selectionFlag = ToolFlag.SelectionFlag.NONE;
                        selected = false;
                        toolBufferPool.clearTempToolBuffers();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (scaleMode) {
                            newDist = Utils.spacing(event);
                            if(newDist != 0) {
                                if (newDist >= oldDist + ApplicationUtils.getResources().getDimension(R.dimen.DP_VALUE_1) * 64) {
                                    setImageScale(imageScale * 2);
                                    oldDist = newDist;
                                }
                                if (newDist <= oldDist - ApplicationUtils.getResources().getDimension(R.dimen.DP_VALUE_1) * 64) {
                                    setImageScale(imageScale / 2);
                                    oldDist = newDist;
                                }
                            }
                            imageToCenterX += event.getX(0) - x;
                            imageToCenterY += event.getY(0) - y;
                            x = event.getX(0);
                            y = event.getY(0);
                        }
                        else if (!readOnlyMode) {
                            // Current path
                            moveX = (int) Math.floor((event.getX(0) - getImageTranslationX()) / imageScale);
                            moveY = (int) Math.floor((event.getY(0) - getImageTranslationY()) / imageScale);
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
                                        default:
                                            if (moveX < 0) {
                                                moveX = 0;
                                            }
                                            if (moveX >= getCurrentBitmap().getWidth()) {
                                                moveX = getCurrentBitmap().getWidth() - 1;
                                            }
                                            if (moveY < 0) {
                                                moveY = 0;
                                            }
                                            if (moveY >= getCurrentBitmap().getHeight()) {
                                                moveY = getCurrentBitmap().getHeight() - 1;
                                            }
                                            break;
                                    }
                                    selected = true;
                                    break;
                            }
                            switch (toolFlag) {
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
                                case ToolFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case ToolFlag.SelectionFlag.CUT:
                                            selectionBitmapDstX = moveX - selectionBitmapDstWidth / 2;
                                            selectionBitmapDstY = moveY - selectionBitmapDstHeight / 2;
                                            toolBufferPool.clearTempToolBuffers();
                                            toolBufferPool.addTempToolBuffer(
                                                    getBitmapBuffer());
                                            break;
                                        case ToolFlag.SelectionFlag.COPY:
                                            selectionBitmapDstX = moveX - selectionBitmapDstWidth / 2;
                                            selectionBitmapDstY = moveY - selectionBitmapDstHeight / 2;
                                            toolBufferPool.clearTempToolBuffers();
                                            toolBufferPool.addTempToolBuffer(
                                                    getSelectionBuffer());
                                            break;
                                    }
                                    break;
                            }
                        }
                        canvasView.invalidate();
                        flushSelectionSizeView(Math.abs(moveX - downX) + 1, Math.abs(moveY - downY) + 1);
                        flushPointerCoordsView(moveX - getOriginX(), moveY - getOriginY());
                        break;
                    case MotionEvent.ACTION_UP:
                        upX = (int) Math.floor((event.getX(0) - getImageTranslationX()) / imageScale);
                        upY = (int) Math.floor((event.getY(0) - getImageTranslationY()) / imageScale);
                        if (readOnlyMode) {
                            readOnlyMode = false;
                        }
                        else {
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
                                            downX < getCurrentBitmap().getWidth() &&
                                            downY < getCurrentBitmap().getHeight()) {
                                        toolBufferPool.addToolBuffer(new FillBuffer(downX, downY, paint.getColor()));
                                    }
                                    break;
                                case ToolFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case ToolFlag.SelectionFlag.CUT:
                                        case ToolFlag.SelectionFlag.COPY:
                                            buildSelectionPopup2();
                                            break;
                                        default:
                                            if (downX >= 0 && downY >= 0 &&
                                                    downX < getCurrentBitmap().getWidth() &&
                                                    downY < getCurrentBitmap().getHeight() &&
                                                    moveX < getCurrentBitmap().getWidth() &&
                                                    moveY < getCurrentBitmap().getHeight()) {
                                                buildSelectionPopup1();
                                            }
                                            break;
                                    }
                                    break;
                                case ToolFlag.COLORIZE:
                                    if (upX >=0 && upY >= 0 &&
                                            upX < getCurrentBitmap().getWidth() &&
                                            upY < getCurrentBitmap().getHeight()) {
                                        listPalettes.setCheckedPaletteColor(getCurrentBitmap().getPixel(upX, upY));
                                        flushColors(listPalettes.getCheckedPaletteColor());
                                        groupTools.checkIndex(0);
                                    }
                                    break;
                            }
                            if (toolFlag != ToolFlag.SELECTION) {
                                flushSelectionSizeView(Math.abs(moveX - downX) + 1, Math.abs(moveY - downY) + 1);
                                flushPointerCoordsView(upX - getOriginX(), upY - getOriginY());
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

        // Set widget hints
        // Set paint width text
        tvPaintWidth.setText(Integer.toString(paintWidth));
        // Set shape ImageButton image
        switch (shapeFlag) {
            case ToolFlag.ShapeFlag.LINE:
                imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_line_24, getTheme()));
                break;
            case ToolFlag.ShapeFlag.CIRCLE:
                imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_circle_24, getTheme()));
                break;
            case ToolFlag.ShapeFlag.ELLIPSE:
                imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_ellipse_24, getTheme()));
                break;
            case ToolFlag.ShapeFlag.SQUARE:
                imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_square_24, getTheme()));
                break;
            case ToolFlag.ShapeFlag.RECTANGLE:
                imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_rectangle_24, getTheme()));
                break;
        }
        // Tool group
        // Select tool
        groupTools.setOnCheckedChangeListener(new CheckedImageGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(CheckedImageGroup group, int checkedId, int checkedIndex) {
                for (int i = 0; i < group.getChildCount(); i ++) {
                    ((CheckedImageView)group.getChildAt(i)).setColorFilter(null);
                }
                switch (checkedId) {
                    case R.id.img_paint:
                        toolFlag = ToolFlag.PAINT;
                        setStrokeCap(Paint.Cap.SQUARE);
                        setStrokeJoin(Paint.Join.ROUND);
                        imgPaint.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_shape:
                        toolFlag = ToolFlag.SHAPE;
                        switch (shapeFlag) {
                            case ToolFlag.ShapeFlag.CIRCLE:
                            case ToolFlag.ShapeFlag.ELLIPSE:
                                setStrokeCap(Paint.Cap.ROUND);
                                setStrokeJoin(Paint.Join.ROUND);
                                break;
                            case ToolFlag.ShapeFlag.LINE:
                            case ToolFlag.ShapeFlag.SQUARE:
                            case ToolFlag.ShapeFlag.RECTANGLE:
                                setStrokeCap(Paint.Cap.SQUARE);
                                setStrokeJoin(Paint.Join.MITER);
                                break;
                        }
                        imgShape.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_eraser:
                        toolFlag = ToolFlag.ERASER;
                        setStrokeCap(Paint.Cap.SQUARE);
                        setStrokeJoin(Paint.Join.ROUND);
                        imgEraser.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_fill:
                        toolFlag = ToolFlag.FILL;
                        imgFill.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_selection:
                        toolFlag = ToolFlag.SELECTION;
                        imgSelection.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_colorize:
                        toolFlag = ToolFlag.COLORIZE;
                        imgColorize.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                }
                if (toolFlag != ToolFlag.SELECTION) {
                    selected = false;
                    selectionFlag = ToolFlag.SelectionFlag.NONE;
                }
                canvasView.invalidate();
            }
        });
        // Initial check
        switch (toolFlag) {
            case ToolFlag.PAINT:
                groupTools.check(R.id.img_paint);
                imgPaint.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolFlag.SHAPE:
                groupTools.check(R.id.img_shape);
                imgShape.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolFlag.ERASER:
                groupTools.check(R.id.img_eraser);
                imgEraser.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolFlag.FILL:
                groupTools.check(R.id.img_fill);
                imgFill.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolFlag.SELECTION:
                groupTools.check(R.id.img_selection);
                imgSelection.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolFlag.COLORIZE:
                groupTools.check(R.id.img_colorize);
                imgColorize.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
        }
        // Double tap
        groupTools.setOnDoubleTapListener(new CheckedImageGroup.OnDoubleTapListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onDoubleTap(CheckedImageGroup group, int checkedId, int checkedIndex) {
                switch (checkedId) {
                    case R.id.img_paint:
                        buildPaintFlagDialog();
                        break;
                    case R.id.img_shape:
                        buildShapeFlagDialog();
                        break;
                }
            }
        });
        // Palette list
        // Set palette background color
        listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(), getCanvasBackgroundColor2());
        // Select palette
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
               loadExternalPalette(externalPaletteName);
                if (externalPalette == null) {
                    paletteFlag = PaletteFlag.INTERNAL;
                    listPalettes.setPalette(builtinPalette);
                }
                else {
                    listPalettes.setPalette(externalPalette);
                }
                break;
        }
        flushPaint(listPalettes.getCheckedPaletteColor());
        // Flush paint
        listPalettes.setOnCheckedChangeListener(new PaletteList.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(PaletteList list, int checkedIndex) {
                flushPaint(list.getPaletteColor(checkedIndex));
            }
        });
        // Double tap
        listPalettes.setOnDoubleTapListener(new PaletteList.OnDoubleTapListener() {
            @Override
            public void onDoubleTap(PaletteList list, int checkedIndex) {
                buildColorPickerDialog();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void flushImageSizeView() {
        tvImageSize.setText(getCurrentBitmap().getWidth() + "x" +
                getCurrentBitmap().getHeight());
    }

    @SuppressLint("SetTextI18n")
    private void flushImageScaleView() {
        tvImageScale.setText((imageScale * 100) + "%");
    }

    @SuppressLint("SetTextI18n")
    private void flushPointerCoordsView(float x, float y) {
        tvPointerCoords.setText(x + "," + y);
    }

    @SuppressLint("SetTextI18n")
    private void flushSelectionSizeView(int width, int height) {
        tvSelectionSize.setText(width + "x" + height);
    }

    private void flushImageNameView() {
        tvImageName.setText(imageName);
    }

    private int getImageTranslationX() {
        return canvasView.getWidth() / 2 + imageToCenterX;
    }

    private int getImageTranslationY() {
        return canvasView.getHeight() / 2 + imageToCenterY;
    }

    private void resetImageTranslation() {
        imageToCenterX = IMAGE_TO_CENTER_X_DEFAULT();
        imageToCenterY = IMAGE_TO_CENTER_Y_DEFAULT();
        canvasView.invalidate();
    }

    private float getOriginX() {
        switch (originFlagHorizontal) {
            case OriginFlag.LEFT:
                return 0;
            case OriginFlag.CENTER:
                return getCurrentBitmap().getWidth() * 0.5f - 1;
            case OriginFlag.RIGHT:
                return getCurrentBitmap().getWidth() - 1;
        }
        return 0;
    }

    private float getOriginY() {
        switch (originFlagVertical) {
            case OriginFlag.TOP:
                return 0;
            case OriginFlag.CENTER:
                return getCurrentBitmap().getHeight() * 0.5f - 1;
            case OriginFlag.BOTTOM:
                return getCurrentBitmap().getHeight() - 1;
        }
        return 0;
    }

}
