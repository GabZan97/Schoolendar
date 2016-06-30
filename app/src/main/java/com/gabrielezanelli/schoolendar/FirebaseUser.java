package com.gabrielezanelli.schoolendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gabrielezanelli.schoolendar.spaggiari.SpaggiariCredentials;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang.WordUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FirebaseUser {
    private static com.google.firebase.auth.FirebaseUser currentUser;
    private static String previousUserID;
    public static Bitmap image;
    private static DatabaseReference currentUserRef;
    private static String defaultUsername;
    private static SpaggiariCredentials spaggiariCredentials;

    public static void init(Context context) {
        defaultUsername = context.getString(R.string.pref_default_username);
    }

    public static void updateUser(com.google.firebase.auth.FirebaseUser currentUser) {
        FirebaseUser.currentUser = currentUser;
        FirebaseUser.currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        if (currentUser.isAnonymous()) {
            FirebaseUser.image = null;
        } else {
            new LoadImageFromFirebaseUser().execute(currentUser.getPhotoUrl());
        }
        // TODO: Download new events from firebase

        if (previousUserID != null && !currentUser.isAnonymous())
            migrateData();
    }

    public static void clearUser() {
        FirebaseUser.currentUser = null;
        FirebaseUser.image = null;
        FirebaseUser.currentUserRef = null;
        FirebaseUser.spaggiariCredentials = null;
    }

    private static void migrateData() {
        Log.d("FirebaseUser", "Migrating "+previousUserID);
        FirebaseDatabase.getInstance().getReference().child("users").child(previousUserID).child("events")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("FirebaseUser", "Data change");
                        for (DataSnapshot dataEvent : dataSnapshot.getChildren()) {
                            addEvent(dataEvent.getValue(Event.class));
                            Log.d("FirebaseUser","Adding Event");
                        }
                        FirebaseDatabase.getInstance().getReference().child("users").child(previousUserID).removeValue();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void deleteUserAndSaveData() {
        previousUserID = currentUser.getUid();
        /**
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("FirebaseUser", "Anonymous user deleted");
            }
        });
         */
    }

    // Get Methods [START]
    public static boolean isAnonymous() {
        return currentUser.isAnonymous();
    }

    public static boolean isLogged() {
        return currentUser != null;
    }

    public static String getUsername() {
        return currentUser.getDisplayName();
    }

    public static String getUserID() {
        return currentUser.getUid();
    }

    public static String getEmail() {
        return currentUser.getEmail();
    }

    public static Bitmap getImage() {
        return image;
    }

    public static DatabaseReference getSubjectsRef() {
        return currentUserRef.child("subjects");
    }

    private static DatabaseReference getEventsRef() {
        return currentUserRef.child("events");
    }

    public static DatabaseReference getTasksRef(String eventID) {
        return getEventsRef().child(eventID).child("tasks");
    }
    // Get Methods [END]

    // Set Methods [START]
    public static void setUsername(final String username) {
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(adjustUsername(username))
                .build()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("User profile", "Username update succeeded: " + username);
                } else
                    Log.d("User profile", "Username update failed" + task.getException().toString());
            }
        });
    }

    public static void setPhotoUrl(final Uri photoUrl) {
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUrl)
                .build()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    new LoadImageFromFirebaseUser().execute(photoUrl);
                    Log.d("User profile update", "Photo update succeeded");
                }
            }
        });
    }

    public static void setSpaggiariCredentials(SpaggiariCredentials spaggiariCredentials) {
        FirebaseUser.spaggiariCredentials = spaggiariCredentials;
    }
    // Set Methods [END]


    // Events Methods [START]
    public static Event addEvent(final Event newEvent) {
        // Use push() in order to get unique ID
        // But for now it's better using the ID of the event
        String eventID = getEventsRef().push().getKey();
        newEvent.setId(eventID);
        getEventsRef().child(eventID).setValue(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d("Firebase Database", "Event Saved with ID: " + newEvent.getId());
                else
                    task.getException().printStackTrace();
            }
        });
        return newEvent;
    }

    public static void removeEvent(String eventID) {
        getEventsRef().child(eventID).removeValue();
    }

    public static void updateEvent(final Event updatingEvent) {

    }
    // Events Methods [END]


    // Subjects Methods [START]
    public static void addSubject(String subject) {
        subject = adjustSubject(subject);
        getSubjectsRef().child(subject).setValue(subject);
    }

    public static void removeSubject(String subject) {
        getSubjectsRef().child(subject).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.d("Remove subject", "Completed");
                else
                    task.getException().printStackTrace();
            }
        });
    }

    public static void updateSubject(String oldSubject, String newSubject) {
        removeSubject(oldSubject);
        addSubject(newSubject);
    }
    // Subjects Methods [END]


    // Tasks Methods [START]
    public static void addTask(String eventID, com.gabrielezanelli.schoolendar.Task newTask) {
        if (!newTask.getText().toString().equals(""))
            getTasksRef(eventID).child(newTask.getText()).setValue(newTask);
    }

    public static void removeTask(String eventID, String taskText) {
        getTasksRef(eventID).child(taskText).removeValue();
    }

    public static void updateTaskComplete(String eventID, com.gabrielezanelli.schoolendar.Task updatingTask) {
        getTasksRef(eventID).child(updatingTask.getText()).setValue(updatingTask);
    }

    public static void updateTaskText(String eventID, String oldTaskText, com.gabrielezanelli.schoolendar.Task updatingTask) {
        if (!updatingTask.getText().toString().equals("")) {
            removeTask(eventID, oldTaskText);
            addTask(eventID, updatingTask);
        }
    }
    // Tasks Methods [END]


    private static String adjustSubject(String subject) {
        return WordUtils.capitalize(subject.trim());
    }

    private static String adjustUsername(String username) {
        if (username == null)
            return defaultUsername;
        if (username.equals(""))
            return defaultUsername;
        return username;
    }
}

class LoadImageFromFirebaseUser extends AsyncTask<Uri, Void, Void> {
    @Override
    protected Void doInBackground(Uri... params) {
        try {
            URL url = new URL("" + params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            FirebaseUser.image = myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

