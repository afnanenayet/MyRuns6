package com.afnanenayet.afnan_enayet_myruns6;

/**
 * Created by aenayet on 1/26/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Contract between {@link Entry} and {@link DatabaseHelper}
 * Contains column ID strings
 */
final class EntryDataSource {

    // SQL columns for the internal database
    final static String[] allColumns = {
            ActivityEntry.idColumn,
            ActivityEntry.inputTypeColumn,
            ActivityEntry.dateTimeColumn,
            ActivityEntry.activityTypeColumn,
            ActivityEntry.durationColumn,
            ActivityEntry.distanceColumn,
            ActivityEntry.avgPaceColumn,
            ActivityEntry.avgSpeedColumn,
            ActivityEntry.caloriesColumn,
            ActivityEntry.climbColumn,
            ActivityEntry.heartRateColumn,
            ActivityEntry.commentColumn,
            ActivityEntry.privacyColumn,
            ActivityEntry.gpsDataColumn
    };
    private final static String DEBUG_TAG = "EntryDS";

    // Keeping constructor private so no instances of this class can be generated
    private EntryDataSource() {
    }

    /**
     * Converts a cursor to an entry object
     *
     * @param cursor The cursor to be converted to an {@link Entry} object
     * @return An {@link Entry} containing the data given in the cursor
     */
    static Entry cursorToEntry(Cursor cursor) {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        String dateString = cursor.getString(2);

        try {
            calendar.setTime(dateFormat.parse(dateString));
        } catch (ParseException e) {
            calendar = null;
            Log.e(DEBUG_TAG, "Failed to parse date");
        }
        Entry entry = new Entry();
        entry.setId(cursor.getLong(0));
        entry.setInputType(cursor.getInt(1));
        entry.setDateTime(calendar);
        entry.setActivityType(cursor.getInt(3));
        entry.setDuration(cursor.getInt(4));
        entry.setDistance(cursor.getFloat(5));
        entry.setAvgPace(cursor.getFloat(6));
        entry.setAvgSpeed(cursor.getFloat(7));
        entry.setCalories(cursor.getInt(8));
        entry.setClimb(cursor.getFloat(9));
        entry.setHeartRate(cursor.getInt(10));
        entry.setComments(cursor.getString(11));
        entry.setPrivacy(cursor.getInt(12));

        ArrayList<Location> locationList = EntryUtil.binaryToLocation(cursor.getBlob(13));
        entry.setLocations(locationList);
        return entry;
    }

    /**
     * Gets the string for an activity based off of the index of the activity
     *
     * @param activityIndex The index retrieved from the database
     * @return A string representing the activity for human consumption
     */
    static String getActivityTypeString(Context context, int activityIndex) {
        String[] activityArray = context.getResources().getStringArray(R.array.activity_array);
        return activityArray[activityIndex];
    }

    /**
     * Gets the string for an input type based off of the int retrieved from database
     *
     * @param context    Context of the activity
     * @param inputIndex The index retrieved from the database
     * @return A human consumable representation of the input type
     */
    static String getInputTypeString(Context context, int inputIndex) {
        String[] inputArray = context.getResources().getStringArray(R.array.input_type_array);
        return inputArray[inputIndex];
    }

    /**
     * Gets a human readable version of a calendar parameter
     *
     * @param calendar The calendar object to be converted to a string
     * @return A human readable string format representation of the calendar object
     */
    static String getDateTimeString(Calendar calendar) {
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Converts an entry to a database cursor
     *
     * @param entry The input {@link Entry} values
     * @return A {@link Cursor} object with the values given from the entry
     */
    static ContentValues entryToValues(Entry entry) {
        ContentValues values = new ContentValues();
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        values.put(ActivityEntry.inputTypeColumn, entry.getInputType());

        if (entry.getDateTime() != null) {
            values.put(ActivityEntry.dateTimeColumn, dateFormat
                    .format(entry.getDateTime().getTime()));
        }

        // Adding values from Entry to contentview
        values.put(ActivityEntry.activityTypeColumn, entry.getActivityType());
        values.put(ActivityEntry.durationColumn, entry.getDuration());
        values.put(ActivityEntry.distanceColumn, entry.getDistance());
        values.put(ActivityEntry.avgPaceColumn, entry.getAvgPace());
        values.put(ActivityEntry.avgSpeedColumn, entry.getAvgSpeed());
        values.put(ActivityEntry.caloriesColumn, entry.getCalories());
        values.put(ActivityEntry.climbColumn, entry.getClimb());
        values.put(ActivityEntry.heartRateColumn, entry.getHeartRate());

        // Null check comments
        if (entry.getComments() != null) {
            values.put(ActivityEntry.commentColumn, entry.getComments());
        }

        values.put(ActivityEntry.privacyColumn, entry.getPrivacy());

        // Nullcheck locations as to not cause a null exception
        if (entry.getLocations() != null) {
            byte[] locationBytes = EntryUtil
                    .locationToBinary(entry.getLocations());
            values.put(ActivityEntry.gpsDataColumn, locationBytes);

        }

        return values;
    }

    /**
     * Stores the names of the columns in the internal DB
     */
    final class ActivityEntry implements BaseColumns {
        final static String idColumn = "_id";
        final static String inputTypeColumn = "input_type";
        final static String dateTimeColumn = "date_time";
        final static String activityTypeColumn = "activity_type";
        final static String durationColumn = "duration";
        final static String distanceColumn = "distance";
        final static String avgPaceColumn = "avg_pace";
        final static String avgSpeedColumn = "avg_speed";
        final static String caloriesColumn = "calories";
        final static String climbColumn = "climb";
        final static String heartRateColumn = "heart_rate";
        final static String commentColumn = "comment";
        final static String privacyColumn = "privacy";
        final static String gpsDataColumn = "gps_data";

        // Private constructor to prevent initialization
        private ActivityEntry() {
        }
    }
}

