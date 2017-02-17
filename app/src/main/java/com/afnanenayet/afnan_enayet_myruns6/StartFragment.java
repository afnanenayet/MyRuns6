package com.afnanenayet.afnan_enayet_myruns6;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class StartFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {
    public static final String ACTIVITY_TYPE_KEY = "activity_type_key";
    public static final String INPUT_TYPE_KEY = "input_type_key";

    // See input_type_array in strings.xml for indices that corresponds to the array
    final static int MANUAL_INPUT = 0;
    final static int GPS_INPUT = 1;
    final static int AUTOMATIC_INPUT = 2;
    private static final String[] permissions = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final String DEBUG_TAG = "myruns/startfr";
    private View view;
    // Spinner indices
    private int selectedInput = 0;
    private int selectedActivity = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_start, container, false);

        // Initializing button callbacks to this fragment
        Button syncButton = (Button) view.findViewById(R.id.sync_button);
        Button startButton = (Button) view.findViewById(R.id.start_button);
        syncButton.setOnClickListener(this);
        startButton.setOnClickListener(this);

        // Setting listeners for spinners
        Spinner activitySpinner = (Spinner) view.findViewById(R.id.activityTypeSpinner);
        activitySpinner.setOnItemSelectedListener(this);

        Spinner inputSpinner = (Spinner) view.findViewById(R.id.inputTypeSpinner);
        inputSpinner.setOnItemSelectedListener(this);

        return view;
    }

    /**
     * After the activity has been created and the view has been inflated, initialize the spinners
     * with the values presented in the XML array
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeSpinner(R.id.activityTypeSpinner, R.array.activity_array);
        initializeSpinner(R.id.inputTypeSpinner, R.array.input_type_array);
    }

    /**
     * Checks location permissions before launching tracking service
     *
     * @return Whether we have location permissions or not
     */
    private boolean permissionGranted() {
        return (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager
                .PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Checking permission results and trying to obtain them if permissions have been denid
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Checking to see if permissions have been granted for the app
        boolean permissionDenied = false;

        // Checking to see if any permissions were denied
        for (int result : grantResults) {
            permissionDenied |= result == PackageManager.PERMISSION_DENIED;
        }

        if (permissionDenied) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) ||
                        shouldShowRequestPermissionRationale(
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(android.Manifest.permission
                                .READ_EXTERNAL_STORAGE)
                        || shouldShowRequestPermissionRationale(android.Manifest.permission
                        .WRITE_EXTERNAL_STORAGE
                )) {
                    // If not, explains to user why permissions are necessary
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.permissions_message)
                            .setTitle(R.string.permissions_title);
                    final String[] innerPermissions = permissions;
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(innerPermissions, 0);
                            }
                        }
                    });

                    // After displaying, use standard Android request permission dialog
                    requestPermissions(permissions, 0);
                }
            }
        }
    }

    /**
     * Performs the appropriate actions based on which button is clicked and which activities are
     * selected
     *
     * @param v the view that initialized the callback function
     */
    @Override
    public void onClick(View v) {
        Intent launchIntent;

        switch (v.getId()) {
            case R.id.sync_button:
                Log.d(DEBUG_TAG, "Sync button clicked");

                // TODO: implement when necessary
                break;

            case R.id.start_button:
                Log.d(DEBUG_TAG, "Start button clicked");

                // Performing different actions based on which activity is selected
                switch (selectedInput) {
                    case MANUAL_INPUT:
                        // Launching manual activity entry
                        launchIntent = new Intent(getActivity(), ManualEntryActivity.class);
                        launchIntent.putExtra(ACTIVITY_TYPE_KEY, selectedActivity);
                        startActivity(launchIntent);
                        break;
                    case GPS_INPUT:
                        // Launching map activity and passing details
                        launchIntent = new Intent(getActivity(), MapActivity.class);
                        launchIntent.putExtra(MapActivity.DISPLAY_HISTORY_KEY, false);
                        launchIntent.putExtra(ACTIVITY_TYPE_KEY, selectedActivity);
                        launchIntent.putExtra(INPUT_TYPE_KEY, GPS_INPUT);

                        // Launch activity if permissions have been granted
                        if (!permissionGranted()) {
                            requestPermissions(permissions, 0);
                        } else {
                            startActivity(launchIntent);
                        }
                        break;
                    case AUTOMATIC_INPUT:
                        // Launching map activity and passing in details
                        launchIntent = new Intent(getActivity(), MapActivity.class);
                        launchIntent.putExtra(MapActivity.DISPLAY_HISTORY_KEY, false);
                        launchIntent.putExtra(INPUT_TYPE_KEY, AUTOMATIC_INPUT);

                        // can't launch activity if we don't have permissions
                        if (!permissionGranted()) {
                            requestPermissions(permissions, 0);

                            if (permissionGranted()) {
                                startActivity(launchIntent);
                            }
                        } else {
                            startActivity(launchIntent);
                        }
                        break;
                }
                break;
        }
    }

    /**
     * Initializes a spinner to utilize the set of elements presented in an XML array resource
     *
     * @param spinnerId The R.id of the spinner
     * @param arrayId   The R.array XML id for the array
     */
    private void initializeSpinner(int spinnerId, int arrayId) {
        Spinner activitySpinner = (Spinner) view.findViewById(spinnerId);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.
                createFromResource(getActivity(), arrayId,
                        android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(spinnerAdapter);
    }

    /**
     * Sets which activity or input is selected by the user via the spinners
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(DEBUG_TAG, "item selected from spinner");

        if (adapterView.getId() == R.id.inputTypeSpinner) {
            Log.d(DEBUG_TAG, "inputTypeSpinner selected");
            selectedInput = i;
        } else if (adapterView.getId() == R.id.activityTypeSpinner) {
            Log.d(DEBUG_TAG, "activityTypeSpinner selected");
            selectedActivity = i;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Do nothing if nothing is selected
        // callback method must be implemented, however
    }
}
