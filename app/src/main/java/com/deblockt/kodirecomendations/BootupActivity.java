package com.deblockt.kodirecomendations;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
             
public class BootupActivity extends BroadcastReceiver {
    private static final String TAG = "BootupActivity";

    private static final long INITIAL_DELAY = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "BootupActivity initiated. Aaction : " + intent.getAction());
        if (intent.getAction().endsWith(Intent.ACTION_BOOT_COMPLETED)) {
            scheduleRecommendationUpdate(context);
        }
   }

    private void scheduleRecommendationUpdate(Context context) {
        Log.d(TAG, "Scheduling recommendations update");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent recommendationIntent = new Intent(context, UpdateRecommendationsService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, 0, recommendationIntent, 0);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                INITIAL_DELAY,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                alarmIntent);


    }


}
