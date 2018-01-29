package com.mustmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mustmobile.model.User;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;
import com.mustmobile.util.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FeedbackActivity extends AppCompatActivity {

    private Button sendButton, retryButton, reportButton;
    private EditText etMessage;
    private String message;
    private ViewGroup loadingIndicator, noConnection, postFeedbackContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initControls();
    }

    private void initControls(){
        sendButton = (Button) findViewById(R.id.activity_feedback_button_send);
        retryButton = (Button) findViewById(R.id.activity_feedback_retry_button);
        reportButton = (Button) findViewById(R.id.activity_feedback_report_button);
        etMessage = (EditText) findViewById(R.id.activity_feedback_message);
        loadingIndicator = (ViewGroup) findViewById(R.id.activity_feedback_loading_indicator);
        noConnection = (ViewGroup) findViewById(R.id.activity_feedback_no_connection);
        postFeedbackContainer = (ViewGroup) findViewById(R.id.activity_feedback_content);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readFields()){
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    connectAndRespond();
                } else {
                    Helper.at(getApplicationContext()).simpleToast("You can\'t send empty feedback!");
                }
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAndRespond();
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private boolean readFields(){
        message = etMessage.getText().toString();
        if (TextUtils.isEmpty(message)){
            return false;
        } else {
            return true;
        }
    }

    public void connectAndRespond(){

        User user =  User.at(this);
        RequestParams params = new RequestParams();
        params.put("content", message);
        params.put("author_name", user.getName());
        params.put("author_number", user.getNumber());

        Client.post(Client.absoluteUrl("postfeedback"), params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (Helper.at(getApplicationContext()).isOnline()) {
                    loadingIndicator.setVisibility(View.VISIBLE);
                    noConnection.setVisibility(View.GONE);
                    postFeedbackContainer.setVisibility(View.GONE);
                    getSupportActionBar().setSubtitle("Sending...");
                } else {
                    postFeedbackContainer.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.GONE);
                    noConnection.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseConnectionResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Toshde", "Error posting topic " + responseString);
                noConnection.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
                getSupportActionBar().setSubtitle("");
            }
        });
    }

    protected void parseConnectionResponse(JSONObject response){
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){
                noConnection.setVisibility(View.GONE);
                loadingIndicator.setVisibility(View.GONE);
                Helper.at(getApplicationContext()).simpleToast("Feedback submitted successfully!");
                getSupportActionBar().setSubtitle("");
                this.finish();
            } else {
                noConnection.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_activity_feedback_action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
