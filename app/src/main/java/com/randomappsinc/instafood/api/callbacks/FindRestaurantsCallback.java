package com.randomappsinc.instafood.api.callbacks;

import android.support.annotation.NonNull;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.api.ApiConstants;
import com.randomappsinc.instafood.api.RestClient;
import com.randomappsinc.instafood.api.models.RestaurantSearchResults;
import com.randomappsinc.instafood.models.Restaurant;
import com.randomappsinc.instafood.views.UIUtils;

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
    public void onFailure(@NonNull Call<RestaurantSearchResults> call, @NonNull Throwable error) {}
}
