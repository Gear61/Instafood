package com.randomappsinc.instafood.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestaurantPhotos {

    @SerializedName("photos")
    @Expose
    private List<String> photoUrls;

    public List<String> getPhotoUrls() {
        return photoUrls;
    }
}
