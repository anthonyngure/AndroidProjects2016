package ke.co.elmaxdevelopers.eventskenya.fragment.preferences;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.database.DataController;
import ke.co.elmaxdevelopers.eventskenya.fragment.EventsListFragment;
import ke.co.elmaxdevelopers.eventskenya.model.Event;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.network.LoadingListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;

public class SettingsFragment extends PreferenceFragment
        implements LoadingListener {

    private Preference userNamePreference, locationPreference,
            preferredImagesPreference, categoryPreference;
    private SharedPreferences sharedPreferences;
    private Preference emailPreference;
    private EditText etUsername;
    private TextInputLayout inputLayout;
    private Button buttonContinue;
    private String username;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        userNamePreference = findPreference(getString(R.string.pref_user));
        locationPreference = findPreference(getString(R.string.pref_county));
        emailPreference = findPreference(getString(R.string.pref_email));
        categoryPreference = findPreference(getString(R.string.pref_category));
        preferredImagesPreference = findPreference(getString(R.string.pref_autodownload_posters));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String userName = sharedPreferences.getString(getString(R.string.pref_user),
                getString(R.string.default_user));
        String userLocation = sharedPreferences.getString(getString(R.string.pref_county)
                , getString(R.string.default_county));

        String setCategory = sharedPreferences.getString(getString(R.string.pref_category)
                , getString(R.string.default_category));

        categoryPreference.setSummary(setCategory);
        categoryPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                selectCategoryDialog();
                return true;
            }
        });

        userNamePreference.setSummary(userName);
        locationPreference.setSummary(userLocation);
        locationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                selectCountyDialog();
                return true;
            }
        });

        userNamePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getUserDetails();
                return true;
            }
        });

        emailPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + getString(R.string.company_email)));
                if (emailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    getActivity().startActivity(emailIntent);
                } else {
                    Helper.toast(getActivity(), "Unable to find an email App.");
                }
                return true;
            }
        });
    }

    private void selectCountyDialog(){
        final CharSequence[] counties = getResources().getStringArray(R.array.counties);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.title_choose_county))
                .setItems(counties, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationPreference.setSummary(String.valueOf(counties[which]));
                        PrefUtils.getInstance(getActivity()).writeSetCounty(String.valueOf(counties[which]));
                        DataController.getInstance(getActivity()).forceSweep();
                        PrefUtils.getInstance(getActivity()).invalidate();
                        DataController.getInstance(getActivity()).invalidate();
                        Intent intent = new Intent(EventsListFragment.ACTION_CATEGORY_CHANGE);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    }
                })
                .create()
                .show();

    }

    private void getUserDetails(){

        View mView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.username_change_dialog, null, false);

        inputLayout = (TextInputLayout) mView.findViewById(R.id.activity_login_inputlayout);
        etUsername = (EditText) mView.findViewById(R.id.activity_login_username_input);
        buttonContinue = (Button) mView.findViewById(R.id.activity_login_button_continue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUsername();
            }
        });

        etUsername.addTextChangedListener(Helper.createTextWatcher(20, inputLayout, etUsername, getString(R.string.hint_username)));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView)
                .setTitle("Change Username");
        dialog = builder.create();
        dialog.show();
    }


    public void requestUsername (){
        username = etUsername.getText().toString().trim();
        if (username.length() >= 4) {
            inputLayout.setErrorEnabled(false);
            RequestParams params = new RequestParams();
            params.put(BackEnd.PARAM_NEW_USERNAME, username);
            APIConnector.getInstance(getActivity()).updateUsername(params, this);
            dialog.hide();

        } else {
            inputLayout.setError("Username must have more than 3 characters!");
        }
    }

    private void mParseResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equals(Response.SUCCESS)){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.pref_user), username);
                editor.apply();
                Helper.toast(getActivity(), getString(R.string.username_created_successfully));
                userNamePreference.setSummary(username);
                PrefUtils.getInstance(getActivity()).invalidate();
                hideProgressDialog();
            } else if (response.getString(Response.SUCCESS).equals(Response.FAILURE)){
                dialog.show();
                inputLayout.setError(response.getString(Response.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void hideProgressDialog(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onNetworkDataLoadingStarted() {
        hideProgressDialog();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating your profile, Please wait...");
        progressDialog.show();
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode) {
        hideProgressDialog();
        dialog.show();
        inputLayout.setError("Connection Error! Please check your network status..");
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response) {
        hideProgressDialog();
        mParseResponse(response);
    }

    private void selectCategoryDialog() {
        final String[] categories = getResources().getStringArray(R.array.categories);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.title_choose_category))
                .setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrefUtils.getInstance(getActivity()).writeSetCategory(categories[which]);
                        categoryPreference.setSummary(categories[which]);
                        DataController.getInstance(getActivity()).forceSweep();
                        PrefUtils.getInstance(getActivity()).invalidate();
                        DataController.getInstance(getActivity()).invalidate();
                        Intent intent = new Intent(EventsListFragment.ACTION_CATEGORY_CHANGE);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    }
                })
                .create()
                .show();
    }
}
