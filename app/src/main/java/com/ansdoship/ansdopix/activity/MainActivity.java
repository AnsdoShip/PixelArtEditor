package com.ansdoship.ansdopix.activity;

import androidx.annotation.NonNull;
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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.ansdoship.ansdopix.R;
import com.ansdoship.ansdopix.util.ColorUtils;
import com.ansdoship.ansdopix.util.BitmapUtils;
import com.ansdoship.ansdopix.util.DrawUtils;
import com.ansdoship.ansdopix.view.CanvasView;
import com.ansdoship.ansdopix.view.PaletteView;
import com.ansdoship.ansdopix.viewgroup.PaletteGroup;

import java.util.ArrayList;
import java.util.List;

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
    private int paintType;
    private static class PaintType {
        final static int REPLACE = 0;
        final static int OVERRIDE = 1;
    }

    private boolean isPaintRbtnChecked;

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
        flushGridBmp();
    }

    private void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
        flushGridBmp();
    }

    private void setGridColor(int gridColor) {
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
    private String currentPalette;
    private int currentPalettePage;
    private List<Integer> currentPaletteColors;
    private List<Integer> palette1Colors;
    private List<Integer> palette2Colors;
    private List<Integer> palette3Colors;
    private int dialogTempColor;
    private int dialogTempColorH;
    private float dialogTempColorS;

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
        prefEditor.putInt("paint_type", paintType);
        // Grid
        prefEditor.putBoolean("is_draw_grid", drawGrid);
        prefEditor.putInt("grid_width", gridWidth);
        prefEditor.putInt("grid_height", gridHeight);
        prefEditor.putInt("grid_color", gridColor);
        // Palette
        prefEditor.putString("current_palette", currentPalette);
        prefEditor.putInt("current_palette_slot", groupPalettes.getCheckedPalettePosition());
        for (int slot = 0; slot < 10; slot ++) {
            prefEditor.putInt("palette_1" + "color_" + slot, palette1Colors.get(slot));
        }
        for (int slot = 0; slot < 10; slot ++) {
            prefEditor.putInt("palette_2" + "color_" + slot, palette2Colors.get(slot));
        }
        for (int slot = 0; slot < 10; slot ++) {
            prefEditor.putInt("palette_3" + "color_" + slot, palette3Colors.get(slot));
        }
        prefEditor.putInt("current_palette_page", currentPalettePage);
        // File save & load path
        prefEditor.putString("CACHE_PATH", CACHE_PATH);
        prefEditor.putString("IMAGE_PATH", IMAGE_PATH);
        prefEditor.putString("PALETTE_PATH", PALETTE_PATH);
        // Bitmap cache
        BitmapUtils.saveBitmapToBMP(CACHE_PATH + "current_bitmap", currentBmp, true);

        //Apply data
        prefEditor.apply();
    }

    private void loadData() {
        // Image
        imgName = prefData.getString("img_name", "Untitled.png");
        imgScale = prefData.getInt("img_scale", 20);
        imgTranslationX = prefData.getFloat("img_translation_x", 0);
        imgTranslationY = prefData.getFloat("img_translation_y", 0);
        // Paint
        paintWidth = prefData.getInt("paint_width", 1);
        drawFlag = prefData.getInt("draw_flag", DrawFlag.PAINT);
        graphType = prefData.getInt("graph_type", GraphType.LINE);
        paintType = prefData.getInt("paint_type", PaintType.REPLACE);
        // Grid
        drawGrid = prefData.getBoolean("is_draw_grid", true);
        gridWidth = prefData.getInt("grid_width", 1);
        gridHeight = prefData.getInt("grid_height", 1);
        gridColor = prefData.getInt("grid_color", Color.BLACK);
        // Palette
        groupPalettes.checkPosition(prefData.getInt("current_palette_slot", 0));
        currentPaletteColors = new ArrayList<>();
        palette1Colors = new ArrayList<>();
        palette2Colors = new ArrayList<>();
        palette3Colors = new ArrayList<>();
        for (int slot = 0; slot < 10; slot ++) {
            palette1Colors.add(prefData.getInt("palette_1" + "color_" + slot, Color.TRANSPARENT));
        }
        for (int slot = 0; slot < 10; slot ++) {
            palette2Colors.add(prefData.getInt("palette_2" + "color_" + slot, Color.TRANSPARENT));
        }
        for (int slot = 0; slot < 10; slot ++) {
            palette3Colors.add(prefData.getInt("palette_3" + "color_" + slot, Color.TRANSPARENT));
        }
        currentPalettePage = prefData.getInt("current_palette_page", 1);
        switch (currentPalettePage) {
            case 1:
                currentPalette = getString(R.string.palette_1);
                currentPaletteColors = palette1Colors;
                break;
            case 2:
                currentPalette = getString(R.string.palette_2);
                currentPaletteColors = palette2Colors;
                break;
            case 3:
                currentPalette = getString(R.string.palette_3);
                currentPaletteColors = palette3Colors;
                break;
            default:
                break;
        }
        // Save & load path
        CACHE_PATH = getExternalCacheDir() + "/";
        IMAGE_PATH = prefData.getString("IMAGE_PATH", getExternalFilesDir("images") + "/");
        PALETTE_PATH = prefData.getString("PALETTE_PATH", getExternalFilesDir("palettes") + "/");
        // Load bitmap
        currentBmp = BitmapUtils.loadBitmapFromFile(CACHE_PATH + "current_bitmap");
        if(currentBmp == null) {
            currentBmp = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        }
        currentCanvas = new Canvas(currentBmp);
        // Scale mode
        scaleMode = false;
        // Is RadioButton checked
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
    private TextView tvPaletteName;
    private PaletteGroup groupPalettes;
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
        bgPaint = new Paint();
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
        String [] items = {
                getString(R.string.replace),
                getString(R.string.override)
        };
        int checkedItem = -1;
        switch (paintType) {
            case PaintType.REPLACE:
                checkedItem = 0;
                break;
            case PaintType.OVERRIDE:
                checkedItem = 1;
                break;
        }
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        paintType = PaintType.REPLACE;
                        break;
                    case 1:
                        paintType = PaintType.OVERRIDE;
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
                        rbtnGraph.setText(R.string.line);
                        break;
                    case 1:
                        graphType = GraphType.CIRCLE;
                        rbtnGraph.setText(R.string.circle);
                        break;
                    case 2:
                        graphType = GraphType.OVAL;
                        rbtnGraph.setText(R.string.oval);
                        break;
                    case 3:
                        graphType = GraphType.SQUARE;
                        rbtnGraph.setText(R.string.square);
                        break;
                    case 4:
                        graphType = GraphType.RECT;
                        rbtnGraph.setText(R.string.rect);
                        break;
                }
                rbtnGraph.requestLayout();
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
        int xOffset = upX * imgScale;
        int yOffset = upY * imgScale;
        int gravity;
        if (yOffset + view.getMeasuredHeight() < canvasView.getHeight()) {
            gravity = Gravity.START;
        }
        else {
            gravity = Gravity.END;
            yOffset -= view.getMeasuredHeight();
            yOffset += imgScale;
        }
        if (xOffset + view.getMeasuredWidth() < canvasView.getWidth()) {
            gravity = gravity | Gravity.TOP;
        }
        else {
            gravity = gravity | Gravity.BOTTOM;
            xOffset -= view.getMeasuredWidth();
            xOffset += imgScale;
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
        yOffset += actionBar.getHeight();
        yOffset += getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height",
                "dimen", "android"));
        window.showAtLocation(canvasView, Gravity.START | Gravity.TOP, xOffset, yOffset);
        TextView tvCut = view.findViewById(R.id.tv_cut);
        TextView tvCopy = view.findViewById(R.id.tv_copy);
        TextView tvClear = view.findViewById(R.id.tv_clear);
        TextView tvNone = view.findViewById(R.id.tv_none);
        tvCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionX = Math.min(downX, moveX);
                int selectionY = Math.min(downY, moveY);
                selectionBmpX = selectionX;
                selectionBmpY = selectionY;
                int selectionWidth = Math.abs(downX - moveX) + 1;
                int selectionHeight = Math.abs(downY - moveY) + 1;
                selectionRectF = new RectF(selectionX, selectionY, Math.max(downX, moveX), Math.max(downY, moveY));
                selectionFlag = SelectionFlag.CUT;
                selectionBmp = Bitmap.createBitmap(currentBmp, selectionX, selectionY, selectionWidth, selectionHeight);
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
        tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionX = Math.min(downX, moveX);
                int selectionY = Math.min(downY, moveY);
                selectionBmpX = selectionX;
                selectionBmpY = selectionY;
                int selectionWidth = Math.abs(downX - moveX) + 1;
                int selectionHeight = Math.abs(downY - moveY) + 1;
                selectionRectF = new RectF(selectionX, selectionY, Math.max(downX, moveX), Math.max(downY, moveY));
                selectionFlag = SelectionFlag.COPY;
                selectionBmp = Bitmap.createBitmap(currentBmp, selectionX, selectionY, selectionWidth, selectionHeight);
                window.dismiss();
                buildSelectionPopup2();
            }
        });
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionX = Math.min(downX, moveX);
                int selectionY = Math.min(downY, moveY);
                selectionBmpX = selectionX;
                selectionBmpY = selectionY;
                selectionRectF = new RectF(selectionX, selectionY, Math.max(downX, moveX), Math.max(downY, moveY));
                selectionFlag = SelectionFlag.CLEAR;
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
        tvNone.setOnClickListener(new View.OnClickListener() {
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
        int xOffset = upX * imgScale;
        int yOffset = upY * imgScale;
        int gravity;
        if (yOffset + view.getMeasuredHeight() < canvasView.getHeight()) {
            gravity = Gravity.START;
        }
        else {
            gravity = Gravity.END;
            yOffset -= view.getMeasuredHeight();
            yOffset += imgScale;
        }
        if (xOffset + view.getMeasuredWidth() < canvasView.getWidth()) {
            gravity = gravity | Gravity.TOP;
        }
        else {
            gravity = gravity | Gravity.BOTTOM;
            xOffset -= view.getMeasuredWidth();
            xOffset += imgScale;
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
        yOffset += actionBar.getHeight();
        yOffset += getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height",
                "dimen", "android"));
        window.showAtLocation(canvasView, Gravity.START | Gravity.TOP, xOffset, yOffset);
        TextView tvRotate = view.findViewById(R.id.tv_rotate);
        TextView tvFlip = view.findViewById(R.id.tv_flip);
        TextView tvDone = view.findViewById(R.id.tv_done);
        TextView tvNone = view.findViewById(R.id.tv_none);
        tvRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheBmp = Bitmap.createBitmap(currentBmp);
                selectionBmp = BitmapUtils.rotateBitmap(selectionBmp, 90);
                currentCanvas.drawBitmap(selectionBmp, selectionBmpX, selectionBmpY, bmpPaint);
                currentCanvas.save();
                currentCanvas.restore();
                canvasView.invalidate();
                // Clear canvas
                currentCanvas.drawPaint(eraser);
                // Draw cache bitmap
                currentCanvas.drawBitmap(cacheBmp, 0, 0, bmpPaint);
                currentCanvas.save();
                currentCanvas.restore();
            }
        });
        tvFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheBmp = Bitmap.createBitmap(currentBmp);
                selectionBmp = BitmapUtils.flipBitmapHorizontally(selectionBmp);
                currentCanvas.drawBitmap(selectionBmp, selectionBmpX, selectionBmpY, bmpPaint);
                currentCanvas.save();
                currentCanvas.restore();
                canvasView.invalidate();
                // Clear canvas
                currentCanvas.drawPaint(eraser);
                // Draw cache bitmap
                currentCanvas.drawBitmap(cacheBmp, 0, 0, bmpPaint);
                currentCanvas.save();
                currentCanvas.restore();
            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionFlag = -1;
                selected = false;
                selectionBmpX = moveX - (int)(selectionBmp.getWidth() * 0.5f);
                selectionBmpY = moveY - (int)(selectionBmp.getHeight() * 0.5f);
                currentCanvas.drawBitmap(selectionBmp, selectionBmpX, selectionBmpY, bmpPaint);
                currentCanvas.save();
                currentCanvas.restore();
                canvasView.invalidate();
                window.dismiss();
            }
        });
        tvNone.setOnClickListener(new View.OnClickListener() {
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
        dialogTempColor = currentPaletteColors.get(groupPalettes.getCheckedPalettePosition());
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
        palette.setDisabled(true);
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
                currentPaletteColors.set(groupPalettes.getCheckedPalettePosition(), dialogTempColor);
                paint.setColor(dialogTempColor);
                flushPaletteColors();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        tvPaletteName = findViewById(R.id.tv_palette_name);
        groupPalettes = findViewById(R.id.group_palettes);
        // CanvasView
        canvasView = findViewById(R.id.canvas_view);

        // Load data
        loadData();

        // Init paints
        bgPaint = new Paint();
        bmpPaint = new Paint();
        bmpPaint.setAntiAlias(false);
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

        // Paint width text
        tvPaintWidth.append(": " + paintWidth);

        // Graph text
        switch (graphType) {
            case GraphType.LINE:
                rbtnGraph.setText(R.string.line);
                break;
            case GraphType.CIRCLE:
                rbtnGraph.setText(R.string.circle);
                break;
            case GraphType.OVAL:
                rbtnGraph.setText(R.string.oval);
                break;
            case GraphType.SQUARE:
                rbtnGraph.setText(R.string.square);
                break;
            case GraphType.RECT:
                rbtnGraph.setText(R.string.rect);
                break;
        }

        // Paint width TextView onClickListener
        tvPaintWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildPaintWidthDialog();
            }
        });

        // Default checked RatioButton
        switch (drawFlag) {
            case DrawFlag.PAINT:
                groupTools.check(R.id.rbtn_paint);
                paint.setStrokeCap(Paint.Cap.SQUARE);
                eraser.setStrokeCap(Paint.Cap.SQUARE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                eraser.setStrokeJoin(Paint.Join.ROUND);
                isPaintRbtnChecked = true;
                break;
            case DrawFlag.GRAPH:
                groupTools.check(R.id.rbtn_graph);
                switch (graphType) {
                    case GraphType.CIRCLE:
                    case GraphType.OVAL:
                        paint.setStrokeCap(Paint.Cap.ROUND);
                        eraser.setStrokeCap(Paint.Cap.ROUND);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        eraser.setStrokeJoin(Paint.Join.ROUND);
                        break;
                    case GraphType.LINE:
                    case GraphType.SQUARE:
                    case GraphType.RECT:
                        paint.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        paint.setStrokeJoin(Paint.Join.MITER);
                        eraser.setStrokeJoin(Paint.Join.MITER);
                        break;
                }
                isGraphRbtnChecked = true;
                break;
            case DrawFlag.ERASER:
                groupTools.check(R.id.rbtn_eraser);
                eraser.setStrokeCap(Paint.Cap.SQUARE);
                eraser.setStrokeJoin(Paint.Join.ROUND);
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
                        paint.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        eraser.setStrokeJoin(Paint.Join.ROUND);
                        break;
                    case R.id.rbtn_graph:
                        drawFlag = DrawFlag.GRAPH;
                        switch (graphType) {
                            case GraphType.CIRCLE:
                            case GraphType.OVAL:
                                paint.setStrokeCap(Paint.Cap.ROUND);
                                eraser.setStrokeCap(Paint.Cap.ROUND);
                                paint.setStrokeJoin(Paint.Join.ROUND);
                                eraser.setStrokeJoin(Paint.Join.ROUND);
                                break;
                            case GraphType.LINE:
                            case GraphType.SQUARE:
                            case GraphType.RECT:
                                paint.setStrokeCap(Paint.Cap.SQUARE);
                                eraser.setStrokeCap(Paint.Cap.SQUARE);
                                paint.setStrokeJoin(Paint.Join.MITER);
                                eraser.setStrokeJoin(Paint.Join.MITER);
                                break;
                        }
                        break;
                    case R.id.rbtn_eraser:
                        drawFlag = DrawFlag.ERASER;
                        eraser.setStrokeCap(Paint.Cap.SQUARE);
                        eraser.setStrokeJoin(Paint.Join.ROUND);
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
                if (drawFlag != DrawFlag.PAINT) {
                    isPaintRbtnChecked = false;
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

        rbtnPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaintRbtnChecked) {
                    buildPaintDialog();
                }
                else {
                    isPaintRbtnChecked = true;
                }
            }
        });

        rbtnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGraphRbtnChecked) {
                    buildGraphDialog();
                }
                else {
                    isGraphRbtnChecked = true;
                }
            }
        });

        // Palette name
        tvPaletteName.setText(currentPalette);
        tvPaletteName.requestLayout();

        // Select Palette
        groupPalettes.setOnCheckedChangeListener(new PaletteGroup.OnCheckedChangeListener() {
            @Override
            public void onInitialCheck(PaletteGroup group, int checkedId, int checkedPosition) {
                paint.setColor(currentPaletteColors.get(checkedPosition));
                flushPaletteColors();
            }
            @Override
            public void onCheckedChanged(PaletteGroup group, int checkedId, int checkedPosition) {
                paint.setColor(currentPaletteColors.get(checkedPosition));
            }
        });
        groupPalettes.setOnDoubleTapListener(new PaletteGroup.OnDoubleTapListener() {
            @Override
            public void onDoubleTap(PaletteGroup group, int checkedId, int checkedPosition) {
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
                if(drawGrid) {
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
                    RectF selectionRectF;
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
                                if (paintType == PaintType.REPLACE) {
                                    currentCanvas.drawPoint(downX, downY, eraser);
                                }
                                currentCanvas.drawPoint(downX, downY, paint);
                                break;
                            case DrawFlag.ERASER:
                                currentCanvas.drawPoint(downX, downY, eraser);
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
                                    currentPaletteColors.set(groupPalettes.getCheckedPalettePosition(), currentBmp.getPixel(downX, downY));
                                    paint.setColor(currentBmp.getPixel(downX, downY));
                                    flushPaletteColors();
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
                                            float circleX = (circleRight + circleLeft) * 0.5f;
                                            float circleY = (circleTop + circleBottom) * 0.5f;
                                            path.addCircle(circleX, circleY, circleDiameter * 0.5f, Path.Direction.CW);
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
                                    if (downX >=0 && downY >= 0 && downX < currentBmp.getWidth() && downY < currentBmp.getHeight()) {
                                        currentPaletteColors.set(groupPalettes.getCheckedPalettePosition(), currentBmp.getPixel(moveX, moveY));
                                        paint.setColor(currentBmp.getPixel(moveX, moveY));
                                        flushPaletteColors();
                                    }
                                    break;
                            }
                            // Clear canvas
                            currentCanvas.drawPaint(eraser);
                            // Draw cache bitmap
                            currentCanvas.drawBitmap(cacheBmp, 0, 0, bmpPaint);
                            currentCanvas.save();
                            currentCanvas.restore();
                            // Draw path
                            if (drawFlag != DrawFlag.ERASER) {
                                if (paintType == PaintType.REPLACE) {
                                    currentCanvas.drawPath(path, eraser);
                                }
                                currentCanvas.drawPath(path, paint);
                            }
                            else {
                                currentCanvas.drawPath(path, eraser);
                            }
                            switch (drawFlag) {
                                // Draw down point
                                case DrawFlag.PAINT:
                                    if (paintType == PaintType.REPLACE) {
                                        currentCanvas.drawPoint(downX, downY, eraser);
                                    }
                                    currentCanvas.drawPoint(downX, downY, paint);
                                    break;
                                case DrawFlag.ERASER:
                                    currentCanvas.drawPoint(downX, downY, eraser);
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
                                        DrawUtils.fill(currentBmp, downX, downY, paint.getColor());
                                    }
                                    break;
                                case DrawFlag.SELECTION:
                                    switch (selectionFlag) {
                                        case SelectionFlag.CUT:
                                        case SelectionFlag.COPY:
                                            // Clear canvas
                                            currentCanvas.drawPaint(eraser);
                                            // Draw cache bitmap
                                            currentCanvas.drawBitmap(cacheBmp, 0, 0, bmpPaint);
                                            currentCanvas.save();
                                            currentCanvas.restore();
                                            buildSelectionPopup2();
                                            break;
                                        case SelectionFlag.CLEAR:
                                        default:
                                            buildSelectionPopup1();
                                            break;
                                    }
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

    private void flushPaletteColors() {
        for (int i = 0; i < currentPaletteColors.size(); i ++) {
            groupPalettes.getPalettes().get(i).setPaletteColor(currentPaletteColors.get(i));
        }
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

    // Set ActionBar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem itemGrid = menu.findItem(R.id.item_grid);
        if (drawGrid) {
            itemGrid.setTitle(getString(R.string.hide_grid));
        }
        else {
            itemGrid.setTitle(getString(R.string.show_grid));
        }
        return true;
    }

    // ActionBar menu listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_undo:
                return true;
            case R.id.item_redo:
                return true;
            case R.id.item_load:
                return true;
            case R.id.item_save:
                return true;
            case R.id.item_grid:
                setDrawGrid(!drawGrid);
                if (drawGrid) {
                    item.setTitle(getString(R.string.hide_grid));
                }
                else {
                    item.setTitle(getString(R.string.show_grid));
                }
                return true;
            case R.id.item_settings:
                return true;
            case R.id.item_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Recycle bitmaps
        if (currentBmp != null) {
            if(!currentBmp.isRecycled()) {
                currentBmp.recycle();
            }
        }
        if (cacheBmp != null) {
            if(!cacheBmp.isRecycled()) {
                cacheBmp.recycle();
            }
        }
        if (gridBmp != null) {
            if(!gridBmp.isRecycled()) {
                gridBmp.recycle();
            }
        }
        if (bgBmp != null) {
            if(!bgBmp.isRecycled()) {
                bgBmp.recycle();
            }
        }
        if (selectionBmp != null) {
            if(!selectionBmp.isRecycled()) {
                selectionBmp.recycle();
            }
        }
    }
}
