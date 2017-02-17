package com.afnanenayet.afnan_enayet_myruns6;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Displays location and activity statistics on a map fragment. Uses an external service to record
 * and track locations.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String DISPLAY_HISTORY_KEY = "should_display_history";
    private static final String DEBUG_TAG = "MapActivity";
    private final static float ZOOM_LEVEL = 17;
    private final static float POLYLINE_WIDTH = 5;
    private final static float NO_SPEED_AVAILABLE = -1;
    private final static int NO_ACTIVITY_AVAILABLE = -1;

    private GoogleMap map;
    private boolean displayHistory = false;
    private boolean isBound = false;
    private TrackingService trackingService;
    private IntentFilter locationFilter;
    private IntentFilter sensorFilter;
    private boolean isMapReady = false;
    private Polyline line;
    private PolylineOptions polylineOptions;
    private long entryId = -1;
    private boolean orientationChanged = false;
    private Marker lastMarker;
    private boolean autoDetect = false;
    private double lastSpeed = 0;


    /**
     * Receives updates from {@link TrackingService} and imports data to the MapActivity
     */
    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {

        /**
         * Updates map with location when a new location update has been received
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isMapReady) {
                Log.d(DEBUG_TAG, "Broadcast received");
                ArrayList<Location> locationArrayList = trackingService.getLocationList();

                // Converting Location to LatLng
                Location latestLocation = locationArrayList.get(locationArrayList.size() - 1);
                LatLng latLng = new LatLng(latestLocation.getLatitude(),
                        latestLocation.getLongitude());

                // Move map to center on new location
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));

                // Update line to connect to new location
                updatePolyline(latLng);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                // Add marker to first location if this is the first location and update last
                // marker
                if (locationArrayList.size() == 1) {
                    setMarker(latLng);
                } else if (locationArrayList.size() > 2) {
                    lastMarker.remove();
                    lastMarker = map.addMarker(markerOptions);
                } else {
                    lastMarker = map.addMarker(markerOptions);
                }

                // Getting entry, current speed, updating UI
                Entry serviceEntry = trackingService.getEntry();
                lastSpeed = latestLocation.getSpeed() * 3.6;
                updateStatsFromEntry(serviceEntry, lastSpeed);
            }
        }
    };

    // Receives sensor updates and updates entry information
    private final BroadcastReceiver sensorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Entry entry = trackingService.getEntry();
            updateStatsFromEntry(entry, lastSpeed);
        }
    };

    // Provides a connection between the MapActivity and the TrackingService
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(DEBUG_TAG, "Tracking service connected");
            TrackingService.TrackingBinder binder = (TrackingService.TrackingBinder) iBinder;
            trackingService = binder.getService();
            updateStatsFromEntry(trackingService.getEntry(), 0f);

            // If restoring from an orientation change, we will load up the data that was present
            // on map before
            if (orientationChanged) {
                orientationChanged = false;
                loadEntrySavedState();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(DEBUG_TAG, "Tracking service disconnected");
        }
    };

    /**
     * Updates polyline of googlemap
     *
     * @param lineLatLng The location to update the polyline to
     */
    private void updatePolyline(LatLng lineLatLng) {
        // Can't update the map if it hasn't been initialized
        if (!isMapReady)
            return;

        // Removing line if it exists
        if (line != null) {
            line.remove();
            line = null;
        }

        // Adding polyline with appropriate options to map
        polylineOptions.width(POLYLINE_WIDTH);
        polylineOptions.color(Color.RED);
        polylineOptions.add(lineLatLng);
        line = map.addPolyline(polylineOptions);
    }

    /**
     * Set position of marker on map
     *
     * @param markerLatLng The desired coordinates of the marker
     */
    private void setMarker(LatLng markerLatLng) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(markerLatLng)
                .flat(false);

        if (isMapReady) {
            map.addMarker(markerOptions);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        displayHistory = getIntent().getBooleanExtra(DISPLAY_HISTORY_KEY, false);
        locationFilter = new IntentFilter();
        locationFilter.addAction(TrackingService.UPDATE_LOCATION_ACTION);

        sensorFilter = new IntentFilter();
        sensorFilter.addAction(TrackingService.UPDATE_SENSOR_ACTION);

        polylineOptions = new PolylineOptions();

        // Setting up map fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Display history if we are pulling up an entry, otherwise start tracking service
        if (displayHistory) {
            // Getting rid of save/cancel buttons for history entry
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_button_layout);
            ViewGroup parent = (ViewGroup) linearLayout.getParent();
            parent.removeView(linearLayout);

            long tempId = getIntent().getLongExtra(HistoryFragment.ENTRY_ID_KEY, 0);
            entryId = tempId;
            new LoadEntryData().execute(tempId);
        } else {
            // Doing relevant actions depending on activity type
            int inputType = getIntent().getIntExtra(StartFragment.INPUT_TYPE_KEY, 0);
            int activityType = NO_ACTIVITY_AVAILABLE;
            switch (inputType) {
                case StartFragment.GPS_INPUT:
                    activityType = getIntent().getIntExtra(StartFragment.ACTIVITY_TYPE_KEY, 0);
                    break;

                case StartFragment.AUTOMATIC_INPUT:
                    autoDetect = true;
                    break;
            }

            // Launch tracking service and pass details for {@link Entry}
            Intent intent = new Intent(MapActivity.this, TrackingService.class);
            intent.putExtra(StartFragment.INPUT_TYPE_KEY, inputType);
            intent.putExtra(StartFragment.ACTIVITY_TYPE_KEY, activityType);
            startService(intent);
            doBindService();

            // If this is an orientation change, we want to load the marker and previous data points
            if (savedInstanceState != null) {
                Log.d(DEBUG_TAG, "Orientation was changed");
                orientationChanged = true;
            }

        }
    }

    /**
     * Deletes entry if delete button is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                // Make sure entryId has been loaded before deleting an Entry
                if (displayHistory && entryId != -1) {
                    new DeleteEntry().execute(entryId);
                    finish();
                }
                return true;
            default:
                super.onOptionsItemSelected(item);
                return true;
        }
    }

    /**
     * Inflates menu depending on whether history is being viewed or not
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (displayHistory) {
            inflater.inflate(R.menu.menu_history, menu);
        } else {
            inflater.inflate(R.menu.menu_main, menu);
        }

        return true;
    }

    // Binding service when activity is resumed
    @Override
    public void onResume() {
        super.onResume();
        doBindService();
    }

    // Unbinding service when activity is paused
    @Override
    public void onPause() {
        Log.d(DEBUG_TAG, "Pausing activity");
        doUnbindService();
        super.onPause();
    }

    // Unbind service from mapactivity if android decides to conserve resources
    @Override
    public void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

    // callback method for save button
    public void onSaveClick(View view) {
        Log.d(DEBUG_TAG, "Save button clicked");
        // Saving entry to database
        new SaveEntry().execute(trackingService.getEntry());

        // Closing up TrackingService
        Intent intent = new Intent(this, TrackingService.class);
        stopService(intent);
        doUnbindService();
        this.finish();
    }

    // callback method for cancel button
    public void onCancelClick(View view) {
        Log.d(DEBUG_TAG, "Cancel button clicked");
        Intent intent = new Intent(this, TrackingService.class);
        stopService(intent);
        doUnbindService();
        this.finish();
    }

    /**
     * Setting up connections to {@link TrackingService}
     */
    private void doBindService() {
        if (!isBound) {
            Log.d(DEBUG_TAG, "Binding service");
            Intent intent = new Intent(this, TrackingService.class);
            bindService(intent, serviceConnection, 0);
            isBound = true;
            registerReceiver(locationReceiver, locationFilter);

            if (autoDetect) {
                registerReceiver(sensorReceiver, sensorFilter);
            }
        }
    }

    /**
     * Breaking connections with {@link TrackingService}
     */
    private void doUnbindService() {
        if (isBound) {
            Log.d(DEBUG_TAG, "Unbinding service");
            unbindService(serviceConnection);
            isBound = false;
            unregisterReceiver(locationReceiver);

            if (autoDetect) {
                unregisterReceiver(sensorReceiver);
            }
        }
    }

    /**
     * Loads polyline, markers, and statistics for location data from an entry
     *
     * @param entry The entry to display to map
     */
    private void loadMapForHistory(Entry entry) {
        Log.d(DEBUG_TAG, "Loading map from existing historical entry");
        ArrayList<Location> locations = entry.getLocations();

        // Checking to make sure locations are valid
        if (locations != null && locations.size() > 1) {
            ArrayList<LatLng> latLngs = new ArrayList<>(locations.size());

            // converting location objects to LatLng objects
            for (Location location : locations) {
                LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());
                latLngs.add(temp);
            }

            // Displaying start and end markers
            setMarker(latLngs.get(0));
            setMarker(latLngs.get(latLngs.size() - 1));

            // Move map to center on new location
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), ZOOM_LEVEL));

            // Adding points to polyline
            for (LatLng latLng : latLngs) {
                polylineOptions.add(latLng);
            }

            polylineOptions.color(Color.RED);
            polylineOptions.width(POLYLINE_WIDTH);

            // Displaying to map
            map.addPolyline(polylineOptions);
        }
    }

    /**
     * Updates textview in front of map from data from an {@link Entry} object
     *
     * @param entry        The entry with the proper data
     * @param currentSpeed The current speed to update the UI with
     */
    private void updateStatsFromEntry(Entry entry, double currentSpeed) {
        TextView statsTv = (TextView) findViewById(R.id.type_stats);
        String activityString = "Activity: ";
        String avgSpeedString = "Average speed: ";
        String currentSpeedString = "Current speed: ";
        String distanceString = "Distance: ";
        String climbString = "Climb: ";
        String calorieString = "Calories: ";

        String unitString = EntryUtil.getUnitString(this);

        // Retrieving data from activity and converting to string in order to be displayed
        if (entry.getActivityType() == NO_ACTIVITY_AVAILABLE) {
            activityString += "unknown";
        } else {
            String[] activityArray = getResources().getStringArray(R.array.activity_array);
            activityString += activityArray[entry.getActivityType()];
        }

        // If viewing history, the current speed will be -1, which is not a valid speed
        if (currentSpeed == NO_SPEED_AVAILABLE) {
            currentSpeedString += "n/a";
        } else {
            currentSpeedString += currentSpeed + " " + unitString + " per hour";
        }

        climbString += Float.toString(EntryUtil.convertedDistance(this, entry.getClimb())) + " "
                + unitString;
        distanceString += EntryUtil.convertedDistance(this, entry.getDistance()) + " " + unitString;
        avgSpeedString += EntryUtil.convertedDistance(this, entry.getAvgSpeed()) + " " + unitString
                + " per hour";
        calorieString += entry.getCalories();

        // Concatenating all the strings so they can be displayed in the text view
        String uiString =
                activityString + System.getProperty("line.separator") +
                        avgSpeedString + System.getProperty("line.separator") +
                        currentSpeedString + System.getProperty("line.separator") +
                        climbString + System.getProperty("line.separator") +
                        calorieString + System.getProperty("line.separator") +
                        distanceString;

        statsTv.setText(uiString);
    }

    /**
     * Sets up the map class variable
     *
     * @param googleMap the map that was returned
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(DEBUG_TAG, "Map is ready (callback received)");
        map = googleMap;
        isMapReady = true;
    }

    /**
     * Load old polylines, and add first marker
     */
    private void loadEntrySavedState() {
        ArrayList<Location> locations;

        synchronized (trackingService.entryLock) {
            locations = trackingService.getLocationList();
        }

        // If we there is no location data then do nothing
        if (locations == null || locations.size() < 1) return;

        ArrayList<LatLng> latLngs = new ArrayList<>(locations.size());

        // converting location objects to LatLng objects
        for (Location location : locations) {
            LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());
            latLngs.add(temp);
        }

        // Can't update the map if it hasn't been initialized
        if (!isMapReady)
            return;

        // Removing line if it exists
        if (line != null) {
            line.remove();
            line = null;
        }

        // Removing polylineoptions if they exist
        if (polylineOptions != null) {
            polylineOptions = null;
            polylineOptions = new PolylineOptions();
        }

        // Displaying start and end markers
        setMarker(latLngs.get(0));

        if (latLngs.size() > 1) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLngs.get(latLngs.size() - 1));
            lastMarker = map.addMarker(markerOptions);
        }

        // Move map to center on new location
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(latLngs.size() - 1),
                ZOOM_LEVEL));

        // Adding points to polyline
        for (LatLng latLng : latLngs) {
            polylineOptions.add(latLng);
        }

        polylineOptions.color(Color.RED);
        polylineOptions.width(POLYLINE_WIDTH);

        // Displaying to map
        line = map.addPolyline(polylineOptions);
        Log.d(DEBUG_TAG, "Updated map with info from before orientation change");
    }

    /**
     * Deletes an entry given an ID
     *
     * @param id the ID/row column that the entry data is held in
     */
    private void deleteEntry(Long id) {
        DatabaseHelper db = new DatabaseHelper(this);
        db.removeEntry(id);
        db.close();
    }

    /**
     * Saves an entry to the DB
     *
     * @param entry The entry to save
     */
    private void saveEntry(Entry entry) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        long id = dbHelper.insertEntry(entry);
        entry.setId(id);
        dbHelper.close();
    }

    /**
     * Asynchronously loads an entry from the database then displays the entry in the UI
     */
    private class LoadEntryData extends AsyncTask<Long, Void, Entry> {

        /**
         * Load entry asynchronously
         *
         * @param longs The ID of the entry
         * @return The entry that was passed to the class
         */
        @Override
        protected Entry doInBackground(Long... longs) {
            long id = longs[0];
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            Entry loadedEntry = dbHelper.fetchEntryByIndex(id);
            dbHelper.close();
            return loadedEntry;
        }

        /**
         * Load map from entry history after Entry has been loaded from database
         *
         * @param entry The entry to be loaded
         */
        protected void onPostExecute(Entry entry) {
            loadMapForHistory(entry);
            updateStatsFromEntry(entry, NO_SPEED_AVAILABLE);
        }
    }

    /**
     * Asynchronously save an entry to the internal SQLite database and post UI indication of save
     */
    private class SaveEntry extends AsyncTask<Entry, Void, Entry> {

        /**
         * Saves entry to database
         *
         * @param entries the entry to be saved
         * @return the entry to be saved with updated id from DB
         */
        @Override
        protected Entry doInBackground(Entry... entries) {
            Log.d(DEBUG_TAG, "Saving entry to db");
            Entry entry = entries[0];
            saveEntry(entry);
            return entry;
        }

        /**
         * Posts to UI indicating that entry has been saved
         *
         * @param entry the entry that was saved
         */
        protected void onPostExecute(Entry entry) {
            Toast.makeText(getApplicationContext(),
                    "Entry #" + ((Long) entry.getId()).toString() + " saved",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes an entry asynchronously given its ID
     */
    private class DeleteEntry extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            long id = longs[0];
            deleteEntry(id);
            return null;
        }
    }
}
