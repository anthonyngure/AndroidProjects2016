package com.mustmobile.network;

import android.util.Log;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class Client {

    public static final int TOPIC_LOAD_LIMIT = 5;

    public abstract static class Error {
        public static final int UN_AUTHORIZED_ACCESS = 403;
        public static final int RESOURCE_NOT_FOUND = 404;
        public static final int SERVER_ERROR = 505;
    }


    private static AsyncHttpClient client = null;
    //public static final String BASE_URL = "http://ca.must.ac.ke/api/v1/";
    public static final String BASE_URL = "http://192.168.234.1/www.mustmobile.com/api/v1/";

    private static void setUpClient(){
        client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(10, 10000);
    }

    public static String absoluteUrl(String relativeUrl) {
        //client.setProxy("10.10.255.254", 8080);
        URL url = null;
        try {
            url = new URL((BASE_URL+relativeUrl).trim().replace(" ","%20"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("ToshLog", "Connecting url " + url.toString());
        return url.toString();
    }

    public static void get(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        setUpClient();
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        setUpClient();
        client.post(url, params, responseHandler);
    }

    public static void post(String url, String tag, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {

        if (client != null){
            client.post(url, params, responseHandler).setTag(tag);
        } else {
            Log.d("Toshde", "Requests canceled ==> TAG : "+tag);
        }
    }

    public static void cancelMyRequests(String tag) {
        try {
            client.cancelAllRequests(true);
            client.cancelRequestsByTAG(tag, true);
        } catch (Exception e){
            e.printStackTrace();
        }
        client = null;
        Log.d("Toshde", "All requests canceled");
    }

    public static void updateViewsCount(String id) {
        Client.post(Client.absoluteUrl("updates/addviewscount/" + id), null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}

