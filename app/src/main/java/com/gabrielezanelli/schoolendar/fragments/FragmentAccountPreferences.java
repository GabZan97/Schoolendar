package com.gabrielezanelli.schoolendar.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import com.gabrielezanelli.schoolendar.FirebaseUser;
import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static android.app.Activity.RESULT_OK;

public class FragmentAccountPreferences extends PreferenceFragment {
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(getString(R.string.fragment_title_account));
        addPreferencesFromResource(R.xml.account_preferences);

        setLogoutPreferenceClickListener();
        setImagePreferenceClickListener();
        initSharedPrefListener();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        initSharedPrefValues();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    private void initSharedPrefValues() {

        sharedPreferences.edit()
                .putString(getString(R.string.pref_key_username), FirebaseUser.getUsername())
                .putString(getString(R.string.pref_key_email), FirebaseUser.getEmail())
                .apply();

        findPreference(getString(R.string.pref_key_username)).setSummary(FirebaseUser.getUsername());
        findPreference(getString(R.string.pref_key_email)).setSummary(FirebaseUser.getEmail());

        String imageSummary;
        if(FirebaseUser.getPhotoUrl()==null)
            imageSummary = getString(R.string.pref_summary_image_unset);
        else
            imageSummary = getString(R.string.pref_summary_image_set);
        findPreference(getString(R.string.pref_key_image)).setSummary(imageSummary);

        boolean defaultNotificationsEnabled = sharedPreferences
                .getBoolean(getString(R.string.pref_key_enable_default_notifications), false);
        findPreference(getString(R.string.pref_key_notification_day)).setEnabled(defaultNotificationsEnabled);
        findPreference(getString(R.string.pref_key_notification_time)).setEnabled(defaultNotificationsEnabled);

    }
    private void initSharedPrefListener() {
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences sharedPref, String preferenceKey) {
                Log.d("Shared Preferences","Preference "+preferenceKey+" changed!");
                Preference prefLink = findPreference(preferenceKey);

                if (preferenceKey.equals(getString(R.string.pref_key_username))) {
                    // Set the username summary to be the username itself
                    String username = sharedPref.getString(preferenceKey, null);

                    if(username==null) {
                        Log.d("Shared Preferences", "Any username retrieved, invalid key");
                        return;
                    }

                    // If the username is empty assign default username and return so the method is called again
                    if (username.equals(""))  {
                        Log.d("Shared Preferences","Empty username, assigning default");
                        sharedPreferences.edit()
                                .putString(preferenceKey,getString(R.string.pref_default_username))
                                .apply();
                        return;
                    }

                    Log.d("Shared Preferences","Username retrieved: "+username);
                    prefLink.setSummary(username);
                    FirebaseUser.setUsername(username);

                }
                 else if (preferenceKey.equals(getString(R.string.pref_key_enable_default_notifications))){
                    boolean defaultNotificationEnabled = sharedPref.getBoolean(preferenceKey, false);
                    findPreference(getString(R.string.pref_key_notification_day))
                            .setEnabled(defaultNotificationEnabled);
                    findPreference(getString(R.string.pref_key_notification_time))
                            .setEnabled(defaultNotificationEnabled);
                }

            }
        };
    }

    private void setImagePreferenceClickListener() {
        findPreference(getString(R.string.pref_key_image)).setOnPreferenceClickListener (new Preference.OnPreferenceClickListener(){
            public boolean onPreferenceClick(Preference pref){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                int PICK_IMAGE = 1;
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                return true;
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            // TODO: Fix getting image path correctly
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            copyProfileImage(filePath);

            // TODO: Upload on cloud and get the url
            // TODO: Set the url to FirebaseUser image

            findPreference(getString(R.string.pref_key_image)).setSummary(getString(R.string.pref_summary_image_set));
        }
    }

    private void copyProfileImage(String sourcePath) {
        try {
            File sourceFile = new File(sourcePath);
            if (!sourceFile.exists()) {
                return;
            }
            File destFile = new File(getActivity().getFilesDir().getPath(), "profile.png");
            if (!destFile.exists()) {
                destFile.createNewFile();
            }

            FileChannel source = null;
            FileChannel destination = null;
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }

            Log.d("Image copy","Succeeded");
            FirebaseUser.setPhotoUrl(Uri.fromFile(destFile));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // TODO: Handle shared preferences ovverride on another login or cancel on logout
    private void setLogoutPreferenceClickListener() {
        findPreference(getString(R.string.pref_key_logout)).setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick( Preference pref ) {

                FirebaseAuth.getInstance().signOut();
                ((MainActivity)getActivity()).fragmentTransaction(new FragmentSignIn(),false,R.id.navAccount);
                return true;
            }
        } );
    }
}
