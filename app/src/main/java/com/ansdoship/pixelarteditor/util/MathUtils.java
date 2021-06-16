/*
 * Copyright (C) 2021 AnsdoShip Studio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.ansdoship.pixelarteditor.util;

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

    public static double log (double antilog, double base) {
        return Math.log(antilog) / Math.log(base);
    }

    public static float log (float antilog, float base) {
        return (float)(Math.log(antilog) / Math.log(base));
    }

}
