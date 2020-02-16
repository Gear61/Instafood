package com.randomappsinc.instafood.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.randomappsinc.instafood.constants.RestaurantTransaction;
import com.randomappsinc.instafood.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class Business {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("review_count")
    @Expose
    private int reviewCount;

    @SerializedName("display_phone")
    @Expose
    private String phoneNumber;

    @SerializedName("price")
    @Expose
    private String price;

    @SerializedName("coordinates")
    @Expose
    private Coordinates coordinates;

    @SerializedName("location")
    @Expose
    private Location location;

    // Distance in meters from the restaurant location
    @SerializedName("distance")
    @Expose
    private double distance;

    public double getDistance() {
        return distance;
    }

    @SerializedName("categories")
    @Expose
    private List<Category> categories;

    @SerializedName("transactions")
    @Expose
    private List<String> transactions;

    Restaurant toRestaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName(name);
        restaurant.setImageUrl(imageUrl);
        restaurant.setUrl(url);
        restaurant.setRating(rating);
        restaurant.setReviewCount(reviewCount);
        restaurant.setPhoneNumber(phoneNumber);
        restaurant.setPrice(price);
        restaurant.setAddress(location.getAddress());
        restaurant.setFullAddress(location.getFullAddress());
        restaurant.setLatitude(coordinates.getLatitude());
        restaurant.setLongitude(coordinates.getLongitude());
        restaurant.setDistance(distance);
        restaurant.setSupportsPickup(transactions.contains(RestaurantTransaction.PICKUP));
        restaurant.setSupportsDelivery(transactions.contains(RestaurantTransaction.DELIVERY));
        restaurant.setSupportsReservations(transactions.contains(RestaurantTransaction.RESERVATION));
        ArrayList<String> restaurantCategories = new ArrayList<>(categories.size());
        for (int i = 0, size = categories.size(); i < size; ++i) {
            restaurantCategories.add(categories.get(i).getTitle());
        }
        restaurant.setCategories(restaurantCategories);
        return restaurant;
    }
}
