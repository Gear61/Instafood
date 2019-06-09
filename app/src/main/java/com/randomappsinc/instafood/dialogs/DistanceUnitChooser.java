package com.randomappsinc.instafood.dialogs;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.constants.DistanceUnit;
import com.randomappsinc.instafood.persistence.PreferencesManager;
import com.randomappsinc.instafood.utils.UIUtils;

public class DistanceUnitChooser {

    private MaterialDialog dialog;

    public DistanceUnitChooser(Context context) {
        @DistanceUnit String currentUnit = PreferencesManager.get().getDistanceUnit();
        int currentPosition = currentUnit.equals(DistanceUnit.MILES) ? 0 : 1;

        dialog = new MaterialDialog.Builder(context)
                .title(R.string.set_distance_unit_title)
                .content(R.string.distance_unit_prompt)
                .items(R.array.distance_unit_options)
                .itemsCallbackSingleChoice(currentPosition, (dialog, itemView, which, text) -> {
                    @DistanceUnit String chosenUnit = which == 0
                            ? DistanceUnit.MILES
                            : DistanceUnit.KILOMETERS;
                    PreferencesManager.get().setDistanceUnit(chosenUnit);
                    UIUtils.showLongToast(R.string.distance_unit_set);
                    return true;
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .build();
    }

    public void show() {
        dialog.show();
    }
}
