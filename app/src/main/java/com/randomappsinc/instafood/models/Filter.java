package com.randomappsinc.instafood.models;

import com.randomappsinc.instafood.constants.PriceRange;
import com.randomappsinc.instafood.utils.DistanceUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Filter {

    public static final float DEFAULT_RADIUS = 8046.72f;
    public static final Set<String> DEFAULT_PRICE_RANGES =
            new HashSet<>(Arrays.asList(
                    PriceRange.CHEAP,
                    PriceRange.MODERATE,
                    PriceRange.PRICEY,
                    PriceRange.VERY_EXPENSIVE));
    public static final Set<String> DEFAULT_ATTRIBUTES = new HashSet<>();

    private static final float MAX_YELP_DISTANCE = 40000;

    // Allowed distance from the user's current location in meters
    private float radius;
    private Set<String> priceRanges;
    private Set<String> attributes;

    public float getRadius() {
        return radius;
    }

    public float getRadiusInMiles() {
        return (float) DistanceUtils.getMilesFromMeters(radius);
    }

    public float getRadiusInKilometers() {
        return (float) DistanceUtils.getKilometersFromMeters(radius);
    }

    public Set<String> getPriceRanges() {
        return priceRanges;
    }

    public String getPriceRangesString() {
        StringBuilder rangesString = new StringBuilder();
        for (String priceRange : priceRanges) {
            if (rangesString.length() > 0) {
                rangesString.append(", ");
            }
            rangesString.append(priceRange);
        }
        return rangesString.toString();
    }

    public Set<String> getAttributes() {
        return attributes;
    }

    public String getAttributesString() {
        StringBuilder attributesString = new StringBuilder();
        for (String attribute : attributes) {
            if (attributesString.length() > 0) {
                attributesString.append(", ");
            }
            attributesString.append(attribute);
        }
        return attributesString.toString();
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setRadiusWithMiles(double miles) {
        float converted = (float) DistanceUtils.getMetersFromMiles(miles);

        // The API call fails if we pass in a radius > 40,000 meters so curb value here to be extra safe
        radius = Math.min(converted, MAX_YELP_DISTANCE);
    }

    public void setRadiusWithKilometers(double kilometers) {
        float converted = (float) DistanceUtils.getMetersFromKilometers(kilometers);

        // The API call fails if we pass in a radius > 40,000 meters so curb value here to be extra safe
        radius = Math.min(converted, MAX_YELP_DISTANCE);
    }

    public void setPricesRanges(Set<String> priceRanges) {
        this.priceRanges = priceRanges;
    }

    public void setAttributes(Set<String> attributes) {
        this.attributes = attributes;
    }

    public void reset() {
        radius = DEFAULT_RADIUS;
        priceRanges = DEFAULT_PRICE_RANGES;
        attributes = DEFAULT_ATTRIBUTES;
    }
}
