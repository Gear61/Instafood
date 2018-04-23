package com.randomappsinc.instafood.views;

import android.view.View;

import com.randomappsinc.instafood.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdditionalInfoView {

    @BindView(R.id.additional_info_container) View infoContainer;
    @BindView(R.id.additional_info_stub) View skeletonView;

    public AdditionalInfoView(View rootView) {
        ButterKnife.bind(this, rootView);
    }

    public void turnOnSkeletonLoading() {
        infoContainer.setVisibility(View.GONE);
        skeletonView.setVisibility(View.VISIBLE);
    }
}
