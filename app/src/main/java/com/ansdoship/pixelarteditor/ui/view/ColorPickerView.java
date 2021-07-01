/*
 * Copyright (C) 2021 AnsdoShip Studio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.ansdoship.pixelarteditor.ui.view;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import yuku.ambilwarna.AmbilWarnaSquare;
import yuku.ambilwarna.R;

public class ColorPickerView extends LinearLayout {

    public interface OnColorPickedListener{
        void onUpdate(ColorPickerView view,int color);
    }

    private OnColorPickedListener listener;
    private View viewHue;
    private AmbilWarnaSquare viewSatVal;
    private ImageView viewCursor;
    private ImageView viewTarget;
    private ViewGroup viewContainer;
    private ImageView viewAlphaCursor;
    private View viewOldColor;
    private View viewNewColor;
    private View viewAlphaOverlay;
    private ImageView viewAlphaCheckered;
    private final float[] currentColorHsv = new float[3];
    int alpha;

    public ColorPickerView(Context context) {
        super(context);
        init(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.ambilwarna_dialog,this);
        
        viewHue = findViewById(R.id.ambilwarna_viewHue);
        viewSatVal = findViewById(R.id.ambilwarna_viewSatBri);
        viewCursor = findViewById(R.id.ambilwarna_cursor);
        viewTarget = findViewById(R.id.ambilwarna_target);
        viewContainer = findViewById(R.id.ambilwarna_viewContainer);

        viewOldColor = findViewById(R.id.ambilwarna_oldColor);
        viewNewColor = findViewById(R.id.ambilwarna_newColor);
        viewAlphaOverlay = findViewById(R.id.ambilwarna_overlay);
        viewAlphaCursor = findViewById(R.id.ambilwarna_alphaCursor);
        viewAlphaCheckered = findViewById(R.id.ambilwarna_alphaCheckered);

        {
            viewAlphaOverlay.setVisibility(GONE);
            viewAlphaCursor.setVisibility(GONE);
            viewAlphaCheckered.setVisibility(GONE);
        }

        viewHue.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_UP) {

                float y = event.getY();
                if (y < 0.f) y = 0.f;
                if (y > viewHue.getMeasuredHeight()) {
                    y = viewHue.getMeasuredHeight() - 0.001f; // to avoid jumping the cursor from bottom to top.
                }
                float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
                if (hue == 360.f) hue = 0.f;
                setHue(hue);

                // update view
                viewSatVal.setHue(getHue());
                moveCursor();
                if (listener!=null) {
                    listener.onUpdate(this, getColor());
                }

                v.performClick();
                return true;
            }
            return false;
        });

        viewSatVal.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_UP) {

                float x = event.getX(); // touch event are in dp units.
                float y = event.getY();

                if (x < 0.f) x = 0.f;
                if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
                if (y < 0.f) y = 0.f;
                if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

                setSat(1.f / viewSatVal.getMeasuredWidth() * x);
                setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

                // update view
                moveTarget();
                if (listener!=null) {
                    listener.onUpdate(this, getColor());
                }

                v.performClick();
                return true;
            }
            return false;
        });
    }

    public void setOrdinalColor(int color){
        alpha = Color.alpha(color);
        Color.colorToHSV(color,currentColorHsv);
        post(() -> {
            viewSatVal.setHue(getHue());
            moveCursor();
            moveTarget();
        });
    }

    public void updateColor(int color){
        setOrdinalColor(color);
    }

    public void setOnColorPickedListener(OnColorPickedListener listener){
        this.listener = listener;
    }

    protected void moveCursor() {
        float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
        if (y == viewHue.getMeasuredHeight()) y = 0.f;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
        layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() * 0.5f) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() * 0.5f) - viewContainer.getPaddingTop());
        viewCursor.setLayoutParams(layoutParams);
    }

    protected void moveTarget() {
        float x = getSat() * viewSatVal.getMeasuredWidth();
        float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() * 0.5f) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() * 0.5f) - viewContainer.getPaddingTop());
        viewTarget.setLayoutParams(layoutParams);
    }

    private int getColor() {
        final int argb = Color.HSVToColor(currentColorHsv);
        return alpha << 24 | (argb & 0x00ffffff);
    }

    public float getHue() {
        return currentColorHsv[0];
    }

    public float getAlpha() {
        return this.alpha;
    }

    public float getSat() {
        return currentColorHsv[1];
    }

    public float getVal() {
        return currentColorHsv[2];
    }

    public void setHue(float hue) {
        currentColorHsv[0] = hue;
    }

    public void setSat(float sat) {
        currentColorHsv[1] = sat;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setVal(float val) {
        currentColorHsv[2] = val;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
