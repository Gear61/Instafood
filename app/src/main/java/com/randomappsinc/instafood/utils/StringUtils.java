package com.randomappsinc.instafood.utils;

import androidx.annotation.StringRes;

import java.util.Currency;
import java.util.Locale;

public class StringUtils {

    public static String getCurrencySymbol() {
        Locale userLocale = Locale.getDefault();
        Currency currency = Currency.getInstance(userLocale);
        return currency.getSymbol();
    }

    public static String getString(@StringRes int resId) {
        return MyApplication.getAppContext().getString(resId);
    }
}
