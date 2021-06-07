package com.ansdoship.pixelarteditor;

import androidx.annotation.NonNull;
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
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.ansdoship.pixelarteditor.editor.Editor;
import com.ansdoship.pixelarteditor.editor.buffer.BufferFlag;
import com.ansdoship.pixelarteditor.editor.buffer.FlipHorizontalBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.FlipVerticalBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.RotateBuffer;
import com.ansdoship.pixelarteditor.editor.buffer.SelectionBuffer;
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
import com.ansdoship.pixelarteditor.ui.view.PaletteView;
import com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView.FileListAdapter;
import com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView.ImageViewListAdapter;
import com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView.PaletteListAdapter;
import com.ansdoship.pixelarteditor.ui.viewAdapter.recycleView.TextViewListAdapter;
import com.ansdoship.pixelarteditor.ui.viewgroup.CheckedImageGroup;
import com.ansdoship.pixelarteditor.ui.viewgroup.PaletteList;
import com.ansdoship.pixelarteditor.util.MathUtils;
import com.ansdoship.pixelarteditor.util.Utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

    // POPUPS
    // Selection popup 1
    private void buildSelectionPopup1() {

        int downX = editor.getDownX();
        int downY = editor.getDownY();
        int moveX = editor.getMoveX();
        int moveY = editor.getMoveY();
        int upX = editor.getUpX();
        int upY = editor.getUpY();
        int imageScale = editor.getImageScale();

        editor.setSelectionFlag(ToolFlag.SelectionFlag.NONE);
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
                editor.setSelectionBitmapSrcX(Math.min(downX, moveX));
                editor.setSelectionBitmapSrcY(Math.min(downY, moveY));
                editor.setSelectionBitmapSrcWidth(Math.abs(downX - moveX) + 1);
                editor.setSelectionBitmapSrcHeight(Math.abs(downY - moveY) + 1);
                editor.setSelectionBitmapDstWidth(editor.getSelectionBitmapSrcWidth());
                editor.setSelectionBitmapDstHeight(editor.getSelectionBitmapSrcHeight());
                editor.setSelectionFlag(ToolFlag.SelectionFlag.CUT);
                window.dismiss();
                buildSelectionPopup2();
            }
        });
        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setSelectionBitmapSrcX(Math.min(downX, moveX));
                editor.setSelectionBitmapSrcY(Math.min(downY, moveY));
                editor.setSelectionBitmapSrcWidth(Math.abs(downX - moveX) + 1);
                editor.setSelectionBitmapSrcHeight(Math.abs(downY - moveY) + 1);
                editor.setSelectionBitmapDstWidth(editor.getSelectionBitmapSrcWidth());
                editor.setSelectionBitmapDstHeight(editor.getSelectionBitmapSrcHeight());
                editor.setSelectionFlag(ToolFlag.SelectionFlag.COPY);
                window.dismiss();
                buildSelectionPopup2();
            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setSelectionBitmapSrcX(Math.min(downX, moveX));
                editor.setSelectionBitmapSrcY(Math.min(downY, moveY));
                editor.setSelectionBitmapSrcWidth(Math.abs(downX - moveX) + 1);
                editor.setSelectionBitmapSrcHeight(Math.abs(downY - moveY) + 1);
                editor.setSelectionBitmapDstWidth(editor.getSelectionBitmapSrcWidth());
                editor.setSelectionBitmapDstHeight(editor.getSelectionBitmapSrcHeight());
                editor.setSelectionFlag(ToolFlag.SelectionFlag.CLEAR);
                editor.getToolBufferPool().addToolBuffer(new SelectionBuffer(
                        editor.getSelectionBitmapSrcX(),
                        editor.getSelectionBitmapSrcY(),
                        editor.getSelectionBitmapSrcWidth(),
                        editor.getSelectionBitmapSrcHeight(),
                        editor.getSelectionBitmapDstX(),
                        editor.getSelectionBitmapDstY(),
                        ToolFlag.SelectionFlag.CLEAR));
                editor.setSelected(false);
                editor.invalidateCanvasView();
                window.dismiss();
            }
        });
        imgNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setSelectionFlag(ToolFlag.SelectionFlag.NONE);
                editor.setSelected(false);
                editor.invalidateCanvasView();
                window.dismiss();
            }
        });
    }
    // Selection popup 2
    private void buildSelectionPopup2() {

        int downX = editor.getDownX();
        int downY = editor.getDownY();
        int moveX = editor.getMoveX();
        int moveY = editor.getMoveY();
        int upX = editor.getUpX();
        int upY = editor.getUpY();
        int imageScale = editor.getImageScale();

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
                int degrees = -90;
                if (editor.getSelectionBitmapRotateBuffer() != null) {
                    degrees = degrees + editor.getSelectionBitmapRotateBuffer().getDegrees();
                }
                if (degrees <= -360) {
                    degrees = degrees + 360;
                }
                editor.swapSelectionBitmapDstWidthHeight();
                editor.setSelectionBitmapRotateBuffer(new RotateBuffer(degrees));
                editor.getToolBufferPool().clearTempToolBuffers();
                editor.getToolBufferPool().addTempToolBuffer(new SelectionBuffer(
                        editor.getSelectionBitmapSrcX(),
                        editor.getSelectionBitmapSrcY(),
                        editor.getSelectionBitmapSrcWidth(),
                        editor.getSelectionBitmapSrcHeight(),
                        editor.getSelectionBitmapDstX(),
                        editor.getSelectionBitmapDstY(),
                        editor.getSelectionFlag(),
                        editor.getSelectionBitmapRotateBuffer(),
                        editor.getSelectionBitmapFlipVerticalBuffer(),
                        editor.getSelectionBitmapFlipHorizontalBuffer()
                ));
                editor.invalidateCanvasView();
            }
        });
        imgRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int degrees = 90;
                if (editor.getSelectionBitmapRotateBuffer() != null) {
                    degrees = degrees + editor.getSelectionBitmapRotateBuffer().getDegrees();
                }
                if (degrees >= 360) {
                    degrees = degrees - 360;
                }
                editor.swapSelectionBitmapDstWidthHeight();
                editor.setSelectionBitmapRotateBuffer(new RotateBuffer(degrees));
                editor.getToolBufferPool().clearTempToolBuffers();
                editor.getToolBufferPool().addTempToolBuffer(new SelectionBuffer(
                        editor.getSelectionBitmapSrcX(),
                        editor.getSelectionBitmapSrcY(),
                        editor.getSelectionBitmapSrcWidth(),
                        editor.getSelectionBitmapSrcHeight(),
                        editor.getSelectionBitmapDstX(),
                        editor.getSelectionBitmapDstY(),
                        editor.getSelectionFlag(),
                        editor.getSelectionBitmapRotateBuffer(),
                        editor.getSelectionBitmapFlipVerticalBuffer(),
                        editor.getSelectionBitmapFlipHorizontalBuffer()
                ));
                editor.invalidateCanvasView();
            }
        });
        imgFlipHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editor.getSelectionBitmapFlipHorizontalBuffer() == null) {
                    editor.setSelectionBitmapFlipHorizontalBuffer(new FlipHorizontalBuffer());
                }
                else {
                    editor.setSelectionBitmapFlipHorizontalBuffer(null);
                }
                editor.getToolBufferPool().clearTempToolBuffers();
                editor.getToolBufferPool().addTempToolBuffer(new SelectionBuffer(
                        editor.getSelectionBitmapSrcX(),
                        editor.getSelectionBitmapSrcY(),
                        editor.getSelectionBitmapSrcWidth(),
                        editor.getSelectionBitmapSrcHeight(),
                        editor.getSelectionBitmapDstX(),
                        editor.getSelectionBitmapDstY(),
                        editor.getSelectionFlag(),
                        editor.getSelectionBitmapRotateBuffer(),
                        editor.getSelectionBitmapFlipVerticalBuffer(),
                        editor.getSelectionBitmapFlipHorizontalBuffer()
                ));
                editor.invalidateCanvasView();
            }
        });
        imgFlipVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editor.getSelectionBitmapFlipVerticalBuffer() == null) {
                    editor.setSelectionBitmapFlipVerticalBuffer(new FlipVerticalBuffer());
                }
                else {
                    editor.setSelectionBitmapFlipVerticalBuffer(null);
                }
                editor.getToolBufferPool().clearTempToolBuffers();
                editor.getToolBufferPool().addTempToolBuffer(new SelectionBuffer(
                        editor.getSelectionBitmapSrcX(),
                        editor.getSelectionBitmapSrcY(),
                        editor.getSelectionBitmapSrcWidth(),
                        editor.getSelectionBitmapSrcHeight(),
                        editor.getSelectionBitmapDstX(),
                        editor.getSelectionBitmapDstY(),
                        editor.getSelectionFlag(),
                        editor.getSelectionBitmapRotateBuffer(),
                        editor.getSelectionBitmapFlipVerticalBuffer(),
                        editor.getSelectionBitmapFlipHorizontalBuffer()
                ));
                editor.invalidateCanvasView();
            }
        });
        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.getToolBufferPool().addToolBuffer(new SelectionBuffer(
                        editor.getSelectionBitmapSrcX(),
                        editor.getSelectionBitmapSrcY(),
                        editor.getSelectionBitmapSrcWidth(),
                        editor.getSelectionBitmapSrcHeight(),
                        editor.getSelectionBitmapDstX(),
                        editor.getSelectionBitmapDstY(),
                        editor.getSelectionFlag(),
                        editor.getSelectionBitmapRotateBuffer(),
                        editor.getSelectionBitmapFlipVerticalBuffer(),
                        editor.getSelectionBitmapFlipHorizontalBuffer()
                ));
                editor.setSelectionFlag(ToolFlag.SelectionFlag.NONE);
                editor.setSelected(false);
                editor.invalidateCanvasView();
                window.dismiss();
            }
        });
        imgNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editor.getSelectionFlag() == ToolFlag.SelectionFlag.CUT) {
                    editor.getToolBufferPool().addToolBuffer(new SelectionBuffer(
                            editor.getSelectionBitmapSrcX(),
                            editor.getSelectionBitmapSrcY(),
                            editor.getSelectionBitmapSrcWidth(),
                            editor.getSelectionBitmapSrcHeight(),
                            editor.getSelectionBitmapDstX(),
                            editor.getSelectionBitmapDstY(),
                            ToolFlag.SelectionFlag.CLEAR));
                }
                editor.setSelectionFlag(ToolFlag.SelectionFlag.NONE);
                editor.setSelected(false);
                editor.invalidateCanvasView();
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
                buildSaveDialog();
                window.dismiss();
            }
        });
        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FIXME HELP DIALOG
            }
        });
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FIXME INFO DIALOG
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
    // Color picker dialog
    private int dialogTempColor;
    private int dialogTempColorH;
    private float dialogTempColorS;
    private float dialogTempColorV;
    @SuppressLint("SetTextI18n")
    private void buildColorPickerDialog() {
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
        palette.setPaletteBackgroundColors(
                editor.getCanvasBackgroundColor1(),
                editor.getCanvasBackgroundColor2());
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
        dialogTempColorH = (int) ColorFactory.hue(dialogTempColor);
        dialogTempColorS = ColorFactory.saturation(dialogTempColor);
        dialogTempColorV = ColorFactory.value(dialogTempColor);
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
                editor.flushPaint(dialogTempColor);
                switch (editor.getPaletteFlag()) {
                    case PaletteFlag.BACKGROUND:
                        if (listPalettes.getCheckedIndex() == 0) {
                            editor.setCanvasViewBackgroundColor(dialogTempColor);
                        }
                        else {
                            editor.flushCanvasBackgroundPaint();
                        }
                        break;
                    case PaletteFlag.GRID:
                        editor.setGridColor(dialogTempColor);
                        break;
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.create().show();
    }
    // Palette flag dialog
    private void buildPaletteFlagDialog () {
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
        List<String> externalPaletteNames = Editor.getExternalPaletteNames();
        int checkedPosition = -1;
        switch (editor.getPaletteFlag()) {
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
                checkedPosition += externalPaletteNames.indexOf(editor.getExternalPaletteName());
                break;
        }
        final PaletteListAdapter adapter = new PaletteListAdapter(this,
                internalPaletteNames, externalPaletteNames, checkedPosition);
        adapter.setOnItemClickListener(new PaletteListAdapter.OnItemClickListener() {
            @Override
            public void onInternalPaletteClick(int position) {
                switch (position) {
                    case 0:
                        editor.setPaletteFlag(PaletteFlag.BACKGROUND);
                        listPalettes.setPalette(editor.getBackgroundPalette());
                        break;
                    case 1:
                        editor.setPaletteFlag(PaletteFlag.GRID);
                        listPalettes.setPalette(editor.getGridPalette());
                        break;
                    case 2:
                        editor.setPaletteFlag(PaletteFlag.INTERNAL);
                        listPalettes.setPalette(editor.getBuiltinPalette());
                        break;
                }
                editor.flushPaint(listPalettes.getCheckedPaletteColor());
                alertDialog.dismiss();
            }
            @Override
            public void onExternalPaletteClick(int position) {
                editor.loadExternalPalette(externalPaletteNames.get(position));
                if (editor.getExternalPalette() == null) {
                    alertDialog.dismiss();
                    buildPaletteFlagDialog();
                }
                editor.setPaletteFlag(PaletteFlag.EXTERNAL);
                listPalettes.setPalette(editor.getExternalPalette());
                editor.flushPaint(listPalettes.getCheckedPaletteColor());
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
                                editor.resetBackgroundPalette();
                                break;
                            case 1:
                                editor.resetGridPalette();
                                break;
                            case 2:
                                editor.resetBuiltinPalette();
                                break;
                        }
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
                        }
                        editor.flushPaint(listPalettes.getCheckedPaletteColor());
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
                        try {
                            FileUtils.forceDelete(new File(Editor.getExternalPalettePathName(
                                    externalPaletteNames.get(position))));
                            if (editor.getPaletteFlag() == PaletteFlag.EXTERNAL) {
                                editor.setPaletteFlag(PaletteFlag.INTERNAL);
                                editor.removeExternalPalette();
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
                        dialogTempPalette = Palette.createPalette(12);
                        break;
                    case 1:
                        switch (editor.getPaletteFlag()) {
                            case PaletteFlag.BACKGROUND:
                                dialogTempPalette  = Palette.createPalette(editor.getBackgroundPalette(), 12);
                                break;
                            case PaletteFlag.GRID:
                                dialogTempPalette  = Palette.createPalette(editor.getGridPalette(), 12);
                                break;
                            case PaletteFlag.INTERNAL:
                                dialogTempPalette  = Palette.createPalette(editor.getBuiltinPalette());
                                break;
                            case PaletteFlag.EXTERNAL:
                                Palette externalPalette = editor.getExternalPalette();
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
                        dialogTempPalette = Palette.createPalette(12);
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
        List<String> externalPaletteNames = Editor.getExternalPaletteNames();
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
                PaletteFactory.encodeFile(dialogTempPalette, Editor.getExternalPalettePathName(dialogTempPaletteName),
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
                                                    Editor.getExternalPalettePathName(dialogTempPaletteName), true);
                                            editor.loadExternalPalette(dialogTempPaletteName);
                                            buildPaletteFlagDialog();
                                        }
                                    }, new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            buildAddPaletteDialog();
                                        }
                                    });
                                    dialogTempPaletteSameName = true;
                                }
                            }
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
        List<String> externalPaletteNames = Editor.getExternalPaletteNames();
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
                File srcFile = new File(Editor.getExternalPalettePathName(oldName));
                File destFile = new File(Editor.getExternalPalettePathName(dialogTempPaletteName));
                if (destFile.exists()) {
                    if (destFile.isFile()) {
                        dialog.dismiss();
                        buildFileSameNameDialog(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    FileUtils.forceDelete(destFile);
                                    FileUtils.moveFile(srcFile, destFile);
                                    if (editor.getPaletteFlag() == PaletteFlag.EXTERNAL) {
                                        editor.removeExternalPalette();
                                        editor.loadExternalPalette(dialogTempPaletteName);
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
                                buildPaletteFlagDialog();
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
    // Load dialog
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
                        buildNewImageDialog();
                        break;
                    case 2:

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
    private void buildLoadImageDialog () {
        dialogTempLoadImage = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        dialogTempTvCurrentPath.setText(editor.getImagePath());
        dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(editor.getImagePath()));
        ImageView imgBack = view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imagePath = editor.getImagePath();
                if (!imagePath.equals("/")) {
                    File imageParentDir = new File(imagePath).getParentFile();
                    if (imageParentDir != null) {
                        if (imageParentDir.canRead() && imageParentDir.canWrite()) {
                            editor.setImagePath(imageParentDir.getAbsolutePath());
                        }
                    }
                }
                dialogTempTvCurrentPath.setText(editor.getImagePath());
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(editor.getImagePath()));
            }
        });
        loadImageDialog.show();
    }
    // New image dialog
    @SuppressLint("SetTextI18n")
    private void buildNewImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_new_image, null);
        EditText etImageWidth = view.findViewById(R.id.et_image_width);
        EditText etImageHeight = view.findViewById(R.id.et_image_height);
        etImageWidth.setText(Integer.toString(editor.getToolBufferPool().getCurrentBitmap().getWidth()));
        etImageHeight.setText(Integer.toString(editor.getToolBufferPool().getCurrentBitmap().getHeight()));
        final int[] imageSize = new int[2];
        etImageWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    imageSize[0] = Integer.parseInt(s.toString());
                    imageSize[0] = MathUtils.clamp(imageSize[0], Editor.IMAGE_WIDTH_MIN, Editor.IMAGE_WIDTH_MAX);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                etImageWidth.removeTextChangedListener(this);
                etImageWidth.setText(Integer.toString(imageSize[0]));
                etImageWidth.setSelection(etImageWidth.getText().length());
                etImageWidth.addTextChangedListener(this);
            }

        });
        etImageHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    imageSize[1] = Integer.parseInt(s.toString());
                    imageSize[1] = MathUtils.clamp(imageSize[1], Editor.IMAGE_HEIGHT_MIN, Editor.IMAGE_HEIGHT_MAX);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                etImageHeight.removeTextChangedListener(this);
                etImageHeight.setText(Integer.toString(imageSize[1]));
                etImageHeight.setSelection(etImageHeight.getText().length());
                etImageHeight.addTextChangedListener(this);
            }

        });
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editor.setBitmap(Bitmap.createBitmap(Integer.parseInt(etImageWidth.getText().toString()),
                        Integer.parseInt(etImageHeight.getText().toString()), Bitmap.Config.ARGB_8888));
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
    // Save dialog
    private String dialogTempImageName;
    private void buildSaveDialog () {
        dialogTempLoadImage = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_save_image, null);
        final TextView tvImageFormat = view.findViewById(R.id.tv_image_format);
        tvImageFormat.setText(".");
        tvImageFormat.append(editor.getImageFormat());
        tvImageFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildImageFormatDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        tvImageFormat.setText(".");
                        tvImageFormat.append(editor.getImageFormat());
                    }
                });
            }
        });
        final EditText etImageName = view.findViewById(R.id.et_image_name);
        etImageName.setText(editor.getImageName());
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
                dialogTempImageName = etImageName.getText().toString();
                String imageFormat = editor.getImageFormat();
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
                Utils.hideSoftInputFromView(MainActivity.this, etImageName);
                BitmapEncoder.encodeFile(editor.getImagePath() + "/" + dialogTempImageName,
                        editor.getToolBufferPool().getCurrentBitmap(),
                        false, compressFormat, editor.getImageQuality(),
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
                                            BitmapEncoder.encodeFile(editor.getImagePath() + "/" + dialogTempImageName,
                                                    editor.getCurrentBitmap(), true,
                                                    compressFormat, editor.getImageQuality());
                                        }
                                    }, new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            Utils.hideSoftInputFromView(MainActivity.this, etImageName);
                                            buildSaveDialog();
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onIOException(IOException e) {
                                e.printStackTrace();
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
                Utils.hideSoftInputFromView(MainActivity.this, etImageName);
            }
        });
        dialogTempRecyclerImageList = view.findViewById(R.id.recycler_images);
        dialogTempRecyclerImageList.setLayoutManager(new LinearLayoutManager(this));
        dialogTempTvCurrentPath = view.findViewById(R.id.tv_current_path);
        dialogTempTvCurrentPath.setText(editor.getImagePath());
        dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(editor.getImagePath()));
        ImageView imgBack = view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imagePath = editor.getImagePath();
                if (!imagePath.equals("/")) {
                    File imageParentDir = new File(imagePath).getParentFile();
                    if (imageParentDir != null) {
                        if (imageParentDir.canRead() && imageParentDir.canWrite()) {
                            editor.setImagePath(imageParentDir.getAbsolutePath());
                        }
                    }
                }
                dialogTempTvCurrentPath.setText(editor.getImagePath());
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(editor.getImagePath()));
            }
        });
        builder.setView(view);
        builder.create().show();
    }
    private int dialogTempImageQuality;
    private void buildImageFormatDialog (DialogInterface.OnCancelListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        if (editor.getImageFormat().equals("png")) {
            tabHost.setCurrentTabByTag("png");
        }
        if (editor.getImageFormat().equals("jpeg")) {
            tabHost.setCurrentTabByTag("jpeg");
        }
        if (editor.getImageFormat().equals("bmp")) {
            tabHost.setCurrentTabByTag("bmp");
        }
        dialogTempImageQuality = editor.getImageQuality();
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
                        editor.setImageFormat("png");
                        break;
                    case 1:
                        editor.setImageFormat("jpeg");
                        break;
                    case 2:
                        editor.setImageFormat("bmp");
                        break;
                }
                editor.setImageQuality(dialogTempImageQuality);
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
    private FileListAdapter flushImageListAdapter (String newPath) {
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
                VectorDrawableCompat.create(getResources(), R.drawable.ic_baseline_file_24, getTheme()));
        adapter.setOnItemClickListener(new FileListAdapter.OnItemClickListener() {
            @Override
            public void onDirectoryClick(String name, int position) {
                editor.setImagePath(editor.getImagePath() + "/" + name);
                dialogTempTvCurrentPath.setText(editor.getImagePath());
                dialogTempRecyclerImageList.setAdapter(flushImageListAdapter(editor.getImagePath()));
            }
            @Override
            public void onFileClick(String name, int position) {
                if (dialogTempLoadImage) {
                    String pathname = editor.getImagePath() + "/" + name;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    Bitmap bounds = BitmapFactory.decodeFile(pathname, options);
                    if ((bounds.getWidth() <= Editor.IMAGE_WIDTH_MAX) &&
                    bounds.getHeight() <= Editor.IMAGE_HEIGHT_MAX) {
                        BitmapUtils.recycleBitmap(bounds);
                        Bitmap bitmap = BitmapDecoder.decodeFile(pathname);
                        if (bitmap != null) {
                            editor.setImageName(FilenameUtils.getBaseName(name));
                            tvImageName.setText(editor.getImageName());
                            editor.setBitmap(bitmap);
                            if (loadImageDialog != null) {
                                loadImageDialog.dismiss();
                            }
                        }
                    }
                }
            }
        });
        return adapter;
    }
    // Delete file dialog
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
    // File same name dialog
    private void buildFileSameNameDialog(DialogInterface.OnClickListener positiveListener,
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
    // Image name dialog
    private void buildImageNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(editor.getImageName());
        builder.create().show();
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
                buildImageNameDialog();
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

        dataSaved = false;

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

        // Get widgets & set listeners
        // TopBar
        tvImageName = findViewById(R.id.tv_image_name);
        tvImageName.setText(editor.getImageName());
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
        // Editor callback
        editor.setCallback(new Editor.Callback() {
            @Override
            public void SelectionCallback() {
                switch (editor.getSelectionFlag()) {
                    case ToolFlag.SelectionFlag.CUT:
                    case ToolFlag.SelectionFlag.COPY:
                        buildSelectionPopup2();
                        break;
                    case ToolFlag.SelectionFlag.CLEAR:
                    default:
                        if (editor.getDownX() >= 0 && editor.getDownY() >= 0 &&
                                editor.getDownX() <
                                        editor.getToolBufferPool().getCurrentBitmap().getWidth() &&
                                editor.getDownY() <
                                        editor.getToolBufferPool().getCurrentBitmap().getHeight() &&
                                editor.getMoveX() <
                                        editor.getToolBufferPool().getCurrentBitmap().getWidth() &&
                                editor.getMoveY() <
                                        editor.getToolBufferPool().getCurrentBitmap().getHeight()) {
                            buildSelectionPopup1();
                        }
                        break;
                }
            }
        });

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
                buildColorPickerDialog();
            }
        });
    }

}
