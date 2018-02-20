package com.randomappsinc.instafood.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.randomappsinc.instafood.utils.TimeUtils;

public class RestaurantReview implements Parcelable {

    private double rating;
    private String text;
    private long timeCreated;
    private String url;
    private String username;
    private String userImageUrl;

    public RestaurantReview() {}

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getText() {
        return "\"" + text + "\"";
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getTimeCreatedText() {
        return TimeUtils.getReviewDateTime(timeCreated);
    }

    protected RestaurantReview(Parcel in) {
        rating = in.readDouble();
        text = in.readString();
        timeCreated = in.readLong();
        url = in.readString();
        username = in.readString();
        userImageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(rating);
        dest.writeString(text);
        dest.writeLong(timeCreated);
        dest.writeString(url);
        dest.writeString(username);
        dest.writeString(userImageUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RestaurantReview> CREATOR = new Parcelable.Creator<RestaurantReview>() {
        @Override
        public RestaurantReview createFromParcel(Parcel in) {
            return new RestaurantReview(in);
        }

        @Override
        public RestaurantReview[] newArray(int size) {
            return new RestaurantReview[size];
        }
    };
}
