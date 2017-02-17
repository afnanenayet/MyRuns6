package com.afnanenayet.afnan_enayet_myruns6;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;

public class ManualEntryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        View.OnClickListener {
    // enums for manual entry options that correspond to the string array manual_input_options
    public static final int DATE = 0;
    public static final int TIME = 1;
    public static final int DURATION = 2;
    public static final int DISTANCE = 3;
    public static final int CALORIES = 4;
    public static final int HEART_RATE = 5;
    public static final int COMMENTS = 6;
    private static final String DEBUG_TAG = "MyRuns/manual entry";
    private static final int INPUT_TYPE = StartFragment.MANUAL_INPUT;
    // Instance vars
    Entry entry;
    DateStruct tempDate;
    private ArrayAdapter<String> ListArrayAdapter;
    private boolean isBeingSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        // populating activity with ListView options in strings resource
        ListArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.manual_input_options));

        // setting up listeners
        ListView listView = (ListView) findViewById(R.id.manual_entry_listview);
        listView.setAdapter(ListArrayAdapter);
        listView.setOnItemClickListener(this);
        Button saveButton = (Button) findViewById(R.id.manual_entry_save);
        saveButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.manual_entry_cancel);
        cancelButton.setOnClickListener(this);
        int activityType = getIntent().getIntExtra(StartFragment.ACTIVITY_TYPE_KEY, 0);

        // Initializing instance variables
        entry = new Entry();
        entry.setActivityType(activityType);
        entry.setInputType(INPUT_TYPE);
        tempDate = new DateStruct();
    }

    /**
     * Creates dialog based on which item was clicked
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(DEBUG_TAG, "item selected from linear layout");
        DialogFragment dialog = null;

        // Creating dialog based on index
        switch (i) {
            case DATE:
                dialog = MyRunsDialog.newInstance(MyRunsDialog.DATE_PICKER_DIALOG);
                break;
            case TIME:
                dialog = MyRunsDialog.newInstance(MyRunsDialog.TIME_PICKER_DIALOG);
                break;
            case DURATION:
                dialog = MyRunsDialog.newInstance(MyRunsDialog.DURATION_DIALOG,
                        getString(R.string.duration_dialog_title), InputType.TYPE_CLASS_NUMBER);
                break;
            case DISTANCE:
                dialog = MyRunsDialog.newInstance(MyRunsDialog.DISTANCE_DIALOG,
                        getString(R.string.distance_dialog_title), InputType.TYPE_CLASS_NUMBER);
                break;
            case CALORIES:
                dialog = MyRunsDialog.newInstance(MyRunsDialog.CALORIES_DIALOG,
                        getString(R.string.calories_dialog_title), InputType.TYPE_CLASS_NUMBER);
                break;
            case HEART_RATE:
                dialog = MyRunsDialog.newInstance(MyRunsDialog.HEART_RATE_DIALOG,
                        getString(R.string.heart_rate_dialog_title), InputType.TYPE_CLASS_NUMBER);
                break;
            case COMMENTS:
                dialog = MyRunsDialog.newInstance(MyRunsDialog.COMMENTS_DIALOG,
                        getString(R.string.calories_dialog_title),
                        InputType.TYPE_CLASS_TEXT);
                break;
        }

        if (dialog != null) {
            dialog.show(getFragmentManager(), getString(R.string.manual_entry_dialog));
        }
    }

    /**
     * Saving or cancelling based on which button is clicked
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.manual_entry_save:
                Log.d(DEBUG_TAG, "Save button clicked");

                if (!isBeingSaved) {
                    isBeingSaved = true;
                    saveDataToEntry();
                }

                finish();
                break;
            case R.id.manual_entry_cancel:
                // finish activity and indicate that data is being discarded
                Toast.makeText(getApplicationContext(), R.string.manual_entry_cancel_toast,
                        Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    public void onDateSet(int year, int month, int day) {
        tempDate.setYear(year);
        tempDate.setMonth(month);
        tempDate.setDay(day);
    }

    //****************************** Dialog callbacks ******************************************

    public void onTimeSet(int hour, int minute) {
        tempDate.setHour(hour);
        tempDate.setMinute(minute);
    }

    public void onDurationSet(String duration) {
        if (!duration.isEmpty()) {
            Log.d(DEBUG_TAG, "Duration: " + duration);
            // Entry saves duration in seconds
            entry.setDuration(Integer.parseInt(duration) * 60);
        }
    }

    public void onDistanceSet(String distance) {
        Log.d(DEBUG_TAG, "Distance: " + distance);
        if (!distance.isEmpty()) {
            entry.setDistance(Float.parseFloat(distance));
        }
    }

    public void onCaloriesSet(String calories) {
        Log.d(DEBUG_TAG, "Calories: " + calories);

        if (!calories.isEmpty()) {
            entry.setCalories(Integer.parseInt(calories));
        }
    }

    public void onHeartRateSet(String heartRate) {
        Log.d(DEBUG_TAG, "Heart rate: " + heartRate);

        if (!heartRate.isEmpty()) {
            entry.setHeartRate(Integer.parseInt(heartRate));
        }
    }

    public void onCommentsSet(String comments) {
        Log.d(DEBUG_TAG, "Comments: " + comments);

        if (!comments.isEmpty()) {
            entry.setComments(comments);
        }
    }

    /**
     * Saves data from Entry and commits to database
     */
    private void saveDataToEntry() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, tempDate.getYear());
        calendar.set(Calendar.MONTH, tempDate.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, tempDate.getDay());
        calendar.set(Calendar.HOUR_OF_DAY, tempDate.getHour());
        calendar.set(Calendar.MINUTE, tempDate.getMinute());
        entry.setDateTime(calendar);

        // Saving database entry asynchronously
        new AsyncSaveEntry().execute();
    }

    private class AsyncSaveEntry extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            entry.setId(dbHelper.insertEntry(entry));
            dbHelper.close();
            return null;
        }
    }

    /**
     * Private class that holds Date information in case it needs to be commited as part of an
     * Entry
     */
    class DateStruct {
        private int hour;
        private int minute;
        private int year;
        private int month;
        private int day;

        int getHour() {
            return hour;
        }

        void setHour(int hour) {
            this.hour = hour;
        }

        int getMinute() {
            return minute;
        }

        void setMinute(int minute) {
            this.minute = minute;
        }

        int getDay() {
            return day;
        }

        void setDay(int day) {
            this.day = day;
        }

        int getMonth() {
            return month;
        }

        void setMonth(int month) {
            this.month = month;
        }

        int getYear() {
            return year;
        }

        void setYear(int year) {
            this.year = year;
        }
    }
}
