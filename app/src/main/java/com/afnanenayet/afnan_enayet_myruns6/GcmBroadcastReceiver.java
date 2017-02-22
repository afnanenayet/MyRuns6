package com.afnanenayet.afnan_enayet_myruns6;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by aenayet on 2/19/17.
 */

/**
 * Implements a receiver that creates a CPU wakelock and performs an action based on the result
 * received from the GCM server. Offload task to {@link GcmIntentService}
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final String DEBUG_TAG = "GcmBcReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(DEBUG_TAG, "Received broadcast from GCM");

        // Only accept a message if registered sender is from MyRuns server
        boolean validSender = intent.getExtras().get("from").equals(GcmRegistrationAsync.SENDER_ID);

        // If the broadcast is from GCM then we will propagate message to service
        if (validSender) {
            Log.d(DEBUG_TAG, "Valid sender, launching intent service");

            // Starting intent service and specifying that result was ok
            startWakefulService(context, intent.setClass(context, GcmIntentService.class));
            setResultCode(Activity.RESULT_OK);
        }
    }
}
