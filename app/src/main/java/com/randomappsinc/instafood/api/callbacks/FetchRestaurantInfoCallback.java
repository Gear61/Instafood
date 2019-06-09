package com.randomappsinc.instafood.api.callbacks;

import androidx.annotation.NonNull;

import com.randomappsinc.instafood.api.ApiConstants;
import com.randomappsinc.instafood.api.RestClient;
import com.randomappsinc.instafood.api.RestaurantFetcher;
import com.randomappsinc.instafood.api.models.BusinessInfoFetchError;
import com.randomappsinc.instafood.api.models.RestaurantInfo;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class FetchRestaurantInfoCallback implements Callback<RestaurantInfo> {

    @Override
    public void onResponse(@NonNull Call<RestaurantInfo> call, @NonNull Response<RestaurantInfo> response) {
        RestaurantFetcher restaurantFetcher = RestaurantFetcher.getInstance();
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            RestaurantInfo restaurantInfo = response.body();
            restaurantFetcher.onPhotosFetched(restaurantInfo.getPhotoUrls());
            restaurantFetcher.onClosingTimeFetched(restaurantInfo.getHoursInfo());
        } else if (response.code() == ApiConstants.HTTP_STATUS_FORBIDDEN) {
            Converter<ResponseBody, BusinessInfoFetchError> errorConverter =
                    RestClient.getInstance().getRetrofitInstance()
                            .responseBodyConverter(BusinessInfoFetchError.class, new Annotation[0]);
            try {
                BusinessInfoFetchError error = errorConverter.convert(response.errorBody());
                if (error.getCode().equals(ApiConstants.BUSINESS_UNAVAILABLE)) {
                    restaurantFetcher.onPhotosFetched(new ArrayList<String>());
                }
            } catch (IOException ignored) {}
        }
        // TODO: Process failure here
    }

    @Override
    public void onFailure(@NonNull Call<RestaurantInfo> call, @NonNull Throwable t) {
        // TODO: Process failure here
    }
}
