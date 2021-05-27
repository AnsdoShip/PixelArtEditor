package com.ansdoship.pixelarteditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.ansdoship.pixelarteditor.app.ApplicationUtils;
import com.ansdoship.pixelarteditor.editor.ColorPalette;
import com.ansdoship.pixelarteditor.editor.buffers.PaintBuffer;
import com.ansdoship.pixelarteditor.editor.buffers.SelectionBuffer;
import com.ansdoship.pixelarteditor.view.PaletteView;
import com.ansdoship.pixelarteditor.viewAdapter.FileListAdapter;
import com.ansdoship.pixelarteditor.viewAdapter.ImageViewListAdapter;
import com.ansdoship.pixelarteditor.viewAdapter.PaletteListAdapter;
import com.ansdoship.pixelarteditor.viewAdapter.TextViewListAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public final class DialogManager {

    private enum Singleton {
        INSTANCE;
        private final DialogManager instance;
        Singleton() {
            instance = new DialogManager();
        }
        public DialogManager getInstance() {
            return instance;
        }
    }

    private DialogManager() {
        context = ApplicationUtils.getApplicationContext();
    }

    public static DialogManager getInstance() {
        return DialogManager.Singleton.INSTANCE.getInstance();
    }

    private final Context context;

    private int dialogTempPaintWidth;
    private void buildPaintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String [] items = {
                getString(R.string.replace),
                getString(R.string.override)
        };
        int checkedItem = -1;
        switch (paintFlag) {
            case PaintBuffer.PaintFlag.REPLACE:
                setPaintFlag(PaintBuffer.PaintFlag.REPLACE);
                checkedItem = 0;
                break;
            case PaintBuffer.PaintFlag.OVERRIDE:
                setPaintFlag(PaintBuffer.PaintFlag.OVERRIDE);
                checkedItem = 1;
                break;
        }
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        setPaintFlag(PaintBuffer.PaintFlag.REPLACE);
                        break;
                    case 1:
                        setPaintFlag(PaintBuffer.PaintFlag.OVERRIDE);
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
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }
    private void buildGraphDialog () {
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
        switch (graphFlag) {
            case ShapeBuffer.GraphFlag.LINE:
                checkedPosition = 0;
                break;
            case ShapeBuffer.GraphFlag.CIRCLE:
                checkedPosition = 1;
                break;
            case ShapeBuffer.GraphFlag.OVAL:
                checkedPosition = 2;
                break;
            case ShapeBuffer.GraphFlag.SQUARE:
                checkedPosition = 3;
                break;
            case ShapeBuffer.GraphFlag.RECTANGLE:
                checkedPosition = 4;
                break;
        }
        ImageViewListAdapter adapter = new ImageViewListAdapter(this, images, checkedPosition);
        adapter.setOnItemClickListener(new ImageViewListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        graphFlag = ShapeBuffer.GraphFlag.LINE;
                        imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_line_24, getTheme()));
                        break;
                    case 1:
                        graphFlag = ShapeBuffer.GraphFlag.CIRCLE;
                        imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_circle_24, getTheme()));
                        break;
                    case 2:
                        graphFlag = ShapeBuffer.GraphFlag.OVAL;
                        imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_ellipse_24, getTheme()));
                        break;
                    case 3:
                        graphFlag = ShapeBuffer.GraphFlag.SQUARE;
                        imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_square_24, getTheme()));
                        break;
                    case 4:
                        graphFlag = ShapeBuffer.GraphFlag.RECTANGLE;
                        imgGraph.setImageDrawable(VectorDrawableCompat.create(getResources(), R.drawable.ic_outline_rectangle_24, getTheme()));
                        break;
                }
                alertDialog.dismiss();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        alertDialog.show();
    }
    private int dialogTempColor;
    private int dialogTempColorH;
    private float dialogTempColorS;
    private float dialogTempColorV;
    @SuppressLint("SetTextI18n")
    private void buildPaletteColorDialog() {
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
        palette.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                getCanvasBackgroundColor2());
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
        dialogTempColor = listPalettes.getPaletteColor(listPalettes.getCheckedIndex());
        dialogTempColorH = (int) ColorUtils.hue(dialogTempColor);
        dialogTempColorS = ColorUtils.saturation(dialogTempColor);
        dialogTempColorV = ColorUtils.value(dialogTempColor);
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
        barColorS.setProgress((int) (ColorUtils.saturation(dialogTempColor) * 100));
        barColorV.setProgress((int) (ColorUtils.value(dialogTempColor) * 100));
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
                    dialogTempColor = ColorUtils.setValue(dialogTempColor, dialogTempColorV);
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
        barColorS.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorS.setText("S: " + progress);
                if (fromUser) {
                    dialogTempColorS = progress * 0.01f;
                    dialogTempColor = ColorUtils.setValue(dialogTempColor, dialogTempColorV);
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
        barColorV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvColorV.setText("V: " + progress);
                if (fromUser) {
                    dialogTempColorV = progress * 0.01f;
                    dialogTempColor = ColorUtils.setValue(dialogTempColor, dialogTempColorV);
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
                listPalettes.setPaletteColor(listPalettes.getCheckedIndex(), dialogTempColor);
                paint.setColor(dialogTempColor);
                switch (paletteId) {
                    case BACKGROUND_PALETTE:
                        if (listPalettes.getCheckedIndex() == 0) {
                            setCanvasViewBackgroundColor(dialogTempColor);
                        } else {
                            flushBackgroundBitmap();
                            listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                                    getCanvasBackgroundColor2());
                            canvasView.invalidate();
                        }
                        break;
                    case GRID_PALETTE:
                        setGridColor(dialogTempColor);
                        break;
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }
    private void buildSelectPaletteDialog () {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        final List<String> externalPalettePaths = FilePathUtils.getAllFilePaths(PALETTE_PATH, new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getAbsolutePath().toLowerCase().endsWith(".palette");
            }
        });
        final List<String> externalPaletteNames = new ArrayList<>();
        for (int i = 0; i < externalPalettePaths.size(); i ++) {
            externalPaletteNames.add(FilePathUtils.getFileNameNoExtension(externalPalettePaths.get(i)));
        }
        int checkedPosition = -1;
        switch (paletteId) {
            case BACKGROUND_PALETTE:
                checkedPosition = 0;
                break;
            case GRID_PALETTE:
                checkedPosition = 1;
                break;
            case BUILTIN_PALETTE:
                checkedPosition = 2;
                break;
            case EXTERNAL_PALETTE:
                checkedPosition = 3;
                checkedPosition += externalPaletteNames.indexOf(paletteName);
                break;
        }
        final PaletteListAdapter adapter = new PaletteListAdapter(this, internalPaletteNames, externalPaletteNames, checkedPosition);
        adapter.setOnItemClickListener(new PaletteListAdapter.OnItemClickListener() {
            @Override
            public void onInternalPaletteClick(int position) {
                if (paletteId == EXTERNAL_PALETTE) {
                    externalPalette.saveToFile(PALETTE_PATH + "/" + paletteName, true);
                }
                int paletteCheckedIndex = listPalettes.getCheckedIndex();
                switch (position) {
                    case 0:
                        paletteId = BACKGROUND_PALETTE;
                        listPalettes.setColorPalette(backgroundPalette, Math.min(paletteCheckedIndex, 2));
                        break;
                    case 1:
                        paletteId = GRID_PALETTE;
                        listPalettes.setColorPalette(gridPalette);
                        break;
                    case 2:
                        paletteId = BUILTIN_PALETTE;
                        listPalettes.setColorPalette(builtinPalette, paletteCheckedIndex);
                        break;
                }
                paint.setColor(listPalettes.getPaletteColor(listPalettes.getCheckedIndex()));
                alertDialog.dismiss();
            }
            @Override
            public void onExternalPaletteClick(int position) {
                if (paletteId == EXTERNAL_PALETTE) {
                    externalPalette.saveToFile(PALETTE_PATH + "/" + paletteName, true);
                }
                externalPalette = ColorPalette.decodeFile(externalPalettePaths.get(position));
                if (externalPalette == null) {
                    alertDialog.dismiss();
                    buildSelectPaletteDialog();
                }
                paletteId = EXTERNAL_PALETTE;
                paletteName = externalPaletteNames.get(position);
                int paletteCheckedIndex = listPalettes.getCheckedIndex();
                listPalettes.setColorPalette(externalPalette, paletteCheckedIndex);
                paint.setColor(listPalettes.getPaletteColor(listPalettes.getCheckedIndex()));
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
                                backgroundPalette = ColorPalette.createColorPalette(new int[]{
                                        Color.DKGRAY,
                                        Color.LTGRAY,
                                        Color.GRAY
                                });
                                break;
                            case 1:
                                gridPalette = ColorPalette.createColorPalette(1, Color.BLACK);
                                break;
                            case 2:
                                builtinPalette = ColorPalette.createColorPalette(new int[]{
                                        Color.RED,
                                        Color.YELLOW,
                                        Color.GREEN,
                                        Color.CYAN,
                                        Color.BLUE,
                                        Color.MAGENTA,
                                        Color.WHITE,
                                        Color.LTGRAY,
                                        Color.GRAY,
                                        Color.DKGRAY,
                                        Color.BLACK,
                                        Color.TRANSPARENT
                                });
                                break;
                        }
                        switch (paletteId) {
                            case BACKGROUND_PALETTE:
                                listPalettes.setColorPalette(backgroundPalette, listPalettes.getCheckedIndex());
                                if (listPalettes.getCheckedIndex() == 0) {
                                    setCanvasViewBackgroundColor(getCanvasViewBackgroundColor());
                                } else {
                                    flushBackgroundBitmap();
                                    listPalettes.setPaletteBackgroundColors(getCanvasBackgroundColor1(),
                                            getCanvasBackgroundColor2());
                                    canvasView.invalidate();
                                }
                                break;
                            case GRID_PALETTE:
                                listPalettes.setColorPalette(gridPalette, listPalettes.getCheckedIndex());
                                setGridColor(getGridColor());
                                break;
                            case BUILTIN_PALETTE:
                                listPalettes.setColorPalette(builtinPalette, listPalettes.getCheckedIndex());
                                break;
                        }
                        paint.setColor(listPalettes.getPaletteColor(listPalettes.getCheckedIndex()));
                    }
                }, null);
            }
            @Override
            public void onRenameClick(int position) {
                buildRenamePaletteDialog(externalPaletteNames.get(position));
                alertDialog.dismiss();
            }
            @Override
            public void onDeleteClick(final int position) {
                buildDeleteFileDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FilePathUtils.deleteFile(externalPalettePaths.get(position));
                        if (externalPaletteNames.get(position).equals(paletteName)) {
                            paletteId = BUILTIN_PALETTE;
                            listPalettes.setColorPalette(builtinPalette, listPalettes.getCheckedIndex());
                        }
                        alertDialog.dismiss();
                        buildSelectPaletteDialog();
                    }
                }, null);
            }
        });
        recyclerView.setAdapter(adapter);
        alertDialog.show();
    }
    private ColorPalette tempPalette;
    private String dialogTempPaletteName;
    private void buildSavePaletteDialog (String etText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        List<String> externalPalettePaths = FilePathUtils.getAllFilePaths(PALETTE_PATH, new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getAbsolutePath().toLowerCase().endsWith(".palette");
            }
        });
        final List<String> externalPaletteNames = new ArrayList<>();
        for (int i = 0; i < externalPalettePaths.size(); i ++) {
            externalPaletteNames.add(FilePathUtils.getFileNameNoExtension(externalPalettePaths.get(i)));
        }
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
                if (!tempPalette.saveToFile(PALETTE_PATH + "/" + dialogTempPaletteName, false)) {
                    ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE)))
                            .hideSoftInputFromWindow(etPaletteName.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    buildSameNameFileDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tempPalette.saveToFile(PALETTE_PATH + "/" + dialogTempPaletteName, true);
                            buildSelectPaletteDialog();
                        }
                    }, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            buildAddPaletteDialog();
                        }
                    });
                    return;
                }
                ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE)))
                        .hideSoftInputFromWindow(etPaletteName.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                buildSelectPaletteDialog();
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
                tempPalette = null;
                ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE)))
                        .hideSoftInputFromWindow(etPaletteName.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                buildAddPaletteDialog();
            }
        });
        builder.create().show();
    }
    private void buildRenamePaletteDialog (@NonNull final String oldName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        etPaletteName.setText(oldName);
        RecyclerView recyclerPalettes = view.findViewById(R.id.recycler_palettes);
        recyclerPalettes.setLayoutManager(new LinearLayoutManager(this));
        final List<String> externalPalettePaths = FilePathUtils.getAllFilePaths(PALETTE_PATH, new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getAbsolutePath().toLowerCase().endsWith(".palette");
            }
        });
        final List<String> externalPaletteNames = new ArrayList<>();
        for (int i = 0; i < externalPalettePaths.size(); i ++) {
            externalPaletteNames.add(FilePathUtils.getFileNameNoExtension(externalPalettePaths.get(i)));
        }
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
                if (!FilePathUtils.renameFile(PALETTE_PATH + "/" + oldName + ".palette",
                        dialogTempPaletteName + ".palette", false)) {
                    ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE)))
                            .hideSoftInputFromWindow(etPaletteName.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    buildSameNameFileDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FilePathUtils.renameFile(PALETTE_PATH + "/" + oldName + ".palette",
                                    dialogTempPaletteName + ".palette", true);
                            buildSelectPaletteDialog();
                        }
                    }, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            buildSelectPaletteDialog();
                        }
                    });
                    return;
                }
                ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE)))
                        .hideSoftInputFromWindow(etPaletteName.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                buildAddPaletteDialog();
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
                ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE)))
                        .hideSoftInputFromWindow(etPaletteName.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                buildSelectPaletteDialog();
            }
        });
        builder.create().show();
    }
    private void buildAddPaletteDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        tempPalette = ColorPalette.createColorPalette(12);
                        break;
                    case 1:
                        switch (paletteId) {
                            case BACKGROUND_PALETTE:
                                tempPalette  = ColorPalette.createColorPalette(backgroundPalette, 12);
                                break;
                            case GRID_PALETTE:
                                tempPalette  = ColorPalette.createColorPalette(gridPalette, 12);
                                break;
                            case BUILTIN_PALETTE:
                                tempPalette  = ColorPalette.createColorPalette(builtinPalette);
                                break;
                            case EXTERNAL_PALETTE:
                                tempPalette  = ColorPalette.createColorPalette(externalPalette);
                                break;
                        }
                        break;
                    case 2:
                        tempPalette = ColorPalette.createColorPalette(12);
                        break;
                }
                buildSavePaletteDialog(null);
                dialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                buildSelectPaletteDialog();
            }
        });
        builder.create().show();
    }
    private void buildResetPaletteDialog(DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_reset_palette);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(cancelListener);
        builder.create().show();
    }
    private void buildLoadDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                        buildLoadImageDialog();
                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }
        });
        builder.create().show();
    }
    private RecyclerView dialogTempRecyclerImageList;
    private TextView dialogTempTvCurrentPath;
    private String dialogTempImageName;
    private void buildLoadImageDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_load_image, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialogTempRecyclerImageList = view.findViewById(R.id.recycler_images);
        dialogTempRecyclerImageList.setLayoutManager(new LinearLayoutManager(this));
        dialogTempTvCurrentPath = view.findViewById(R.id.tv_current_path);
        dialogTempTvCurrentPath.setText(IMAGE_PATH);
        FileListAdapter adapter = flushImageListAdapter(IMAGE_PATH);
        adapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
            @Override
            public void onDirectoryClick(String name, int position) {
                IMAGE_PATH = IMAGE_PATH + "/" + name;
                dialogTempTvCurrentPath.setText(IMAGE_PATH);
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(IMAGE_PATH));
            }
            @Override
            public void onFileClick(String name, int position) {
                currentBitmap = BitmapUtils.loadBitmapFromFile(IMAGE_PATH + "/" + name);
                currentCanvas.setBitmap(currentBitmap);
                canvasView.invalidate();
                dialog.dismiss();
            }
        });
        dialogTempRecyclerImageList.setAdapter(adapter);
        ImageView imgBack = view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMAGE_PATH = FilePathUtils.getParentDirectoryPath(IMAGE_PATH);
                dialogTempTvCurrentPath.setText(IMAGE_PATH);
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(IMAGE_PATH));
            }
        });
        dialog.show();
    }
    private void buildSaveImageDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_save_image, null);
        final TextView tvFormatImage = view.findViewById(R.id.tv_format_image);
        tvFormatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildFormatImageDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        tvFormatImage.setText(imageFormat);
                    }
                });
            }
        });
        final EditText etImageName = view.findViewById(R.id.et_image_name);
        etImageName.setFilters(new InputFilter[] {
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
                boolean saved = false;
                dialogTempImageName = etImageName.getText().toString();
                if (imageFormat.equals(".png")) {
                    saved = BitmapUtils.saveBitmapToPNG(IMAGE_PATH + "/" + dialogTempImageName,
                            currentBitmap, false);
                }
                if (imageFormat.equals(".jpeg")) {
                    saved = BitmapUtils.saveBitmapToJPEG(IMAGE_PATH + "/" + dialogTempImageName,
                            currentBitmap, false, jpegQuality);
                }
                if (imageFormat.equals(".bmp")) {
                    saved = BitmapUtils.saveBitmapToBMP(IMAGE_PATH + "/" + dialogTempImageName,
                            currentBitmap, false);
                }
                if (!saved) {
                    ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE)))
                            .hideSoftInputFromWindow(etImageName.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    buildSameNameFileDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (imageFormat.equals(".png")) {
                                BitmapUtils.saveBitmapToPNG(IMAGE_PATH + "/" + dialogTempImageName,
                                        currentBitmap, true);
                            }
                            if (imageFormat.equals(".jpeg")) {
                                BitmapUtils.saveBitmapToJPEG(IMAGE_PATH + "/" + dialogTempImageName,
                                        currentBitmap, true, jpegQuality);
                            }
                            if (imageFormat.equals(".bmp")) {
                                BitmapUtils.saveBitmapToBMP(IMAGE_PATH + "/" + dialogTempImageName,
                                        currentBitmap, true);
                            }
                        }
                    }, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                        }
                    });
                    return;
                }
                ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE)))
                        .hideSoftInputFromWindow(etImageName.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
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
                ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE)))
                        .hideSoftInputFromWindow(etImageName.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        dialogTempRecyclerImageList = view.findViewById(R.id.recycler_images);
        dialogTempRecyclerImageList.setLayoutManager(new LinearLayoutManager(this));
        dialogTempTvCurrentPath = view.findViewById(R.id.tv_current_path);
        dialogTempTvCurrentPath.setText(IMAGE_PATH);
        FileListAdapter adapter = flushImageListAdapter(IMAGE_PATH);
        adapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
            @Override
            public void onDirectoryClick(String name, int position) {
                IMAGE_PATH = IMAGE_PATH + "/" + name;
                dialogTempTvCurrentPath.setText(IMAGE_PATH);
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(IMAGE_PATH));
            }
            @Override
            public void onFileClick(String name, int position) {
                dialogTempImageName = FilePathUtils.getFileNameNoExtension(name);
                etImageName.setText(dialogTempImageName);
            }
        });
        dialogTempRecyclerImageList.setAdapter(adapter);
        ImageView imgBack = view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMAGE_PATH = FilePathUtils.getParentDirectoryPath(IMAGE_PATH);
                dialogTempTvCurrentPath.setText(IMAGE_PATH);
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(IMAGE_PATH));
            }
        });
        builder.setView(view);
        builder.create().show();
    }
    private FileListAdapter flushImageListAdapter (String newPath) {
        List<String> dirs = FilePathUtils.getAllDirectoryPaths(newPath);
        List<String> files = FilePathUtils.getAllFilePaths(newPath, new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName().toLowerCase();
                return fileName.endsWith(".png") ||
                        fileName.endsWith(".jpg") ||
                        fileName.endsWith(".jpeg") ||
                        fileName.endsWith(".bmp");
            }
        });
        List<String> dirNames = new ArrayList<>();
        for (String dir : dirs) {
            dirNames.add(FilePathUtils.getFileName(dir));
        }
        List<String> fileNames = new ArrayList<>();
        for (String file : files) {
            fileNames.add(FilePathUtils.getFileName(file));
        }
        FileListAdapter adapter = new FileListAdapter(MainActivity.this, dirNames, fileNames,
                VectorDrawableCompat.create(getResources(), R.drawable.ic_baseline_file_24, getTheme()));
        adapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
            @Override
            public void onDirectoryClick(String name, int position) {
                IMAGE_PATH = IMAGE_PATH + "/" + name;
                dialogTempTvCurrentPath.setText(IMAGE_PATH);
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(IMAGE_PATH));
            }

            @Override
            public void onFileClick(String name, int position) {

            }
        });
        return adapter;
    }
    private FileListAdapter flushImageListAdapter (String newPath) {
        List<String> dirs = FileUtils.getAllDirectoryPaths(newPath);
        List<String> files = FileUtils.getAllFilePaths(newPath, new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName().toLowerCase();
                return fileName.endsWith(".png") ||
                        fileName.endsWith(".jpg") ||
                        fileName.endsWith(".jpeg") ||
                        fileName.endsWith(".bmp");
            }
        });
        List<String> dirNames = new ArrayList<>();
        for (String dir : dirs) {
            dirNames.add(FileUtils.getFileName(dir));
        }
        List<String> fileNames = new ArrayList<>();
        for (String file : files) {
            fileNames.add(FileUtils.getFileName(file));
        }
        FileListAdapter adapter = new FileListAdapter(MainActivity.this, dirNames, fileNames,
                VectorDrawableCompat.create(getResources(), R.drawable.ic_baseline_file_24, getTheme()));
        adapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
            @Override
            public void onDirectoryClick(String name, int position) {
                IMAGE_PATH = IMAGE_PATH + "/" + name;
                dialogTempTvCurrentPath.setText(IMAGE_PATH);
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(IMAGE_PATH));
            }

            @Override
            public void onFileClick(String name, int position) {

            }
        });
        return adapter;
    }
    private int jpegQuality = 30;
    private String imageFormat = ".png";
    private void buildFormatImageDialog (DialogInterface.OnCancelListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_format_image, null);
        final TabHost tabHost = view.findViewById(R.id.tabhost_format_image);
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
        if (imageFormat.equals(".png")) {
            tabHost.setCurrentTabByTag("png");
        }
        if (imageFormat.equals(".jpeg")) {
            tabHost.setCurrentTabByTag("jpeg");
        }
        if (imageFormat.equals(".bmp")) {
            tabHost.setCurrentTabByTag("bmp");
        }
        final TextView tvJpegQuality = view.findViewById(R.id.tv_jpeg_quality);
        SeekBar barJpegQuality = view.findViewById(R.id.bar_jpeg_quality);
        tvJpegQuality.setText(R.string.quality);
        tvJpegQuality.append(": ");
        tvJpegQuality.append(Integer.toString(jpegQuality));
        barJpegQuality.setProgress(jpegQuality - 30);
        barJpegQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                jpegQuality = progress + 30;
                tvJpegQuality.setText(R.string.quality);
                tvJpegQuality.append(": ");
                tvJpegQuality.append(Integer.toString(jpegQuality));
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
                switch (tabHost.getCurrentTab()) {
                    case 0:
                        imageFormat = ".png";
                        break;
                    case 1:
                        imageFormat = ".jpeg";
                        break;
                    case 2:
                        imageFormat = ".bmp";
                        break;
                }
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
    private void buildDeleteFileDialog(DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_delete_file);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(cancelListener);
        builder.create().show();
    }
    private void buildSameNameFileDialog(DialogInterface.OnClickListener positiveListener,
                                         DialogInterface.OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.warning_same_name_file);
        builder.setPositiveButton(android.R.string.ok, positiveListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnCancelListener(cancelListener);
        builder.create().show();
    }
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

}
