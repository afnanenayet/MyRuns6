package com.afnanenayet.afnan_enayet_myruns6;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

/**
 * Helper class used to communicate with the AppEngine server.
 * Taken from XD's example. He said it was ok:
 * <a href=https://piazza.com/class?cid=izd2sni9kpg1bt&nid=ixggr2bfwpo45k&token=vwnSoJcip4b/>
 */
public final class ServerUtilities {

    // public static final String SERVER_ADDRESS = "https://myruns-1486240948224.appspot.com";
    public static final String SERVER_ADDRESS = "http://127.0.0.1:8080";
    public static final String SERVER_ID_KEY = "server_entry_id_key";
    public static final String SENDER_ID_API = "526923652751";
    public static final String ENTRY_REQUEST_KEY = "json_string";

    private static final String DEBUG_TAG = "ServerUtil";

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();


    /**
     * Issue a POST request to the server.
     *
     * @param endpoint
     *            POST address.
     * @param params
     *            request parameters.
     *
     * @throws IOException
     *             propagated from POST.
     */
     static String post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }

        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);//XD: used with POST to allow sending a body via the connection:
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();//Log.d("XD", body);//XD: output stream: https://abstract-arc-123122.appspot.com/add.do?phone=12345678&name=xd&addr=Sudikoff211
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            Log.d("TAGG",""+status);
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }

            // Get Response
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
            rd.close();
            return response.toString();

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Issue a POST request to the server and passes along a JSON string
     *
     * @param endpoint
     *            POST address.
     * @param params
     *            request parameter
     *
     * @throws IOException
     *             propagated from POST.
     */
     static String post(String endpoint, String params)
            throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put(ServerUtilities.ENTRY_REQUEST_KEY, params);

         // Redirecting
        return post(endpoint, map);
    }

    /**
     * Converts an Entry to JSON so it can be sent with a post request
     * @param entry The {@link Entry} to be converted
     * @return A {@link JSONObject}
     */
     static JSONObject entryToJson (Context context, com.afnanenayet.afnan_enayet_myruns6.Entry entry) {
        JSONObject jsonObject = new JSONObject();

        try {
            // Loading JSON object with properties from Entry
            // Passing formatted strings
            jsonObject
                    .put(EntryDataSource.ActivityEntry.idColumn, entry.getId())
            .put(EntryDataSource.ActivityEntry.inputTypeColumn,
                    EntryDataSource.getInputTypeString(context, entry.getInputType()))
           .put(EntryDataSource.ActivityEntry.dateTimeColumn, EntryDataSource
                   .getDateTimeString(entry.getDateTime()))
            .put(EntryDataSource.ActivityEntry.activityTypeColumn,
                    EntryDataSource.getActivityTypeString(context,entry.getActivityType()))
            .put(EntryDataSource.ActivityEntry.durationColumn,
                    EntryUtil.getTimeString(entry.getDuration()))
            .put(EntryDataSource.ActivityEntry.distanceColumn, entry.getDistance())
            .put(EntryDataSource.ActivityEntry.avgPaceColumn, entry.getAvgPace())
            .put(EntryDataSource.ActivityEntry.avgSpeedColumn, entry.getAvgSpeed())
            .put(EntryDataSource.ActivityEntry.caloriesColumn, entry.getCalories())
            .put(EntryDataSource.ActivityEntry.climbColumn, entry.getClimb())
            .put(EntryDataSource.ActivityEntry.heartRateColumn, entry.getHeartRate())
            .put(EntryDataSource.ActivityEntry.commentColumn, entry.getComments())
            .put(EntryDataSource.ActivityEntry.privacyColumn, Integer.toString(entry.getPrivacy()))
            .put(EntryDataSource.ActivityEntry.gpsDataColumn,
                    EntryUtil.locationToBinary(entry.getLocations()));
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "Failed to JSONify Entry object");
            e.printStackTrace();
            return null;
        }

        return jsonObject;
    }
}
