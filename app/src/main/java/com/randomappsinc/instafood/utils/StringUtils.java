package com.randomappsinc.instafood.utils;

import java.util.Currency;
import java.util.Locale;

public class StringUtils {

    public static String getCurrencySymbol() {
        Locale userLocale = Locale.getDefault();
        Currency currency = Currency.getInstance(userLocale);
        return currency.getSymbol();
    }
}
