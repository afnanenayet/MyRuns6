package com.afnanenayet.afnan_enayet_myruns6;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * A persistent service that tracks the user's location and sensor data
 */
public class TrackingService extends Service implements
        LocationListener,
        ServiceConnection,
        SensorEventListener {
    public final static String UPDATE_LOCATION_ACTION = "location_updated";
    public final static String UPDATE_SENSOR_ACTION = "sensor_updated";
    private final static String DEBUG_TAG = "TrackingService";
    private final static int NOTIFICATION_TAG = 1;
    private static final int ACC_BUFFER_CAP = 2048;
    private static final int ACC_BLOCK_SIZE = 64;
    // Variables for Weka results
    private static final int WEKA_STANDING = 0;
    private static final int WEKA_WALKING = 1;
    private static final int WEKA_RUNNING = 2;
    private static final int WEKA_OTHER = 3;
    public final Object entryLock = new Object();
    private final Object sensorLock = new Object();
    private Entry currentEntry;
    private ActivityClassifyTask classifierTask;
    private IBinder binder = new TrackingBinder();
    private LocationManager locationManager;
    private SensorManager sensorManager = null;
    private ArrayList<Location> locationList;
    private Intent locationUpdateNotifier;
    private Intent sensorUpdateNotifier;
    private ArrayList<Float> speedList;
    private boolean firstUpdate = true;
    private long startTime;
    private ArrayBlockingQueue<Double> accBuffer;
    private boolean autoDetect = false;

    public TrackingService() {
    }

    // Initializing class variables
    @Override
    public void onCreate() {
        Log.d(DEBUG_TAG, "Tracking service onCreate() called");
        super.onCreate();
        binder = new TrackingBinder();
        locationList = new ArrayList<>();

        locationUpdateNotifier = new Intent();
        locationUpdateNotifier.setAction(UPDATE_LOCATION_ACTION);

        sensorUpdateNotifier = new Intent();
        sensorUpdateNotifier.setAction(UPDATE_SENSOR_ACTION);

        speedList = new ArrayList<>();
        startTime = System.currentTimeMillis();
        accBuffer = new ArrayBlockingQueue<>(ACC_BUFFER_CAP);
    }

    /**
     * Adds the location to the location list, and sends a broadcast to the map activity indicating
     * that location has been updated
     *
     * @param mLocation the location signifying the update
     */
    @Override
    public void onLocationChanged(Location mLocation) {
        Log.d(DEBUG_TAG, "Location changed");
        final Location location = mLocation;

        // Running in a separate thread to keep main UI smooth
        new Thread(new Runnable() {
            @Override
            public void run() {
                // We have a lock to make sure the lists are not modified while they are being read
                synchronized (entryLock) {
                    locationList.add(location);

                    // Converting speed from m/s to kph
                    float convertedSpeed = location.getSpeed() * 3.6f;
                    speedList.add(convertedSpeed);
                    currentEntry.setLocations(locationList);


                    // With the first update, we have no climb we are just setting the baseline
                    // afterwards, start increasing climb (must also convert climb from meters to km
                    if (firstUpdate) {
                        firstUpdate = false;
                    } else {
                        float currentClimb = (float) locationList.get(locationList.size() - 1)
                                .getAltitude() - (float) locationList.get(0).getAltitude();
                        currentEntry.setClimb(currentClimb / 1000f);
                    }

                    // updating entry with new average speed
                    float averageSpeed = 0;

                    // Can't divide by 0
                    if (speedList.size() > 0) {
                        for (float i : speedList) {
                            averageSpeed += i;
                        }
                    }

                    averageSpeed /= speedList.size();
                    currentEntry.setAvgSpeed(averageSpeed);
                    currentEntry.setCalories(Math.round(currentEntry.getDistance()));
                }
            }
        }).start();

        // Prompt MapActivity to update based on new info
        this.sendBroadcast(locationUpdateNotifier);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(DEBUG_TAG, "Status changed");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(DEBUG_TAG, "Location provider enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(DEBUG_TAG, "Location provider disabled");
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    // Setting up location trackers when startService is called
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(DEBUG_TAG, "Tracking service started");

        // Adding initial parameters for Entry object
        final int inputType = intent.getIntExtra(StartFragment.INPUT_TYPE_KEY,
                StartFragment.AUTOMATIC_INPUT);
        if (inputType == StartFragment.AUTOMATIC_INPUT)
            autoDetect = true;


        final int activityType = intent.getIntExtra(StartFragment.ACTIVITY_TYPE_KEY, -1);

        Log.d(DEBUG_TAG, ((Integer) activityType).toString());
        final Calendar currentTime = Calendar.getInstance();

        // Offloading to worker thread to keep UI smooth
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (entryLock) {
                    // Creating entry with initial parameters
                    currentEntry = new Entry();
                    currentEntry.setInputType(inputType);
                    currentEntry.setCalories(0);
                    currentEntry.setClimb(0);
                    currentEntry.setDateTime(currentTime);

                    if (autoDetect) {
                        currentEntry.setActivityType(0);
                    } else {
                        currentEntry.setActivityType(activityType);
                    }
                }
            }
        }).start();


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.e(DEBUG_TAG, "Permissions not granted");
        }

        // Only need to classify accelerometer data if input type is automatic
        // Initializing sensormanager, getting system services, setting up callback
        if (autoDetect) {

            // Setting up sensormanager callbacks
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                    SensorManager.SENSOR_DELAY_NORMAL);

            // Starting classifier task
            classifierTask = new ActivityClassifyTask();
            classifierTask.execute();
        }

        // Used for services that are started and stopped as needed
        return START_STICKY;
    }

    /**
     * Sets up notification to alert user that they are being tracked
     */
    private void setupNotification() {
        String notificationTitle = getString(R.string.notification_title);
        String notificationMessage = getString(R.string.notification_content);

        // Adding intent so when notif is clicked, it will open the map activity
        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFICATION_TAG,
                new Intent(this, MapActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setting notification properties
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_media_play);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(0, notification);
        Log.d(DEBUG_TAG, "Set up notification");
    }

    /**
     * Removes persistent notification in status bar
     */
    private void shutdownNotification() {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /**
     * Getter method for locationList
     *
     * @return Returns the internal location nlist
     */
    public ArrayList<Location> getLocationList() {
        synchronized (entryLock) {
            return locationList;
        }
    }

    /**
     * Getter method for entry
     *
     * @return Returns the internal location list
     */
    public Entry getEntry() {
        synchronized (entryLock) {
            // convert milliseconds to seconds as it will be stored for the Entry object
            long milis = System.currentTimeMillis() - startTime;
            currentEntry.setDuration(milis / 1000);
        }
        return currentEntry;
    }

    /**
     * Initializes notification
     */
    @Override
    public IBinder onBind(Intent intent) {
        setupNotification();
        return binder;
    }

    // Removes the persistent notification that informs the user that they are being tracked
    @Override
    public void onDestroy() {
        shutdownNotification();

        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.e(DEBUG_TAG, "Location permission absent");
        }

        if (autoDetect) {
            // Shutting down classifier task, adding delay before unregistering sensor
            classifierTask.cancel(true);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sensorManager.unregisterListener(this);
        }

        Log.d(DEBUG_TAG, "Shutting down tracking service");
        super.onDestroy();
    }

    /**
     * Callback method for accelerometer updates
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(DEBUG_TAG, "Sensor changed");

        // Retrieving information only from accelerometer
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            double m = Math.sqrt(Math.pow(sensorEvent.values[0], 2) +
                    Math.pow(sensorEvent.values[1], 2) +
                    Math.pow(sensorEvent.values[2], 2));

            try {
                accBuffer.add(m);
            }

            // Indicates that buffer is full, must double capacity before adding a new element
            catch (IllegalStateException e) {
                ArrayBlockingQueue<Double> temp = new ArrayBlockingQueue<>
                        (accBuffer.size() * 2);

                // Dump queue to larger temp variable then swap
                accBuffer.drainTo(temp);
                accBuffer = temp;
                accBuffer.add(m);
            }
        }

        sendBroadcast(sensorUpdateNotifier);
    }

    /**
     * Empty method to complete {@link SensorEventListener} abstraction
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    /**
     * A binder for activities to interact with {@link TrackingService}
     */
    public final class TrackingBinder extends Binder {
        @org.jetbrains.annotations.Contract(pure = true)

        public TrackingService getService() {
            return TrackingService.this;
        }
    }

    /**
     * Creates a feature vector, taken from XD's example on the instructions
     */
    private class ActivityClassifyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            double[] accBlock = new double[ACC_BLOCK_SIZE];
            double[] re = accBlock;
            double[] im = new double[ACC_BLOCK_SIZE];
            FFT fft = new FFT(ACC_BLOCK_SIZE);
            double max;
            ArrayList<Double> features = new ArrayList<>();
            int blockSize = 0;

            while (true) {
                try {

                    // Allows the asynctask to be interrupted properly
                    if (isCancelled()) {
                        return null;
                    }

                    accBlock[blockSize++] = accBuffer.take();

                    if (blockSize == ACC_BLOCK_SIZE) {
                        blockSize = 0;

                        max = .0;

                        // Find max value in the readings
                        for (double value : accBlock) {
                            if (max < value) {
                                max = value;
                            }
                        }

                        // Compute the re and im:
                        // setting values of re and im by reference.
                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            // Compute each coefficient
                            double mag = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
                            // Adding the computed FFT coefficient to the
                            // featVect
                            features.add(mag);
                            // Clear the field
                            im[i] = .0;
                        }

                        // Finally, append max after frequency components
                        features.add(max);

                        // Classify feature vector
                        double wekaActivityType = WekaClassifier.classify(features.toArray());

                        // Clear feature vector
                        features.clear();

                        // Set Entry activity type
                        int activityType = 0;

                        // Converting Weka activity type to activity type for Entry
                        if (wekaActivityType == WEKA_STANDING) {
                            activityType = 2;
                        } else if (wekaActivityType == WEKA_WALKING) {
                            activityType = 1;
                        } else if (wekaActivityType == WEKA_RUNNING) {
                            activityType = 0;
                        } else if (wekaActivityType == WEKA_OTHER) {
                            activityType = 13;
                        } else {
                            Log.d(DEBUG_TAG, "Weka classifier returned invalid result");
                        }

                        synchronized (entryLock) {
                            currentEntry.setActivityType(activityType);
                        }
                    }
                } catch (Exception e) {
                    Log.e(DEBUG_TAG, "Error with classification");
                    e.printStackTrace();
                }
            }
        }


    }
}
