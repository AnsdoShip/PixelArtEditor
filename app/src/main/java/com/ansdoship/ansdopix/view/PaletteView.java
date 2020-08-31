package com.ansdoship.ansdopix.view;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

public class PaletteView extends View {
	private Paint paint;
	private int paletteColor;
	private boolean checked;
	private boolean touched;

	public PaletteView(Context context){
		this(context, null);
	}
	public PaletteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PaletteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		invalidate();
	}

	private void init() {
		paletteColor = Color.TRANSPARENT;
		checked = false;
		touched = false;
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
			if (checked || touched) {
				canvas.drawRect(getWidth() * 0.2f, getHeight() * 0.2f,
						getWidth() * 0.8f, getHeight() * 0.8f, paint);
				paint.setColor(Color.WHITE);
				canvas.drawRect(getWidth() * 0.4f, getHeight() * 0.4f,
						getWidth() * 0.6f, getHeight() * 0.6f, paint);
				paint.setColor(Color.BLACK);
				canvas.drawRect(getWidth() * 0.45f, getHeight() * 0.45f,
						getWidth() * 0.55f, getHeight() * 0.55f, paint);
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
		if (hasOnClickListeners()) {
			return super.onTouchEvent(event);
		}
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touched = true;
				break;
			case MotionEvent.ACTION_UP:
				touched = false;
				checked = !checked;
				break;
		}
		invalidate();
		return true;
	}

}