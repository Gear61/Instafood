package com.randomappsinc.instafood.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DailyHours {

    @SerializedName("start")
    @Expose
    private int start;

    @SerializedName("end")
    @Expose
    private int end;

    @SerializedName("day")
    @Expose
    private int day;

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDay() {
        return day;
    }
}
