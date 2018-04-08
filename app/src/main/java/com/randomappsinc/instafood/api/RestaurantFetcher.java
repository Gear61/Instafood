package com.randomappsinc.instafood.api;


import android.os.Bundle;

import com.randomappsinc.instafood.models.Restaurant;
import com.randomappsinc.instafood.models.RestaurantReview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Utility class to fetch restaurant info, so UI pieces don't need to do any networking **/
public class RestaurantFetcher {

    private static final String CURRENT_LOCATION_KEY = "currentLocation";

    public interface Listener {
        void onRestaurantFetched(Restaurant newRestaurant);

        void onPhotosFetched(ArrayList<String> newPhotos);

        void onReviewsFetched(ArrayList<RestaurantReview> newReviews);
    }

    private static RestaurantFetcher instance;

    public static RestaurantFetcher getInstance() {
        if (instance == null) {
            instance = new RestaurantFetcher();
        }
        return instance;
    }

    private Listener listener;
    private RestClient restClient;
    private String location;
    private Random random;
    private List<Restaurant> restaurantPool;
    private List<Restaurant> alreadyChosen;

    private RestaurantFetcher() {
        restClient = RestClient.getInstance();
        random = new Random();
        restaurantPool = new ArrayList<>();
        alreadyChosen = new ArrayList<>();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean canReturnRestaurantImmediately() {
        return !restaurantPool.isEmpty();
    }

    public void fetchRestaurant() {
        restClient.cancelPhotosFetch();
        restClient.cancelReviewsFetch();

        if (restaurantPool.isEmpty()) {
            restClient.findRestaurants(location);
        } else {
            returnRestaurant();
        }
    }

    private void returnRestaurant() {
        int randomIndex = random.nextInt(restaurantPool.size());
        Restaurant chosenOne = restaurantPool.get(randomIndex);
        restaurantPool.remove(randomIndex);
        alreadyChosen.add(chosenOne);

        if (restaurantPool.isEmpty()) {
            restaurantPool.addAll(alreadyChosen);
            Collections.shuffle(restaurantPool);
            alreadyChosen.clear();
        }

        listener.onRestaurantFetched(chosenOne);
        restClient.fetchRestaurantPhotos(chosenOne);
        restClient.fetchRestaurantReviews(chosenOne);
    }

    public void setRestaurantList(List<Restaurant> restaurants) {
        clearRestaurants();
        restaurantPool.addAll(restaurants);
        Collections.shuffle(restaurantPool);
        returnRestaurant();
    }

    public void clearRestaurants() {
        restaurantPool.clear();
        alreadyChosen.clear();
    }

    public void onPhotosFetched(ArrayList<String> photos) {
        listener.onPhotosFetched(photos);
    }

    public void onReviewsFetched(ArrayList<RestaurantReview> reviews) {
        listener.onReviewsFetched(reviews);
    }

    public void persistState(Bundle outState) {
        outState.putString(CURRENT_LOCATION_KEY, location);
    }

    public void extractState(Bundle savedInstanceState) {
        location = savedInstanceState.getString(CURRENT_LOCATION_KEY);
    }

    public void clearEverything() {
        restClient.cancelRestaurantFetch();
        restClient.cancelPhotosFetch();
        restClient.cancelReviewsFetch();
        listener = null;
        restaurantPool.clear();
        alreadyChosen.clear();
    }
}
