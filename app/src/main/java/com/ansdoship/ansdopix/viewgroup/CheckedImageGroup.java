package com.ansdoship.ansdopix.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ansdoship.ansdopix.view.CheckedImageView;

import java.util.ArrayList;
import java.util.List;

public class CheckedImageGroup extends LinearLayout implements View.OnClickListener {

    private List<CheckedImageView> imageViews;
    private int index;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnDoubleTapListener mOnDoubleTapListener;

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
        if (index < 0 || index >= imageViews.size()) {
            return -1;
        }
        return imageViews.get(index).getId();
    }

    public int getCheckedIndex() {
        return index;
    }

    public void check(int checkedId) {
        checkIndex(imageViews.indexOf((CheckedImageView) findViewById(checkedId)));
    }

    public void checkIndex(int index) {
        int preIndex = getCheckedIndex();
        if (preIndex != index) {
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, index, imageViews.get(index).getId());
            }
        }
        else {
            if (mOnDoubleTapListener != null) {
                mOnDoubleTapListener.onDoubleTap(this, index, imageViews.get(index).getId());
            }
        }
        this.index = index;
        if (imageViews.isEmpty()) {
            return;
        }
        for (CheckedImageView imageView : imageViews) {
            imageView.setChecked(false);
        }
        if (index >= 0 && index < imageViews.size()) {
            imageViews.get(index).setChecked(true);
        }
    }

    public CheckedImageGroup(Context context) {
        this(context, null);
    }

    public CheckedImageGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckedImageGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageViews = new ArrayList<>();
    }

    private void init() {
        for (int i = 0; i < getChildCount(); i ++) {
            imageViews.add((CheckedImageView) getChildAt(i));
        }
        if (imageViews.isEmpty()) {
            return;
        }
        for (CheckedImageView imageView : imageViews) {
            imageView.setOnClickListener(this);
        }
        if (mOnCheckedChangeListener == null) {
            return;
        }
        checkIndex(0);
    }

}
