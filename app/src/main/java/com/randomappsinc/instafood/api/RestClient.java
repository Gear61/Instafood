package com.randomappsinc.instafood.api;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.randomappsinc.instafood.api.callbacks.FetchRestaurantInfoCallback;
import com.randomappsinc.instafood.api.callbacks.FetchReviewsCallback;
import com.randomappsinc.instafood.api.callbacks.FindRestaurantsCallback;
import com.randomappsinc.instafood.api.models.RestaurantInfo;
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

    protected Retrofit retrofit;
    protected YelpService yelpService;
    protected Handler handler;

    protected Call<RestaurantSearchResults> currentFindRestaurantsCall;
    protected Call<RestaurantInfo> currentFetchPhotosCall;
    protected Call<RestaurantReviewResults> currentFetchReviewsCall;

    public static RestClient getInstance() {
        if (instance == null ) {
            synchronized (RestClient.class) {
                if (instance == null) {
                    instance = new RestClient();
                }
            }
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

    void findRestaurants(final String location, final String searchTerm, Context context) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        Filter filter = preferencesManager.getFilter();
        handler.post(() -> {
            if (currentFindRestaurantsCall != null) {
                currentFindRestaurantsCall.cancel();
            }
            String finalSearchTerm = TextUtils.isEmpty(searchTerm)
                    ? ApiConstants.DEFAULT_SEARCH_TERM
                    : searchTerm;
            int numResults = TextUtils.isEmpty(searchTerm)
                    ? ApiConstants.NUM_RESTAURANT_RESULTS_NO_SEARCH_TERM
                    : ApiConstants.NUM_RESTAURANT_RESULTS_WITH_SEARCH_TERM;
            currentFindRestaurantsCall = yelpService.findRestaurants(
                    finalSearchTerm,
                    location,
                    numResults,
                    true,
                    (int) filter.getRadius(),
                    filter.getPriceRangesString(),
                    filter.getAttributesString());
            currentFindRestaurantsCall.enqueue(new FindRestaurantsCallback(context));
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
                currentFetchPhotosCall.enqueue(new FetchRestaurantInfoCallback());
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
