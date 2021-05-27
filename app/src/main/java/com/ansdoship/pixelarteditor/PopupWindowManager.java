package com.ansdoship.pixelarteditor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.ansdoship.pixelarteditor.app.ApplicationUtils;
import com.ansdoship.pixelarteditor.editor.buffers.SelectionBuffer;

public final class PopupWindowManager {

    private enum Singleton {
        INSTANCE;
        private final PopupWindowManager instance;
        Singleton() {
            instance = new PopupWindowManager();
        }
        public PopupWindowManager getInstance() {
            return instance;
        }
    }

    private PopupWindowManager() {
        context = ApplicationUtils.getApplicationContext();
    }

    public static PopupWindowManager getInstance() {
        return PopupWindowManager.Singleton.INSTANCE.getInstance();
    }

    private final Context context;

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
                selectionFlag = SelectionBuffer.SelectionFlag.CUT;
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
                selectionFlag = SelectionBuffer.SelectionFlag.COPY;
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
                selectionFlag = SelectionBuffer.SelectionFlag.CLEAR;
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
                selectionBitmap = BitmapUtils.rotateBitmap(selectionBitmap, -90);
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
                selectionBitmap = BitmapUtils.rotateBitmap(selectionBitmap, 90);
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
                selectionBitmap = BitmapUtils.flipBitmapHorizontally(selectionBitmap);
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
                selectionBitmap = BitmapUtils.flipBitmapVertically(selectionBitmap);
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
    private void buildMenuPopup() {
        View view = View.inflate(this, R.layout.popup_menu, null);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        final PopupWindow window = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
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
                buildSaveImageDialog();
                window.dismiss();
            }
        });
        imgHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

}
