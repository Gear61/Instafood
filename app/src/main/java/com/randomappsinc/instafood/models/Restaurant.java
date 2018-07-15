package com.randomappsinc.instafood.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.constants.DistanceUnit;
import com.randomappsinc.instafood.persistence.PreferencesManager;
import com.randomappsinc.instafood.utils.DistanceUtils;
import com.randomappsinc.instafood.utils.StringUtils;

import java.util.ArrayList;

public class Restaurant implements Parcelable {

    private String yelpId;
    private String name;
    private String imageUrl;
    private String yelpUrl;
    private double rating;
    private int reviewCount;
    private String phoneNumber;
    private String price;
    private String address;
    private String fullAddress;
    private double latitude;
    private double longitude;
    private boolean supportsPickup;
    private boolean supportsDelivery;
    private boolean supportsReservations;
    private ArrayList<String> categories;
    private ArrayList<String> photoUrls;
    private ArrayList<RestaurantReview> reviews;

    public Restaurant() {}

    // Distance between the place location and the user's current location in meters
    private double distance;

    public String getId() {
        return yelpId;
    }

    public void setId(String id) {
        yelpId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUrl(String url) {
        yelpUrl = url;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public boolean supportsPickup() {
        return supportsPickup;
    }

    public void setSupportsPickup(boolean supportsPickup) {
        this.supportsPickup = supportsPickup;
    }

    public boolean supportsDelivery() {
        return supportsDelivery;
    }

    public void setSupportsDelivery(boolean supportsDelivery) {
        this.supportsDelivery = supportsDelivery;
    }

    public boolean supportsReservations() {
        return supportsReservations;
    }

    public void setSupportsReservations(boolean supportsReservations) {
        this.supportsReservations = supportsReservations;
    }

    public ArrayList<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(ArrayList<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public ArrayList<RestaurantReview> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<RestaurantReview> reviews) {
        this.reviews = reviews;
    }

    public double getDistanceToShow() {
        return PreferencesManager.get().getDistanceUnit().equals(DistanceUnit.MILES)
                ? DistanceUtils.getMilesFromMeters(distance)
                : DistanceUtils.getKilometersFromMeters(distance);
    }

    public String getCategoriesListText() {
        StringBuilder categoriesList = new StringBuilder();
        for (String category : categories) {
            if (categoriesList.length() > 0) {
                categoriesList.append(", ");
            }
            categoriesList.append(category);
        }
        return categoriesList.toString();
    }

    public String getShareText() {
        String template = StringUtils.getString(R.string.share_template);
        return String.format(
                template,
                name,
                getRatingShareText(),
                fullAddress,
                getCategoriesListText(),
                phoneNumber,
                yelpUrl);
    }

    private String getRatingShareText() {
        String reviewText = reviewCount == 1
                ? StringUtils.getString(R.string.one_review)
                : String.format(StringUtils.getString(R.string.num_reviews), reviewCount);
        return String.valueOf(rating) + "/5 (" + reviewText + ")";
    }

    protected Restaurant(Parcel in) {
        yelpId = in.readString();
        name = in.readString();
        imageUrl = in.readString();
        yelpUrl = in.readString();
        rating = in.readDouble();
        reviewCount = in.readInt();
        phoneNumber = in.readString();
        price = in.readString();
        address = in.readString();
        fullAddress = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        supportsPickup = in.readByte() != 0x00;
        supportsDelivery = in.readByte() != 0x00;
        supportsReservations = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            categories = new ArrayList<>();
            in.readList(categories, String.class.getClassLoader());
        } else {
            categories = null;
        }
        if (in.readByte() == 0x01) {
            photoUrls = new ArrayList<>();
            in.readList(photoUrls, String.class.getClassLoader());
        } else {
            photoUrls = null;
        }
        if (in.readByte() == 0x01) {
            reviews = new ArrayList<>();
            in.readList(reviews, RestaurantReview.class.getClassLoader());
        } else {
            reviews = null;
        }
        distance = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(yelpId);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(yelpUrl);
        dest.writeDouble(rating);
        dest.writeInt(reviewCount);
        dest.writeString(phoneNumber);
        dest.writeString(price);
        dest.writeString(address);
        dest.writeString(fullAddress);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeByte((byte) (supportsPickup ? 0x01 : 0x00));
        dest.writeByte((byte) (supportsDelivery ? 0x01 : 0x00));
        dest.writeByte((byte) (supportsReservations ? 0x01 : 0x00));
        if (categories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(categories);
        }
        if (photoUrls == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(photoUrls);
        }
        if (reviews == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(reviews);
        }
        dest.writeDouble(distance);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
