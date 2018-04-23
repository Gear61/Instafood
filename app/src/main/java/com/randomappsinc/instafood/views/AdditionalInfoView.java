package com.randomappsinc.instafood.views;

import android.view.View;
import android.widget.TextView;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.models.Restaurant;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AdditionalInfoView {

    @BindView(R.id.additional_info_container) View infoContainer;
    @BindView(R.id.additional_info_stub) View skeletonView;

    @BindView(R.id.pickup_icon) TextView pickupIcon;
    @BindView(R.id.delivery_icon) TextView deliveryIcon;
    @BindView(R.id.reservation_icon) TextView reservationIcon;

    @BindColor(R.color.green) int green;
    @BindColor(R.color.red) int red;

    public AdditionalInfoView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    public void turnOnSkeletonLoading() {
        infoContainer.setVisibility(View.GONE);
        skeletonView.setVisibility(View.VISIBLE);
    }

    public void loadRestaurant(Restaurant restaurant) {
        pickupIcon.setText(restaurant.supportsPickup() ? R.string.check_icon : R.string.x_icon);
        pickupIcon.setTextColor(restaurant.supportsPickup() ? green : red);
        deliveryIcon.setText(restaurant.supportsDelivery() ? R.string.check_icon : R.string.x_icon);
        deliveryIcon.setTextColor(restaurant.supportsDelivery() ? green : red);
        reservationIcon.setText(restaurant.supportsReservations() ? R.string.check_icon : R.string.x_icon);
        reservationIcon.setTextColor(restaurant.supportsReservations() ? green : red);
        skeletonView.setVisibility(View.GONE);
        infoContainer.setVisibility(View.VISIBLE);
    }
}
