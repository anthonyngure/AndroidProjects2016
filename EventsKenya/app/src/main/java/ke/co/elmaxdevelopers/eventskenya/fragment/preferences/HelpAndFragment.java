package ke.co.elmaxdevelopers.eventskenya.fragment.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;

public class HelpAndFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Preference emailPreference, whatsAppPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.more);

        whatsAppPreference = findPreference(getString(R.string.pref_whatsapp));
        emailPreference = findPreference(getString(R.string.pref_email));

        whatsAppPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri uri = Uri.parse("smsto:+254723203475");
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send");
                intent.setPackage("com.whatsapp");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(Intent.createChooser(intent,""));
                } else {
                    Helper.toast(getActivity(), "You must have WhatsApp installed!");
                }
                return true;
            }
        });

        emailPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"+getString(R.string.company_email)));
                if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    getActivity().startActivity(emailIntent);
                } else {
                    Helper.toast(getActivity(), "Unable to find an email App.");
                }
                return true;
            }
        });
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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
