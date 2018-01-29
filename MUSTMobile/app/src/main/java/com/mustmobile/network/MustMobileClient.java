package com.mustmobile.network;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by Tosh on 10/7/2015.
 */
public abstract class MustMobileClient extends Client {

    public static AsyncHttpClient client = new AsyncHttpClient();

    public static void authenticate(String userNumber, String password){
        client.setBasicAuth(userNumber, password);
    }
}
