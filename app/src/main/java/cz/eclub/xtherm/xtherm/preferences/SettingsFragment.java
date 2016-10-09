package cz.eclub.xtherm.xtherm.preferences;


import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cz.eclub.xtherm.xtherm.R;

/**
 * Created by vesely on 9/15/16.
 */
@PreferenceScreen(R.xml.settings)
@EFragment
public class SettingsFragment extends PreferenceFragment {

    @Pref
    Prefs_ preferences;

    @PreferenceByKey(R.string.interval_settings)
    EditTextPreference interval;

    @AfterPreferences
    void initDefault(){
        interval.setText(String.valueOf(preferences.interval().get()));
    }

    @PreferenceChange(R.string.interval_settings)
    void preferenceChangeIntervalSettings(Preference preference, String newValue){
        preferences.edit().interval().put(Integer.valueOf(newValue)).apply();
    }
}

