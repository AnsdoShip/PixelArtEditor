package com.ansdoship.ansdopix.view;
import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.widget.Checkable;

import com.ansdoship.ansdopix.R;

public class PaletteView extends View implements Checkable {
	private Paint paint;
	private int paletteColor;
	private boolean mChecked;
	private boolean mTouched;

	public PaletteView(Context context){
		this(context, null);
	}
	public PaletteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PaletteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
		paletteColor = Color.TRANSPARENT;
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PaletteView, defStyleAttr, 0);
		mChecked = typedArray.getBoolean(R.styleable.PaletteView_android_checked, false);
		typedArray.recycle();
    }

    @Override
	public boolean isChecked() {
		return mChecked;
	}

	@Override
	public void toggle() {
		mChecked = !mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		this.mChecked = checked;
		invalidate();
	}

	@Override
	public void setClickable(boolean clickable) {
		super.setClickable(clickable);
		mTouched = false;
		invalidate();
	}

	public void setPaletteColor(int color) {
		paletteColor = color;
		invalidate();
	}
	
	public int getPaletteColor() {
		return paletteColor;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
			setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
		}
		else if(widthSpecMode == MeasureSpec.AT_MOST) {
			setMeasuredDimension(getMeasuredWidth(), heightSpecSize);
		}
		else if(heightSpecMode == MeasureSpec.AT_MOST) {
			setMeasuredDimension(widthSpecSize, getMeasuredHeight());
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (paint == null) {
			paint = new Paint();
			paint.setStyle(Paint.Style.FILL);
			invalidate();
		}
		else {
			paint.setColor(Color.LTGRAY);
			canvas.drawRect(0, 0, getWidth() * 0.5f, getHeight() * 0.5f, paint);
			canvas.drawRect(getWidth() * 0.5f, getHeight() * 0.5f, getWidth(), getHeight(), paint);
			paint.setColor(Color.GRAY);
			canvas.drawRect(getWidth() * 0.5f, 0, getWidth(), getHeight() * 0.5f, paint);
			canvas.drawRect(0, getHeight() * 0.5f, getWidth() * 0.5f, getHeight(), paint);
			paint.setColor(paletteColor);
			if (mChecked || mTouched) {
				canvas.drawRect(getWidth() * 0.2f, getHeight() * 0.2f,
						getWidth() * 0.8f, getHeight() * 0.8f, paint);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(getWidth() * 0.2f);
				paint.setColor(Color.WHITE);
				canvas.drawRect(getWidth() * 0.2f, getHeight() * 0.2f,
						getWidth() * 0.8f, getHeight() * 0.8f, paint);
				paint.setStrokeWidth(getWidth() * 0.1f);
				paint.setColor(Color.BLACK);
				canvas.drawRect(getWidth() * 0.2f, getHeight() * 0.2f,
						getWidth() * 0.8f, getHeight() * 0.8f, paint);
				paint.setStyle(Paint.Style.FILL);
				paint.setStrokeWidth(1);
			}
			else {
				canvas.drawRect(getWidth() * 0.1f, getHeight() * 0.1f,
						getWidth() * 0.9f, getHeight() * 0.9f, paint);
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isClickable()) {
			return false;
		}
		if (hasOnClickListeners()) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTouched = true;
				break;
			case MotionEvent.ACTION_UP:
				mTouched = false;
				toggle();
				break;
		}
		invalidate();
		return true;
	}

}