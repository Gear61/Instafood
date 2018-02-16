package com.randomappsinc.automaticfoodfinder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.automaticfoodfinder.R;
import com.randomappsinc.automaticfoodfinder.adapters.RestaurantPhotosAdapter;
import com.randomappsinc.automaticfoodfinder.adapters.RestaurantReviewsAdapter;
import com.randomappsinc.automaticfoodfinder.api.RestClient;
import com.randomappsinc.automaticfoodfinder.models.Restaurant;
import com.randomappsinc.automaticfoodfinder.models.RestaurantReview;
import com.randomappsinc.automaticfoodfinder.views.RestaurantInfoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends StandardActivity implements RestClient.PhotosListener, RestClient.RestaurantListener,
        RestClient.ReviewsListener, RestaurantReviewsAdapter.Listener, RestaurantPhotosAdapter.Listener, OnMapReadyCallback {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        restaurantMap.onCreate(savedInstanceState);
        photosAdapter = new RestaurantPhotosAdapter(this, this);
        photos.setAdapter(photosAdapter);
        reviewsAdapter = new RestaurantReviewsAdapter(this);

        restClient = RestClient.getInstance();
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
        restaurantInfoView.loadRestaurant(restaurant);
        restaurantMap.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.setOnMapClickListener(mMapClickListener);

        LatLng coordinates = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(coordinates)
                .title(this.restaurant.getName()));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordinates)
                .zoom(16)
                .bearing(0)
                .tilt(0)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private final GoogleMap.OnMapClickListener mMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
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
    public void callrestaurant() {
        String phoneUri = "tel:" + restaurant.getPhoneNumber();
        startActivity(Intent.createChooser(
                new Intent(Intent.ACTION_DIAL, Uri.parse(phoneUri)),
                getString(R.string.call_with)));
    }

    @OnClick(R.id.share_button)
    public void sharerestaurant() {
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
        photosStub.setVisibility(View.INVISIBLE);
        photosAdapter.setPhotoUrls(photos);
        this.photos.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReviewsFetched(List<RestaurantReview> reviews) {
        reviewsStub.setVisibility(View.GONE);
        reviewsAdapter.setReviews(reviews, reviewsContainer, this);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        restaurantMap.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        restaurantMap.onResume();
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

        // Stop listening for photo fetch results
        restClient.cancelPhotosFetch();
        restClient.unregisterPhotosListener();

        // Stop listening for review fetch results
        restClient.cancelReviewsFetch();
        restClient.unregisterReviewsListener();
    }
}
