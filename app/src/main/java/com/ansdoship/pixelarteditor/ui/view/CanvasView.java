package com.ansdoship.pixelarteditor.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CanvasView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;

    private OnInvalidateListener mOnInvalidateListener;

    public interface OnInvalidateListener {
        void onInvalidate(Canvas canvas);
    }

    public void setOnInvalidateListener(OnInvalidateListener onInvalidateListener) {
        mOnInvalidateListener = onInvalidateListener;
    }

    public CanvasView(@NonNull Context context) {
        this(context, null, 0);
    }

    public CanvasView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    public void invalidate() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if(mOnInvalidateListener != null) {
                mOnInvalidateListener.onInvalidate(mCanvas);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        invalidate();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {}

    @Override
    public boolean performClick() {
        return super.performClick();
    }
    
}
