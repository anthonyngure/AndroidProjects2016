package ke.co.elmaxdevelopers.cheaters.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Tosh on 1/26/2016.
 */
public class ConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = "Toshde";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Connection received!");
    }
}
