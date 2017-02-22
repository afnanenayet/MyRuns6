package com.afnanenayet.afnan_enayet_myruns6;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Created by aenayet on 2/19/17.
 */

/**
 * Handles notifications from GCM which indicate that an Entry should be deleted
 */
public class GcmIntentService extends IntentService {
    private static final String SERVICE_NAME = "GcmIntentService";
    private static final String DEBUG_TAG = SERVICE_NAME;

    public GcmIntentService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(DEBUG_TAG, "Intent received");

        // Unparceling bundle received with intent
        Bundle bundle = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        long entryId;

        // Doing a null-check and unparceling bundle
        if (bundle != null && !bundle.isEmpty()) {
            // Checking to make sure message types match up
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.d(DEBUG_TAG, "Valid message type from GCM message");
                // Getting ID of entry to be deleted from the server then deleting
                Log.d(DEBUG_TAG, bundle.getString("message"));

                // Parsing ID value from GCM message
                entryId = Long.parseLong(bundle.getString("message"));

                Log.d(DEBUG_TAG, "Received ID " + entryId);
                deleteEntry(entryId);
                Logger.getLogger("GCM notification received").log(Level.INFO, bundle.toString());
            }
        }

        // Completing intent and ending wakelock
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /**
     * Deletes an Entry using the databasehelper
     * @param entryId The ID of the entry to delete
     */
    protected void deleteEntry(final long entryId) {
        // Posting the deletion within the app thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(DEBUG_TAG, "Deleting entry " + entryId + " from main looper");
                // Deleting entry from SQLite database
                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                dbHelper.removeEntry(entryId);

                // Close IO connection
                dbHelper.close();

                // If history fragment is open, then update entries
                if (HistoryFragment.getInstance() != null) {
                    HistoryFragment instance = HistoryFragment.getInstance();
                    instance.updateEntries();
                }
            }
        });
    }
}
