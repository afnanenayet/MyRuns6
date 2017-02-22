package com.example.aenayet.myapplication.backend;

/**
 * Created by aenayet on 2/19/17.
 */

/**
 * A model for an Entry class that can be represented in AppEngine
 * This means that all fields are serializable
 */
public class WebEntry {
    /******************************* Strings for Datastore **************************************/
    public final static String ENTRY_ENTITY_KIND = "entry";
    public final static String PARENT_KIND = "device_identifier";
    public final static String PARENT_IDENTIFIER = "parent_id";

    // Property strings for entry
    final class Properties {
        final static String idColumn = "_id";
        final static String inputTypeColumn = "input_type";
        final static String dateTimeColumn = "date_time";
        final static String activityTypeColumn = "activity_type";
        final static String durationColumn = "duration";
        final static String distanceColumn = "distance";
        final static String avgSpeedColumn = "avg_speed";
        final static String caloriesColumn = "calories";
        final static String climbColumn = "climb";
        final static String heartRateColumn = "heart_rate";
        final static String commentColumn = "comment";

        // Private constructor to prevent initialization
        private Properties() { }
    }

    private long id; // ID
    private String inputType; // Input type (auto, manual, etc)
    private String activityType; // Activity type (running, walking, etc)
    private String dateTime; // Date and time
    private String duration; // Duration of activity
    private float distance; // Distance of activity
    private float avgPace; // Average pace
    private int calories;  // Calories
    private int heartRate; // Average heart rate
    private float avgSpeed; // Average speed
    private float climb; // Total climb
    private String comments; // Comments

    // Getters and setters
    long getId() {
        return this.id;
    }

    void setId(long idToSet) {
        this.id = idToSet;
    }

    String getInputType() {
        return this.inputType;
    }

    void setInputType(String inputTypeToSet) {
        this.inputType = inputTypeToSet;
    }

    String getDateTime() {
        return this.dateTime;
    }

    void setDateTime(String dateTimeToSet) {
        this.dateTime = dateTimeToSet;
    }

    String getDuration() {
        return this.duration;
    }

    void setDuration(String durationToSet) {
        this.duration = durationToSet;
    }

    /**
     * Get distance of workout
     *
     * @return Distance of workout in meters
     */
    float getDistance() {
        return this.distance;
    }

    /**
     * Set distance of workout
     *
     * @param distanceToSet Distance in meters
     */
    void setDistance(float distanceToSet) {
        this.distance = distanceToSet;
    }

    float getAvgPace() {
        return this.avgPace;
    }

    void setAvgPace(float avgPaceToSet) {
        this.avgPace = avgPaceToSet;
    }

    int getCalories() {
        return this.calories;
    }

    void setCalories(int caloriesToSet) {
        this.calories = caloriesToSet;
    }

    int getHeartRate() {
        return this.heartRate;
    }

    void setHeartRate(int heartRateToSet) {
        this.heartRate = heartRateToSet;
    }

    float getAvgSpeed() {
        return this.avgSpeed;
    }

    void setAvgSpeed(float avgSpeedToSet) {
        this.avgSpeed = avgSpeedToSet;
    }

    /**
     * Gets total meters climbed. You are responsible for converting the distance.
     *
     * @return Meters climbed
     */
    float getClimb() {
        return this.climb;
    }

    /**
     * Allows you to set total climb in meters. You are reponsible for unit conversion
     *
     * @param climbToSet Distance in meters
     */
    void setClimb(float climbToSet) {
        this.climb = climbToSet;
    }

    String getComments() {
        return this.comments;
    }

    void setComments(String commentsToSet) {
        this.comments = commentsToSet;
    }

    String getActivityType() {
        return this.activityType;
    }

    void setActivityType(String activityTypeToSet) {
        this.activityType = activityTypeToSet;
    }

}
