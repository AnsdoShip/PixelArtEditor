package com.ansdoship.pixelarteditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ansdoship.pixelarteditor.editor.Editor;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFlag;
import com.ansdoship.pixelarteditor.editor.ToolFlag;
import com.ansdoship.pixelarteditor.editor.buffer.FillBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.MultiBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.PaintBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.PointBuffer;
import com.ansdoship.pixelarteditor.util.Utils;
import com.ansdoship.pixelarteditor.ui.view.CanvasView;
import com.ansdoship.pixelarteditor.ui.view.CheckedImageView;
import com.ansdoship.pixelarteditor.ui.viewgroup.CheckedImageGroup;
import com.ansdoship.pixelarteditor.ui.viewgroup.PaletteList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // EDITOR
    private boolean dataSaved;
    private final Editor editor = Editor.getInstance();

    // WIDGETS
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

    // ON CLICK
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.tv_image_name:
                break;
            case R.id.img_grid:

                break;
            case R.id.img_undo:
                editor.getToolBufferPool().undo();
                canvasView.invalidate();
                break;
            case R.id.img_redo:
                editor.getToolBufferPool().redo();
                canvasView.invalidate();
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

    // PERMISSION CALLBACK
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

    // SAVE & LOAD DATA
    // On pause
    @Override
    protected void onPause() {
        if (isFinishing() && (!dataSaved)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    editor.saveData();
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
                    editor.saveData();
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
        if (dataSaved) {
            editor.loadData();
            dataSaved = false;
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    // On create
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

        // Set window format
        getWindow().setFormat(PixelFormat.RGBA_8888);

        // Get widgets & set listeners
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
        editor.setCanvasView(canvasView);

        // Set widget hints
        // Set paint width text
        tvPaintWidth.setText(Integer.toString(editor.getPaintWidth()));
        // Set shape ImageButton image
        switch (editor.getShapeFlag()) {
            case ToolFlag.ShapeFlag.LINE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_line_24, getTheme()));
                break;
            case ToolFlag.ShapeFlag.CIRCLE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_circle_24, getTheme()));
                break;
            case ToolFlag.ShapeFlag.ELLIPSE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_ellipse_24, getTheme()));
                break;
            case ToolFlag.ShapeFlag.SQUARE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
                        R.drawable.ic_outline_square_24, getTheme()));
                break;
            case ToolFlag.ShapeFlag.RECTANGLE:
                imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(),
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
                        editor.setToolFlag(ToolFlag.PAINT);
                        editor.setStrokeCap(Paint.Cap.SQUARE);
                        editor.setStrokeJoin(Paint.Join.ROUND);
                        imgPaint.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_shape:
                        editor.setToolFlag(ToolFlag.SHAPE);
                        switch (editor.getShapeFlag()) {
                            case ToolFlag.ShapeFlag.CIRCLE:
                            case ToolFlag.ShapeFlag.ELLIPSE:
                                editor.setStrokeCap(Paint.Cap.ROUND);
                                editor.setStrokeJoin(Paint.Join.ROUND);
                                break;
                            case ToolFlag.ShapeFlag.LINE:
                            case ToolFlag.ShapeFlag.SQUARE:
                            case ToolFlag.ShapeFlag.RECTANGLE:
                                editor.setStrokeCap(Paint.Cap.SQUARE);
                                editor.setStrokeJoin(Paint.Join.MITER);
                                break;
                        }
                        imgGraph.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_eraser:
                        editor.setToolFlag(ToolFlag.ERASER);
                        editor.setStrokeCap(Paint.Cap.SQUARE);
                        editor.setStrokeJoin(Paint.Join.ROUND);
                        imgEraser.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_fill:
                        editor.setToolFlag(ToolFlag.FILL);
                        imgFill.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_selection:
                        editor.setToolFlag(ToolFlag.SELECTION);
                        imgSelection.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                    case R.id.img_colorize:
                        editor.setToolFlag(ToolFlag.COLORIZE);
                        imgColorize.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                        break;
                }
                if (editor.getToolFlag() != ToolFlag.SELECTION) {
                    editor.setSelected(false);
                    editor.setSelectionFlag(ToolFlag.SelectionFlag.NONE);
                }
                canvasView.invalidate();
            }
        });
        // Initial check
        switch (editor.getToolFlag()) {
            case ToolFlag.PAINT:
                groupTools.check(R.id.img_paint);
                imgPaint.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
                break;
            case ToolFlag.SHAPE:
                groupTools.check(R.id.img_shape);
                imgGraph.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
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
        // Palette list
        // Select palette
        switch (editor.getPaletteFlag()) {
            case PaletteFlag.BACKGROUND:
                listPalettes.setPalette(editor.getBackgroundPalette());
                break;
            case PaletteFlag.GRID:
                listPalettes.setPalette(editor.getGridPalette());
                break;
            case PaletteFlag.INTERNAL:
                listPalettes.setPalette(editor.getBuiltinPalette());
                break;
            case PaletteFlag.EXTERNAL:
                editor.loadExternalPalette(editor.getExternalPaletteName());
                if (editor.getExternalPalette() == null) {
                    editor.setPaletteFlag(PaletteFlag.INTERNAL);
                    listPalettes.setPalette(editor.getBuiltinPalette());
                }
                else {
                    listPalettes.setPalette(editor.getExternalPalette());
                }
                break;
        }
        // Initial select
        listPalettes.setOnCheckedChangeListener(new PaletteList.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(PaletteList list, int checkedIndex) {
                editor.flushPaint(listPalettes.getCheckedPaletteColor());
            }
        });
        // Double tap
        listPalettes.setOnDoubleTapListener(new PaletteList.OnDoubleTapListener() {
            @Override
            public void onDoubleTap(PaletteList list, int checkedIndex) {
                //buildPaletteColorDialog();
            }
        });

        // CanvasView
        // Draw
        canvasView.setOnInvalidateListener(new CanvasView.OnInvalidateListener() {

            float imageTranslationX;
            float imageTranslationY;
            int imageScale;
            final Paint eraser = editor.getEraser();
            final Matrix matrix = editor.getMatrix();
            
            @Override
            public void onInvalidate(Canvas canvas) {
                imageTranslationX = editor.getImageTranslationX();
                imageTranslationY = editor.getImageTranslationY();
                imageScale = editor.getImageScale();
                // Clear canvas
                canvas.drawPaint(eraser);
                canvas.save();
                canvas.restore();
                // Set matrix
                matrix.setTranslate(imageTranslationX / imageScale, imageTranslationY / imageScale);
                matrix.postScale(imageScale, imageScale);
                // Draw background
                canvas.drawColor(getCanvasViewBackgroundColor());
                canvas.drawRect(imageTranslationX, imageTranslationY,
                        imageTranslationX + editor.getToolBufferPool().getCurrentBitmap().getWidth() * imageScale,
                        imageTranslationY + editor.getToolBufferPool().getCurrentBitmap().getHeight() * imageScale,
                        canvasBackgroundPaint);
                canvas.save();
                canvas.restore();
                // Draw scaled bitmap
                canvas.drawBitmap(editor.getToolBufferPool().getCurrentBitmap(), matrix, bitmapPaint);
                canvas.save();
                canvas.restore();
                // Draw grid
                if(editor.isGridVisible()) {
                    if(imageScale >= 4) {
                        int width = editor.getToolBufferPool().getCurrentBitmap().getWidth();
                        int height = editor.getToolBufferPool().getCurrentBitmap().getHeight();
                        for (int i = 0; i <= width; i += editor.getGridWidth()) {
                            canvas.drawLine(
                                    imageTranslationX + i * imageScale,
                                    imageTranslationY,
                                    imageTranslationX + i * imageScale,
                                    imageTranslationY + height * imageScale, gridPaint);
                        }
                        for (int i = 0; i <= height; i += editor.getGridHeight()) {
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
                if (editor.getToolFlag() == ToolFlag.SELECTION && selected) {
                    float selectionLeft;
                    float selectionTop;
                    float selectionRight;
                    float selectionBottom;
                    RectF selectionRectF;
                    switch (editor.getSelectionFlag()) {
                        case ToolFlag.SelectionFlag.CUT:
                        case ToolFlag.SelectionFlag.COPY:
                            selectionLeft = selectionBitmapX * imageScale + selectionPaint1.getStrokeWidth() / 2;
                            selectionTop = selectionBitmapY * imageScale + selectionPaint1.getStrokeWidth() / 2;
                            selectionRight = selectionLeft + selectionBitmap.getWidth() * imageScale;
                            selectionBottom = selectionTop + selectionBitmap.getHeight() * imageScale;
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
                imageTranslationX = editor.getImageTranslationX();
                imageTranslationY = editor.getImageTranslationY();
                imageScale = editor.getImageScale();
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
                        switch (editor.getToolFlag()) {
                            case ToolFlag.PAINT:
                                editor.getToolBufferPool().addTempToolBuffer(new PointBuffer(paint, downX, downY));
                                break;
                            case ToolFlag.ERASER:
                                editor.getToolBufferPool().addTempToolBuffer(new PointBuffer(eraser, downX, downY));
                                break;
                            case ToolFlag.SELECTION:
                                switch (editor.getSelectionFlag()) {
                                    case ToolFlag.SelectionFlag.CUT:
                                    case ToolFlag.SelectionFlag.COPY:
                                        selectionBitmapX = downX - (int)(selectionBitmap.getWidth() * 0.5f);
                                        selectionBitmapY = downY - (int)(selectionBitmap.getHeight() * 0.5f);
                                        // FIXME canvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                                        selected = true;
                                        break;
                                    case ToolFlag.SelectionFlag.CLEAR:
                                    default:
                                        if (downX < 0) {
                                            downX = 0;
                                        }
                                        if (downX >= editor.getToolBufferPool().getCurrentBitmap().getWidth()) {
                                            downX = editor.getToolBufferPool().getCurrentBitmap().getWidth() - 1;
                                        }
                                        if (downY < 0) {
                                            downY = 0;
                                        }
                                        if (downY >= editor.getToolBufferPool().getCurrentBitmap().getHeight()) {
                                            downY = editor.getToolBufferPool().getCurrentBitmap().getHeight() - 1;
                                        }
                                        break;
                                }
                                break;
                            case ToolFlag.COLORIZE:
                                if (downX >=0 && downY >= 0 && downX < editor.getToolBufferPool().getCurrentBitmap().getWidth() && downY < editor.getToolBufferPool().getCurrentBitmap().getHeight()) {
                                    listPalettes.setPaletteColor(listPalettes.getCheckedIndex(), editor.getToolBufferPool().getCurrentBitmap().getPixel(downX, downY));
                                    paint.setColor(editor.getToolBufferPool().getCurrentBitmap().getPixel(downX, downY));
                                    switch (editor.getPaletteFlag()) {
                                        case PaletteFlag.BACKGROUND:
                                            if (listPalettes.getCheckedIndex() == 0) {
                                                setCanvasViewBackgroundColor(editor.getToolBufferPool().getCurrentBitmap().getPixel(downX, downY));
                                            }
                                            else {
                                                flushCanvasBackgroundPaint();
                                                listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                                                        getCanvasBackgroundColor2());
                                                canvasView.invalidate();
                                            }
                                            break;
                                        case PaletteFlag.GRID:
                                            setGridColor(editor.getToolBufferPool().getCurrentBitmap().getPixel(downX, downY));
                                            break;
                                    }
                                }
                                break;
                        }
                        //currentCanvas.save();
                        //currentCanvas.restore();
                        canvasView.invalidate();
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // Record initial distance
                        oldDist = Utils.spacing(event);
                        newDist = oldDist;
                        scaleMode = true;
                        readOnlyMode = true;

                        editor.setSelectionFlag(ToolFlag.SelectionFlag.NONE);
                        selected = false;
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
                            switch (editor.getToolFlag()) {
                                case ToolFlag.PAINT:
                                case ToolFlag.ERASER:
                                    path.lineTo(moveX, moveY);
                                    break;
                                case ToolFlag.SHAPE:
                                    editor.getToolBufferPool().clearTempToolBuffers();
                                    path.reset();
                                    switch (editor.getShapeFlag()) {
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
                                    switch (editor.getSelectionFlag()) {
                                        case ToolFlag.SelectionFlag.CUT:
                                        case ToolFlag.SelectionFlag.COPY:
                                            break;
                                        case ToolFlag.SelectionFlag.CLEAR:
                                        default:
                                            if (moveX < 0) {
                                                moveX = 0;
                                            }
                                            if (moveX >= editor.getToolBufferPool().getCurrentBitmap().getWidth()) {
                                                moveX = editor.getToolBufferPool().getCurrentBitmap().getWidth() - 1;
                                            }
                                            if (moveY < 0) {
                                                moveY = 0;
                                            }
                                            if (moveY >= editor.getToolBufferPool().getCurrentBitmap().getHeight()) {
                                                moveY = editor.getToolBufferPool().getCurrentBitmap().getHeight() - 1;
                                            }
                                            break;
                                    }
                                    selected = true;
                                    break;
                                case ToolFlag.COLORIZE:
                                    if (moveX >=0 && moveY >= 0 && moveX < editor.getToolBufferPool().getCurrentBitmap().getWidth() && moveY < editor.getToolBufferPool().getCurrentBitmap().getHeight()) {
                                        listPalettes.setPaletteColor(listPalettes.getCheckedIndex(), editor.getToolBufferPool().getCurrentBitmap().getPixel(moveX, moveY));
                                        paint.setColor(editor.getToolBufferPool().getCurrentBitmap().getPixel(moveX, moveY));
                                        switch (editor.getPaletteFlag()) {
                                            case PaletteFlag.BACKGROUND:
                                                if (listPalettes.getCheckedIndex() == 0) {
                                                    setCanvasViewBackgroundColor(editor.getToolBufferPool().getCurrentBitmap().getPixel(downX, downY));
                                                } else {
                                                    flushCanvasBackgroundPaint();
                                                    listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                                                            getCanvasBackgroundColor2());
                                                    canvasView.invalidate();
                                                }
                                                break;
                                            case PaletteFlag.GRID:
                                                setGridColor(editor.getToolBufferPool().getCurrentBitmap().getPixel(downX, downY));
                                                break;
                                        }
                                    }
                                    break;
                            }
                            switch (editor.getToolFlag()) {
                                // Draw down point
                                case ToolFlag.PAINT:
                                    editor.getToolBufferPool().addTempToolBuffer(new PointBuffer(paint, downX, downY));
                                case ToolFlag.SHAPE:
                                    editor.getToolBufferPool().addTempToolBuffer(new PaintBuffer(paint, path));
                                    break;
                                case ToolFlag.ERASER:
                                    editor.getToolBufferPool().addTempToolBuffer(new PointBuffer(eraser, downX, downY));
                                    editor.getToolBufferPool().addTempToolBuffer(new PaintBuffer(eraser, path));
                                    break;
                                // Draw selection bmp
                                case ToolFlag.SELECTION:
                                    switch (editor.getSelectionFlag()) {
                                        case ToolFlag.SelectionFlag.CUT:
                                        case ToolFlag.SelectionFlag.COPY:
                                            selectionBitmapX = moveX - (int)(selectionBitmap.getWidth() * 0.5f);
                                            selectionBitmapY = moveY - (int)(selectionBitmap.getHeight() * 0.5f);
                                            //currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                                            break;
                                    }
                                    break;
                            }
                            //currentCanvas.save();
                            //currentCanvas.restore();
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
                            switch (editor.getToolFlag()) {
                                case ToolFlag.PAINT:
                                    editor.getToolBufferPool().addToolBuffer(
                                            new MultiBuffer(new PointBuffer(paint, downX, downY), new PaintBuffer(paint, path)));
                                    break;
                                case ToolFlag.ERASER:
                                    editor.getToolBufferPool().addToolBuffer(
                                            new MultiBuffer(new PointBuffer(eraser, downX, downY), new PaintBuffer(eraser, path)));
                                    break;
                                case ToolFlag.SHAPE:
                                    editor.getToolBufferPool().addToolBuffer(new PaintBuffer(paint, path));
                                    break;
                                case ToolFlag.FILL:
                                    if (downX >=0 && downY >= 0 && downX < editor.getToolBufferPool().getCurrentBitmap().getWidth() && downY < editor.getToolBufferPool().getCurrentBitmap().getHeight()) {
                                        editor.getToolBufferPool().addToolBuffer(new FillBuffer(downX, downY, paint.getColor()));
                                    }
                                    break;
                                case ToolFlag.SELECTION:
                                    switch (editor.getSelectionFlag()) {
                                        case ToolFlag.SelectionFlag.CUT:
                                        case ToolFlag.SelectionFlag.COPY:
                                            // Clear canvas
                                            //currentCanvas.drawPaint(eraser);
                                            // Draw cache bitmap
                                            //currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                                            //currentCanvas.save();
                                            //currentCanvas.restore();
                                            //buildSelectionPopup2();
                                            break;
                                        case ToolFlag.SelectionFlag.CLEAR:
                                        default:
                                            //buildSelectionPopup1();
                                            break;
                                    }
                                    break;
                            }
                            if (editor.getToolFlag() != ToolFlag.SELECTION) {
                                //currentCanvas.save();
                                //currentCanvas.restore();
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
