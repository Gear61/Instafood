package com.randomappsinc.instafood.api;

import android.os.Handler;
import android.os.HandlerThread;

import com.randomappsinc.instafood.api.callbacks.FetchPhotosCallback;
import com.randomappsinc.instafood.api.callbacks.FetchReviewsCallback;
import com.randomappsinc.instafood.api.callbacks.FindRestaurantsCallback;
import com.randomappsinc.instafood.api.models.RestaurantPhotos;
import com.randomappsinc.instafood.api.models.RestaurantReviewResults;
import com.randomappsinc.instafood.api.models.RestaurantSearchResults;
import com.randomappsinc.instafood.models.Filter;
import com.randomappsinc.instafood.models.Restaurant;
import com.randomappsinc.instafood.persistence.PreferencesManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static RestClient instance;

    private Retrofit retrofit;
    private YelpService yelpService;
    private Handler handler;

    private Call<RestaurantSearchResults> currentFindRestaurantsCall;
    private Call<RestaurantPhotos> currentFetchPhotosCall;
    private Call<RestaurantReviewResults> currentFetchReviewsCall;

    public static RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }
        return instance;
    }

    private RestClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        yelpService = retrofit.create(YelpService.class);

        HandlerThread backgroundThread = new HandlerThread("");
        backgroundThread.start();
        handler = new Handler(backgroundThread.getLooper());
    }

    public Retrofit getRetrofitInstance() {
        return retrofit;
    }

    void findRestaurants(final String location) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFindRestaurantsCall != null) {
                    currentFindRestaurantsCall.cancel();
                }
                Filter filter = PreferencesManager.get().getFilter();
                currentFindRestaurantsCall = yelpService.findRestaurants(
                        ApiConstants.DEFAULT_SEARCH_TERM,
                        location,
                        ApiConstants.DEFAULT_NUM_RESTAURANT_RESULTS,
                        true,
                        (int) filter.getRadius(),
                        filter.getPriceRangesString(),
                        filter.getAttributesString());
                currentFindRestaurantsCall.enqueue(new FindRestaurantsCallback());
            }
        });
    }

    void cancelRestaurantFetch() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFindRestaurantsCall != null) {
                    currentFindRestaurantsCall.cancel();
                }
            }
        });
    }

    void fetchRestaurantPhotos(final Restaurant restaurant) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                currentFetchPhotosCall = yelpService.fetchRestaurantPhotos(restaurant.getId());
                currentFetchPhotosCall.enqueue(new FetchPhotosCallback());
            }
        });
    }

    void cancelPhotosFetch() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFetchPhotosCall != null) {
                    currentFetchPhotosCall.cancel();
                }
            }
        });
    }

    void fetchRestaurantReviews(final Restaurant restaurant) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                currentFetchReviewsCall = yelpService.fetchRestaurantReviews(restaurant.getId());
                currentFetchReviewsCall.enqueue(new FetchReviewsCallback());
            }
        });
    }

    void cancelReviewsFetch() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFetchReviewsCall != null) {
                    currentFetchReviewsCall.cancel();
                }
            }
        });
    }
}
