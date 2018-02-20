package com.randomappsinc.instafood.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RestaurantPhotos {

    @SerializedName("photos")
    @Expose
    private ArrayList<String> photoUrls;

    public ArrayList<String> getPhotoUrls() {
        return photoUrls;
    }
}
