package com.ansdoship.pixelarteditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ansdoship.pixelarteditor.editor.Editor;
import com.ansdoship.pixelarteditor.editor.palette.PaletteFlag;
import com.ansdoship.pixelarteditor.editor.ToolFlag;
import com.ansdoship.pixelarteditor.ui.view.CanvasView;
import com.ansdoship.pixelarteditor.ui.view.CheckedImageView;
import com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView.ImageViewListAdapter;
import com.ansdoship.pixelarteditor.ui.viewgroup.CheckedImageGroup;
import com.ansdoship.pixelarteditor.ui.viewgroup.PaletteList;

import java.util.ArrayList;
import java.util.List;

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

    // DIALOGS
    // Paint flag dialog
    private void buildPaintFlagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String [] items = {
                getString(R.string.replace),
                getString(R.string.override)
        };
        int checkedItem = -1;
        switch (editor.getPaintFlag()) {
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
                        editor.setPaintFlag(ToolFlag.PaintFlag.REPLACE);
                        break;
                    case 1:
                        editor.setPaintFlag(ToolFlag.PaintFlag.OVERRIDE);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_paint_width, null);
        SeekBar barPaintWidthValue = view.findViewById(R.id.bar_paint_width_value);
        final TextView tvPaintWidthValue = view.findViewById(R.id.tv_paint_width_value);
        barPaintWidthValue.setProgress(editor.getPaintWidth() - 1);
        tvPaintWidthValue.setText(Integer.toString(editor.getPaintWidth()));
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
                editor.setPaintWidth(dialogTempPaintWidth);
                tvPaintWidth.setText(Integer.toString(editor.getPaintWidth()));
                tvPaintWidth.requestLayout();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        builder.create().show();
    }
    // Shape flag dialog
    private void buildShapeFlagDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        switch (editor.getShapeFlag()) {
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
                        editor.setShapeFlag(ToolFlag.ShapeFlag.LINE);
                        imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                                R.drawable.ic_line_24, getTheme()));
                        break;
                    case 1:
                        editor.setShapeFlag(ToolFlag.ShapeFlag.CIRCLE);
                        imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                                R.drawable.ic_outline_circle_24, getTheme()));
                        break;
                    case 2:
                        editor.setShapeFlag(ToolFlag.ShapeFlag.ELLIPSE);
                        imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                                R.drawable.ic_outline_ellipse_24, getTheme()));
                        break;
                    case 3:
                        editor.setShapeFlag(ToolFlag.ShapeFlag.SQUARE);
                        imgShape.setImageDrawable(VectorDrawableCompat.create(getResources(),
                                R.drawable.ic_outline_square_24, getTheme()));
                        break;
                    case 4:
                        editor.setShapeFlag(ToolFlag.ShapeFlag.RECTANGLE);
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
    // Permission dialog
    private void buildPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_permission_denied);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        builder.create().show();
    }

    // ON CLICK
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.tv_image_name:
                break;
            case R.id.img_grid:
                editor.setGridVisible(!editor.isGridVisible());
                if (editor.isGridVisible()) {
                    imgGrid.setImageDrawable(VectorDrawableCompat.create(getResources(),
                            R.drawable.ic_baseline_grid_off_24, getTheme()));
                }
                else {
                    imgGrid.setImageDrawable(VectorDrawableCompat.create(getResources(),
                            R.drawable.ic_baseline_grid_24, getTheme()));
                }
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
                buildPaintWidthDialog();
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
        if (dataSaved) {
            editor.loadData();
            dataSaved = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataSaved) {
            editor.loadData();
            dataSaved = false;
        }
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

        // Load data
        editor.loadData();
        dataSaved = false;

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
        editor.setPaletteList(listPalettes);
        // CanvasView
        canvasView = findViewById(R.id.canvas_view);
        editor.setCanvasView(canvasView);

        // Set widget hints
        // Set grid visible
        if (editor.isGridVisible()) {
            imgGrid.setImageDrawable(VectorDrawableCompat.create(getResources(),
                    R.drawable.ic_baseline_grid_off_24, getTheme()));
        }
        else {
            imgGrid.setImageDrawable(VectorDrawableCompat.create(getResources(),
                    R.drawable.ic_baseline_grid_24, getTheme()));
        }
        // Set paint width text
        tvPaintWidth.setText(Integer.toString(editor.getPaintWidth()));
        // Set shape ImageButton image
        switch (editor.getShapeFlag()) {
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
                        imgShape.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorTheme));
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
        listPalettes.setPaletteBackgroundColors(editor.getCanvasBackgroundColor1(), editor.getCanvasBackgroundColor2());
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
        editor.flushPaint(listPalettes.getCheckedPaletteColor());
        // Initial select
        listPalettes.setOnCheckedChangeListener(new PaletteList.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(PaletteList list, int checkedIndex) {
                editor.flushPaint(listPalettes.getPaletteColor(checkedIndex));
            }
        });
        // Double tap
        listPalettes.setOnDoubleTapListener(new PaletteList.OnDoubleTapListener() {
            @Override
            public void onDoubleTap(PaletteList list, int checkedIndex) {
                //buildPaletteColorDialog();
            }
        });
    }

}
