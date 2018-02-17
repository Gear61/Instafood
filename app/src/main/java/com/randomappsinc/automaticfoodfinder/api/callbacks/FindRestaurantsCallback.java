package com.randomappsinc.automaticfoodfinder.api.callbacks;

import android.support.annotation.NonNull;

import com.randomappsinc.automaticfoodfinder.R;
import com.randomappsinc.automaticfoodfinder.api.ApiConstants;
import com.randomappsinc.automaticfoodfinder.api.RestClient;
import com.randomappsinc.automaticfoodfinder.api.models.RestaurantSearchResults;
import com.randomappsinc.automaticfoodfinder.models.Restaurant;
import com.randomappsinc.automaticfoodfinder.views.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindRestaurantsCallback implements Callback<RestaurantSearchResults> {

    private Set<String> restaurantsToAvoid;

    public FindRestaurantsCallback(Set<String> alreadyVisitedIds) {
        restaurantsToAvoid = alreadyVisitedIds;
    }

    @Override
    public void onResponse(
            @NonNull Call<RestaurantSearchResults> call,
            @NonNull Response<RestaurantSearchResults> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            List<Restaurant> restaurants = response.body().getRestaurants();
            if (restaurants.isEmpty()) {
                UIUtils.showLongToast(R.string.no_open_restaurants);
            } else {
                List<Restaurant> freshRestaurants = new ArrayList<>();
                for (Restaurant restaurant : restaurants) {
                    if (!restaurantsToAvoid.contains(restaurant.getId())) {
                        freshRestaurants.add(restaurant);
                    }
                }
                Restaurant chosenOne;
                Random random = new Random();
                if (freshRestaurants.isEmpty()) {
                    restaurantsToAvoid.clear();
                    int randomIndex = random.nextInt(restaurants.size());
                    chosenOne = restaurants.get(randomIndex);
                } else {
                    int randomIndex = random.nextInt(freshRestaurants.size());
                    chosenOne = freshRestaurants.get(randomIndex);
                }
                RestClient.getInstance().processRestaurant(chosenOne);
                RestClient.getInstance().fetchRestaurantPhotos(chosenOne);
                RestClient.getInstance().fetchRestaurantReviews(chosenOne);
            }
        } else {
            UIUtils.showLongToast(R.string.restaurant_call_bad_result);
        }
    }

    @Override
    public void onFailure(@NonNull Call<RestaurantSearchResults> call, @NonNull Throwable t) {
        UIUtils.showLongToast(R.string.restaurant_call_failed);
    }
}
