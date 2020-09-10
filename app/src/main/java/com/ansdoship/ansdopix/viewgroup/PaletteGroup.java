package com.ansdoship.ansdopix.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ansdoship.ansdopix.view.PaletteView;

import java.util.ArrayList;
import java.util.List;

public class PaletteGroup extends LinearLayout implements View.OnClickListener{
    private List<PaletteView> palettes;
    private int position;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnDoubleTapListener mOnDoubleTapListener;

    @Override
    public void onClick(View v) {
        int preId = getCheckedPaletteViewId();
        int posId = v.getId();
        if (preId != posId) {
            if (mOnCheckedChangeListener == null) {
                return;
            }
            mOnCheckedChangeListener.onCheckedChanged(this, posId,
                    palettes.indexOf((PaletteView) findViewById(posId)));
            check(posId);
        }
        else {
            if (mOnDoubleTapListener == null) {
                return;
            }
            mOnDoubleTapListener.onDoubleTap(this, posId,
                    palettes.indexOf((PaletteView) findViewById(posId)));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        mOnDoubleTapListener = onDoubleTapListener;
    }

    public interface OnCheckedChangeListener {
        void onInitialCheck(PaletteGroup group, int checkedId, int checkedPosition);
        void onCheckedChanged(PaletteGroup group, int checkedId, int checkedPosition);
    }

    public interface OnDoubleTapListener {
        void onDoubleTap(PaletteGroup group, int checkedId, int checkedPosition);
    }

    public int getCheckedPaletteViewId() {
        if (position < 0 || position >= palettes.size()) {
            return -1;
        }
        return palettes.get(position).getId();
    }

    public void check(int checkedId) {
        checkPosition(palettes.indexOf((PaletteView) findViewById(checkedId)));
    }

    public void checkPosition(int position) {
        this.position = position;
        if (palettes.isEmpty()) {
            return;
        }
        for (PaletteView palette : palettes) {
            palette.setChecked(false);
        }
        if (position >= 0 && position < palettes.size()) {
            palettes.get(position).setChecked(true);
        }
    }

    public int getCheckedPalettePosition() {
        return position;
    }

    public List<PaletteView> getPalettes() {
        return palettes;
    }

    public PaletteGroup(Context context) {
        this(context, null);
    }

    public PaletteGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        palettes = new ArrayList<>();
        position = -1;
    }

    private void init() {
        for (int i = 0; i < getChildCount(); i ++) {
            palettes.add((PaletteView)getChildAt(i));
        }
        if (palettes.isEmpty()) {
            return;
        }
        for (PaletteView palette : palettes) {
            palette.setOnClickListener(this);
        }
        checkPosition(0);
        if (mOnCheckedChangeListener == null) {
            return;
        }
        mOnCheckedChangeListener.onInitialCheck(this, palettes.get(0).getId(), 0);
    }

}
