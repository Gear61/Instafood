package com.randomappsinc.instafood.api;


import android.os.Bundle;

import androidx.annotation.Nullable;

import com.randomappsinc.instafood.api.models.DailyHours;
import com.randomappsinc.instafood.models.Restaurant;
import com.randomappsinc.instafood.models.RestaurantReview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Utility class to fetch restaurant info, so UI pieces don't need to do any networking **/
public class RestaurantFetcher {

    private static final String CURRENT_LOCATION_KEY = "currentLocation";
    private static final String SEARCH_TERM_KEY = "searchTerm";
    private static final String RESTAURANT_POOL_KEY = "restaurantPool";
    private static final String ALREADY_CHOSEN_KEY = "alreadyChosen";

    public interface Listener {
        void onRestaurantFetched(Restaurant newRestaurant);

        void onPhotosFetched(ArrayList<String> newPhotos);

        void onReviewsFetched(ArrayList<RestaurantReview> newReviews);

        void onClosingTimeFetched(@Nullable List<DailyHours> hoursInfo);
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
    private String searchTerm = "";
    private Random random;
    private ArrayList<Restaurant> restaurantPool;
    private ArrayList<Restaurant> alreadyChosen;

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

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean canReturnRestaurantImmediately() {
        return !restaurantPool.isEmpty();
    }

    public void fetchRestaurant() {
        restClient.cancelPhotosFetch();
        restClient.cancelReviewsFetch();

        if (restaurantPool.isEmpty()) {
            restClient.findRestaurants(location, searchTerm);
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

    public void onClosingTimeFetched(@Nullable List<DailyHours> closingTime) {
        listener.onClosingTimeFetched(closingTime);
    }

    public void onReviewsFetched(ArrayList<RestaurantReview> reviews) {
        listener.onReviewsFetched(reviews);
    }

    public void persistState(Bundle outState) {
        outState.putString(CURRENT_LOCATION_KEY, location);
        outState.putString(SEARCH_TERM_KEY, searchTerm);
        outState.putParcelableArrayList(RESTAURANT_POOL_KEY, restaurantPool);
        outState.putParcelableArrayList(ALREADY_CHOSEN_KEY, alreadyChosen);
    }

    public void extractState(Bundle savedInstanceState) {
        location = savedInstanceState.getString(CURRENT_LOCATION_KEY);
        searchTerm = savedInstanceState.getString(SEARCH_TERM_KEY);
        restaurantPool = savedInstanceState.getParcelableArrayList(RESTAURANT_POOL_KEY);
        alreadyChosen = savedInstanceState.getParcelableArrayList(ALREADY_CHOSEN_KEY);
    }

    public void clearEverything() {
        restClient.cancelRestaurantFetch();
        restClient.cancelPhotosFetch();
        restClient.cancelReviewsFetch();
        listener = null;
        location = null;
        searchTerm = "";
        restaurantPool.clear();
        alreadyChosen.clear();
    }
}
