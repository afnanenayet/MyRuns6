package com.afnanenayet.afnan_enayet_myruns6;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HistoryFragment extends Fragment implements AdapterView.OnItemClickListener {
    public static final String ENTRY_ID_KEY = "entry_id_key";
    private static final String DEBUG_TAG = "HistoryFrag";
    private static final String timeUnitString = "minutes";
    private ArrayList<Entry> entries;
    private ArrayList<String[]> titleEntries;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        listView = (ListView) rootView.findViewById(R.id.history_list_view);

        // Initializing class variables
        titleEntries = new ArrayList<>();
        entries = new ArrayList<>();

        // Loading entries
        updateEntries();
        return rootView;
    }

    /*********************
     * Updating entries when fragment is in view
     ***************************/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        updateEntries();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateEntries();
    }

    // Refresh entries when user swipes to history tab
    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisible);
        Log.d(DEBUG_TAG, "History fragment is now visible");

        if (isVisible) {
            updateEntries();
        }
    }

    /**
     * Updates entries asynchronously, wrapper for the AsyncTask
     */
    private void updateEntries() {
        Log.d(DEBUG_TAG, "Updating entries");
        new HistoryLoader().execute();
    }

    /**
     * Retrieves records from the database
     */
    private void getRecords() {
        Log.d(DEBUG_TAG, "Retrieving entries");
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        entries = dbHelper.fetchEntries();
        dbHelper.close();
    }

    /**
     * Launches EntryViewerActivity for the Entry selected
     *
     * @param id the ID attribute of the entry selected
     */
    private void viewEntryDetail(long id) {
        Log.d(DEBUG_TAG, "Launching activity to view details of entry");

        Intent intent = new Intent(getActivity(), EntryViewerActivity.class);
        intent.putExtra(EntryViewerActivity.ID_KEY, id);
        startActivity(intent);
    }

    /**
     * Creates a string ArrayList that can be used by the listview to display a summary of the
     * entry
     */
    private void setTitleEntries() {

        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        String[] activityArray = getResources().getStringArray(R.array.activity_array);
        titleEntries = new ArrayList<>();

        if (entries.size() == 0)
            return;

        // Retrieving each detail
        for (int i = 0; i < entries.size(); i++) {
            String[] row = new String[2];
            String title;
            String text;
            String activityString;

            Entry currentEntry = entries.get(i);

            if (currentEntry == null) {
                Log.d(DEBUG_TAG, "Entry is null!");
            } else {
                float distance = EntryUtil.convertedDistance(getContext(),
                        currentEntry.getDistance());
                String unitStr = EntryUtil.getUnitString(getContext());
                int activityType = currentEntry.getActivityType();

                if (activityType == -1) {
                    activityString = "unknown";
                } else {
                    activityString = activityArray[activityType];
                }

                // Converting entry details to human-readable strings
                title = EntryDataSource.getInputTypeString(getActivity(),
                        currentEntry.getInputType()) + ": " + activityString +
                        ", " + dateFormat.format(currentEntry.getDateTime().getTime());
                text = Float.toString(distance)
                        + " " + unitStr + ", " + EntryUtil
                        .getTimeString(currentEntry.getDuration());

                row[0] = title;
                row[1] = text;

                titleEntries.add(row);
            }
        }
    }

    /**
     * Creates an adapter for the ListView in the fragment
     */
    private void createAdapter() {
        ArrayAdapter<String[]> arrayAdapter = new ArrayAdapter<String[]>(getContext(),
                android.R.layout.simple_list_item_2, android.R.id.text1, titleEntries) {
            @NonNull
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                if (position < titleEntries.size()) {
                    String[] row = titleEntries.get(position);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                    text1.setText(row[0]);
                    text2.setText(row[1]);
                }
                return view;
            }
        };
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    /**
     * Opens entry detail activity and passes ID parameter
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(DEBUG_TAG, "Entry clicked");

        Entry entry = entries.get(i);
        long id = entry.getId();
        Intent intent = new Intent(getActivity(), MapActivity.class);

        // If manual input, view with entry detail viewer, otherwise view in map
        switch (entry.getInputType()) {
            case StartFragment.MANUAL_INPUT:
                viewEntryDetail(id);
                break;
            case StartFragment.GPS_INPUT:
                intent.putExtra(ENTRY_ID_KEY, id);
                intent.putExtra(MapActivity.DISPLAY_HISTORY_KEY, true);
                startActivity(intent);
                break;
            case StartFragment.AUTOMATIC_INPUT:
                intent.putExtra(ENTRY_ID_KEY, id);
                intent.putExtra(MapActivity.DISPLAY_HISTORY_KEY, true);
                startActivity(intent);
                break;
        }
    }

    /**
     * Asynchronously loads the tasks needed to display the data in the fragment
     */
    private final class HistoryLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getRecords();
            setTitleEntries();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            createAdapter();
        }
    }
}
