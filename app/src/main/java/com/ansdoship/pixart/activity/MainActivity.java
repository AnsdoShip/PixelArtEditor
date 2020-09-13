package com.ansdoship.pixart.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
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
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.ansdoship.pixart.R;
import com.ansdoship.pixart.util.ColorUtils;
import com.ansdoship.pixart.util.BitmapUtils;
import com.ansdoship.pixart.util.DrawCache;
import com.ansdoship.pixart.util.DrawUtils;
import com.ansdoship.pixart.util.GraphCache;
import com.ansdoship.pixart.util.PaintCache;
import com.ansdoship.pixart.util.SelectionCache;
import com.ansdoship.pixart.view.CanvasView;
import com.ansdoship.pixart.view.CheckedImageView;
import com.ansdoship.pixart.view.PaletteView;
import com.ansdoship.pixart.viewgroup.CheckedImageGroup;
import com.ansdoship.pixart.viewgroup.PaletteList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // SharedPreferences
    private SharedPreferences prefData;
    private SharedPreferences.Editor prefEditor;
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
        prefEditor.putBoolean("is_draw_grid", drawGrid);
        prefEditor.putInt("grid_width", gridWidth);
        prefEditor.putInt("grid_height", gridHeight);
        prefEditor.putInt("grid_color", gridColor);
        // Palette

        // File save & load path
        prefEditor.putString("CACHE_PATH", CACHE_PATH);
        prefEditor.putString("IMAGE_PATH", IMAGE_PATH);
        prefEditor.putString("PALETTE_PATH", PALETTE_PATH);
        // Bitmap cache
        BitmapUtils.saveBitmapToBMP(CACHE_PATH + "current_bitmap", currentBitmap, true);

        //Apply data
        prefEditor.apply();

    }
    private void loadData() {

        // Image
        tvImageName.setText(prefData.getString("image_name", "Untitled.png"));
        imageScale = prefData.getInt("image_scale", 20);
        imageTranslationX = prefData.getFloat("image_translation_x", 0);
        imageTranslationY = prefData.getFloat("image_translation_y", 0);

        // Flags
        drawFlag = prefData.getInt("draw_flag", DrawCache.DrawFlag.PAINT);
        graphFlag = prefData.getInt("graph_flag", GraphCache.GraphFlag.LINE);
        paintFlag = prefData.getInt("paint_flag", PaintCache.PaintFlag.REPLACE);
        selectionFlag = -1;

        // Paint
        paintWidth = prefData.getInt("paint_width", 1);

        // Grid
        drawGrid = prefData.getBoolean("is_draw_grid", true);
        gridWidth = prefData.getInt("grid_width", 1);
        gridHeight = prefData.getInt("grid_height", 1);
        gridColor = prefData.getInt("grid_color", Color.BLACK);

        // Palette

        // File save & load path
        CACHE_PATH = getExternalCacheDir() + "/";
        IMAGE_PATH = prefData.getString("IMAGE_PATH", getExternalFilesDir("images") + "/");
        PALETTE_PATH = prefData.getString("PALETTE_PATH", getExternalFilesDir("palettes") + "/");

        // Load bitmap
        currentBitmap = BitmapUtils.loadBitmapFromFile(CACHE_PATH + "current_bitmap");
        if(currentBitmap == null) {
            currentBitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        }
        currentCanvas = new Canvas(currentBitmap);

        // Scale mode
        scaleMode = false;

    }

    // Image
    private int imageScale;
    private void setImageScale(int scale) {
        if(scale >= 1 && scale <= 64) {
            imageScale = scale;
            selectionPaint1.setStrokeWidth(imageScale * 0.5f + 0.5f);
            selectionPaint2.setStrokeWidth(imageScale * 0.25f + 0.25f);
            flushBackgroundBitmap();
            flushGridBitmap();
        }
    }
    private float imageTranslationX;
    private float imageTranslationY;
    private int getBackgroundImageScale() {
        int scale = 0;
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

    // Paint
    private int paintWidth;
    private void setPaintWidth(int width) {
        paintWidth = width;
        paint.setStrokeWidth(width);
        eraser.setStrokeWidth(width);
    }

    // Flags
    private int drawFlag;
    private int graphFlag;
    private int paintFlag;
    private int selectionFlag;

    // Selection
    private boolean selected;
    private int selectionBitmapX;
    private int selectionBitmapY;
    private RectF selectionRectF;

    // Grid
    private boolean drawGrid;
    private int gridWidth;
    private int gridHeight;
    private int gridColor;

    private void setDrawGrid(boolean isDrawGrid) {
        drawGrid = isDrawGrid;
        canvasView.invalidate();
    }

    private void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
        flushGridBitmap();
    }

    private void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
        flushGridBitmap();
    }

    private void setGridColor(int gridColor) {
        this.gridColor = gridColor;
        flushGridBitmap();
    }

    private void flushGridBitmap() {
        int width = currentBitmap.getWidth();
        int height = currentBitmap.getHeight();
        gridBitmap = Bitmap.createBitmap(gridWidth * imageScale, gridHeight *imageScale,
                Bitmap.Config.ARGB_8888);
        gridCanvas = new Canvas(gridBitmap);
        gridPaint = new Paint();
        gridPaint.setColor(gridColor);
        gridPaint.setStrokeWidth(1);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridCanvas.drawRect(0, 0, gridBitmap.getWidth(), gridBitmap.getHeight(), gridPaint);
        BitmapShader gridShader = new BitmapShader(gridBitmap, BitmapShader.TileMode.REPEAT,
                BitmapShader.TileMode.REPEAT);
        gridPaint.setShader(gridShader);
        gridBitmap = Bitmap.createBitmap(width * imageScale, height * imageScale,
                Bitmap.Config.ARGB_8888);
        gridCanvas = new Canvas(gridBitmap);
        gridCanvas.drawPaint(gridPaint);
    }

    // Palette
    private int dialogTempColor;
    private int dialogTempColorH;
    private float dialogTempColorS;

    // File save & load path
    private String CACHE_PATH;
    private String IMAGE_PATH;
    private String PALETTE_PATH;

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
    private void flushBackgroundBitmap() {
        backgroundBitmap = Bitmap.createBitmap(new int[] {Color.LTGRAY, Color.GRAY, Color.GRAY, Color.LTGRAY},
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

    // Dialogs
    private void buildPaintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String [] items = {
                getString(R.string.replace),
                getString(R.string.override)
        };
        int checkedItem = -1;
        switch (paintFlag) {
            case PaintCache.PaintFlag.REPLACE:
                checkedItem = 0;
                break;
            case PaintCache.PaintFlag.OVERRIDE:
                checkedItem = 1;
                break;
        }
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        paintFlag = PaintCache.PaintFlag.REPLACE;
                        break;
                    case 1:
                        paintFlag = PaintCache.PaintFlag.OVERRIDE;
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    @SuppressLint("SetTextI18n")
    private void buildPaintWidthDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_paint_width, null);
        SeekBar barPaintWidthValue = view.findViewById(R.id.bar_paint_width_value);
        final TextView tvPaintWidthValue = view.findViewById(R.id.tv_paint_width_value);
        barPaintWidthValue.setProgress(paintWidth - 1);
        tvPaintWidthValue.setText(Integer.toString(paintWidth));
        barPaintWidthValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setPaintWidth(progress + 1);
                tvPaintWidthValue.setText(Integer.toString(paintWidth));
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
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                tvPaintWidth.setText(R.string.paint_width);
                tvPaintWidth.append(": " + paintWidth);
                tvPaintWidth.requestLayout();
            }
        });
        builder.create().show();
    }
    private void buildGraphDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String [] items = {
                getString(R.string.line),
                getString(R.string.circle),
                getString(R.string.oval),
                getString(R.string.square),
                getString(R.string.rect)
        };
        int checkedItem = -1;
        switch (graphFlag) {
            case GraphCache.GraphFlag.LINE:
                checkedItem = 0;
                break;
            case GraphCache.GraphFlag.CIRCLE:
                checkedItem = 1;
                break;
            case GraphCache.GraphFlag.OVAL:
                checkedItem = 2;
                break;
            case GraphCache.GraphFlag.SQUARE:
                checkedItem = 3;
                break;
            case GraphCache.GraphFlag.RECTANGLE:
                checkedItem = 4;
                break;
        }
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        graphFlag = GraphCache.GraphFlag.LINE;
                        imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_line_24, getTheme()));
                        break;
                    case 1:
                        graphFlag = GraphCache.GraphFlag.CIRCLE;
                        imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_24, getTheme()));
                        break;
                    case 2:
                        graphFlag = GraphCache.GraphFlag.OVAL;
                        imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_oval_24, getTheme()));
                        break;
                    case 3:
                        graphFlag = GraphCache.GraphFlag.SQUARE;
                        imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_square_24, getTheme()));
                        break;
                    case 4:
                        graphFlag = GraphCache.GraphFlag.RECTANGLE;
                        imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rectangle_24, getTheme()));
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void buildSelectionPopup1() {
        selectionFlag = -1;
        View view = View.inflate(this, R.layout.popup_selection_1, null);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final PopupWindow window = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        int xOffset = upX * imageScale;
        int yOffset = upY * imageScale;
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
        TypedArray typedArray = obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
        yOffset += typedArray.getDimension(0, 0);
        typedArray.recycle();
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
                int selectionX = Math.min(downX, moveX);
                int selectionY = Math.min(downY, moveY);
                selectionBitmapX = selectionX;
                selectionBitmapY = selectionY;
                int selectionWidth = Math.abs(downX - moveX) + 1;
                int selectionHeight = Math.abs(downY - moveY) + 1;
                selectionRectF = new RectF(selectionX, selectionY, Math.max(downX, moveX), Math.max(downY, moveY));
                selectionFlag = SelectionCache.SelectionFlag.CUT;
                selectionBitmap = Bitmap.createBitmap(currentBitmap, selectionX, selectionY, selectionWidth, selectionHeight);
                eraser.setStyle(Paint.Style.FILL_AND_STROKE);
                eraser.setStrokeJoin(Paint.Join.MITER);
                currentCanvas.drawRect(selectionRectF, eraser);
                currentCanvas.save();
                currentCanvas.restore();
                eraser.setStyle(Paint.Style.STROKE);
                eraser.setStrokeJoin(Paint.Join.ROUND);
                window.dismiss();
                buildSelectionPopup2();
            }
        });
        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionX = Math.min(downX, moveX);
                int selectionY = Math.min(downY, moveY);
                selectionBitmapX = selectionX;
                selectionBitmapY = selectionY;
                int selectionWidth = Math.abs(downX - moveX) + 1;
                int selectionHeight = Math.abs(downY - moveY) + 1;
                selectionRectF = new RectF(selectionX, selectionY, Math.max(downX, moveX), Math.max(downY, moveY));
                selectionFlag = SelectionCache.SelectionFlag.COPY;
                selectionBitmap = Bitmap.createBitmap(currentBitmap, selectionX, selectionY, selectionWidth, selectionHeight);
                window.dismiss();
                buildSelectionPopup2();
            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionX = Math.min(downX, moveX);
                int selectionY = Math.min(downY, moveY);
                selectionBitmapX = selectionX;
                selectionBitmapY = selectionY;
                selectionRectF = new RectF(selectionX, selectionY, Math.max(downX, moveX), Math.max(downY, moveY));
                selectionFlag = SelectionCache.SelectionFlag.CLEAR;
                selected = false;
                eraser.setStyle(Paint.Style.FILL_AND_STROKE);
                eraser.setStrokeJoin(Paint.Join.MITER);
                currentCanvas.drawRect(selectionRectF, eraser);
                currentCanvas.save();
                currentCanvas.restore();
                eraser.setStyle(Paint.Style.STROKE);
                eraser.setStrokeJoin(Paint.Join.ROUND);
                canvasView.invalidate();
                window.dismiss();
            }
        });
        imgNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = false;
                canvasView.invalidate();
                window.dismiss();
            }
        });
    }
    private void buildSelectionPopup2() {
        View view = View.inflate(this, R.layout.popup_selection_2, null);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final PopupWindow window = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        int xOffset = upX * imageScale;
        int yOffset = upY * imageScale;
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
        TypedArray typedArray = obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
        yOffset += typedArray.getDimension(0, 0);
        typedArray.recycle();
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
                cacheBitmap = Bitmap.createBitmap(currentBitmap);
                BitmapUtils.rotateBitmap(selectionBitmap, -90);
                currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                currentCanvas.save();
                currentCanvas.restore();
                canvasView.invalidate();
                // Clear canvas
                currentCanvas.drawPaint(eraser);
                // Draw cache bitmap
                currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                currentCanvas.save();
                currentCanvas.restore();
            }
        });
        imgRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheBitmap = Bitmap.createBitmap(currentBitmap);
                BitmapUtils.rotateBitmap(selectionBitmap, 90);
                currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                currentCanvas.save();
                currentCanvas.restore();
                canvasView.invalidate();
                // Clear canvas
                currentCanvas.drawPaint(eraser);
                // Draw cache bitmap
                currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                currentCanvas.save();
                currentCanvas.restore();
            }
        });
        imgFlipHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheBitmap = Bitmap.createBitmap(currentBitmap);
                BitmapUtils.flipBitmapHorizontally(selectionBitmap);
                currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                currentCanvas.save();
                currentCanvas.restore();
                canvasView.invalidate();
                // Clear canvas
                currentCanvas.drawPaint(eraser);
                // Draw cache bitmap
                currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                currentCanvas.save();
                currentCanvas.restore();
            }
        });
        imgFlipVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheBitmap = Bitmap.createBitmap(currentBitmap);
                BitmapUtils.flipBitmapVertically(selectionBitmap);
                currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                currentCanvas.save();
                currentCanvas.restore();
                canvasView.invalidate();
                // Clear canvas
                currentCanvas.drawPaint(eraser);
                // Draw cache bitmap
                currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                currentCanvas.save();
                currentCanvas.restore();
            }
        });
        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionFlag = -1;
                selected = false;
                selectionBitmapX = moveX - (int)(selectionBitmap.getWidth() * 0.5f);
                selectionBitmapY = moveY - (int)(selectionBitmap.getHeight() * 0.5f);
                currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                currentCanvas.save();
                currentCanvas.restore();
                canvasView.invalidate();
                window.dismiss();
            }
        });
        imgNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionFlag = -1;
                selected = false;
                canvasView.invalidate();
                window.dismiss();
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void buildPaletteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_palette, null);
        TabHost tabHost = view.findViewById(R.id.tabhost_palette);
        tabHost.setup();
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
        final TextView tvPaletteColorValue = view.findViewById(R.id.tv_palette_color_value);
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
        // dialogTempColor = currentPaletteColors.get(groupPalettes.getCheckedPalettePosition());
        dialogTempColorH = (int) ColorUtils.hue(dialogTempColor);
        dialogTempColorS = ColorUtils.saturation(dialogTempColor);

        tvPaletteColorValue.setText(ColorUtils.colorToHexString(dialogTempColor));
        tvColorA.setText("A: " + Color.alpha(dialogTempColor));
        tvColorR.setText("R: " + Color.red(dialogTempColor));
        tvColorG.setText("G: " + Color.green(dialogTempColor));
        tvColorB.setText("B: " + Color.blue(dialogTempColor));
        tvColorH.setText("H: " + (int) ColorUtils.hue(dialogTempColor));
        tvColorS.setText("S: " + (int) (ColorUtils.saturation(dialogTempColor) * 100));
        tvColorV.setText("V: " + (int) (ColorUtils.value(dialogTempColor) * 100));
        palette.setPaletteColor(dialogTempColor);
        barColorA.setProgress(Color.alpha(dialogTempColor));
        barColorR.setProgress(Color.red(dialogTempColor));
        barColorG.setProgress(Color.green(dialogTempColor));
        barColorB.setProgress(Color.blue(dialogTempColor));
        barColorH.setProgress((int) ColorUtils.hue(dialogTempColor));
        barColorS.setProgress((int) ColorUtils.saturation(dialogTempColor) * 100);
        barColorV.setProgress((int) ColorUtils.value(dialogTempColor) * 100);
        barColorA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorA.setText("A: " + progress);
                dialogTempColor = ColorUtils.setAlpha(dialogTempColor, progress);
                tvPaletteColorValue.setText(ColorUtils.colorToHexString(dialogTempColor));
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
                    dialogTempColor = ColorUtils.setRed(dialogTempColor, progress);
                    tvPaletteColorValue.setText(ColorUtils.colorToHexString(dialogTempColor));
                    barColorH.setProgress((int) ColorUtils.hue(dialogTempColor));
                    barColorS.setProgress((int) ColorUtils.saturation(dialogTempColor) * 100);
                    barColorV.setProgress((int) ColorUtils.value(dialogTempColor) * 100);
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
                    dialogTempColor = ColorUtils.setGreen(dialogTempColor, progress);
                    tvPaletteColorValue.setText(ColorUtils.colorToHexString(dialogTempColor));
                    barColorH.setProgress((int) ColorUtils.hue(dialogTempColor));
                    barColorS.setProgress((int) ColorUtils.saturation(dialogTempColor) * 100);
                    barColorV.setProgress((int) ColorUtils.value(dialogTempColor) * 100);
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
                    dialogTempColor = ColorUtils.setBlue(dialogTempColor, progress);
                    tvPaletteColorValue.setText(ColorUtils.colorToHexString(dialogTempColor));
                    barColorH.setProgress((int) ColorUtils.hue(dialogTempColor));
                    barColorS.setProgress((int) ColorUtils.saturation(dialogTempColor) * 100);
                    barColorV.setProgress((int) ColorUtils.value(dialogTempColor) * 100);
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
                    dialogTempColor = ColorUtils.setHue(dialogTempColor, progress);
                    tvPaletteColorValue.setText(ColorUtils.colorToHexString(dialogTempColor));
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
                    dialogTempColor = ColorUtils.setSaturation(dialogTempColor, progress * 0.01f);
                    dialogTempColor = ColorUtils.setHue(dialogTempColor, dialogTempColorH);
                    tvPaletteColorValue.setText(ColorUtils.colorToHexString(dialogTempColor));
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
                    dialogTempColor = ColorUtils.setValue(dialogTempColor, progress * 0.01f);
                    dialogTempColor = ColorUtils.setSaturation(dialogTempColor, dialogTempColorS);
                    dialogTempColor = ColorUtils.setHue(dialogTempColor, dialogTempColorH);
                    tvPaletteColorValue.setText(ColorUtils.colorToHexString(dialogTempColor));
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
                //currentPaletteColors.set(groupPalettes.getCheckedPalettePosition(), dialogTempColor);
                paint.setColor(dialogTempColor);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

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

                break;
            case R.id.tv_paint_width:
                buildPaintWidthDialog();
                break;
            case R.id.img_palette:

                break;
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "CommitPrefEdits", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Window format
        getWindow().setFormat(PixelFormat.RGBA_8888);

        // Init SharedPreferences
        prefData = getSharedPreferences("app_data", MODE_PRIVATE);
        prefEditor = prefData.edit();

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

        // Init bmp
        flushBackgroundBitmap();
        flushGridBitmap();

        // Init matrix
        matrix = new Matrix();

        // Paint width text
        tvPaintWidth.setText(Integer.toString(paintWidth));

        // Graph ImageButton image
        switch (graphFlag) {
            case GraphCache.GraphFlag.LINE:
                //imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_line_24, getTheme()));
                break;
            case GraphCache.GraphFlag.CIRCLE:
                //imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_24, getTheme()));
                break;
            case GraphCache.GraphFlag.OVAL:
                //imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_oval_24, getTheme()));
                break;
            case GraphCache.GraphFlag.SQUARE:
                //imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_square_24, getTheme()));
                break;
            case GraphCache.GraphFlag.RECTANGLE:
                //imgGraph.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_rectangle_24, getTheme()));
                break;
        }

        // Select tool
        groupTools.setOnCheckedChangeListener(new CheckedImageGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckedImageGroup group, int checkedId, int checkedIndex) {
                switch (checkedId) {
                    case R.id.img_paint:
                        drawFlag = DrawCache.DrawFlag.PAINT;
                        paint.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        eraser.setStrokeJoin(Paint.Join.ROUND);
                        break;
                    case R.id.img_graph:
                        drawFlag = DrawCache.DrawFlag.GRAPH;
                        switch (graphFlag) {
                            case GraphCache.GraphFlag.CIRCLE:
                            case GraphCache.GraphFlag.OVAL:
                                paint.setStrokeCap(Paint.Cap.ROUND);
                                eraser.setStrokeCap(Paint.Cap.ROUND);
                                paint.setStrokeJoin(Paint.Join.ROUND);
                                eraser.setStrokeJoin(Paint.Join.ROUND);
                                break;
                            case GraphCache.GraphFlag.LINE:
                            case GraphCache.GraphFlag.SQUARE:
                            case GraphCache.GraphFlag.RECTANGLE:
                                paint.setStrokeCap(Paint.Cap.SQUARE);
                                eraser.setStrokeCap(Paint.Cap.SQUARE);
                                paint.setStrokeJoin(Paint.Join.MITER);
                                eraser.setStrokeJoin(Paint.Join.MITER);
                                break;
                        }
                        break;
                    case R.id.img_eraser:
                        drawFlag = DrawCache.DrawFlag.ERASER;
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeJoin(Paint.Join.ROUND);
                        break;
                    case R.id.img_fill:
                        drawFlag = DrawCache.DrawFlag.FILL;
                        break;
                    case R.id.img_selection:
                        drawFlag = DrawCache.DrawFlag.SELECTION;
                        break;
                    case R.id.img_colorize:
                        drawFlag = DrawCache.DrawFlag.COLORIZE;
                        break;
                }
                if (drawFlag != DrawCache.DrawFlag.SELECTION) {
                    selected = false;
                    selectionFlag = -1;
                }
                canvasView.invalidate();
            }
        });
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

        // CheckedImageGroup initial check
        switch (drawFlag) {
            case DrawCache.DrawFlag.PAINT:
                groupTools.check(R.id.img_paint);
                break;
            case DrawCache.DrawFlag.GRAPH:
                groupTools.check(R.id.img_graph);
                break;
            case DrawCache.DrawFlag.ERASER:
                groupTools.check(R.id.img_eraser);
                break;
            case DrawCache.DrawFlag.FILL:
                groupTools.check(R.id.img_fill);
                break;
            case DrawCache.DrawFlag.SELECTION:
                groupTools.check(R.id.img_selection);
                break;
            case DrawCache.DrawFlag.COLORIZE:
                groupTools.check(R.id.img_colorize);
                break;
        }

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
                buildPaletteDialog();
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
                matrix.setTranslate(imageTranslationX / imageScale, imageTranslationY / imageScale);
                matrix.postScale(imageScale, imageScale);
                // Draw background
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
                if (drawFlag == DrawCache.DrawFlag.SELECTION && selected) {
                    int selectionLeft;
                    int selectionTop;
                    int selectionRight;
                    int selectionBottom;
                    RectF selectionRectF;
                    switch (selectionFlag) {
                        case SelectionCache.SelectionFlag.CUT:
                        case SelectionCache.SelectionFlag.COPY:
                            selectionLeft = selectionBitmapX * imageScale + imageScale / 2;
                            selectionTop = selectionBitmapY * imageScale + imageScale / 2;
                            selectionRight = selectionLeft + (selectionBitmap.getWidth() - 1) * imageScale;
                            selectionBottom = selectionTop + (selectionBitmap.getHeight() - 1) * imageScale;
                            break;
                        case SelectionCache.SelectionFlag.CLEAR:
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
                            case DrawCache.DrawFlag.PAINT:
                                if (paintFlag == PaintCache.PaintFlag.REPLACE) {
                                    currentCanvas.drawPoint(downX, downY, eraser);
                                }
                                currentCanvas.drawPoint(downX, downY, paint);
                                break;
                            case DrawCache.DrawFlag.ERASER:
                                currentCanvas.drawPoint(downX, downY, eraser);
                                break;
                            case DrawCache.DrawFlag.SELECTION:
                                switch (selectionFlag) {
                                    case SelectionCache.SelectionFlag.CUT:
                                    case SelectionCache.SelectionFlag.COPY:
                                        selectionBitmapX = downX - (int)(selectionBitmap.getWidth() * 0.5f);
                                        selectionBitmapY = downY - (int)(selectionBitmap.getHeight() * 0.5f);
                                        currentCanvas.drawBitmap(selectionBitmap, selectionBitmapX, selectionBitmapY, bitmapPaint);
                                        selected = true;
                                        break;
                                    case SelectionCache.SelectionFlag.CLEAR:
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
                            case DrawCache.DrawFlag.COLORIZE:
                                if (downX >=0 && downY >= 0 && downX < currentBitmap.getWidth() && downY < currentBitmap.getHeight()) {
                                    //currentPaletteColors.set(groupPalettes.getCheckedPalettePosition(), currentBmp.getPixel(downX, downY));
                                    paint.setColor(currentBitmap.getPixel(downX, downY));
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
                        currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
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
                                case DrawCache.DrawFlag.PAINT:
                                case DrawCache.DrawFlag.ERASER:
                                    path.lineTo(moveX, moveY);
                                    break;
                                case DrawCache.DrawFlag.GRAPH:
                                    path.reset();
                                    switch (graphFlag) {
                                        case GraphCache.GraphFlag.LINE:
                                            path.moveTo(downX, downY);
                                            path.lineTo(moveX, moveY);
                                            break;
                                        case GraphCache.GraphFlag.CIRCLE:
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
                                        case GraphCache.GraphFlag.OVAL:
                                            int ovalLeft = Math.min(downX, moveX);
                                            int ovalTop = Math.min(downY, moveY);
                                            int ovalRight = Math.max(downX, moveX);
                                            int ovalBottom = Math.max(downY, moveY);
                                            RectF ovalRectF = new RectF(ovalLeft, ovalTop, ovalRight, ovalBottom);
                                            path.addOval(ovalRectF, Path.Direction.CW);
                                            break;
                                        case GraphCache.GraphFlag.SQUARE:
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
                                        case GraphCache.GraphFlag.RECTANGLE:
                                            int rectLeft = Math.min(downX, moveX);
                                            int rectTop = Math.min(downY, moveY);
                                            int rectRight = Math.max(downX, moveX);
                                            int rectBottom = Math.max(downY, moveY);
                                            RectF rectRectF = new RectF(rectLeft, rectTop, rectRight, rectBottom);
                                            path.addRect(rectRectF, Path.Direction.CW);
                                            break;
                                    }
                                    break;
                                case DrawCache.DrawFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case SelectionCache.SelectionFlag.CUT:
                                        case SelectionCache.SelectionFlag.COPY:
                                            break;
                                        case SelectionCache.SelectionFlag.CLEAR:
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
                                case DrawCache.DrawFlag.COLORIZE:
                                    if (downX >=0 && downY >= 0 && downX < currentBitmap.getWidth() && downY < currentBitmap.getHeight()) {
                                        //currentPaletteColors.set(groupPalettes.getCheckedPalettePosition(), currentBmp.getPixel(moveX, moveY));
                                        paint.setColor(currentBitmap.getPixel(moveX, moveY));
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
                            if (drawFlag != DrawCache.DrawFlag.ERASER) {
                                if (paintFlag == PaintCache.PaintFlag.REPLACE) {
                                    currentCanvas.drawPath(path, eraser);
                                }
                                currentCanvas.drawPath(path, paint);
                            }
                            else {
                                currentCanvas.drawPath(path, eraser);
                            }
                            switch (drawFlag) {
                                // Draw down point
                                case DrawCache.DrawFlag.PAINT:
                                    if (paintFlag == PaintCache.PaintFlag.REPLACE) {
                                        currentCanvas.drawPoint(downX, downY, eraser);
                                    }
                                    currentCanvas.drawPoint(downX, downY, paint);
                                    break;
                                case DrawCache.DrawFlag.ERASER:
                                    currentCanvas.drawPoint(downX, downY, eraser);
                                    break;
                                // Draw selection bmp
                                case DrawCache.DrawFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case SelectionCache.SelectionFlag.CUT:
                                        case SelectionCache.SelectionFlag.COPY:
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
                                case DrawCache.DrawFlag.FILL:
                                    if (downX >=0 && downY >= 0 && downX < currentBitmap.getWidth() && downY < currentBitmap.getHeight()) {
                                        DrawUtils.fill(currentBitmap, downX, downY, paint.getColor());
                                    }
                                    break;
                                case DrawCache.DrawFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case SelectionCache.SelectionFlag.CUT:
                                        case SelectionCache.SelectionFlag.COPY:
                                            // Clear canvas
                                            currentCanvas.drawPaint(eraser);
                                            // Draw cache bitmap
                                            currentCanvas.drawBitmap(cacheBitmap, 0, 0, bitmapPaint);
                                            currentCanvas.save();
                                            currentCanvas.restore();
                                            buildSelectionPopup2();
                                            break;
                                        case SelectionCache.SelectionFlag.CLEAR:
                                        default:
                                            buildSelectionPopup1();
                                            break;
                                    }
                                    break;
                            }
                            if (drawFlag != DrawCache.DrawFlag.SELECTION) {
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

}
