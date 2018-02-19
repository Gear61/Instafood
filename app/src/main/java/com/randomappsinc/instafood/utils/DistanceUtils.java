package com.randomappsinc.instafood.utils;

public class DistanceUtils {
    private static final double MILES_IN_A_METER = 0.000621371;

    public static double getMilesFromMeters(double meters) {
        return meters * MILES_IN_A_METER;
    }
}
