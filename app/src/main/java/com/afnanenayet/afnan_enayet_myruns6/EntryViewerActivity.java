package com.afnanenayet.afnan_enayet_myruns6;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EntryViewerActivity extends AppCompatActivity {
    public static final String ID_KEY = "id_key";
    private static final String DEBUG_TAG = "MyRunsEntryV";
    private static final int DELETE_MENU_ITEM = 0;
    Entry entryToDisplay;
    private boolean isMetric;
    private long entryId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        entryId = intent.getLongExtra(ID_KEY, -1);
        setContentView(R.layout.activity_entry_viewer);

        // Making sure that valid ID param was passed in
        if (entryId != -1) {
            new LoadEntryData().execute(entryId);
        }

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        isMetric = prefs.getString(getString(R.string.pref_unit_preference_key),
                "metric")
                .equals("metric");

        Log.d(DEBUG_TAG, prefs.getString(getString(R.string.pref_unit_preference_key),
                getString(R.string.pref_unit_preference_key)));
    }

    // Creates "Delete" menu item at the top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, DELETE_MENU_ITEM, Menu.NONE, getString(R.string.delete));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_MENU_ITEM:
                deleteEntry();
                return true;
            default:
                return false;
        }
    }

    /**
     * Retrieves entry by ID
     *
     * @param id The ID of the entry in the database
     */
    private Entry getEntryById(long id) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        return dbHelper.fetchEntryByIndex(id);
    }

    /**
     * Wrapper for DeleteEntryThread
     */
    private void deleteEntry() {
        new DeleteEntryThread().execute(entryId);
        this.finish();
    }

    /**
     * Displays details of an entry to the UI
     *
     * @param entry The {@link Entry} object to display
     */
    private void displayEntryDetails(Entry entry) {
        float distance;
        String distanceUnit;

        if (isMetric) {
            distance = entry.getDistance();
            distanceUnit = getString(R.string.metric_units_distance);
        } else {
            distance = EntryUtil.kmToMiles(entry.getDistance());
            distanceUnit = getString(R.string.imperial_units_distance);
        }

        Log.d(DEBUG_TAG, "Displaying entry details");

        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.activity_entry_viewer);

        // Find order of title/detail in string values xml file
        ArrayList<String> titles = new ArrayList<>(Arrays.asList(getResources()
                .getStringArray(R.array.entry_detail_titles)));
        ArrayList<String> details = new ArrayList<>();

        // Populating details string
        details.add(EntryDataSource.getInputTypeString(this, entry.getInputType()));
        details.add(EntryDataSource.getActivityTypeString(this, entry.getActivityType()));
        details.add(EntryDataSource.getDateTimeString(entry.getDateTime()));
        details.add(EntryUtil.getTimeString(entry.getDuration()));
        details.add(Float.toString(distance) + " " + distanceUnit);
        details.add(Integer.toString(entry.getCalories()) + " cals");
        details.add(Integer.toString(entry.getHeartRate()) + "bpm");

        for (int i = 0; i < titles.size(); i++) {
            rootLayout.addView(entrySection(titles.get(i), details.get(i)));
        }
    }

    /**
     * Creates LinearLayout with title and detail view
     *
     * @param title  Title text
     * @param detail Detail text
     * @return The view with the textviews for the Entry detail
     */
    private LinearLayout entrySection(String title, String detail) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(5, 5, 5, 5);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // Setting text on TextViews
        TextView titleTextView = new TextView(this);
        titleTextView.setText(title);
        titleTextView.setPadding(5, 5, 5, 5);
        linearLayout.addView(titleTextView);

        TextView detailTextView = new TextView(this);
        detailTextView.setText(detail);
        detailTextView.setPadding(5, 5, 5, 5);
        linearLayout.addView(detailTextView);

        return linearLayout;
    }

    private class DeleteEntryThread extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... id) {
            // Remove entry from internal database
            DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
            helper.removeEntry(id[0]);
            helper.close();

            // Remove entry from server
            Map<String, String> map = new HashMap<>();
            map.put(EntryDataSource.ActivityEntry.idColumn, Long.toString(id[0]));

            try {
                ServerUtilities.post(ServerUtilities.SERVER_ADDRESS + "/delete.do", map);
            } catch (IOException e) {
                Log.e(DEBUG_TAG, "Failed to post entry delete request to server");
                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Handles threading for database
     */
    private class LoadEntryData extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... id) {
            Entry entryToDisplay = getEntryById(id[0]);
            displayEntryDetails(entryToDisplay);
            return null;
        }
    }
}
