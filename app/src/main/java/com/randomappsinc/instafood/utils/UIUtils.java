package com.randomappsinc.instafood.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;
import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.persistence.PreferencesManager;

public class UIUtils {

    private static final int NUM_APP_OPENS_BEFORE_ASKING_FOR_RATING = 5;

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

    public static void setCheckedImmediately(CompoundButton checkableView, boolean checked) {
        checkableView.setChecked(checked);
        checkableView.jumpDrawablesToCurrentState();
    }

    public static int getColor(@ColorRes int color) {
        return MyApplication.getAppContext().getResources().getColor(color);
    }

    public static void askForRatingIfAppropriate(final Activity activity) {
        if (PreferencesManager.get().getNumAppOpens() == NUM_APP_OPENS_BEFORE_ASKING_FOR_RATING) {
            new MaterialDialog.Builder(activity)
                    .content(R.string.please_rate)
                    .negativeText(R.string.no_im_good)
                    .positiveText(R.string.will_rate)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            if (!(activity.getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                                UIUtils.showToast(R.string.play_store_error, Toast.LENGTH_LONG);
                                return;
                            }
                            activity.startActivity(intent);
                        }
                    })
                    .show();
        }
    }
}
