package com.ansdoship.pixelarteditor.editor.graphics;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

public final class ColorFactory {

    private ColorFactory(){}

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

    public static float lightness (int color) {
        return value(color);
    }

    public static float value (int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv[2];
    }

    public static double x (int color) {
        double[] xyz = new double[3];
        ColorUtils.colorToXYZ(color, xyz);
        return xyz[0];
    }

    public static double y (int color) {
        double[] xyz = new double[3];
        ColorUtils.colorToXYZ(color, xyz);
        return xyz[1];
    }

    public static double z (int color) {
        double[] xyz = new double[3];
        ColorUtils.colorToXYZ(color, xyz);
        return xyz[2];
    }

    public static double l (int color) {
        double[] lab = new double[3];
        ColorUtils.colorToLAB(color, lab);
        return lab[0];
    }

    public static double a (int color) {
        double[] lab = new double[3];
        ColorUtils.colorToLAB(color, lab);
        return lab[1];
    }

    public static double b (int color) {
        double[] lab = new double[3];
        ColorUtils.colorToLAB(color, lab);
        return lab[2];
    }

    public static int resetAlpha (int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int resetRed (int color, int red) {
        return Color.argb(Color.alpha(color), red, Color.green(color), Color.blue(color));
    }

    public static int resetGreen (int color, int green) {
        return Color.argb(Color.alpha(color), Color.red(color), green, Color.blue(color));
    }

    public static int resetBlue (int color, int blue) {
        return Color.argb(Color.alpha(color), Color.red(color), Color.green(color), blue);
    }

    public static int resetHue (int color, int hue) {
        return Color.HSVToColor(Color.alpha(color), new float[] {hue, saturation(color), value(color)});
    }

    public static int resetSaturation (int color, float saturation) {
        return Color.HSVToColor(Color.alpha(color), new float[] {hue(color), saturation, value(color)});
    }

    public static int resetValue (int color, float value) {
        return Color.HSVToColor(Color.alpha(color), new float[] {hue(color), saturation(color), value});
    }

    public static int resetLightness (int color, float lightness) {
        return resetValue(color, lightness);
    }

    public static int resetX (int color, double x) {
        return resetAlpha(ColorUtils.XYZToColor(x, y(color), z(color)), Color.alpha(color));
    }

    public static int resetY (int color, double y) {
        return resetAlpha(ColorUtils.XYZToColor(x(color), y, z(color)), Color.alpha(color));
    }

    public static int resetZ (int color, double z) {
        return resetAlpha(ColorUtils.XYZToColor(x(color), y(color), z), Color.alpha(color));
    }

    public static int resetL (int color, double l) {
        return resetAlpha(ColorUtils.LABToColor(l, a(color), b(color)), Color.alpha(color));
    }

    public static int resetA (int color, double a) {
        return resetAlpha(ColorUtils.LABToColor(l(color), a, b(color)), Color.alpha(color));
    }

    public static int resetB (int color, double b) {
        return resetAlpha(ColorUtils.LABToColor(l(color), a(color), b), Color.alpha(color));
    }

    public static @NonNull String colorToHexString (int color) {
        return String.format("#%08X", color);
    }
    
}
