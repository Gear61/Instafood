package com.randomappsinc.instafood.api.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Location {

    @SerializedName("address1")
    @Expose
    private String address1;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("display_address")
    @Expose
    private List<String> displayAddress;

    String getCity() {
        return city;
    }

    String getAddress() {
        StringBuilder address = new StringBuilder();
        if (!TextUtils.isEmpty(address1)) {
            address.append(address1).append(", ");
        }
        address.append(city);
        return address.toString();
    }

    String getFullAddress() {
        StringBuilder address = new StringBuilder();
        for (String line : displayAddress) {
            if (address.length() > 0) {
                address.append(", ");
            }
            address.append(line);
        }
        return address.toString();
    }
}