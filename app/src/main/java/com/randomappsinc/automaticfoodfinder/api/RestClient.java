package com.randomappsinc.automaticfoodfinder.api;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.randomappsinc.automaticfoodfinder.api.callbacks.FetchPhotosCallback;
import com.randomappsinc.automaticfoodfinder.api.callbacks.FetchReviewsCallback;
import com.randomappsinc.automaticfoodfinder.api.callbacks.FindRestaurantsCallback;
import com.randomappsinc.automaticfoodfinder.api.models.RestaurantPhotos;
import com.randomappsinc.automaticfoodfinder.api.models.RestaurantReviewResults;
import com.randomappsinc.automaticfoodfinder.api.models.RestaurantSearchResults;
import com.randomappsinc.automaticfoodfinder.models.Restaurant;
import com.randomappsinc.automaticfoodfinder.models.RestaurantReview;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public interface RestaurantListener {
        void onRestaurantFetched(Restaurant restaurant);
    }

    public interface PhotosListener {
        void onPhotosFetched(List<String> photos);
    }

    public interface ReviewsListener {
        void onReviewsFetched(List<RestaurantReview> photos);
    }

    private static final RestaurantListener DUMMY_RESTAURANT_LISTENER = new RestaurantListener() {
        @Override
        public void onRestaurantFetched(@Nullable Restaurant restaurant) {}
    };

    private static final PhotosListener DUMMY_PHOTOS_LISTENER = new PhotosListener() {
        @Override
        public void onPhotosFetched(List<String> photos) {}
    };

    private static final ReviewsListener DUMMY_REVIEWS_LISTENER = new ReviewsListener() {
        @Override
        public void onReviewsFetched(List<RestaurantReview> reviews) {}
    };

    private static RestClient instance;

    private Retrofit retrofit;
    private YelpService yelpService;
    private Handler handler;

    // Restaurants
    @NonNull private RestaurantListener restaurantListener = DUMMY_RESTAURANT_LISTENER;
    private Call<RestaurantSearchResults> currentFindRestaurantsCall;

    // Photos
    @NonNull private PhotosListener photosListener = DUMMY_PHOTOS_LISTENER;
    private Call<RestaurantPhotos> currentFetchPhotosCall;

    // Reviews
    @NonNull private ReviewsListener reviewsListener = DUMMY_REVIEWS_LISTENER;
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

    public void findRestaurant(final String location) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFindRestaurantsCall != null) {
                    currentFindRestaurantsCall.cancel();
                }
                if (currentFetchPhotosCall != null) {
                    currentFetchPhotosCall.cancel();
                }
                if (currentFetchReviewsCall != null) {
                    currentFetchReviewsCall.cancel();
                }
                currentFindRestaurantsCall = yelpService.findRestaurants(
                        ApiConstants.DEFAULT_SEARCH_TERM,
                        location,
                        ApiConstants.DEFAULT_NUM_RESTAURANT_RESULTS,
                        true);
                currentFindRestaurantsCall.enqueue(new FindRestaurantsCallback());
            }
        });
    }

    public void registerRestaurantListener(RestaurantListener restaurantsListener) {
        this.restaurantListener = restaurantsListener;
    }

    public void unregisterRestaurantListener() {
        restaurantListener = DUMMY_RESTAURANT_LISTENER;
    }

    public void processRestaurant(@Nullable Restaurant restaurant) {
        restaurantListener.onRestaurantFetched(restaurant);
    }

    public void cancelRestaurantFetch() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFindRestaurantsCall != null) {
                    currentFindRestaurantsCall.cancel();
                }
            }
        });
    }

    public void fetchRestaurantPhotos(final Restaurant restaurant) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFetchPhotosCall != null) {
                    currentFetchPhotosCall.cancel();
                }
                currentFetchPhotosCall = yelpService.fetchRestaurantPhotos(restaurant.getId());
                currentFetchPhotosCall.enqueue(new FetchPhotosCallback());
            }
        });
    }

    public void registerPhotosListener(PhotosListener photosListener) {
        this.photosListener = photosListener;
    }

    public void unregisterPhotosListener() {
        photosListener = DUMMY_PHOTOS_LISTENER;
    }

    public void processPhotos(List<String> photoUrls) {
        photosListener.onPhotosFetched(photoUrls);
    }

    public void cancelPhotosFetch() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFetchPhotosCall != null) {
                    currentFetchPhotosCall.cancel();
                }
            }
        });
    }

    public void fetchRestaurantReviews(final Restaurant restaurant) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentFetchReviewsCall != null) {
                    currentFetchReviewsCall.cancel();
                }
                currentFetchReviewsCall = yelpService.fetchRestaurantReviews(restaurant.getId());
                currentFetchReviewsCall.enqueue(new FetchReviewsCallback());
            }
        });
    }

    public void registerReviewsListener(ReviewsListener reviewsListener) {
        this.reviewsListener = reviewsListener;
    }

    public void unregisterReviewsListener() {
        reviewsListener = DUMMY_REVIEWS_LISTENER;
    }

    public void processReviews(List<RestaurantReview> reviews) {
        reviewsListener.onReviewsFetched(reviews);
    }

    public void cancelReviewsFetch() {
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
