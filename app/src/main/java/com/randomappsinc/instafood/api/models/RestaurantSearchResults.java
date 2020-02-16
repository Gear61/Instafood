package com.randomappsinc.instafood.api.models;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.randomappsinc.instafood.models.Restaurant;
import com.randomappsinc.instafood.persistence.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSearchResults {

    @SerializedName("businesses")
    @Expose
    private List<Business> businesses;

    public List<Restaurant> getRestaurants(Context context) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        double filterDistance = preferencesManager.getFilter().getRadius();

        List<Restaurant> restaurants = new ArrayList<>(businesses.size());
        for (int i = 0, size = businesses.size(); i < size; ++i) {
            Business business = businesses.get(i);
            // Filter out restaurants that are super far away, because Yelp API refuses to return empty lists
            if (business.getDistance() <= filterDistance) {
                restaurants.add(business.toRestaurant());
            }
        }
        return restaurants;
    }
}
