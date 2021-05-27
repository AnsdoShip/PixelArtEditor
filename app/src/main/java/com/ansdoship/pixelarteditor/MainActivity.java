package com.ansdoship.pixelarteditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.ansdoship.pixelarteditor.editor.Flags;
import com.ansdoship.pixelarteditor.editor.PaletteManager;
import com.ansdoship.pixelarteditor.graphics.BitmapChanger;
import com.ansdoship.pixelarteditor.graphics.BitmapDecoder;
import com.ansdoship.pixelarteditor.graphics.BitmapEncoder;
import com.ansdoship.pixelarteditor.graphics.BitmapUtils;
import com.ansdoship.pixelarteditor.view.CanvasView;
import com.ansdoship.pixelarteditor.view.CheckedImageView;
import com.ansdoship.pixelarteditor.viewgroup.CheckedImageGroup;
import com.ansdoship.pixelarteditor.viewgroup.PaletteList;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean dataSaved;
    private void saveData() {

        // Bitmap cache
        BitmapEncoder.encodeFile(Utils.getCachePath() + "/CURRENT.png",
                currentBitmap, true, BitmapEncoder.CompressFormat.PNG, 100,
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
    private void loadData() {

        // Load bitmap
        currentBitmap = BitmapDecoder.decodeFile(Utils.getCachePath() + "/CURRENT.png");

        if(currentBitmap == null) {
            currentBitmap = Bitmap.createBitmap(Settings.IMAGE_WIDTH_DEFAULT,
                    Settings.IMAGE_HEIGHT_DEFAULT, Bitmap.Config.ARGB_8888);
        }
        currentCanvas = new Canvas(currentBitmap);

        // Scale mode
        scaleMode = false;

    }

    // Image
    private void setImageScale(int scale) {
        if(scale >= 1 && scale <= 64) {
            Settings.getInstance().putImageScale(scale);
            getSelectionPaint1().setStrokeWidth(scale * 0.5f + 0.5f);
            getSelectionPaint2().setStrokeWidth(scale * 0.25f + 0.25f);
            flushBackgroundBitmap();
            flushGridBitmap();
        }
    }

    private int getBackgroundImageScale() {
        int imageScale = Settings.getInstance().getImageScale();
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

    // Paint
    private void setPaintWidth(int width) {
        Settings.getInstance().putPaintWidth(width);
        paint.setStrokeWidth(width);
        eraser.setStrokeWidth(width);
    }

    private void setPaintFlag (int paintFlag) {
        if (paint == null) {
            return;
        }
        Settings.getInstance().putPaintFlag(paintFlag);
        switch (paintFlag) {
            case Flags.ToolFlag.PaintFlag.REPLACE:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                break;
            case Flags.ToolFlag.PaintFlag.OVERRIDE:
                paint.setXfermode(null);
                break;
        }
    }

    // Selection
    private boolean selected;
    private int selectionBitmapX;
    private int selectionBitmapY;
    private RectF selectionRectF;

    // Grid

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
        PaletteManager.getInstance().getGridPalette().setColor(0, gridColor);
        flushGridBitmap();
        canvasView.invalidate();
    }

    private int getGridColor() {
        return PaletteManager.getInstance().getGridPalette().getColor(0);
    }

    // Grid
    private Bitmap gridBitmap;
    private Paint gridPaint;
    private Canvas gridCanvas;
    public Paint getGridPaint() {
        if (gridPaint == null) {
            gridPaint = new Paint();
            gridPaint.setAntiAlias(false);
            gridPaint.setDither(false);
            gridPaint.setStyle(Paint.Style.STROKE);
            gridPaint.setStrokeWidth(1);
        }
        return gridPaint;
    }
    public Canvas getGridCanvas() {
        if (gridCanvas == null) {
            gridCanvas = new Canvas();
        }
        return gridCanvas;
    }

    private void flushGridBitmap() {
        int width = currentBitmap.getWidth();
        int height = currentBitmap.getHeight();
        int gridWidth = Settings.getInstance().getGridWidth();
        int gridHeight = Settings.getInstance().getGridHeight();
        int imageScale = Settings.getInstance().getImageScale();
        Paint gridPaint = getGridPaint();
        Canvas gridCanvas = getGridCanvas();
        replaceGridBitmap(Bitmap.createBitmap(gridWidth * imageScale, gridHeight *imageScale,
                Bitmap.Config.ARGB_8888));
        gridCanvas.setBitmap(gridBitmap);
        gridPaint.setColor(getGridColor());
        gridCanvas.drawRect(0, 0, gridBitmap.getWidth(), gridBitmap.getHeight(), gridPaint);
        BitmapShader gridShader = new BitmapShader(gridBitmap,
                BitmapShader.TileMode.REPEAT,
                BitmapShader.TileMode.REPEAT);
        gridPaint.setShader(gridShader);
        replaceGridBitmap(Bitmap.createBitmap(width * imageScale, height * imageScale,
                Bitmap.Config.ARGB_8888));
        gridCanvas.setBitmap(gridBitmap);
        gridCanvas.drawPaint(gridPaint);
    }

    private void replaceGridBitmap(Bitmap newBitmap) {
        Bitmap temp = gridBitmap;
        gridBitmap = newBitmap;
        BitmapUtils.recycleBitmap(temp);
    }

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
    private Paint backgroundPaint;
    private Canvas backgroundCanvas;
    public Paint getBackgroundPaint() {
        if (backgroundPaint == null) {
            backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(false);
            backgroundPaint.setDither(false);
        }
        return gridPaint;
    }
    public Canvas getBackgroundCanvas() {
        if (backgroundCanvas == null) {
            backgroundCanvas = new Canvas();
        }
        return backgroundCanvas;
    }
    private void setCanvasViewBackgroundColor (int backgroundColor) {
        PaletteManager.getInstance().getBackgroundPalette().setColor(0, backgroundColor);
        canvasView.invalidate();
    }
    private int getCanvasViewBackgroundColor () {
        return PaletteManager.getInstance().getBackgroundPalette().getColor(0);
    }
    private int getCanvasBackgroundColor1() {
        return PaletteManager.getInstance().getBackgroundPalette().getColor(1);
    }
    private int getCanvasBackgroundColor2() {
        return PaletteManager.getInstance().getBackgroundPalette().getColor(2);
    }
    private void flushBackgroundBitmap() {
        replaceBackgroundBitmap(Bitmap.createBitmap(new int[] {getCanvasBackgroundColor1(),
                        getCanvasBackgroundColor2(),
                        getCanvasBackgroundColor2(),
                        getCanvasBackgroundColor1()},
                2, 2, Bitmap.Config.ARGB_8888));
        int imageScale = Settings.getInstance().getImageScale();
        Paint backgroundPaint = getBackgroundPaint();
        Canvas backgroundCanvas = getBackgroundCanvas();
        replaceBackgroundBitmap(Bitmap.createScaledBitmap(backgroundBitmap,
                imageScale * getBackgroundImageScale(),
                imageScale * getBackgroundImageScale(), false));
        BitmapShader bgShader = new BitmapShader(backgroundBitmap,
                BitmapShader.TileMode.REPEAT,
                BitmapShader.TileMode.REPEAT);
        backgroundPaint.setShader(bgShader);
        replaceBackgroundBitmap(Bitmap.createBitmap(currentBitmap.getWidth() * imageScale,
                currentBitmap.getHeight() * imageScale, Bitmap.Config.ARGB_8888));
        backgroundCanvas.setBitmap(backgroundBitmap);
        backgroundCanvas.drawPaint(backgroundPaint);
    }
    private void replaceBackgroundBitmap(Bitmap newBitmap) {
        Bitmap temp = backgroundBitmap;
        backgroundBitmap = newBitmap;
        BitmapUtils.recycleBitmap(temp);
    }

    // Current
    private Bitmap currentBitmap;
    private Canvas currentCanvas;
    public Canvas getCurrentCanvas() {
        if (currentCanvas == null) {
            currentCanvas = new Canvas();
        }
        return currentCanvas;
    }

    // Cache
    private Bitmap cacheBitmap;

    // Selection
    private Bitmap selectionBitmap;

    // Paints
    private Paint bitmapPaint;

    public Paint getBitmapPaint() {
        if (bitmapPaint == null) {
            bitmapPaint = new Paint();
            bitmapPaint.setAntiAlias(false);
            bitmapPaint.setFilterBitmap(false);
        }
        return bitmapPaint;
    }

    private Paint paint;

    public Paint getPaint() {
        if (paint == null) {
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setDither(false);
            paint.setAntiAlias(false);
            paint.setColor(listPalettes.getPaletteColor(listPalettes.getCheckedIndex()));
            setPaintFlag(Settings.getInstance().getPaintFlag());
        }
        return paint;
    }

    private Paint eraser;

    public Paint getEraser() {
        if (eraser == null) {
            eraser = new Paint();
            eraser.setStyle(Paint.Style.STROKE);
            eraser.setDither(false);
            eraser.setAntiAlias(false);
            eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            setPaintWidth(Settings.getInstance().getPaintWidth());
        }
        return eraser;
    }

    private Paint selectionPaint1;

    public Paint getSelectionPaint1() {
        if (selectionPaint1 == null) {
            selectionPaint1 = new Paint();
            selectionPaint1.setStyle(Paint.Style.STROKE);
            selectionPaint1.setStrokeCap(Paint.Cap.SQUARE);
            selectionPaint1.setDither(false);
            selectionPaint1.setAntiAlias(false);
            selectionPaint1.setColor(Color.WHITE);
            selectionPaint1.setStrokeWidth(Settings.getInstance().getImageScale() * 0.5f + 0.5f);
        }
        return selectionPaint1;
    }

    private Paint selectionPaint2;

    public Paint getSelectionPaint2() {
        if (selectionPaint2 == null) {
            selectionPaint2 = new Paint();
            selectionPaint2.setStyle(Paint.Style.STROKE);
            selectionPaint2.setStrokeCap(Paint.Cap.SQUARE);
            selectionPaint2.setDither(false);
            selectionPaint2.setAntiAlias(false);
            selectionPaint2.setColor(Color.BLACK);
            selectionPaint2.setStrokeWidth(Settings.getInstance().getImageScale() * 0.25f + 0.25f);
        }
        return selectionPaint2;
    }

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
                //buildMenuPopup();
                break;
            case R.id.tv_paint_width:
                //buildPaintWidthDialog();
                break;
            case R.id.img_palette:
                //buildSelectPaletteDialog();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            dataSaved = true;
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // FIXME: BUILD PERMISSION DIALOG
            }
            else {
                recreate();
            }
        }
    }

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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (!dataSaved) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    saveData();
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

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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
        imgGraph = findViewById(R.id.img_shape);
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
        getPaint();
        getEraser();
        getBitmapPaint();
        getBackgroundPaint();
        getGridPaint();
        getSelectionPaint1();
        getSelectionPaint2();

        // Init path
        path = new Path();

        // Init bitmap
        flushBackgroundBitmap();
        flushGridBitmap();

        // Init matrix
        matrix = new Matrix();

        // Paint width text
        tvPaintWidth.setText(Integer.toString(Settings.getInstance().getPaintWidth()));

        // Set Shape ImageButton image
        switch (Settings.getInstance().getShapeFlag()) {
            case Flags.ToolFlag.ShapeFlag.LINE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_line_24, getTheme()));
                break;
            case Flags.ToolFlag.ShapeFlag.CIRCLE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_circle_24, getTheme()));
                break;
            case Flags.ToolFlag.ShapeFlag.ELLIPSE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_ellipse_24, getTheme()));
                break;
            case Flags.ToolFlag.ShapeFlag.SQUARE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_square_24, getTheme()));
                break;
            case Flags.ToolFlag.ShapeFlag.RECTANGLE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_rectangle_24, getTheme()));
                break;
        }
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
                        Settings.getInstance().putToolFlag(Flags.ToolFlag.PAINT);
                        paint.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        eraser.setStrokeJoin(Paint.Join.ROUND);
                        imgPaint.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_shape:
                        Settings.getInstance().putToolFlag(Flags.ToolFlag.SHAPE);
                        switch (Settings.getInstance().getShapeFlag()) {
                            case Flags.ToolFlag.ShapeFlag.CIRCLE:
                            case Flags.ToolFlag.ShapeFlag.ELLIPSE:
                                paint.setStrokeCap(Paint.Cap.ROUND);
                                eraser.setStrokeCap(Paint.Cap.ROUND);
                                paint.setStrokeJoin(Paint.Join.ROUND);
                                eraser.setStrokeJoin(Paint.Join.ROUND);
                                break;
                            case Flags.ToolFlag.ShapeFlag.LINE:
                            case Flags.ToolFlag.ShapeFlag.SQUARE:
                            case Flags.ToolFlag.ShapeFlag.RECTANGLE:
                                paint.setStrokeCap(Paint.Cap.SQUARE);
                                eraser.setStrokeCap(Paint.Cap.SQUARE);
                                paint.setStrokeJoin(Paint.Join.MITER);
                                eraser.setStrokeJoin(Paint.Join.MITER);
                                break;
                        }
                        imgGraph.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_eraser:
                        Settings.getInstance().putToolFlag(Flags.ToolFlag.ERASER);
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeJoin(Paint.Join.ROUND);
                        imgEraser.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_fill:
                        Settings.getInstance().putToolFlag(Flags.ToolFlag.FILL);
                        imgFill.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_selection:
                        Settings.getInstance().putToolFlag(Flags.ToolFlag.SELECTION);
                        imgSelection.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_colorize:
                        Settings.getInstance().putToolFlag(Flags.ToolFlag.COLORIZE);
                        imgColorize.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                }
                if (Settings.getInstance().getToolFlag() != Flags.ToolFlag.SELECTION) {
                    selected = false;
                    Settings.getInstance().putSelectionFlag(Flags.ToolFlag.SelectionFlag.NONE);
                }
                canvasView.invalidate();
            }
        });
        // Initial check
        switch (Settings.getInstance().getToolFlag()) {
            case Flags.ToolFlag.PAINT:
                groupTools.check(R.id.img_paint);
                imgPaint.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case Flags.ToolFlag.SHAPE:
                groupTools.check(R.id.img_shape);
                imgGraph.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case Flags.ToolFlag.ERASER:
                groupTools.check(R.id.img_eraser);
                imgEraser.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case Flags.ToolFlag.FILL:
                groupTools.check(R.id.img_fill);
                imgFill.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case Flags.ToolFlag.SELECTION:
                groupTools.check(R.id.img_selection);
                imgSelection.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case Flags.ToolFlag.COLORIZE:
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
                        //buildPaintDialog();
                        break;
                    case R.id.img_shape:
                        //buildGraphDialog();
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
                //buildPaletteColorDialog();
            }
        });

        // Draw
        canvasView.setOnInvalidateListener(new CanvasView.OnInvalidateListener() {

            float imageTranslationX;
            float imageTranslationY;
            int imageScale;
            
            @Override
            public void onInvalidate(Canvas canvas) {
                imageTranslationX = Settings.getInstance().getImageTranslationX();
                imageTranslationY = Settings.getInstance().getImageTranslationY();
                imageScale = Settings.getInstance().getImageScale();
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
                if(Settings.getInstance().getGridVisibility()) {
                    if(imageScale >= 4) {
                        canvas.drawBitmap(gridBitmap, imageTranslationX, imageTranslationY, bitmapPaint);
                        canvas.save();
                        canvas.restore();
                    }
                }
                // Draw selection board
                if (Settings.getInstance().getToolFlag() == Flags.ToolFlag.SELECTION && selected) {
                    int selectionLeft;
                    int selectionTop;
                    int selectionRight;
                    int selectionBottom;
                    RectF selectionRectF;
                    switch (Settings.getInstance().getSelectionFlag()) {
                        case Flags.ToolFlag.SelectionFlag.CUT:
                        case Flags.ToolFlag.SelectionFlag.COPY:
                            selectionLeft = selectionBitmapX * imageScale + imageScale / 2;
                            selectionTop = selectionBitmapY * imageScale + imageScale / 2;
                            selectionRight = selectionLeft + (selectionBitmap.getWidth() - 1) * imageScale;
                            selectionBottom = selectionTop + (selectionBitmap.getHeight() - 1) * imageScale;
                            break;
                        case Flags.ToolFlag.SelectionFlag.CLEAR:
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

        canvasView.setOnTouchListener(new View.OnTouchListener() {
            // Scale params: distance of two fingers
            double oldDist = 0;
            double newDist = 0;
            float x;
            float y;
            float imageTranslationX;
            float imageTranslationY;
            int imageScale;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                imageTranslationX = Settings.getInstance().getImageTranslationX();
                imageTranslationY = Settings.getInstance().getImageTranslationY();
                imageScale = Settings.getInstance().getImageScale();
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
                        switch (Settings.getInstance().getToolFlag()) {
                            case Flags.ToolFlag.PAINT:
                                currentCanvas.drawPoint(downX, downY, paint);
                                break;
                            case Flags.ToolFlag.ERASER:
                                currentCanvas.drawPoint(downX, downY, eraser);
                                break;
                            case Flags.ToolFlag.SELECTION:
                                switch (Settings.getInstance().getSelectionFlag()) {
                                    case Flags.ToolFlag.SelectionFlag.CUT:
                                    case Flags.ToolFlag.SelectionFlag.COPY:
                                        selectionBitmapX = downX - (int)(selectionBitmap.getWidth() * 0.5f);
                                        selectionBitmapY = downY - (int)(selectionBitmap.getHeight() * 0.5f);
                                        currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                                        selected = true;
                                        break;
                                    case Flags.ToolFlag.SelectionFlag.CLEAR:
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
                            case Flags.ToolFlag.COLORIZE:
                                if (downX >=0 && downY >= 0 && downX < currentBitmap.getWidth() && downY < currentBitmap.getHeight()) {
                                    listPalettes.setPaletteColor(listPalettes.getCheckedIndex(), currentBitmap.getPixel(downX, downY));
                                    paint.setColor(currentBitmap.getPixel(downX, downY));
                                    switch (Settings.getInstance().getPaletteFlag()) {
                                        case Flags.PaletteFlag.BACKGROUND:
                                            if (listPalettes.getCheckedIndex() == 0) {
                                                setCanvasViewBackgroundColor(currentBitmap.getPixel(downX, downY));
                                            } else {
                                                flushBackgroundBitmap();
                                                listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                                                        getCanvasBackgroundColor2());
                                                canvasView.invalidate();
                                            }
                                            break;
                                        case Flags.PaletteFlag.GRID:
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

                        Settings.getInstance().putSelectionFlag(Flags.ToolFlag.SelectionFlag.NONE);
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
                            switch (Settings.getInstance().getToolFlag()) {
                                case Flags.ToolFlag.PAINT:
                                case Flags.ToolFlag.ERASER:
                                    path.lineTo(moveX, moveY);
                                    break;
                                case Flags.ToolFlag.SHAPE:
                                    path.reset();
                                    switch (Settings.getInstance().getShapeFlag()) {
                                        case Flags.ToolFlag.ShapeFlag.LINE:
                                            path.moveTo(downX, downY);
                                            path.lineTo(moveX, moveY);
                                            break;
                                        case Flags.ToolFlag.ShapeFlag.CIRCLE:
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
                                        case Flags.ToolFlag.ShapeFlag.ELLIPSE:
                                            int ovalLeft = Math.min(downX, moveX);
                                            int ovalTop = Math.min(downY, moveY);
                                            int ovalRight = Math.max(downX, moveX);
                                            int ovalBottom = Math.max(downY, moveY);
                                            RectF ovalRectF = new RectF(ovalLeft, ovalTop, ovalRight, ovalBottom);
                                            path.addOval(ovalRectF, Path.Direction.CW);
                                            break;
                                        case Flags.ToolFlag.ShapeFlag.SQUARE:
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
                                        case Flags.ToolFlag.ShapeFlag.RECTANGLE:
                                            int rectLeft = Math.min(downX, moveX);
                                            int rectTop = Math.min(downY, moveY);
                                            int rectRight = Math.max(downX, moveX);
                                            int rectBottom = Math.max(downY, moveY);
                                            RectF rectRectF = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                                            path.addRect(rectRectF, Path.Direction.CW);
                                            break;
                                    }
                                    break;
                                case Flags.ToolFlag.SELECTION:
                                    switch (Settings.getInstance().getSelectionFlag()) {
                                        case Flags.ToolFlag.SelectionFlag.CUT:
                                        case Flags.ToolFlag.SelectionFlag.COPY:
                                            break;
                                        case Flags.ToolFlag.SelectionFlag.CLEAR:
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
                                case Flags.ToolFlag.COLORIZE:
                                    if (moveX >=0 && moveY >= 0 && moveX < currentBitmap.getWidth() && moveY < currentBitmap.getHeight()) {
                                        listPalettes.setPaletteColor(listPalettes.getCheckedIndex(), currentBitmap.getPixel(moveX, moveY));
                                        paint.setColor(currentBitmap.getPixel(moveX, moveY));
                                        switch (Settings.getInstance().getPaletteFlag()) {
                                            case Flags.PaletteFlag.BACKGROUND:
                                                if (listPalettes.getCheckedIndex() == 0) {
                                                    setCanvasViewBackgroundColor(currentBitmap.getPixel(downX, downY));
                                                } else {
                                                    flushBackgroundBitmap();
                                                    listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                                                            getCanvasBackgroundColor2());
                                                    canvasView.invalidate();
                                                }
                                                break;
                                            case Flags.PaletteFlag.GRID:
                                                setGridColor(currentBitmap.getPixel(downX, downY));
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
                            if (Settings.getInstance().getToolFlag() != Flags.ToolFlag.ERASER) {
                                currentCanvas.drawPath(path, paint);
                            }
                            else {
                                currentCanvas.drawPath(path, eraser);
                            }
                            switch (Settings.getInstance().getToolFlag()) {
                                // Draw down point
                                case Flags.ToolFlag.PAINT:
                                    currentCanvas.drawPoint(downX, downY, paint);
                                    break;
                                case Flags.ToolFlag.ERASER:
                                    currentCanvas.drawPoint(downX, downY, eraser);
                                    break;
                                // Draw selection bmp
                                case Flags.ToolFlag.SELECTION:
                                    switch (Settings.getInstance().getSelectionFlag()) {
                                        case Flags.ToolFlag.SelectionFlag.CUT:
                                        case Flags.ToolFlag.SelectionFlag.COPY:
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
                            switch (Settings.getInstance().getToolFlag()) {
                                case Flags.ToolFlag.FILL:
                                    if (downX >=0 && downY >= 0 && downX < currentBitmap.getWidth() && downY < currentBitmap.getHeight()) {
                                        new BitmapChanger(currentBitmap, false).fill(downX, downY, paint.getColor());
                                    }
                                    break;
                                case Flags.ToolFlag.SELECTION:
                                    switch (Settings.getInstance().getSelectionFlag()) {
                                        case Flags.ToolFlag.SelectionFlag.CUT:
                                        case Flags.ToolFlag.SelectionFlag.COPY:
                                            // Clear canvas
                                            currentCanvas.drawPaint(eraser);
                                            // Draw cache bitmap
                                            currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                                            currentCanvas.save();
                                            currentCanvas.restore();
                                            //buildSelectionPopup2();
                                            break;
                                        case Flags.ToolFlag.SelectionFlag.CLEAR:
                                        default:
                                            //buildSelectionPopup1();
                                            break;
                                    }
                                    break;
                            }
                            if (Settings.getInstance().getToolFlag() != Flags.ToolFlag.SELECTION) {
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

}
