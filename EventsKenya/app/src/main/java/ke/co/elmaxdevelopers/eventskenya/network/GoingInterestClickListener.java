package ke.co.elmaxdevelopers.eventskenya.network;


import org.json.JSONObject;

import ke.co.elmaxdevelopers.eventskenya.adapter.BaseEventsListAdapter;

/**
 * Created by Tosh on 1/5/2016.
 */
public interface GoingInterestClickListener {

    void onNetworkDataLoadingStarted(int position, int operation);
    void onNetworkDataLoadingFailed(int statusCode, int position, BaseEventsListAdapter adapter, int operation);
    void onNetworkDataLoadingSuccess(int statusCode, JSONObject response, int position, BaseEventsListAdapter adapter, int operation);

}
