package com.randomappsinc.instafood.views;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.utils.StringUtils;
import com.randomappsinc.instafood.utils.TimeUtils;
import com.randomappsinc.instafood.utils.UIUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClosingHourView {

    private static long MILLIS_IN_30_MINUTES = TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES);

    @BindView(R.id.hours_text) TextView hoursText;
    @BindView(R.id.skeleton_hours_text) View skeletonHoursText;

    public ClosingHourView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    public void turnOnSkeletonLoading() {
        hoursText.setVisibility(View.GONE);
        skeletonHoursText.setVisibility(View.VISIBLE);
    }

    public void setClosingHour(@Nullable Calendar closingHour) {
        skeletonHoursText.setVisibility(View.GONE);
        if (closingHour == null) {
            hoursText.setTextColor(UIUtils.getColor(R.color.dark_gray));
            hoursText.setText(R.string.closing_time_unavailable);
        } else {
            long closingMillis = closingHour.getTimeInMillis();
            long currentMillis = System.currentTimeMillis();
            String formattedClosingHour = TimeUtils.getHoursInfoText(closingHour);
            if (closingMillis - currentMillis <= MILLIS_IN_30_MINUTES) {
                hoursText.setTextColor(UIUtils.getColor(R.color.red));
                hoursText.setText(String.format(StringUtils.getString(R.string.closing_at), formattedClosingHour));
            } else {
                hoursText.setTextColor(UIUtils.getColor(R.color.green));
                hoursText.setText(String.format(StringUtils.getString(R.string.open_until), formattedClosingHour));
            }
        }
        hoursText.setVisibility(View.VISIBLE);
    }
}
