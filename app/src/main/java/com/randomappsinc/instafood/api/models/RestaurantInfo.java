package com.randomappsinc.instafood.api.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;

public class RestaurantInfo {

    @SerializedName("photos")
    @Expose
    private ArrayList<String> photoUrls;

    @SerializedName("hours")
    @Expose
    private ArrayList<HoursInfo> hoursInfo;

    public class HoursInfo {

        @SerializedName("open")
        @Expose
        private ArrayList<DailyHours> dailyHoursList;

        class DailyHours {
            @SerializedName("end")
            @Expose
            private int end;

            @SerializedName("day")
            @Expose
            private int day;
        }

        @Nullable
        Calendar getTodaysClosingTime() {
            Calendar currentCalendar = Calendar.getInstance();
            int currentDay = currentCalendar.get(Calendar.DAY_OF_WEEK);
            int yelpCurrentDay = 0;
            switch (currentDay) {
                case Calendar.MONDAY:
                    yelpCurrentDay = 0;
                    break;
                case Calendar.TUESDAY:
                    yelpCurrentDay = 1;
                    break;
                case Calendar.WEDNESDAY:
                    yelpCurrentDay = 2;
                    break;
                case Calendar.THURSDAY:
                    yelpCurrentDay = 3;
                    break;
                case Calendar.FRIDAY:
                    yelpCurrentDay = 4;
                    break;
                case Calendar.SATURDAY:
                    yelpCurrentDay = 5;
                    break;
                case Calendar.SUNDAY:
                    yelpCurrentDay = 6;
                    break;
            }
            for (DailyHours dailyHours : dailyHoursList) {
                if (dailyHours.day == yelpCurrentDay) {
                    int closingHour = dailyHours.end / 100;
                    currentCalendar.set(Calendar.HOUR_OF_DAY, closingHour);
                    int closingMinutes = dailyHours.end % 100;
                    currentCalendar.set(Calendar.MINUTE, closingMinutes);
                    return currentCalendar;
                }
            }
            return null;
        }
    }

    public ArrayList<String> getPhotoUrls() {
        return photoUrls;
    }

    @Nullable
    public Calendar getClosingTime() {
        return hoursInfo.isEmpty() ? null : hoursInfo.get(0).getTodaysClosingTime();
    }
}
