package com.randomappsinc.instafood.api.callbacks;

import android.support.annotation.NonNull;

import com.randomappsinc.instafood.api.ApiConstants;
import com.randomappsinc.instafood.api.RestClient;
import com.randomappsinc.instafood.api.models.BusinessInfoFetchError;
import com.randomappsinc.instafood.api.models.RestaurantReviewResults;
import com.randomappsinc.instafood.models.RestaurantReview;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class FetchReviewsCallback implements Callback<RestaurantReviewResults> {

    @Override
    public void onResponse(@NonNull Call<RestaurantReviewResults> call, @NonNull Response<RestaurantReviewResults> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            RestClient.getInstance().processReviews(response.body().getReviews());
        } else if (response.code() == ApiConstants.HTTP_STATUS_FORBIDDEN) {
            Converter<ResponseBody, BusinessInfoFetchError> errorConverter =
                    RestClient.getInstance().getRetrofitInstance()
                            .responseBodyConverter(BusinessInfoFetchError.class, new Annotation[0]);
            try {
                BusinessInfoFetchError error = errorConverter.convert(response.errorBody());
                if (error.getCode().equals(ApiConstants.BUSINESS_UNAVAILABLE)) {
                    RestClient.getInstance().processReviews(new ArrayList<RestaurantReview>());
                }
            } catch (IOException ignored) {}
        }
    }

    @Override
    public void onFailure(@NonNull Call<RestaurantReviewResults> call, @NonNull Throwable t) {
        // TODO: Handle failure here
    }
}
