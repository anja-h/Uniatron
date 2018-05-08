package com.edu.uni.augsburg.uniatron.ui.setting;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.edu.uni.augsburg.uniatron.R;

/**
 * This fragment is for user specific configuration.
 *
 * @author Fabio Hellmann
 */
public class SettingFragment extends PreferenceFragmentCompat {

    private static final String PREF_APP_SELECTION = "pref_app_selection";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        final SettingViewModel model = ViewModelProviders.of(this).get(SettingViewModel.class);

        final MultiSelectListPreference list =
                (MultiSelectListPreference) findPreference(PREF_APP_SELECTION);
        list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // TODO Update database
                return false;
            }
        });

        model.getInstalledApps(getContext()).observe(this, data -> {
            final String[] entries = data.toArray(new String[data.size()]);
            list.setEntries(entries);
            list.setEntryValues(entries);
        });
    }
}
