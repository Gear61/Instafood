package com.randomappsinc.instafood.views;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.api.models.DailyHours;
import com.randomappsinc.instafood.utils.StringUtils;
import com.randomappsinc.instafood.utils.TimeUtils;
import com.randomappsinc.instafood.utils.UIUtils;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClosingHourView {

    private static long MILLIS_IN_30_MINUTES = TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES);
    private static long MILLIS_IN_A_DAY = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

    @BindView(R.id.hours_text) TextView hoursText;
    @BindView(R.id.skeleton_hours_text) View skeletonHoursText;

    public ClosingHourView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    public void turnOnSkeletonLoading() {
        hoursText.setVisibility(View.GONE);
        skeletonHoursText.setVisibility(View.VISIBLE);
    }

    public void setHours(@Nullable List<DailyHours> hoursInfo) {
        skeletonHoursText.setVisibility(View.GONE);
        if (hoursInfo == null) {
            hoursText.setTextColor(UIUtils.getColor(R.color.dark_gray));
            hoursText.setText(R.string.closing_time_unavailable);
        } else {
            if (isOpen24Hours(hoursInfo)) {
                hoursText.setTextColor(UIUtils.getColor(R.color.green));
                hoursText.setText(R.string.open_24_hours);
            } else {
                Calendar closingCalendar = getTodaysClosingTime(hoursInfo);
                if (closingCalendar == null) {
                    hoursText.setTextColor(UIUtils.getColor(R.color.dark_gray));
                    hoursText.setText(R.string.closing_time_unavailable);
                } else {
                    long closingMillis = closingCalendar.getTimeInMillis();
                    long currentMillis = System.currentTimeMillis();
                    String formattedClosingHour = TimeUtils.getHoursInfoText(closingCalendar);
                    if (closingMillis - currentMillis <= MILLIS_IN_30_MINUTES) {
                        hoursText.setTextColor(UIUtils.getColor(R.color.red));
                        hoursText.setText(String.format(
                                StringUtils.getString(R.string.closing_at),
                                formattedClosingHour));
                    } else {
                        hoursText.setTextColor(UIUtils.getColor(R.color.green));
                        hoursText.setText(String.format(
                                StringUtils.getString(R.string.open_until),
                                formattedClosingHour));
                    }
                }
            }
        }
        hoursText.setVisibility(View.VISIBLE);
    }

    private boolean isOpen24Hours(List<DailyHours> hoursInfo) {
        int yelpCurrentDay = getYelpDayFromCurrentDay();
        for (DailyHours dailyHours : hoursInfo) {
            if (dailyHours.getDay() == yelpCurrentDay
                    && dailyHours.getStart() == 0
                    && dailyHours.getEnd() == 0) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private Calendar getTodaysClosingTime(List<DailyHours> hoursInfo) {
        int yelpCurrentDay = getYelpDayFromCurrentDay();

        boolean searchingLateAtNight = false;
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        // If the user is searching for something late at night (really early in the morning),
        // we need to get the hours for the previous day
        if (currentHour >= 0 && currentHour <= 4) {
            searchingLateAtNight = true;
            yelpCurrentDay--;
            // If it's currently Monday, we need to look for Sunday hours
            if (yelpCurrentDay < 0) {
                yelpCurrentDay = 6;
            }
        }

        for (DailyHours dailyHours : hoursInfo) {
            if (dailyHours.getDay() == yelpCurrentDay) {
                Calendar closingCalendar = Calendar.getInstance();
                int closingHour = dailyHours.getEnd() / 100;
                closingCalendar.set(Calendar.HOUR_OF_DAY, closingHour);

                int closingMinutes = dailyHours.getEnd() % 100;
                closingCalendar.set(Calendar.MINUTE, closingMinutes);

                long closingMillis = closingCalendar.getTimeInMillis();

                boolean earlyMorningHours = closingHour >= 0 && closingHour <= 4;

                // Add a day if we aren't searching at night and the hours are for early morning the next day
                if (earlyMorningHours && !searchingLateAtNight) {
                    closingMillis += MILLIS_IN_A_DAY;
                }
                // Remove a day if we are searching at night and the hours are for the previous day
                else if (!earlyMorningHours && searchingLateAtNight) {
                    closingMillis -= MILLIS_IN_A_DAY;
                }

                // Skip this hours object if it's for an old, irrelevant shift
                if (closingMillis < System.currentTimeMillis()) {
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
}
