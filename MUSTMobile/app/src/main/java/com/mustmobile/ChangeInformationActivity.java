package com.mustmobile;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mustmobile.fragment.settings.InformationPreferenceFragment;
import com.mustmobile.fragment.settings.SettingsFragment;

public class ChangeInformationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new InformationPreferenceFragment(),
                        InformationPreferenceFragment.class.getSimpleName())
                .commit();
    }
}
