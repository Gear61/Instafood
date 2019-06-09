package com.randomappsinc.instafood.api.callbacks;

import androidx.annotation.NonNull;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.api.ApiConstants;
import com.randomappsinc.instafood.api.RestaurantFetcher;
import com.randomappsinc.instafood.api.models.RestaurantSearchResults;
import com.randomappsinc.instafood.models.Restaurant;
import com.randomappsinc.instafood.utils.UIUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindRestaurantsCallback implements Callback<RestaurantSearchResults> {

    @Override
    public void onResponse(
            @NonNull Call<RestaurantSearchResults> call,
            @NonNull Response<RestaurantSearchResults> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            List<Restaurant> restaurants = response.body().getRestaurants();
            if (restaurants.isEmpty()) {
                UIUtils.showLongToast(R.string.no_open_restaurants);
            } else {
                RestaurantFetcher.getInstance().setRestaurantList(restaurants);
            }
        } else {
            UIUtils.showLongToast(R.string.restaurant_call_bad_result);
        }
    }

    @Override
    public void onFailure(@NonNull Call<RestaurantSearchResults> call, @NonNull Throwable error) {}
}
