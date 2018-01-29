package ke.co.elmaxdevelopers.eventskenya.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.network.APIConnector;
import ke.co.elmaxdevelopers.eventskenya.network.BackEnd;
import ke.co.elmaxdevelopers.eventskenya.network.LoadingListener;
import ke.co.elmaxdevelopers.eventskenya.network.Response;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;
import ke.co.elmaxdevelopers.eventskenya.views.ProgressWheel;

public class LoginActivity extends AppCompatActivity implements LoadingListener {

    private TextInputLayout inputLayout;
    private EditText etUsername;
    private String username;
    private ViewGroup loadingScreen, loginArea;
    private CheckBox checkBox;
    private ProgressWheel progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputLayout = (TextInputLayout) findViewById(R.id.activity_login_inputlayout);


        etUsername = (EditText) findViewById(R.id.activity_login_username_input);
        TextView tvTerms = (TextView) findViewById(R.id.activity_login_terms_and_conditions);
        checkBox = (CheckBox) findViewById(R.id.activity_login_checkbox);
        loadingScreen = (ViewGroup) findViewById(R.id.loading_screen);
        loginArea = (ViewGroup) findViewById(R.id.login_details_area);
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressWheel.setText("...");
        progressWheel.setTextSize(14);

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = getString(R.string.company_website);
                if (!url.isEmpty()) {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    intent.setData(Uri.parse(url));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });
        etUsername.addTextChangedListener(Helper.createTextWatcher(20, inputLayout, etUsername, getString(R.string.hint_username)));
    }

    public void requestUsername (View view){
        username = etUsername.getText().toString().trim();
        if (username.length() >= 4) {
            if (checkBox.isChecked()){
                RequestParams params = new RequestParams();
                params.put(BackEnd.PARAM_USERNAME,username);
                APIConnector.getInstance(getApplicationContext()).addUser(params, this );
            } else {
                Helper.toast(this, "You must agree to our terms and conditions!");
            }

        } else {
            inputLayout.setError("Username must have more than 3 characters!");
        }
    }

    private void mParseResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equals(Response.SUCCESS)){
                PrefUtils.getInstance(this).writeDefaultInitialPrefs(username);
                Helper.toast(this, getString(R.string.username_created_successfully));
                startActivity(new Intent(getApplicationContext(), StarterSettingsActivity.class));
                LoginActivity.this.finish();
            } else if (response.getString(Response.SUCCESS).equals(Response.FAILURE)){
                loginArea.setVisibility(View.VISIBLE);
                loadingScreen.setVisibility(View.GONE);
                inputLayout.setError(response.getString(Response.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkDataLoadingStarted() {
        progressWheel.startSpinning();
        loadingScreen.setVisibility(View.VISIBLE);
        loginArea.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkDataLoadingFailed(int statusCode) {
        progressWheel.stopSpinning();
        loadingScreen.setVisibility(View.GONE);
        loginArea.setVisibility(View.VISIBLE);
        inputLayout.setError("Connection Error! Please check your network status..");
    }

    @Override
    public void onNetworkDataLoadingSuccess(int statusCode, JSONObject response) {
        progressWheel.stopSpinning();
        mParseResponse(response);
    }

}
