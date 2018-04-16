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
            int yelpCurrentDay = getYelpDayFromCurrentDay();
            for (DailyHours dailyHours : dailyHoursList) {
                if (dailyHours.day == yelpCurrentDay) {
                    Calendar closingCalendar = Calendar.getInstance();
                    int closingHour = dailyHours.end / 100;
                    closingCalendar.set(Calendar.HOUR_OF_DAY, closingHour);

                    // If the restaurant is open until early morning, increase the day
                    if (closingHour >= 0 && closingHour <= 4) {
                        int currentDayInMonth = closingCalendar.get(Calendar.DAY_OF_MONTH);
                        closingCalendar.set(Calendar.DAY_OF_MONTH, currentDayInMonth + 1);
                    }

                    int closingMinutes = dailyHours.end % 100;
                    closingCalendar.set(Calendar.MINUTE, closingMinutes);

                    // Skip this hours object if its for an old, irrelevant shift
                    if (closingCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                        continue;
                    }

                    return closingCalendar;
                }
            }
            return null;
        }

        private int getYelpDayFromCurrentDay() {
            Calendar calendar = Calendar.getInstance();
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    return 0;
                case Calendar.TUESDAY:
                    return 1;
                case Calendar.WEDNESDAY:
                    return 2;
                case Calendar.THURSDAY:
                    return 3;
                case Calendar.FRIDAY:
                    return 4;
                case Calendar.SATURDAY:
                    return 5;
                case Calendar.SUNDAY:
                    return 6;
                default:
                    return 0;
            }
        }

        private int getCalendarDayFromYelpDay(int yelpDay) {
            switch (yelpDay) {
                case 0:
                    return Calendar.MONDAY;
                case 1:
                    return Calendar.TUESDAY;
                case 2:
                    return Calendar.WEDNESDAY;
                case 3:
                    return Calendar.THURSDAY;
                case 4:
                    return Calendar.FRIDAY;
                case 5:
                    return Calendar.SATURDAY;
                case 6:
                    return Calendar.SUNDAY;
                default:
                    return Calendar.MONDAY;
            }
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
