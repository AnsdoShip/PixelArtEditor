package com.ansdoship.pixelarteditor.math;

/**
 * A utility class providing functions useful for common mathematical operations.
 */
public final class MathUtils {

    private MathUtils (){}

    /**
     * This method takes a numerical value and ensures it fits in a given numerical range. If the
     * number is smaller than the minimum required by the range, then the minimum of the range will
     * be returned. If the number is higher than the maximum allowed by the range then the maximum
     * of the range will be returned.
     *
     * @param value the value to be clamped.
     * @param min   minimum resulting value.
     * @param max   maximum resulting value.
     * @return the clamped value.
     */
    public static int clamp (int value, int min, int max) {
        return androidx.core.math.MathUtils.clamp(value, min, max);
    }

    /**
     * This method takes a numerical value and ensures it fits in a given numerical range. If the
     * number is smaller than the minimum required by the range, then the minimum of the range will
     * be returned. If the number is higher than the maximum allowed by the range then the maximum
     * of the range will be returned.
     *
     * @param value the value to be clamped.
     * @param min   minimum resulting value.
     * @param max   maximum resulting value.
     * @return the clamped value.
     */
    public static long clamp (long value, long min, long max) {
        return androidx.core.math.MathUtils.clamp(value, min, max);
    }

    /**
     * This method takes a numerical value and ensures it fits in a given numerical range. If the
     * number is smaller than the minimum required by the range, then the minimum of the range will
     * be returned. If the number is higher than the maximum allowed by the range then the maximum
     * of the range will be returned.
     *
     * @param value the value to be clamped.
     * @param min   minimum resulting value.
     * @param max   maximum resulting value.
     * @return the clamped value.
     */
    public static float clamp (float value, float min, float max) {
        return androidx.core.math.MathUtils.clamp(value, min, max);
    }

    /**
     * This method takes a numerical value and ensures it fits in a given numerical range. If the
     * number is smaller than the minimum required by the range, then the minimum of the range will
     * be returned. If the number is higher than the maximum allowed by the range then the maximum
     * of the range will be returned.
     *
     * @param value the value to be clamped.
     * @param min   minimum resulting value.
     * @param max   maximum resulting value.
     * @return the clamped value.
     */
    public static double clamp (double value, double min, double max) {
        return androidx.core.math.MathUtils.clamp(value, min, max);
    }


    /**
     * Converts degrees to radians.
     *
     * @param deg the degrees
     * @return the radians
     */
    public static float deg2rad (float deg) {
        return (float) (deg * Math.PI / 180);
    }

    /**
     * Converts degrees to radians.
     *
     * @param deg the degrees
     * @return the radians
     */
    public static double deg2rad (double deg) {
        return deg * Math.PI / 180;
    }

    /**
     * Converts radians to degrees.
     *
     * @param rad the radians
     * @return the degrees
     */
    public static float rad2deg (float rad) {
        return (float) (rad * 180 / Math.PI);
    }

    /**
     * Converts radians to degrees.
     *
     * @param rad the radians
     * @return the degrees
     */
    public static double rad2deg (double rad) {
        return rad * 180 / Math.PI;
    }

}
