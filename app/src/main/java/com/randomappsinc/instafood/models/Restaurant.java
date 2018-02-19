package com.randomappsinc.instafood.models;

import java.util.List;

public class Restaurant {

    private String yelpId;
    private String name;
    private String imageUrl;
    private String yelpUrl;
    private double rating;
    private int reviewCount;
    private String phoneNumber;
    private String price;
    private String address;
    private double latitute;
    private double longitude;
    private List<RestaurantCategory> categories;

    // Distance between the place location and the user's current location in miles/kilometers
    // Miles vs. kilometers is determined by the user's setting
    private double mDistance;

    public String getId() {
        return yelpId;
    }

    public void setId(String id) {
        yelpId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return yelpUrl;
    }

    public void setUrl(String url) {
        yelpUrl = url;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitute;
    }

    public void setLatitude(double latitude) {
        latitute = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        mDistance = distance;
    }

    public void setCategories(List<RestaurantCategory> categories) {
        this.categories = categories;
    }

    public String getCategoriesListText() {
        StringBuilder categoriesList = new StringBuilder();
        for (RestaurantCategory category : categories) {
            if (categoriesList.length() > 0) {
                categoriesList.append(", ");
            }
            categoriesList.append(category.getTitle());
        }
        return categoriesList.toString();
    }
}
