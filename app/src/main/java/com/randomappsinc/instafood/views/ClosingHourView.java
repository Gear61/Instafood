package com.randomappsinc.instafood.views;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.utils.TimeUtils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClosingHourView {

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
            hoursText.setText(R.string.closing_time_unavailable);
        } else {
            hoursText.setText(TimeUtils.getHoursInfoText(closingHour));
        }
        hoursText.setVisibility(View.VISIBLE);
    }
}
