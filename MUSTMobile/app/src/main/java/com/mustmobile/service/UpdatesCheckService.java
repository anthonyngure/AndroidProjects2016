package com.mustmobile.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mustmobile.R;
import com.mustmobile.network.Client;
import com.mustmobile.network.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Tosh on 10/21/2015.
 */
public class UpdatesCheckService extends IntentService {

    private static final String TAG = "UpdatesCheckService";

    public UpdatesCheckService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Toshde", "Service Created");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        connectAndRespond();
        Log.d("Toshde", "Service At handle intent");
    }

    protected void connectAndRespond(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String lastId = sp.getString(getString(R.string.pref_last_update_id), "0");

        Client.post(Client.absoluteUrl("updates/notifications/"+lastId), null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                parseConnectionResponse(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Toshde", "Error " + responseString);

            }
        });
    }

    protected void parseConnectionResponse(JSONObject response) {
        Log.d("Toshde","Response "+response.toString());
        try {
            if (response.getString(Response.SUCCESS).equalsIgnoreCase("1")){

                String lastUpdateId = response.getString(Response.LAST_UPDATE_ID);

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(getString(R.string.pref_last_update_id), lastUpdateId);
                editor.commit();

                String count = response.getString(Response.UPDATES_COUNT);
                int updatesCount = Integer.parseInt(count);
                Log.d("Toshde", "Updates "+updatesCount);
                if (updatesCount > 0){
                    sendBroadcast(new Intent("com.mustmobile.action.NEW_UPDATES").putExtra("updates_count",updatesCount));
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Toshde", "Service Destroyed");
    }
}
