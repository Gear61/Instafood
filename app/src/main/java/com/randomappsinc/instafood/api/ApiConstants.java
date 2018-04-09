package com.randomappsinc.instafood.api;

public class ApiConstants {

    static final String BASE_URL = "https://api.yelp.com";

    static final String AUTHORIZATION = "Authorization";
    static final String BEARER_PREFIX = "Bearer ";
    static final String DEFAULT_SEARCH_TERM = "food";

    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_FORBIDDEN = 403;

    static final int DEFAULT_NUM_RESTAURANT_RESULTS = 20;

    // Yelp error codes
    public static final String BUSINESS_UNAVAILABLE = "BUSINESS_UNAVAILABLE";
}
