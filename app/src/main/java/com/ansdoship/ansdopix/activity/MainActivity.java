package com.ansdoship.ansdopix.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ansdoship.ansdopix.R;
import com.ansdoship.ansdopix.util.FileIO;
import com.ansdoship.ansdopix.view.CanvasView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    // Data save
    // SharedPreferences
    private SharedPreferences prefData;
    private SharedPreferences.Editor prefEditor;
    // Data
    // Image
    private String imgName;
    private int imgScale;
    private void setImgScale(int scale) {
        if(scale >= 1 && scale <= 64) {
            imgScale = scale;
            selectionPaint1.setStrokeWidth(imgScale * 0.5f + 0.5f);
            selectionPaint2.setStrokeWidth(imgScale * 0.25f + 0.25f);
            flushBgBmp();
            flushGridBmp();
        }
    }
    private float imgTranslationX;
    private float imgTranslationY;
    private int bgImgScale() {
        int scale = 0;
        if(imgScale <= 4) {
            scale = 16;
        }
        if (imgScale > 4 && imgScale <= 8) {
            scale = 8;
        }
        if (imgScale > 8 && imgScale <= 16) {
            scale = 4;
        }
        if (imgScale > 16 && imgScale <= 32) {
            scale = 2;
        }
        if (imgScale > 32) {
            scale = 1;
        }
        return scale;
    }
    // Paint
    private int paintWidth;
    private void setPaintWidth(int width) {
        paintWidth = width;
        paint.setStrokeWidth(width);
        eraser.setStrokeWidth(width);
    }

    // Draw
    private int drawFlag;
    private static class DrawFlag {
        final static int PAINT = 0;
        final static int GRAPH = 1;
        final static int ERASER = 2;
        final static int FILL = 3;
        final static int SELECTION = 4;
        final static int COLOR_PICKER = 5;
    }
    
    private boolean isPaintRbtnChecked;

    // Graph
    private int graphType;
    private static class GraphType {
        final static int LINE = 0;
        final static int CIRCLE = 1;
        final static int OVAL = 2;
        final static int SQUARE = 3;
        final static int RECT = 4;
    }
    
    private boolean isGraphRbtnChecked;

    // Selection
    private int selectionFlag;
    private static class SelectionFlag {
        final static int CUT = 0;
        final static int COPY = 1;
        final static int CLEAR = 2;
    }
    private boolean selected;
    private int selectionBmpX;
    private int selectionBmpY;
    private RectF selectionRectF;

    // Grid
    private boolean isDrawGrid;
    private int gridWidth;
    private int gridHeight;
    private int gridColor;

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
        flushGridBmp();
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
        flushGridBmp();
    }

    public void setGridColor(int gridColor) {
        this.gridColor = gridColor;
        flushGridBmp();
    }

    private void flushGridBmp() {
        int width = currentBmp.getWidth();
        int height = currentBmp.getHeight();
        gridBmp = Bitmap.createBitmap(gridWidth * imgScale, gridHeight * imgScale,
                Bitmap.Config.ARGB_8888);
        gridCanvas = new Canvas(gridBmp);
        gridPaint = new Paint();
        gridPaint.setColor(gridColor);
        gridPaint.setStrokeWidth(1);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridCanvas.drawRect(0, 0, gridBmp.getWidth(), gridBmp.getHeight(), gridPaint);
        BitmapShader gridShader = new BitmapShader(gridBmp, BitmapShader.TileMode.REPEAT,
                BitmapShader.TileMode.REPEAT);
        gridPaint.setShader(gridShader);
        gridBmp = Bitmap.createBitmap(width * imgScale, height * imgScale,
                Bitmap.Config.ARGB_8888);
        gridCanvas = new Canvas(gridBmp);
        gridCanvas.drawPaint(gridPaint);
    }

    // Palette
    private int palettePage;
    private int paletteSlot;
    private int[][] palettes;
    // File save & load path
    private String CACHE_PATH;
    private String IMAGE_PATH;
    private String PALETTE_PATH;

    // Save & load methods
    private void saveData() {
        // Clear Old Data
        prefEditor.clear();

        // Write new data
        // Image
        prefEditor.putString("img_name", imgName);
        prefEditor.putInt("img_scale", imgScale);
        prefEditor.putFloat("img_translation_x", imgTranslationX);
        prefEditor.putFloat("img_translation_y", imgTranslationY);
        // Paint
        prefEditor.putInt("paint_width", paintWidth);
        prefEditor.putInt("draw_flag", drawFlag);
        prefEditor.putInt("graph_type", graphType);
        // Grid
        prefEditor.putBoolean("is_draw_grid", isDrawGrid);
        prefEditor.putInt("grid_width", gridWidth);
        prefEditor.putInt("grid_height", gridHeight);
        prefEditor.putInt("grid_color", gridColor);
        // Palette
        prefEditor.putInt("palette_page", palettePage);
        prefEditor.putInt("palette_slot", paletteSlot);
        for(int page = 0; page < palettes.length; page ++) {
            for(int slot = 0; slot < palettes[page].length; slot ++) {
                prefEditor.putInt("palette_" + page + "color_" + slot, palettes[page][slot]);
            }
        }
        // File save & load path
        prefEditor.putString("CACHE_PATH", CACHE_PATH);
        prefEditor.putString("IMAGE_PATH", IMAGE_PATH);
        prefEditor.putString("PALETTE_PATH", PALETTE_PATH);

        //Apply data
        prefEditor.apply();
    }

    private void loadData() {
        // Image
        imgName = prefData.getString("img_name", "Untitled.png");
        imgScale = prefData.getInt("img_scale", 50);
        imgTranslationX = prefData.getFloat("img_translation_x", 0);
        imgTranslationY = prefData.getFloat("img_translation_y", 0);
        // Paint
        paintWidth = prefData.getInt("paint_width", 1);
        drawFlag = prefData.getInt("draw_flag", DrawFlag.PAINT);
        graphType = prefData.getInt("graph_type", GraphType.LINE);
        // Grid
        isDrawGrid = prefData.getBoolean("is_draw_grid", true);
        gridWidth = prefData.getInt("grid_width", 1);
        gridHeight = prefData.getInt("grid_height", 1);
        gridColor = prefData.getInt("grid_color", Color.BLACK);
        // Palette
        palettePage = prefData.getInt("palette_page", 0);
        paletteSlot = prefData.getInt("palette_slot", 0);
        if(palettes == null) {
            palettes = new int[3][10];
        }
        for(int page = 0; page < palettes.length; page ++) {
            for(int slot = 0; slot < palettes[page].length; slot ++) {
                palettes[page][slot] = prefData.getInt("palette_" + page + "color_" + slot, Color.TRANSPARENT);
            }
        }
        // Save & load path
        CACHE_PATH = getExternalCacheDir() + "/";
        IMAGE_PATH = prefData.getString("IMAGE_PATH", getExternalFilesDir("images") + "/");
        PALETTE_PATH = prefData.getString("PALETTE_PATH", getExternalFilesDir("palettes") + "/");
        // Cache bitmap
        if(currentBmp == null) {
            currentBmp = FileIO.loadBitmapFromFile(CACHE_PATH + "current_bitmap.png");
            if(currentBmp == null) {
                currentBmp = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_8888);
            }
        }
        currentCanvas = new Canvas(currentBmp);
        // Scale mode
        scaleMode = false;
        // is rbtn checked
        isPaintRbtnChecked = false;
        isGraphRbtnChecked = false;
        // Selection flag
        selectionFlag = -1;
    }

    // Widgets
    // ActionBar
    private ActionBar actionBar;
    // ToolBar
    private TextView tvPaintWidth;
    private RadioGroup groupTools;
    private RadioButton rbtnPaint;
    private RadioButton rbtnGraph;
    private RadioButton rbtnEraser;
    private RadioButton rbtnFill;
    private RadioButton rbtnSelection;
    private RadioButton rbtnColorPicker;
    // PaletteBar
    private TextView tvPalettePage;
    // CanvasView
    private CanvasView canvasView;

    // TouchEvent positions
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
    private Bitmap bgBmp;
    private Canvas bgCanvas;
    // Current
    private Bitmap currentBmp;
    private Canvas currentCanvas;
    // Cache
    private Bitmap cacheBmp;
    // Selection
    private Bitmap selectionBmp;
    // Grid
    private Bitmap gridBmp;
    private Canvas gridCanvas;


    // Paints
    private Paint bgPaint;
    private void flushBgBmp() {
        bgBmp = Bitmap.createBitmap(new int[] {Color.LTGRAY, Color.GRAY, Color.GRAY, Color.LTGRAY},
                2, 2, Bitmap.Config.ARGB_8888);
        bgBmp = Bitmap.createScaledBitmap(bgBmp, imgScale * bgImgScale(),
                imgScale * bgImgScale(), false);
        BitmapShader bgShader = new BitmapShader(bgBmp, BitmapShader.TileMode.REPEAT,
                BitmapShader.TileMode.REPEAT);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setShader(bgShader);
        bgBmp = Bitmap.createBitmap(currentBmp.getWidth() * imgScale,
                currentBmp.getHeight() * imgScale, Bitmap.Config.ARGB_8888);
        bgCanvas = new Canvas(bgBmp);
        bgCanvas.drawPaint(bgPaint);
    }
    private Paint gridPaint;
    private Paint bmpPaint;
    private Paint paint;
    private Paint eraser;
    private Paint selectionPaint1;
    private Paint selectionPaint2;

    // Matrix
    private Matrix matrix;

    // Path
    private Path path;

    // Dialogs
    private void buildPaintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.paint_width);
        builder.create().show();
    }
    private void buildGraphDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.graph);
        String [] items = {
                getString(R.string.line),
                getString(R.string.circle),
                getString(R.string.oval),
                getString(R.string.square),
                getString(R.string.rect)
        };
        int checkedItem = -1;
        switch (graphType) {
            case GraphType.LINE:
                checkedItem = 0;
                break;
            case GraphType.CIRCLE:
                checkedItem = 1;
                break;
            case GraphType.OVAL:
                checkedItem = 2;
                break;
            case GraphType.SQUARE:
                checkedItem = 3;
                break;
            case GraphType.RECT:
                checkedItem = 4;
                break;
        }
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        graphType = GraphType.LINE;
                        break;
                    case 1:
                        graphType = GraphType.CIRCLE;
                        break;
                    case 2:
                        graphType = GraphType.OVAL;
                        break;
                    case 3:
                        graphType = GraphType.SQUARE;
                        break;
                    case 4:
                        graphType = GraphType.RECT;
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void buildSelectionDialog() {
        selectionFlag = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.selection);
        String[] items = {
                getString(R.string.selection_cut),
                getString(R.string.selection_copy),
                getString(R.string.clear)
        };
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectionX = Math.min(downX, moveX);
                int selectionY = Math.min(downY, moveY);
                selectionBmpX = selectionX;
                selectionBmpY = selectionY;
                int selectionWidth = Math.abs(downX - moveX) + 1;
                int selectionHeight = Math.abs(downY - moveY) + 1;
                selectionRectF = new RectF(selectionX, selectionY, Math.max(downX, moveX), Math.max(downY, moveY));
                switch (which) {
                    case 0:
                        selectionFlag = SelectionFlag.CUT;
                        selectionBmp = Bitmap.createBitmap(currentBmp, selectionX, selectionY, selectionWidth, selectionHeight);
                        eraser.setStyle(Paint.Style.FILL_AND_STROKE);
                        currentCanvas.drawRect(selectionRectF, eraser);
                        eraser.setStyle(Paint.Style.STROKE);
                        break;
                    case 1:
                        selectionFlag = SelectionFlag.COPY;
                        selectionBmp = Bitmap.createBitmap(currentBmp, selectionX, selectionY, selectionWidth, selectionHeight);
                        break;
                    case 2:
                        selectionFlag = SelectionFlag.CLEAR;
                        selected = false;
                        eraser.setStyle(Paint.Style.FILL_AND_STROKE);
                        currentCanvas.drawRect(selectionRectF, eraser);
                        currentCanvas.save();
                        currentCanvas.restore();
                        eraser.setStyle(Paint.Style.STROKE);
                        canvasView.invalidate();
                        break;
                }
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (selectionFlag == -1) {
                    selected = false;
                    canvasView.invalidate();
                }
            }
        });
        builder.create().show();
    }
    private void buildSelectionDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.selection);
        String[] items = {
                getString(R.string.done),
                getString(R.string.none),
                getString(R.string.clear)
        };
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 1:
                        // Clear canvas
                        currentCanvas.drawPaint(eraser);
                        // Draw cache bitmap
                        currentCanvas.drawBitmap(cacheBmp, 0, 0, bmpPaint);
                        break;
                    case 2:
                        // Clear canvas
                        currentCanvas.drawPaint(eraser);
                        // Draw cache bitmap
                        currentCanvas.drawBitmap(cacheBmp, 0, 0, bmpPaint);
                    case 0:
                        selectionFlag = -1;
                        selected = false;
                        currentCanvas.save();
                        currentCanvas.restore();
                        canvasView.invalidate();
                        break;
                }
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (selectionFlag == -1) {
                    selected = false;
                    canvasView.invalidate();
                }
            }
        });
        builder.create().show();
    }

    @SuppressLint({"ClickableViewAccessibility", "CommitPrefEdits"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFormat(PixelFormat.RGBA_8888);

        // Init SharedPreferences
        prefData = getSharedPreferences("data", MODE_PRIVATE);
        prefEditor = prefData.edit();

        // Get widgets
        // ActionBar
        actionBar = getSupportActionBar();
        // ToolBar
        tvPaintWidth = findViewById(R.id.tv_paint_width);
        groupTools = findViewById(R.id.group_tools);
        rbtnPaint = findViewById(R.id.rbtn_paint);
        rbtnGraph = findViewById(R.id.rbtn_graph);
        rbtnEraser = findViewById(R.id.rbtn_eraser);
        rbtnFill = findViewById(R.id.rbtn_fill);
        rbtnSelection = findViewById(R.id.rbtn_selection);
        rbtnColorPicker = findViewById(R.id.rbtn_color_picker);
        // PaletteBar
        tvPalettePage = findViewById(R.id.tv_palette_page);
        // CanvasView
        canvasView = findViewById(R.id.canvas_view);

        // Load data
        loadData();

        // Init paints
        bgPaint = new Paint();
        bmpPaint = new Paint();
        bmpPaint.setAntiAlias(true);
        bmpPaint.setFilterBitmap(false);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(false);
        paint.setAntiAlias(false);
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
        selectionPaint1.setStrokeWidth(imgScale * 0.5f + 0.5f);
        selectionPaint2 = new Paint();
        selectionPaint2.setStyle(Paint.Style.STROKE);
        selectionPaint2.setStrokeCap(Paint.Cap.SQUARE);
        selectionPaint2.setDither(false);
        selectionPaint2.setAntiAlias(false);
        selectionPaint2.setColor(Color.BLACK);
        selectionPaint2.setStrokeWidth(imgScale * 0.25f + 0.25f);

        // Init path
        path = new Path();

        // Init bmp
        flushBgBmp();
        flushGridBmp();

        // Init matrix
        matrix = new Matrix();

        // Default checked RatioButton
        switch (drawFlag) {
            case DrawFlag.PAINT:
                groupTools.check(R.id.rbtn_paint);
                paint.setStrokeCap(Paint.Cap.ROUND);
                eraser.setStrokeCap(Paint.Cap.ROUND);
                break;
            case DrawFlag.GRAPH:
                groupTools.check(R.id.rbtn_graph);
                paint.setStrokeCap(Paint.Cap.SQUARE);
                eraser.setStrokeCap(Paint.Cap.SQUARE);
                isGraphRbtnChecked = true;
                break;
            case DrawFlag.ERASER:
                groupTools.check(R.id.rbtn_eraser);
                eraser.setStrokeCap(Paint.Cap.ROUND);
                break;
            case DrawFlag.FILL:
                groupTools.check(R.id.rbtn_fill);
                break;
            case DrawFlag.SELECTION:
                groupTools.check(R.id.rbtn_selection);
                break;
            case DrawFlag.COLOR_PICKER:
                groupTools.check(R.id.rbtn_color_picker);
                break;
        }

        // Select tool
        groupTools.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_paint:
                        drawFlag = DrawFlag.PAINT;
                        paint.setStrokeCap(Paint.Cap.ROUND);
                        eraser.setStrokeCap(Paint.Cap.ROUND);
                        break;
                    case R.id.rbtn_graph:
                        drawFlag = DrawFlag.GRAPH;
                        paint.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        break;
                    case R.id.rbtn_eraser:
                        drawFlag = DrawFlag.ERASER;
                        eraser.setStrokeCap(Paint.Cap.ROUND);
                        break;
                    case R.id.rbtn_fill:
                        drawFlag = DrawFlag.FILL;
                        break;
                    case R.id.rbtn_selection:
                        drawFlag = DrawFlag.SELECTION;
                        break;
                    case R.id.rbtn_color_picker:
                        drawFlag = DrawFlag.COLOR_PICKER;
                        break;
                }
                if (drawFlag != DrawFlag.GRAPH) {
                    isGraphRbtnChecked = false;
                }
                if (drawFlag != DrawFlag.SELECTION) {
                    selected = false;
                    selectionFlag = -1;
                }
                canvasView.invalidate();
            }
        });

        rbtnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGraphRbtnChecked) {
                    buildGraphDialog();
                }
                if (!isGraphRbtnChecked) {
                    isGraphRbtnChecked = true;
                }
            }
        });

        // Draw
        canvasView.setOnInvalidateListener(new CanvasView.OnInvalidateListener() {
            @Override
            public void onInvalidate(Canvas canvas) {
                // Clear canvas
                canvas.drawPaint(eraser);
                canvas.save();
                canvas.restore();
                // Set matrix
                matrix.setTranslate(imgTranslationX / imgScale, imgTranslationY / imgScale);
                matrix.postScale(imgScale, imgScale);
                // Draw background
                canvas.drawBitmap(bgBmp, imgTranslationX, imgTranslationY, bgPaint);
                canvas.save();
                canvas.restore();
                // Draw scaled bitmap
                canvas.drawBitmap(currentBmp, matrix, bmpPaint);
                canvas.save();
                canvas.restore();
                // Draw grid
                if(isDrawGrid) {
                    if(imgScale >= 4) {
                        canvas.drawBitmap(gridBmp, imgTranslationX, imgTranslationY, bmpPaint);
                        canvas.save();
                        canvas.restore();
                    }
                }
                // Draw selection board
                if (drawFlag == DrawFlag.SELECTION && selected) {
                    int selectionLeft;
                    int selectionTop;
                    int selectionRight;
                    int selectionBottom;
                    RectF selectionRectF = new RectF();
                    switch (selectionFlag) {
                        case SelectionFlag.CUT:
                        case SelectionFlag.COPY:
                            selectionLeft = selectionBmpX * imgScale + imgScale / 2;
                            selectionTop = selectionBmpY * imgScale + imgScale / 2;
                            selectionRight = selectionLeft + (selectionBmp.getWidth() - 1) * imgScale;
                            selectionBottom = selectionTop + (selectionBmp.getHeight() - 1) * imgScale;
                            break;
                        case SelectionFlag.CLEAR:
                        default:
                            selectionLeft = Math.min(downX, moveX) * imgScale + imgScale / 2;
                            selectionTop = Math.min(downY, moveY) * imgScale + imgScale / 2;
                            selectionRight = Math.max(downX, moveX) * imgScale + imgScale / 2;
                            selectionBottom = Math.max(downY, moveY) * imgScale + imgScale / 2;
                            break;
                    }
                    selectionRectF = new RectF(selectionLeft, selectionTop, selectionRight, selectionBottom);
                    canvas.drawRect(selectionRectF, selectionPaint1);
                    canvas.drawRect(selectionRectF, selectionPaint2);
                }
            }
        });

        // Set OnTouchListener
        canvasView.setOnTouchListener(new View.OnTouchListener() {
            // Scale parameters: distance of two fingers
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
                        cacheBmp = Bitmap.createBitmap(currentBmp);
                        selected = false;
                        // Draw current bitmap
                        downX = (int) Math.floor((event.getX(0) - imgTranslationX) / imgScale);
                        downY = (int) Math.floor((event.getY(0) - imgTranslationY) / imgScale);
                        moveX = downX;
                        moveY = downY;
                        path.moveTo(downX, downY);
                        switch (drawFlag) {
                            case DrawFlag.PAINT:
                                currentCanvas.drawPoint(downX, downY, eraser);
                                currentCanvas.drawPoint(downX, downY, paint);
                                if (downX >=0 && downY >= 0 && downX < currentBmp.getWidth() && downY < currentBmp.getHeight()) {
                                    currentBmp.setPixel(downX, downY, paint.getColor());
                                }
                                break;
                            case DrawFlag.ERASER:
                                currentCanvas.drawPoint(downX, downY, eraser);
                                if (downX >=0 && downY >= 0 && downX < currentBmp.getWidth() && downY < currentBmp.getHeight()) {
                                    currentBmp.setPixel(downX, downY, Color.TRANSPARENT);
                                }
                                break;
                            case DrawFlag.SELECTION:
                                switch (selectionFlag) {
                                    case SelectionFlag.CUT:
                                    case SelectionFlag.COPY:
                                        selectionBmpX = downX - (int)(selectionBmp.getWidth() * 0.5f);
                                        selectionBmpY = downY - (int)(selectionBmp.getHeight() * 0.5f);
                                        currentCanvas.drawBitmap(selectionBmp, selectionBmpX, selectionBmpY, bmpPaint);
                                        selected = true;
                                        break;
                                    case SelectionFlag.CLEAR:
                                    default:
                                        if (downX < 0) {
                                            downX = 0;
                                        }
                                        if (downX >= currentBmp.getWidth()) {
                                            downX = currentBmp.getWidth() - 1;
                                        }
                                        if (downY < 0) {
                                            downY = 0;
                                        }
                                        if (downY >= currentBmp.getHeight()) {
                                            downY = currentBmp.getHeight() - 1;
                                        }
                                        break;
                                }
                                break;
                            case DrawFlag.COLOR_PICKER:
                                if (downX >=0 && downY >= 0 && downX < currentBmp.getWidth() && downY < currentBmp.getHeight()) {
                                }
                                break;
                        }
                        currentCanvas.save();
                        currentCanvas.restore();
                        canvasView.invalidate();
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // Record initial distance
                        oldDist = spacing(event);
                        newDist = oldDist;
                        scaleMode = true;
                        readOnlyMode = true;

                        selectionFlag = -1;
                        selected = false;

                        currentCanvas.drawPaint(eraser);
                        currentCanvas.drawBitmap(cacheBmp, 0, 0, bmpPaint);
                        currentCanvas.save();
                        currentCanvas.restore();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(scaleMode) {
                            newDist = spacing(event);
                            // If distance of two fingers > 256
                            // Replace 256 with other value to control sensitivity
                            if(newDist != 0) {
                                if(newDist >= oldDist + 256) {
                                    setImgScale(imgScale * 2);
                                    oldDist = newDist;
                                }
                                if(newDist <= oldDist - 256) {
                                    setImgScale(imgScale / 2);
                                    oldDist = newDist;
                                }
                            }
                            imgTranslationX += event.getX(0) - x;
                            imgTranslationY += event.getY(0) - y;
                            x = event.getX(0);
                            y = event.getY(0);
                        }
                        else if (!readOnlyMode) {
                            // Current path
                            moveX = (int) Math.floor((event.getX(0) - imgTranslationX) / imgScale);
                            moveY = (int) Math.floor((event.getY(0) - imgTranslationY) / imgScale);
                            switch (drawFlag) {
                                case DrawFlag.PAINT:
                                case DrawFlag.ERASER:
                                    path.lineTo(moveX, moveY);
                                    break;
                                case DrawFlag.GRAPH:
                                    path.reset();
                                    switch (graphType) {
                                        case GraphType.LINE:
                                            path.moveTo(downX, downY);
                                            path.lineTo(moveX, moveY);
                                            break;
                                        case GraphType.CIRCLE:
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
                                            RectF circleRectF = new RectF(circleLeft, circleTop, circleRight, circleBottom);
                                            path.addOval(circleRectF, Path.Direction.CW);
                                            break;
                                        case GraphType.OVAL:
                                            int ovalLeft = Math.min(downX, moveX);
                                            int ovalTop = Math.min(downY, moveY);
                                            int ovalRight = Math.max(downX, moveX);
                                            int ovalBottom = Math.max(downY, moveY);
                                            RectF ovalRectF = new RectF(ovalLeft, ovalTop, ovalRight, ovalBottom);
                                            path.addOval(ovalRectF, Path.Direction.CW);
                                            break;
                                        case GraphType.SQUARE:
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
                                        case GraphType.RECT:
                                            int rectLeft = Math.min(downX, moveX);
                                            int rectTop = Math.min(downY, moveY);
                                            int rectRight = Math.max(downX, moveX);
                                            int rectBottom = Math.max(downY, moveY);
                                            RectF rectRectF = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                                            path.addRect(rectRectF, Path.Direction.CW);
                                            break;
                                    }
                                    break;
                                case DrawFlag.FILL:
                                    break;
                                case DrawFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case SelectionFlag.CUT:
                                        case SelectionFlag.COPY:
                                            break;
                                        case SelectionFlag.CLEAR:
                                        default:
                                            if (moveX < 0) {
                                                moveX = 0;
                                            }
                                            if (moveX >= currentBmp.getWidth()) {
                                                moveX = currentBmp.getWidth() - 1;
                                            }
                                            if (moveY < 0) {
                                                moveY = 0;
                                            }
                                            if (moveY >= currentBmp.getHeight()) {
                                                moveY = currentBmp.getHeight() - 1;
                                            }
                                            break;
                                    }
                                    selected = true;
                                    break;
                                case DrawFlag.COLOR_PICKER:
                                    break;
                            }
                            // Clear canvas
                            currentCanvas.drawPaint(eraser);
                            // Draw cache bitmap
                            currentCanvas.drawBitmap(cacheBmp, 0, 0, bmpPaint);
                            currentCanvas.save();
                            currentCanvas.restore();
                            // Draw path
                            currentCanvas.drawPath(path, eraser);
                            if (drawFlag != DrawFlag.ERASER) {
                                currentCanvas.drawPath(path, paint);
                            }
                            switch (drawFlag) {
                                // Draw down point
                                case DrawFlag.PAINT:
                                    currentCanvas.drawPoint(downX, downY, eraser);
                                    currentCanvas.drawPoint(downX, downY, paint);
                                    if (downX >=0 && downY >= 0 && downX < currentBmp.getWidth() && downY < currentBmp.getHeight()) {
                                        currentBmp.setPixel(downX, downY, paint.getColor());
                                    }
                                    break;
                                case DrawFlag.ERASER:
                                    currentCanvas.drawPoint(downX, downY, eraser);
                                    if (downX >=0 && downY >= 0 && downX < currentBmp.getWidth() && downY < currentBmp.getHeight()) {
                                        currentBmp.setPixel(downX, downY, Color.TRANSPARENT);
                                    }
                                    break;
                                // Draw selection bmp
                                case DrawFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case SelectionFlag.CUT:
                                        case SelectionFlag.COPY:
                                            selectionBmpX = moveX - (int)(selectionBmp.getWidth() * 0.5f);
                                            selectionBmpY = moveY - (int)(selectionBmp.getHeight() * 0.5f);
                                            currentCanvas.drawBitmap(selectionBmp, selectionBmpX, selectionBmpY, bmpPaint);
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
                        upX = (int) Math.floor((event.getX(0) - imgTranslationX) / imgScale);
                        upY = (int) Math.floor((event.getY(0) - imgTranslationY) / imgScale);
                        if (readOnlyMode) {
                            readOnlyMode = false;
                        }
                        else {
                            // Draw current bitmap
                            switch (drawFlag) {
                                case DrawFlag.FILL:
                                    if (downX >=0 && downY >= 0 && downX < currentBmp.getWidth() && downY < currentBmp.getHeight()) {
                                        fill(currentBmp, downX, downY, paint.getColor());
                                    }
                                    break;
                                case DrawFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case SelectionFlag.CUT:
                                        case SelectionFlag.COPY:
                                            buildSelectionDialog2();
                                            break;
                                        case SelectionFlag.CLEAR:
                                        default:
                                            buildSelectionDialog();
                                            break;
                                    }
                                    break;
                                case DrawFlag.COLOR_PICKER:
                                    break;
                            }
                            if (drawFlag != DrawFlag.SELECTION) {
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
        });
    }

    // Calculate linear distance of two fingers
    private double spacing(MotionEvent event) {
        if(event.getPointerCount() >= 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return Math.pow(x * x + y * y, 0.5);
        }
        return 0;
    }

    // Scan line seed fill
    private void fill(Bitmap bitmap, int x, int y, int newColor) {
        Stack<Point> pointStack = new Stack<>();
        Point seed;
        if (bitmap.getPixel(x, y) != newColor) {
            pointStack.push(new Point(x, y));
        }
        while (true) {
            if (!pointStack.isEmpty()) {
                seed = pointStack.pop();
                int oldColor = bitmap.getPixel(seed.x, seed.y);
                int leftX = seed.x;
                int rightX = seed.x;
                int boundaryLeft = -1;
                int boundaryRight = -1;
                if (oldColor != newColor) {
                    while(true) {
                        if (leftX - 1 >= 0) {
                            if(bitmap.getPixel(leftX - 1, seed.y) != oldColor) {
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
                        if (rightX + 1 < bitmap.getWidth()) {
                            if (bitmap.getPixel(rightX + 1, seed.y) != oldColor) {
                                boundaryRight = rightX;
                                break;
                            }
                            rightX++;
                        } else {
                            if (rightX + 1 == bitmap.getWidth()) {
                                boundaryRight = rightX;
                            }
                            break;
                        }
                    }
                    if (boundaryLeft != -1 && boundaryRight != -1) {
                        for (int detectX = boundaryLeft; detectX <= boundaryRight; detectX ++) {
                            if (seed.y + 1 < bitmap.getHeight()) {
                                if (bitmap.getPixel(detectX, seed.y + 1) == oldColor) {
                                    if (detectX < boundaryRight) {
                                        if (bitmap.getPixel(detectX + 1, seed.y + 1) != oldColor) {
                                            pointStack.push(new Point(detectX, seed.y + 1));
                                        }
                                    }
                                    else {
                                        pointStack.push(new Point(detectX, seed.y + 1));
                                    }
                                }
                            }
                            if (seed.y - 1 >= 0) {
                                if (bitmap.getPixel(detectX, seed.y - 1) == oldColor) {
                                    if (detectX < boundaryRight) {
                                        if (bitmap.getPixel(detectX + 1, seed.y - 1) != oldColor) {
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
                fillLine(bitmap, seed.x, seed.y, newColor);
            }
            else {
                break;
            }
        }
    }

    private void fillLine(Bitmap bitmap, int x, int y, int newColor) {
        fillLineLeft(bitmap, x, y, newColor);
        if (x + 1 < bitmap.getWidth()) {
            fillLineRight(bitmap, x + 1, y, newColor);
        }
    }

    private void fillLineLeft(Bitmap bitmap, int x, int y, int newColor) {
        int oldColor = bitmap.getPixel(x, y);
        if (oldColor == newColor) {
            return;
        }
        while(true) {
            if(x >= 0) {
                if(bitmap.getPixel(x, y) == oldColor) {
                    bitmap.setPixel(x, y, newColor);
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

    private void fillLineRight(Bitmap bitmap, int x, int y, int newColor) {
        int oldColor = bitmap.getPixel(x, y);
        if (oldColor == newColor) {
            return;
        }
        while(true) {
            if(x < bitmap.getWidth()) {
                if(bitmap.getPixel(x, y) == oldColor) {
                    bitmap.setPixel(x, y, newColor);
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

    // Set ActionBar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Recycle bitmaps
        /*
        if(!currentBmp.isRecycled()) {
            currentBmp.recycle();
        }
        if(!cacheBmp.isRecycled()) {
            cacheBmp.recycle();
        }
        if(!gridBmp.isRecycled()) {
            gridBmp.recycle();
        }
        if(!bgBmp.isRecycled()) {
            bgBmp.recycle();
        }

         */
    }
}
