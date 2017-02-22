package com.afnanenayet.afnan_enayet_myruns6;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.aenayet.myapplication.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by aenayet on 2/19/17.
 */

/**
 * Registers device with Google Cloud Messaging and returns
 */
public class GcmRegistrationAsync extends AsyncTask<Void, Void, String> {
    private Context mContext;
    private Registration registration;
    private GoogleCloudMessaging gcm;

    // Sender ID from Google apps
    static final String SENDER_ID = "526923652751";
    private static final String DEBUG_TAG = "GCM Registration";

    public GcmRegistrationAsync(Context context) {
        mContext = context;
    }

    @Override
    protected String doInBackground(Void... voids) {

        if (registration == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp
                    .newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // Setting root URL and client request initializer for debugging purposes
                    .setRootUrl(ServerUtilities.SERVER_ADDRESS + "/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?>
                                                       abstractGoogleClientRequest)
                                throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            registration = builder.build();
        }

        String debugMessage;

        // Get registration ID or return error message
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(mContext);
            }
            String registrationId = gcm.register(SENDER_ID);

            registration.register(registrationId).execute();
            Log.d(DEBUG_TAG, "Registration ID: " + registrationId);
            debugMessage = "Registration id: " + registrationId;

        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Failed to register with GCM service");
            e.printStackTrace();
            debugMessage = "Failed to register with GCM";
        }

        // return null;
        return debugMessage;
    }

    // Display reg id or error message to user
    @Override
    protected void onPostExecute(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        Logger.getLogger(DEBUG_TAG).log(Level.INFO, message);
    }
}
