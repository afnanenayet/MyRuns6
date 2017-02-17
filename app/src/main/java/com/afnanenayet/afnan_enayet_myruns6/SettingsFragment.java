package com.afnanenayet.afnan_enayet_myruns6;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * The settings for the application. Uses the standard preference framework in Android, so no
 * manual file editing is necessary here.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        addPreferencesFromResource(R.xml.settings);

        // setting onclick listeners for buttons in settings tab
        Preference websiteButton = findPreference(getString(R.string.pref_webpage_key));
        websiteButton.setOnPreferenceClickListener(this);

        Preference profileButton = findPreference(getString(R.string.pref_profile_key));
        profileButton.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        // performing action based on which button was clicked
        if (key.equals(getString(R.string.pref_webpage_key))) {
            // Launching browser with URL
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.browser_url)));
            startActivity(webIntent);
        } else if (key.equals(getString(R.string.pref_profile_key))) {
            // Start profile activity
            Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(profileIntent);
        }
        return false;
    }
}
