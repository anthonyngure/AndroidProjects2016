package ke.co.elmaxdevelopers.eventskenya.network;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ke.co.elmaxdevelopers.eventskenya.R;
import ke.co.elmaxdevelopers.eventskenya.adapter.BaseEventsListAdapter;
import ke.co.elmaxdevelopers.eventskenya.model.Queue;
import ke.co.elmaxdevelopers.eventskenya.utils.Helper;
import ke.co.elmaxdevelopers.eventskenya.utils.PrefUtils;

public class APIConnector{

    private static APIConnector mInstance;
    private static AsyncHttpClient client;
    private  static Context context;

    private APIConnector(Context mContext){
        this.context = mContext;
        client = getClient();
    }

    public static synchronized APIConnector getInstance(Context context){
        if (mInstance == null){
            mInstance = new APIConnector(context.getApplicationContext());
        }
        return mInstance;
    }

    public AsyncHttpClient getClient(){
        if (client == null){
            client = new AsyncHttpClient();
            /** configure client here */
            client.setMaxRetriesAndTimeout(5, 20000);
            client.setLoggingEnabled(true);
            client.setEnableRedirects(false);
            client.setURLEncodingEnabled(true);
        }
        return client;
    }




    public void addUser(RequestParams params, LoadingListener listener){
        client.post(BackEnd.absoluteUrl(BackEnd.USERS_ADD), params, createResponseHandler(listener));
    }

    public void loadEvents(RequestParams params, String tag, LoadingListener loadingListener){
        PrefUtils.getInstance(context).getDefaultParams(params);
        client.post(BackEnd.absoluteUrl(BackEnd.EVENTS_END_POINT),
                params, createResponseHandler(loadingListener)).setTag(tag);

        //Toast.makeText(context, "Events request " + eventsLoad + " TAG "+tag+ " With Client " + clients, Toast.LENGTH_LONG).show();
    }

    public void getServices(RequestParams params, LoadingListener listener){
        client.post(BackEnd.absoluteUrl(BackEnd.SERVICES_END_POINT), params, createResponseHandler(listener));
    }

    public void search(String url, RequestParams params, LoadingListener listener){

        client.post(url, PrefUtils.getInstance(context).getDefaultParams(params), createResponseHandler(listener));
    }

    public void checkComments(LoadingListener listener){
        client.post(BackEnd.absoluteUrl(BackEnd.SYNC_COMMENTS), PrefUtils.getInstance(context).getDefaultParams(new RequestParams()),
                createResponseHandler(listener));
    }

    public void refreshComments(long eventId, RequestParams params, LoadingListener listener){
        client.post(BackEnd.absoluteUrl(BackEnd.REFRESH_COMMENTS+eventId),
                PrefUtils.getInstance(context).getDefaultParams(params),
                createResponseHandler(listener));
    }

    public void addGoing(long eventId, GoingInterestClickListener listener, int position,
                         BaseEventsListAdapter adapter, int operation){
        client.post(BackEnd.absoluteUrl(BackEnd.EVENTS_ADD_GOING + eventId),
                PrefUtils.getInstance(context).getDefaultParams(new RequestParams()),
                createResponseHandler(listener, position, adapter, operation));
    }

    public void addGoing(QueueWorkListener listener, Queue queue){
        client.post(BackEnd.absoluteUrl(BackEnd.EVENTS_ADD_GOING + queue.getEvent_id()),
                PrefUtils.getInstance(context).getDefaultParams(new RequestParams()),
                createResponseHandler(listener, queue));
    }

    public void removeGoing(long eventId, GoingInterestClickListener listener, int position,
                            BaseEventsListAdapter adapter,  int operation){

        client.post(BackEnd.absoluteUrl(BackEnd.EVENTS_REMOVE_GOING + eventId),
                PrefUtils.getInstance(context).getDefaultParams(new RequestParams()),
                createResponseHandler(listener, position, adapter, operation));
    }

    public void addInterested(long eventId, GoingInterestClickListener listener, int position,
                              BaseEventsListAdapter adapter,  int operation){

        client.post(BackEnd.absoluteUrl(BackEnd.EVENTS_ADD_INTERESTED + eventId),
                PrefUtils.getInstance(context).getDefaultParams(new RequestParams()),
                createResponseHandler(listener, position, adapter, operation));
    }

