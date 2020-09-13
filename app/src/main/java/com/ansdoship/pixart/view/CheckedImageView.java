package com.ansdoship.pixart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import com.ansdoship.pixart.R;

public class CheckedImageView extends AppCompatImageButton implements Checkable {

    private boolean mChecked;

    public CheckedImageView(@NonNull Context context) {
        super(context);
    }

    public CheckedImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckedImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckedImageView, defStyleAttr, 0);
        mChecked = typedArray.getBoolean(R.styleable.CheckedImageView_android_checked, false);
        typedArray.recycle();
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        mChecked = !mChecked;
    }

}
