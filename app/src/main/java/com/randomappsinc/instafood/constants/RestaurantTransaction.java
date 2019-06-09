package com.randomappsinc.instafood.constants;

import androidx.annotation.StringDef;

@StringDef({
        RestaurantTransaction.PICKUP,
        RestaurantTransaction.DELIVERY,
        RestaurantTransaction.RESERVATION,
})
public @interface RestaurantTransaction {
    String PICKUP = "pickup";
    String DELIVERY = "delivery";
    String RESERVATION = "restaurant_reservation";
}
