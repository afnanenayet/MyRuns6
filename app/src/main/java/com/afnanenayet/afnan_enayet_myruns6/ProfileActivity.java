package com.afnanenayet.afnan_enayet_myruns6;

import android.Manifest;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class ProfileActivity extends AppCompatActivity {
    private static final String PREFS_KEY = "preferences";
    // general tags and keys
    private static final String TAG = "MyRunsProfile";
    private final static String GENDER_KEY = "pref_gender";
    private final static String GALLERY_INTENT_TYPE = "image/*";
    private final static String GALLERY_CROPPED_KEY = "cropped_from_gallery";

    // image capture
    private final static String PROF_PIC_URI_KEY = "SAVED_INSTANCE_PROF_PIC";
    private final static String DIALOG_FRAGMENT_TAG = "photo_picker_dialog";
    private static final String[] permissionsArray = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

    // This map contains key and value pairs for each widget which returns a string
    // key: R.id, value: R.string
    private static SparseIntArray prefMap = null;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_GALLERY = 2;
    private ImageView profilePicView = null;
    private Uri tempImageUri;

    // indicates whether the picture being loaded is from the gallery or not
    private boolean fromGallery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initializing profile view
        profilePicView = (ImageView) findViewById(R.id.profile_image_view);
        initializeViewMaps();
        checkPermissions();
        loadProfile();
        loadProfilePic();

        // On orientation change, this repopulates the ImageView with what was present before
        // if this is a fresh start of the activity, we will load the saved profile picture or
        // default
        if (savedInstanceState != null) {
            Log.d(TAG, "Loading temporary picture from saved state");
            tempImageUri = savedInstanceState.getParcelable(PROF_PIC_URI_KEY);
            profilePicView.setImageURI(tempImageUri);
            fromGallery = savedInstanceState.getBoolean(GALLERY_CROPPED_KEY, false);
            Log.d(TAG, ((Boolean) fromGallery).toString());
        }
    }

    /**
     * Saving image currently in ImageView for orientation change
     */
    @Override
    public void onSaveInstanceState(Bundle state) {
        Log.d(TAG, "Saving temporary picture");
        // Saving the picture currently in the profile pic view
        state.putParcelable(PROF_PIC_URI_KEY, tempImageUri);
        state.putBoolean(GALLERY_CROPPED_KEY, fromGallery);
        super.onSaveInstanceState(state);
    }

    /**
     * Handling activity callbacks from camera and for the picture crop
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                Log.d(TAG, "code: REQUEST_IMAGE_CAPTURE");
                fromGallery = false;
                launchCropActivity(tempImageUri);
                break;

            case Crop.REQUEST_CROP:
                Log.d(TAG, "code: REQUEST_CROP");
                executeCrop(resultCode, data);

                // Keeps the copy of the cropped picture of the gallery, otherwise deletes the
                // original picture taken by the user that is stored as a tmp file
                if (!fromGallery) {
                    File file = new File(tempImageUri.getPath());
                    if (file.exists()) {
                        if (!file.delete()) {
                            Log.e(TAG, "Failed to delete picture taken with camera (not cropped)");
                        } else {
                            Log.d(TAG, "deleted temporary picture");
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_GALLERY:
                Log.d(TAG, "code: REQUEST_IMAGE_GALLERY");
                Uri selectedImageUri = data.getData();
                ParcelFileDescriptor parcelFileDescriptor = null;

                try {
                    parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri,
                            "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (parcelFileDescriptor != null) {
                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                    FileChannel sourceFileChannel = new FileInputStream(fileDescriptor).getChannel();

                    try {
                        FileChannel copyFileChannel = openFileOutput(getString(R.string.gallery_copy_filename),
                                MODE_PRIVATE).getChannel();
                        copyFileChannel.transferFrom(sourceFileChannel, 0, sourceFileChannel.size());
                        sourceFileChannel.close();
                        copyFileChannel.close();
                        tempImageUri = Uri.fromFile(getFileStreamPath(
                                getString(R.string.gallery_copy_filename)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                fromGallery = true;
                launchCropActivity(tempImageUri);
        }
    }

    /**
     * Saves the data present for the profile and presents a toast indicating that the data
     * was saved, then finishes
     */
    public void saveButtonCallback(View v) {
        Log.d(TAG, "Save button clicked");
        saveProfile();
        Toast.makeText(this, R.string.save_toast_text, Toast.LENGTH_SHORT).show();
        this.finish();
    }

    /**
     * Finishes the activity on button press
     */
    public void cancelButtonCallback(View v) {
        Log.d(TAG, "Cancel button clicked");
        this.finish();
    }

    /**
     * Presents an intent to take a picture using the system camera, then returns that picture and
     * changes the profile image viewer to reflect that picture
     *
     * @param v the view that initialized the callback
     */
    public void changePicCallback(View v) {
        Log.d(TAG, "change picture button clicked");
        displayPhotoPickerDialog();
    }

    // Displays the fragment that allows a user to choose their new profile picture
    private void displayPhotoPickerDialog() {
        DialogFragment dialog = MyRunsDialog.newInstance(MyRunsDialog.PROFILE_PICKER);
        dialog.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    //*********************************** Permissions ******************************************

    /**
     * Code to check for runtime permissions. This is mostly based on the example given by the Camera
     * example from the notes
     */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return;
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissionsArray, 0);
        }
    }

    /**
     * Checking permission results and trying to obtain them if permissions have been denid
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // Checking to see if permissions have been granted for the app
        boolean permissionDenied = false;

        // Checking to see if any permissions were denied
        for (int result : grantResults) {
            permissionDenied |= result == PackageManager.PERMISSION_DENIED;
        }

        if (permissionDenied) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                        shouldShowRequestPermissionRationale(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                        || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE
                )) {
                    // If not, explains to user why permissions are necessary
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.permissions_message)
                            .setTitle(R.string.permissions_title);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        permissionsArray, 0);
                            }
                        }
                    });

                    // After displaying, use standard Android request permission dialog
                    requestPermissions(permissionsArray, 0);
                }
            }
        }
    }

    //********************************** Private helper functions ******************************

    /**
     * Saves data present in each input field
     */
    private void saveProfile() {
        Log.d(TAG, "Attempting to save profile data");

        SharedPreferences prefs = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();

        // Saving values for EditText prefs
        for (int i = 0; i < prefMap.size(); i++) {
            EditText editView = (EditText) findViewById(prefMap.keyAt(i));
            String saveString = editView.getText().toString();
            prefEditor.putString(getString(prefMap.valueAt(i)), saveString);
            String prefKey = getString(prefMap.valueAt(i));

            Log.d(TAG, saveString + " committed with id: " + prefKey);
        }

        // Getting gender preference from radio buttons and saving
        RadioGroup genderPicker = (RadioGroup) findViewById(R.id.pref_gender);
        int pickedGender = genderPicker.getCheckedRadioButtonId();
        prefEditor.putInt(GENDER_KEY, pickedGender);

        prefEditor.apply();

        // Saving profile picture in imageview if it has been changed
        saveProfilePic();
    }

    /**
     * Loads previously saved data back into each input field
     */
    private void loadProfile() {
        Log.d(TAG, "Attempting to load profile data");

        // Setting EditTexts which take strings
        for (int i = 0; i < prefMap.size(); i++) {
            EditText editView = (EditText) findViewById(prefMap.keyAt(i));
            int stringKey = prefMap.valueAt(i);
            editView.setText(getStringFromKey(stringKey));
        }

        // Setting radioButton for gender pick
        SharedPreferences prefs = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        int genderId = prefs.getInt(GENDER_KEY, -1);

        if (genderId != -1) {
            RadioGroup genderPicker = (RadioGroup) findViewById(R.id.pref_gender);
            genderPicker.check(genderId);
        }
    }

    /**
     * Gets the string that corresponds to a key in the Android string resources file
     * If the key given is invalid, this function will return an empty string and dump the
     * system stack trace
     *
     * @param stringId The R.id of the string in the XML file
     * @return The string that corresponds to the given key in preferences
     */
    private String getStringFromKey(int stringId) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);

            // Putting saved values into their respective UI elements
            return prefs.getString(getString(stringId),
                    getString(R.string.default_pref_string));
        } catch (Exception e) {
            e.printStackTrace();
            return getString(R.string.default_pref_string);
        }
    }

    /**
     * Initializes the Maps that hold id/key string pairs with their respective values
     */
    private void initializeViewMaps() {
        Log.d(TAG, "Initializing hash map");

        // Entering ids and the string that correspond to that id's keys into the
        // preference map
        prefMap = new SparseIntArray();
        prefMap.put(R.id.pref_email, R.string.pref_email_key);
        prefMap.put(R.id.pref_major, R.string.pref_major_key);
        prefMap.put(R.id.pref_profile_name, R.string.pref_profile_name_key);
        prefMap.put(R.id.pref_phone_number, R.string.pref_phone_number_key);
        prefMap.put(R.id.pref_class, R.string.pref_class_key);
    }

    /**
     * Launches the system's default camera app
     */
    public void launchCamera() {
        Log.d(TAG, "Launching camera");

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Getting URI of image
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImageUri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Based off the Android Developers example
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
        cameraIntent.putExtra("return-data", true);

        // Checking to make sure there is a camera app that can return a picture - otherwise
        // the app will crash
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Launches the gallery so user can select an existing photo
     */
    public void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/jpg");
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    /**
     * Saves the profile photo if a new picture has been selected
     */
    private void saveProfilePic() {
        Log.d(TAG, "Attempting to save profile picture");

        profilePicView.buildDrawingCache();
        Bitmap profilePic = profilePicView.getDrawingCache();

        try {
            FileOutputStream fostream = openFileOutput(getString(R.string.profile_pic_filename),
                    MODE_PRIVATE);

            // Use PNG to preserve transparency of crop (so there are no black bars on the sides
            // of the displayed picture
            profilePic.compress(Bitmap.CompressFormat.PNG, 100, fostream);

            fostream.flush();
            fostream.close();
        } catch (IOException e) {
            Log.e(TAG, "Profile picture failed to save");
            e.printStackTrace();
        }
    }

    /**
     * Loads saved profile picture to profile ImageView, if there is no saved photo, it will load
     * the default photo
     */
    private void loadProfilePic() {
        Log.d(TAG, "Attempting to load profile picture");

        try {
            FileInputStream instream = openFileInput(getString(R.string.profile_pic_filename));
            Bitmap profPic = BitmapFactory.decodeStream(instream);
            profilePicView.setImageBitmap(profPic);
            Log.d(TAG, "Saved profile picture retrieved");
            instream.close();
        } catch (IOException e) {
            Log.e(TAG, "A saved profile picture could not be retrieved, loading default");
            profilePicView.setImageResource(R.drawable.default_profile);
        }
    }

    /**
     * Begins SoundCloud's crop widget to crop a picture and replaces the given image with the
     * cropped image
     *
     * @param source the URI of the source image
     */
    private void launchCropActivity(Uri source) {
        Log.d(TAG, "launching crop activity");
        Crop.of(source, source).asSquare().start(this);
    }

    /**
     * Callback method for soundcloud's crop library. Sets the profilePicView to display the
     * cropped image
     */
    private void executeCrop(int resultCode, Intent result) {
        // Make sure we have permission to modify external storage before cropping photo
        checkPermissions();
        Log.d(TAG, "handling crop function result");

        if (resultCode == RESULT_OK) {
            tempImageUri = Crop.getOutput(result);
            profilePicView.setImageURI(tempImageUri);
        } else {
            Toast.makeText(this, R.string.crop_error_message, Toast.LENGTH_SHORT).show();
        }
    }
}
