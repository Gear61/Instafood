package com.randomappsinc.instafood.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.api.RestaurantFetcher;
import com.randomappsinc.instafood.constants.DistanceUnit;
import com.randomappsinc.instafood.models.Filter;
import com.randomappsinc.instafood.persistence.PreferencesManager;
import com.randomappsinc.instafood.views.AttributePickerView;
import com.randomappsinc.instafood.views.PriceRangePickerView;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class FilterActivity extends AppCompatActivity {

    @BindView(R.id.search_term) EditText searchTerm;
    @BindView(R.id.clear_search) View clearSearch;
    @BindView(R.id.filter_content) View filterContent;
    @BindView(R.id.radius_slider) SeekBar radiusSlider;
    @BindView(R.id.radius_text) TextView distanceText;

    @BindInt(R.integer.max_radius_volume_miles) int maxMilesSliderValue;
    @BindInt(R.integer.max_radius_volume_kilometers) int maxKilometersSliderValue;

    @BindString(R.string.radius_text_miles) String radiusTemplateMiles;
    @BindString(R.string.radius_text_kilometers) String radiusTemplateKilometers;

    private Filter filter;
    private PriceRangePickerView priceRangePickerView;
    private AttributePickerView attributePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.filter);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                .colorRes(R.color.white)
                .actionBarSize());

        radiusSlider.setMax(PreferencesManager.get().getDistanceUnit().equals(DistanceUnit.MILES)
                ? maxMilesSliderValue
                : maxKilometersSliderValue);

        priceRangePickerView = new PriceRangePickerView(filterContent);
        attributePickerView = new AttributePickerView(filterContent);

        filter = PreferencesManager.get().getFilter();
        loadFilterIntoView();

        radiusSlider.setOnSeekBarChangeListener(mRadiusSliderListener);
    }

    private final SeekBar.OnSeekBarChangeListener mRadiusSliderListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setDistanceSliderText(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private void setDistanceSliderText(int sliderValue) {
        double progressAdjusted = (double) (sliderValue + 1) / 10.0;
        String template = PreferencesManager.get().getDistanceUnit().equals(DistanceUnit.MILES)
                ? radiusTemplateMiles
                : radiusTemplateKilometers;
        distanceText.setText(String.format(template, progressAdjusted));
    }

    private void loadFilterIntoView() {
        float filterDistanceValue = PreferencesManager.get().getDistanceUnit().equals(DistanceUnit.MILES)
                ? filter.getRadiusInMiles()
                : filter.getRadiusInKilometers();

        // 0.1km doesn't convert to 0.1 miles, so we need to have a safeguard to prevent negatives
        int convertedSliderValue = Math.round(Math.max((filterDistanceValue * 10) - 1, 0));
        radiusSlider.setProgress(convertedSliderValue);
        setDistanceSliderText(convertedSliderValue);

        priceRangePickerView.loadFilter(filter);
        attributePickerView.loadFilter(filter);
    }

    @OnClick(R.id.clear_search)
    public void clearSearch() {
        searchTerm.setText("");
    }

    @OnTextChanged(value = R.id.search_term, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onSearchInput(Editable input) {
        clearSearch.setVisibility(input.length() == 0 ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.apply_filter)
    public void applyFilter() {
        RestaurantFetcher.getInstance().clearRestaurants();

        double sliderVal = radiusSlider.getProgress();
        double distanceValue = (sliderVal + 1) / 10;

        if (PreferencesManager.get().getDistanceUnit().equals(DistanceUnit.MILES)) {
            filter.setRadiusWithMiles(distanceValue);
        } else {
            filter.setRadiusWithKilometers(distanceValue);
        }
        filter.setPricesRanges(priceRangePickerView.getPriceRanges());
        filter.setAttributes(attributePickerView.getAttributes());
        PreferencesManager.get().saveFilter(filter);
        Toast.makeText(this, R.string.filter_applied, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_bottom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.reset_all:
                filter.reset();
                loadFilterIntoView();
                return true;
        }
        return false;
    }
}
