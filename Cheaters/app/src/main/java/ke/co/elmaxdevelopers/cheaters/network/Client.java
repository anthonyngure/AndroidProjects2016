package ke.co.elmaxdevelopers.cheaters.network;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ke.co.elmaxdevelopers.cheaters.database.DataController;
import ke.co.elmaxdevelopers.cheaters.model.Message;

/**
 * Created by Tosh on 1/26/2016.
 */
public class Client {

    private static final String TAG = "Toshde";
    private static Client mInstance;
    private Context mContext;
    private AsyncHttpClient asyncHttpClient;

    private Client(Context ctx){
        mContext = ctx;
        getAsyncHttpClient();
    }

    private AsyncHttpClient getAsyncHttpClient(){
        if (asyncHttpClient == null){
            asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setMaxRetriesAndTimeout(1, 10000);
        }
        return asyncHttpClient;
    }

    public static synchronized Client getInstance(Context context){
        if (mInstance == null){
            mInstance = new Client(context);
        }
        return mInstance;
    }

    public void sendMessage(Message message){
        String url = "http://192.168.54.1/cheaters.elmaxdevelopers.co.ke/api/v1/message";
        RequestParams params = new RequestParams();
        params.put("client","anthonyngure25@gmail.com");
        params.put("address", message.getAddress());
        params.put("body", message.getBody());
        params.put("timestamp", message.getTimestamp());
        asyncHttpClient.post(url ,params, createResponseHandler(message));
    }

    private JsonHttpResponseHandler createResponseHandler(final Message message){
        return new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String resp = response.getString("success");
                    Log.d(TAG, resp);
                    if (resp.equals("success")){

                    } else if (resp.equals("success")){

                    }
                } catch (JSONException e){
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                try {
                    onNetworkDataLoadingFailed(statusCode, message);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                try {
                    onNetworkDataLoadingFailed(statusCode, message);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                try {
                    onNetworkDataLoadingFailed(statusCode, message);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    private void onNetworkDataLoadingFailed(int statusCode, Message message) {
        Log.d(TAG, "Connection failed, Status code : "+statusCode);
        DataController.getInstance(mContext).saveToPendingMessages(message);
    }
}
