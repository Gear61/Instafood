package com.randomappsinc.instafood.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
import com.randomappsinc.instafood.api.RestClient;
import com.randomappsinc.instafood.location.LocationManager;
import com.randomappsinc.instafood.models.Restaurant;
import com.randomappsinc.instafood.models.RestaurantReview;
import com.randomappsinc.instafood.persistence.PreferencesManager;
import com.randomappsinc.instafood.utils.UIUtils;
import com.randomappsinc.instafood.views.RestaurantInfoView;
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends StandardActivity implements RestClient.PhotosListener,
        RestClient.RestaurantListener, RestClient.ReviewsListener, RestaurantReviewsAdapter.Listener,
        RestaurantPhotosAdapter.Listener, OnMapReadyCallback, LocationManager.Listener,
        ShakeDetector.Listener {

    private static final int FILTER_REQUEST_CODE = 1;

    @BindView(R.id.parent) ScrollView parent;
    @BindView(R.id.restaurant_map) MapView restaurantMap;
    @BindView(R.id.restaurant_info_parent) View restaurantInfo;
    @BindView(R.id.photos_stub) View photosStub;
    @BindView(R.id.restaurant_photos) RecyclerView photos;
    @BindView(R.id.reviews_stub) View reviewsStub;
    @BindView(R.id.reviews_container) LinearLayout reviewsContainer;

    private Restaurant restaurant;
    private RestaurantInfoView restaurantInfoView;
    private RestClient restClient;
    private RestaurantPhotosAdapter photosAdapter;
    private RestaurantReviewsAdapter reviewsAdapter;
    private GoogleMap googleMap;
    @Nullable private String currentLocation;
    private LocationManager locationManager;
    private boolean denialLock;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        locationManager = new LocationManager(this, this);
        shakeDetector = new ShakeDetector(this);

        restaurantMap.onCreate(savedInstanceState);
        restaurantMap.getMapAsync(this);

        photosAdapter = new RestaurantPhotosAdapter(this, this);
        photos.setAdapter(photosAdapter);
        reviewsAdapter = new RestaurantReviewsAdapter(this);

        restClient = RestClient.getInstance();
        restClient.registerRestaurantListener(this);
        restClient.registerPhotosListener(this);
        restClient.registerReviewsListener(this);

        restaurantInfoView = new RestaurantInfoView(
                this,
                restaurantInfo,
                new IconDrawable(this, IoniconsIcons.ion_location).colorRes(R.color.dark_gray));
    }

    @Override
    public void onRestaurantFetched(Restaurant restaurant) {
        this.restaurant = restaurant;
        this.restaurantInfoView.loadRestaurant(restaurant);
        if (googleMap != null) {
            loadRestaurantLocationInMap();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.setOnMapClickListener(mapClickListener);
        this.googleMap = googleMap;
        if (restaurant != null) {
            loadRestaurantLocationInMap();
        }
    }

    private void loadRestaurantLocationInMap() {
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

    private final GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if (restaurant == null) {
                return;
            }

            String mapUri = "google.navigation:q=" + restaurant.getAddress() + " " + restaurant.getName();
            startActivity(Intent.createChooser(
                    new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapUri)),
                    getString(R.string.navigate_with)));
        }
    };

    @OnClick(R.id.restaurant_thumbnail)
    public void onThumbnailClicked() {
        if (TextUtils.isEmpty(restaurant.getImageUrl())) {
            return;
        }

        Intent intent = new Intent(this, PictureFullViewActivity.class);
        ArrayList<String> imageUrl = new ArrayList<>();
        imageUrl.add(restaurant.getImageUrl());
        intent.putStringArrayListExtra(PictureFullViewActivity.IMAGE_URLS_KEY, imageUrl);
        startActivity(intent);
        overridePendingTransition(0, 0);
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
    }

    @OnClick(R.id.share_button)
    public void shareRestaurant() {
        if (restaurant == null) {
            return;
        }

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(restaurant.getUrl())
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }

    @Override
    public void onPhotosFetched(List<String> photos) {
        this.photosStub.setVisibility(View.INVISIBLE);
        this.photosAdapter.setPhotoUrls(photos);
        this.photos.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReviewsFetched(List<RestaurantReview> reviews) {
        reviewsStub.setVisibility(View.GONE);
        reviewsContainer.setVisibility(View.VISIBLE);
        reviewsAdapter.setReviews(reviews, reviewsContainer, this);
    }

    private void resetAndFindNewRestaurant() {
        if (currentLocation == null) {
            locationManager.fetchCurrentLocation();
        } else {
            parent.fullScroll(ScrollView.FOCUS_UP);
            restaurant = null;
            restClient.findRestaurant(currentLocation);
            turnOnSkeletonLoading();
        }
    }

    private void turnOnSkeletonLoading() {
        restaurantInfoView.setSkeletonVisibility(true);
        photos.setVisibility(View.INVISIBLE);
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
            @NonNull String permissions[],
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
                    UIUtils.showLongToast(R.string.location_services_on);
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
    public void onLocationFetched(String location) {
        if (currentLocation != null && currentLocation.equals(location)) {
            return;
        }

        currentLocation = location;
        restClient.findRestaurant(currentLocation);
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
    public void onResume() {
        super.onResume();
        restaurantMap.onResume();

        // Re-render distance text since they might have changed their distance setting
        restaurantInfoView.renderDistanceText();

        // Run this here instead of onCreate() to cover the case where they return from turning on location
        if (currentLocation == null && !denialLock) {
            locationManager.fetchCurrentLocation();
        }

        if (PreferencesManager.get().isShakeEnabled()) {
            shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        restaurantMap.onSaveInstanceState(outState);
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

        if (PreferencesManager.get().isShakeEnabled()) {
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

        // Stop listening for restaurant results
        restClient.cancelRestaurantFetch();
        restClient.unregisterRestaurantListener();

        // Stop listening for photo fetch results
        restClient.cancelPhotosFetch();
        restClient.unregisterPhotosListener();

        // Stop listening for review fetch results
        restClient.cancelReviewsFetch();
        restClient.unregisterReviewsListener();
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
