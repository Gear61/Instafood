package com.randomappsinc.instafood.api.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RestaurantInfo {

    @SerializedName("photos")
    @Expose
    private ArrayList<String> photoUrls;

    @SerializedName("hours")
    @Expose
    private List<HoursInfo> hoursInfo;

    private class HoursInfo {
        @SerializedName("open")
        @Expose
        private List<DailyHours> dailyHoursList;

        List<DailyHours> getDailyHoursList() {
            return dailyHoursList;
        }
    }

    public ArrayList<String> getPhotoUrls() {
        return photoUrls;
    }

    @Nullable
    public List<DailyHours> getHoursInfo() {
        return (hoursInfo == null || hoursInfo.isEmpty()) ? null : hoursInfo.get(0).getDailyHoursList();
    }
}
