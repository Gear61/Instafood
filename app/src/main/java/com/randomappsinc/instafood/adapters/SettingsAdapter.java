package com.randomappsinc.instafood.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.randomappsinc.instafood.R;
import com.randomappsinc.instafood.persistence.PreferencesManager;
import com.randomappsinc.instafood.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {

    public interface ItemSelectionListener {
        void onItemClick(int position);
    }

    @NonNull protected ItemSelectionListener itemSelectionListener;
    protected Context context;
    protected String[] options;
    protected String[] icons;

    public SettingsAdapter(Context context, @NonNull ItemSelectionListener itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
        this.context = context;
        this.options = context.getResources().getStringArray(R.array.settings_options);
        this.icons = context.getResources().getStringArray(R.array.settings_icons);
    }

    @Override
    public SettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.settings_item_cell,
                parent,
                false);
        return new SettingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SettingViewHolder holder, int position) {
        holder.loadSetting(position);
    }

    @Override
    public int getItemCount() {
        return options.length;
    }

    class SettingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.settings_icon) TextView icon;
        @BindView(R.id.settings_option) TextView option;
        @BindView(R.id.shake_toggle) Switch shakeToggle;

        SettingViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void loadSetting(int position) {
            option.setText(options[position]);
            icon.setText(icons[position]);

            if (position == 0) {
                UIUtils.setCheckedImmediately(shakeToggle, PreferencesManager.get().isShakeEnabled());
                shakeToggle.setVisibility(View.VISIBLE);
            } else {
                shakeToggle.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.shake_toggle)
        public void onSoundToggle() {
            PreferencesManager.get().setShakeEnabled(shakeToggle.isChecked());
        }

        @OnClick(R.id.parent)
        public void onSettingSelected() {
            itemSelectionListener.onItemClick(getAdapterPosition());
        }
    }
}
