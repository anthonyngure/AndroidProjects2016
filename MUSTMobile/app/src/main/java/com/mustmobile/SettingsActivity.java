package com.mustmobile;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.mustmobile.fragment.settings.SettingsFragment;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new SettingsFragment(),
                        SettingsFragment.class.getSimpleName())
                .commit();
    }

    public void settingsAccount(View view){

    }

    public void settingsNotifications(View view){

    }
}
