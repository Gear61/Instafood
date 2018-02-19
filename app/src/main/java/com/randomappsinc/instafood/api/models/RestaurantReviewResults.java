package com.randomappsinc.instafood.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.randomappsinc.instafood.models.RestaurantReview;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RestaurantReviewResults {

    @SerializedName("reviews")
    @Expose
    private List<RestaurantReviewResult> reviews;

    public class RestaurantReviewResult {

        @SerializedName("rating")
        @Expose
        private double rating;

        @SerializedName("text")
        @Expose
        private String text;

        @SerializedName("time_created")
        @Expose
        private String timeCreated;

        @SerializedName("url")
        @Expose
        private String url;

        @SerializedName("user")
        @Expose
        private User user;

        public class User {
            @SerializedName("name")
            @Expose
            private String name;

            @SerializedName("image_url")
            @Expose
            private String imageUrl;

            String getName() {
                return name;
            }

            String getImageUrl() {
                return imageUrl;
            }
        }

        private long convertToUnixTime(String originalTime) {
            if (originalTime == null) {
                return 0L;
            }

            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            originalFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date date;
            try {
                date = originalFormat.parse(originalTime);
            } catch (ParseException exception) {
                throw new RuntimeException("Incorrect time format: " + originalTime);
            }
            return date.getTime();
        }

        RestaurantReview toRestaurantReview() {
            RestaurantReview restaurantReview = new RestaurantReview();
            restaurantReview.setRating(rating);
            restaurantReview.setText(text);
            restaurantReview.setTimeCreated(convertToUnixTime(timeCreated));
            restaurantReview.setUrl(url);
            restaurantReview.setUsername(user.getName());
            restaurantReview.setUserImageUrl(user.getImageUrl());
            return restaurantReview;
        }
    }

    public List<RestaurantReview> getReviews() {
        List<RestaurantReview> restaurantReviews = new ArrayList<>();
        for (RestaurantReviewResult reviewResult : reviews) {
            restaurantReviews.add(reviewResult.toRestaurantReview());
        }

        // Put the most recent reviews first, because Yelp doesn't do that
        Collections.sort(restaurantReviews, new Comparator<RestaurantReview>() {
            @Override
            public int compare(RestaurantReview review1, RestaurantReview review2) {
                if (review1.getTimeCreated() > review2.getTimeCreated()) {
                    return -1;
                } else if (review1.getTimeCreated() < review2.getTimeCreated()) {
                    return 1;
                }
                return 0;
            }
        });
        return restaurantReviews;
    }
}
