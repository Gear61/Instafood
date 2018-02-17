package com.randomappsinc.automaticfoodfinder.api.callbacks;

import android.support.annotation.NonNull;

import com.randomappsinc.automaticfoodfinder.api.ApiConstants;
import com.randomappsinc.automaticfoodfinder.api.RestClient;
import com.randomappsinc.automaticfoodfinder.api.models.RestaurantSearchResults;
import com.randomappsinc.automaticfoodfinder.models.Restaurant;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindRestaurantsCallback implements Callback<RestaurantSearchResults> {

    @Override
    public void onResponse(@NonNull Call<RestaurantSearchResults> call, @NonNull Response<RestaurantSearchResults> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            List<Restaurant> restaurants = response.body().getRestaurants();
            if (restaurants.isEmpty()) {
                RestClient.getInstance().processRestaurant(null);
            } else {
                int randomIndex = (new Random()).nextInt(restaurants.size());
                Restaurant chosenOne = restaurants.get(randomIndex);
                RestClient.getInstance().processRestaurant(chosenOne);
                RestClient.getInstance().fetchRestaurantPhotos(chosenOne);
                RestClient.getInstance().fetchRestaurantReviews(chosenOne);
            }
        }
        // TODO: Process failure here
    }

    @Override
    public void onFailure(@NonNull Call<RestaurantSearchResults> call, @NonNull Throwable t) {
        // TODO: Deal with the place search failing case
    }
}
