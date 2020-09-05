package com.ansdoship.ansdopix.util;

import android.graphics.Color;

public class ColorUtils {
    public static float hue (int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[0];
    }
    public static float saturation (int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[1];
    }
    public static float value (int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[2];
    }
    public static int setAlpha (int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
    public static int setRed (int color, int red) {
        return Color.argb(Color.alpha(color), red, Color.green(color), Color.blue(color));
    }
    public static int setGreen (int color, int green) {
        return Color.argb(Color.alpha(color), Color.red(color), green, Color.blue(color));
    }
    public static int setBlue (int color, int blue) {
        return Color.argb(Color.alpha(color), Color.red(color), Color.green(color), blue);
    }
    public static int setHue (int color, int hue) {
        return Color.HSVToColor(Color.alpha(color), new float[] {hue, saturation(color), value(color)});
    }
    public static int setSaturation (int color, float saturation) {
        return Color.HSVToColor(Color.alpha(color), new float[] {hue(color), saturation, value(color)});
    }
    public static int setValue (int color, float value) {
        return Color.HSVToColor(Color.alpha(color), new float[] {hue(color), saturation(color), value});
    }
    public static String colorToHexString (int color) {
        return "#" +
                (Integer.toHexString(Color.alpha(color)) + "0").substring(0, 2) +
                (Integer.toHexString(Color.red(color)) + "0").substring(0, 2) +
                (Integer.toHexString(Color.green(color)) + "0").substring(0, 2) +
                (Integer.toHexString(Color.blue(color)) + "0").substring(0, 2);
    }
}
