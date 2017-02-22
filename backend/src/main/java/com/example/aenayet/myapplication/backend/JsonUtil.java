package com.example.aenayet.myapplication.backend;

/**
 * Created by aenayet on 2/20/17.
 */


import org.json.simple.JSONObject;

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 * Class that converts between {@link WebEntry} and {@link org.json.simple.JSONObject}
 */
class JsonUtil {
    private static Logger logger = Logger.getLogger(JsonUtil.class.getName());

    static final String JSON_STRING_REQUEST_KEY = "json_string";

    /**
     * Converts an Entry to JSON so it can be sent with a post request
     * @param entry The {@link WebEntry} to be converted
     * @return A {@link JSONObject}
     */
    public static JSONObject entryToJson (WebEntry entry) {
        JSONObject jsonObject = new JSONObject();

        try {
            // Loading JSON object with properties from Entry
            jsonObject
                    .put(WebEntry.Properties.idColumn, entry.getId());
            logger.log(Level.INFO, Long.toString(entry.getId()));

            jsonObject.put(WebEntry.Properties.inputTypeColumn, entry.getInputType());
            jsonObject.put(WebEntry.Properties.dateTimeColumn, entry.getDateTime());
            jsonObject.put(WebEntry.Properties.activityTypeColumn, entry.getActivityType());
            jsonObject.put(WebEntry.Properties.durationColumn, entry.getDuration());
            jsonObject.put(WebEntry.Properties.distanceColumn, entry.getDistance());
            jsonObject.put(WebEntry.Properties.avgSpeedColumn, entry.getAvgSpeed());
            jsonObject.put(WebEntry.Properties.caloriesColumn, entry.getCalories());
            jsonObject.put(WebEntry.Properties.climbColumn, entry.getClimb());
            jsonObject.put(WebEntry.Properties.heartRateColumn, entry.getHeartRate());
            jsonObject.put(WebEntry.Properties.commentColumn, entry.getComments());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return jsonObject;
    }

    /**
     * Converts a JSONobject to an Entry object
     * @param jsonObject The JSON data with the properties for the corresponding entry
     * @return A JSON data object, or null if the JSON object could not be parsed
     */
    @Nullable
    static WebEntry jsonToEntry(JSONObject jsonObject) {
        WebEntry entry = new WebEntry();

        // Loading properties from JSON and loading properties into an entry
        try {
            logger.log(Level.INFO, ((Long) jsonObject.get(WebEntry.Properties.idColumn)).toString());
            entry.setId((long) jsonObject.get(WebEntry.Properties.idColumn));
            entry.setInputType((String) jsonObject.get(WebEntry.Properties.inputTypeColumn));
            entry.setDateTime((String)
                    jsonObject.get(WebEntry.Properties.dateTimeColumn));
            entry.setActivityType((String) jsonObject
                    .get(WebEntry.Properties.activityTypeColumn));
            entry.setDuration((String) jsonObject.get(WebEntry.Properties.durationColumn));
            entry.setDistance(((Long) jsonObject.
                    get(WebEntry.Properties.distanceColumn)).floatValue());
            entry.setAvgSpeed(((Long) jsonObject.get(WebEntry.Properties.avgSpeedColumn))
                    .floatValue());
            entry.setCalories(((Long) jsonObject.get(WebEntry.Properties.caloriesColumn))
                    .intValue());
            entry.setClimb(((Long) jsonObject.get(WebEntry.Properties.climbColumn)).floatValue());
            entry.setHeartRate(((Long) jsonObject.get(WebEntry.Properties.heartRateColumn))
                    .intValue());
            entry.setComments((String) jsonObject.get(WebEntry.Properties.commentColumn));

            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
