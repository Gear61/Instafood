package com.randomappsinc.instafood.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("title")
    @Expose
    private String title;

    String getTitle() {
        return title;
    }
}
