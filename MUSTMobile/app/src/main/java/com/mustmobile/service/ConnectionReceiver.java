package com.mustmobile.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mustmobile.util.Helper;

/**
 * Created by Tosh on 10/21/2015.
 */
public class ConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectionReceiver.class.getSimpleName();
    private static final long DEFAULT_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Toshde","Connection changed");
        long interval = DEFAULT_INTERVAL;

        if (Helper.at(context).isOnline()){
            PendingIntent operation = PendingIntent.getService(context, -1,
                    new Intent(context, UpdatesCheckService.class), PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, operation);
            Log.d("Toshde", "Setting repeat operation for : "+interval);

            /*if (interval == 0){
                alarmManager.cancel(operation);
                Log.d("Toshde", "Cancelling repeat operation!!!");
            } else {
                alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, operation);
                Log.d("Toshde", "Setting repeat operation for : "+interval);
            }*/
        } else {
            Log.d("Toshde","User is offline cant start the service");
        }

        Log.d("Toshde", "onReceived");
    }
}