    public void addInterested(QueueWorkListener listener, Queue queue){

        client.post(BackEnd.absoluteUrl(BackEnd.EVENTS_ADD_INTERESTED + queue.getEvent_id()),
                PrefUtils.getInstance(context).getDefaultParams(new RequestParams()),
                createResponseHandler(listener, queue));
    }

    public void removeInterested(long eventId, GoingInterestClickListener listener, int position,
                                 BaseEventsListAdapter adapter,  int operation){

        client.post(BackEnd.absoluteUrl(BackEnd.EVENTS_REMOVE_INTERESTED + eventId),
                PrefUtils.getInstance(context).getDefaultParams(new RequestParams()),
                createResponseHandler(listener, position, adapter, operation));
    }

    public void addComment(long eventId, RequestParams params, LoadingListener listener){
        client.post(BackEnd.absoluteUrl(BackEnd.ADD_COMMENT)+eventId,
                PrefUtils.getInstance(context).getDefaultParams(params),createResponseHandler(listener));
    }

    public void addEvent(RequestParams params, LoadingListener listener){
        client.post(BackEnd.absoluteUrl(BackEnd.ADD_EVENT), params,createResponseHandler(listener));
    }


    public void getDetails(String tag, long eventId, LoadingListener listener){
        client.post(BackEnd.absoluteUrl(BackEnd.GET_DETAILS + eventId),
                createResponseHandler(listener)).setTag(tag);
    }

    public void sendImage(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.post(url, params, responseHandler);
    }

    private JsonHttpResponseHandler createResponseHandler(final LoadingListener mListener){
        if (mListener != null){
            return new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    super.onStart();
                    try {
                        mListener.onNetworkDataLoadingStarted();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        mListener.onNetworkDataLoadingSuccess(statusCode, response);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    try {
                        mListener.onNetworkDataLoadingFailed(statusCode);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    try {
                        mListener.onNetworkDataLoadingFailed(statusCode);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    try {
                        mListener.onNetworkDataLoadingFailed(statusCode);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
        } else {
            Helper.toast(context, context.getString(R.string.unknown_error));
            return new JsonHttpResponseHandler();
        }
    }

    private JsonHttpResponseHandler createResponseHandler(final GoingInterestClickListener mListener,
                                                          final int position, final BaseEventsListAdapter adapter, final int type){
        if (mListener != null){
            return new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    super.onStart();
                    try {
                        mListener.onNetworkDataLoadingStarted(position, type);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }


                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        mListener.onNetworkDataLoadingSuccess(statusCode, response, position, adapter, type);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    try {
                        mListener.onNetworkDataLoadingFailed(statusCode, position, adapter, type);
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    try {
                        mListener.onNetworkDataLoadingFailed(statusCode, position, adapter, type);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    try {
                        mListener.onNetworkDataLoadingFailed(statusCode, position, adapter, type);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
        } else {
            Helper.toast(context, context.getString(R.string.unknown_error));
            return new JsonHttpResponseHandler();
        }
    }

    private JsonHttpResponseHandler createResponseHandler(final QueueWorkListener mListener, final Queue queue){
        if (mListener != null){
            return new JsonHttpResponseHandler(){
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        mListener.onNetworkDataLoadingSuccess(statusCode, response, queue);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    try {
                        mListener.onNetworkDataLoadingFailed(statusCode, queue);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    try {
                        mListener.onNetworkDataLoadingFailed(statusCode, queue);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    try {
                        mListener.onNetworkDataLoadingFailed(statusCode, queue);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
        } else {
            Helper.toast(context, context.getString(R.string.unknown_error));
            return new JsonHttpResponseHandler();
        }
    }

    public void updateUsername(RequestParams params, LoadingListener listener) {
        client.post(BackEnd.absoluteUrl(BackEnd.UPDATE_USERNAME),
                PrefUtils.getInstance(context).getDefaultParams(params),
                createResponseHandler(listener));
    }

    public void checkObsolete(AsyncHttpResponseHandler handler) {
        client.post(BackEnd.absoluteUrl(BackEnd.CHECK_OBSOLETE), handler);
    }

    public void addService(RequestParams params, LoadingListener listener) {
        client.post(BackEnd.absoluteUrl(BackEnd.ADD_SERVICE),PrefUtils.getInstance(context).getDefaultParams(params),
                createResponseHandler(listener));
    }
}

