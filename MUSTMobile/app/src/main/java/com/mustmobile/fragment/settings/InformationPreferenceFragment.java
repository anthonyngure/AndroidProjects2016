package com.mustmobile.fragment.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.mustmobile.R;

public class InformationPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.information_preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        findPreference(getString(R.string.pref_user_program))
                .setSummary(sharedPreferences.getString(getString(R.string.pref_user_program),
                        getString(R.string.pref_default_user_program)));

        findPreference(getString(R.string.pref_user_program_code))
                .setSummary(sharedPreferences.getString(getString(R.string.pref_user_program_code),
                        getString(R.string.pref_default_program_code)));

        findPreference(getString(R.string.pref_user_stage))
                .setSummary(sharedPreferences.getString(getString(R.string.pref_user_stage),
                        getString(R.string.pref_user_default_stage)));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_user_program))){
            Preference userProgram = findPreference(key);
            userProgram.setSummary(sharedPreferences.getString(key, "No program defined."));
        } else if (key.equalsIgnoreCase(getString(R.string.pref_user_program_code))){
            Preference programCode = findPreference(key);
            programCode.setSummary(sharedPreferences.getString(key, "No program initials defined"));
        } else if (key.equalsIgnoreCase(getString(R.string.pref_user_stage))){
            Preference userStage = findPreference(key);
            userStage.setSummary(sharedPreferences.getString(key, "No stage defined."));
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
