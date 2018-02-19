package com.randomappsinc.instafood.api;

import com.randomappsinc.instafood.api.models.RestaurantPhotos;
import com.randomappsinc.instafood.api.models.RestaurantReviewResults;
import com.randomappsinc.instafood.api.models.RestaurantSearchResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YelpService {

    @GET("v3/businesses/search")
    Call<RestaurantSearchResults> findRestaurants(@Query("term") String term,
                                                  @Query("location") String location,
                                                  @Query("limit") int limit,
                                                  @Query("open_now") boolean openNow);

    @GET("v3/businesses/{id}")
    Call<RestaurantPhotos> fetchRestaurantPhotos(@Path("id") String restaurantId);

    @GET("v3/businesses/{id}/reviews")
    Call<RestaurantReviewResults> fetchRestaurantReviews(@Path("id") String restaurantId);
}
