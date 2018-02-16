package com.randomappsinc.automaticfoodfinder.api.callbacks;

import android.support.annotation.NonNull;

import com.randomappsinc.automaticfoodfinder.api.ApiConstants;
import com.randomappsinc.automaticfoodfinder.api.RestClient;
import com.randomappsinc.automaticfoodfinder.api.models.BusinessInfoFetchError;
import com.randomappsinc.automaticfoodfinder.api.models.RestaurantPhotos;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class FetchPhotosCallback implements Callback<RestaurantPhotos> {

    @Override
    public void onResponse(@NonNull Call<RestaurantPhotos> call, @NonNull Response<RestaurantPhotos> response) {
        if (response.code() == ApiConstants.HTTP_STATUS_OK) {
            RestClient.getInstance().processPhotos(response.body().getPhotoUrls());
        } else if (response.code() == ApiConstants.HTTP_STATUS_FORBIDDEN) {
            Converter<ResponseBody, BusinessInfoFetchError> errorConverter =
                    RestClient.getInstance().getRetrofitInstance()
                            .responseBodyConverter(BusinessInfoFetchError.class, new Annotation[0]);
            try {
                BusinessInfoFetchError error = errorConverter.convert(response.errorBody());
                if (error.getCode().equals(ApiConstants.BUSINESS_UNAVAILABLE)) {
                    RestClient.getInstance().processPhotos(new ArrayList<String>());
                }
            } catch (IOException ignored) {}
        }
        // TODO: Process failure here
    }

    @Override
    public void onFailure(@NonNull Call<RestaurantPhotos> call, @NonNull Throwable t) {
        // TODO: Process failure here
    }
}
