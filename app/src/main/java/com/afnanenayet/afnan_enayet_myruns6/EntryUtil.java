package com.afnanenayet.afnan_enayet_myruns6;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by aenayet on 1/27/17.
 */

final class EntryUtil {
    private final static String DEBUG_TAG = "EntryUtil";

    /**
     * Converts an {@link ArrayList} of locations to a Byte array to be saved as a blob
     *
     * @param locationArrayList The input ArrayList of locations
     * @return A blobbable byte array
     */
    @Nullable
    static byte[] locationToBinary(ArrayList<Location> locationArrayList) {
        // null -> null
        if (locationArrayList == null || locationArrayList.size() == 0) return null;

        // Each location has two doubles
        final int byteRatio = Double.SIZE / Byte.SIZE;
        byte[] bytes = new byte[2 * byteRatio * locationArrayList.size()];

        // Storing lattitudes and longitudes from locations
        for (int i = 0; i < locationArrayList.size(); i++) {
            int index_1 = i * 2;
            int index_2 = index_1 + 1;
            ByteBuffer.wrap(bytes, index_1 * byteRatio, byteRatio)
                    .putDouble(locationArrayList.get(i).getLatitude());
            ByteBuffer.wrap(bytes, index_2 * byteRatio, byteRatio)
                    .putDouble(locationArrayList.get(i).getLongitude());
        }

        return bytes;
    }

    /**
     * Converts a byte array to an {@link ArrayList} of Locations
     *
     * @param locationByteArray a byte array representing location data
     * @return The input as an {@link ArrayList} of {@link Location}
     */
    static ArrayList<Location> binaryToLocation(byte[] locationByteArray) {
        // If we have no location array, return null
        if (locationByteArray == null || locationByteArray.length == 0) return null;

        // Converting stored byte array to a double array
        final int byteRatio = Double.SIZE / Byte.SIZE;
        ArrayList<Location> locations = new ArrayList<>();
        double[] doubles = new double[locationByteArray.length / byteRatio];

        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = ByteBuffer.wrap(locationByteArray, i * byteRatio, byteRatio).getDouble();
        }

        // Retrieving lats and longs from our doubles
        for (int i = 0; i < doubles.length / 2; i++) {
            int latIndex = i * 2;
            int longIndex = latIndex + 1;

            // Creating location object from doubles
            Location tmp = new Location("");
            tmp.setLatitude(doubles[latIndex]);
            tmp.setLongitude(doubles[longIndex]);

            locations.add(tmp);
        }

        return locations;
    }

    /**
     * Converts kilometers to miles
     *
     * @param kilometers a distance in kilometers
     * @return a distance in miles
     */
    @Contract(pure = true)
    static float kmToMiles(float kilometers) {
        return kilometers * 0.621371f;
    }

    /**
     * Converts miles to kilometers
     *
     * @param miles a distance in miles
     * @return a distance in kilometers
     */
    @Contract(pure = true)
    static float milesToKm(float miles) {
        return miles * 1.60934f;
    }

    /**
     * Checks preference to return the proper distance value based on whether user selects imperial
     * or metric
     *
     * @param context     The context
     * @param rawDistance The distance returned from the entry
     * @return The converted distance based on what unit the user selected
     */
    static float convertedDistance(Context context, float rawDistance) {
        float distance;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isMetric = prefs.getString(context.getString(R.string.pref_unit_preference_key),
                "metric")
                .equals("metric");

        if (isMetric) {
            distance = rawDistance;
        } else {
            distance = EntryUtil.kmToMiles(rawDistance);
        }

        return distance;
    }

    /**
     * Gets the proper unit string based on the user preferences
     *
     * @param context The context of the activity calling this function
     * @return A string with a human readable representation of the unit that will be displayed
     * to the user
     */
    @NonNull
    static String getUnitString(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isMetric = prefs.getString(context.getString(R.string.pref_unit_preference_key),
                "metric")
                .equals("metric");

        if (isMetric) {
            return context.getString(R.string.metric_units_distance);
        } else {
            return context.getString(R.string.imperial_units_distance);
        }
    }

    /**
     * Returns a representation of time formatted nicely as a string
     *
     * @param time The amount of seconds to convert
     * @return a string in the format XX hour(s) XX minute(s) XX second(s)
     */
    static String getTimeString(long time) {
        String timeString = "";
        Long hours = time / 3600;
        Long minutes = (time - (hours * 3600)) / 60;
        Long seconds = time - ((hours * 3600) + (minutes * 60));

        // Converting time to string with proper plurality of units
        // because of the way java evaluates if/else, if the hour == 1, then the hour > 0
        // statement will not be executed
        if (hours == 1) {
            timeString += "1 hour ";
        } else if (hours > 0) {
            timeString += hours.toString() + " hours ";
        }

        if (minutes == 1) {
            timeString += "1 minute ";
        } else if (minutes > 0) {
            timeString += minutes.toString() + " minutes ";
        }

        if (seconds == 1) {
            timeString += "1 second ";
        } else if (seconds > 0) {
            timeString += seconds.toString() + " seconds ";
        }

        // If duration is 0, set 0 seconds as string representation
        if (timeString.length() == 0) {
            return "0 seconds";
        } else {
            return timeString;
        }

    }
}
