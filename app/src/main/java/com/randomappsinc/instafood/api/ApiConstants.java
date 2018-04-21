package com.randomappsinc.instafood.api;

public class ApiConstants {

    static final String BASE_URL = "https://api.yelp.com";

    static final String AUTHORIZATION = "Authorization";
    static final String BEARER_PREFIX = "Bearer ";
    static final String DEFAULT_SEARCH_TERM = "food";

    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_FORBIDDEN = 403;

    // Yelp's API takes a VERY aggressive "more content is better than less" approach,
    // blatantly ignoring search parameters in order to do so.
    // Because of this, we want to limit the amount of results fetched from the API when the user
    // has specified a search term to prevent clearly unrelated results.
    static final int NUM_RESTAURANT_RESULTS_WITH_SEARCH_TERM = 20;

    static final int NUM_RESTAURANT_RESULTS_NO_SEARCH_TERM = 50;

    // Yelp error codes
    public static final String BUSINESS_UNAVAILABLE = "BUSINESS_UNAVAILABLE";
}
