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

package com.ansdoship.pixelarteditor.ui.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ansdoship.pixelarteditor.ui.view.CheckedImageView;

public class CheckedImageGroup extends LinearLayout implements View.OnClickListener {

    private int mIndex;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnDoubleTapListener mOnDoubleTapListener;
    private boolean initSwitch;

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        mOnDoubleTapListener = onDoubleTapListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    @Override
    public void onClick(View view) {
        checkIndex(this.indexOfChild(view));
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CheckedImageGroup group, int checkedId, int checkedIndex);
    }

    public interface OnDoubleTapListener {
        void onDoubleTap(CheckedImageGroup group, int checkedId, int checkedIndex);
    }

    public int getCheckedId() {
        if (mIndex < 0 || mIndex >= getChildCount()) {
            return -1;
        }
        return getChildAt(mIndex).getId();
    }

    public int getCheckedIndex() {
        return mIndex;
    }

    public void check(int checkedId) {
        CheckedImageView view = findViewById(checkedId);
        if (view == null) {
            checkIndex(-1);
            return;
        }
        checkIndex(indexOfChild(view));
    }

    public void checkIndex(int index) {
        if (getChildCount() < 1) {
            return;
        }
        for (int i = 0; i < getChildCount(); i ++) {
            ((CheckedImageView) getChildAt(i)).setChecked(false);
        }
        if (index < 0 || index >= getChildCount()) {
            return;
        }
        int preId = getCheckedId();
        if (preId == -1) {
            return;
        }
        int posId = getChildAt(index).getId();
        if (preId != posId) {
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, posId, index);
            }
        }
        else {
            if (mOnDoubleTapListener != null) {
                mOnDoubleTapListener.onDoubleTap(this, posId, index);
            }
        }
        mIndex = index;
        ((CheckedImageView) getChildAt(index)).setChecked(true);
    }

    public CheckedImageGroup(@NonNull Context context) {
        this(context, null);
    }

    public CheckedImageGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckedImageGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSwitch = true;
    }

    private void init() {
        if (!initSwitch) {
            return;
        }
        initSwitch = false;
        if (getChildCount() < 0) {
            return;
        }
        int defaultCheckedIndex = 0;
        for (int i = 0; i < getChildCount(); i ++) {
            if (((CheckedImageView) getChildAt(i)).isChecked()) {
                defaultCheckedIndex = i;
            }
        }
        for (int i = 0; i < getChildCount(); i ++) {
            getChildAt(i).setOnClickListener(this);
        }
        ((CheckedImageView) getChildAt(defaultCheckedIndex)).setChecked(true);
    }

}
