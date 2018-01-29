package com.mustmobile.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.mustmobile.NewUpdatesActivity;
import com.mustmobile.R;

/**
 * Created by Tosh on 10/21/2015.
 */
public class NotificationsReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 42;

    @Override
    public void onReceive(Context context, Intent intent) {
        
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        int updatesCount = intent.getIntExtra("updates_count", 0);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.getBoolean(context.getString(R.string.pref_notification_tones), true)){
            final MediaPlayer mediaPlayer;
            mediaPlayer = MediaPlayer.create(context, R.raw.notification);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                }
            });
        }

        PendingIntent operation = PendingIntent.getActivity(context, -1,
                new Intent(context, NewUpdatesActivity.class), PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Important school updates")
                .setContentText("You have " + updatesCount + " new updates to read")
                .setContentIntent(operation)
                .setAutoCancel(true)
                .getNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
