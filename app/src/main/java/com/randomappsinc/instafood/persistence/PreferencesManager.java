package com.randomappsinc.instafood.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.randomappsinc.instafood.constants.DistanceUnit;
import com.randomappsinc.instafood.models.Filter;
import com.randomappsinc.instafood.utils.DeviceUtils;
import com.randomappsinc.instafood.utils.DistanceUtils;
import com.randomappsinc.instafood.utils.MyApplication;

import java.util.Set;

public class PreferencesManager {

    private static final String DISTANCE_UNIT_KEY = "distanceUnit";
    private static final String NUM_OPENS_KEY = "numOpens";
    private static final String NUM_RATING_ASKS_KEY = "numRatingAsks";

    // Filter
    private static final String FILTER_RADIUS = "filterRadius";
    private static final String FILTER_PRICE_RANGES = "filterPriceRanges";
    private static final String FILTER_ATTRIBUTES = "filterAttributes";
    private static final String SHAKE_ENABLED = "shakeEnabled";

    private static PreferencesManager instance;

    public static PreferencesManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized PreferencesManager getSync() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    private SharedPreferences prefs;

    private PreferencesManager() {
        Context context = MyApplication.getAppContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Filter getFilter() {
        float radius = prefs.getFloat(FILTER_RADIUS, Filter.DEFAULT_RADIUS);
        Set<String> priceRanges = prefs.getStringSet(FILTER_PRICE_RANGES, Filter.DEFAULT_PRICE_RANGES);
        Set<String> attributes = prefs.getStringSet(FILTER_ATTRIBUTES, Filter.DEFAULT_ATTRIBUTES);

        Filter filter = new Filter();
        filter.setRadius(radius);
        filter.setPricesRanges(priceRanges);
        filter.setAttributes(attributes);
        return filter;
    }

    public void saveFilter(Filter filter) {
        prefs.edit()
                .putFloat(FILTER_RADIUS, filter.getRadius())
                .putStringSet(FILTER_PRICE_RANGES, filter.getPriceRanges())
                .putStringSet(FILTER_ATTRIBUTES, filter.getAttributes())
                .apply();
    }

    public @DistanceUnit String getDistanceUnit() {
        return prefs.getString(DISTANCE_UNIT_KEY, DistanceUtils.getDefaultDistanceUnit());
    }

    public void setDistanceUnit(@DistanceUnit String distanceUnit) {
        prefs.edit().putString(DISTANCE_UNIT_KEY, distanceUnit).apply();
    }

    public void setShakeEnabled(boolean shakeEnabled) {
        prefs.edit().putBoolean(SHAKE_ENABLED, shakeEnabled).apply();
    }

    public boolean isShakeEnabled() {
        return prefs.getBoolean(SHAKE_ENABLED, getDefaultShakeValue());
    }

    private boolean getDefaultShakeValue() {
        // Turn off shake by default for Samsung Galaxy S3, since it doesn't hook into sensor APIs properly
        return !DeviceUtils.getDeviceName().equals("Samsung SAMSUNG-SGH-I747");
    }

    public int getNumAppOpens() {
        return prefs.getInt(NUM_OPENS_KEY, 0);
    }

    public void logAppOpen() {
        prefs.edit().putInt(NUM_OPENS_KEY, getNumAppOpens() + 1).apply();
    }

    public int getNumRatingAsks() {
        return prefs.getInt(NUM_RATING_ASKS_KEY, 0);
    }

    public void logRatingAsk() {
        prefs.edit().putInt(NUM_RATING_ASKS_KEY, getNumRatingAsks() + 1).apply();
    }
}
