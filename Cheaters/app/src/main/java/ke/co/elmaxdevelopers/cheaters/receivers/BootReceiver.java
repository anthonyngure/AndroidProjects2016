package ke.co.elmaxdevelopers.cheaters.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ke.co.elmaxdevelopers.cheaters.MainActivity;

/**
 * Created by Tosh on 1/28/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "Toshde";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot completed");
        context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
