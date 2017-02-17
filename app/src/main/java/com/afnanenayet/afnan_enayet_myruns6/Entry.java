package com.afnanenayet.afnan_enayet_myruns6;

import android.location.Location;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Afnan Enayet on 1/25/17.
 */

/**
 * Object to represent database entry with applicable properties
 */
public class Entry {
    private long id; // ID
    private int inputType; // Input type (auto, manual, etc)
    private int activityType; // Activity type (running, walking, etc)
    private Calendar dateTime; // Date and time
    private long duration; // Duration of activity
    private float distance; // Distance of activity
    private float avgPace; // Average pace
    private int calories;  // Calories
    private int heartRate; // Average heart rate
    private float avgSpeed; // Average speed
    private float climb; // Total climb
    private String comments; // Comments
    private int privacy; // Privacy
    private ArrayList<Location> locations; // List of locations

    // Getters and setters
    public long getId() {
        return this.id;
    }

    public void setId(long idToSet) {
        this.id = idToSet;
    }

    int getInputType() {
        return this.inputType;
    }

    void setInputType(int inputTypeToSet) {
        this.inputType = inputTypeToSet;
    }

    Calendar getDateTime() {
        return this.dateTime;
    }

    void setDateTime(Calendar dateTimeToSet) {
        this.dateTime = dateTimeToSet;
    }

    long getDuration() {
        return this.duration;
    }

    void setDuration(long durationToSet) {
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

    int getActivityType() {
        return this.activityType;
    }

    void setActivityType(int activityTypeToSet) {
        this.activityType = activityTypeToSet;
    }

    int getPrivacy() {
        return this.privacy;
    }

    void setPrivacy(int privacyToSet) {
        this.privacy = privacyToSet;
    }

    ArrayList<Location> getLocations() {
        return this.locations;
    }

    void setLocations(ArrayList<Location> locationsToSet) {
        this.locations = locationsToSet;
    }
}
