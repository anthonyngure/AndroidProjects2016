package com.mustmobile.fragment.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mustmobile.MainActivity;
import com.mustmobile.R;
import com.mustmobile.fragment.BaseFragment;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.service.UpdatesCheckService;
import com.mustmobile.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class StarterFragment extends BaseFragment {

    private Button  retryButton, reportButton;
    private Button guestAccessButton, resetPasswordButton;
    private TextView tvMessage, loginButton;
    private TextView tvNumber, tvNewPassword, tvConfirmPassword;
    private String number, newPassword, confirmPassword;
    private AlertDialog dialog;
    private ViewGroup noConnection, responseMessage, authenticatingIndicator, loginForm;
    private final String TAG = StarterFragment.class.getSimpleName();

    public static StarterFragment newInstance() {
        StarterFragment fragment = new StarterFragment();
        return fragment;
    }

    public StarterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starter, container, false);
        loginButton = (TextView) view.findViewById(R.id.fragment_starter_button_login);
        guestAccessButton = (Button) view.findViewById(R.id.fragment_starter_button_guest_access);
        responseMessage = (ViewGroup) view.findViewById(R.id.fragment_starter_response_message);
        tvMessage = (TextView) view.findViewById(R.id.fragment_starter_message);
        tvNumber = (TextView) view.findViewById(R.id.fragment_starter_number);
        tvNewPassword = (TextView) view.findViewById(R.id.fragment_starter_new_password);
        tvConfirmPassword = (TextView) view.findViewById(R.id.fragment_starter_confirm_password);
        noConnection = (ViewGroup) view.findViewById(R.id.fragment_starter_no_connection);
        authenticatingIndicator = (ViewGroup) view.findViewById(R.id.fragment_starter_authenticating_indicator);
        loginForm = (ViewGroup) view.findViewById(R.id.fragment_starter_login_form);
        retryButton = (Button) view.findViewById(R.id.fragment_starter_retry_button);
        reportButton = (Button) view.findViewById(R.id.fragment_starter_report_button);
        resetPasswordButton = (Button) view.findViewById(R.id.fragment_starter_button_reset_password);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFields();
                if (allFieldsAreSet()) {
                    if (TextUtils.equals(newPassword, confirmPassword)){
                        showWarningDialog();
                    } else {
                        Helper.at(getActivity()).simpleToast("Passwords don\'t match.");
                    }
                } else {
                    Helper.at(getActivity()).simpleToast(getString(R.string.empty_fields_toast));
                }
            }
        });

        guestAccessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndRespond();
            }
        });

    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("What is this...")
                .setMessage("This app is released for initial testing, therefore it\'s an " +
                        "incomplete version of UNOFFICIAL Meru University Android App.\n\n" +
                        "Some data might not be accurate.\n\n" +
                        "However, we have tried to maintain consistency and accuracy of data.")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        authenticatingIndicator.setVisibility(View.VISIBLE);
                        connectAndRespond();
                    }
                })
                .create()
                .show();
    }

    private boolean allFieldsAreSet(){
        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(newPassword)|| TextUtils.isEmpty(confirmPassword)){
            return false;
        } else {
            return true;
        }
    }

    private void readFields(){
        number = tvNumber.getText().toString();
        newPassword = tvNewPassword.getText().toString();
        confirmPassword = tvConfirmPassword.getText().toString();

    }

    @Override
    protected void connectAndRespond(){
        RequestParams params = new RequestParams();
        params.add(Response.UserData.USER_NUMBER, number);
        params.add(Response.UserData.PASSWORD, newPassword);
        Client.post(Client.absoluteUrl("login"), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!Helper.at(getActivity()).isOnline()){
                    noConnection.setVisibility(View.VISIBLE);
                    authenticatingIndicator.setVisibility(View.GONE);
                    loginForm.setVisibility(View.GONE);
                    Helper.at(getActivity()).simpleToast("No Internet Connection!!");
                } else {
                    authenticatingIndicator.setVisibility(View.VISIBLE);
                    loginForm.setVisibility(View.GONE);
                    noConnection.setVisibility(View.GONE);
                    responseMessage.setVisibility(View.GONE);
                    resetPasswordButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseConnectionResponse(response);
                noConnection.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                noConnection.setVisibility(View.VISIBLE);
                authenticatingIndicator.setVisibility(View.GONE);
                loginForm.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void parseConnectionResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equals("1")){

                JSONArray data = response.getJSONArray(Response.UserData.USER_DATA);
                String name = null, registrationNumber = null, mClass = null, privilegeCode = null;

                JSONObject userData = data.getJSONObject(0);
                name = userData.getString(Response.UserData.NAME);
                mClass = userData.getString(Response.UserData.CLASS);
                registrationNumber = userData.getString(Response.UserData.REGISTRATION_NUMBER);
                privilegeCode = userData.getString(Response.UserData.PRIVILEGE_CODE);


                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sp.edit();
                editor.clear().commit();
                editor.putBoolean(getString(R.string.pref_is_first_run), false);
                editor.putString(getString(R.string.pref_user_number), registrationNumber);
                editor.putString(getString(R.string.pref_user_stage), mClass);
                editor.putString(getString(R.string.pref_user_password), newPassword);
                editor.putString(getString(R.string.pref_user_name), name);
                editor.putString(getString(R.string.pref_privilege_code), privilegeCode);
                editor.commit();
                startActivity(new Intent(getActivity(), MainActivity.class));
                try {
                    getActivity().startService(new Intent(getActivity(), UpdatesCheckService.class));
                } catch (Exception e){
                    e.printStackTrace();
                }
                getActivity().finish();

            } else if (response.getString(Response.SUCCESS).equals("0")){
                /**
                 * Unsuccessful authentication
                 */
                setErrorAndMessage(response.getString(Response.MESSAGE),
                        response.getString(Response.ERROR));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setErrorAndMessage(String message, String error){
        if (error.equalsIgnoreCase(Response.Error.WRONG_PASSWORD)){
            resetPasswordButton.setVisibility(View.GONE);
            tvMessage.setText(error + "\n" + message);
            loginForm.setVisibility(View.VISIBLE);
        } else if (error.equalsIgnoreCase(Response.Error.USER_NOT_IN_SYSTEM)){
            tvMessage.setText(message);
            loginForm.setVisibility(View.VISIBLE);
        }
        responseMessage.setVisibility(View.VISIBLE);
        authenticatingIndicator.setVisibility(View.GONE);
        noConnection.setVisibility(View.GONE);
    }
}
