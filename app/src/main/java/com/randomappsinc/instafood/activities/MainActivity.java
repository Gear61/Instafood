package com.randomappsinc.instafood.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.adapters.RestaurantPhotosAdapter;
import com.randomappsinc.instafood.adapters.RestaurantReviewsAdapter;
import com.randomappsinc.instafood.api.RestaurantFetcher;
import com.randomappsinc.instafood.api.models.DailyHours;
import com.randomappsinc.instafood.location.LocationManager;
import com.randomappsinc.instafood.models.Restaurant;
import com.randomappsinc.instafood.models.RestaurantReview;
import com.randomappsinc.instafood.persistence.PreferencesManager;
import com.randomappsinc.instafood.utils.UIUtils;
import com.randomappsinc.instafood.views.AdditionalInfoView;
import com.randomappsinc.instafood.views.ClosingHourView;
import com.randomappsinc.instafood.views.RestaurantInfoView;
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends StandardActivity implements RestaurantReviewsAdapter.Listener,
        RestaurantPhotosAdapter.Listener, OnMapReadyCallback, LocationManager.Listener,
        ShakeDetector.Listener, SwipeRefreshLayout.OnRefreshListener {

    private static final int FILTER_REQUEST_CODE = 1;

    private static final String RESTAURANT_KEY = "restaurant";

    @BindView(R.id.swipe_container) SwipeRefreshLayout swipeLayout;
    @BindView(R.id.homepage_scrollview) ScrollView scrollView;
    @BindView(R.id.hours_card) View hoursCard;
    @BindView(R.id.restaurant_map) MapView restaurantMap;
    @BindView(R.id.restaurant_info_parent) View restaurantInfo;
    @BindView(R.id.photos_stub) View photosStub;
    @BindView(R.id.restaurant_photos) RecyclerView photosList;
    @BindView(R.id.reviews_stub) View reviewsStub;
    @BindView(R.id.reviews_container) LinearLayout reviewsContainer;
    @BindView(R.id.additional_info_card) View additionalInfoCard;

    protected RestaurantFetcher restaurantFetcher;
    protected Restaurant restaurant;
    protected ClosingHourView closingHourView;
    protected GoogleMap googleMap;
    protected RestaurantInfoView restaurantInfoView;
    protected RestaurantPhotosAdapter photosAdapter;
    protected RestaurantReviewsAdapter reviewsAdapter;
    protected AdditionalInfoView additionalInfoView;
    protected LocationManager locationManager;
    protected boolean denialLock;
    protected ShakeDetector shakeDetector;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManager = new PreferencesManager(this);
        preferencesManager.logAppOpen();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        locationManager = new LocationManager(this, this);
        shakeDetector = new ShakeDetector(this);

        closingHourView = new ClosingHourView(hoursCard);
        restaurantMap.onCreate(savedInstanceState);
        restaurantMap.getMapAsync(this);

        photosAdapter = new RestaurantPhotosAdapter(this, this);
        photosList.setAdapter(photosAdapter);
        reviewsAdapter = new RestaurantReviewsAdapter(this);

        restaurantInfoView = new RestaurantInfoView(
                restaurantInfo,
                new IconDrawable(this, IoniconsIcons.ion_location).colorRes(R.color.dark_gray));

        additionalInfoView = new AdditionalInfoView(additionalInfoCard);

        restaurantFetcher = RestaurantFetcher.getInstance();
        restaurantFetcher.setListener(restaurantInfoListener);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.red, R.color.yellow, R.color.green, R.color.blue);

        if (savedInstanceState != null) {
            restaurant = savedInstanceState.getParcelable(RESTAURANT_KEY);
            restaurantFetcher.extractState(savedInstanceState);

            if (restaurant == null) {
                return;
            }

            restaurantInfoListener.onRestaurantFetched(restaurant);
            if (restaurant.getPhotoUrls() != null) {
                restaurantInfoListener.onPhotosFetched(restaurant.getPhotoUrls());
            }
            if (restaurant.getReviews() != null) {
                restaurantInfoListener.onReviewsFetched(restaurant.getReviews());
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        restaurantMap.onSaveInstanceState(outState);
        if (restaurant != null) {
            outState.putParcelable(RESTAURANT_KEY, restaurant);
        }
        restaurantFetcher.persistState(outState);
    }

    @Override
    public void onRefresh() {
        resetAndFindNewRestaurant();
    }

    @Override
    public void onResume() {
        super.onResume();
        restaurantMap.onResume();

        // Re-render distance text since they might have changed their distance setting
        restaurantInfoView.renderDistanceText();

        // Run this here instead of onCreate() to cover the case where they return from turning on location
        if (restaurantFetcher.getLocation() == null && !denialLock) {
            locationManager.fetchCurrentLocation();
        }

        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        }
    }

    @Override
    public void onLocationFetched(String location) {
        swipeLayout.setRefreshing(true);
        restaurantFetcher.setLocation(location);
        restaurantFetcher.fetchRestaurant(this);
    }

    private final RestaurantFetcher.Listener restaurantInfoListener = new RestaurantFetcher.Listener() {
        @Override
        public void onRestaurantFetched(Restaurant freshRestaurant) {
            swipeLayout.setRefreshing(false);
            swipeLayout.setEnabled(true);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
            photosList.smoothScrollToPosition(0);

            restaurant = freshRestaurant;
            restaurantInfoView.loadRestaurant(restaurant);
            if (googleMap != null) {
                loadRestaurantLocationInMap();
            }
            additionalInfoView.loadRestaurant(restaurant);

            UIUtils.askForRatingIfAppropriate(MainActivity.this);
        }

        @Override
        public void onPhotosFetched(ArrayList<String> freshPhotos) {
            restaurant.setPhotoUrls(freshPhotos);
            photosStub.setVisibility(View.INVISIBLE);
            photosAdapter.setPhotoUrls(freshPhotos);
            photosList.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReviewsFetched(ArrayList<RestaurantReview> freshReviews) {
            restaurant.setReviews(freshReviews);
            reviewsStub.setVisibility(View.GONE);
            reviewsContainer.setVisibility(View.VISIBLE);
            reviewsAdapter.setReviews(freshReviews, reviewsContainer, MainActivity.this);
        }

        @Override
        public void onClosingTimeFetched(@Nullable List<DailyHours> hoursInfo) {
            closingHourView.setHours(hoursInfo);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.setOnMapClickListener(mapClickListener);
        if (restaurant != null) {
            loadRestaurantLocationInMap();
        }
    }

    protected void loadRestaurantLocationInMap() {
        googleMap.clear();
        LatLng coordinates = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(coordinates)
                .title(restaurant.getName()));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordinates)
                .zoom(16)
                .bearing(0)
                .tilt(0)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private final GoogleMap.OnMapClickListener mapClickListener = latLng -> navigateToRestaurant();

    protected void navigateToRestaurant() {
        if (restaurant == null) {
            return;
        }

        String mapUri = "google.navigation:q=" + restaurant.getAddress() + " " + restaurant.getName();
        startActivity(Intent.createChooser(
                new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapUri)),
                getString(R.string.navigate_with)));
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.stay);
    }

    @OnClick(R.id.restaurant_thumbnail)
    public void onThumbnailClicked() {
        if (TextUtils.isEmpty(restaurant.getImageUrl())) {
            return;
        }

        Intent intent = new Intent(this, PictureFullViewActivity.class);
        ArrayList<String> imageUrl = new ArrayList<>(1);
        imageUrl.add(restaurant.getImageUrl());
        intent.putStringArrayListExtra(PictureFullViewActivity.IMAGE_URLS_KEY, imageUrl);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @OnClick(R.id.navigate_button)
    public void onNavigateClicked() {
        navigateToRestaurant();
    }

    @OnClick(R.id.share_button)
    public void shareRestaurant() {
        if (restaurant == null) {
            return;
        }

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(R.string.share_restaurant_with)
                .setType("text/plain")
                .setText(restaurant.getShareText(this))
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.stay);
        }
    }

    @OnClick(R.id.call_button)
    public void callRestaurant() {
        if (restaurant == null) {
            return;
        }

        String phoneUri = "tel:" + restaurant.getPhoneNumber();
        startActivity(Intent.createChooser(
                new Intent(Intent.ACTION_DIAL, Uri.parse(phoneUri)),
                getString(R.string.call_with)));
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.stay);
    }

    private void resetAndFindNewRestaurant() {
        if (restaurantFetcher.getLocation() == null) {
            locationManager.fetchCurrentLocation();
        } else {
            restaurant = null;
            closingHourView.turnOnSkeletonLoading();
            turnOnSkeletonLoading();
            restaurantFetcher.fetchRestaurant(this);
        }
    }

    private void turnOnSkeletonLoading() {
        if (!restaurantFetcher.canReturnRestaurantImmediately()) {
            restaurantInfoView.setSkeletonVisibility(true);
            additionalInfoView.turnOnSkeletonLoading();
        }
        photosList.setVisibility(View.INVISIBLE);
        photosStub.setVisibility(View.VISIBLE);
        reviewsContainer.setVisibility(View.GONE);
        reviewsStub.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReviewClicked(RestaurantReview review) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
        startActivity(intent);
    }

    @Override
    public void onPhotoClicked(ArrayList<String> imageUrls, int position) {
        Intent intent = new Intent(this, PictureFullViewActivity.class);
        intent.putStringArrayListExtra(PictureFullViewActivity.IMAGE_URLS_KEY, imageUrls);
        intent.putExtra(PictureFullViewActivity.POSITION_KEY, position);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode != LocationManager.LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        // No need to check if the location permission has been granted because of the onResume() block
        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            denialLock = true;
            locationManager.showLocationPermissionDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationManager.LOCATION_SERVICES_CODE:
                if (resultCode == RESULT_OK) {
                    UIUtils.showLongToast(R.string.location_services_on, this);
                    locationManager.fetchAutomaticLocation();
                } else {
                    denialLock = true;
                    locationManager.showLocationDenialDialog();
                }
                break;
            case FILTER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    resetAndFindNewRestaurant();
                }
                break;
        }
    }

    @Override
    public void onServicesOrPermissionChoice() {
        denialLock = false;
    }

    @Override
    public void hearShake() {
        if (restaurant != null) {
            resetAndFindNewRestaurant();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        restaurantMap.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        restaurantMap.onPause();

        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.stop();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        restaurantMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        restaurantMap.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restaurantMap.onDestroy();
        restaurantFetcher.clearEverything();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        UIUtils.loadMenuIcon(menu, R.id.find_new_restaurant, IoniconsIcons.ion_android_refresh, this);
        UIUtils.loadMenuIcon(menu, R.id.filter, IoniconsIcons.ion_funnel, this);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.find_new_restaurant:
                resetAndFindNewRestaurant();
                return true;
            case R.id.filter:
                startActivityForResult(
                        new Intent(this, FilterActivity.class),
                        FILTER_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.stay);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return false;
    }
}
