package cz.eclub.xtherm.xtherm.preferences;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.annotations.ViewById;

import cz.eclub.xtherm.xtherm.R;

/**
 * Created by vesely on 9/15/16.
 */
@EActivity(R.layout.preference_activity)
public class SettingsActivity extends AppCompatActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.preferences, new SettingsFragment_.FragmentBuilder_().build()).commit();
    }

    @AfterViews
    protected void setActionBar() {
        toolbar.setTitle(R.string.preferences);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    @PreferenceByKey(R.string.interval_settings)
//    EditTextPreference interval;
//
//    @AfterPreferences
//    void initPrefs() {
//        interval.setText("5000");
//    }
}
