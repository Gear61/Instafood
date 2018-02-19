package com.randomappsinc.instafood.views;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.Toast;

import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.utils.MyApplication;

public class UIUtils {

    public static void loadMenuIcon(Menu menu, int itemId, Icon icon, Context context) {
        menu.findItem(itemId).setIcon(
                new IconDrawable(context, icon)
                        .colorRes(R.color.white)
                        .actionBarSize());
    }

    public static int getRatingDrawableId(double rating) {
        if (rating < 1.0) {
            return R.drawable.stars_0;
        }
        if (rating < 1.25) {
            return R.drawable.stars_1;
        }
        if (rating < 1.75) {
            return R.drawable.stars_1_and_a_half;
        }
        if (rating < 2.25) {
            return R.drawable.stars_2;
        }
        if (rating < 2.75) {
            return R.drawable.stars_2_and_a_half;
        }
        if (rating < 3.25) {
            return R.drawable.stars_3;
        }
        if (rating < 3.75) {
            return R.drawable.stars_3_and_a_half;
        }
        if (rating < 4.25) {
            return R.drawable.stars_4;
        }
        if (rating < 4.75) {
            return R.drawable.stars_4_and_a_half;
        }
        return R.drawable.stars_5;
    }

    public static void showLongToast(@StringRes int stringId) {
        showToast(stringId, Toast.LENGTH_LONG);
    }

    private static void showToast(@StringRes int stringId, int toastLength) {
        Toast.makeText(MyApplication.getAppContext(), stringId, toastLength).show();
    }

    public static void setCheckedImmediately(CheckBox checkbox, boolean checked) {
        checkbox.setChecked(checked);
        checkbox.jumpDrawablesToCurrentState();
    }
}
