package com.mustmobile.fragment.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.mustmobile.R;

public class SettingsFragment extends PreferenceFragment
                implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_is_first_run))){
            Preference accountLogOut = findPreference(key);
            Boolean logout = sharedPreferences.getBoolean(getString(R.string.pref_is_first_run), false);
            if (logout){
                accountLogOut.setSummary("Exit the app to clear all your data.");
            } else {
                accountLogOut.setSummary("All your account data will be cleared.");
            }
        }  else if (key.equals(getString(R.string.pref_use_campus_proxy))){
            Preference proxyPref = findPreference(key);
            Boolean useProxy = sharedPreferences.getBoolean(getString(R.string.pref_use_campus_proxy), false);
            if (useProxy){
                proxyPref.setSummary("Disable connection behind campus proxy.");
            } else {
                proxyPref.setSummary("Enable connection behind campus proxy.");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
