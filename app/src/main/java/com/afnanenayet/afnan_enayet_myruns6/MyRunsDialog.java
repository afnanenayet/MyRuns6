package com.afnanenayet.afnan_enayet_myruns6;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class MyRunsDialog extends DialogFragment {
    private final static String DEBUG_LOG = "MyRuns/Dialog";

    // Enums for type of dialog to create
    public final static int PROFILE_PICKER = 0;
    public final static int DATE_PICKER_DIALOG = 2;
    public final static int TIME_PICKER_DIALOG = 3;
    public final static int DURATION_DIALOG = 4;
    public final static int DISTANCE_DIALOG = 5;
    public final static int CALORIES_DIALOG = 6;
    public final static int HEART_RATE_DIALOG = 7;
    public final static int COMMENTS_DIALOG = 8;

    // Profile picker enums
    private final static int CHOOSE_CAMERA_PIC = 0;
    private final static int CHOOSE_GALLERY_PIC = 1;
    private final static String TYPE_ID_KEY = "instance_id";
    private final static String TITLE_KEY = "dialog_title";
    private final static String INPUT_TYPE_KEY = "edit_text_input_type";
    private int DIALOG_INSTANCE_ID;

    /**
     * Creates a custom dialog
     *
     * @param type The specifier for which dialog to create
     * @return A dialog fragment
     */
    public static MyRunsDialog newInstance(int type) {
        MyRunsDialog dialog = new MyRunsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE_ID_KEY, type);
        dialog.setArguments(bundle);
        return dialog;
    }

    /** Constructors **/

    /**
     * Create an EditText dialog
     *
     * @param type      The type of dialog (MyRunsDialog enum)
     * @param title     The title displayed at the top of the dialog
     * @param inputType The input type for the EditText box
     * @return returns an instance of MyRunsDialog
     */
    public static MyRunsDialog newInstance(int type, String title, int inputType) {
        MyRunsDialog dialog = new MyRunsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE_ID_KEY, type);
        bundle.putString(TITLE_KEY, title);
        bundle.putInt(INPUT_TYPE_KEY, inputType);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DIALOG_INSTANCE_ID = getArguments().getInt(TYPE_ID_KEY);
        final Activity parentActivity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        final Calendar calendar = Calendar.getInstance();
        LayoutInflater layoutInflater;
        View dialogLayout;

        switch (DIALOG_INSTANCE_ID) {
            // Pick whether to change profile pic by choosing gallery picture or camera
            case PROFILE_PICKER:
                builder.setItems(R.array.profile_picker_dialog_items,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case CHOOSE_CAMERA_PIC:
                                        Log.d(DEBUG_LOG, "choose_camera_pic");
                                        ((ProfileActivity) parentActivity).launchCamera();
                                        break;
                                    case CHOOSE_GALLERY_PIC:
                                        Log.d(DEBUG_LOG, "choose_gallery_pic");
                                        ((ProfileActivity) parentActivity).launchGallery();
                                        break;
                                }
                            }
                        });

                builder.setTitle(R.string.profile_picker_dialog_title);
                break;

            case DURATION_DIALOG:
                // Creating dialog from edit_text_dialog layout file
                builder.setTitle(getArguments().getString(TITLE_KEY));
                layoutInflater = LayoutInflater.from(getActivity());
                dialogLayout = layoutInflater.inflate(R.layout.edit_text_dialog, null);
                final EditText editTextDuration = (EditText) dialogLayout.
                        findViewById(R.id.dialogEditText);
                editTextDuration.setInputType(getArguments().getInt(INPUT_TYPE_KEY));
                builder.setView(dialogLayout);
                builder.setPositiveButton(getString(R.string.dialog_positive_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((ManualEntryActivity) parentActivity).onDurationSet(
                                        editTextDuration.getText().toString()
                                );
                                Log.d(DEBUG_LOG, "EditText dialog: positive button clicked");
                            }
                        });
                builder.setNegativeButton(getString(R.string.dialog_negative_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(DEBUG_LOG, "EditText dialog: negative button clicked");
                            }
                        });
                break;

            case DISTANCE_DIALOG:
                // Creating dialog from edit_text_dialog layout file
                builder.setTitle(getArguments().getString(TITLE_KEY));
                layoutInflater = LayoutInflater.from(getActivity());
                dialogLayout = layoutInflater.inflate(R.layout.edit_text_dialog, null);
                final EditText editTextDistance = (EditText) dialogLayout.
                        findViewById(R.id.dialogEditText);
                editTextDistance.setInputType(getArguments().getInt(INPUT_TYPE_KEY));
                builder.setView(dialogLayout);
                builder.setPositiveButton(getString(R.string.dialog_positive_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((ManualEntryActivity) getActivity())
                                        .onDistanceSet(editTextDistance.getText().toString());
                                Log.d(DEBUG_LOG, "EditText dialog: positive button clicked");
                            }
                        });
                builder.setNegativeButton(getString(R.string.dialog_negative_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(DEBUG_LOG, "EditText dialog: negative button clicked");
                            }
                        });
                break;

            case CALORIES_DIALOG:
                // Creating dialog from edit_text_dialog layout file
                builder.setTitle(getArguments().getString(TITLE_KEY));
                layoutInflater = LayoutInflater.from(getActivity());
                dialogLayout = layoutInflater.inflate(R.layout.edit_text_dialog, null);
                final EditText editTextCalories = (EditText) dialogLayout.
                        findViewById(R.id.dialogEditText);
                editTextCalories.setInputType(getArguments().getInt(INPUT_TYPE_KEY));
                builder.setView(dialogLayout);
                builder.setPositiveButton(getString(R.string.dialog_positive_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((ManualEntryActivity) getActivity())
                                        .onCaloriesSet(editTextCalories.getText().toString());
                                Log.d(DEBUG_LOG, "EditText dialog: positive button clicked");
                            }
                        });
                builder.setNegativeButton(getString(R.string.dialog_negative_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(DEBUG_LOG, "EditText dialog: negative button clicked");
                            }
                        });
                break;

            case HEART_RATE_DIALOG:
                // Creating dialog from edit_text_dialog layout file
                builder.setTitle(getArguments().getString(TITLE_KEY));
                layoutInflater = LayoutInflater.from(getActivity());
                dialogLayout = layoutInflater.inflate(R.layout.edit_text_dialog, null);
                final EditText editTextHeartRate = (EditText) dialogLayout.
                        findViewById(R.id.dialogEditText);
                editTextHeartRate.setInputType(getArguments().getInt(INPUT_TYPE_KEY));
                builder.setView(dialogLayout);
                builder.setPositiveButton(getString(R.string.dialog_positive_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((ManualEntryActivity) getActivity())
                                        .onHeartRateSet(editTextHeartRate.getText().toString());
                                Log.d(DEBUG_LOG, "EditText dialog: positive button clicked");
                            }
                        });
                builder.setNegativeButton(getString(R.string.dialog_negative_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(DEBUG_LOG, "EditText dialog: negative button clicked");
                            }
                        });
                break;

            case COMMENTS_DIALOG:
                // Creating dialog from edit_text_dialog layout file
                builder.setTitle(getArguments().getString(TITLE_KEY));
                layoutInflater = LayoutInflater.from(getActivity());
                dialogLayout = layoutInflater.inflate(R.layout.edit_text_dialog, null);
                final EditText editTextComments = (EditText) dialogLayout.
                        findViewById(R.id.dialogEditText);
                editTextComments.setInputType(getArguments().getInt(INPUT_TYPE_KEY));
                builder.setView(dialogLayout);
                builder.setPositiveButton(getString(R.string.dialog_positive_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((ManualEntryActivity) getActivity())
                                        .onCommentsSet(editTextComments.getText().toString());
                                Log.d(DEBUG_LOG, "EditText dialog: positive button clicked");
                            }
                        });
                builder.setNegativeButton(getString(R.string.dialog_negative_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(DEBUG_LOG, "EditText dialog: negative button clicked");
                            }
                        });
                break;

            case DATE_PICKER_DIALOG:
                // Creating date picker dialog populated with current date
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                return new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                ((ManualEntryActivity) getActivity()).onDateSet(i, i1, i2);
                            }

                        }, year, month, day);

            case TIME_PICKER_DIALOG:
                // Creating time picker dialog populated with current time
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                return new TimePickerDialog(getActivity(), new TimePickerDialog.
                        OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        ((ManualEntryActivity) getActivity()).onTimeSet(i, i1);
                    }
                }, hour, minute, false);
        }
        return builder.create();
    }
}
