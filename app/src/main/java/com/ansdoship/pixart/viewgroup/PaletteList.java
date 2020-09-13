package com.ansdoship.pixart.viewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ansdoship.pixart.R;
import com.ansdoship.pixart.util.ColorPalette;
import com.ansdoship.pixart.view.PaletteView;

import java.util.ArrayList;
import java.util.List;

public class PaletteList extends LinearLayout implements View.OnClickListener{
    private List<PaletteView> palettes;
    private int mIndex;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnDoubleTapListener mOnDoubleTapListener;
    private Context mContext;
    private ColorPalette mColorPalette;

    @Override
    public void onClick(View view) {
        checkIndex(this.indexOfChild(view));
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        mOnDoubleTapListener = onDoubleTapListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(PaletteList paletteList, int checkedIndex);
    }

    public interface OnDoubleTapListener {
        void onDoubleTap(PaletteList paletteList, int checkedIndex);
    }

    public void checkIndex(int index) {
        int preIndex = getCheckedIndex();
        if (preIndex != index) {
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, index);
            }
        }
        else {
            if (mOnDoubleTapListener != null) {
                mOnDoubleTapListener.onDoubleTap(this, index);
            }
        }
        mIndex = index;
        for (PaletteView palette : palettes) {
            palette.setChecked(false);
        }
        if (index >= 0 && index < palettes.size()) {
            palettes.get(index).setChecked(true);
        }
    }

    public int getCheckedIndex() {
        return mIndex;
    }

    public PaletteList(Context context) {
        this(context, null);
    }

    public PaletteList(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaletteList(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PaletteList, defStyleAttr, 0);
        int size = typedArray.getInt(R.styleable.PaletteList_size, 1);
        palettes = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            PaletteView paletteView = new PaletteView(context);
            paletteView.setLayoutParams(new LinearLayout.LayoutParams(typedArray.getDimensionPixelSize(R.styleable.PaletteList_paletteWidth, 0),
                    typedArray.getDimensionPixelSize(R.styleable.PaletteList_paletteHeight, 0)));
            palettes.add(paletteView);
        }
        for (PaletteView palette : palettes) {
            palette.setOnClickListener(this);
            addView(palette);
        }
        checkIndex(typedArray.getInt(R.styleable.PaletteList_checkedIndex, 0));
        typedArray.recycle();
    }

    public void setSize (int size) {
        setSize(size, 0);
    }

    public void setSize (int size, int index) {
        if (size < 1) {
            return;
        }
        mColorPalette = null;
        removeAllViews();
        palettes = new ArrayList<>(size);
        for (int i = 0; i < size; i ++) {
            palettes.add(new PaletteView(mContext));
        }
        for (PaletteView palette : palettes) {
            palette.setOnClickListener(this);
            addView(palette);
        }
        checkIndex(index);
    }

    public int getSize () {
        return palettes.size();
    }

    public void setColorPalette (ColorPalette colorPalette) {
        removeAllViews();
        mColorPalette = colorPalette;
        if (colorPalette != null) {
            for (int i = 0; i < colorPalette.size(); i++) {
                palettes.add(new PaletteView(mContext));
            }
            for (PaletteView palette : palettes) {
                palette.setOnClickListener(this);
                addView(palette);
            }
        }
    }

    public void setPaletteColor (int index, int color) {
        palettes.get(index).setPaletteColor(color);
        if (mColorPalette != null) {
            mColorPalette.set(index, color);
        }
    }

    public int getPaletteColor (int index) {
        return palettes.get(index).getPaletteColor();
    }

}
