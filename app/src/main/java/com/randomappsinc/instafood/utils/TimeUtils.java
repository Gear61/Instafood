package com.randomappsinc.instafood.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    private static final String REVIEW_DATE_FORMAT = "MMMM d, yyyy";

    private static final String OPEN_UNTIL_FORMAT = "h:mm a";

    public static String getReviewDateTime(long unixTime) {
        Date date = new Date(unixTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(REVIEW_DATE_FORMAT, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }

    public static String getHoursInfoText(long closingTimeMillis) {
        Date date = new Date(closingTimeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(OPEN_UNTIL_FORMAT, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }
}
