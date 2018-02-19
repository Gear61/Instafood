package com.randomappsinc.instafood.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.models.RestaurantReview;
import com.randomappsinc.instafood.views.RestaurantReviewCell;

import java.util.List;

public class RestaurantReviewsAdapter implements RestaurantReviewCell.Listener {

    public interface Listener {
        void onReviewClicked(RestaurantReview review);
    }

    @NonNull private Listener listener;

    public RestaurantReviewsAdapter(@NonNull Listener listener) {
        this.listener = listener;
    }

    public void setReviews(List<RestaurantReview> reviews, ViewGroup reviewContainer, Context context) {
        reviewContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(context);
        if (reviews.isEmpty()) {
            View noReviewsCell = inflater.inflate(
                    R.layout.no_reviews_cell,
                    reviewContainer,
                    false);
            reviewContainer.addView(noReviewsCell);
        } else {
            for (RestaurantReview review : reviews) {
                View reviewCellParent = inflater.inflate(
                        R.layout.review_cell,
                        reviewContainer,
                        false);
                RestaurantReviewCell restaurantReviewCell = new RestaurantReviewCell(reviewCellParent, this);
                restaurantReviewCell.loadReview(review, context);
                reviewContainer.addView(reviewCellParent);
            }
        }
    }

    @Override
    public void onReviewClicked(RestaurantReview restaurantReview) {
        listener.onReviewClicked(restaurantReview);
    }
}
