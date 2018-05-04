package com.edu.uni.augsburg.uniatron.setting;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.edu.uni.augsburg.uniatron.R;

/**
 * This fragment is for user specific configuration.
 *
 * @author Fabio Hellmann
 */
public class SettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
