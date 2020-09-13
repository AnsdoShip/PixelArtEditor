package com.ansdoship.pixart.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ansdoship.pixart.view.CheckedImageView;

import java.util.ArrayList;
import java.util.List;

public class CheckedImageGroup extends LinearLayout implements View.OnClickListener {

    private List<CheckedImageView> imageViews;
    private int mIndex;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnDoubleTapListener mOnDoubleTapListener;
    private boolean initSwitch;
    private boolean checkable;

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
        if (mIndex < 0 || mIndex >= imageViews.size()) {
            return -1;
        }
        return imageViews.get(mIndex).getId();
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
        checkIndex(imageViews.indexOf(view));
    }

    public void checkIndex(int index) {
        if (!checkable) {
            return;
        }
        if (imageViews.isEmpty()) {
            return;
        }
        for (CheckedImageView imageView : imageViews) {
            imageView.setChecked(false);
        }
        if (index < 0 || index >= imageViews.size()) {
            return;
        }
        int preId = getCheckedId();
        if (preId == -1) {
            return;
        }
        int posId = imageViews.get(index).getId();
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
        imageViews.get(index).setChecked(true);
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
        initSwitch = true;
    }

    private void init() {
        if (!initSwitch) {
            return;
        }
        int defaultCheckedIndex = 0;
        for (int i = 0; i < getChildCount(); i ++) {
            imageViews.add((CheckedImageView) getChildAt(i));
            if (((CheckedImageView) getChildAt(i)).isChecked()) {
                defaultCheckedIndex = i;
            }
        }
        if (imageViews.isEmpty()) {
            return;
        }
        for (CheckedImageView imageView : imageViews) {
            imageView.setOnClickListener(this);
        }
        checkable = false;
        checkIndex(defaultCheckedIndex);
        checkable = true;
        initSwitch = false;
    }

}
