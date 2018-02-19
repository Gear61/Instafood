package com.randomappsinc.instafood.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.adapters.SettingsAdapter;
import com.randomappsinc.instafood.dialogs.DistanceUnitChooser;
import com.randomappsinc.instafood.persistence.PreferencesManager;
import com.randomappsinc.instafood.utils.UIUtils;
import com.randomappsinc.instafood.views.SimpleDividerItemDecoration;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends StandardActivity implements SettingsAdapter.ItemSelectionListener {

    public static final String SUPPORT_EMAIL = "jchiouapps@gmail.com";
    public static final String OTHER_APPS_URL = "https://play.google.com/store/apps/developer?id=Jchiou+Apps+Inc.";
    public static final String REPO_URL = "https://github.com/Gear61/Instafood";

    @BindView(R.id.settings_options) RecyclerView settingsOptions;
    @BindString(R.string.feedback_subject) String feedbackSubject;
    @BindString(R.string.send_email) String sendEmail;

    private DistanceUnitChooser distanceUnitChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        settingsOptions.addItemDecoration(new SimpleDividerItemDecoration(this));
        settingsOptions.setAdapter(new SettingsAdapter(this, this));
        distanceUnitChooser = new DistanceUnitChooser(this);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                View firstCell = settingsOptions.getChildAt(0);
                Switch shakeToggle = firstCell.findViewById(R.id.shake_toggle);
                boolean currentState = shakeToggle.isChecked();
                shakeToggle.setChecked(!currentState);
                PreferencesManager.get().setShakeEnabled(!currentState);
                return;
            case 1:
                distanceUnitChooser.show();
                return;
            case 2:
                String uriText = "mailto:" + SUPPORT_EMAIL + "?subject=" + Uri.encode(feedbackSubject);
                Uri mailUri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, mailUri);
                startActivity(Intent.createChooser(sendIntent, sendEmail));
                return;
            case 3:
                Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setText(getString(R.string.share_app_message))
                        .getIntent();
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(shareIntent);
                }
                return;
            case 4:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OTHER_APPS_URL));
                break;
            case 5:
                Uri uri =  Uri.parse("market://details?id=" + getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                    UIUtils.showLongToast(R.string.play_store_error);
                    return;
                }
                break;
            case 6:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL));
                break;
        }
        startActivity(intent);
    }
}
