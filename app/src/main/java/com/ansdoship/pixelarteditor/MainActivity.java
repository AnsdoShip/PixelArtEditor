package com.ansdoship.pixelarteditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ansdoship.pixelarteditor.editor.ColorPalette;
import com.ansdoship.pixelarteditor.editor.buffers.ToolBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.PaintBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.SelectionBuffer;
import com.ansdoship.pixelarteditor.view.CanvasView;
import com.ansdoship.pixelarteditor.view.CheckedImageView;
import com.ansdoship.pixelarteditor.viewgroup.CheckedImageGroup;
import com.ansdoship.pixelarteditor.viewgroup.PaletteList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // SharedPreferences
    private SharedPreferences prefData;
    private SharedPreferences.Editor prefEditor;
    private boolean dataSaved;
    private void saveData() {

        // Clear old data
        prefEditor.clear();

        // Write new data

        // Image
        prefEditor.putString("image_name", tvImageName.getText().toString());
        prefEditor.putInt("image_scale", imageScale);
        prefEditor.putFloat("image_translation_x", imageTranslationX);
        prefEditor.putFloat("image_translation_y", imageTranslationY);

        // Flags
        prefEditor.putInt("draw_flag", drawFlag);
        prefEditor.putInt("graph_flag", graphFlag);
        prefEditor.putInt("paint_flag", paintFlag);

        // Paint
        prefEditor.putInt("paint_width", paintWidth);

        // Grid
        prefEditor.putBoolean("draw_grid", drawGrid);
        prefEditor.putInt("grid_width", gridWidth);
        prefEditor.putInt("grid_height", gridHeight);

        // File save & load path
        prefEditor.putString("IMAGE_PATH", IMAGE_PATH);

        // Palette
        prefEditor.putInt("canvas_view_background_color", backgroundPalette.get(0));
        prefEditor.putInt("canvas_background_color_1", backgroundPalette.get(1));
        prefEditor.putInt("canvas_background_color_2", backgroundPalette.get(2));
        prefEditor.putInt("grid_color", gridPalette.get(0));
        for (int i = 1; i <= builtinPalette.size(); i ++) {
            prefEditor.putInt("builtin_palette_color_" + i, builtinPalette.get(i - 1));
        }
        prefEditor.putInt("palette_id", paletteId);
        if (paletteId == EXTERNAL_PALETTE) {
            prefEditor.putString("palette_name", paletteName);
            externalPalette.saveToFile(PALETTE_PATH + "/" + paletteName, true);
        }
        prefEditor.putInt("palette_checked_index", listPalettes.getCheckedIndex());

        //Apply data
        prefEditor.apply();

        // Bitmap cache
        BitmapUtils.saveBitmapToBMP(CACHE_PATH + "/current_bitmap", currentBitmap, true);

    }
    private void loadData() {

        // Image
        tvImageName.setText(prefData.getString("image_name", "Untitled.png"));
        imageScale = prefData.getInt("image_scale", 20);
        imageTranslationX = prefData.getFloat("image_translation_x", 0);
        imageTranslationY = prefData.getFloat("image_translation_y", 0);

        // Flags
        drawFlag = prefData.getInt("draw_flag", ToolBuffer.DrawFlag.PAINT);
        graphFlag = prefData.getInt("graph_flag", ShapeBuffer.GraphFlag.LINE);
        paintFlag = prefData.getInt("paint_flag", PaintBuffer.PaintFlag.REPLACE);
        selectionFlag = -1;

        // Paint
        paintWidth = prefData.getInt("paint_width", 1);

        // Grid
        drawGrid = prefData.getBoolean("draw_grid", true);
        gridWidth = prefData.getInt("grid_width", 1);
        gridHeight = prefData.getInt("grid_height", 1);

        // Palette

        paletteId = prefData.getInt("palette_id", BUILTIN_PALETTE);
        switch (paletteId) {
            case BACKGROUND_PALETTE:
                paletteName = getString(R.string.background_palette);
                listPalettes.setColorPalette(backgroundPalette, Math.min(prefData.getInt("palette_checked_index", 0), 2));
                break;
            case GRID_PALETTE:
                paletteName = getString(R.string.grid_palette);
                listPalettes.setColorPalette(gridPalette);
                break;
            case BUILTIN_PALETTE:
                paletteName = getString(R.string.builtin_palette);
                listPalettes.setColorPalette(builtinPalette, prefData.getInt("palette_checked_index", 0));
                break;
            case EXTERNAL_PALETTE:
                paletteName = prefData.getString("palette_name", null);
                externalPalette = ColorPalette.decodeFile(PALETTE_PATH + "/" + paletteName + ".palette");
                if (externalPalette == null) {
                    paletteId = BUILTIN_PALETTE;
                    listPalettes.setColorPalette(builtinPalette, prefData.getInt("palette_checked_index", 0));
                }
                else {
                    listPalettes.setColorPalette(externalPalette, prefData.getInt("palette_checked_index", 0));
                }
                break;
        }

        // Load bitmap
        currentBitmap = BitmapUtils.loadBitmapFromFile(CACHE_PATH + "/current_bitmap.bmp");

        if(currentBitmap == null) {
            currentBitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        }
        currentCanvas = new Canvas(currentBitmap);

        // Scale mode
        scaleMode = false;

    }

    private void setImageScale(int scale) {
        if(scale >= 1 && scale <= 64) {
            Settings.getInstance().putImageScale(scale);
            selectionPaint1.setStrokeWidth(scale * 0.5f + 0.5f);
            selectionPaint2.setStrokeWidth(scale * 0.25f + 0.25f);
            flushBackgroundBitmap();
            flushGridBitmap();
        }
    }

    private int getBackgroundImageScale() {
        int scale = 0;
        float imageScale = Settings.getInstance().getImageScale();
        if(imageScale <= 4) {
            scale = 16;
        }
        if (imageScale > 4 && imageScale <= 8) {
            scale = 8;
        }
        if (imageScale > 8 && imageScale <= 16) {
            scale = 4;
        }
        if (imageScale > 16 && imageScale <= 32) {
            scale = 2;
        }
        if (imageScale > 32) {
            scale = 1;
        }
        return scale;
    }

    private void setPaintWidth(int width) {
        Settings.getInstance().putPaintWidth(width);
        paint.setStrokeWidth(width);
        eraser.setStrokeWidth(width);
    }

    private int dialogTempPaintWidth;

    private void setPaintFlag (int paintFlag) {
        if (paint == null) {
            return;
        }
        Settings.getInstance().putPaintFlag(paintFlag);
        switch (paintFlag) {
            case PaintBuffer.PaintFlag.REPLACE:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                break;
            case PaintBuffer.PaintFlag.OVERRIDE:
                paint.setXfermode(null);
                break;
        }
    }

    // Selection
    private boolean selected;
    private int selectionBitmapX;
    private int selectionBitmapY;
    private RectF selectionRectF;

    private void setGridVisibility(boolean gridVisibility) {
        Settings.getInstance().putGridVisibility(gridVisibility);
        canvasView.invalidate();
    }

    private void setGridWidth(int gridWidth) {
        Settings.getInstance().putGridWidth(gridWidth);
        flushGridBitmap();
    }

    private void setGridHeight(int gridHeight) {
        Settings.getInstance().putGridHeight(gridHeight);
        flushGridBitmap();
    }

    private void setGridColor(int gridColor) {
        gridPalette.set(0, gridColor);
        flushGridBitmap();
        canvasView.invalidate();
    }

    private int getGridColor() {
        return gridPalette.get(0);
    }

    private void flushGridBitmap() {
        int width = currentBitmap.getWidth();
        int height = currentBitmap.getHeight();
        int imageScale = Settings.getInstance().getImageScale();
        recycleGridBitmap();
        gridBitmap = Bitmap.createBitmap(Settings.getInstance().getGridWidth() * imageScale, imageScale,
                Bitmap.Config.ARGB_8888);
        gridCanvas = new Canvas(gridBitmap);
        gridPaint = new Paint();
        gridPaint.setColor(getGridColor());
        gridPaint.setStrokeWidth(1);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridCanvas.drawRect(0, 0, gridBitmap.getWidth(), gridBitmap.getHeight(), gridPaint);
        BitmapShader gridShader = new BitmapShader(gridBitmap, BitmapShader.TileMode.REPEAT,
                BitmapShader.TileMode.REPEAT);
        gridPaint.setShader(gridShader);
        recycleGridBitmap();
        gridBitmap = Bitmap.createBitmap(width * imageScale, height * imageScale,
                Bitmap.Config.ARGB_8888);
        gridCanvas = new Canvas(gridBitmap);
        gridCanvas.drawPaint(gridPaint);
    }

    private void recycleGridBitmap() {
        if (gridBitmap == null) {
            return;
        }
        if (!gridBitmap.isRecycled()) {
            gridBitmap.recycle();
            gridBitmap = null;
        }
    }

    private ColorPalette tempPalette;

    private String dialogTempPaletteName;
    private int dialogTempColor;
    private int dialogTempColorH;
    private float dialogTempColorS;
    private float dialogTempColorV;

    // TouchEvent
    private int downX;
    private int downY;
    private int moveX;
    private int moveY;
    private int upX;
    private int upY;
    private boolean scaleMode;
    private boolean readOnlyMode;

    // Bitmaps & canvas

    // Background
    private Bitmap backgroundBitmap;
    private Canvas backgroundCanvas;
    private void setCanvasViewBackgroundColor (int backgroundColor) {
        backgroundPalette.set(0, backgroundColor);
        canvasView.invalidate();
    }
    private int getCanvasViewBackgroundColor () {
        return backgroundPalette.get(0);
    }
    private int getCanvasBackgroundColor1() {
        return backgroundPalette.get(1);
    }
    private int getCanvasBackgroundColor2() {
        return backgroundPalette.get(2);
    }
    private void flushBackgroundBitmap() {
        backgroundBitmap = Bitmap.createBitmap(new int[] {getCanvasBackgroundColor1(),
                        getCanvasBackgroundColor2(),
                        getCanvasBackgroundColor2(),
                        getCanvasBackgroundColor1()},
                2, 2, Bitmap.Config.ARGB_8888);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, imageScale * getBackgroundImageScale(),
                imageScale * getBackgroundImageScale(), false);
        BitmapShader bgShader = new BitmapShader(backgroundBitmap, BitmapShader.TileMode.REPEAT,
                BitmapShader.TileMode.REPEAT);
        backgroundPaint = new Paint();
        backgroundPaint.setShader(bgShader);
        backgroundBitmap = Bitmap.createBitmap(currentBitmap.getWidth() * imageScale,
                currentBitmap.getHeight() * imageScale, Bitmap.Config.ARGB_8888);
        backgroundCanvas = new Canvas(backgroundBitmap);
        backgroundCanvas.drawPaint(backgroundPaint);
    }

    // Current
    private Bitmap currentBitmap;
    private Canvas currentCanvas;

    // Cache
    private Bitmap cacheBitmap;

    // Selection
    private Bitmap selectionBitmap;

    // Grid
    private Bitmap gridBitmap;
    private Canvas gridCanvas;

    // Paints
    private Paint backgroundPaint;
    private Paint gridPaint;
    private Paint bitmapPaint;
    private Paint paint;
    private Paint eraser;
    private Paint selectionPaint1;
    private Paint selectionPaint2;

    // Matrix
    private Matrix matrix;

    // Path
    private Path path;

    // Widgets

    // TopBar
    private TextView tvImageName;
    private ImageButton imgGrid;
    private ImageButton imgUndo;
    private ImageButton imgRedo;
    private ImageButton imgMenu;

    // ToolBar
    private TextView tvPaintWidth;
    private CheckedImageGroup groupTools;
    private CheckedImageView imgPaint;
    private CheckedImageView imgGraph;
    private CheckedImageView imgEraser;
    private CheckedImageView imgFill;
    private CheckedImageView imgSelection;
    private CheckedImageView imgColorize;

    // PaletteBar
    private ImageButton imgPalette;
    private PaletteList listPalettes;

    // CanvasView
    private CanvasView canvasView;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_image_name:

                break;
            case R.id.img_grid:

                break;
            case R.id.img_undo:

                break;
            case R.id.img_redo:

                break;
            case R.id.img_menu:
                buildMenuPopup();
                break;
            case R.id.tv_paint_width:
                buildPaintWidthDialog();
                break;
            case R.id.img_palette:
                buildSelectPaletteDialog();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            dataSaved = true;
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                buildPermissionDialog();
            }
            else {
                recreate();
            }
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "CommitPrefEdits", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Settings.getInstance().getShapeFlag();

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

        // Set content view
        setContentView(R.layout.activity_main);

        // Window format
        getWindow().setFormat(PixelFormat.RGBA_8888);

        // Init SharedPreferences
        prefData = getSharedPreferences("app_data", MODE_PRIVATE);
        prefEditor = prefData.edit();
        dataSaved = false;

        // Get widgets

        // TopBar
        tvImageName = findViewById(R.id.tv_image_name);
        imgGrid = findViewById(R.id.img_grid);
        imgUndo = findViewById(R.id.img_undo);
        imgRedo = findViewById(R.id.img_redo);
        imgMenu = findViewById(R.id.img_menu);

        tvImageName.setOnClickListener(this);
        imgGrid.setOnClickListener(this);
        imgUndo.setOnClickListener(this);
        imgRedo.setOnClickListener(this);
        imgMenu.setOnClickListener(this);

        // ToolBar
        tvPaintWidth = findViewById(R.id.tv_paint_width);
        groupTools = findViewById(R.id.group_tools);
        imgPaint = findViewById(R.id.img_paint);
        imgGraph = findViewById(R.id.img_graph);
        imgEraser = findViewById(R.id.img_eraser);
        imgFill = findViewById(R.id.img_fill);
        imgSelection = findViewById(R.id.img_selection);
        imgColorize = findViewById(R.id.img_colorize);

        tvPaintWidth.setOnClickListener(this);

        // PaletteBar
        imgPalette = findViewById(R.id.img_palette);
        listPalettes = findViewById(R.id.list_palettes);

        imgPalette.setOnClickListener(this);

        // CanvasView
        canvasView = findViewById(R.id.canvas_view);

        // Load data
        loadData();

        // Init paints
        backgroundPaint = new Paint();
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(false);
        bitmapPaint.setFilterBitmap(false);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(false);
        paint.setAntiAlias(false);
        paint.setColor(listPalettes.getPaletteColor(listPalettes.getCheckedIndex()));
        setPaintFlag(paintFlag);
        eraser = new Paint();
        eraser.setStyle(Paint.Style.STROKE);
        eraser.setDither(false);
        eraser.setAntiAlias(false);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        setPaintWidth(paintWidth);
        selectionPaint1 = new Paint();
        selectionPaint1.setStyle(Paint.Style.STROKE);
        selectionPaint1.setStrokeCap(Paint.Cap.SQUARE);
        selectionPaint1.setDither(false);
        selectionPaint1.setAntiAlias(false);
        selectionPaint1.setColor(Color.WHITE);
        selectionPaint1.setStrokeWidth(imageScale * 0.5f + 0.5f);
        selectionPaint2 = new Paint();
        selectionPaint2.setStyle(Paint.Style.STROKE);
        selectionPaint2.setStrokeCap(Paint.Cap.SQUARE);
        selectionPaint2.setDither(false);
        selectionPaint2.setAntiAlias(false);
        selectionPaint2.setColor(Color.BLACK);
        selectionPaint2.setStrokeWidth(imageScale * 0.25f + 0.25f);

        // Init path
        path = new Path();

        // Init bitmap
        flushBackgroundBitmap();
        flushGridBitmap();

        // Init matrix
        matrix = new Matrix();

        // Paint width text
        tvPaintWidth.setText(Integer.toString(paintWidth));

        // Graph ImageButton image
        switch (graphFlag) {
            case ShapeBuffer.GraphFlag.LINE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_line_24, getTheme()));
                break;
            case ShapeBuffer.GraphFlag.CIRCLE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_circle_24, getTheme()));
                break;
            case ShapeBuffer.GraphFlag.OVAL:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_ellipse_24, getTheme()));
                break;
            case ShapeBuffer.GraphFlag.SQUARE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_square_24, getTheme()));
                break;
            case ShapeBuffer.GraphFlag.RECTANGLE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_rectangle_24, getTheme()));
                break;
        }

        // Select tool
        groupTools.setOnCheckedChangeListener(new CheckedImageGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckedImageGroup group, int checkedId, int checkedIndex) {
                for (int i = 0; i < group.getChildCount(); i ++) {
                    ((CheckedImageView)group.getChildAt(i)).setColorFilter(null);
                }
                switch (checkedId) {
                    case R.id.img_paint:
                        drawFlag = ToolBuffer.DrawFlag.PAINT;
                        paint.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        eraser.setStrokeJoin(Paint.Join.ROUND);
                        imgPaint.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_graph:
                        drawFlag = ToolBuffer.DrawFlag.GRAPH;
                        switch (graphFlag) {
                            case ShapeBuffer.GraphFlag.CIRCLE:
                            case ShapeBuffer.GraphFlag.OVAL:
                                paint.setStrokeCap(Paint.Cap.ROUND);
                                eraser.setStrokeCap(Paint.Cap.ROUND);
                                paint.setStrokeJoin(Paint.Join.ROUND);
                                eraser.setStrokeJoin(Paint.Join.ROUND);
                                break;
                            case ShapeBuffer.GraphFlag.LINE:
                            case ShapeBuffer.GraphFlag.SQUARE:
                            case ShapeBuffer.GraphFlag.RECTANGLE:
                                paint.setStrokeCap(Paint.Cap.SQUARE);
                                eraser.setStrokeCap(Paint.Cap.SQUARE);
                                paint.setStrokeJoin(Paint.Join.MITER);
                                eraser.setStrokeJoin(Paint.Join.MITER);
                                break;
                        }
                        imgGraph.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_eraser:
                        drawFlag = ToolBuffer.DrawFlag.ERASER;
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeJoin(Paint.Join.ROUND);
                        imgEraser.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_fill:
                        drawFlag = ToolBuffer.DrawFlag.FILL;
                        imgFill.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_selection:
                        drawFlag = ToolBuffer.DrawFlag.SELECTION;
                        imgSelection.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_colorize:
                        drawFlag = ToolBuffer.DrawFlag.COLORIZE;
                        imgColorize.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                }
                if (drawFlag != ToolBuffer.DrawFlag.SELECTION) {
                    selected = false;
                    selectionFlag = -1;
                }
                canvasView.invalidate();
            }
        });
        // Initial check
        switch (drawFlag) {
            case ToolBuffer.DrawFlag.PAINT:
                groupTools.check(R.id.img_paint);
                imgPaint.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolBuffer.DrawFlag.GRAPH:
                groupTools.check(R.id.img_graph);
                imgGraph.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolBuffer.DrawFlag.ERASER:
                groupTools.check(R.id.img_eraser);
                imgEraser.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolBuffer.DrawFlag.FILL:
                groupTools.check(R.id.img_fill);
                imgFill.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolBuffer.DrawFlag.SELECTION:
                groupTools.check(R.id.img_selection);
                imgSelection.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolBuffer.DrawFlag.COLORIZE:
                groupTools.check(R.id.img_colorize);
                imgColorize.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
        }
        // Double tap
        groupTools.setOnDoubleTapListener(new CheckedImageGroup.OnDoubleTapListener() {
            @Override
            public void onDoubleTap(CheckedImageGroup group, int checkedId, int checkedIndex) {
                switch (checkedId) {
                    case R.id.img_paint:
                        buildPaintDialog();
                        break;
                    case R.id.img_graph:
                        buildGraphDialog();
                        break;
                }
            }
        });

        // Select palette
        listPalettes.setOnCheckedChangeListener(new PaletteList.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(PaletteList list, int checkedIndex) {
                paint.setColor(listPalettes.getPaletteColor(checkedIndex));
            }
        });
        listPalettes.setOnDoubleTapListener(new PaletteList.OnDoubleTapListener() {
            @Override
            public void onDoubleTap(PaletteList list, int checkedIndex) {
                buildPaletteColorDialog();
            }
        });

    }

}
