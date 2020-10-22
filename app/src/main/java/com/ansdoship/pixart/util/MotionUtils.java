package com.ansdoship.pixart.util;

import android.view.MotionEvent;

public final class MotionUtils {

    // Calculate linear distance of two fingers
    public static double spacing(MotionEvent event) {
        if(event.getPointerCount() >= 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return Math.pow(x * x + y * y, 0.5);
        }
        return 0;
    }

}
