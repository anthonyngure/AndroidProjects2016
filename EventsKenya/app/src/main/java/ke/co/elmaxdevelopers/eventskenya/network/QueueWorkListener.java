package ke.co.elmaxdevelopers.eventskenya.network;


import org.json.JSONObject;

import ke.co.elmaxdevelopers.eventskenya.model.Queue;

/**
 * Created by Tosh on 1/5/2016.
 */
public interface QueueWorkListener {

    void onNetworkDataLoadingFailed(int statusCode, Queue queue);
    void onNetworkDataLoadingSuccess(int statusCode, JSONObject response, Queue queue);

}
