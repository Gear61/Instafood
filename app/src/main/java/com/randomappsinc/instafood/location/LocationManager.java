package com.randomappsinc.instafood.location;

import android.Manifest;
import android.app.Activity;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.utils.PermissionUtils;
import com.randomappsinc.instafood.views.UIUtils;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class LocationManager implements LocationForm.Listener {

    // NOTE: If an activity uses this class, IT CANNOT USE MATCHING CODES
    public static final int LOCATION_SERVICES_CODE = 350;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 9001;

    public interface Listener {
        void onLocationFetched(String location);

        void onServicesOrPermissionChoice();
    }

    @NonNull private Listener listener;
    @NonNull private Activity activity;

    private boolean locationFetched;
    private Handler locationChecker;
    private Runnable locationCheckTask;
    private LocationServicesManager locationServicesManager;
    private MaterialDialog locationDenialDialog;
    private MaterialDialog locationPermissionDialog;
    private LocationForm locationForm;

    public LocationManager(@NonNull Listener listener, @NonNull Activity activity) {
        this.listener = listener;
        this.activity = activity;
        initNonContext();
    }

    private void initNonContext() {
        locationServicesManager = new LocationServicesManager(activity);
        locationChecker = new Handler();
        locationCheckTask = new Runnable() {
            @Override
            public void run() {
                SmartLocation.with(activity).location().stop();
                if (!locationFetched) {
                    UIUtils.showLongToast(R.string.auto_location_fail);
                }
            }
        };

        locationForm = new LocationForm(activity, this);
        locationDenialDialog = new MaterialDialog.Builder(activity)
                .cancelable(false)
                .title(R.string.location_services_needed)
                .content(R.string.location_services_denial)
                .positiveText(R.string.location_services_confirm)
                .negativeText(R.string.enter_location_manually)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        locationServicesManager.askForLocationServices(LOCATION_SERVICES_CODE);
                        listener.onServicesOrPermissionChoice();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        locationForm.show();
                        listener.onServicesOrPermissionChoice();
                    }
                })
                .build();

        locationPermissionDialog = new MaterialDialog.Builder(activity)
                .cancelable(false)
                .title(R.string.location_permission_needed)
                .content(R.string.location_permission_denial)
                .positiveText(R.string.give_location_permission)
                .negativeText(R.string.enter_location_manually)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        requestLocationPermission();
                        listener.onServicesOrPermissionChoice();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        locationForm.show();
                        listener.onServicesOrPermissionChoice();
                    }
                })
                .build();
    }

    @Override
    public void onLocationEntered(String location) {
        stopFetchingCurrentLocation();
        listener.onLocationFetched(location);
    }

    public void fetchCurrentLocation() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (SmartLocation.with(activity).location().state().locationServicesEnabled()) {
                fetchAutomaticLocation();
            } else {
                locationServicesManager.askForLocationServices(LOCATION_SERVICES_CODE);
            }
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        PermissionUtils.requestPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void fetchAutomaticLocation() {
        locationFetched = false;
        SmartLocation.with(activity).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        locationChecker.removeCallbacks(locationCheckTask);
                        locationFetched = true;
                        String currentLocation = String.valueOf(location.getLatitude())
                                + ", "
                                + String.valueOf(location.getLongitude());
                        listener.onLocationFetched(currentLocation);
                    }
                });
        locationChecker.postDelayed(locationCheckTask, 10000L);
    }

    public void stopFetchingCurrentLocation() {
        locationChecker.removeCallbacks(locationCheckTask);
        SmartLocation.with(activity).location().stop();
    }

    public void showLocationForm() {
        locationForm.show();
    }

    public void showLocationDenialDialog() {
        locationDenialDialog.show();
    }

    public void showLocationPermissionDialog() {
        locationPermissionDialog.show();
    }
}