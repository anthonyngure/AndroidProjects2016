package ke.co.elmaxdevelopers.eventskenya.network;

import org.json.JSONObject;

/**
 * Created by Tosh on 12/30/2015.
 */
public interface LoadingListener {

    void onNetworkDataLoadingStarted();
    void onNetworkDataLoadingFailed(int statusCode);
    void onNetworkDataLoadingSuccess(int statusCode, JSONObject response);
}
